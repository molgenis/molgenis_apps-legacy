package org.molgenis.compute.commandline;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.molgenis.compute.design.ComputeParameter;
import org.molgenis.compute.design.ComputeProtocol;
import org.molgenis.compute.design.WorkflowElement;
import org.molgenis.util.CsvFileReader;
import org.molgenis.util.CsvReader;

public class ComputeBundleValidator
{
	ComputeBundleFromDirectory cb;

	/*
	 * Validate workflow, protocols, parameters, worksheet
	 */
	public ComputeBundleValidator(ComputeBundleFromDirectory cb)
	{
		this.cb = cb;
	}

	/*
	 * Check whether all files and paths refered to in the parameters actually
	 * exist
	 */
	public void validateReferedFilesAndPathsExists(ComputeCommandLine options) throws Exception
	{
		if (!options.parametersfile.exists()) fileNotExistsError(options.parametersfile.toString());
		if (!options.worksheetfile.exists()) fileNotExistsError(options.worksheetfile.toString());
		if (!options.workflowfile.exists()) fileNotExistsError(options.workflowfile.toString());
		if (!options.protocoldir.exists()) fileNotExistsError(options.protocoldir.toString());
		// if (!options.templatedir.exists())
		// fileNotExistsError(options.templatedir.toString());
	}

	private void fileNotExistsError(String f)
	{
		printError("The file or path you refer to in your parameters does not exist:\n" + f);
	}

	public void validateFileHeaders(ComputeCommandLine options) throws Exception
	{

		CsvReader reader = new CsvFileReader(options.workflowfile);
		List<String> header = Arrays.asList("name", "protocol_name", "PreviousSteps_name");
		if (!reader.colnames().containsAll(header)) headerError("Workflow", header, reader.colnames(),
				options.workflowfile);

		reader = new CsvFileReader(options.parametersfile);
		header = Arrays.asList("Name", "defaultValue", "description", "dataType", "hasOne_name");
		if (!reader.colnames().containsAll(header)) headerError("Parameters", header, reader.colnames(),
				options.parametersfile);
	}

	private void headerError(String inputName, List<String> targetHeader, List<String> currentHeader, File workflowfile)
	{
		printError(inputName + " file does not have the right header. Please correct this.\n"
				+ "Columns that should be contained in the header are: " + targetHeader + "\n" + "Current header is: "
				+ currentHeader + "\n" + "Your workflow file: " + workflowfile);
	}

	private void exit()
	{
		System.err.println("\nProgram exits with status code 1.");
		System.exit(1);
	}

	private void printErrorHeaderFooter(boolean b)
	{
		System.err.println("\n#");
		System.err.println("##");
		System.err.println("### " + (b ? "Begin" : "End") + " of error message.");
		System.err.println("##");
		System.err.println("#\n");
	}

	private void printError(String msg)
	{
		printErrorHeaderFooter(true);
		System.err.println(msg);
		printErrorHeaderFooter(false);
		exit();
	}

	public void validateBundle()
	{
		// VALIDATE WORKFLOW
		List<WorkflowElement> wfelements = cb.getWorkflowElements();

		// Validate that all wfe step names are different
		List<String> wfeNamesList = new ArrayList<String>();
		Set<String> wfeNamesSet = new HashSet();
		for (WorkflowElement wfe : wfelements)
		{
			wfeNamesList.add(wfe.getName());
			wfeNamesSet.add(wfe.getName());
		}
		if (wfeNamesList.size() != wfeNamesSet.size())
		{
			printError("In your workflow file, you have duplicate workflow step names. All values in the column 'name' should be different. Please fix this.");
		}

		// Validate that PreviousSteps_name's all refer to workflow steps.
		for (WorkflowElement wfe : wfelements)
		{
			for (String ps : wfe.getPreviousSteps_Name())
			{
				if (!wfeNamesSet.contains(ps)) printError("In your workflow, the PreviousSteps_name '"
						+ ps
						+ "' is not a workflow step name, because it does not match to a value in the 'name' column. Please fix this.");
			}
		}

		// First put protocol names in a list
		List<String> protocolNames = new ArrayList<String>();
		for (ComputeProtocol cp : cb.getComputeProtocols())
		{
			protocolNames.add(cp.getName());
		}

		// Validate that there are no two files with the same name in protocols
		// + templates dir
		Set<String> ftlFiles = new HashSet<String>();
		for (String p : protocolNames)
		{
			if (ftlFiles.contains(p))
			{
				printError("The following file is present in both your protocols directory and your templates directory: "
						+ p + ".\nPlease remove one of the two.");
			}
			else
			{
				ftlFiles.add(p);
			}
		}

		// Validate that each protocol that is referred to in the workflow
		// actually exists
		for (WorkflowElement wfe : wfelements)
		{
			String pn = wfe.getProtocol_Name();
			if (!protocolNames.contains(pn))
			{
				printError("In your workflow file, the protocol name '"
						+ pn
						+ "' in the 'protocol_name' column, does not refer to a known protocol.\nPlease upload a protocol with the name '"
						+ pn + ".ftl' or change '" + pn + "' to the name of a known protocol.");
			}
		}

		// Validate that a file called 'Submit.sh.ftl' exists.
		if (!protocolNames.contains("Submit.sh"))
		{
			printError("You should have a protocol 'Submit.sh.ftl' in which you define how to submit the generated scripts.\n"
					+ "You may use the following code that is compatible with a PBS-scheduler.\n\n"
					+ "DIR=\"$( cd \"$( dirname \"<#noparse>${BASH_SOURCE[0]}</#noparse>\" )\" && pwd )\""
					+ "\n"
					+ "touch $DIR/${workflowfilename}.started\n"
					+ "\n"
					+ "<#foreach j in jobs>\n"
					+ "#${j.name}\n"
					+ "${j.name}=$(qsub -N ${j.name}<#if j.prevSteps_Name?size &gt; 0> -W depend=afterok<#foreach d in j.prevSteps_Name>:$${d}</#foreach></#if> ${j.name}.sh)\n"
					+ "echo $${j.name}\n"
					+ "sleep 0\n"
					+ "</#foreach>\n"
					+ "\n"
					+ "touch $DIR/${workflowfilename}.finished");
		}

		// Validate that a file called 'DataTransfer.sh.ftl' exists.
		if (!protocolNames.contains("DataTransfer.sh"))
		{
			printError("You should have a protocol 'DataTransfer.sh.ftl' in which you define how to submit the generated scripts.\n"
					+ "You may use the following code that is compatible with a PBS-scheduler.\n\n"
					+ "#!/bin/bash\n"
					+ "getFile()\n"
					+ "{\n"
					+ "    ARGS=($@)\n"
					+ "    NUMBER=\"${#ARGS[@]}\";\n"
					+ "if [ \"$NUMBER\" -eq \"1\" ]\n"
					+ "    then\n"
					+ "    myFile=${ARGS[0]}\n"
					+ "    \n"
					+ "    if test ! -e $myFile;\n"
					+ "	then\n"
					+ "	echo \"WARNING in getFile/putFile: $myFile is missing\" 1>&2\n"
					+ "	fi\n"
					+ "	\n"
					+ "    else\n"
					+ "    echo \"Example usage: getData \"\\$TMPDIR/datadir/myfile.txt\"\"\n"
					+ "    fi\n"
					+ "}\n"
					+ "\n"
					+ "putFile()\n"
					+ "{\n"
					+ "`getFile $@`\n"
					+ "}\n"
					+ "\n"
					+ "export -f getFile\n" + "export -f putFile\n");
		}

		// VALIDATE PARAMETERS
		// Validate that all hasOne_name's refer to parameter names
		// Moreover, only allow one instance of each parameter
		HashSet<String> params = new HashSet<String>();
		HashSet<String> hasOnes = new HashSet<String>();
		for (ComputeParameter cp : cb.getComputeParameters())
		{
			String p = cp.getName();

			if (params.contains(p)) printError("In your paramer file, parameter '" + p
					+ "' occurs more than once, which is not allowed.\nPlease fix this.");

			params.add(p);
			hasOnes.addAll(cp.getHasOne_Name());
		}
		for (String h : hasOnes)
		{
			if (!params.contains(h)) printError("In your parameters file, in your hasOne_name column, you refer to a non-existing parameter '"
					+ h + "'. Please add this parameter to your file.");
		}
	}
}

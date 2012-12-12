package org.molgenis.compute.db.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.molgenis.compute.design.ComputeParameter;
import org.molgenis.compute.design.ComputeProtocol;
import org.molgenis.compute.design.ComputeRequirement;
import org.molgenis.compute.design.Workflow;
import org.molgenis.compute.design.WorkflowElement;
import org.molgenis.compute.runtime.ComputeParameterDefaultValue;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.util.CsvFileReader;
import org.molgenis.util.CsvReader;
import org.molgenis.util.Tuple;

import app.DatabaseFactory;

/**
 * Created with IntelliJ IDEA. User: georgebyelas Date: 15/08/2012 Time: 13:15
 * To change this template use File | Settings | File Templates.
 */
public class WorkflowImporterJPA
{
	private File parametersFile, workflowFile, protocolsDir;

	// workaround
	private String[] sharedProtocols =
	{ "CustomSubmit.sh.ftl", "Footer.ftl", "Header.ftl", "Macros.ftl", "Helpers.ftl" };
	private Vector<String> shared = new Vector<String>();

	public static void main(String[] args)
	{

		if (args.length == 3)
		{
			System.out.println("*** START WORKFLOW IMPORT");
		}
		else
		{
			System.out.println("Not enough parameters");
			System.exit(1);
		}

		try
		{
			new WorkflowImporterJPA().process(args[0], args[1], args[2]);
		}
		catch (DatabaseException e)
		{
			e.printStackTrace();
		}
	}

	private void process(String parametersFileName, String workflowFileName, String protocolsDirName)
			throws DatabaseException
	{
		// ignore shared protocols
		for (int i = 0; i < sharedProtocols.length; i++)
			shared.add(sharedProtocols[i]);

		Vector<ComputeParameter> oldParameters = new Vector<ComputeParameter>();

		// self-explanatory code
		parametersFile = new File(parametersFileName);
		workflowFile = new File(workflowFileName);
		protocolsDir = new File(protocolsDirName);

		continueIfExist(parametersFile);
		continueIfExist(workflowFile);
		continueIfExist(protocolsDir);

		Database db = DatabaseFactory.create();
		try
		{
			db.beginTx();

			String workflowName = workflowFile.getName();

			// create workflow
			Workflow workflow = new Workflow();
			workflow.setName(workflowName);
			db.add(workflow);

			// create one requirement which we use for all protocols (for
			// testing)
			ComputeRequirement requirement = new ComputeRequirement();
			requirement.setName("InHouseRequirement" + System.nanoTime());
			requirement.setCores(1);
			requirement.setNodes(1);
			requirement.setMem("test");
			requirement.setWalltime("test");
			db.add(requirement);

			// parse the parameters file
			CsvReader reader = new CsvFileReader(parametersFile);
			Hashtable<ComputeParameter, Vector<String>> collectionParameterHasOnes = new Hashtable<ComputeParameter, Vector<String>>();
			Vector<ComputeParameter> parameters = new Vector<ComputeParameter>();
			for (Tuple row : reader)
			{
				// String description = row.getString("description");

				String name = row.getString("Name");
				if (name.equals("#")) continue;

				ComputeParameter parameter = new ComputeParameter();
				parameter.setName(name);

				boolean addDefault = false;
				ComputeParameterDefaultValue value = null;

				// adding default value
				if (row.getString("defaultValue") != null)
				{
					parameter.setDefaultValue("has default");

					String defaultValue = row.getString("defaultValue");

					value = new ComputeParameterDefaultValue();
					value.setWorkflow(workflow);
					value.setDefaultValue(defaultValue);
					addDefault = true;
				}

				String dataType = row.getString("dataType");
				if (dataType == null) parameter.setDataType("string");
				else
					parameter.setDataType(dataType);

				String hasOne_name = row.getString("hasOne_name");
				if (hasOne_name != null)
				{
					Vector<String> hasOnes = splitCommas(hasOne_name);
					collectionParameterHasOnes.put(parameter, hasOnes);
				}
				parameters.add(parameter);

				if (parameterNotExist(db, parameter))
				{
					db.add(parameter);
					if (addDefault) value.setComputeParameter(parameter);
				}
				else
				{
					oldParameters.add(parameter);
					if (addDefault)
					{
						ComputeParameter dbParameter = db.find(ComputeParameter.class,
								new QueryRule(ComputeParameter.NAME, QueryRule.Operator.EQUALS, parameter.getName()))
								.get(0);
						value.setComputeParameter(dbParameter);
					}
				}

				if (addDefault) db.add(value);
			}

			// find parameters has ones
			Enumeration<ComputeParameter> ekeys = collectionParameterHasOnes.keys();
			while (ekeys.hasMoreElements())
			{
				ComputeParameter parameter = ekeys.nextElement();
				Vector<String> parNames = collectionParameterHasOnes.get(parameter);

				Vector<ComputeParameter> vecParameters = new Vector<ComputeParameter>();
				for (String name : parNames)
				{

					ComputeParameter hasParameter = findParameter(parameters, name.trim());

					if (hasParameter == null) System.err.println("Cannot find hasOne '" + name + "' for parameter '"
							+ parameter.getName() + "'");
					vecParameters.add(hasParameter);
				}

				if (!oldParameters.contains(parameter))
				{
					parameter.setHasOne(vecParameters);
					db.update(parameter);
				}
				else
				{
					boolean correct = checkHasOneNotCorrectness(db, parameter, vecParameters);
					if (!correct)
					{
						System.out.println("SPECIFICATION OF HAS ONE FOR PARAMETER " + parameter.getName()
								+ " IS NOT CORRECT.");
						System.out
								.println("Please check the database and parameters list or change the parameter name");
						System.exit(1);
					}
				}
			}

			// db.update(parameters);

			// add protocols
			Vector<ComputeProtocol> protocols = new Vector<ComputeProtocol>();
			if (protocolsDir.isDirectory())
			{
				String[] files = protocolsDir.list();
				for (int i = 0; i < files.length; i++)
				{
					String fName = files[i];

					String protocolName = fName;

					int workflowNumber = db.query(Workflow.class).find().size();
					if (workflowNumber > 1) if (shared.contains(fName)) continue;
					else
					{
						ComputeProtocol protocol = db.find(ComputeProtocol.class,
								new QueryRule(ComputeProtocol.NAME, QueryRule.Operator.EQUALS, fName)).get(0);
						if (protocol != null)
						{
							System.out.println("DOUBLE PROTOCOL: " + fName + "is skipped");
							continue;
						}
					}

					// Don't remove extension while importing as two files, say
					// a.x and a.y, may have the same name 'a' but a different
					// extension.
					// int dotPos = fName.lastIndexOf(".");
					// if (dotPos > -1)
					// {
					// protocolName = fName.substring(0, dotPos);
					// }
					// else
					// protocolName = fName;

					String protocolFile = protocolsDir.getPath() + System.getProperty("file.separator") + fName;

					if (new File(protocolFile).isDirectory()) continue;

					String listing = getFileAsString(protocolFile);

					// Get string list of target names
					List<String> targetStringList = findTargetList(listing);

					// convert targetList to a parameterList
					List<ComputeParameter> targetList = new ArrayList<ComputeParameter>();
					Iterator<String> it = targetStringList.iterator();
					while (it.hasNext())
					{
						String name = it.next();

						// Check whether protocol contains no target (c.q.
						// #FOREACH
						// ), which will lead to an emtpy string "" as target...
						// And
						// that leads to an error.
						// ACTUALLY THIS SHOULD BE TACKLED IN THE VALIDATOR, I
						// THINK
						// (MD)
						if (name.equals(""))
						{
							System.err.println("#");
							System.err.println("##");
							System.err
									.println("### ERROR: Protocol '"
											+ protocolFile
											+ "' contains a #FOREACH statement without specifying a target. Please remove this line, or add a target.");
							System.err.println("##");
							System.err.println("#");

							System.err.println("\nProgram exits with status code 1.");
							System.exit(1);
						}

						targetList.add(findParameter(parameters, name));
					}

					// Why do we have requirements here if we have xref to
					// ComputeRequirements

					ComputeProtocol protocol = new ComputeProtocol();
					protocol.setName(protocolName);
					protocol.setScriptTemplate(listing);
					if (0 < targetList.size()) protocol.setIterateOver(targetList);
					protocol.setRequirements(requirement);

					//
					protocol.setCores(1);
					protocol.setNodes(1);
					protocol.setMem("");
					protocol.setWalltime("");

					protocols.add(protocol);
					db.add(protocol);
				}
			}

			// add workflow elements
			Vector<WorkflowElement> workflowElements = new Vector<WorkflowElement>();
			reader = new CsvFileReader(workflowFile);
			for (Tuple row : reader)
			{
				String workflowElementName = row.getString("name");
				// We need to add the extension here. Templates (e.g. a *.tex
				// file) may have a different extension than *.ftl. So, to
				// discriminate between two templates a.x and a.y, we'd to add
				// the extension...
				String protocolName = row.getString("protocol_name") + ".ftl";
				String previousSteps = row.getString("PreviousSteps_name");

				WorkflowElement element = new WorkflowElement();
				element.setName(workflowElementName);
				element.setWorkflow(workflow);

				// ComputeProtocol p = findProtocol(protocols, protocolName);
				ComputeProtocol p = db.find(ComputeProtocol.class,
						new QueryRule(ComputeProtocol.NAME, QueryRule.Operator.EQUALS, protocolName)).get(0);

				element.setProtocol(p);

				if (previousSteps != null)
				{
					Vector<String> previousStepsNames = splitCommas(previousSteps);
					Vector<WorkflowElement> previousStepsVector = new Vector<WorkflowElement>();
					for (String prev : previousStepsNames)
					{
						WorkflowElement elPrev = findWorkflowElement(workflowElements, prev);
						previousStepsVector.add(elPrev);
					}
					element.setPreviousSteps(previousStepsVector);
				}

				workflowElements.add(element);
				db.add(element);
			}

			db.commitTx();
			System.out.println("... done");
		}
		catch (Exception e)
		{
			db.rollbackTx();
			e.printStackTrace();
		}

	}

	private boolean checkHasOneNotCorrectness(Database db, ComputeParameter parameter,
			Vector<ComputeParameter> vecParameters) throws DatabaseException
	{
		ComputeParameter dbParameter = db.find(ComputeParameter.class,
				new QueryRule(ComputeParameter.NAME, QueryRule.Operator.EQUALS, parameter.getName())).get(0);
		List<ComputeParameter> hasOnes = dbParameter.getHasOne();

		// compare names at 2 sides
		for (ComputeParameter p : hasOnes)
		{
			String name = p.getName();
			if (parameterIsMissing(name, vecParameters)) return false;
		}

		for (ComputeParameter p : vecParameters)
		{
			String name = p.getName();
			if (parameterIsMissing(name, vecParameters)) return false;
		}

		return true;
	}

	private boolean parameterIsMissing(String name, Vector<ComputeParameter> vecParameters)
	{
		for (ComputeParameter p : vecParameters)
		{
			if (p.getName().equals(name)) return false;
		}
		return true;
	}

	// checking if parameter with this name already exists
	private boolean parameterNotExist(Database db, ComputeParameter parameter) throws DatabaseException
	{
		String name = parameter.getName();
		List<ComputeParameter> p = db.find(ComputeParameter.class, new QueryRule(ComputeParameter.NAME,
				QueryRule.Operator.EQUALS, name));
		if (p.size() > 0) return false;
		return true;
	}

	private List<String> findTargetList(String listing)
	{
		int start = listing.indexOf("#FOREACH");
		if (start == -1)
		{
			return new ArrayList<String>();
		}
		else
		{
			start += "#FOREACH".length();
			int stop = listing.indexOf("\n", start);

			String targetsString = listing.substring(start, stop);
			String[] targets = targetsString.split(",");

			List<String> targetList = new ArrayList<String>();
			for (int i = 0; i < targets.length; i++)
			{
				if (!targets[i].trim().equals(""))
				{
					targetList.add(targets[i].trim());
				}
			}

			return targetList;
		}
	}

	private WorkflowElement findWorkflowElement(Vector<WorkflowElement> vector, String name)
	{
		for (WorkflowElement par : vector)
		{
			if (par.getName().equalsIgnoreCase(name)) return par;
		}
		return null;
	}

	private ComputeProtocol findProtocol(Vector<ComputeProtocol> vector, String name)
	{
		for (ComputeProtocol par : vector)
		{
			if (par.getName().equalsIgnoreCase(name)) return par;
		}
		return null;
	}

	private ComputeParameter findParameter(Vector<ComputeParameter> vector, String name)
	{
		for (ComputeParameter par : vector)
		{
			if (par.getName().equalsIgnoreCase(name))
			{
				return par;
			}
		}

		System.err.println("ERROR: Cannot find parameter " + name);
		return null;
	}

	private void continueIfExist(File file)
	{
		if (!file.exists())
		{
			System.out.println("Error: " + file.getName() + " does not exist");
			System.exit(1);
		}
	}

	private Vector<String> splitCommas(String list)
	{
		list = list.trim();
		Vector<String> values = new Vector<String>();

		while (list.indexOf(",") > -1)
		{
			int posComa = list.indexOf(",");
			String name = list.substring(0, posComa).trim();
			if (name != "") values.addElement(name);
			list = list.substring(posComa + 1);
		}
		values.add(list.trim());
		return values;
	}

	private final String getFileAsString(String filename) throws IOException
	{
		File file = new File(filename);

		final BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
		final byte[] bytes = new byte[(int) file.length()];
		bis.read(bytes);
		bis.close();
		return new String(bytes);
	}
}

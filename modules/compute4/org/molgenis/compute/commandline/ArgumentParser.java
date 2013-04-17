package org.molgenis.compute.commandline;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import org.molgenis.compute.commandline.options.Options;
import org.molgenis.compute.commandline.options.Options.Multiplicity;
import org.molgenis.compute.commandline.options.Options.Separator;

public class ArgumentParser
{
	static Options o;

	static String DEF = "DEFAULT", EMPTY = "EMPTY";

	/*
	 * The map that is returned.
	 */
	static LinkedHashMap<String, String> paramMap = new LinkedHashMap<String, String>();

	/*
	 * All command line parameters
	 */
	static List<String> parameters = Arrays.asList("inputdir", "outputdir", "workflow", "templates", "protocols",
			"parameters", "worksheet", "mcdir", "id");

	/*
	 * Get value of command line option 'option' in set 'set'.
	 */
	private static String getValue(String set, String option)
	{
		if (o.getSet(set).getOption(option).getResultCount() == 1) return o.getSet(set).getOption(option)
				.getResultValue(0);
		else
			return null;
	}

	/*
	 * Add an obligatory parameter 'name' to parameter set 'set'.
	 */
	private static void addParam(String set, String name)
	{
		o.getSet(set).addOption(name, false, Separator.EQUALS, Multiplicity.ONCE);
	}

	/*
	 * Add an optional parameter to set.
	 */
	private static void addParamOptional(String set, String name)
	{
		o.getSet(set).addOption(name, false, Separator.EQUALS, Multiplicity.ZERO_OR_ONE);
	}

	/*
	 * Fill paramMap with values based on user inputs and defaults
	 */
	private static void fillParamMap(String set)
	{
		String inputdir = "input"; // is reused below

		// set default for inputdir
		paramMap.put("inputdir", inputdir);

		// set default for outputdir
		String id = "output";
		paramMap.put("outputdir", inputdir + File.separator + id);

		// add/overwrite parameters that are not null
		for (String p : parameters)
		{
			String value = getValue(set, p);
			if (value != null) paramMap.put(p, value);
		}

		inputdir = paramMap.get("inputdir") + File.separator;

		if (getValue(set, "workflow") == null) paramMap.put("workflow", inputdir + "workflow.csv");
		if (getValue(set, "templates") == null) paramMap.put("templates", inputdir + "templates");
		if (getValue(set, "protocols") == null) paramMap.put("protocols", inputdir + "protocols");
		if (getValue(set, "parameters") == null) paramMap.put("parameters", inputdir + "parameters.csv");
		if (getValue(set, "worksheet") == null) paramMap.put("worksheet", inputdir + "worksheet.csv");
		if (getValue(set, "mcdir") == null) paramMap.put("mcdir", ".");
		if (getValue(set, "id") == null) paramMap.put("id", id);

		/*
		 * 'Repair' outputdir.
		 */
		if (getValue(set, "outputdir") == null)
		{
			paramMap.put("outputdir", inputdir + paramMap.get("id"));
		}
	}

	/*
	 * Define three different sets of parameters
	 */
	private static Options defineSets(String[] args)
	{
		o = new Options(args, Options.Prefix.DASH);

		o.addSet(DEF);
		for (String p : parameters)
			addParamOptional(DEF, p);

		o.addSet(EMPTY);

		return o;
	}

	/*
	 * Determine to which of our three parameter sets the given parameters
	 * match. Return a boolean that indicates whether a match has been found.
	 */
	private static boolean matchParameters()
	{
		boolean match = false;

		if (o.check(DEF, false, false) || o.check(EMPTY, false, false))
		{
			match = true;
			fillParamMap(DEF);
		}

		return match;
	}

	/*
	 * Takes command line arguments as input and returns a map with the
	 * following parameters: inputdir, outputdir, worksheet, workflow,
	 * protocols, parameters, id and mcdir. But, if command line arguments are
	 * not specified properly, then an error message this function produces an
	 * error message and exits with status code 1.
	 */
	public static LinkedHashMap<String, String> parseParameters(String[] args, Exiter exiter)
	{
		defineSets(args);

		if (!matchParameters())
		{
			System.err.println("#");
			System.err.println("##");
			System.err.println("### Begin of error message.");
			System.err.println("##");
			System.err.println("#\n");

			// System.err.println(o.getCheckErrors());

			System.err.println("Valid command line arguments are:\n");

			System.err
					.println("  -inputdir=<input>                     # Directory with default inputs: workflow.cvs, protocols, parameters.csv, worksheet.csv.");
			System.err
					.println("  -outputdir=<inputdir/id>              # Directory where the generated scripts will be stored.");
			System.err
					.println("  -workflow=<inputdir/workflow.csv>     # A file describing the workflowsteps and their interdependencies.");
			System.err
					.println("  -templates=<inputdir/templates>       # A directory containing your *.ftl template files.");
			System.err
					.println("  -protocols=<inputdir/protocols>       # A directory containing the *.ftl protocol files.");
			System.err
					.println("  -parameters=<inputdir/parameters.csv> # A file that describes the parameters that are used in the protocols.");
			System.err.println("  -worksheet=<inputdir/worksheet.csv>   # A file that describes the work to be doen.");
			System.err
					.println("  -id=<output>                          # An ID that may be different each time you generate.");
			System.err
					.println("  -mcdir=<.>                            # A directory containing molgenis_compute.sh. This may be used by a protocol that executes that script.");

			System.err
					.println("\nNo parameters are obligatory. Each parameter has its default value, as specified betweet the brackets < and >.\n");

			System.err.println("#");
			System.err.println("##");
			System.err.println("### End of error message.");
			System.err.println("##");
			System.err.println("#");

			System.err.println("\nProgram exits with status code 1.");

			exiter.exit();
			// systemExit();
		}
		else
		{
			System.out.println(">> Parsed command line parameters:");
			for (String p : paramMap.keySet())
				System.out.println("   -" + p + "=" + paramMap.get(p));
			System.out.println("\n");
		}

		return (paramMap);
	}

	private static void systemExit()
	{
		System.exit(1);
	}
}

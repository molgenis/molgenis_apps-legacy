package org.molgenis.compute.test.util;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.molgenis.compute.commandline.WorksheetHelper;
import org.molgenis.compute.design.ComputeProtocol;
import org.molgenis.compute.design.Workflow;
import org.molgenis.compute.test.generator.ComputeGenerator;
import org.molgenis.compute.test.generator.ComputeGeneratorDBWorksheet;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.util.Tuple;
import org.molgenis.util.cmdline.CmdLineException;

import app.DatabaseFactory;

public class WorksheetImporter
{

	public static void main(String[] args) throws CmdLineException, DatabaseException
	{
		System.out.println(">> Start..");

		// read the parameters
		ImportWorksheetOptions options = new ImportWorksheetOptions(args);

		// get workflow from database
		Database db = DatabaseFactory.create();
		Query<Workflow> wf_query = db.query(Workflow.class).eq(Workflow.NAME, options.workflow_name);
		if (wf_query.count() < 1) throw new RuntimeException("Workflow name=" + options.workflow_name
				+ " not known in database. Please import");
		Workflow workflow = wf_query.find().get(0);

		// load targets from a worksheet
		List<Tuple> worksheet = null;
		try
		{
			worksheet = (new WorksheetHelper()).readTuplesFromFile(new File(options.worksheet_file));
			// "/Users/mdijkstra/Documents/work/git/molgenis_apps/modules/compute/workflows/in-house_worksheet.csv"));
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
		}

		for (Tuple t : worksheet)
		{
			t.set("McId", options.McId);
			t.set("McDir", "database");
			t.set("McWorksheet", options.worksheet_file);
			t.set("McParameters", "database");
			t.set("McWorkflow", "database");
			t.set("McProtocols", "database");
		}

		// generate ComputeTasks
		ComputeGenerator generator = new ComputeGeneratorDBWorksheet();

		// Put protocols in a temporary directory to enable a protocol to
		// include other protocols while generating. We do it this way because
		// we want to use Freemarker to handle the includes. Doing the includes
		// ourselves (in memory) would mean that we would have to deal with many
		// exceptional cases, which are now automatically handled by Freemarker.

		String protocolsDirName = System.getProperty("java.io.tmpdir") + worksheet.get(0).getString("McId")
				+ System.getProperty("file.separator");

		File protocolsDir = new File(protocolsDirName);

		saveProtocolsInDir(db, protocolsDir);

		generator.generateTasks(workflow, worksheet, protocolsDir);

		System.out.println("... generated");
	}

	/**
	 * Save protocols from DB in the directory protocolsDir
	 */
	private static void saveProtocolsInDir(Database db, File protocolsDir)
	{
		// remove if directory exists
		if (protocolsDir.exists()) try
		{
			FileUtils.deleteDirectory(protocolsDir);
		}
		catch (IOException e)
		{
			System.err.println(">> ERROR: Unable to delete tmp dir: " + protocolsDir);
			e.printStackTrace();
		}

		// create new, empty directory
		boolean success = protocolsDir.mkdirs();

		if (success) System.out.println(">> Created an empty tmp directory to store protocols: " + protocolsDir);
		else
			throw new RuntimeException(">> ERROR: Unable to create tmp directory " + protocolsDir);

		// put protocols there
		Query<ComputeProtocol> cp_query = db.query(ComputeProtocol.class);
		try
		{
			System.out.println(">> Saving protocols...");
			Iterator<ComputeProtocol> it = cp_query.find().iterator();
			while (it.hasNext())
			{
				ComputeProtocol cp = it.next();
				FileUtils.writeStringToFile(
						new File(protocolsDir + System.getProperty("file.separator") + cp.getName()),
						cp.getScriptTemplate());
			}
		}
		catch (DatabaseException e)
		{
			System.err.println(">> ERROR: unable to iterate over ComputeProtocols from db");
			e.printStackTrace();
		}
		catch (IOException e)
		{
			System.err.println(">> ERROR: Something goes wrong");
			e.printStackTrace();
		}
	}
}

package org.molgenis.compute.db;

import org.molgenis.compute.db.generator.ComputeGenerator;
import org.molgenis.compute.db.generator.ComputeGeneratorDB;
import org.molgenis.compute.db.reader.WorkflowReader;
import org.molgenis.compute.db.reader.WorkflowReaderDBJPA;
import org.molgenis.compute.db.temp.Target;
import org.molgenis.compute.design.ComputeParameter;
import org.molgenis.compute.design.Workflow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class PilotDemo
{
	public static void main(String[] args) throws IOException
	{
		WorkflowReader reader = new WorkflowReaderDBJPA();
		// WorkflowReader reader = new WorkflowReaderDBJDBC();
		List<ComputeParameter> parameters = reader.getParameters();

		// read test workflow
		Workflow workflow = reader.getWorkflow("TestWorkflow");

		// read test targets
		Target target = new Target();
		List<Target> targetList = new ArrayList<Target>();
		targetList.add(target);

		// create user values
		Hashtable<String, String> userValues = new Hashtable<String, String>();
		userValues.put(ComputeGeneratorDB.RUN_ID, "test1");
		// todo add mc parameters!

		// generate ComputeTasks
		ComputeGenerator generator = new ComputeGeneratorDB();
		generator.generate(workflow, parameters, targetList, userValues);

		System.out.println("... generated");
	}
}

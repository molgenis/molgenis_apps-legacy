package org.molgenis.compute.db.generator;

import app.DatabaseFactory;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.FileUtils;
import org.molgenis.compute.commandline.FreemarkerHelper;
import org.molgenis.compute.commandline.Worksheet;
import org.molgenis.compute.design.ComputeParameter;
import org.molgenis.compute.design.ComputeProtocol;
import org.molgenis.compute.design.Workflow;
import org.molgenis.compute.design.WorkflowElement;
import org.molgenis.compute.runtime.ComputeParameterDefaultValue;
import org.molgenis.compute.runtime.ComputeTask;
import org.molgenis.compute.db.temp.Target;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.util.Pair;
import org.molgenis.util.Tuple;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;

/**
 * Created with IntelliJ IDEA. User: georgebyelas Date: 22/08/2012 Time: 12:05
 * To change this template use File | Settings | File Templates.
 */

public class ComputeGeneratorDBWorksheet implements ComputeGenerator {
	// supplementary (just because it's handy to use)
	// one workflow element can have many tasks generated from it, so we store
	// them as a vector of pairs
	Vector<Pair> workflowElementComputeTaskPairs = new Vector<Pair>();

	Database db = null;

	public void generate(Workflow workflow, List<ComputeParameter> parameters, List<Target> targets,
			Hashtable<String, String> config) {
	}

	/**
	 * Create a script, given a tuple from folded worksheet, taskName,
	 * workflowElementsList and ComputeParameter list
	 * 
	 * @param work
	 * @param taskName
	 * @param workflowElementsList
	 * @param protocolsDir
	 * @return filledtemplate.toString();
	 */
	private String createScript(String templateScript, Tuple work,
			String taskName, Collection<WorkflowElement> workflowElementsList,
			List<ComputeParameter> paramList, File protocolsDir) {

		// put all parameters from tuple in hashmap for weaving
		Map<String, Object> parameters = new HashMap<String, Object>();
		for (String field : work.getFields()) {
			parameters.put(field, work.getObject(field));
		}

		// add the helper
		parameters.put("freemarkerHelper", new FreemarkerHelper(paramList));
		parameters.put("parameters", work);
		parameters.put("workflowElements", workflowElementsList);

		try {
			Configuration cfg = new Configuration();

			// Set path so that protocols can include other protocols using the
			// "include" statement

			cfg.setDirectoryForTemplateLoading(protocolsDir);

			System.out.println(">> Create script name: " + taskName);
			// System.out.println(">> Create script template: " +
			// templateScript);

			Template template = new Template(taskName, new StringReader(
					templateScript), cfg);
			StringWriter script = new StringWriter();
			template.process(parameters, script);

			return script.toString();
		} catch (IOException e) {
			System.err.println(">> ERROR >> IOException");
			e.printStackTrace();
		} catch (TemplateException e) {
			System.err.println(">> ERROR >> TemplateException");
			e.printStackTrace();
		}
		return null;
	}

	public void generateWithTuple(Workflow workflow, List<Tuple> targets,
			Hashtable<String, String> config) {
		// TODO Auto-generated method stub

	}

	/**
	 * Generate tasks and put them into the database
	 */
	public void generateTasks(Workflow workflow, List<ComputeParameter> pList, List<Tuple> worksheet, String backend_name)
    {
        List<ComputeParameter> parameterList = pList;
        try
        {
            db = DatabaseFactory.create();

        }
        catch (DatabaseException e)
        {
            e.printStackTrace();
        }


        //here, substitute default values of compute parameters with actual one for workflow
        for(ComputeParameter parameter : parameterList)
        {
            if(parameter.getDefaultValue() != null)
            {
                try
                {
                    List<ComputeParameterDefaultValue> defaultValues = db.query(ComputeParameterDefaultValue.class)
                            .equals(ComputeParameterDefaultValue.COMPUTEPARAMETER_NAME, parameter.getName())
                            .equals(ComputeParameterDefaultValue.WORKFLOW_NAME, workflow.getName()).find();

                    if(defaultValues.size() > 0)
                    {
                        String value = defaultValues.get(0).getDefaultValue();
                        parameter.setDefaultValue(value);
                    }
                }
                catch (DatabaseException e)
                {
                    e.printStackTrace();
                }
            }
        }

		Collection<WorkflowElement> workflowElementsList = workflow.getWorkflowWorkflowElementCollection();

		// Put protocols in a temporary directory to enable a protocol to
		// include other protocols while generating. We do it this way because
		// we want to use Freemarker to handle the includes. Doing the includes
		// ourselves (in memory) would mean that we would have to deal with many
		// exceptional cases, which are now automatically handled by Freemarker.


		String protocolsDirName = System.getProperty("java.io.tmpdir")
				+ System.getProperty("file.separator")
				+ worksheet.get(0).getString("McId")
				+ System.getProperty("file.separator");

		File protocolsDir = new File(protocolsDirName);

		saveProtocolsInDir(db, protocolsDir);

		// I guess, we should also add line_number as a 'ComputeParameter'...
		ComputeParameter line_number = new ComputeParameter();
		line_number.setName("line_number");
		line_number.setDefaultValue(null);
		parameterList.add(line_number);

		// Create a Worksheet entity, just as in Compute 2
		Worksheet worksheetEntity = new Worksheet(parameterList, worksheet);

		List<ComputeTask> tasks = new ArrayList<ComputeTask>();
		for (WorkflowElement workflowElement : workflowElementsList) {

			// System.out.println(">> Workflow element name: " +
			// workflowElement.getName());
			// System.out.println(">> Protocol name: " +
			// workflowElement.getProtocol_Name());
			// System.out.println(">> Protocol template: " +
			// workflowElement.getProtocol().getScriptTemplate());

			List<String> iterationTargetNameList = new ArrayList<String>();
			Iterator<ComputeParameter> it = workflowElement.getProtocol()
					.getIterateOver().iterator();
			while (it.hasNext()) {
				iterationTargetNameList.add(it.next().getName());
			}

			// if no targets specified, then actually we mean "all targets".
			// Therefore, we add line_number as a target.
			// MD: is this best place to do this?!
			if (0 == iterationTargetNameList.size()) {
				iterationTargetNameList.add("line_number");
			}

			List<Tuple> foldedWorksheet = Worksheet.foldWorksheet(
					worksheetEntity.worksheet, parameterList,
					iterationTargetNameList);

			String template = workflowElement.getProtocol().getScriptTemplate();

			// Add Header.ftl
			template = "<#include \"Header.ftl\" />\n" + template;
			// Add Footer.ftl
			template += "\n<#include \"Footer.ftl\" />\n";

			for (Tuple work : foldedWorksheet) {
				// put ComputeParams in map
				Map<String, Object> parameters = new HashMap<String, Object>();
				for (String field : work.getFields()) {
					parameters.put(field, work.getObject(field));
				}

				// construct taskName
				String lala = work.getList("McId").get(0) + "_"
						+ System.nanoTime();

				String taskName = workflowElement.getName() + "_" + lala;
				// String taskName = workflowElement.getName() + "_" +
				// parameters.get("McId") + "_"
				// + parameters.get("line_number");

				String script = createScript(template, work, taskName,
						workflowElementsList, parameterList, protocolsDir);

				ComputeTask task = new ComputeTask();
				task.setName(taskName);
				task.setComputeScript(script);
                task.setBackEndName("grid");
                task.setBackEndName(backend_name);
				task.setInterpreter(workflowElement.getProtocol()
						.getScriptInterpreter());
				task.setRequirements(workflowElement.getProtocol()
						.getRequirements());
				task.setWorkflowElement(workflowElement);
				task.setStatusCode("generated");

				List<WorkflowElement> prev = workflowElement.getPreviousSteps();
				List<ComputeTask> prevTasks = new ArrayList<ComputeTask>();

				for (WorkflowElement w : prev) {
					List<ComputeTask> listPrevTasks = getPreviousTasks(w);
					prevTasks.addAll(listPrevTasks);
				}
				task.setPrevSteps(prevTasks);

				tasks.add(task);

				// because it's handy:
				Pair pair = new Pair();
				pair.setA(workflowElement);
				pair.setB(task);
				workflowElementComputeTaskPairs.add(pair);
			}
		}

		try
        {
            db.beginTx();
			db.add(tasks);
			db.commitTx();
		}
        catch (DatabaseException e)
        {
			e.printStackTrace();
		}

	}

	private List<ComputeTask> getPreviousTasks(WorkflowElement w) {
		List<ComputeTask> list = new ArrayList<ComputeTask>();

		for (Pair p : workflowElementComputeTaskPairs) {
			WorkflowElement we = (WorkflowElement) p.getA();
			if (we.equals(w)) {
				ComputeTask task = (ComputeTask) p.getB();
				list.add(task);
			}
		}
		return list;
	}

	/**
	 * Save protocols from DB in the directory protocolsDir
	 */
	private static void saveProtocolsInDir(Database db, File protocolsDir) {
		// remove if directory exists
		if (protocolsDir.exists())
			try {
				FileUtils.deleteDirectory(protocolsDir);
			} catch (IOException e) {
				System.err.println(">> ERROR: Unable to delete tmp dir: "
						+ protocolsDir);
				e.printStackTrace();
			}

		// create new, empty directory
		boolean success = protocolsDir.mkdirs();

		if (success)
			System.out
					.println(">> Created an empty tmp directory to store protocols: "
							+ protocolsDir);
		else
			throw new RuntimeException(
					">> ERROR: Unable to create tmp directory " + protocolsDir);

		// put protocols there
		Query<ComputeProtocol> cp_query = db.query(ComputeProtocol.class);
		try {
			System.out.println(">> Saving protocols...");
			Iterator<ComputeProtocol> it = cp_query.find().iterator();
			while (it.hasNext()) {
				ComputeProtocol cp = it.next();
				FileUtils.writeStringToFile(
						new File(protocolsDir
								+ System.getProperty("file.separator")
								+ cp.getName()), cp.getScriptTemplate());
			}
		} catch (DatabaseException e) {
			System.err
					.println(">> ERROR: unable to iterate over ComputeProtocols from db");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println(">> ERROR: Something goes wrong");
			e.printStackTrace();
		}
	}

}

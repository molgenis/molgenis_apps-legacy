package org.molgenis.compute.test.reader;

import java.io.IOException;
import java.util.List;

import org.molgenis.compute.design.ComputeParameter;
import org.molgenis.compute.design.ComputeProtocol;
import org.molgenis.compute.design.Workflow;
import org.molgenis.compute.design.WorkflowElement;
import org.molgenis.compute.test.reader.WorkflowReader;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;

import app.DatabaseFactory;

/**
 * Created with IntelliJ IDEA. User: georgebyelas Date: 24/08/2012 Time: 11:17
 * To change this template use File | Settings | File Templates.
 */
public class WorkflowReaderDBJDBC implements WorkflowReader
{
	public Workflow getWorkflow(String name) throws IOException
	{
		Database db = null;
		try
		{
			db = DatabaseFactory.create();
			db.beginTx();

			Workflow w = db.find(Workflow.class, new QueryRule(Workflow.NAME, QueryRule.Operator.EQUALS, name)).get(0);

			List<WorkflowElement> workflowElements = db.find(WorkflowElement.class, new QueryRule(
					WorkflowElement.WORKFLOW_NAME, QueryRule.Operator.EQUALS, w.getName()));

			for (WorkflowElement we : workflowElements)
			{
				String protocol_name = we.getProtocol_Name();
				ComputeProtocol protocol = db.find(ComputeProtocol.class,
						new QueryRule(ComputeProtocol.NAME, QueryRule.Operator.EQUALS, protocol_name)).get(0);
				we.setProtocol(protocol);

				// we.getPreviousSteps_Id();

				// List<WorkflowElement> prev = db.find(WorkflowElement.class,
				// new QueryRule(WorkflowElement_PreviousSteps.,
				// QueryRule.Operator.EQUALS, we.getName()));
				int i = 0;
			}

			db.close();
			return w;
		}
		catch (DatabaseException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public List<ComputeParameter> getParameters()
	{
		// TODO: we do not use JDBC workflow reader - otherwise it should be
		// implemented
		return null;
	}

}

package org.molgenis.compute.test.reader;

import java.io.IOException;
import java.util.List;

import org.molgenis.compute.design.ComputeParameter;
import org.molgenis.compute.design.Workflow;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;

import app.DatabaseFactory;

/**
 * Created with IntelliJ IDEA. User: georgebyelas Date: 22/08/2012 Time: 10:18
 * To change this template use File | Settings | File Templates.
 */
public class WorkflowReaderDBJPA implements WorkflowReader
{
	public Workflow getWorkflow(String name) throws IOException
	{
		Database db = null;
		try
		{
			db = DatabaseFactory.create();
			db.beginTx();

			// Workflow w = db.query(Workflow.class).find().get(0);
			Workflow w = db.find(Workflow.class, new QueryRule(Workflow.NAME, QueryRule.Operator.EQUALS, name)).get(0);

			db.close();
			return w;
		}
		catch (DatabaseException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public List<ComputeParameter> getParameters() throws IOException
	{
		Database db = null;
		try
		{
			db = DatabaseFactory.create();
			db.beginTx();

			// Workflow w = db.query(Workflow.class).find().get(0);
			List<ComputeParameter> parameters = db.query(ComputeParameter.class).find();
			db.close();
			return parameters;
		}
		catch (DatabaseException e)
		{
			e.printStackTrace();
		}
		return null;
	}

}

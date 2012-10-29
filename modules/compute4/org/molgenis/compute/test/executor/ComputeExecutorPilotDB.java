package org.molgenis.compute.test.executor;

import app.DatabaseFactory;
import org.molgenis.compute.runtime.ComputeHost;
import org.molgenis.compute.runtime.ComputeTask;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;

import java.io.IOException;
import java.util.List;

/**
 * Created with IntelliJ IDEA. User: georgebyelas Date: 22/08/2012 Time: 14:26
 * To change this template use File | Settings | File Templates.
 */
public class ComputeExecutorPilotDB implements ComputeExecutor
{
	public static final String BACK_END_GRID = "grid";
	public static final String BACK_END_CLUSTER = "cluster";

	private ExecutionHost host = null;

	// actual start pilots here
	public void executeTasks(String backend)
	{
		// evaluate if we have tasks ready to run
		// Database db = null;
		// List<ComputeTask> generatedTasks = null;
		int readyToSubmitSize = 0;

		try
		{
			Database db = DatabaseFactory.create();
			db.beginTx();

			List<ComputeTask> generatedTasks = db.find(ComputeTask.class, new QueryRule(ComputeTask.STATUSCODE,
					QueryRule.Operator.EQUALS, "generated"));
			readyToSubmitSize = evaluateTasks(db, generatedTasks);
			db.commitTx();

			List<ComputeTask> readyTasks = db.find(ComputeTask.class, new QueryRule(ComputeTask.STATUSCODE,
					QueryRule.Operator.EQUALS, "ready"));
			readyToSubmitSize = readyTasks.size();

		}
		catch (DatabaseException e)
		{
			e.printStackTrace();
		}

		System.out.println("task ready for execution " + readyToSubmitSize);

		// create free pilots for one actual task
		// readyToSubmitSize = readyToSubmitSize * 3;

		// start as many pilots as we have tasks ready to run
		for (int i = 0; i < readyToSubmitSize; i++)
		{
			try
			{
				if (backend.equalsIgnoreCase(BACK_END_GRID)) host.submitPilotGrid();
				else if (backend.equalsIgnoreCase(BACK_END_CLUSTER)) host.submitPilotCluster();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}

			// sleep, because we have a strange behavior in pilot service
			try
			{
				Thread.sleep(10000);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

	private int evaluateTasks(Database db, List<ComputeTask> generatedTasks)
	{
		int count = 0;
		for (ComputeTask task : generatedTasks)
		{
			boolean isReady = true;
			List<ComputeTask> prevSteps = task.getPrevSteps();
			for (ComputeTask prev : prevSteps)
			{
				if (!prev.getStatusCode().equalsIgnoreCase("done")) isReady = false;
			}

			if (isReady)
			{
				System.out.println(">>> TASK " + task.getName() + " is ready for execution");
				// count++;
				task.setStatusCode("ready");
			}
		}
		return count;
	}

	public void startHost(String name)
	{
		// get host name from database
		Database db = null;
		ComputeHost dbHost = null;
		try
		{
			db = DatabaseFactory.create();
			db.beginTx();

			dbHost = db.find(ComputeHost.class, new QueryRule(ComputeHost.NAME, QueryRule.Operator.EQUALS, name))
					.get(0);
			db.close();
		}
		catch (DatabaseException e)
		{
			e.printStackTrace();
		}

		try
		{
			host = new ExecutionHost(dbHost.getHostName(), dbHost.getHostUsername(), dbHost.getHostPassword(), 22);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}

	public void startHostWithCredentials(String h, String user, String password, int port)
	{
		try
		{
			host = new ExecutionHost(h, user, password, port);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}

}

package org.molgenis.compute.test;

import org.molgenis.compute.test.executor.ComputeExecutor;
import org.molgenis.compute.test.executor.ComputeExecutorPilotDB;

public class JobExecutor
{
	public static void main(String[] args)
	{
		System.out.println("execute with pilots on the grid");
		// execute generated tasks with pilots
		ComputeExecutor executor = new ComputeExecutorPilotDB();
		executor.startHost("lsgrid");

		while (true)
		{
			executor.executeTasks("");
			try
			{
				Thread.sleep(3000);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}

package org.molgenis.compute.test;

import org.molgenis.compute.test.executor.ComputeExecutor;
import org.molgenis.compute.test.executor.ComputeExecutorPilotDB;

/**
 * Created with IntelliJ IDEA.
 * User: georgebyelas
 * Date: 13/09/2012
 * Time: 16:10
 * To change this template use File | Settings | File Templates.
 */
public class RunDemoTasksGrid
{
    public static void main(String[] args)
    {
        System.out.println("execute with pilots on the grid");
        // execute generated tasks with pilots
        ComputeExecutor executor = new ComputeExecutorPilotDB();
        executor.startHost("lsgrid");
        executor.executeTasks(ComputeExecutorPilotDB.BACK_END_GRID);

        while (true)
        {
            executor.executeTasks(ComputeExecutorPilotDB.BACK_END_GRID);
            try
            {
                Thread.sleep(120000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
}

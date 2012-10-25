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
public class RunPilotsOnLocalhost
{
    public static void main(String[] args)
    {
        System.out.println("execute with pilots on localhost");
        // execute generated tasks with pilots
        ComputeExecutor executor = new ComputeExecutorPilotDB();

        while (true)
        {
            executor.executeTasks("",ComputeExecutorPilotDB.BACK_END_LOCALHOST);
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
}

package org.molgenis.compute.test.executor;

import app.DatabaseFactory;
import org.molgenis.compute.runtime.ComputeTask;
import org.molgenis.compute.test.sysexecutor.SysCommandExecutor;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;

import java.io.IOException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: georgebyelas
 * Date: 22/08/2012
 * Time: 14:26
 * To change this template use File | Settings | File Templates.
 */
public class ComputeExecutorPilotDB implements ComputeExecutor
{
    public static final String BACK_END_GRID = "grid";
    public static final String BACK_END_CLUSTER = "cluster";
    public static final String BACK_END_LOCALHOST = "localhost";


    private ExecutionHost host = null;
    SysCommandExecutor localExecutor = new SysCommandExecutor();

    private Database db = null;

    public ComputeExecutorPilotDB()
    {
        startDB();
    }

    private void startDB()
    {
        try
        {
            db = DatabaseFactory.create();
        }
        catch (DatabaseException e)
        {
            e.printStackTrace();
        }
    }

    //actual start pilots here
    public void executeTasks(String backend, String backendType)
    {
        //evaluate if we have tasks ready to run on a specific back-end
        int readyToSubmitSize = 0;

        try
        {
            db.beginTx();

//            List<ComputeTask> generatedTasks = db.find(ComputeTask.class, new QueryRule(ComputeTask.STATUSCODE, QueryRule.Operator.EQUALS, "generated"));

            List<ComputeTask> generatedTasks = db.query(ComputeTask.class)
                    .equals(ComputeTask.STATUSCODE, "generated")
                    .equals(ComputeTask.BACKENDNAME, backend).find();

            readyToSubmitSize = evaluateTasks(generatedTasks);

//            List<ComputeTask> readyTasks = db.find(ComputeTask.class, new QueryRule(ComputeTask.STATUSCODE, QueryRule.Operator.EQUALS, "ready"));
            List<ComputeTask> readyTasks = db.query(ComputeTask.class)
                    .equals(ComputeTask.STATUSCODE, "ready")
                    .equals(ComputeTask.BACKENDNAME, backend).find();

            readyToSubmitSize = readyTasks.size();

        }
        catch (DatabaseException e)
        {
            e.printStackTrace();
        }

        System.out.println("task ready for execution " + readyToSubmitSize);

        //create free pilots for one actual task
        //readyToSubmitSize = readyToSubmitSize * 3;

        //start as many pilots as we have tasks ready to run
        for (int i = 0; i < readyToSubmitSize; i++)
        {
            try
            {
                if (backendType.equalsIgnoreCase(BACK_END_GRID))
                    host.submitPilotGrid();
                else if (backendType.equalsIgnoreCase(BACK_END_CLUSTER))
                    host.submitPilotCluster();
                else if (backendType.equalsIgnoreCase(BACK_END_LOCALHOST))
                    submitPilotLocalhost();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            //sleep, because we have a strange behavior in pilot service
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

    private void submitPilotLocalhost() throws IOException
    {
        String str = System.nanoTime() + "";

        String command = "sh /Users/georgebyelas/Development/molgenis_modules/maverick_demo/maverick.sh " +str;
    	System.out.println(">>> " + command);

        try
        {
            localExecutor.runCommand(command);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        String cmdError = localExecutor.getCommandError();
        String cmdOutput = localExecutor.getCommandOutput();

        System.out.println(cmdError);
      	System.out.println(cmdOutput);
    }

    private int evaluateTasks(List<ComputeTask> generatedTasks) throws DatabaseException
    {
        int count = 0;
        for (ComputeTask task : generatedTasks)
        {
            boolean isReady = true;
            List<ComputeTask> prevSteps = task.getPrevSteps();
            for (ComputeTask prev : prevSteps)
            {
                if (!prev.getStatusCode().equalsIgnoreCase("done"))
                    isReady = false;
            }

            if (isReady)
            {
                System.out.println(">>> TASK " + task.getName() + " is ready for execution");
                //count++;
                task.setStatusCode("ready");
                db.commitTx();
            }
        }
        return count;
    }

    public void startHost(String name)
    {

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

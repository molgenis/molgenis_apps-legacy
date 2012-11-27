package plugins.harmonization;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;

public class MonitorJob implements Job
{
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException
	{
		HarmonizationModel model = (HarmonizationModel) context.getJobDetail().getJobDataMap().get("model");

		if (model.isStringMatching())
		{
			int totalNumber = model.getTotalNumber();

			int finishedNumber = model.getFinishedNumber();

			System.out.println("Finished: " + finishedNumber + ". Total number is " + totalNumber);

			if (model.getTotalJobs() == model.getFinishedJobs())
			{
				try
				{
					context.getScheduler().shutdown();
				}
				catch (SchedulerException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else
			{
				System.out.println("Finished jobs: " + model.getFinishedJobs() + ". Total number is "
						+ model.getTotalJobs());
			}
		}
	}
}
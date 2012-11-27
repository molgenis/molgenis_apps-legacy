package plugins.harmonization;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.molgenis.pheno.Measurement;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import plugins.HarmonizationComponent.LevenshteinDistanceModel;
import plugins.HarmonizationComponent.MappingList;

public class StringMatchingJob implements Job
{
	@SuppressWarnings("unchecked")
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException
	{
		try
		{
			PredictorInfo predictor = (PredictorInfo) context.getJobDetail().getJobDataMap().get("predictor");

			List<Measurement> measurements = (List<Measurement>) context.getJobDetail().getJobDataMap()
					.get("measurements");

			HarmonizationModel model = (HarmonizationModel) context.getJobDetail().getJobDataMap().get("model");

			LevenshteinDistanceModel matchingModel = (LevenshteinDistanceModel) context.getJobDetail().getJobDataMap()
					.get("matchingModel");

			MappingList mappings = new MappingList();

			for (String eachQuery : predictor.getExpandedQuery())
			{
				executeMapping(matchingModel, eachQuery, mappings, measurements);

				model.setFinishedNumber(model.getFinishedNumber() + 1);
			}

			model.setFinishedJobs(model.getFinishedJobs() + 1);

			predictor.setMappings(mappings);

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void executeMapping(LevenshteinDistanceModel model, String eachQuery, MappingList mappings,
			List<Measurement> measurementsInStudy) throws Exception
	{
		List<String> tokens = model.createNGrams(eachQuery.toLowerCase().trim(), true);

		for (Measurement m : measurementsInStudy)
		{
			List<String> fields = new ArrayList<String>();

			if (m.getDescription() != null && !StringUtils.isEmpty(m.getDescription()))
			{
				fields.add(m.getDescription());

				StringBuilder combinedString = new StringBuilder();

				if (m.getCategories_Name().size() > 0)
				{
					for (String categoryName : m.getCategories_Name())
					{
						combinedString.delete(0, combinedString.length());

						combinedString.append(categoryName.replaceAll(m.getInvestigation_Name(), "")).append(" ")
								.append(m.getDescription());

						fields.add(combinedString.toString().replaceAll("_", " "));
					}
				}
			}

			for (String question : fields)
			{
				List<String> dataItemTokens = model.createNGrams(question.toLowerCase().trim(), true);

				double similarity = model.calculateScore(dataItemTokens, tokens);

				mappings.add(eachQuery, (m.getDescription() == null ? m.getName() : m.getDescription()), similarity,
						m.getName());
			}
		}
	}
}
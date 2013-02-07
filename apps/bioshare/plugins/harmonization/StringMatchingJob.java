package plugins.harmonization;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.molgenis.pheno.Measurement;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;

import plugins.HarmonizationComponent.MappingList;
import plugins.HarmonizationComponent.NGramMatchingModel;

public class StringMatchingJob implements StatefulJob
{
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException
	{
		try
		{
			PredictorInfo predictor = (PredictorInfo) context.getJobDetail().getJobDataMap().get("predictor");

			HarmonizationModel dataModel = (HarmonizationModel) context.getJobDetail().getJobDataMap().get("model");

			NGramMatchingModel matchingModel = (NGramMatchingModel) context.getJobDetail().getJobDataMap()
					.get("matchingModel");

			MappingList mappings = new MappingList();

			for (String eachQuery : predictor.getExpandedQuery())
			{
				executeMapping(matchingModel, eachQuery, mappings, dataModel);

				// dataModel.setFinishedNumber(dataModel.getFinishedNumber() +
				// 1);
				dataModel.incrementFinishedQueries();
			}

			dataModel.incrementFinishedJob();

			predictor.setMappings(mappings);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void executeMapping(NGramMatchingModel matchingModel, String eachQuery, MappingList mappings,
			HarmonizationModel dataModel) throws Exception
	{
		Set<String> tokens = matchingModel.createNGrams(eachQuery.toLowerCase().trim(), true);

		for (Entry<Measurement, List<Set<String>>> entry : dataModel.getnGramsMapForMeasurements().entrySet())
		{
			Measurement m = entry.getKey();

			for (Set<String> eachNGrams : entry.getValue())
			{
				double similarity = matchingModel.calculateScore(eachNGrams, tokens);

				mappings.add(eachQuery, (m.getDescription() == null ? m.getName() : m.getDescription()), similarity,
						m.getName());
			}
		}
	}
}
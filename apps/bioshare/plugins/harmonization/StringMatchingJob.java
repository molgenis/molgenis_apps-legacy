package plugins.harmonization;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

			// MappingList mappings = new MappingList();

			Map<String, MappingList> mappingsForStudies = new HashMap<String, MappingList>();

			for (String eachQuery : predictor.getExpandedQuery())
			{
				executeMapping(matchingModel, eachQuery, mappingsForStudies, dataModel);

				// dataModel.setFinishedNumber(dataModel.getFinishedNumber() +
				// 1);
				dataModel.incrementFinishedQueries();
			}

			dataModel.incrementFinishedJob();

			predictor.setMappings(mappingsForStudies);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void executeMapping(NGramMatchingModel matchingModel, String eachQuery,
			Map<String, MappingList> mappingsForStudies, HarmonizationModel dataModel) throws Exception
	{
		Set<String> tokens = matchingModel.createNGrams(eachQuery.toLowerCase().trim(), true);

		for (Entry<String, Map<Measurement, List<Set<String>>>> entry : dataModel.getnGramsMapForMeasurements()
				.entrySet())
		{
			String invesigationName = entry.getKey();

			Map<Measurement, List<Set<String>>> measurementMap = entry.getValue();

			MappingList mappings = null;

			if (mappingsForStudies.containsKey(invesigationName))
			{
				mappings = mappingsForStudies.get(invesigationName);
			}
			else
			{
				mappings = new MappingList();
			}

			for (Measurement m : measurementMap.keySet())
			{
				for (Set<String> eachNGrams : measurementMap.get(m))
				{
					double similarity = matchingModel.calculateScore(eachNGrams, tokens);

					mappings.add(eachQuery, m, similarity);
				}
			}

			mappingsForStudies.put(invesigationName, mappings);
		}
	}
}
package plugins.harmonization;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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

			for (Entry<String, Map<Integer, List<Set<String>>>> entry : dataModel.getnGramsMapForMeasurements()
					.entrySet())
			{
				String investigationName = entry.getKey();
				MappingList mappingList = new MappingList();

				for (String eachQuery : predictor.getExpandedQuery())
				{
					executeMapping(matchingModel, eachQuery, mappingList, entry.getValue());
					dataModel.incrementFinishedQueries();
				}

				mappingsForStudies.put(investigationName, mappingList);
			}

			dataModel.incrementFinishedJob();
			predictor.setMappings(mappingsForStudies);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void executeMapping(NGramMatchingModel matchingModel, String eachQuery, MappingList mappingList,
			Map<Integer, List<Set<String>>> measurementMap) throws Exception
	{
		Set<String> tokens = matchingModel.createNGrams(eachQuery.toLowerCase().trim(), true);

		for (Integer featureID : measurementMap.keySet())
		{
			double highScore = 0;

			for (Set<String> eachNGrams : measurementMap.get(featureID))
			{
				double similarity = matchingModel.calculateScore(eachNGrams, tokens);

				if (highScore < similarity) highScore = similarity;
			}

			mappingList.add(eachQuery, featureID, highScore);
		}
	}
}
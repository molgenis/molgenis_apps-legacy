package plugins.harmonization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import uk.ac.ebi.ontocat.OntologyService;
import uk.ac.ebi.ontocat.OntologyService.SearchOptions;
import uk.ac.ebi.ontocat.OntologyServiceException;
import uk.ac.ebi.ontocat.bioportal.BioportalOntologyService;

public class TermExpansionJob implements Job
{
	@Override
	@SuppressWarnings("unchecked")
	public void execute(JobExecutionContext context) throws JobExecutionException
	{
		try
		{
			if (context.getJobDetail().getJobDataMap().get("predictors") instanceof List<?>)
			{
				List<PredictorInfo> predictors = (List<PredictorInfo>) context.getJobDetail().getJobDataMap()
						.get("predictors");

				HarmonizationModel model = (HarmonizationModel) context.getJobDetail().getJobDataMap().get("model");

				List<String> stopWords = (List<String>) context.getJobDetail().getJobDataMap().get("stopWords");

				int count = 0;

				OntologyService os = new BioportalOntologyService();

				for (PredictorInfo predictor : predictors)
				{
					predictor.getExpandedQuery().add(predictor.getLabel());

					if (predictor.getBuildingBlocks().size() > 0)
					{
						for (String eachBlock : predictor.getBuildingBlocks())
						{
							predictor.getExpandedQuery().addAll(
									expandQueryByDefinedBlocks(eachBlock.split(","), stopWords, model, os));
						}
					}
					else
					{
						predictor.getExpandedQuery().addAll(
								expandByPotentialBuildingBlocks(predictor.getLabel(), stopWords, model, os));
					}

					predictor.setExpandedQuery(uniqueList(predictor.getExpandedQuery()));

					model.setTotalNumber(model.getTotalNumber() + predictor.getExpandedQuery().size());

					count++;

					model.setFinishedJobs(model.getFinishedJobs() + 1);

					System.out.println("Finished: " + count + " out of " + predictors.size());
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public List<String> expandByPotentialBuildingBlocks(String predictorLabel, List<String> stopWords,
			HarmonizationModel model, OntologyService os) throws OntologyServiceException
	{
		List<String> expandedQueries = new ArrayList<String>();

		ArrayList<List<String>> potentialBlocks = Terms.getTermsLists(Arrays.asList(predictorLabel.split(" ")));

		HashMap<String, List<String>> mapForBlocks = new HashMap<String, List<String>>();

		boolean possibleBlocks = false;

		for (List<String> eachSetOfBlocks : potentialBlocks)
		{
			for (String eachBlock : eachSetOfBlocks)
			{
				if (!stopWords.contains(eachBlock.toLowerCase()))
				{
					mapForBlocks.put(eachBlock, collectInfoFromOntology(eachBlock.toLowerCase().trim(), model, os));

					if (mapForBlocks.get(eachBlock).size() > 1)
					{
						possibleBlocks = true;
					}

					if (!mapForBlocks.get(eachBlock).contains(eachBlock.toLowerCase().trim()))
					{
						mapForBlocks.get(eachBlock).add(eachBlock.toLowerCase().trim());
					}
				}
			}

			if (possibleBlocks == true)
			{
				List<String> combinedList = mapForBlocks.get(eachSetOfBlocks.get(0));

				if (eachSetOfBlocks.size() > 1)
				{
					for (int i = 1; i < eachSetOfBlocks.size(); i++)
					{
						if (mapForBlocks.containsKey(eachSetOfBlocks.get(i)))
						{
							combinedList = combineLists(combinedList, mapForBlocks.get(eachSetOfBlocks.get(i)));
						}
					}
				}
				expandedQueries.addAll(combinedList);
			}

			mapForBlocks.clear();

			possibleBlocks = false;

		}
		return expandedQueries;
	}

	public List<String> expandQueryByDefinedBlocks(String[] buildingBlocksArray, List<String> stopWords,
			HarmonizationModel model, OntologyService os) throws OntologyServiceException
	{
		List<String> expandedQueries = new ArrayList<String>();

		HashMap<String, List<String>> mapForBlocks = new HashMap<String, List<String>>();

		List<String> buildingBlocks = new ArrayList<String>(Arrays.asList(buildingBlocksArray));

		for (String eachBlock : buildingBlocks)
		{
			if (!stopWords.contains(eachBlock.toLowerCase()))
			{
				mapForBlocks.put(eachBlock, collectInfoFromOntology(eachBlock.toLowerCase().trim(), model, os));

				if (!mapForBlocks.get(eachBlock).contains(eachBlock.toLowerCase().trim()))
				{
					mapForBlocks.get(eachBlock).add(eachBlock.toLowerCase().trim());
				}
			}
		}

		String previousBlock = buildingBlocksArray[0];

		List<String> combinedList = mapForBlocks.get(previousBlock);

		if (buildingBlocksArray.length > 1)
		{
			for (int j = 1; j < buildingBlocksArray.length; j++)
			{
				String nextBlock = buildingBlocksArray[j];

				if (mapForBlocks.containsKey(nextBlock))
				{
					combinedList = combineLists(combinedList, mapForBlocks.get(nextBlock));
				}
			}
		}

		expandedQueries.addAll(combinedList);

		return uniqueList(expandedQueries);
	}

	public List<String> collectInfoFromOntology(String queryToExpand, HarmonizationModel model, OntologyService os)
			throws OntologyServiceException
	{
		List<String> expandedQueries = new ArrayList<String>();

		for (uk.ac.ebi.ontocat.OntologyTerm ot : os.searchAll(queryToExpand, SearchOptions.EXACT))
		{
			if (model.getOntologyAccessions().contains(ot.getOntologyAccession()))
			{
				if (ot.getLabel() != null)
				{
					expandedQueries.add(ot.getLabel());

					for (String synonym : os.getSynonyms(ot))
					{
						expandedQueries.add(synonym);
					}
					try
					{
						if (os.getChildren(ot) != null && os.getChildren(ot).size() > 0)
						{
							for (uk.ac.ebi.ontocat.OntologyTerm childOt : os.getChildren(ot))
							{
								expandedQueries.add(childOt.getLabel());
							}
						}
					}
					catch (Exception e)
					{
						System.out.println(ot.getLabel() + " does not have children!");
					}
				}
			}
		}

		return uniqueList(expandedQueries);
	}

	public List<String> uniqueList(List<String> uncleanedList)
	{
		List<String> uniqueList = new ArrayList<String>();

		for (String eachString : uncleanedList)
		{
			if (!uniqueList.contains(eachString.toLowerCase().trim()))
			{
				uniqueList.add(eachString.toLowerCase().trim());
			}
		}

		return uniqueList;
	}

	public List<String> combineLists(List<String> listOne, List<String> listTwo)
	{
		List<String> combinedList = new ArrayList<String>();

		StringBuilder combinedString = new StringBuilder();

		for (String first : listOne)
		{
			for (String second : listTwo)
			{
				combinedString.delete(0, combinedString.length());

				combinedString.append(first).append(" ").append(second);

				if (!combinedList.contains(combinedString.toString()))
				{
					combinedList.add(combinedString.toString());
				}
			}
		}

		return combinedList;
	}
}
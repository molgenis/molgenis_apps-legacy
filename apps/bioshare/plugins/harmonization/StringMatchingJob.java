package plugins.harmonization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.molgenis.pheno.Measurement;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import plugins.HarmonizationComponent.LevenshteinDistanceModel;
import plugins.HarmonizationComponent.MappingList;
import uk.ac.ebi.ontocat.OntologyService.SearchOptions;
import uk.ac.ebi.ontocat.OntologyServiceException;
import uk.ac.ebi.ontocat.bioportal.BioportalOntologyService;

public class StringMatchingJob implements Job
{
	@SuppressWarnings("unchecked")
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException
	{
		try
		{
			PredictorInfo predictor = (PredictorInfo) context.getJobDetail().getJobDataMap().get("predictor");

			BioportalOntologyService os = new BioportalOntologyService();

			List<Measurement> measurements = (List<Measurement>) context.getJobDetail().getJobDataMap()
					.get("measurements");

			HarmonizationModel model = (HarmonizationModel) context.getJobDetail().getJobDataMap().get("model");

			LevenshteinDistanceModel matchingModel = (LevenshteinDistanceModel) context.getJobDetail().getJobDataMap()
					.get("matchingModel");

			MappingList mappings = new MappingList();

			for (String eachBlock : predictor.getBuildingBlocks())
			{
				predictor.getExpandedQuery().addAll(expandQueryByDefinedBlocks(eachBlock.split(","), model, os));
			}

			if (!predictor.getExpandedQuery().contains(predictor.getLabel()))
			{
				predictor.getExpandedQuery().add(predictor.getLabel());
			}

			predictor.getExpandedQuery().addAll(expandByPotentialBuildingBlocks(predictor.getLabel(), model, os));

			predictor.setExpandedQuery(uniqueList(predictor.getExpandedQuery()));

			model.setTotalNumber(model.getTotalNumber() + predictor.getExpandedQuery().size());

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

	private List<String> expandByPotentialBuildingBlocks(String predictorLabel, HarmonizationModel model,
			BioportalOntologyService os) throws OntologyServiceException
	{
		List<String> expandedQueries = new ArrayList<String>();

		ArrayList<List<String>> potentialBlocks = Terms.getTermsLists(Arrays.asList(predictorLabel.split(" ")));

		HashMap<String, List<String>> mapForBlocks = new HashMap<String, List<String>>();

		boolean possibleBlocks = false;

		for (List<String> eachSetOfBlocks : potentialBlocks)
		{
			for (String eachBlock : eachSetOfBlocks)
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

			if (possibleBlocks == true)
			{
				List<String> combinedList = mapForBlocks.get(eachSetOfBlocks.get(0));

				if (eachSetOfBlocks.size() > 1)
				{
					for (int i = 1; i < eachSetOfBlocks.size(); i++)
					{
						combinedList = combineLists(combinedList, mapForBlocks.get(eachSetOfBlocks.get(i)));
					}
				}
				expandedQueries.addAll(combinedList);
			}

			mapForBlocks.clear();

			possibleBlocks = false;

		}
		return expandedQueries;
	}

	private List<String> expandQueryByDefinedBlocks(String[] buildingBlocksArray, HarmonizationModel model,
			BioportalOntologyService os) throws OntologyServiceException
	{
		List<String> expandedQueries = new ArrayList<String>();

		HashMap<String, List<String>> mapForBlocks = new HashMap<String, List<String>>();

		List<String> buildingBlocks = new ArrayList<String>(Arrays.asList(buildingBlocksArray));

		for (String eachBlock : buildingBlocks)
		{
			mapForBlocks.put(eachBlock, collectInfoFromOntology(eachBlock.toLowerCase().trim(), model, os));

			if (!mapForBlocks.get(eachBlock).contains(eachBlock.toLowerCase().trim()))
			{
				mapForBlocks.get(eachBlock).add(eachBlock.toLowerCase().trim());
			}
		}

		String previousBlock = buildingBlocksArray[0];

		List<String> combinedList = mapForBlocks.get(previousBlock);

		if (buildingBlocksArray.length > 1)
		{
			for (int j = 1; j < buildingBlocksArray.length; j++)
			{
				String nextBlock = buildingBlocksArray[j];

				combinedList = combineLists(combinedList, mapForBlocks.get(nextBlock));
			}
		}

		expandedQueries.addAll(combinedList);

		return uniqueList(expandedQueries);
	}

	public List<String> collectInfoFromOntology(String queryToExpand, HarmonizationModel model,
			BioportalOntologyService os) throws OntologyServiceException
	{
		List<String> expandedQueries = new ArrayList<String>();

		for (uk.ac.ebi.ontocat.OntologyTerm ot : os.searchAll(queryToExpand, SearchOptions.EXACT))
		{
			if (model.getOntologyAccessions().contains(ot.getOntologyAccession()))
			{
				expandedQueries.add(ot.getLabel());

				for (String synonym : os.getSynonyms(ot))
				{
					expandedQueries.add(synonym);
				}

				try
				{
					for (uk.ac.ebi.ontocat.OntologyTerm childOt : os.getChildren(ot))
					{
						expandedQueries.add(childOt.getLabel());

						// for (String synonymChild : os.getSynonyms(childOt))
						// {
						// expandedQueries.add(synonymChild);
						// }
					}
				}
				catch (Exception e)
				{
					System.out.println("The ontology term " + ot.getLabel() + " doesn not have any children!");
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
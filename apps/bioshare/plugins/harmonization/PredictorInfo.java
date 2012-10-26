package plugins.harmonization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import plugins.HarmonizationComponent.LinkedInformation;
import plugins.HarmonizationComponent.MappingList;

public class PredictorInfo
{
	private String name = null;
	private String label = null;
	private List<String> buildingBlocks = new ArrayList<String>();
	private List<String> expandedQuery = new ArrayList<String>();
	private List<String> finalMappings = new ArrayList<String>();
	private HashMap<String, String> category = new HashMap<String, String>();
	private HashMap<String, String> description = new HashMap<String, String>();
	private HashMap<String, Double> similarity = new HashMap<String, Double>();
	private LinkedHashMap<String, List<String>> expandedQueryPerMapping = new LinkedHashMap<String, List<String>>();
	private MappingList mappings = new MappingList();

	public PredictorInfo(String name)
	{
		this.name = name;
	}

	public PredictorInfo(String name, List<String> buildingBlocks)
	{
		this.name = name;

		if (buildingBlocks != null)
		{
			this.buildingBlocks = buildingBlocks;
		}
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void setLabel(String label)
	{
		this.label = label;
	}

	public void setBuildingBlocks(List<String> buildingBlocks)
	{
		this.buildingBlocks = buildingBlocks;
	}

	public void setBuildingBlocks(String... buildingBlocks)
	{
		this.buildingBlocks.clear();

		for (String eachBlock : buildingBlocks)
		{
			this.buildingBlocks.add(eachBlock.trim());
		}
	}

	public void setExpandedQuery(List<String> expandedQuery)
	{
		this.expandedQuery = expandedQuery;
	}

	public void setCategory(HashMap<String, String> category)
	{
		this.category = category;
	}

	public void setFinalMappings(List<String> finalMappings)
	{
		this.finalMappings = finalMappings;
	}

	public void setMappings(MappingList mappings)
	{
		this.mappings = mappings;

		List<LinkedInformation> allMappings = mappings.getSortedInformation();

		for (int i = allMappings.size(); i > 0; i--)
		{
			LinkedInformation eachRow = allMappings.get(i - 1);
			String expandedQuery = eachRow.expandedQuery;
			String matchedItem = eachRow.matchedItem;
			Double similarity = eachRow.similarity;
			String measurementName = eachRow.measurementName;

			if (!this.similarity.containsKey(expandedQuery))
			{
				this.similarity.put(expandedQuery, similarity);
			}

			if (!this.description.containsKey(measurementName))
			{
				this.description.put(measurementName, matchedItem);
			}

			List<String> temp = null;

			if (this.expandedQueryPerMapping.containsKey(measurementName))
			{
				temp = this.expandedQueryPerMapping.get(measurementName);
				temp.add(expandedQuery);
			}
			else
			{
				temp = new ArrayList<String>();
				temp.add(expandedQuery);
			}
			this.expandedQueryPerMapping.put(measurementName, temp);
		}
	}

	public String getName()
	{
		return name;
	}

	public List<String> getBuildingBlocks()
	{
		return buildingBlocks;
	}

	public MappingList getMappings()
	{
		return mappings;
	}

	public List<String> getExpandedQuery()
	{
		return expandedQuery;
	}

	public List<String> getFinalMappings()
	{
		return finalMappings;
	}

	public String getLabel()
	{
		return label;
	}

	public HashMap<String, String> getCategory()
	{
		return category;
	}

	public List<String> getMappedVariables()
	{
		return new ArrayList<String>(expandedQueryPerMapping.keySet());
	}

	public List<String> getExpandedQueryForOneMapping(String measurementName)
	{

		if (expandedQueryPerMapping.containsKey(measurementName)) return expandedQueryPerMapping.get(measurementName);
		else
			return null;
	}

	public String getDescription(String measurementName)
	{
		if (description.containsKey(measurementName)) return description.get(measurementName);
		else
			return null;
	}

	public Double getSimilarity(String expandedQuery)
	{
		if (similarity.containsKey(expandedQuery)) return similarity.get(expandedQuery);
		else
			return null;
	}
}
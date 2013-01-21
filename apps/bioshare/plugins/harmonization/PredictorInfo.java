package plugins.harmonization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.molgenis.pheno.Measurement;

import plugins.HarmonizationComponent.LinkedInformation;
import plugins.HarmonizationComponent.MappingList;

public class PredictorInfo
{
	private String name = null;
	private String label = null;
	private String identifier = null;
	private List<String> buildingBlocks = new ArrayList<String>();
	private List<String> expandedQuery = new ArrayList<String>();
	private Map<String, Measurement> finalMappings = new HashMap<String, Measurement>();
	private Map<String, String> category = new HashMap<String, String>();
	private Map<String, String> description = new HashMap<String, String>();
	private Map<String, Double> similarity = new HashMap<String, Double>();
	private Map<String, List<String>> expandedQueryPerMapping = new LinkedHashMap<String, List<String>>();
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

	public void setIdentifier(String identifier)
	{
		this.identifier = identifier;
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

	public void setFinalMappings(HashMap<String, Measurement> finalMaapings)
	{
		this.finalMappings = finalMaapings;
	}

	public void addFinalMappings(List<Measurement> finalMappings)
	{
		for (Measurement m : finalMappings)
		{
			if (!this.finalMappings.containsKey(m.getName()))
			{
				this.finalMappings.put(m.getName(), m);
			}
		}
	}

	public void setDescription(String name, String measurementDescription)
	{
		if (!description.containsKey(name))
		{
			description.put(name, measurementDescription);
		}
	}

	public void setMappings(MappingList mappings)
	{
		this.mappings = mappings;

		List<LinkedInformation> allMappings = mappings.getSortedInformation();

		for (int i = allMappings.size(); i > 0; i--)
		{
			LinkedInformation eachRow = allMappings.get(i - 1);
			String expandedQuery = eachRow.getExpandedQuery();
			String matchedItem = eachRow.getMatchedItem();
			double similarity = eachRow.getSimilarity();
			String measurementName = eachRow.getMeasurementName();
			StringBuilder expandedQueryIdentifier = new StringBuilder();
			expandedQueryIdentifier.append(expandedQuery).append("_").append(measurementName);

			if (!this.similarity.containsKey(expandedQueryIdentifier.toString()))
			{
				this.similarity.put(expandedQueryIdentifier.toString(), similarity);
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

	public Map<String, Measurement> getFinalMappings()
	{
		return finalMappings;
	}

	public String getLabel()
	{
		return label;
	}

	public Map<String, String> getCategory()
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

	public String getIdentifier()
	{
		return identifier;
	}
}
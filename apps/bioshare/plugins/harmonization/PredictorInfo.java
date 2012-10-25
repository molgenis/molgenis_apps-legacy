package plugins.harmonization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PredictorInfo
{
	private String name = null;
	private String label = null;
	private List<String> buildingBlocks = new ArrayList<String>();
	private List<String> expandedQuery = new ArrayList<String>();
	private List<String> finalMappings = new ArrayList<String>();
	private HashMap<String, String> category = new HashMap<String, String>();
	private HashMap<String, String> expandedQueryMappings = new HashMap<String, String>();

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

	public String getName()
	{
		return name;
	}

	public List<String> getBuildingBlocks()
	{
		return buildingBlocks;
	}

	public HashMap<String, String> getMappings()
	{
		return expandedQueryMappings;
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
}
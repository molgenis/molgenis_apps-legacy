package plugins.harmonization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.molgenis.pheno.Measurement;

import plugins.HarmonizationComponent.LinkedInformation;
import plugins.HarmonizationComponent.MappingList;

public class PredictorInfo
{
	private Integer id = null;
	private String name = null;
	private String label = null;
	private String identifier = null;
	private List<String> buildingBlocks = new ArrayList<String>();
	private List<String> expandedQuery = new ArrayList<String>();
	private Map<String, Measurement> finalMappings = new HashMap<String, Measurement>();
	// private Map<String, String> category = new HashMap<String, String>();
	// private Map<String, String> description = new HashMap<String, String>();
	private Map<SimilarityScore, MeasurementSimilarity> similarity = new HashMap<SimilarityScore, MeasurementSimilarity>();
	private Map<MeasurementPerStudy, MeasurementExpandedQuery> expandedQueryPerMapping = new LinkedHashMap<MeasurementPerStudy, MeasurementExpandedQuery>();
	private Map<String, MappingList> mappingsForStudies = new HashMap<String, MappingList>();

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

	// public void setCategory(HashMap<String, String> category)
	// {
	// this.category = category;
	// }

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

	// public void setDescription(String name, String measurementDescription)
	// {
	// if (!description.containsKey(name))
	// {
	// description.put(name, measurementDescription);
	// }
	// }

	public void setMappings(Map<String, MappingList> mappingsForStudies)
	{
		this.mappingsForStudies = mappingsForStudies;

		for (Entry<String, MappingList> entry : mappingsForStudies.entrySet())
		{
			String investigationName = entry.getKey();

			MappingList mappings = entry.getValue();

			List<LinkedInformation> allMappings = mappings.getSortedInformation();

			for (int i = allMappings.size(); i > 0; i--)
			{
				LinkedInformation eachRow = allMappings.get(i - 1);

				String expandedQuery = eachRow.getExpandedQuery();

				Measurement measurement = eachRow.getMeasurement();

				double similarity = eachRow.getSimilarity();

				StringBuilder expandedQueryIdentifier = new StringBuilder();

				expandedQueryIdentifier.append(investigationName).append(expandedQuery).append("_")
						.append(measurement.getName());

				SimilarityScore similarityScore = new SimilarityScore(measurement.getName(),
						measurement.getInvestigation_Name(), expandedQuery);

				if (!this.similarity.containsKey(similarityScore))
				{
					this.similarity.put(similarityScore, new MeasurementSimilarity(measurement, similarity));
				}

				MeasurementPerStudy measurementStudy = new MeasurementPerStudy(measurement.getName(), investigationName);

				MeasurementExpandedQuery temp = null;

				if (this.expandedQueryPerMapping.containsKey(measurementStudy))
				{
					temp = this.expandedQueryPerMapping.get(measurementStudy);
				}
				else
				{
					temp = new MeasurementExpandedQuery(measurement);
				}

				temp.addExpandeQueries(expandedQuery);

				this.expandedQueryPerMapping.put(measurementStudy, temp);
			}
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

	public Map<String, MappingList> getMappings()
	{
		return mappingsForStudies;
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

	// public Map<String, String> getCategory()
	// {
	// return category;
	// }

	public boolean hasMappingResult()
	{

		if (expandedQueryPerMapping.keySet().size() > 0) return true;
		else
			return false;

	}

	public Map<String, List<Measurement>> getMappedVariables()
	{
		Map<String, List<Measurement>> variablesOfStudies = new HashMap<String, List<Measurement>>();

		for (MeasurementExpandedQuery measurementQuery : expandedQueryPerMapping.values())
		{
			Measurement m = measurementQuery.getMeasurement();

			List<Measurement> listOfVariables = null;

			if (variablesOfStudies.containsKey(m.getInvestigation_Name()))
			{
				listOfVariables = variablesOfStudies.get(m.getInvestigation_Name());
			}
			else
			{
				listOfVariables = new ArrayList<Measurement>();
			}

			listOfVariables.add(m);

			variablesOfStudies.put(m.getInvestigation_Name(), listOfVariables);
		}

		return variablesOfStudies;
	}

	public List<String> getExpandedQueryForOneMapping(String measurementName, String investigationName)
	{

		MeasurementPerStudy measurement = new MeasurementPerStudy(measurementName, investigationName);

		if (expandedQueryPerMapping.containsKey(measurement))
		{
			System.out.println(measurementName);
			return expandedQueryPerMapping.get(measurement).getExpandeQueries();
		}
		else
			return null;
	}

	// public String getDescription(String measurementName)
	// {
	// if (description.containsKey(measurementName)) return
	// description.get(measurementName);
	// else
	// return null;
	// }
	//
	public Double getSimilarity(String measurementName, String expandedQuery, String investigationName)
	{
		SimilarityScore mea = new SimilarityScore(measurementName, investigationName, expandedQuery);

		if (similarity.containsKey(mea)) return similarity.get(mea).getSimilarity();
		else
			return null;
	}

	public String getIdentifier()
	{
		return identifier;
	}

	public Integer getId()
	{
		return id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}

	private static class MeasurementSimilarity
	{
		private final Measurement measurement;
		private final Double similarity;

		public MeasurementSimilarity(Measurement measurement, Double similarity)
		{
			this.measurement = measurement;
			this.similarity = similarity;
		}

		public Measurement getMeasurement()
		{
			return measurement;
		}

		public Double getSimilarity()
		{
			return similarity;
		}
	}

	private static class MeasurementExpandedQuery
	{

		private final Measurement measurement;
		private List<String> expandeQueries;

		public MeasurementExpandedQuery(Measurement measurement)
		{
			this.measurement = measurement;
			expandeQueries = new ArrayList<String>();
		}

		public List<String> getExpandeQueries()
		{
			return expandeQueries;
		}

		public void addExpandeQueries(String query)
		{
			this.expandeQueries.add(query);
		}

		public Measurement getMeasurement()
		{
			return measurement;
		}

	}

	public static class MeasurementPerStudy
	{
		private final String investigationName;
		private final String measurementName;

		public MeasurementPerStudy(String measurementName, String investigationName)
		{
			this.investigationName = investigationName;
			this.measurementName = measurementName;
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + ((investigationName == null) ? 0 : investigationName.hashCode());
			result = prime * result + ((measurementName == null) ? 0 : measurementName.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			MeasurementPerStudy other = (MeasurementPerStudy) obj;
			if (investigationName == null)
			{
				if (other.investigationName != null) return false;
			}
			else if (!investigationName.equals(other.investigationName)) return false;
			if (measurementName == null)
			{
				if (other.measurementName != null) return false;
			}
			else if (!measurementName.equals(other.measurementName)) return false;
			return true;
		}
	}

	public static class SimilarityScore
	{
		private final String investigationName;
		private final String measurementName;
		private final String expandedQuery;

		public SimilarityScore(String measurementName, String investigationName, String expandedQuery)
		{
			this.expandedQuery = expandedQuery;
			this.investigationName = investigationName;
			this.measurementName = measurementName;
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + ((expandedQuery == null) ? 0 : expandedQuery.hashCode());
			result = prime * result + ((investigationName == null) ? 0 : investigationName.hashCode());
			result = prime * result + ((measurementName == null) ? 0 : measurementName.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			SimilarityScore other = (SimilarityScore) obj;
			if (expandedQuery == null)
			{
				if (other.expandedQuery != null) return false;
			}
			else if (!expandedQuery.equals(other.expandedQuery)) return false;
			if (investigationName == null)
			{
				if (other.investigationName != null) return false;
			}
			else if (!investigationName.equals(other.investigationName)) return false;
			if (measurementName == null)
			{
				if (other.measurementName != null) return false;
			}
			else if (!measurementName.equals(other.measurementName)) return false;
			return true;
		}
	}
}
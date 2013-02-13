package plugins.HarmonizationComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.molgenis.pheno.Measurement;

public class MappingList
{
	private Map<ExpandedQueryObject, Double> uniqueElements = new HashMap<ExpandedQueryObject, Double>();

	// private Map<ExpandedQueryObject, LinkedInformation> uniqueElements = new
	// HashMap<ExpandedQueryObject, LinkedInformation>();

	public void add(String expandedQuery, Measurement measurement, double similarity) throws Exception
	{
		if (expandedQuery == null || measurement == null) throw new Exception("Parameters have to be not null!");

		if (expandedQuery.isEmpty()) throw new Exception("Parameters have to be not empty");

		ExpandedQueryObject uniqueName = new ExpandedQueryObject(expandedQuery, measurement);

		if (uniqueElements.containsKey(uniqueName))
		{
			if (similarity > uniqueElements.get(uniqueName))
			{
				uniqueElements.put(uniqueName, similarity);
			}
		}
		else
		{
			// LinkedInformation inf = new LinkedInformation(expandedQuery,
			// matchedItem, similarity, measurementName);

			uniqueElements.put(uniqueName, similarity);
		}
	}

	public List<LinkedInformation> getSortedInformation()
	{
		List<LinkedInformation> sortedLinks = new ArrayList<LinkedInformation>(uniqueElements.size());

		for (Map.Entry<ExpandedQueryObject, Double> entry : uniqueElements.entrySet())
		{
			sortedLinks.add(new LinkedInformation(entry.getKey().getExpandedQuery(), entry.getKey().getMeasurement(),
					entry.getValue()));
		}

		Collections.sort(sortedLinks);

		return (sortedLinks.size() > 100 ? sortedLinks.subList(sortedLinks.size() - 100, sortedLinks.size())
				: sortedLinks);
	}

	// private static class SimilarityRecord
	// {
	// private final String matchedItem;
	// private double similarity;
	//
	// public SimilarityRecord(String matchedItem, double similarity)
	// {
	// this.matchedItem = matchedItem;
	// this.setSimilarity(similarity);
	// }
	//
	// public double getSimilarity()
	// {
	// return similarity;
	// }
	//
	// public void setSimilarity(double similarity)
	// {
	// this.similarity = similarity;
	// }
	// }

	private static class ExpandedQueryObject
	{
		private final String expandedQuery;
		private final Measurement measurement;

		public ExpandedQueryObject(String expandedQuery, Measurement measurement)
		{
			this.expandedQuery = expandedQuery;
			this.measurement = measurement;
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + ((expandedQuery == null) ? 0 : expandedQuery.hashCode());
			result = prime * result + ((measurement == null) ? 0 : measurement.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			ExpandedQueryObject other = (ExpandedQueryObject) obj;
			if (expandedQuery == null)
			{
				if (other.expandedQuery != null) return false;
			}
			else if (!expandedQuery.equals(other.expandedQuery)) return false;
			if (measurement == null)
			{
				if (other.measurement != null) return false;
			}
			else if (!measurement.equals(other.measurement)) return false;
			return true;
		}

		public String getExpandedQuery()
		{
			return expandedQuery;
		}

		public Measurement getMeasurement()
		{
			return measurement;
		}
	}
}
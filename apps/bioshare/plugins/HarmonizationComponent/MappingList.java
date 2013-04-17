package plugins.HarmonizationComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MappingList
{
	private Map<ExpandedQueryObject, Double> uniqueElements = new HashMap<ExpandedQueryObject, Double>();

	// private Map<ExpandedQueryObject, LinkedInformation> uniqueElements = new
	// HashMap<ExpandedQueryObject, LinkedInformation>();

	public void add(String expandedQuery, Integer featureID, double similarity) throws Exception
	{
		if (expandedQuery == null || featureID == null) throw new Exception("Parameters have to be not null!");

		if (expandedQuery.isEmpty()) throw new Exception("Parameters have to be not empty");

		ExpandedQueryObject uniqueName = new ExpandedQueryObject(expandedQuery, featureID);

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
			sortedLinks.add(new LinkedInformation(entry.getKey().getExpandedQuery(), entry.getKey().getFeatureID(),
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
		private final Integer featureID;

		public ExpandedQueryObject(String expandedQuery, Integer featureID)
		{
			this.expandedQuery = expandedQuery;
			this.featureID = featureID;
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + ((expandedQuery == null) ? 0 : expandedQuery.hashCode());
			result = prime * result + ((featureID == null) ? 0 : featureID.hashCode());
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
			if (featureID == null)
			{
				if (other.featureID != null) return false;
			}
			else if (!featureID.equals(other.featureID)) return false;
			return true;
		}

		public String getExpandedQuery()
		{
			return expandedQuery;
		}

		public Integer getFeatureID()
		{
			return featureID;
		}
	}
}
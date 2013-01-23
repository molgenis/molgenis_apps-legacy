package plugins.HarmonizationComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MappingList
{
	private Map<ExpandedQueryObject, SimilarityRecord> uniqueElements = new HashMap<ExpandedQueryObject, SimilarityRecord>();

	// private Map<ExpandedQueryObject, LinkedInformation> uniqueElements = new
	// HashMap<ExpandedQueryObject, LinkedInformation>();

	public void add(String expandedQuery, String matchedItem, double similarity, String measurementName)
			throws Exception
	{
		if (expandedQuery == null || matchedItem == null || measurementName == null) throw new Exception(
				"Parameters have to be not null!");

		if (expandedQuery.isEmpty() || matchedItem.isEmpty()) throw new Exception("Parameters have to be not empty");

		ExpandedQueryObject uniqueName = new ExpandedQueryObject(expandedQuery, measurementName);

		if (uniqueElements.containsKey(uniqueName))
		{
			if (similarity > uniqueElements.get(uniqueName).getSimilarity())
			{
				uniqueElements.get(uniqueName).setSimilarity(similarity);
			}
		}
		else
		{
			// LinkedInformation inf = new LinkedInformation(expandedQuery,
			// matchedItem, similarity, measurementName);

			uniqueElements.put(uniqueName, new SimilarityRecord(matchedItem, similarity));
		}
	}

	public List<LinkedInformation> getSortedInformation()
	{
		List<LinkedInformation> sortedLinks = new ArrayList<LinkedInformation>(uniqueElements.size());

		for (Map.Entry<ExpandedQueryObject, SimilarityRecord> entry : uniqueElements.entrySet())
		{
			sortedLinks.add(new LinkedInformation(entry.getKey().getExpandedQuery(), entry.getValue().getMatchedItem(),
					entry.getValue().getSimilarity(), entry.getKey().getMeasurementName()));
		}

		Collections.sort(sortedLinks);

		return (sortedLinks.size() > 100 ? sortedLinks.subList(sortedLinks.size() - 100, sortedLinks.size())
				: sortedLinks);
	}

	private static class SimilarityRecord
	{
		private final String matchedItem;
		private double similarity;

		public SimilarityRecord(String matchedItem, double similarity)
		{
			this.matchedItem = matchedItem;
			this.setSimilarity(similarity);
		}

		public double getSimilarity()
		{
			return similarity;
		}

		public void setSimilarity(double similarity)
		{
			this.similarity = similarity;
		}

		public String getMatchedItem()
		{
			return matchedItem;
		}
	}

	private static class ExpandedQueryObject
	{
		private final String expandedQuery;
		private final String measurementName;

		public ExpandedQueryObject(String expandedQuery, String measurementName)
		{
			this.expandedQuery = expandedQuery;
			this.measurementName = measurementName;
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + ((getExpandedQuery() == null) ? 0 : getExpandedQuery().hashCode());
			result = prime * result + ((getMeasurementName() == null) ? 0 : getMeasurementName().hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			ExpandedQueryObject other = (ExpandedQueryObject) obj;
			if (getExpandedQuery() == null)
			{
				if (other.getExpandedQuery() != null) return false;
			}
			else if (!getExpandedQuery().equals(other.getExpandedQuery())) return false;
			if (getMeasurementName() == null)
			{
				if (other.getMeasurementName() != null) return false;
			}
			else if (!getMeasurementName().equals(other.getMeasurementName())) return false;
			return true;
		}

		public String getExpandedQuery()
		{
			return expandedQuery;
		}

		public String getMeasurementName()
		{
			return measurementName;
		}
	}
}
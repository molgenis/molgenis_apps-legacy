package plugins.HarmonizationComponent;

public class LinkedInformation implements Comparable<LinkedInformation>
{
	private final String measurementName;

	private final String expandedQuery;

	private final String matchedItem;

	private double similarity;

	public LinkedInformation(String expandedQuery, String matchedItem, double similarity, String measurementName)
	{

		if (expandedQuery == null || matchedItem == null) throw new IllegalArgumentException(
				"Parameters have to be not null!");

		if (expandedQuery.isEmpty() || matchedItem.isEmpty()) throw new IllegalArgumentException(
				"Parameters have to be not empty");

		this.expandedQuery = expandedQuery;
		this.matchedItem = matchedItem;
		this.setSimilarity(similarity);
		this.measurementName = measurementName;
	}

	@Override
	public int compareTo(LinkedInformation o)
	{
		return Double.compare(this.getSimilarity(), o.getSimilarity());
	}

	public String getMeasurementName()
	{
		return measurementName;
	}

	public String getName()
	{
		return expandedQuery + matchedItem;
	}

	public String getExpandedQuery()
	{
		return expandedQuery;
	}

	public String getMatchedItem()
	{
		return matchedItem;
	}

	public Double getSimilarity()
	{
		return similarity;
	}

	public void setSimilarity(Double similarity)
	{
		this.similarity = similarity;
	}

}
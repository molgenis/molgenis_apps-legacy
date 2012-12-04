package org.molgenis.mutation.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class LovdDTO implements Serializable
{
	/* The serial version UID of this class. Needed for serialization. */
	private static final long serialVersionUID = 5236957271806864053L;

	private List<Map<String, String>> geneMap;
	private List<Map<String, String>> transcriptMap;
	private List<Map<String, String>> diseaseMap;
	private List<Map<String, String>> individualMap;
	private List<Map<String, String>> individualDiseaseMap;
	private List<Map<String, String>> phenotypeMap;
	private List<Map<String, String>> screeningMap;
	private List<Map<String, String>> screeningGeneMap;
	private List<Map<String, String>> variantGenomeMap;
	private List<Map<String, String>> variantTranscriptMap;
	private List<Map<String, String>> screeningVariantMap;

	public List<Map<String, String>> getGeneMap()
	{
		return geneMap;
	}

	public void setGeneMap(List<Map<String, String>> geneMap)
	{
		this.geneMap = geneMap;
	}

	public List<Map<String, String>> getTranscriptMap()
	{
		return transcriptMap;
	}

	public void setTranscriptMap(List<Map<String, String>> transcriptMap)
	{
		this.transcriptMap = transcriptMap;
	}

	public List<Map<String, String>> getDiseaseMap()
	{
		return diseaseMap;
	}

	public void setDiseaseMap(List<Map<String, String>> diseaseMap)
	{
		this.diseaseMap = diseaseMap;
	}

	public List<Map<String, String>> getIndividualMap()
	{
		return individualMap;
	}

	public void setIndividualMap(List<Map<String, String>> individualMap)
	{
		this.individualMap = individualMap;
	}

	public List<Map<String, String>> getIndividualDiseaseMap()
	{
		return individualDiseaseMap;
	}

	public void setIndividualDiseaseMap(List<Map<String, String>> individualDiseaseMap)
	{
		this.individualDiseaseMap = individualDiseaseMap;
	}

	public List<Map<String, String>> getPhenotypeMap()
	{
		return phenotypeMap;
	}

	public void setPhenotypeMap(List<Map<String, String>> phenotypeMap)
	{
		this.phenotypeMap = phenotypeMap;
	}

	public List<Map<String, String>> getScreeningMap()
	{
		return screeningMap;
	}

	public void setScreeningMap(List<Map<String, String>> screeningMap)
	{
		this.screeningMap = screeningMap;
	}

	public List<Map<String, String>> getScreeningGeneMap()
	{
		return screeningGeneMap;
	}

	public void setScreeningGeneMap(List<Map<String, String>> screeningGeneMap)
	{
		this.screeningGeneMap = screeningGeneMap;
	}

	public List<Map<String, String>> getVariantGenomeMap()
	{
		return variantGenomeMap;
	}

	public void setVariantGenomeMap(List<Map<String, String>> variantGenomeMap)
	{
		this.variantGenomeMap = variantGenomeMap;
	}

	public List<Map<String, String>> getVariantTranscriptMap()
	{
		return variantTranscriptMap;
	}

	public void setVariantTranscriptMap(List<Map<String, String>> variantTranscriptMap)
	{
		this.variantTranscriptMap = variantTranscriptMap;
	}

	public List<Map<String, String>> getScreeningVariantMap()
	{
		return screeningVariantMap;
	}

	public void setScreeningVariantMap(List<Map<String, String>> screeningVariantMap)
	{
		this.screeningVariantMap = screeningVariantMap;
	}
}

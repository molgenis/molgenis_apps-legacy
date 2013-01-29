package org.molgenis.gonl.service;

import java.util.Collections;
import java.util.List;

import org.molgenis.variant.SequenceVariant;

public class VariantResponse
{
	private int nrSearchResults;
	private List<SequenceVariant> variants;

	public int getNrSearchResults()
	{
		return nrSearchResults;
	}

	public void setNrSearchResults(int nrSearchResults)
	{
		this.nrSearchResults = nrSearchResults;
	}

	public List<SequenceVariant> getVariants()
	{
		return variants != null ? variants : Collections.<SequenceVariant> emptyList();
	}

	public void setVariants(List<SequenceVariant> variants)
	{
		this.variants = variants;
	}
}

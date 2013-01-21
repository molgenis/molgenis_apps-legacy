package org.molgenis.gonl.service;

public class VariantRequest
{
	private String chromosome;
	private Integer startBp;
	private Integer endBp;

	public String getChromosome()
	{
		return chromosome;
	}

	public void setChromosome(String chromosome)
	{
		this.chromosome = chromosome;
	}

	public Integer getStartBp()
	{
		return startBp;
	}

	public void setStartBp(Integer startBp)
	{
		this.startBp = startBp;
	}

	public Integer getEndBp()
	{
		return endBp;
	}

	public void setEndBp(Integer endBp)
	{
		this.endBp = endBp;
	}
}

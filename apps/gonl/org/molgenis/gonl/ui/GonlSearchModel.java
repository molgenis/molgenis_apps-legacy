/*
 * Date: September 5, 2011 Template: EasyPluginModelGen.java.ftl generator:
 * org.molgenis.generators.ui.EasyPluginModelGen 4.0.0-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.gonl.ui;

import java.util.List;

import org.molgenis.framework.ui.EasyPluginModel;
import org.molgenis.gonl.service.VariantRequest;
import org.molgenis.gonl.utils.VariantAggregator.VariantAggregate;
import org.molgenis.variant.Chromosome;

public class GonlSearchModel extends EasyPluginModel
{
	private static final long serialVersionUID = 1L;

	private List<Chromosome> allChromosomes;
	private List<VariantRequest> variantRequests;
	private List<VariantAggregate> variantAggregates;

	public GonlSearchModel(GonlSearch controller)
	{
		super(controller);
	}

	public List<Chromosome> getAllChromosomes()
	{
		return allChromosomes;
	}

	public void setAllChromosomes(List<Chromosome> allChromosomes)
	{
		this.allChromosomes = allChromosomes;
	}

	public List<VariantRequest> getVariantRequests()
	{
		return variantRequests;
	}

	public void setVariantRequests(List<VariantRequest> variantRequests)
	{
		this.variantRequests = variantRequests;
	}

	public List<VariantAggregate> getVariantAggregates()
	{
		return variantAggregates;
	}

	public void setVariantAggregates(List<VariantAggregate> variantAggregates)
	{
		this.variantAggregates = variantAggregates;
	}
}

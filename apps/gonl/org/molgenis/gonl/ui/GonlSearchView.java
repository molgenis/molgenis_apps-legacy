package org.molgenis.gonl.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.HtmlInputException;
import org.molgenis.framework.ui.html.IntInput;
import org.molgenis.framework.ui.html.JQueryDataTable;
import org.molgenis.framework.ui.html.MolgenisForm;
import org.molgenis.framework.ui.html.Newline;
import org.molgenis.framework.ui.html.Paragraph;
import org.molgenis.framework.ui.html.SelectInput;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.variant.SequenceVariant;

public class GonlSearchView implements ScreenView
{
	GonlSearchModel model;

	public GonlSearchView(GonlSearchModel model)
	{
		this.model = model;
	}

	@Override
	public String render() throws HtmlInputException
	{
		MolgenisForm form = new MolgenisForm(this.model);

		form.add(new Paragraph(
				"Welcome to the first alpha release of GoNL variants. "
						+ "This release contains only SNPs and is based on 500 independent Dutch individuals (250 males + 250 females). "
						+ "Future releases will also contain other types of genetic variation and will be based on enhanced filtering using trio-aware variant calling on 250 trios. "
						+ "For more information visit <a href=\"http://www.nlgenome.nl\">www.nlgenome.nl</a>. "
						+ "Use GRCh37 / HG19 genomic coordinates to find matching SNPs."));

		form.add(new Newline());

		// provide a search box
		SelectInput chromosomes = new SelectInput("chromosome", model.getSelectedChrName());
		chromosomes.setOptions(model.getChromosomes(), model.getChromosomes());
		chromosomes.setNillable(false);
		form.add(chromosomes);

		// provide a 'from' box
		form.add(new IntInput("from", model.getSelectedFrom()));

		// provide a 'range' box
		form.add(new IntInput("to", model.getSelectedTo()));

		form.add(new ActionInput("search"));

		// provide a box with the current results
		form.add(new Newline());

		List<SequenceVariant> variants = model.getVariants();
		Map<String, List<ObservedValue>> alleleCountMap = model.getAlleleCounts();
		if (variants != null && alleleCountMap != null)
		{
			JQueryDataTable table = new JQueryDataTable("Result");
			table.addColumn("Panel");
			table.addColumn("Chr");
			table.addColumn("Pos");
			table.addColumn("EndBp");
			table.addColumn("Ref");
			table.addColumn("Alt");
			table.addColumn("HomRefCount");
			table.addColumn("HetCount");
			table.addColumn("HomAltCount");

			for (SequenceVariant variant : variants)
			{
				// aggregate individuals and panels
				List<String> individuals = new ArrayList<String>();
				AlleleCount aggregateCount = new AlleleCount();
				List<ObservedValue> variantValues = alleleCountMap.get(variant.getName());
				for (ObservedValue variantValue : variantValues)
				{
					aggregateCount.add(AlleleCount.parse(variantValue.getValue()));
					individuals.add(variantValue.getTarget_Name());
				}

				// create table row
				int row = table.addRow(variant.getName());
				int col = 0;
				table.setCell(col++, row, StringUtils.join(individuals, ','));
				table.setCell(col++, row, variant.getChr_Name());
				table.setCell(col++, row, variant.getStartBP());
				table.setCell(col++, row, variant.getEndBP());
				table.setCell(col++, row, variant.getRef());
				table.setCell(col++, row, variant.getAlt());
				table.setCell(col++, row, aggregateCount.getHomRefCount());
				table.setCell(col++, row, aggregateCount.getHetCount());
				table.setCell(col++, row, aggregateCount.getHomAltCount());
			}

			form.add(table);
		}

		return form.render();
	}

	private static class AlleleCount
	{
		private final AtomicInteger homRefCount;
		private final AtomicInteger hetCount;
		private final AtomicInteger homAltCount;

		private AlleleCount()
		{
			this(new AtomicInteger(0), new AtomicInteger(0), new AtomicInteger(0));
		}

		private AlleleCount(AtomicInteger homRefCount, AtomicInteger hetCount, AtomicInteger homAltCount)
		{
			this.homRefCount = homRefCount;
			this.hetCount = hetCount;
			this.homAltCount = homAltCount;
		}

		public int getHomRefCount()
		{
			return homRefCount.get();
		}

		public int getHetCount()
		{
			return hetCount.get();
		}

		public int getHomAltCount()
		{
			return homAltCount.get();
		}

		public void add(AlleleCount other)
		{
			homRefCount.addAndGet(other.getHomRefCount());
			hetCount.addAndGet(other.getHetCount());
			homAltCount.addAndGet(other.getHomAltCount());
		}

		public static AlleleCount parse(String str)
		{
			AtomicInteger homRefCount = new AtomicInteger(0);
			AtomicInteger hetCount = new AtomicInteger(0);
			AtomicInteger homAltCount = new AtomicInteger(0);

			if (str.equals("0/0")) homRefCount.incrementAndGet();
			else if (str.equals("0/1")) hetCount.incrementAndGet();
			else
			{
				int off = str.indexOf('/');
				String part1 = str.substring(0, off);
				String part2 = str.substring(off + 1);
				if (part1.equals(part2)) homAltCount.incrementAndGet(); // e.g.
																		// 1/1
				else
					hetCount.incrementAndGet(); // e.g. 1/2 TODO check with
												// Pieter

			}
			return new AlleleCount(homRefCount, hetCount, homAltCount);
		}
	}

	@Override
	public String getCustomHtmlHeaders()
	{
		return null;
	}
}

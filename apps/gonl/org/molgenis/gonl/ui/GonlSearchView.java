package org.molgenis.gonl.ui;

import java.util.List;

import javax.annotation.Nullable;

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
import org.molgenis.framework.ui.html.StringInput;
import org.molgenis.gonl.service.VariantRequest;
import org.molgenis.gonl.utils.VariantAggregator.VariantAggregate;
import org.molgenis.variant.Chromosome;
import org.molgenis.variant.SequenceVariant;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class GonlSearchView implements ScreenView
{
	GonlSearchModel model;

	public GonlSearchView(GonlSearchModel model)
	{
		if (model == null) throw new IllegalArgumentException("model is null");
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

		List<VariantRequest> requests = model.getVariantRequests();
		String chromosome = null;
		int startBp = 0;
		int endBp = 0;
		if (requests != null && requests.size() == 1)
		{
			VariantRequest variantRequest = requests.get(0);
			chromosome = variantRequest.getChromosome();
			startBp = variantRequest.getStartBp();
			endBp = variantRequest.getEndBp();
		}

		List<String> allChromosomeNames = Lists.transform(model.getAllChromosomes(), new Function<Chromosome, String>()
		{
			@Override
			@Nullable
			public String apply(@Nullable
			Chromosome arg0)
			{
				return arg0 != null ? arg0.getName() : null;
			}
		});

		// batch search inputs
		form.add(new StringInput("searchfile"));
		form.add(new ActionInput("Browse"));
		form.add(new Newline());

		// provide a search box
		SelectInput chromosomes = new SelectInput("chromosome", chromosome);
		chromosomes.setOptions(allChromosomeNames, allChromosomeNames);
		chromosomes.setNillable(false);
		form.add(chromosomes);

		// provide a 'from' box
		form.add(new IntInput("from", startBp));

		// provide a 'range' box
		form.add(new IntInput("to", endBp));

		form.add(new ActionInput("search"));

		// provide a box with the current results
		form.add(new Newline());

		List<VariantAggregate> variantAggregates = model.getVariantAggregates();
		if (variantAggregates != null && !variantAggregates.isEmpty())
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

			for (VariantAggregate variantAggregate : variantAggregates)
			{
				SequenceVariant variant = variantAggregate.getVariant();

				// create table row
				int row = table.addRow(variant.getName());
				int col = 0;
				table.setCell(col++, row, StringUtils.join(variantAggregate.getPanels(), ','));
				table.setCell(col++, row, variant.getChr_Name());
				table.setCell(col++, row, variant.getStartBP());
				table.setCell(col++, row, variant.getEndBP());
				table.setCell(col++, row, variant.getRef());
				table.setCell(col++, row, variant.getAlt());
				table.setCell(col++, row, variantAggregate.getHomRefCount());
				table.setCell(col++, row, variantAggregate.getHetCount());
				table.setCell(col++, row, variantAggregate.getHomAltCount());
			}

			form.add(table);
		}

		return form.render();
	}

	@Override
	public String getCustomHtmlHeaders()
	{
		return null;
	}
}

package org.molgenis.gonl.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.server.MolgenisRequest;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.gonl.service.VariantRequest;
import org.molgenis.gonl.service.VariantResponse;
import org.molgenis.gonl.service.VariantSearchException;
import org.molgenis.gonl.service.VariantSearchService;
import org.molgenis.gonl.utils.VariantAggregator;
import org.molgenis.gonl.utils.VariantAggregator.VariantAggregate;
import org.molgenis.io.TableReader;
import org.molgenis.io.TableReaderFactory;
import org.molgenis.io.TupleReader;
import org.molgenis.util.tuple.Tuple;
import org.molgenis.variant.Chromosome;
import org.molgenis.variant.SequenceVariant;

/**
 * GonlSearchController takes care of all user requests and application logic.
 * 
 * <li>Each user request is handled by its own method based action=methodName.
 * <li>MOLGENIS takes care of db.commits and catches exceptions to show to the
 * user <li>GonlSearchModel holds application state and business logic on top of
 * domain model. Get it via this.getModel()/setModel(..). <li>PathoSearchView
 * holds the template to show the layout. Get/set it via
 * this.getView()/setView(..).
 */
@SuppressWarnings("deprecation")
public class GonlSearch extends EasyPluginController<GonlSearchModel>
{
	private static final long serialVersionUID = 1L;

	private VariantSearchService variantSearchService;
	private VariantAggregator variantAggregator;

	public GonlSearch(String name, ScreenController<?> parent)
	{
		super(name, parent);
		this.setModel(new GonlSearchModel(this));
		this.variantSearchService = new VariantSearchService();
		this.variantAggregator = new VariantAggregator();
	}

	public void search(Database db, MolgenisRequest request) throws VariantSearchException, DatabaseException,
			IOException
	{
		variantSearchService.setDatabase(db);
		variantAggregator.setDatabase(db);

		List<VariantRequest> variantRequests = createVariantRequests(db, request);

		try
		{
			Set<SequenceVariant> sequenceVariants = new LinkedHashSet<SequenceVariant>();
			for (VariantRequest variantRequest : variantRequests)
			{
				VariantResponse variantResponse = variantSearchService.search(variantRequest);
				sequenceVariants.addAll(variantResponse.getVariants());
			}

			// aggregate search results
			List<SequenceVariant> sequenceVariantList = new ArrayList<SequenceVariant>(sequenceVariants);
			List<VariantAggregate> variantAggregates = variantAggregator.aggregate(sequenceVariantList);

			// fill model with search results
			getModel().setVariantRequests(variantRequests);
			getModel().setVariantAggregates(variantAggregates);
		}
		catch (DatabaseException e)
		{
			setModel(new GonlSearchModel(this));
			getModel().setMessages(new ScreenMessage(e.getMessage(), false));
		}
		catch (VariantSearchException e)
		{
			setModel(new GonlSearchModel(this));
			getModel().setMessages(new ScreenMessage(e.getMessage(), false));
		}
	}

	private List<VariantRequest> createVariantRequests(Database db, MolgenisRequest request) throws IOException
	{
		List<VariantRequest> variantRequests = new ArrayList<VariantRequest>();

		// batch search
		String searchRequestFileStr = request.getString("searchfile");
		if (searchRequestFileStr != null)
		{
			File searchRequestFile = new File(searchRequestFileStr);
			TableReader tableReader = TableReaderFactory.create(searchRequestFile);
			try
			{
				for (TupleReader tupleReader : tableReader)
				{
					for (Tuple tuple : tupleReader)
					{
						VariantRequest variantRequest = new VariantRequest();
						variantRequest.setChromosome(tuple.getString("chr"));
						variantRequest.setStartBp(tuple.getInt("startBp"));
						variantRequest.setEndBp(tuple.getInt("endBp"));
						variantRequests.add(variantRequest);
					}
				}
			}
			finally
			{
				tableReader.close();
			}
		}
		else
		{
			// single batch
			VariantRequest variantRequest = new VariantRequest();
			variantRequest.setChromosome(request.getString("chromosome"));
			variantRequest.setStartBp(request.getInt("from"));
			variantRequest.setEndBp(request.getInt("to"));
			variantRequests.add(variantRequest);
		}
		return variantRequests;
	}

	@Override
	public ScreenView getView()
	{
		return new GonlSearchView(getModel());
	}

	@Override
	public void reload(Database db) throws Exception
	{
		// refresh chromosome list
		getModel().setAllChromosomes(db.query(Chromosome.class).find());
	}
}
package org.molgenis.gonl.service;

import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.variant.Chromosome;
import org.molgenis.variant.SequenceVariant;

public class VariantSearchService
{
	private static final int MAX_SEARCH_RESULTS = 1000;

	private Database db;

	@Deprecated
	public VariantSearchService()
	{

	}

	public VariantSearchService(Database db)
	{
		if (db == null) throw new IllegalArgumentException("db is null");
		this.db = db;
	}

	public VariantResponse search(VariantRequest request) throws VariantSearchException
	{
		// validate request
		String chromosome = request.getChromosome();
		Integer startBp = request.getStartBp();
		Integer endBp = request.getEndBp();

		if (chromosome == null) throw new VariantSearchException("no chromosome selected");
		if (startBp == null || startBp < 0) throw new VariantSearchException("start position must be a positive number");
		if (endBp == null || endBp < 0) throw new VariantSearchException("end position must be a positive number");
		if (startBp > endBp) throw new VariantSearchException("start position must be lower than the end position");

		try
		{
			// find chromosome
			List<Chromosome> chromosomes = db.find(Chromosome.class, new QueryRule(Chromosome.NAME, Operator.EQUALS,
					chromosome));
			if (chromosomes == null || chromosomes.isEmpty())
			{
				throw new VariantSearchException("unknown chromosome: " + chromosome);
			}
			int chrId = chromosomes.get(0).getId();

			// find variants
			Query<SequenceVariant> query = db.query(SequenceVariant.class).eq(SequenceVariant.CHR, chrId)
					.greaterOrEqual(SequenceVariant.STARTBP, startBp)
					.lessOrEqual(SequenceVariant.ENDBP, endBp);

			int count = query.count();
			if (count > MAX_SEARCH_RESULTS)
			{
				throw new VariantSearchException(
						"Your query resulted in too much data; Please reduce the search window...");
			}

			VariantResponse response = new VariantResponse();
			if (count > 0)
			{
				response.setNrSearchResults(count);
				response.setVariants(query.find());
			}
			return response;
		}
		catch (DatabaseException e)
		{
			throw new VariantSearchException(e);
		}
	}

	@Deprecated
	public void setDatabase(Database db)
	{
		if (db == null) throw new IllegalArgumentException("db is null");
		this.db = db;
	}
}

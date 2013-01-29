package org.molgenis.gonl.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.variant.Chromosome;
import org.molgenis.variant.SequenceVariant;
import org.testng.annotations.Test;

public class VariantSearchServiceTest
{
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void VariantSearchServiceDatabase()
	{
		new VariantSearchService(null);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void search() throws VariantSearchException, DatabaseException
	{
		Query<SequenceVariant> query = (Query<SequenceVariant>) mock(Query.class);
		Query<SequenceVariant> queryChr = (Query<SequenceVariant>) mock(Query.class);
		Query<SequenceVariant> queryGrOrEq = (Query<SequenceVariant>) mock(Query.class);
		Query<SequenceVariant> queryLessOrEq = (Query<SequenceVariant>) mock(Query.class);

		Chromosome chromosome1 = when(mock(Chromosome.class).getId()).thenReturn(1).getMock();
		SequenceVariant variant1 = when(mock(SequenceVariant.class).getName()).thenReturn("variant#1").getMock();
		SequenceVariant variant2 = when(mock(SequenceVariant.class).getName()).thenReturn("variant#2").getMock();

		Database db = mock(Database.class);
		when(db.find(Chromosome.class, new QueryRule(Chromosome.NAME, Operator.EQUALS, "1"))).thenReturn(
				Arrays.asList(chromosome1));
		when(db.query(SequenceVariant.class)).thenReturn(query);
		when(query.eq(SequenceVariant.CHR, 1)).thenReturn(queryChr);
		when(queryChr.greaterOrEqual(SequenceVariant.STARTBP, Integer.valueOf(0))).thenReturn(queryGrOrEq);
		when(queryGrOrEq.lessOrEqual(SequenceVariant.ENDBP, Integer.valueOf(10))).thenReturn(queryLessOrEq);

		when(queryLessOrEq.count()).thenReturn(2);
		when(queryLessOrEq.find()).thenReturn(Arrays.asList(variant1, variant2));

		VariantSearchService searchService = new VariantSearchService(db);

		VariantRequest request = new VariantRequest();
		request.setChromosome("1");
		request.setStartBp(0);
		request.setEndBp(10);

		VariantResponse variantResponse = searchService.search(request);
		assertEquals(2, variantResponse.getNrSearchResults());

		List<SequenceVariant> variants = variantResponse.getVariants();
		assertEquals(2, variants.size());
		assertEquals("variant#1", variants.get(0).getName());
		assertEquals("variant#2", variants.get(1).getName());
	}
}

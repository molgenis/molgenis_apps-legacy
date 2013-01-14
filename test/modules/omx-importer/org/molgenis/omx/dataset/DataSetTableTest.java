package org.molgenis.omx.dataset;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.tupletable.TableException;
import org.molgenis.framework.tupletable.TupleTable;
import org.molgenis.framework.tupletable.impl.CsvTable;
import org.molgenis.model.elements.Field;
import org.molgenis.observ.DataSet;
import org.molgenis.observ.ObservableFeature;
import org.molgenis.observ.Protocol;
import org.molgenis.observ.target.Individual;
import org.molgenis.util.tuple.Tuple;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import app.DatabaseFactory;

public class DataSetTableTest
{
	Database db;

	DataSet ds;

	Logger logger;

	@BeforeClass
	public void setUp() throws DatabaseException
	{
		BasicConfigurator.configure();
		logger = Logger.getLogger(DataSetTableTest.class);

		// assume empthy
		db = DatabaseFactory.create();
		// reset database, danger!
		db.createTables();

		try
		{
			db.beginTx();
			// add some dataset
			ds = new DataSet();
			ds.setName("test1");
			ds.setIdentifier("test1");
			db.add(ds);

			// add some features
			ObservableFeature f = new ObservableFeature();
			f.setName("feature1");
			f.setIdentifier("feature1");
			db.add(f);

			f = new ObservableFeature();
			f.setName("feature2");
			f.setIdentifier("feature2");
			db.add(f);

			Individual i = new Individual();
			i.setName("patient1");
			i.setIdentifier("patient1");
			db.add(i);

			db.commitTx();
		}
		catch (Exception e)
		{
			db.rollbackTx();
		}
	}

	@Test
	public void test1load() throws TableException
	{
		String csv = "target\tfeature1\tfeature2\npatient1\tvalue1\tvalue2";
		TupleTable source = new CsvTable(csv);

		DataSetTable table = new DataSetTable(ds, db);

		table.add(source);

		// columns
		List<Field> columns = table.getAllColumns();
		assertEquals(3, columns.size());
		assertEquals("target", columns.get(0).getName());
		assertEquals("feature1", columns.get(1).getName());
		assertEquals("feature2", columns.get(2).getName());

		// count
		int count = table.getCount();
		assertEquals(1, count);

		// rows
		List<Tuple> rows = table.getRows();
		assertEquals(1, rows.size());
		assertEquals("patient1", rows.get(0).getString("target"));
		assertEquals("value1", rows.get(0).getString("feature1"));
		assertEquals("value2", rows.get(0).getString("feature2"));
	}

	@Test
	public void testHideColumn() throws TableException
	{
		String csv = "target\tfeature1\tfeature2\npatient1\tvalue1\tvalue2";
		TupleTable source = new CsvTable(csv);

		DataSetTable table = new DataSetTable(ds, db);

		table.add(source);

		// columns
		table.hideColumn("feature1");
		List<Field> columns = table.getColumns();
		for (Field field : columns)
			System.out.println(field.getName());
	}

	@Test
	public void getAllColumns() throws TableException, DatabaseException
	{
		DataSet dataSet = when(mock(DataSet.class).getProtocolUsed_Id()).thenReturn(1).getMock();

		Protocol p0 = mock(Protocol.class);
		when(p0.getFeatures_Id()).thenReturn(Arrays.asList(10));
		when(p0.getSubprotocols_Id()).thenReturn(Arrays.asList(1, 2));
		Protocol p1 = mock(Protocol.class);
		when(p1.getFeatures_Id()).thenReturn(Arrays.asList(11));
		Protocol p2 = mock(Protocol.class);
		when(p2.getFeatures_Id()).thenReturn(Arrays.asList(12));

		ObservableFeature f10 = mock(ObservableFeature.class);
		when(f10.getIdentifier()).thenReturn("10");
		when(f10.getName()).thenReturn("name10");
		ObservableFeature f11 = mock(ObservableFeature.class);
		when(f11.getIdentifier()).thenReturn("11");
		when(f11.getName()).thenReturn("name11");
		ObservableFeature f12 = mock(ObservableFeature.class);
		when(f12.getIdentifier()).thenReturn("12");
		when(f12.getName()).thenReturn("name12");

		Database db = mock(Database.class);
		when(db.find(Protocol.class, new QueryRule(Protocol.ID, Operator.EQUALS, 1))).thenReturn(
				Collections.singletonList(p0));
		when(db.find(Protocol.class, new QueryRule(Protocol.ID, Operator.IN, Arrays.asList(1, 2)))).thenReturn(
				Arrays.asList(p1, p2));
		when(
				db.find(ObservableFeature.class,
						new QueryRule(ObservableFeature.ID, Operator.IN, Arrays.asList(10, 11, 12)))).thenReturn(
				Arrays.asList(f10, f11, f12));

		List<Field> cols = new DataSetTable(dataSet, db).getAllColumns();
		assertEquals("10", cols.get(0).getName());
		assertEquals("name10", cols.get(0).getLabel());
		assertEquals("11", cols.get(1).getName());
		assertEquals("name11", cols.get(1).getLabel());
		assertEquals("12", cols.get(2).getName());
		assertEquals("name12", cols.get(2).getLabel());
		assertEquals(3, cols.size());
	}
}
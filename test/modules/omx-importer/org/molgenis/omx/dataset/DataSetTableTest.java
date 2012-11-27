package org.molgenis.omx.dataset;

import java.util.List;

import junit.framework.Assert;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.tupletable.TableException;
import org.molgenis.framework.tupletable.TupleTable;
import org.molgenis.framework.tupletable.impl.CsvTable;
import org.molgenis.model.elements.Field;
import org.molgenis.observ.DataSet;
import org.molgenis.observ.ObservableFeature;
import org.molgenis.observ.target.Individual;
import org.molgenis.omx.dataset.DataSetTable;
import org.molgenis.util.Tuple;
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
		Assert.assertEquals(3, columns.size());
		Assert.assertEquals("target", columns.get(0).getName());
		Assert.assertEquals("feature1", columns.get(1).getName());
		Assert.assertEquals("feature2", columns.get(2).getName());

		// count
		int count = table.getCount();
		Assert.assertEquals(1, count);

		// rows
		List<Tuple> rows = table.getRows();
		Assert.assertEquals(1, rows.size());
		Assert.assertEquals("patient1", rows.get(0).getString("target"));
		Assert.assertEquals("value1", rows.get(0).getString("feature1"));
		Assert.assertEquals("value2", rows.get(0).getString("feature2"));
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

}
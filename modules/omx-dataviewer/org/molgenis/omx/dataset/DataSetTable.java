package org.molgenis.omx.dataset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.tupletable.AbstractFilterableTupleTable;
import org.molgenis.framework.tupletable.DatabaseTupleTable;
import org.molgenis.framework.tupletable.TableException;
import org.molgenis.framework.tupletable.TupleTable;
import org.molgenis.model.elements.Field;
import org.molgenis.observ.Characteristic;
import org.molgenis.observ.DataSet;
import org.molgenis.observ.ObservableFeature;
import org.molgenis.observ.ObservationSet;
import org.molgenis.observ.ObservedValue;
import org.molgenis.util.SimpleTuple;
import org.molgenis.util.Tuple;

public class DataSetTable extends AbstractFilterableTupleTable implements DatabaseTupleTable
{
	private static Logger logger = Logger.getLogger(DataSetTable.class);
	private DataSet dataSet;
	private Database db;
	private List<Field> columns;

	public DataSetTable(DataSet set, Database db) throws TableException
	{
		if (set == null) throw new TableException("DataSet cannot be null");
		this.dataSet = set;
		if (db == null) throw new TableException("db cannot be null");
		setDb(db);
		setFirstColumnFixed(true);
	}

	@Override
	public List<Field> getAllColumns() throws TableException
	{
		if (columns == null) initColumnsFromDb();
		return Collections.unmodifiableList(columns);
	}

	private void initColumnsFromDb() throws TableException
	{
		try
		{
			// instead ask for protocol.features?

			String sql = "SELECT DISTINCT Characteristic.identifier as name, Characteristic.name as label FROM Characteristic, ObservedValue, ObservationSet WHERE ObservationSet.partOfDataSet="
					+ dataSet.getId()
					+ " AND ObservedValue.ObservationSet=ObservationSet.id AND Characteristic.id = ObservedValue.feature";

			columns = new ArrayList<Field>();
			Field targetField = new Field("target");
			targetField.setLabel("target");
			columns.add(targetField);

			for (Tuple t : getDb().sql(sql))
			{
				Field f = new Field(t.getString("name"));
				f.setLabel(t.getString("label"));
				columns.add(f);
			}
		}
		catch (Exception e)
		{
			throw new TableException(e);
		}
	}

	@Override
	public Iterator<Tuple> iterator()
	{
		try
		{
			return getRows().iterator();
		}
		catch (TableException e)
		{
			logger.error("Exception getting iterator", e);
			throw new RuntimeException(e);
		}
	}

	private Integer findCharacteristicId(String identifier) throws DatabaseException
	{
		List<Characteristic> characteristics = getDb().query(Characteristic.class)
				.eq(Characteristic.IDENTIFIER, identifier).find();

		return characteristics.isEmpty() ? null : characteristics.get(0).getId();
	}

	@Override
	public List<Tuple> getRows() throws TableException
	{
		try
		{
			List<Tuple> result = new ArrayList<Tuple>();
			Query<ObservationSet> queryObservertionSet = getDb().query(ObservationSet.class);

			// Limit the nr of rows
			if (getLimit() > 0)
			{
				queryObservertionSet.limit(getLimit());
			}
			if (getOffset() > 0)
			{
				queryObservertionSet.offset(getOffset());
			}

			for (ObservationSet os : queryObservertionSet.eq(ObservationSet.PARTOFDATASET, dataSet.getId()).find())
			{

				Tuple t = new SimpleTuple();
				t.set("target", os.getTarget_Identifier());

				Query<ObservedValue> queryObservedValue = getDb().query(ObservedValue.class);

				// Limit the nr of columns
				if (getColLimit() > 0)
				{
					queryObservedValue.limit(getColLimit());
				}
				if (getColOffset() > 0)
				{
					queryObservedValue.offset(getColOffset());
				}

				for (ObservedValue v : queryObservedValue.eq(ObservedValue.OBSERVATIONSET, os.getId()).find())
				{
					t.set(v.getFeature_Identifier(), v.getValue());
				}

				result.add(t);
			}

			return result;
		}
		catch (Exception e)
		{
			logger.error("Exception getRows", e);
			throw new TableException(e);
		}

	}

	@Override
	public int getCount() throws TableException
	{
		try
		{
			return getDb().query(ObservationSet.class).eq(ObservationSet.PARTOFDATASET, dataSet.getId()).count();
		}
		catch (DatabaseException e)
		{
			logger.error("DatabaseException getCount", e);
			throw new TableException(e);
		}

	}

	public void add(TupleTable table) throws TableException
	{
		try
		{
			getDb().beginTx();

			// verify columns to be feature identifiers
			Map<String, Integer> featureMap = new TreeMap<String, Integer>();

			List<Field> cols = table.getAllColumns();
			for (Field f : cols)
			{
				// first is always target, rest should be feature identifiers
				if (!f.equals(cols.get(0)))
				{
					// slow
					try
					{
						List<ObservableFeature> feature = getDb().query(ObservableFeature.class)
								.eq(ObservableFeature.IDENTIFIER, f.getName()).find();
						if (feature.size() != 1)
						{
							throw new TableException("add failed: " + f.getName() + " not known ObservableFeature");
						}
						else
						{
							featureMap.put(f.getName(), feature.get(0).getId());
						}
					}
					catch (DatabaseException e)
					{
						throw new TableException(e);
					}
				}
			}

			// load the rows
			int count = 0;

			for (Tuple t : table)
			{
				// slow, check if target exists
				List<Characteristic> targets = getDb().query(Characteristic.class)
						.eq(Characteristic.IDENTIFIER, t.getString(0)).find();
				if (targets.size() == 1)
				{
					ObservationSet es = new ObservationSet();
					es.setPartOfDataSet(dataSet.getId());
					es.setTarget(targets.get(0).getId());
					getDb().add(es);

					List<ObservedValue> values = new ArrayList<ObservedValue>();
					for (String name : t.getFieldNames())
					{
						if (!name.equals(t.getColName(0)))
						{
							ObservedValue v = new ObservedValue();
							v.setObservationSet(es.getId());
							v.setFeature(featureMap.get(name));
							v.setValue(t.getString(name));

							values.add(v);
						}
					}
					getDb().add(values);
				}
				else
				{
					throw new DatabaseException("import of row " + count + " failed: target " + t.getString(0)
							+ " unknown");
				}
			}
			count++;

			getDb().commitTx();

		}
		catch (Exception e)
		{
			try
			{
				getDb().rollbackTx();
			}
			catch (DatabaseException e1)
			{
				;
			}
			throw new TableException(e);
		}
	}

	@Override
	public Database getDb()
	{
		return db;
	}

	@Override
	public void setDb(Database db)
	{
		this.db = db;
	}

	public DataSet getDataSet()
	{
		return dataSet;
	}

	public void setDataSet(DataSet dataSet)
	{
		this.dataSet = dataSet;
	}

}

package org.molgenis.omx.dataset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
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
	private DataSet dataSet;

	private Database db;

	private int rows = -1;

	private List<Field> columns;

	public DataSetTable(DataSet set, Database db) throws TableException
	{
		if (set == null) throw new TableException("DataSet cannot be null");
		this.dataSet = set;
		if (db == null) throw new TableException("db cannot be null");
		this.setDb(db);
		this.setFirstColumnFixed(true);
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
	public List<Tuple> getRows() throws TableException
	{
		try
		{
			List<Tuple> result = new ArrayList<Tuple>();
			List<QueryRule> filters = getFilters();
			rows = 0;
			// load visible ObservationSet
			if (filters.isEmpty())
			{
				rows = getDb().query(ObservationSet.class).eq(ObservationSet.PARTOFDATASET, dataSet.getId()).count();
				for (ObservationSet os : getDb().query(ObservationSet.class)
						.eq(ObservationSet.PARTOFDATASET, dataSet.getId()).find())
				{

					Tuple t = new SimpleTuple();
					t.set("target", os.getTarget_Identifier());

					for (ObservedValue v : getDb().query(ObservedValue.class)
							.eq(ObservedValue.OBSERVATIONSET, os.getId()).find())
					{
						t.set(v.getFeature_Identifier(), v.getValue());
					}
					result.add(t);
				}
			}
			else
			{

				for (ObservationSet os : getDb().query(ObservationSet.class)
						.eq(ObservationSet.PARTOFDATASET, dataSet.getId()).find())
				{

					Tuple t = new SimpleTuple();
					// TODO : remove the empty rows that do not much the filters
					// from t

					Integer rowId = 0;

					// for (QueryRule queryRule : filters)
					// {os.
					// if (os.getTarget_Identifier().equals(os.) t.set("target",
					// os.getTarget_Identifier());
					// }

					for (QueryRule queryRule : filters)
					{
						for (ObservedValue v : getDb().query(ObservedValue.class)
								.eq(ObservedValue.OBSERVATIONSET, os.getId()).find())
						{
							// set the filter to the Observable features here
							if (queryRule.getField().equals(v.getFeature_Identifier())
									&& queryRule.getValue().equals(v.getValue()))
							{
								// t.set(v.getFeature_Identifier(),v.getValue());
								rowId = v.getObservationSet_Id();
								break;
							}
						}
						//
					}
					if (rowId != 0)
					{
						t.set("target", os.getTarget_Identifier());
						for (ObservedValue v : getDb().query(ObservedValue.class)
								.eq(ObservedValue.OBSERVATIONSET, rowId).find())
						{
							t.set(v.getFeature_Identifier(), v.getValue());
						}
						result.add(t);
						this.rows++;
					}

					// boolean keep = false;
					//
					// for (ObservedValue v : getDb().query(ObservedValue.class)
					// .eq(ObservedValue.OBSERVATIONSET, os.getId()).find())
					// {
					// for (QueryRule queryRule : filters)
					// {
					// // set the filter to the Observable features here
					// if
					// (queryRule.getField().equals(v.getFeature_Identifier())
					// && queryRule.getValue().equals(v.getValue()))
					// t.set(v.getFeature_Identifier(),
					// v.getValue());
					// }
					// result.add(t);
					// }
				}
			}

			return result;
		}
		catch (Exception e)
		{
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
	public int getCount() throws TableException
	{
		getRows();
		return this.rows;
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

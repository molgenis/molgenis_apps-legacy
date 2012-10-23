package org.molgenis.omicsconnect.dataset;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.molgenis.datatable.model.AbstractTupleTable;
import org.molgenis.datatable.model.TableException;
import org.molgenis.datatable.model.TupleTable;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.model.elements.Field;
import org.molgenis.observ.Characteristic;
import org.molgenis.observ.DataSet;
import org.molgenis.observ.ObservableFeature;
import org.molgenis.observ.ObservationSet;
import org.molgenis.observ.ObservedValue;
import org.molgenis.util.SimpleTuple;
import org.molgenis.util.Tuple;

/** Unfilterable */
public class DataSetTable extends AbstractTupleTable
{
	// the data set that is wrapped
	DataSet set;

	// cache of features
	List<ObservableFeature> features = new ArrayList<ObservableFeature>();

	public DataSetTable(DataSet set, Database db) throws TableException
	{
		if (set == null) throw new TableException("DataSet cannot be null");
		this.set = set;
		if (db == null) throw new TableException("db cannot be null");
		this.setDb(db);
	}

	@Override
	public List<Field> getAllColumns() throws TableException
	{
		try
		{
			// instead ask for protocol.features?

			String sql = "SELECT DISTINCT Characteristic.identifier as name FROM Characteristic, ObservedValue, ObservationSet WHERE ObservationSet.partOfDataSet="
					+ set.getId()
					+ " AND ObservedValue.ObservationSet=ObservationSet.id AND Characteristic.id = ObservedValue.feature";

			List<Field> result = new ArrayList<Field>();
			result.add(new Field("target"));

			for (Tuple t : getDb().sql(sql))
			{
				Field f = new Field(t.getString("name"));
				result.add(f);
			}
			return result;
		}
		catch (Exception e)
		{
			throw new TableException(e);
		}

	}

	public List<Tuple> getRows() throws TableException
	{
		try
		{
			List<Tuple> result = new ArrayList<Tuple>();

			// load visible ObservationSet
			for (ObservationSet os : getDb().query(ObservationSet.class).eq(ObservationSet.PARTOFDATASET, set.getId())
					.find())
			{
				Tuple t = new SimpleTuple();
				t.set("target", os.getTarget_Identifier());

				for (ObservedValue v : getDb().query(ObservedValue.class).eq(ObservedValue.OBSERVATIONSET, os.getId())
						.find())
				{
					t.set(v.getFeature_Identifier(), v.getValue());
				}
				result.add(t);
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
					es.setPartOfDataSet(set.getId());
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
		// unfiltered
		try
		{
			return getDb().query(ObservationSet.class).eq(ObservationSet.PARTOFDATASET, set.getId()).count();
		}
		catch (DatabaseException e)
		{
			throw new TableException(e);
		}
	}
}

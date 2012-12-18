package org.molgenis.omx.dataset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.tupletable.AbstractFilterableTupleTable;
import org.molgenis.framework.tupletable.DatabaseTupleTable;
import org.molgenis.framework.tupletable.TableException;
import org.molgenis.framework.tupletable.TupleTable;
import org.molgenis.model.elements.Field;
import org.molgenis.observ.Characteristic;
import org.molgenis.observ.DataSet;
import org.molgenis.observ.ObservableFeature;
import org.molgenis.observ.ObservationSet;
import org.molgenis.observ.ObservationTarget;
import org.molgenis.observ.ObservedValue;
import org.molgenis.util.tuple.KeyValueTuple;
import org.molgenis.util.tuple.Tuple;
import org.molgenis.util.tuple.WritableTuple;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

/**
 * DataSetTable
 * 
 * If this table is too slow consider creating database an index on the
 * ObservedValue table : One on the fields Feature-Value and one on
 * ObservationSet-Feature-Value
 * 
 */
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

	@Override
	public List<Tuple> getRows() throws TableException
	{
		try
		{
			List<Tuple> result = new ArrayList<Tuple>();

			Query<ObservationSet> query = createQuery();

			if (query == null)
			{
				return new ArrayList<Tuple>();
			}

			// Limit the nr of rows
			if (getLimit() > 0)
			{
				query.limit(getLimit());
			}

			if (getOffset() > 0)
			{
				query.offset(getOffset());
			}

			for (ObservationSet os : query.find())
			{

				WritableTuple t = new KeyValueTuple();
				Characteristic c = db.findById(Characteristic.class, os.getTarget_Id());
				t.set("target", c.getName());

				Query<ObservedValue> queryObservedValue = getDb().query(ObservedValue.class);

				List<Field> columns = getColumns();
				if (isFirstColumnFixed())
				{
					columns.remove(0);
				}

				// Only retrieve the visible columns
				Collection<String> fieldNames = Collections2.transform(columns, new Function<Field, String>()
				{
					@Override
					public String apply(final Field field)
					{
						return field.getName();
					}
				});

				for (ObservedValue v : queryObservedValue.eq(ObservedValue.OBSERVATIONSET, os.getId())
						.in(ObservedValue.FEATURE_IDENTIFIER, new ArrayList<String>(fieldNames)).find())
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
			Query<ObservationSet> query = createQuery();
			return query == null ? 0 : query.count();
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
					String firstColName = t.getColNames().iterator().next();
					for (String name : t.getColNames())
					{
						if (!name.equals(firstColName))
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

	// Creates the query based on the provided filters
	// Returns null if we already now there wil be no results
	private Query<ObservationSet> createQuery() throws TableException, DatabaseException
	{

		Query<ObservationSet> query;

		if (getFilters().isEmpty())
		{
			query = getDb().query(ObservationSet.class).eq(ObservationSet.PARTOFDATASET, dataSet.getId());
		}
		else
		{
			// For now only single simple queries are supported
			List<QueryRule> queryRules = new ArrayList<QueryRule>();

			for (QueryRule filter : getFilters())
			{
				if ((filter.getOperator() != Operator.EQUALS) && (filter.getOperator() != Operator.LIKE))
				{
					// value is always a String so LESS etc. can't be
					// supported, NOT queries are not supported yet
					throw new NotImplementedException("Operator [" + filter.getOperator()
							+ "] not yet implemented, only EQUALS and LIKE are supported.");

				}

				// Null values come to us as String 'null'
				if ((filter.getValue() != null) && (filter.getValue() instanceof String)
						&& ((String) filter.getValue()).equalsIgnoreCase("null"))
				{
					filter.setValue(null);
				}

				if (filter.getField().equals("target"))
				{

					QueryRule rule = new QueryRule(ObservationTarget.NAME, filter.getOperator(), filter.getValue());
					List<ObservationTarget> targets = getDb().find(ObservationTarget.class, rule);

					// Create a collection of ObservationTarget ids
					Collection<Integer> targetIds = Collections2.transform(targets,
							new Function<ObservationTarget, Integer>()
							{
								@Override
								public Integer apply(final ObservationTarget target)
								{
									return target.getId();
								}
							});

					List<ObservationSet> observationSets = getDb().query(ObservationSet.class)
							.in(ObservationSet.TARGET, new ArrayList<Integer>(targetIds)).find();

					// No results
					if (observationSets.isEmpty())
					{
						return null;
					}

					// Create a collection of ObservationSet ids
					Collection<Integer> observationSetIds = Collections2.transform(observationSets,
							new Function<ObservationSet, Integer>()
							{
								@Override
								public Integer apply(final ObservationSet observationSet)
								{
									return observationSet.getId();
								}
							});

					queryRules.add(new QueryRule(ObservedValue.OBSERVATIONSET_ID, Operator.IN, new ArrayList<Integer>(
							observationSetIds)));
				}
				else
				{
					queryRules.add(new QueryRule(ObservedValue.FEATURE_IDENTIFIER, Operator.EQUALS, filter.getField()));
					queryRules.add(new QueryRule(ObservedValue.VALUE, filter.getOperator(), filter.getValue()));
				}

			}

			List<ObservedValue> observedValues = getDb().find(ObservedValue.class,
					queryRules.toArray(new QueryRule[queryRules.size()]));

			// No results
			if (observedValues.isEmpty())
			{
				return null;
			}

			List<Integer> observationSetIds = new ArrayList<Integer>();
			for (ObservedValue observedValue : observedValues)
			{
				if (!observationSetIds.contains(observedValue.getObservationSet_Id()))
				{
					observationSetIds.add(observedValue.getObservationSet_Id());
				}
			}

			query = getDb().query(ObservationSet.class).eq(ObservationSet.PARTOFDATASET, dataSet.getId())
					.in(ObservationSet.ID, observationSetIds);
		}

		return query;

	}
}

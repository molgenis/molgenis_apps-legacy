package org.molgenis.datatable.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;
import org.molgenis.datatable.util.MolgenisUpdateDatabase;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.tupletable.AbstractFilterableTupleTable;
import org.molgenis.framework.tupletable.EditableTupleTable;
import org.molgenis.framework.tupletable.TableException;
import org.molgenis.model.elements.Field;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.util.SimpleTuple;
import org.molgenis.util.Tuple;

import app.DatabaseFactory;

/**
 * Wrap one Protocol EAV model in a TupleTable so that you can query this
 * protocol as if it was a real table (which we actually could implement for
 * smaller protocols).
 * 
 * Here each Measurement is converted into Field, and each ProtocolApplication
 * becomes a Tuple with the ObservedValue for filling the elements of the Tuple.
 * In addition, the 'target' is assumed constant for all ObservedValue and is
 * added as first column. Optionally, the ProtocolApplication metadata can be
 * viewed (future todo).
 */
public class AnimalDBTable extends AbstractFilterableTupleTable implements EditableTupleTable

{
	// mapping to Field (changes on paging)
	private List<Field> columns = new ArrayList<Field>();

	private List<Field> visibleColumns = new ArrayList<Field>();
	private List<String> measNames = new ArrayList<String>();
	private int numberofrows = 0;

	private HashMap<String, String> hashMeasurementsWithCategories;

	public AnimalDBTable(Database db) throws TableException
	{
		this.setDb(db);
	}

	/*
	 * @Override public int getColCount() { // +1 for the target column return
	 * protocol.getFeatures_Id().size() + 1; }
	 */

	@Override
	public int getCount() throws TableException
	{
		try
		{
			return getDb().find(Individual.class).size();
		}
		catch (DatabaseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return numberofrows;
	}

	public boolean isFirstColumnFixed()
	{
		return true;
	}

	// public List<Field> getVisibleColumns() throws TableException
	// {
	// return visibleColumns;
	// }

	public List<Field> getAllColumns() throws TableException
	{
		if (columns.size() == 0)
		{

			try
			{
				List<String> arrayVisColumns = new ArrayList<String>();
				arrayVisColumns.add("Active");
				arrayVisColumns.add("Line");
				arrayVisColumns.add("Location");
				arrayVisColumns.add("Sex");
				arrayVisColumns.add("Species");
				arrayVisColumns.add("Background");

				Field target = new Field("target");

				columns.add(target);

				// Preset the variables that are shown in the matrix
				for (Measurement columnName : getDb().find(Measurement.class,
						new QueryRule(Measurement.NAME, Operator.IN, arrayVisColumns)))
				{
					columns.add(new Field(columnName.getName()));
				}

				for (Measurement columnName : getDb().find(Measurement.class))
				{
					if (!arrayVisColumns.contains(columnName.getName()))
					{
						columns.add(new Field(columnName.getName()));
					}
					// if (arrayVisColumns.contains(columnName.getName()))
					// {
					// visibleColumns.add(columns.get(columns.size() - 1));
					// }
				}
			}
			catch (DatabaseException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return columns;
	}

	// private List<String> getColumnNames()
	// {
	// try
	// {
	// for (Measurement columnName : getDb().find(Measurement.class))
	// {
	// measNames.add(columnName.getName());
	// }
	// }
	// catch (DatabaseException e)
	// {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// return measNames;
	// }

	@Override
	public void close()
	{
	}

	public List<Tuple> getRows()
	{
		List<Individual> obT;

		List<Tuple> result = new ArrayList<Tuple>();
		try
		{
			obT = getDb().find(Individual.class);

			List<String> targetNames = new ArrayList<String>();

			for (Individual target : obT)
			{
				targetNames.add(target.getName());
			}

			String lastTarget = "";

			Tuple t = null;

			for (ObservedValue v : getDb().find(ObservedValue.class,
					new QueryRule(ObservedValue.TARGET_NAME, Operator.IN, targetNames)))
			{
				// New target in the table, we should create a new tuple
				if (!lastTarget.equals(v.getTarget_Name()))
				{
					if (t != null)
					{
						result.add(t);
					}

					t = new SimpleTuple();

					lastTarget = v.getTarget_Name();

					t.set("target", v.getTarget_Name());
				}

				t.set(v.getFeature_Name(), v.getValue());

			}

			result.add(t);

		}
		catch (Exception e1)
		{
			result = null;
			System.out.println("There is no RESULT tuple");
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return result;
	}

	/*
	 * @Override protected Tuple getValues(int row, List<Field> columns) throws
	 * TableException { numberofrows = obsT.size();
	 * 
	 * Tuple tuple = new SimpleTuple(); ; try {
	 * 
	 * for (ObservedValue o : getDb().find(ObservedValue.class)) { //
	 * 
	 * tuple.set(o.getTarget_Name(), getColumnNames()); } } catch
	 * (DatabaseException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } return tuple;
	 * 
	 * }
	 */
	@Override
	public void add(Tuple request) throws TableException
	{
		Database db = getDb();
		String patientID = request.getString("targetID");

		Individual ot = null;

		String investigationName = "";

		try
		{
			// Check if the individual already exists in the database,
			// if
			// so, it only gives back the message. If it doesn`t, the
			// individual is added to the database
			if (db.find(Individual.class, new QueryRule(Individual.NAME, Operator.EQUALS, patientID)).size() > 0)
			{
				throw new TableException("The patient has already existed and adding failed. Please edit this patient");
			}
			else
			{
				ot = new Individual();
				ot.setName(patientID);
			}
			// If the individual is new, the following code will be
			// executed.
			if (ot != null)
			{

				if (request.getString("data") != null)
				{
					// Get the data and transform it to json object. And
					// this json object contains all the new added
					// values
					JSONObject json = new JSONObject(request.getString("data"));

					List<ObservedValue> listOfNewValues = new ArrayList<ObservedValue>();

					// create an iterator for the json object.
					Iterator<?> iterator = json.keys();

					int count = 0;

					while (iterator.hasNext())
					{

						String feature = iterator.next().toString();

						// We do not know which investigation it is in
						// JQGridView.java class. Therefore we take the
						// investigationName from measurement
						if (count == 0)
						{
							investigationName = db
									.find(Measurement.class, new QueryRule(Measurement.NAME, Operator.EQUALS, feature))
									.get(0).getInvestigation_Name();
							count++;
						}

						String value = json.get(feature).toString();
						if (!value.equals(""))
						{
							ObservedValue ov = new ObservedValue();
							ov.setTarget_Name(patientID);
							ov.setFeature_Name(feature);
							if (getHashMeasurementsWithCategories().containsKey(feature))
							{
								String[] splitValue = value.split("\\.");
								ov.setValue(splitValue[0]);
							}
							else
							{
								ov.setValue(value);
							}
							ov.setInvestigation_Name(investigationName);
							listOfNewValues.add(ov);
						}
					}

					ot.setInvestigation_Name(investigationName);

					db.add(ot);

					db.add(listOfNewValues);
				}
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new TableException(e);
		}

	}

	@Override
	public void update(Tuple request) throws TableException
	{
		String targetString = "Pa_Id";

		String targetID = request.getString(targetString);

		if (targetID != null)
		{

			try
			{
				List<String> listFields = request.getFieldNames();

				List<QueryRule> listQuery = new ArrayList<QueryRule>();
				listQuery.add(new QueryRule(ObservedValue.TARGET_NAME, Operator.EQUALS, targetID));
				listQuery.add(new QueryRule(ObservedValue.FEATURE_NAME, Operator.IN, listFields));

				Integer protAppID = getDb().find(ObservedValue.class, new QueryRule(listQuery)).get(0)
						.getProtocolApplication_Id();

				for (Field eachField : getAllColumns())
				{

					if (!eachField.getName().equals(targetString))
					{
						MolgenisUpdateDatabase mu = new MolgenisUpdateDatabase();
						mu.UpdateDatabase(getDb(), targetID, request.getString(eachField.getName()),
								eachField.getName(), protAppID);
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
				throw new TableException(e);
			}

		}
	}

	@Override
	public void remove(Tuple request) throws TableException
	{
		try
		{
			String paId = request.getString("Pa_Id");

			Query<ObservedValue> query = getDb().query(ObservedValue.class);
			List<String> listOfColumns = new ArrayList<String>();
			for (Field f : getAllColumns())
			{
				if (!f.getName().equals("Pa_Id"))
				{
					listOfColumns.add(f.getName());
				}
			}
			query.addRules(new QueryRule(ObservedValue.TARGET_NAME, Operator.EQUALS, paId));
			query.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.IN, listOfColumns));
			List<ObservedValue> listOfRemoveValues = query.find();

			if (listOfRemoveValues.size() > 0)
			{

				Database db = getDb();

				Individual ind = db.find(Individual.class, new QueryRule(Individual.NAME, Operator.EQUALS, paId))
						.get(0);
				db.remove(ind);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new TableException(e);
		}

	}

	private HashMap<String, String> getHashMeasurementsWithCategories() throws DatabaseException
	{
		if (hashMeasurementsWithCategories == null)
		{
			hashMeasurementsWithCategories = new HashMap<String, String>();

			List<Measurement> listOM = getDb().find(Measurement.class);
			for (Measurement m : listOM)
			{
				if (m.getCategories_Name().size() > 0)
				{
					hashMeasurementsWithCategories.put(m.getName(), m.getDataType());

				}

			}
		}

		return hashMeasurementsWithCategories;
	}

	/**
	 * very bad: bypasses all security and connection management
	 */
	private Database db;

	public void setDb(Database db)
	{
		if (db == null) throw new NullPointerException("database cannot be null in setDb(db)");
		this.db = db;
	}

	public Database getDb()
	{
		try
		{
			db = DatabaseFactory.create();
		}
		catch (DatabaseException e)
		{
			throw new RuntimeException(e);
		}
		return this.db;
	}

}
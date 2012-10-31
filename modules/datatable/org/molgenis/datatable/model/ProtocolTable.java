package org.molgenis.datatable.model;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;
import org.molgenis.datatable.util.MolgenisUpdateDatabase;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.tupletable.AbstractFilterableTupleTable;
import org.molgenis.framework.tupletable.DatabaseTupleTable;
import org.molgenis.framework.tupletable.EditableTupleTable;
import org.molgenis.framework.tupletable.TableException;
import org.molgenis.model.elements.Field;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Category;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.protocol.Protocol;
import org.molgenis.protocol.ProtocolApplication;
import org.molgenis.util.SimpleTuple;
import org.molgenis.util.Tuple;

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
public class ProtocolTable extends AbstractFilterableTupleTable implements EditableTupleTable, DatabaseTupleTable
{
	// protocol to query
	private Protocol protocol;
	private String investigationName = "";
	// mapping to Field (changes on paging)
	private List<Field> columns = new ArrayList<Field>();
	private String targetString = "target";
	private HashMap<String, String> hashMeasurementsWithCategories;

	public String getTargetString()
	{
		return targetString;
	}

	public void setTargetString(String targetString)
	{
		this.targetString = targetString;
	}

	// measurements
	Map<Measurement, Protocol> measurements = new LinkedHashMap<Measurement, Protocol>();

	public ProtocolTable(Protocol protocol) throws TableException
	{
		if (protocol == null) throw new TableException("protocol cannot be null");

		this.protocol = protocol;
	}

	public ProtocolTable(String investigationName) throws TableException
	{
		this.investigationName = investigationName;
	}

	/*
	 * @Override public int getColCount() { // +1 for the target column return
	 * protocol.getFeatures_Id().size() + 1; }
	 */

	/**
	 * Beware: this function expects a prepared database object with security
	 * and an active connection to be set before it is called!!
	 */
	@Override
	public List<Field> getAllColumns() throws TableException
	{
		try
		{
			if (getDb().getConnection().isClosed())
			{
				throw new TableException("getRows(): Connection is closed!");
			}
		}
		catch (SQLException e1)
		{
			throw new TableException(e1);
		}
		catch (DatabaseException e1)
		{
			throw new TableException(e1);
		}

		if (columns.size() == 0)
		{
			try
			{
				// get all features of protocol AND subprotocols
				measurements = getMeasurementsRecursive(getDb());

				Field target = new Field(targetString);

				columns.add(target);

				// convert into field
				for (Measurement m : measurements.keySet())
				{
					Field col = new Field(m.getLabel());
					// Field col = new Field(m.getName());

					col.setDescription(m.getDescription());
					// todo: setType()
					columns.add(col);
				}

			}
			catch (Exception e)
			{
				throw new TableException(e);
			}
		}
		return columns;
	}

	private Map<Measurement, Protocol> getMeasurementsRecursive(Database db) throws DatabaseException
	{
		Map<Measurement, Protocol> result = new LinkedHashMap<Measurement, Protocol>();

		for (Protocol p : db.find(Protocol.class, new QueryRule(Protocol.INVESTIGATION_NAME, Operator.EQUALS,
				investigationName)))
		{

			if (p.getFeatures_Name().size() > 0)
			{

				for (Measurement m : db.find(Measurement.class,
						new QueryRule(Measurement.NAME, Operator.IN, p.getFeatures_Name())))
				{
					result.put(m, p);
				}

			}

		}
		return result;

	}

	private Map<Measurement, Protocol> getMeasurementsRecursive(Database db, Protocol protocol)
			throws DatabaseException
	{
		List<Integer> featureIds = protocol.getFeatures_Id();

		Map<Measurement, Protocol> result = new LinkedHashMap<Measurement, Protocol>();

		if (featureIds.size() > 0)
		{
			List<Measurement> mList = db.query(Measurement.class).in(Measurement.ID, featureIds).find();
			for (Measurement m : mList)
			{
				result.put(m, protocol);
			}
		}

		// go recursive on all subprotocols
		if (protocol.getSubprotocols_Id().size() > 0)
		{
			List<Protocol> subProtocols = db.query(Protocol.class).in(Protocol.ID, protocol.getSubprotocols_Id())
					.find();
			for (Protocol subProtocol : subProtocols)
			{
				result.putAll(getMeasurementsRecursive(db, subProtocol));
			}
		}

		// return all the featureId
		return result;

	}

	/**
	 * Beware: this function expects a prepared database object with security
	 * and an active connection to be set before it is called!!
	 */
	@Override
	public List<Tuple> getRows() throws TableException
	{
		try
		{
			if (getDb().getConnection().isClosed())
			{
				throw new TableException("getRows(): Connection is closed!");
			}
		}
		catch (SQLException e1)
		{
			throw new TableException(e1);
		}
		catch (DatabaseException e1)
		{
			throw new TableException(e1);
		}

		try
		{
			List<Tuple> result = new ArrayList<Tuple>();
			for (Integer rowId : getRowIds(false, getDb()))
			{
				boolean target = false;
				Tuple row = new SimpleTuple();

				List<QueryRule> ovFromInv = new ArrayList<QueryRule>();
				ovFromInv.add(new QueryRule(ObservedValue.PROTOCOLAPPLICATION, Operator.EQUALS, rowId));
				ovFromInv.add(new QueryRule(ObservedValue.INVESTIGATION_NAME, Operator.EQUALS, investigationName));

				for (ObservedValue v : getDb().find(ObservedValue.class, new QueryRule(ovFromInv)))
				{
					if (!target)
					{
						if (isInViewPort(targetString))
						{
							row.set(targetString, v.getTarget_Name());
						}
						target = true;
					}

					// get measurements (evil expensive)
					Measurement currentMeasurement = null;
					for (Measurement m : measurements.keySet())
					{
						if (m.getName().equals(v.getFeature_Name()))
						{
							currentMeasurement = m;
							break;
						}
					}
					if (currentMeasurement != null)
					{

						if ("categorical".equals(currentMeasurement.getDataType()))
						{

							for (Category c : getDb().find(Category.class,
									new QueryRule(Category.NAME, Operator.IN, currentMeasurement.getCategories_Name())))
							{
								if (v.getValue().equals(c.getCode_String())
										&& isInViewPort(currentMeasurement.getLabel()))
								{
									row.set(currentMeasurement.getLabel(), v.getValue() + "." + c.getDescription());
									break;
								}
							}
						}
						else
						{

							if (v.getValue() != null && !v.getValue().isEmpty()
									&& isInViewPort(currentMeasurement.getLabel()))
							{
								row.set(currentMeasurement.getLabel(), v.getValue());

							}

						}
					}
				}
				result.add(row);
			}

			// Query for the measurement that is asked to sort, Scolsom01, with
			// filter rule
			//

			if (this.getFilters().size() > 0)
			{

			}

			return result;
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
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public void close()
	{
	}

	/**
	 * Beware: this function expects a prepared database object with security
	 * and an active connection to be set before it is called!!
	 */
	@Override
	public int getCount() throws TableException
	{
		try
		{
			return this.getRowIds(true, getDb()).get(0);
		}
		catch (DatabaseException e)
		{
			throw new TableException(e);
		}
	}

	// FILTERING
	// we only need to know what rows to show :-)
	private List<Integer> getRowIds(boolean count, Database db) throws TableException, DatabaseException
	{
		// get columns that are used in filtering or sorting
		Set<String> columnsUsed = new HashSet<String>();

		for (QueryRule r : getFilters())
		{
			if (r.getValue() != null)
			{

				if (!r.getValue().toString().contains(investigationName))
				{
					r.setValue(r.getValue() + "_" + investigationName);
				}
			}
			// IF SEARCH BUTTON IS CLICKED
			if (getFilters().get(0).getField() != null)
			{
				columnsUsed.add(r.getField());
			}
			else
			{
				// IF WE WANT TO ORDER A COLUMN
				columnsUsed.add(r.getValue().toString());
			}
		}

		// get measurements
		List<Measurement> measurementsUsed = new ArrayList<Measurement>();

		if (columnsUsed.size() > 0)
		{

			List<QueryRule> query = new ArrayList<QueryRule>();
			List<String> filteredColumns = new ArrayList<String>(columnsUsed);
			query.add(new QueryRule(Measurement.NAME, Operator.IN, filteredColumns));
			query.add(new QueryRule(Measurement.INVESTIGATION_NAME, Operator.EQUALS, investigationName));

			// measurementsUsed = getDb().query(Measurement.class)
			// .in(Measurement.NAME, new ArrayList<String>(columnsUsed)).find();

			measurementsUsed = db.find(Measurement.class, new QueryRule(query));

			for (Measurement m : measurementsUsed)
			{

				for (QueryRule r : getFilters())
				{

					if (r.getValue().equals(m.getLabel()))
					{
						r.setValue(m.getName());
						break;
					}
				}
			}
		}

		// one column is defined by ObservedValue.Investigation,
		// ObservedValue.protocolApplication, ObservedValue.Feature (column
		// 'target' will be moved to ProtocolApplication)

		String sql = "SELECT id from ProtocolApplication ";
		if (count) sql = "SELECT count(*) as id from ProtocolApplication";

		Integer invID = db
				.find(Investigation.class, new QueryRule(Investigation.NAME, Operator.EQUALS, investigationName))
				.get(0).getId();

		for (Measurement m : measurementsUsed)
		{
			sql += " NATURAL JOIN (SELECT ObservedValue.protocolApplication as id, ObservedValue.target as targetId, ObservedValue.value as "
					+ m.getName()
					+ " FROM ObservedValue WHERE ObservedValue.feature = "
					+ m.getId()
					+ ") as "
					+ m.getName();
		}

		// filtering [todo: data model change!]
		if (columnsUsed.contains(targetString))
		{
			sql += " NATURAL JOIN (SELECT id as targetId, name as " + this.targetString
					+ " from ObservationElement) as " + this.targetString;
		}

		sql += " WHERE ProtocolApplication.Investigation=" + invID;

		// System.out.println(sql);

		List<QueryRule> filters = new ArrayList<QueryRule>(getFilters());

		// limit and offset
		if (!count && getLimit() > 0)
		{
			filters.add(new QueryRule(Operator.LIMIT, getLimit()));
		}
		if (!count && getOffset() > 0)
		{
			filters.add(new QueryRule(Operator.OFFSET, getOffset()));
		}

		List<Integer> result = new ArrayList<Integer>();
		// sql = SELECT count (*) as id from ProtocolApplication
		// filters = Scl90som3 = '1'
		// filters.size() = 1

		for (Tuple t : db.sql(sql, filters.toArray(new QueryRule[filters.size()])))
		{
			result.add(t.getInt("id"));
		}

		return result;
	}

	/**
	 * Beware: this function expects a prepared database object with security
	 * and an active connection to be set before it is called!!
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

					// Create a new ProtocolApplication for the new
					// patient.
					ProtocolApplication pa = new ProtocolApplication();

					// Set the protocol to it. At the moment, the
					// reference
					// protocol is hard-coded in the importing. All
					// protocolApplications refer to the same protocol
					// in
					// the mainImporter.
					pa.setProtocol_Name("TestProtocol");

					// Set the name to protocolApplication. The pa name
					// schema should be more flexible later on.
					pa.setName("pa_" + ot.getName());

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
							if (getHashMeasurementsWithCategories(getDb()).containsKey(feature))
							{
								String[] splitValue = value.split("\\.");
								ov.setValue(splitValue[0]);
							}
							else
							{
								ov.setValue(value);
							}
							ov.setProtocolApplication_Name(pa.getName());
							ov.setInvestigation_Name(investigationName);
							listOfNewValues.add(ov);
						}
					}

					ot.setInvestigation_Name(investigationName);

					pa.setInvestigation_Name(investigationName);

					db.add(ot);

					db.add(pa);

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

	/**
	 * Beware: this function expects a prepared database object with security
	 * and an active connection to be set before it is called!!
	 */
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

	/**
	 * Beware: this function expects a prepared database object with security
	 * and an active connection to be set before it is called!!
	 */
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

				Integer ProtocolApplicationID = null;

				if (listOfRemoveValues.get(0).getProtocolApplication_Id() != null)
				{
					ProtocolApplicationID = listOfRemoveValues.get(0).getProtocolApplication_Id();
				}
				db.remove(listOfRemoveValues);
				if (ProtocolApplicationID != null)
				{
					ProtocolApplication pa = db.find(ProtocolApplication.class,
							new QueryRule(ProtocolApplication.ID, Operator.EQUALS, ProtocolApplicationID)).get(0);
					db.remove(pa);
				}

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

	private HashMap<String, String> getHashMeasurementsWithCategories(Database db) throws DatabaseException
	{
		if (hashMeasurementsWithCategories == null)
		{
			hashMeasurementsWithCategories = new HashMap<String, String>();

			List<Measurement> listOM = db.find(Measurement.class);
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

	public void setInvestigation(String investigationName)
	{
		this.investigationName = investigationName;

	}

	private Database _db;

	public void setDb(Database db)
	{
		this._db = db;
	}

	public Database getDb()
	{
		return this._db;
	}
}
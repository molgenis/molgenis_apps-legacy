package org.molgenis.datatable.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.model.elements.Field;
import org.molgenis.util.Entity;
import org.molgenis.util.SimpleTuple;
import org.molgenis.util.Tuple;

/**
 * Wrap a database table (entity) into a TupleTable
 */
public class DatabaseTable implements TupleTable
{
	// database connection
	private Database db;

	// class to query
	private Class<? extends Entity> entityClass;

	// copy of the fields from meta database
	private List<Field> fields;
	
	// current query rules (includes all: limit, offset, paging, filters)
	private List<QueryRule> rules = new ArrayList<QueryRule>();

	/**
	 * Constructor
	 * 
	 * @param database
	 *            containing the entity
	 * @param entityClass
	 *            class of entities to query
	 */
	public DatabaseTable(Database database, Class<? extends Entity> entityClass)
	{
		assert (database != null);
		assert (entityClass != null);

		this.db = database;
		this.entityClass = entityClass;
	}

	@Override
	public List<Field> getColumns() throws TableException
	{
		if (fields != null) return fields;
		try
		{
			fields = db.getMetaData().getEntity(entityClass.getSimpleName()).getAllFields();
			return fields;
		}
		catch (Exception e)
		{
			throw new TableException(e);
		}
	}

	@Override
	public List<Tuple> getRows()
	{
		try
		{
			List<? extends Entity> entities;
			if(rules.size() > 0) entities = db.find(entityClass, rules.toArray(new QueryRule[rules.size()]));
			else entities = db.find(entityClass);
			
			List<Tuple> result = new ArrayList<Tuple>();
			for (Entity e : entities)
			{
				Tuple t = new SimpleTuple();
				for (Field f : getColumns())
				{
					t.set(f.getName(), e.get(f.getName()));
				}
				result.add(t);
			}
			return result;
		}
		catch (Exception e)
		{
			// todo: throw exception
			e.printStackTrace();
			return null;

		}
	}

	@Override
	public Iterator<Tuple> iterator()
	{
		//should be optimized
		return this.getRows().iterator();
	}

	@Override
	public void close() throws TableException
	{
	}

	@Override
	public int getRowCount() throws TableException
	{
		//should get rid of limit clause!
		try
		{
			if(rules.size() > 0) return db.count(entityClass, rules.toArray(new QueryRule[rules.size()]));
			else return db.count(entityClass);
		}
		catch (DatabaseException e)
		{
			throw new TableException(e);
		}
	}

	public List<QueryRule> getFilters()
	{
		return rules;
	}

	public Database getDb()
	{
		return db;
	}

	public void setDb(Database db)
	{
		this.db = db;
	}

}

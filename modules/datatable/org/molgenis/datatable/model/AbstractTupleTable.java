package org.molgenis.datatable.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.model.elements.Field;
import org.molgenis.util.Tuple;

import app.DatabaseFactory;

public abstract class AbstractTupleTable implements TupleTable
{
	private int limit = 0;
	private int offset = 0;
	private int colOffset = 0;
	private int colLimit = 0;
	private List<Field> visibleColumns;
	private Database db;
	private Map<String, Integer> columnByIndex;

	@Override
	public void reset()
	{
		limit = 0;
		offset = 0;
		colOffset = 0;
		colLimit = 0;
	}

	@Override
	public void setVisibleColumnNames(List<String> columnNames)
	{
		List<Field> columns;
		try
		{
			columns = getAllColumns();
		}
		catch (TableException e)
		{
			throw new RuntimeException(e);
		}

		visibleColumns = new ArrayList<Field>(columnNames.size());
		for (Field column : columns)
		{
			if (columnNames.contains(column.getName()))
			{
				visibleColumns.add(column);
			}
		}

	}

	public List<String> getVisibleColumnNames()
	{
		List<Field> visibleColumns;
		try
		{
			visibleColumns = getVisibleColumns();
		}
		catch (TableException e)
		{
			throw new RuntimeException(e);
		}

		List<String> visibleColumnNames = new ArrayList<String>(visibleColumns.size());
		for (Field column : visibleColumns)
		{
			visibleColumnNames.add(column.getName());
		}

		return visibleColumnNames;
	}

	protected List<Field> getVisibleColumns() throws TableException
	{
		if (visibleColumns == null)
		{
			visibleColumns = getAllColumns();
		}

		return visibleColumns;

	}

	@Override
	public int getLimit()
	{
		return limit;
	}

	@Override
	public void setLimit(int limit)
	{
		if (limit < 0) throw new RuntimeException("limit cannot be < 0");
		this.limit = limit;
	}

	@Override
	public int getOffset()
	{
		return offset;
	}

	@Override
	public void setOffset(int offset)
	{
		if (offset < 0) throw new RuntimeException("offset cannot be < 0");
		this.offset = offset;
	}

	@Override
	public abstract List<Field> getAllColumns() throws TableException;

	@Override
	public List<Field> getColumns() throws TableException
	{
		List<Field> result;
		List<Field> columns = getVisibleColumns();

		int colLimit = (int) (getColLimit() == 0 ? getColCount() - getColOffset() : getCurrentColumnPageSize());

		if (getColOffset() > 0)
		{
			if (colLimit > 0)
			{
				result = columns.subList(getColOffset(), Math.min(getColOffset() + colLimit, columns.size()));
			}
			else
			{
				result = columns.subList(getColOffset(), columns.size());
			}
		}
		else
		{
			if (colLimit > 0)
			{
				result = columns.subList(0, colLimit);
			}
			else
			{
				result = columns;
			}
		}

		return result;
	}

	@Override
	public List<Tuple> getRows() throws TableException
	{
		List<Tuple> result = new ArrayList<Tuple>();
		for (Tuple t : this)
		{
			result.add(t);
		}
		return result;
	}

	@Override
	public abstract Iterator<Tuple> iterator();

	@Override
	public void close() throws TableException
	{
		// close resources if applicable
	}

	@Override
	public abstract int getCount() throws TableException;

	@Override
	public int getColCount() throws TableException
	{
		return getVisibleColumns().size();
	}

	@Override
	public void setColLimit(int limit)
	{
		if (limit < 0) throw new RuntimeException("colLimit cannot be < 0");
		this.colLimit = limit;
	}

	@Override
	public int getColLimit()
	{
		return this.colLimit;
	}

	@Override
	public int getColOffset()
	{
		return this.colOffset;
	}

	@Override
	public void setColOffset(int offset)
	{
		if (offset < 0) throw new RuntimeException("colOffset cannot be < 0");
		this.colOffset = offset;
	}

	@Override
	public void setDb(Database db)
	{
		if (db == null) throw new NullPointerException("database cannot be null in setDb(db)");
		this.db = db;
	}

	@Override
	public void setLimitOffset(int limit, int offset)
	{
		this.setLimit(limit);
		this.setOffset(offset);
	}

	public Database getDb()
	{
		try
		{
			db = DatabaseFactory.create();
		}
		catch (DatabaseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return this.db;
	}

	protected int getCurrentColumnPageSize() throws TableException
	{
		int colCount = getColCount();
		int pageSize = getColLimit();

		if (getColOffset() + pageSize > colCount)
		{
			pageSize = colCount - getColOffset();
		}

		return pageSize;
	}

	/**
	 * Please override in subclass if you use the TupleTableIterator !!!!
	 * 
	 * @throws TableException
	 */
	protected Tuple getValues(int row, List<Field> columns) throws TableException
	{
		return getRows().get(row);
	}

	protected int getColumnIndex(String columnName) throws TableException
	{
		if (columnByIndex == null)
		{
			columnByIndex = new HashMap<String, Integer>();

			List<Field> columns = getAllColumns();
			for (int i = 0; i < columns.size(); i++)
			{
				columnByIndex.put(columns.get(i).getName(), i);
			}

		}

		Integer index = columnByIndex.get(columnName);

		if (index == null)
		{
			throw new TableException("Unknown columnName [" + columnName + "]");
		}

		return index;
	}

	protected Field getColumnByName(String columnName) throws TableException
	{
		for (Field field : getAllColumns())
		{
			if (field.getName().equals(columnName))
			{
				return field;
			}
		}

		throw new TableException("Unknown columnName [" + columnName + "]");
	}

	/**
	 * Checks if the column is in the current view port (the columns that the
	 * user sees in the table on the screen)
	 * 
	 * @param columnName
	 * @return
	 * @throws TableException
	 */
	protected boolean isInViewPort(String columnName) throws TableException
	{
		for (Field field : getColumns())
		{
			if (field.getName().equals(columnName))
			{
				return true;
			}
		}

		return false;
	}
}

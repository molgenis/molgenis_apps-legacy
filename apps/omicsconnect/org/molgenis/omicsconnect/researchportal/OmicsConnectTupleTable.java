package org.molgenis.omicsconnect.researchportal;

import java.util.Iterator;
import java.util.List;

import org.molgenis.framework.tupletable.TableException;
import org.molgenis.framework.tupletable.TupleTable;
import org.molgenis.model.elements.Field;
import org.molgenis.util.Tuple;

public class OmicsConnectTupleTable implements TupleTable
{

	@Override
	public void hideColumn(String columnName)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void showColumn(String columnName)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public List<String> getHiddenColumnNames()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setFirstColumnFixed(boolean firstColumnFixed)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isFirstColumnFixed()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Field> getColumns() throws TableException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Field> getAllColumns() throws TableException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Tuple> getRows() throws TableException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<Tuple> iterator()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close() throws TableException
	{
		// TODO Auto-generated method stub

	}

	@Override
	public int getCount() throws TableException
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getColCount() throws TableException
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getLimit()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getColLimit()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setLimit(int limit)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setColLimit(int limit)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public int getOffset()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getColOffset()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setOffset(int offset)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setColOffset(int offset)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void reset()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setLimitOffset(int limit, int offset)
	{
		// TODO Auto-generated method stub

	}

}

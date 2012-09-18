package org.molgenis.datatable.model;

import java.util.Iterator;

import org.molgenis.util.Tuple;

/**
 * Iterator to be used with an AbstractTupleTable. Takes into account limit,
 * offset, colLimit and colOffset of the TupleTable
 * 
 * If you use this make sure to override getValue(row, col) in
 * AbstractTupleTable
 * 
 * @author erwin
 * 
 */
public class TupleTableIterator implements Iterator<Tuple>
{
	private AbstractTupleTable tupleTable;
	private int row = 0;
	private int colLimit;

	public TupleTableIterator(AbstractTupleTable tupleTable) throws TableException
	{
		this.tupleTable = tupleTable;

		colLimit = (int) (tupleTable.getColLimit() == 0 ? tupleTable.getColCount() - tupleTable.getColOffset()
				: tupleTable.getCurrentPageSize());
	}

	@Override
	public boolean hasNext()
	{
		try
		{
			if (tupleTable.getOffset() + row >= tupleTable.getCount()
					|| (tupleTable.getLimit() > 0 && row >= tupleTable.getLimit()))
			{
				return false;
			}

			return true;
		}
		catch (TableException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public Tuple next()
	{

		Tuple tuple;
		try
		{
			tuple = tupleTable.getValues(tupleTable.getOffset() + row, tupleTable.getColOffset(),
					tupleTable.getColOffset() + colLimit);
		}
		catch (TableException e)
		{
			throw new RuntimeException(e);
		}

		row++;

		return tuple;
		/*
		 * Tuple tuple = new SimpleTuple();
		 * 
		 * for (int col = tupleTable.getColOffset(); col <
		 * tupleTable.getColOffset() + colLimit; col++) { try {
		 * tuple.set(tupleTable.getAllColumns().get(col).getName(),
		 * tupleTable.getValue(tupleTable.getOffset() + row, col)); } catch
		 * (TableException e) { throw new RuntimeException(e); } }
		 * 
		 * row++;
		 * 
		 * return tuple;
		 */
	}

	@Override
	public void remove()
	{
		throw new UnsupportedOperationException();
	}

}

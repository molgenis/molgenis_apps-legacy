package org.molgenis.datatable.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.molgenis.framework.db.QueryRule;
import org.molgenis.model.elements.Field;
import org.molgenis.util.SimpleTuple;
import org.molgenis.util.Tuple;
import org.molgenis.util.plink.datatypes.MapEntry;
import org.molgenis.util.plink.datatypes.PedEntry;
import org.molgenis.util.plink.drivers.CachingPedFileDriver;
import org.molgenis.util.plink.drivers.MapFileDriver;
import org.molgenis.util.plink.drivers.PedFileDriver;

public class PedMapTupleTable extends AbstractTupleTable implements FilterableTupleTable
{
	private CachingPedFileDriver pedFile;
	private MapFileDriver mapFile;
	private List<Field> columns = null;
	private List<QueryRule> filters;
	private List<String> snpNames = new ArrayList<String>();

	private static String[] fixedColumns = new String[]
	{ "IndividualID", "FamilyID", "FatherID", "MotherID", "Sex", "Phenotype" };

	public PedMapTupleTable(File ped, File map) throws Exception
	{
		this.mapFile = new MapFileDriver(map);
		this.pedFile = new CachingPedFileDriver(ped);

		for (MapEntry me : mapFile.getAllEntries())
		{
			snpNames.add(me.getSNP());
		}
	}

	@Override
	public List<Field> getAllColumns() throws TableException
	{
		try
		{
			if (columns == null)
			{
				columns = new ArrayList<Field>();
				for (String columnName : getColumnNames())
				{
					columns.add(new Field(columnName));
				}
			}
		}
		catch (Exception e)
		{
			throw new TableException(e);
		}

		return columns;
	}

	private List<String> getColumnNames()
	{
		List<String> columns = new ArrayList<String>(Arrays.asList(fixedColumns));
		columns.addAll(snpNames);

		return columns;
	}

	@Override
	public Iterator<Tuple> iterator()
	{
		return new PedMapIterator(pedFile, mapFile, this);
	}

	@Override
	public int getCount() throws TableException
	{
		return (int) pedFile.getNrOfElements();
	}

	@Override
	public int getColCount() throws TableException
	{
		return (int) (mapFile.getNrOfElements() + fixedColumns.length);
	}

	private static class PedMapIterator implements Iterator<Tuple>
	{
		int count = 0;

		// source data
		PedFileDriver pedFile;
		List<String> columns;

		// wrapper state
		PedMapTupleTable table;

		// colLimit
		int colLimit;

		PedMapIterator(PedFileDriver pedFile, MapFileDriver mapFile, PedMapTupleTable table)
		{
			this.pedFile = pedFile;
			this.table = table;

			columns = table.getColumnNames();

			// TODO FIX MapFile.getNrOfElements
			colLimit = (int) (table.getColLimit() == 0 ? mapFile.getNrOfElements() + fixedColumns.length
					- table.getColOffset() : table.getColLimit());
		}

		@Override
		public boolean hasNext()
		{
			if (table.getOffset() + count >= pedFile.getNrOfElements()
					|| (table.getLimit() > 0 && count >= table.getLimit()))
			{
				return false;
			}

			return true;
		}

		@Override
		public Tuple next()
		{
			if (!hasNext())
			{
				throw new UnsupportedOperationException("No next element exists");
			}

			try
			{
				int index = table.getOffset() + count;
				List<PedEntry> pedEntries = pedFile.getEntries(index, index + 1);
				PedEntry pe = pedEntries.get(0);

				Tuple result = new SimpleTuple();

				for (int i = table.getColOffset(); i < table.getColOffset() + colLimit; i++)
				{
					Object value;

					switch (i)
					{
						case 0:
							value = pe.getIndividual();
							break;
						case 1:
							value = pe.getFamily();
							break;
						case 2:
							value = pe.getFather();
							break;
						case 3:
							value = pe.getMother();
							break;
						case 4:
							value = pe.getSex();
							break;
						case 5:
							value = pe.getPhenotype();
							break;
						default:
							value = pe.getBialleles().get(i - fixedColumns.length).toString();

					}

					result.set(columns.get(i), value);
				}

				count++;

				return result;
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
		}

		@Override
		public void remove()
		{
			System.out.println("REMOVE");
			// TODO Auto-generated method stub
		}

	}

	@Override
	public void setFilters(List<QueryRule> rules) throws TableException
	{
		this.filters = rules;
		pedFile.setFilters(rules, snpNames);
	}

	@Override
	public List<QueryRule> getFilters()
	{
		return this.filters;
	}

	@Override
	public QueryRule getSortRule()
	{
		return null;
	}

}

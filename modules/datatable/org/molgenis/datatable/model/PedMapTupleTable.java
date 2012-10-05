package org.molgenis.datatable.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.molgenis.framework.db.QueryRule;
import org.molgenis.model.elements.Field;
import org.molgenis.util.SimpleTuple;
import org.molgenis.util.Tuple;
import org.molgenis.util.plink.datatypes.MapEntry;
import org.molgenis.util.plink.datatypes.PedEntry;
import org.molgenis.util.plink.drivers.CachingPedFileDriver;
import org.molgenis.util.plink.drivers.MapFileDriver;

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
	public int getCount() throws TableException
	{
		return (int) pedFile.getNrOfElements();
	}

	@Override
	protected Tuple getValues(int row, List<Field> columns) throws TableException
	{
		List<PedEntry> pedEntries;
		try
		{
			pedEntries = pedFile.getEntries(row, row + 1);
		}
		catch (Exception e)
		{
			throw new TableException(e);
		}

		PedEntry pe = pedEntries.get(0);

		Tuple result = new SimpleTuple();

		for (Field column : columns)
		{
			int col = getColumnIndex(column.getName());
			Object value;

			switch (col)
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
					value = pe.getBialleles().get(col - fixedColumns.length).toString();

			}

			result.set(column.getName(), value);
		}

		return result;
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

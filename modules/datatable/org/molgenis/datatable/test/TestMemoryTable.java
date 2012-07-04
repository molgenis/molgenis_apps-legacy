package org.molgenis.datatable.test;

import java.util.List;

import org.molgenis.datatable.model.TableException;
import org.molgenis.datatable.model.TupleTable;
import org.molgenis.util.Tuple;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class TestMemoryTable
{
	List<Tuple> rows;
	TupleTable table;
	
	@BeforeClass
	public void setup()
	{
		table = MemoryTableFactory.create();
	}
	
	@Test
	public void test1() throws TableException
	{
		//check columns
		Assert.assertEquals("firstName", table.getColumns().get(0).getName());
		Assert.assertEquals("lastName", table.getColumns().get(1).getName());
		
		//check rows
		Assert.assertEquals(5, table.getRows().size());
		
		//check iterator
		int i = 1;
		for(Tuple row: table)
		{
			Assert.assertEquals(2, row.getFieldNames().size());
			
			Assert.assertEquals(true, row.getFieldNames().contains("firstName"));
			Assert.assertEquals(true, row.getFieldNames().contains("lastName"));
			
			Assert.assertEquals(row.getObject("firstName"),"first"+i);
			Assert.assertEquals(row.getObject("lastName"),"last"+i);
			
			i=i+1;
		}
	}
	
	@Test
	public void testLimitOffset() throws TableException
	{
		table.setLimitOffset(2, 3);
		
		//limit == 2
		Assert.assertEquals(table.getRows().size(), 2);
		
		//offset = 3, so we skip first1-first3 and expect first4
		Assert.assertEquals(table.getRows().get(0).getString("firstName"), "first4");
		
		//remove filters again
		table.setLimitOffset(0, 0);
	}
}

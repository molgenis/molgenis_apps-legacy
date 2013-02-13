package external;

import app.JDBCMetaDatabase;
import org.molgenis.fieldtypes.MrefField;
import org.molgenis.fieldtypes.XrefField;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.model.MolgenisModelException;
import org.molgenis.model.elements.Entity;
import org.molgenis.model.elements.Field;
import org.molgenis.model.elements.Model;
import org.molgenis.io.csv.CsvWriter;
import org.molgenis.util.tuple.KeyValueTuple;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;

public class ModelToExcel
{
	public static void main(String[] args) throws DatabaseException, IOException, MolgenisModelException
	{
		Model m = new JDBCMetaDatabase();

		System.out.println(write(m));
	}

	public static String write(Model m)
	{
		StringWriter strWriter = new StringWriter();
		CsvWriter csvWriter = new CsvWriter(strWriter);
		try
		{
			write(m, csvWriter);
		}
		catch (MolgenisModelException e)
		{
			throw new RuntimeException(e);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
		finally
		{
			try
			{
				csvWriter.close();
			}
			catch (IOException e)
			{
			}
		}
		return strWriter.toString();
	}

	public static void write(Model m, CsvWriter w) throws MolgenisModelException, IOException
	{
		w.writeColNames(Arrays.asList("module", "entity", "field", "type", "nillable", "xref", "description"));

		for (Entity e : m.getEntities())
			if (!e.isAbstract())
			{
				KeyValueTuple tuple = new KeyValueTuple();
				if (e.getModule() != null) tuple.set("module", e.getModule().getName());
				tuple.set("entity", e.getName());
				tuple.set("field", "====");
				tuple.set("type", e.getAncestor() != null ? e.getAncestor().getName() : "");
				tuple.set("nillable", "====");
				tuple.set("xref", "====");
				tuple.set("description", e.getDescription());

				w.write(tuple);

				for (Field f : e.getAllFields())
				{
					tuple.set("field", f.getName());
					tuple.set("type", f.getType());
					tuple.set("nillable", f.isNillable());
					tuple.set("description", f.getDescription());
					tuple.set("xref", null);

					if (f.getType() instanceof XrefField || f.getType() instanceof MrefField)
					{
						tuple.set("xref", f.getXrefEntityName());
					}

					w.write(tuple);
				}
			}
	}
}

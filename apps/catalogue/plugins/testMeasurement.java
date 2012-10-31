package plugins;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.pheno.Measurement;

import app.DatabaseFactory;

public class testMeasurement
{

	public static void main(String args[]) throws DatabaseException
	{

		Database db = DatabaseFactory.create();

		Measurement m = new Measurement();

		m.setName("testName");

		db.add(m);

		db.remove(m);

	}

}

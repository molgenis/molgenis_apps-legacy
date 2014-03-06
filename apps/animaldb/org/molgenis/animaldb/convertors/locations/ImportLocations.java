package org.molgenis.animaldb.convertors.locations;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.molgenis.animaldb.commonservice.CommonService;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.security.Login;
import org.molgenis.pheno.Location;
import org.molgenis.util.CsvFileReader;
import org.molgenis.util.Tuple;

public class ImportLocations
{
	private Database db;
	private CommonService ct;
	private List<String> seenLocs = new ArrayList<String>();
	private String userName;

	public ImportLocations(Database db, Login login) throws Exception
	{
		this.db = db;
		ct = CommonService.getInstance();
		ct.setDatabase(this.db);
		userName = login.getUserName();
	}

	public void doImport(String filename) throws Exception
	{
		final String investigationName = ct.getOwnUserInvestigationNames(userName).get(0);

		File file = new File(filename);
		CsvFileReader reader = new CsvFileReader(file);
		for (Tuple tuple : reader)
		{
			// gebouw nr -> name of building
			String newLocString = tuple.getString("Location");
			if (!seenLocs.contains(newLocString))
			{
				seenLocs.add(newLocString);
				Location newLocation = new Location();
				newLocation.setName(newLocString);
				newLocation.setInvestigation_Name(investigationName);
				db.add(newLocation);
			}
		}
	}
}

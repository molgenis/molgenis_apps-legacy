package plugins.archiveexportimport;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.Database.DatabaseAction;
import org.molgenis.framework.db.EntitiesImporter;

import app.EntitiesImporterImpl;

public class XgapCsvImport
{
	public XgapCsvImport(File extractDir, Database db, boolean skipWhenDestExists) throws Exception
	{

		db.beginTx();

		try
		{
			EntitiesImporter entitiesImporter = new EntitiesImporterImpl(db);
			entitiesImporter.importEntities(Arrays.asList(extractDir.listFiles()), DatabaseAction.ADD);

			File investigationFile = new File(extractDir + File.separator + "study.txt");
			List<String> investigationNames = XgapCommonImport.getInvestigationNameFromFile(investigationFile);

			XgapCommonImport.importMatrices(investigationNames, db, false, new File(extractDir + File.separator
					+ "data"), skipWhenDestExists);

			db.commitTx();
		}
		catch (Exception e)
		{
			db.rollbackTx();
			throw (e);
		}

	}

}

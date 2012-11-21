package org.molgenis.omx.plugins;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.Database.DatabaseAction;
import org.molgenis.omx.dataset.DataSetImporter;
import org.molgenis.util.SimpleTuple;
import org.molgenis.util.Tuple;

import app.ExcelImport;

public class ImportFileWizardPage extends WizardPage
{
	public ImportFileWizardPage()
	{
		super("Import", "ImportFileWizardPage.ftl");
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		String entityImportOption = request.getString("entity_option");

		if (entityImportOption != null)
		{
			doImport(db, entityImportOption);
		}
	}

	private void doImport(Database db, String entityAction)
	{
		ImportWizard importWizard = getWizard();

		try
		{
			// convert input to database action
			DatabaseAction entityDbAction = toDatabaseAction(entityAction);
			if (entityDbAction == null) throw new IOException("unknown database action: " + entityAction);

			// import entities
			ExcelImport.importAll(importWizard.getFile(), db, new SimpleTuple(), null, entityDbAction, "", true);

			// import dataset instances
			if (importWizard.getDataImportable() != null)
			{
				List<String> dataSetSheetNames = new ArrayList<String>();
				for (Entry<String, Boolean> entry : importWizard.getDataImportable().entrySet())
					if (entry.getValue() == true) dataSetSheetNames.add("dataset_" + entry.getKey());

				new DataSetImporter(db).importXLS(importWizard.getFile(), dataSetSheetNames);
			}

			importWizard.setSuccessMessage("File successfully imported.");
		}
		catch (Exception e)
		{
			logger.warn("Import of file [" + importWizard.getFile().getName() + "] failed for action [" + entityAction
					+ "]", e);
			importWizard.setValidationMessage("<b>Your import failed:</b><br />" + e.getMessage());
		}
	}

	private DatabaseAction toDatabaseAction(String actionStr)
	{
		// convert input to database action
		DatabaseAction dbAction;

		if (actionStr.equals("add")) dbAction = DatabaseAction.ADD;
		else if (actionStr.equals("add_ignore")) dbAction = DatabaseAction.ADD_IGNORE_EXISTING;
		else if (actionStr.equals("add_update")) dbAction = DatabaseAction.ADD_UPDATE_EXISTING;
		else if (actionStr.equals("update")) dbAction = DatabaseAction.UPDATE;
		else if (actionStr.equals("update_ignore")) dbAction = DatabaseAction.UPDATE_IGNORE_MISSING;
		else
			dbAction = null;

		return dbAction;
	}

}

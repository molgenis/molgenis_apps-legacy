package org.molgenis.load.ui;

import java.io.File;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.server.MolgenisRequest;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.util.Tuple;

import app.CsvImport;

/**
 * LoadFromDirectoryController takes care of all user requests and application
 * logic.
 * 
 * <li>Each user request is handled by its own method based action=methodName.
 * <li>MOLGENIS takes care of db.commits and catches exceptions to show to the
 * user <li>LoadFromDirectoryModel holds application state and business logic on
 * top of domain model. Get it via this.getModel()/setModel(..) <li>
 * LoadFromDirectoryView holds the template to show the layout. Get/set it via
 * this.getView()/setView(..).
 */
public class LoadFromDirectory extends EasyPluginController<LoadFromDirectoryModel>
{
	// needed for serialization
	private static final long serialVersionUID = -8519723133998268849L;

	public LoadFromDirectory(String name, ScreenController<?> parent)
	{
		super(name, parent);
		this.setModel(new LoadFromDirectoryModel(this)); // the default model
		// this.setView(new LoadFromDirectoryView(this.getModel())); // <plugin
		// flavor="freemarker"
	}

	public void loadDirectory(Database db, MolgenisRequest request) throws Exception
	{
		File directory = new File(request.getString("directory"));

		// If there is a target dir import that one first, otherwise we maybe
		// can't import the ObservedValues because they need a target
		File targetDir = new File(directory, "target");
		if (targetDir.exists() && targetDir.isDirectory())
		{
			CsvImport.importAll(targetDir, db, null);
		}

		CsvImport.importAll(directory, db, null);
	}

	@Override
	public void reload(Database db) throws Exception
	{
		// TODO Auto-generated method stub

	}

	@Override
	public ScreenView getView()
	{
		return new LoadFromDirectoryView(this.getModel());
	}
}
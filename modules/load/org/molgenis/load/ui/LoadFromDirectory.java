package org.molgenis.load.ui;

import java.io.File;
import java.util.Arrays;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.Database.DatabaseAction;
import org.molgenis.framework.server.MolgenisRequest;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenView;

import app.EntitiesImporterImpl;

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
		new EntitiesImporterImpl(db).importEntities(Arrays.asList(directory.listFiles()),
				DatabaseAction.ADD_IGNORE_EXISTING);
	}

	@Override
	public void reload(Database db) throws Exception
	{
		// noop
	}

	@Override
	public ScreenView getView()
	{
		return new LoadFromDirectoryView(this.getModel());
	}
}
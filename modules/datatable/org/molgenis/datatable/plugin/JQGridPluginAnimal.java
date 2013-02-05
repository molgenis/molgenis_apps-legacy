package org.molgenis.datatable.plugin;

import java.io.OutputStream;

import org.molgenis.datatable.model.AnimalDBTable;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.server.MolgenisRequest;
import org.molgenis.framework.tupletable.view.JQGridView;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.framework.ui.html.MolgenisForm;
import org.molgenis.util.HandleRequestDelegationException;

public class JQGridPluginAnimal extends EasyPluginController<JQGridPluginAnimal>
{

	private static final long serialVersionUID = 1L;
	JQGridView tableView;

	public JQGridPluginAnimal(String name, ScreenController<?> parent)
	{
		super(name, parent);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void reload(Database db) throws Exception
	{

		AnimalDBTable table = new AnimalDBTable(db);
		tableView = new JQGridView("test", this, table);

	}

	public void download_json_test(Database db, MolgenisRequest request, OutputStream out)
			throws HandleRequestDelegationException
	{
		// handle requests for the table named 'test'

		tableView.handleRequest(db, request, out);

	}

	// what is shown to the user
	@Override
	public ScreenView getView()
	{
		MolgenisForm view = new MolgenisForm(this);

		view.add(tableView);

		return view;
	}

}

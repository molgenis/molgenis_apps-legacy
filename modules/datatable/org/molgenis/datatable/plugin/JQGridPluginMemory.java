package org.molgenis.datatable.plugin;

import java.io.OutputStream;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.server.MolgenisRequest;
import org.molgenis.framework.tupletable.TupleTable;
import org.molgenis.framework.tupletable.impl.MemoryTableFactory;
import org.molgenis.framework.tupletable.view.JQGridView;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.framework.ui.html.MolgenisForm;
import org.molgenis.util.HandleRequestDelegationException;

/** Simple plugin that only shows a data table for testing */
public class JQGridPluginMemory extends EasyPluginController<JQGridPluginMemory>
{
	private static final long serialVersionUID = 1L;

	JQGridView tableView;

	public JQGridPluginMemory(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public void reload(Database db)
	{

		// only this line changed ...
		final TupleTable table = MemoryTableFactory.create(51, 10);
		tableView = new JQGridView("test", this, table);
		tableView.setLabel("<b>Table:</b>Testing using the MemoryTupleTable");

	}

	// handling of the ajax; should be auto-wired via the JQGridTableView
	// contructor (TODO)
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
		final MolgenisForm view = new MolgenisForm(this);

		view.add(tableView);

		return view;
	}
}
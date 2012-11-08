package org.molgenis.omicsconnect.dataset;

import java.io.OutputStream;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.tupletable.TableException;
import org.molgenis.framework.tupletable.view.JQGridView;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.FormController;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.framework.ui.html.MolgenisForm;
import org.molgenis.observ.DataSet;
import org.molgenis.util.HandleRequestDelegationException;
import org.molgenis.util.Tuple;

/** Simple plugin that only shows a data table for testing */
public class DataSetViewerPlugin extends EasyPluginController<DataSetViewerPlugin>
{
	private static final long serialVersionUID = 1L;

	JQGridView tableView;

	public DataSetViewerPlugin(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public void reload(Database db)
	{
		// get from parent
		FormController<DataSet> parent = (FormController<DataSet>) this.getParent();

		// get current dataset in a table
		DataSetTable table;
		try
		{
			table = new DataSetTable(parent.getModel().getCurrent(), db);

			tableView = new JQGridView("test", this, table);
		}
		catch (TableException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// handling of the ajax; should be auto-wired via the JQGridTableView
	// contructor (TODO)
	public void download_json_test(Database db, Tuple request, OutputStream out)
			throws HandleRequestDelegationException
	{
		// handle requests for the table named 'test'
		tableView.handleRequest(db, request, out);
	}

	// what is shown to the user
	public ScreenView getView()
	{
		MolgenisForm view = new MolgenisForm(this);

		view.add(tableView);

		return view;
	}
}
package matrix.datatable;

import matrix.DataMatrixInstance;
import matrix.general.DataMatrixHandler;

import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.tupletable.view.JQGridView;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.FormController;
import org.molgenis.framework.ui.FormModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.framework.ui.html.MolgenisForm;

/**
 * View data in a matrix.
 */
public class JQGridPlugin_xQTL extends EasyPluginController<JQGridPlugin_xQTL>
{
	private DataMatrixHandler dmh = null;
	private JQGridView tableView;

	public JQGridPlugin_xQTL(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public void reload(Database db)
	{
		ScreenController<?> parentController = (ScreenController<?>) this.getParent().getParent();
		FormModel<Data> parentForm = (FormModel<Data>) ((FormController) parentController).getModel();
		Data data = parentForm.getRecords().get(0);

		if (this.dmh == null)
		{
			dmh = new DataMatrixHandler(db);
		}

		try
		{
			DataMatrixInstance m = dmh.createInstance(data, db);
			BinaryTupleTable btt = new BinaryTupleTable(m.getAsFile());
			tableView = new JQGridView("test", this, btt);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			this.setError(e.getMessage());
		}
	}

	@Override
	public ScreenView getView()
	{
		MolgenisForm view = new MolgenisForm(this);

		view.add(tableView);

		return view;
	}

}
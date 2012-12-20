package org.molgenis.datatable.plugin;

import java.io.OutputStream;
import java.util.List;

import org.molgenis.datatable.model.ProtocolTable;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.server.MolgenisRequest;
import org.molgenis.framework.tupletable.TableException;
import org.molgenis.framework.tupletable.TupleTable;
import org.molgenis.framework.tupletable.view.JQGridView;
import org.molgenis.framework.tupletable.view.JQGridViewCallback;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.framework.ui.html.MolgenisForm;
import org.molgenis.model.elements.Field;
import org.molgenis.pheno.Measurement;
import org.molgenis.util.HandleRequestDelegationException;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/** Simple plugin that only shows a data table for testing */
public class JQGridPluginProtocol extends EasyPluginController<JQGridPluginProtocol> implements JQGridViewCallback
{
	private static final long serialVersionUID = 1678403545717313675L;
	private JQGridView tableView;
	String topProtocol = null;
	String target = null;

	public JQGridPluginProtocol(String name, ScreenController<?> parent, String topProtocol, String target)
	{
		super(name, parent);
		this.topProtocol = topProtocol;
		this.target = target;
	}

	@Override
	public void reload(Database db)
	{
		// need to (re) load the table
		try
		{
			// // only this line changed ...
			// Protocol p = null;
			//
			// if (topProtocol != null)
			// {
			// p = db.query(Protocol.class).eq(Protocol.NAME,
			// topProtocol).find().get(0);
			// }
			// else
			// {
			//
			// p = new Protocol();
			// p.setName("topProtocol");
			// for (Protocol subProtocol : db.find(Protocol.class))
			// {
			// List<Integer> subProtocolIds = p.getSubprotocols_Id();
			// subProtocolIds.add(subProtocol.getId());
			// p.setSubprotocols_Id(subProtocolIds);
			// }
			// }

			// create table
			ProtocolTable table = new ProtocolTable("ChaoRoan");
			table.setTargetString(target);
			table.setFirstColumnFixed(true);
			// add editable decorator

			// check which table to show
			tableView = new JQGridView("test", this, table);
			tableView.setLabel("Phenotypes: <select><option>Roan</option><option>Kanninga</option></select>");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			setError(e.getMessage());
		}
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
		MolgenisForm view = new MolgenisForm(this);

		view.add(tableView);

		return view;
	}

	@Override
	public void beforeLoadConfig(MolgenisRequest request, TupleTable tupleTable)
	{
		@SuppressWarnings("unchecked")
		List<Measurement> selectedMeasurements = (List<Measurement>) request.getRequest().getSession()
				.getAttribute("selectedMeasurements");

		if (selectedMeasurements != null)
		{
			try
			{
				for (final Field field : tupleTable.getAllColumns())
				{
					Measurement measurement = Iterables.find(selectedMeasurements, new Predicate<Measurement>()
					{
						@Override
						public boolean apply(Measurement m)
						{
							return m.getName().equals(field.getName());
						}

					}, null);

					if (measurement == null)
					{
						tupleTable.hideColumn(field.getName());
					}
				}
			}
			catch (TableException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
}
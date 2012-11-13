package org.molgenis.omicsconnect.dataset;

import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.server.MolgenisRequest;
import org.molgenis.framework.tupletable.TableException;
import org.molgenis.framework.tupletable.TupleTable;
import org.molgenis.framework.tupletable.view.JQGridView;
import org.molgenis.framework.tupletable.view.JQGridViewCallback;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.FormController;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.framework.ui.html.MolgenisForm;
import org.molgenis.model.elements.Field;
import org.molgenis.observ.DataSet;
import org.molgenis.observ.ObservableFeature;
import org.molgenis.util.HandleRequestDelegationException;
import org.molgenis.util.Tuple;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/** Simple plugin that only shows a data table for testing */
public class DataSetViewerPlugin extends EasyPluginController<DataSetViewerPlugin> implements JQGridViewCallback
{
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(DataSetViewerPlugin.class);

	JQGridView tableView;

	public DataSetViewerPlugin(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public void reload(Database db)
	{
		// get from parent
		@SuppressWarnings("unchecked")
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
			logger.error("TableException in reload of DataSetViewPlugin", e);
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

	@Override
	public void beforeLoadConfig(MolgenisRequest request, TupleTable tupleTable)
	{
		HttpSession session = request.getRequest().getSession();

		// Check if there are selected observable features (from the catalogue
		// viewer)
		@SuppressWarnings("unchecked")
		List<ObservableFeature> selectedObservableFeatures = (List<ObservableFeature>) session
				.getAttribute("selectedObservableFeatures");

		if (selectedObservableFeatures != null)
		{
			try
			{
				for (final Field field : tupleTable.getAllColumns())
				{
					ObservableFeature observableFeature = Iterables.find(selectedObservableFeatures,
							new Predicate<ObservableFeature>()
							{
								@Override
								public boolean apply(ObservableFeature of)
								{
									return of.getIdentifier().equals(field.getName());
								}

							}, null);

					if (observableFeature == null)
					{
						tupleTable.hideColumn(field.getName());
					}
					else
					{
						tupleTable.showColumn(field.getName());
					}
				}

				session.removeAttribute("selectedObservableFeatures");
			}
			catch (TableException e)
			{
				logger.error("TableException setting selected ObservableFeatures", e);
			}

		}
	}
}
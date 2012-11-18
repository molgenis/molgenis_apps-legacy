package org.molgenis.omx.dataset;

import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.server.MolgenisRequest;
import org.molgenis.framework.tupletable.TableException;
import org.molgenis.framework.tupletable.view.JQGridView;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.framework.ui.html.MolgenisForm;
import org.molgenis.model.elements.Field;
import org.molgenis.observ.DataSet;
import org.molgenis.observ.ObservableFeature;
import org.molgenis.omx.view.DataSetChooser;
import org.molgenis.util.HandleRequestDelegationException;
import org.molgenis.util.Tuple;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/** Simple plugin that only shows a data table for testing */
public class DataSetViewerPlugin extends EasyPluginController<DataSetViewerPlugin>
{
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(DataSetViewerPlugin.class);
	private JQGridView tableView;
	private DataSetChooser dataSetChooser;

	public DataSetViewerPlugin(String name, ScreenController<?> parent)
	{
		super(name, parent);
		setModel(this);
	}

	@Override
	public void reload(Database db)
	{
		if (tableView == null)
		{
			createViews(db, null, null);
		}
	}

	// handling of the ajax; should be auto-wired via the JQGridTableView
	// contructor (TODO)
	public void download_json_dataset(Database db, Tuple request, OutputStream out)
			throws HandleRequestDelegationException
	{
		// handle requests for the table named 'dataset'
		tableView.handleRequest(db, request, out);
	}

	public void selectDataSet(Database db, Tuple request) throws HandleRequestDelegationException
	{
		Integer selectedDataSetId = request.getInt("dataSetId");
		HttpSession session = ((MolgenisRequest) request).getRequest().getSession();

		if (selectedDataSetId != null)
		{
			@SuppressWarnings("unchecked")
			List<ObservableFeature> selectedObservableFeatures = (List<ObservableFeature>) session
					.getAttribute("selectedObservableFeatures");

			createViews(db, selectedDataSetId, selectedObservableFeatures);

			session.removeAttribute("selectedObservableFeatures");
		}

	}

	private void createViews(Database db, Integer selectedDataSetId, List<ObservableFeature> selectedObservableFeatures)
	{
		try
		{
			List<DataSet> dataSets = db.find(DataSet.class);

			if ((dataSets != null) && !dataSets.isEmpty())
			{
				DataSet dataSet = null;
				if (selectedDataSetId != null)
				{
					dataSet = db.findById(DataSet.class, selectedDataSetId);
				}

				if (dataSet == null)
				{
					dataSet = dataSets.get(0);
					selectedDataSetId = dataSet.getId();
				}

				DataSetTable table = new DataSetTable(dataSet, db);

				if (selectedObservableFeatures != null)
				{
					for (final Field field : table.getAllColumns())
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
							table.hideColumn(field.getName());
						}
						else
						{
							table.showColumn(field.getName());
						}
					}
				}

				tableView = new JQGridView("dataset", this, table);
				dataSetChooser = new DataSetChooser(dataSets, selectedDataSetId);
			}
		}
		catch (TableException e)
		{
			logger.error("TableException creating views");
			throw new RuntimeException(e);
		}
		catch (DatabaseException e)
		{
			logger.error("TableException creating views");
			throw new RuntimeException(e);
		}
	}

	/**
	 * This is for the dataset chooser
	 */
	@Override
	public String getCustomHtmlHeaders()
	{
		StringBuilder s = new StringBuilder();
		s.append("<link rel=\"stylesheet\" href=\"bootstrap/css/bootstrap.min.css\" type=\"text/css\" />");
		s.append("<script type=\"text/javascript\" src=\"bootstrap/js/bootstrap.min.js\"></script>");

		return s.toString();
	}

	// what is shown to the user
	public ScreenView getView()
	{
		MolgenisForm view = new MolgenisForm(this);
		view.add(dataSetChooser);
		view.add(tableView);

		return view;
	}

}
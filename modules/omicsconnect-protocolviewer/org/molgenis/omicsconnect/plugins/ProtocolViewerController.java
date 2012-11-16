package org.molgenis.omicsconnect.plugins;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.server.MolgenisRequest;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.observ.Category;
import org.molgenis.observ.DataSet;
import org.molgenis.observ.ObservableFeature;
import org.molgenis.observ.Protocol;
import org.molgenis.omicsconnect.EMeasureFeatureWriter;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;
import org.molgenis.util.XlsWriter;

import app.ui.DataSetViewerPlugin;

import com.google.gson.Gson;

/**
 * Protocol viewer controller
 */
public class ProtocolViewerController extends PluginModel<Entity>
{
	private static final long serialVersionUID = -6143910771849972946L;
	/** Protocol viewer model */
	private ProtocolViewer protocolViewer;

	public ProtocolViewerController(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	public ProtocolViewer getMyModel()
	{
		return protocolViewer;
	}

	@Override
	public String getViewName()
	{
		return ProtocolViewer.class.getSimpleName();
	}

	@Override
	public String getViewTemplate()
	{
		return ProtocolViewer.class.getName().replace('.', '/') + ".ftl";
	}

	@Override
	public String getCustomHtmlHeaders()
	{
		// TODO move to head section in view
		StringBuilder s = new StringBuilder();
		s.append("<link rel=\"stylesheet\" href=\"bootstrap/css/bootstrap.min.css\" type=\"text/css\" />");
		s.append("<script type=\"text/javascript\" src=\"bootstrap/js/bootstrap.min.js\"></script>");
		s.append("<link rel=\"stylesheet\" href=\"res/jquery-plugins/dynatree/skin-vista/ui.dynatree.css\" type=\"text/css\" />");
		s.append("<script type=\"text/javascript\" src=\"res/jquery-plugins/dynatree/jquery.dynatree.min.js\"></script>");
		s.append("<script type=\"text/javascript\" src=\"res/scripts/protocolviewer.js\"></script>");
		s.append("<link rel=\"stylesheet\" href=\"res/css/protocolviewer.css\" type=\"text/css\" />");
		return s.toString();
	}

	@Override
	public Show handleRequest(Database db, Tuple request, OutputStream out) throws Exception
	{
		if (out == null)
		{
			this.handleRequest(db, request);
			return Show.SHOW_MAIN;
		}

		Object src = null;
		if (request.getAction().equals("download_json_getdataset"))
		{
			Integer dataSetId = request.getInt("datasetid");
			List<DataSet> dataSets = db.find(DataSet.class, new QueryRule(DataSet.ID, Operator.EQUALS, dataSetId));
			if (dataSets != null && !dataSets.isEmpty()) src = toJSDataSet(db, dataSets.get(0));
		}
		else if (request.getAction().equals("download_json_getfeature"))
		{
			Integer featureId = request.getInt("featureid");
			List<ObservableFeature> features = db.find(ObservableFeature.class, new QueryRule(ObservableFeature.ID,
					Operator.EQUALS, featureId));
			if (features != null && !features.isEmpty()) src = toJSFeature(db, features.get(0));
		}
		else
		{
			throw new RuntimeException("unknown action: " + request.getAction());
		}

		// serialize object to json
		if (src != null)
		{
			Writer writer = new OutputStreamWriter(out, Charset.forName("UTF-8"));
			try
			{
				new Gson().toJson(src, writer);
			}
			finally
			{
				writer.close();
			}
		}
		return Show.SHOW_MAIN;
	}

	@Override
	public void handleRequest(Database db, Tuple request) throws Exception
	{
		MolgenisRequest req = (MolgenisRequest) request;
		HttpServletResponse response = req.getResponse();

		// get data set
		Integer dataSetId = request.getInt("datasetid");
		DataSet dataSet = null;
		List<DataSet> dataSets = db.find(DataSet.class, new QueryRule(DataSet.ID, Operator.EQUALS, dataSetId));
		if (dataSets != null && !dataSets.isEmpty()) dataSet = dataSets.get(0);

		// get features
		String featuresStr = request.getString("features");
		List<ObservableFeature> features = null;
		if (featuresStr != null && !featuresStr.isEmpty())
		{
			String[] featuresStrArr = request.getString("features").split(",");
			List<Integer> featureIds = new ArrayList<Integer>(featuresStrArr.length);
			for (String featureStr : featuresStrArr)
				featureIds.add(Integer.valueOf(featureStr));
			features = findFeatures(db, featureIds);
		}

		if (request.getAction().equals("download_emeasure"))
		{

			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH.mm");
			String fileName = "EMeasure_" + dateFormat.format(new Date()) + ".xml";

			// write response headers
			response.setContentType("application/x-download");
			response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

			// write eMeasure XML file
			EMeasureFeatureWriter eMeasureWriter = new EMeasureFeatureWriter(response.getOutputStream());
			try
			{
				eMeasureWriter.writeFeatures(features);
			}
			finally
			{
				eMeasureWriter.close();
			}
		}
		else if (request.getAction().equals("download_xls"))
		{
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH.mm");
			String fileName = "selectedvariables_" + dateFormat.format(new Date()) + ".xls";

			// write response headers
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

			// write excel file
			List<String> headers = Arrays.asList("Selected variables", "Descriptions", "Sector/Protocol");
			XlsWriter xlsWriter = new XlsWriter(response.getOutputStream(), headers);
			try
			{
				xlsWriter.writeHeader();

				int row = 1;

				for (ObservableFeature feature : features)
				{
					xlsWriter.writeCell(0, row, feature.getName());
					xlsWriter.writeCell(1, row, feature.getDescription());
					// TODO not consistent with eMeasure output
					xlsWriter.writeCell(2, row, dataSet.getProtocolUsed_Identifier());
					row++;
				}
			}
			finally
			{
				xlsWriter.close();
			}
		}
		else if (request.getAction().equals("download_viewer"))
		{
			req.getRequest().getSession().setAttribute("selectedObservableFeatures", features);

			String dataSetViewerName = this.getDataSetViewerName();
			if (dataSetViewerName != null)
			{
				StringBuilder sb = new StringBuilder();
				sb.append(req.getAppLocation());
				sb.append("/molgenis.do?__target=").append(dataSetViewerName);
				sb.append("&select=").append(dataSetViewerName);
				sb.append("&__action=selectDataSet");
				sb.append("&dataSetId=").append(dataSetId);
				response.sendRedirect(sb.toString());
			}
		}

	}

	/*
	 * Find the name of the DataSetViewer for user in a url. For now if there
	 * are multiple DataSetViewers it returns the first Returns null if not
	 * found
	 */
	private String getDataSetViewerName()
	{
		ScreenController<?> menu = getParent();
		for (ScreenController<?> controller : menu.getAllChildren())
		{
			if (controller instanceof DataSetViewerPlugin)
			{
				return controller.getName();
			}
		}

		return null;
	}

	// TODO reload should throw DatabaseException
	@Override
	public void reload(Database db)
	{
		List<DataSet> dataSets;
		try
		{
			dataSets = db.query(DataSet.class).find();
		}
		catch (DatabaseException e)
		{
			throw new RuntimeException(e);
		}

		// create new model
		this.protocolViewer = new ProtocolViewer();
		List<JSDataSet> jsDataSets;
		if (dataSets != null && !dataSets.isEmpty())
		{
			jsDataSets = new ArrayList<JSDataSet>(dataSets.size());
			for (DataSet dataSet : dataSets)
				// performance: do not add protocols
				jsDataSets.add(new JSDataSet(dataSet, null));
		}
		else
		{
			jsDataSets = Collections.emptyList();
		}
		this.protocolViewer.setDataSets(jsDataSets);
	}

	private List<Category> findCategories(Database db, ObservableFeature feature) throws DatabaseException
	{
		// TODO can we get by (internal) id instead of identifier?
		List<Category> categories = db.find(Category.class, new QueryRule(Category.OBSERVABLEFEATURE_IDENTIFIER,
				Operator.EQUALS, feature.getIdentifier()));
		return categories;
	}

	private List<Protocol> findSubProtocols(Database db, Protocol protocol) throws DatabaseException
	{
		List<Integer> subProtocolIds = protocol.getSubprotocols_Id();
		if (subProtocolIds == null || subProtocolIds.isEmpty()) return Collections.emptyList();

		List<Protocol> protocols = db.find(Protocol.class, new QueryRule(Protocol.ID, Operator.IN, subProtocolIds));
		return protocols;
	}

	private List<ObservableFeature> findFeatures(Database db, List<Integer> featureIds) throws DatabaseException
	{
		if (featureIds == null || featureIds.isEmpty()) return null;
		List<ObservableFeature> features = db.find(ObservableFeature.class, new QueryRule(ObservableFeature.ID,
				Operator.IN, featureIds));
		return features;
	}

	private JSDataSet toJSDataSet(Database db, DataSet dataSet) throws DatabaseException
	{
		Integer protocolId = dataSet.getProtocolUsed_Id();
		List<Protocol> protocols = db.find(Protocol.class, new QueryRule(Protocol.ID, Operator.EQUALS, protocolId));
		JSProtocol jsProtocol = null;
		if (protocols != null && !protocols.isEmpty()) jsProtocol = toJSProtocol(db, protocols.get(0));
		return new JSDataSet(dataSet, jsProtocol);
	}

	private JSProtocol toJSProtocol(Database db, Protocol protocol) throws DatabaseException
	{
		// get features
		List<JSFeature> jsFeatures = null;
		List<ObservableFeature> features = findFeatures(db, protocol.getFeatures_Id());
		if (features != null && !features.isEmpty())
		{
			jsFeatures = new ArrayList<JSFeature>(features.size());
			for (ObservableFeature feature : features)
				jsFeatures.add(toJSFeature(db, feature));
		}

		// get sub protocols (recursive)
		List<JSProtocol> jsSubProtocols = null;
		List<Protocol> subProtocols = findSubProtocols(db, protocol);
		if (subProtocols != null && !subProtocols.isEmpty())
		{
			jsSubProtocols = new ArrayList<JSProtocol>(subProtocols.size());
			for (Protocol subProtocol : subProtocols)
				jsSubProtocols.add(toJSProtocol(db, subProtocol));
		}

		return new JSProtocol(protocol, jsFeatures, jsSubProtocols);
	}

	private JSFeature toJSFeature(Database db, ObservableFeature feature) throws DatabaseException
	{
		List<JSCategory> jsCategories = null;

		List<Category> categories = findCategories(db, feature);
		if (categories != null && !categories.isEmpty())
		{
			jsCategories = new ArrayList<JSCategory>(categories.size());
			for (Category category : categories)
				jsCategories.add(new JSCategory(category));
		}
		return new JSFeature(feature, jsCategories);
	}

	public static class JSDataSet
	{
		private final int id;
		private final String name;
		private final JSProtocol protocol;

		public JSDataSet(DataSet dataSet, JSProtocol protocol)
		{
			this.id = dataSet.getId();
			this.name = dataSet.getName();
			this.protocol = protocol;
		}

		public int getId()
		{
			return id;
		}

		public String getName()
		{
			return name;
		}

		public JSProtocol getProtocol()
		{
			return protocol;
		}
	}

	@SuppressWarnings("unused")
	private static class JSProtocol
	{
		private final int id;
		private final String name;
		private final List<JSFeature> features;
		private final List<JSProtocol> subProtocols;

		public JSProtocol(Protocol protocol, List<JSFeature> features, List<JSProtocol> subProtocols)
		{
			this.id = protocol.getId();
			this.name = protocol.getName();
			this.features = features;
			this.subProtocols = subProtocols;
		}
	}

	@SuppressWarnings("unused")
	private static class JSFeature
	{
		private final int id;
		private final String name;
		private final String description;
		private final String dataType;
		private final List<JSCategory> categories;

		public JSFeature(ObservableFeature feature, List<JSCategory> categories)
		{
			this.id = feature.getId();
			this.name = feature.getName();
			this.description = feature.getDescription();
			this.dataType = feature.getDataType();
			this.categories = categories;
		}
	}

	@SuppressWarnings("unused")
	private static class JSCategory
	{
		private final int id;
		private final String code;
		private final String label;
		private final String description;

		public JSCategory(Category category)
		{
			this.id = category.getId();
			this.code = category.getValueCode();
			this.label = category.getValueLabel();
			this.description = category.getValueDescription();
		}
	}
}
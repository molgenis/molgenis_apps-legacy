package plugins.protocolViewer;

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
import org.molgenis.omicsconnect.EMeasureEntityWriter;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;
import org.molgenis.util.XlsWriter;

import com.google.gson.Gson;

public class ProtocolViewerPlugin extends PluginModel<Entity>
{
	private static final long serialVersionUID = -6143910771849972946L;

	/** all data sets */
	private List<DataSet> dataSets;

	public ProtocolViewerPlugin(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public String getCustomHtmlHeaders()
	{
		return "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/download_list.css\">";
	}

	@Override
	public String getViewName()
	{
		return "plugins_protocolViewer_ProtocolViewerPlugin";
	}

	@Override
	public String getViewTemplate()
	{
		return "ProtocolViewerPlugin.ftl";
	}

	@Override
	public Show handleRequest(Database db, Tuple request, OutputStream out) throws Exception
	{
		if (out == null) this.handleRequest(db, request);

		Object object = null;
		if (request.getAction().equals("download_json_getdataset"))
		{
			Integer datasetId = request.getInt("datasetid");
			DataSet selectedDataSet = null;
			for (DataSet dataSet : dataSets)
			{
				if (dataSet.getId().equals(datasetId))
				{
					selectedDataSet = dataSet;
					break;
				}
			}
			if (selectedDataSet != null)
			{
				Protocol usedProtocol = findProtocol(db, selectedDataSet);
				if (usedProtocol != null)
				{
					object = toJSProtocol(db, usedProtocol);
				}
			}
		}
		else if (request.getAction().equals("download_json_getfeature"))
		{
			Integer featureId = request.getInt("featureid");
			List<ObservableFeature> features = db.find(ObservableFeature.class, new QueryRule(ObservableFeature.ID,
					Operator.EQUALS, featureId));
			if (features == null || features.isEmpty()) throw new RuntimeException("feature does not exist: "
					+ featureId);

			ObservableFeature feature = features.get(0);

			List<Category> categories = findCategories(db, feature);
			List<JSCategory> jsCategories = null;
			if (categories != null && !categories.isEmpty())
			{
				jsCategories = new ArrayList<JSCategory>(categories.size());
				for (Category category : categories)
					jsCategories.add(new JSCategory(category));
			}

			object = new JSFeature(feature, jsCategories);
		}
		else if (request.getAction().equals("download_json_searchAll"))
		{
			// JsonObject jsonObject = new JsonObject();
			// for (ObservableFeature feature : getFeatures(db))
			// {
			// List<Category> categories = findCategories(db, feature);
			// jsonObject.add(feature.getName(), buildJson(feature,
			// categories));
			// }
			//
			// object = jsonObject;
		}
		else
		{
			throw new RuntimeException("unknown action: " + request.getAction());
		}

		Writer writer = new OutputStreamWriter(out, Charset.forName("UTF-8"));
		try
		{
			new Gson().toJson(object, writer);
		}
		finally
		{
			writer.close();
		}

		return Show.SHOW_MAIN;
	}

	@Override
	public void handleRequest(Database db, Tuple request) throws Exception
	{
		MolgenisRequest req = (MolgenisRequest) request;
		HttpServletResponse response = req.getResponse();

		Integer dataSetId = request.getInt("datasetid");
		DataSet dataSet = null;
		for (DataSet aDataSet : dataSets)
		{
			if (aDataSet.getId().equals(dataSetId))
			{
				dataSet = aDataSet;
				break;
			}
		}
		String[] featuresStr = request.getString("features").split(",");
		List<ObservableFeature> features = findFeatures(db, featuresStr);

		if (request.getAction().equals("download_emeasure"))
		{

			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH.mm");
			String fileName = "EMeasure_" + dateFormat.format(new Date()) + ".xml";

			// write response headers
			response.setContentType("application/x-download");
			response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

			// write eMeasure XML file
			EMeasureEntityWriter eMeasureWriter = new EMeasureEntityWriter(response.getOutputStream());
			try
			{
				eMeasureWriter.writeObservableFeatures(features);
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
			// FIXME pheno reference
			response.sendRedirect(req.getAppLocation() + "/molgenis.do?__target=main&select=phenotypeViewer");
		}

	}

	private List<Category> findCategories(Database db, ObservableFeature feature) throws DatabaseException
	{
		// TODO can we get by (internal) id instead of identifier?
		QueryRule queryRule = new QueryRule(Category.OBSERVABLEFEATURE_IDENTIFIER, Operator.EQUALS,
				feature.getIdentifier());
		List<Category> categories = db.find(Category.class, queryRule);
		return categories;
	}

	private Protocol findProtocol(Database db, DataSet dataSet) throws DatabaseException
	{
		Integer protocolUsedId = dataSet.getProtocolUsed_Id();
		if (protocolUsedId == null) return null;
		List<Protocol> protocols = db.find(Protocol.class, new QueryRule(Protocol.ID, Operator.EQUALS, protocolUsedId));
		return protocols != null ? protocols.get(0) : null;
	}

	private List<Protocol> findSubProtocols(Database db, Protocol protocol) throws DatabaseException
	{
		List<Integer> subProtocolIds = protocol.getSubprotocols_Id();
		if (subProtocolIds == null || subProtocolIds.isEmpty()) return Collections.emptyList();

		List<Protocol> protocols = db.find(Protocol.class, new QueryRule(Protocol.ID, Operator.IN, subProtocolIds));
		return protocols;
	}

	private List<ObservableFeature> findFeatures(Database db, Protocol protocol) throws DatabaseException
	{
		List<Integer> featureIds = protocol.getFeatures_Id();
		if (featureIds == null || featureIds.isEmpty()) return null;
		List<ObservableFeature> features = db.find(ObservableFeature.class, new QueryRule(ObservableFeature.ID,
				Operator.IN, featureIds));
		return features;
	}

	private List<ObservableFeature> findFeatures(Database db, String[] featureStrIds) throws DatabaseException
	{
		List<Integer> featureIds = new ArrayList<Integer>(featureStrIds.length);
		for (String featureIdStr : featureStrIds)
			featureIds.add(Integer.valueOf(featureIdStr));

		if (featureIds == null || featureIds.isEmpty()) return null;
		List<ObservableFeature> features = db.find(ObservableFeature.class, new QueryRule(ObservableFeature.ID,
				Operator.IN, featureIds));
		return features;
	}

	// private JsonObject buildJson(ObservableFeature feature, List<Category>
	// categories)
	// {
	// JsonObject jsonObject = new JsonObject();
	// jsonObject.addProperty(ObservableFeature.NAME.toLowerCase(),
	// feature.getName());
	// jsonObject.addProperty(ObservableFeature.DESCRIPTION.toLowerCase(),
	// feature.getDescription());
	// jsonObject.addProperty(ObservableFeature.DATATYPE.toLowerCase(),
	// feature.getDataType());
	//
	// JsonArray jsonArray = new JsonArray();
	// for (Category category : categories)
	// {
	// JsonObject jsonCategoryObject = new JsonObject();
	// jsonCategoryObject.addProperty(Category.VALUECODE.toLowerCase(),
	// category.getValueCode());
	// jsonCategoryObject.addProperty(Category.VALUELABEL.toLowerCase(),
	// category.getValueLabel());
	// jsonCategoryObject.addProperty(Category.VALUEDESCRIPTION.toLowerCase(),
	// category.getValueDescription());
	// jsonCategoryObject.addProperty(Category.ISMISSING.toLowerCase(),
	// category.getIdField());
	//
	// jsonArray.add(jsonCategoryObject);
	// }
	// jsonObject.add("categories", jsonArray);
	// return jsonObject;
	// }
	//
	// private List<ObservableFeature> getFeatures(Database db) throws
	// DatabaseException
	// {
	// return getFeatures(db, null);
	// }
	//
	// private List<ObservableFeature> getFeatures(Database db, List<Integer>
	// featureFilterIds) throws DatabaseException
	// {
	// // get protocol
	// Protocol protocol = findProtocol(db, this.selectedDataSet);
	// if (protocol == null) return Collections.emptyList();
	//
	// // find protocol features
	// List<ObservableFeature> features = db.find(ObservableFeature.class, new
	// QueryRule(ObservableFeature.ID,
	// Operator.IN, protocol.getFeatures_Id()));
	// if (features == null || features.isEmpty()) return
	// Collections.emptyList();
	//
	// // filter features
	// if (featureFilterIds != null)
	// {
	// Set<Integer> featureIdSet = new HashSet<Integer>(featureFilterIds);
	// for (Iterator<ObservableFeature> it = features.iterator(); it.hasNext();)
	// {
	// if (!featureIdSet.contains(it.next().getId()))
	// {
	// it.remove();
	// }
	// }
	// }
	// return features;
	// }

	@Override
	public void reload(Database db)
	{
		try
		{
			this.dataSets = db.query(DataSet.class).find();
		}
		catch (DatabaseException e)
		{
			// TODO reload should throw DatabaseException
			throw new RuntimeException(e);
		}
	}

	private JSProtocol toJSProtocol(Database db, Protocol protocol) throws DatabaseException
	{
		List<JSFeature> jsFeatures = null;
		List<ObservableFeature> features = findFeatures(db, protocol);
		if (features != null && !features.isEmpty())
		{
			jsFeatures = new ArrayList<JSFeature>(features.size());
			for (ObservableFeature feature : features)
			{
				List<JSCategory> jsCategories = null;

				List<Category> categories = findCategories(db, feature);
				if (categories != null && !categories.isEmpty())
				{
					jsCategories = new ArrayList<JSCategory>(categories.size());
					for (Category category : categories)
					{
						jsCategories.add(new JSCategory(category));
					}
				}

				jsFeatures.add(new JSFeature(feature, jsCategories));
			}
		}

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

	public List<DataSet> getDataSets()
	{
		return dataSets;
	}

	public List<String> getSearchFields()
	{
		return Arrays.asList("All", "Protocols", "ObservableFeatures");
	}

	public String getUrl()
	{
		return "molgenis.do?__target=" + this.getName();
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
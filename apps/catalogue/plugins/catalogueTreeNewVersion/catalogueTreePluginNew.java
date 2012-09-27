package plugins.catalogueTreeNewVersion;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.html.JQueryTreeViewElement;
import org.molgenis.pheno.Category;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.protocol.Protocol;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

//import org.molgenis.util.XlsWriter;

public class catalogueTreePluginNew extends PluginModel<Entity> {

	private static final long serialVersionUID = -3520012368180455984L;

	private JQueryTreeViewElement protocolsTree = null;
	private HashMap<String, Protocol> nameToProtocol = new HashMap<String, Protocol>();
	private HashMap<String, JQueryTreeViewElement> protocolsAndMeasurementsinTree = new HashMap<String, JQueryTreeViewElement>();
	private HashMap<String, List<JQueryTreeViewElement>> findNodes = new HashMap<String, List<JQueryTreeViewElement>>();
	private int loadingProcess = 0;
	private String investigationName = "Prevend";

	private List<String> topProtocols = new ArrayList<String>();;

	public catalogueTreePluginNew(String name, ScreenController<?> parent) {
		super(name, parent);
	}

	public String getCustomHtmlHeaders() {
		return "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/download_list.css\">";
	}

	@Override
	public String getViewName() {
		return "plugins_catalogueTreeNewVersion_catalogueTreePluginNew";
	}

	@Override
	public String getViewTemplate() {
		return "plugins/catalogueTreeNewVersion/catalogueTreePluginNew.ftl";
	}

	@Override
	public Show handleRequest(Database db, Tuple request, OutputStream out)
			throws Exception {

		if (out == null) {

			this.handleRequest(db, request);

		} else {

			if ("download_json_loadingTree".equals(request.getAction())) {

				if (topProtocols.size() == 0) {
					topProtocols = getTopProtocols(investigationName, db);
				}

				int increment = topProtocols.size() / 5;

				int upperLimit = topProtocols.size() < (loadingProcess + 1)
						* increment ? topProtocols.size()
						: (loadingProcess + 1) * increment;

				for (int i = loadingProcess * increment; i < upperLimit; i++) {

					String protocolName = topProtocols.get(i);

					JQueryTreeViewElement childTree = null;

					if (!protocolsAndMeasurementsinTree
							.containsKey(protocolName)) {

						Protocol protocol = nameToProtocol.get(protocolName);
						// The tree first time is being created.
						childTree = new JQueryTreeViewElement(protocolName,
								Protocol.class.getSimpleName()
										+ protocol.getId().toString(),
								protocolsTree);

						protocolsAndMeasurementsinTree.put(
								protocolName.replaceAll(" ", "_"), childTree);

					}
					createNodesForChild(childTree, db);
				}

				loadingProcess++;

				JSONObject json = new JSONObject();

				json.put("result", loadingProcess);

				if (loadingProcess == 5) {
					json.put("status", true);
				} else {
					json.put("status", false);
				}

				PrintWriter writer = new PrintWriter(out);

				writer.write(json.toString());

				writer.flush();

				writer.close();

			} else if ("download_json_toggleNode".equals(request.getAction())) {

				String nodeIdentifier = request.getString("nodeIdentifier");

				protocolsAndMeasurementsinTree.get(nodeIdentifier).toggleNode();

			} else if ("download_json_search".equals(request.getAction())) {

				String searchToken = request.getString("searchToken");

				Query<Measurement> query = db.query(Measurement.class);

				query.addRules(new QueryRule(Measurement.INVESTIGATION_NAME,
						Operator.EQUALS, investigationName));
				query.addRules(new QueryRule(Measurement.NAME, Operator.LIKE,
						"age"));

				List<JQueryTreeViewElement> listOfNodes = new ArrayList<JQueryTreeViewElement>();

				for (Measurement m : query.find()) {
					System.out.println(findNodes.get(m.getName()));
					if (findNodes.get(m.getName()) != null) {
						listOfNodes.addAll(findNodes.get(m.getName()));
					}
				}

				JSONObject searchResult = new JSONObject();

				for (int i = 0; i < listOfNodes.size(); i++) {

					String results[] = listOfNodes.get(i).renderParent();

					searchResult.put(results[0], results[1]);

				}

				String tree = searchForInTree(protocolsTree, listOfNodes);

				JSONObject json = new JSONObject();

				json.put("result", tree);

				PrintWriter writer = new PrintWriter(out);

				writer.write(json.toString());

				writer.flush();

				writer.close();

				System.out.println("The searching token is " + searchToken);

			} else if ("download_json_getChildren".equals(request.getAction())) {

				String nodeIdentifier = request.getString("nodeIdentifier");

				JQueryTreeViewElement node = protocolsAndMeasurementsinTree
						.get(nodeIdentifier);

				node.toggleNode();

				String addedNodes = "";

				for (JQueryTreeViewElement child : node.getChildren()) {

					addedNodes += child.toHtml(null);
				}

				JSONObject json = new JSONObject();

				json.put("result", addedNodes);

				PrintWriter writer = new PrintWriter(out);

				writer.write(json.toString());

				writer.flush();

				writer.close();
			}

		}

		return Show.SHOW_MAIN;
	}

	private String searchForInTree(JQueryTreeViewElement parentNode,
			List<JQueryTreeViewElement> listOfNodes) {
		// System.out.println(parentNode);

		String returnString = "";

		for (JQueryTreeViewElement childNode : parentNode.getChildren()) {

			if (listOfNodes.contains(childNode)) {

				returnString += childNode.toHtml();

			} else {
				returnString += searchForInTree(childNode, listOfNodes);
			}
		}

		return returnString;
	}

	// private void searchBottomNode() {
	//
	// for (JQueryTreeViewElement eachNode : protocolsTree.getAllChildren()) {
	//
	// if (eachNode.getParent() == null) {
	//
	// } else if (eachNode.isIsbottom()) {
	// String label = eachNode.getLabel();
	//
	// }
	// }
	// }

	private void createNodesForChild(JQueryTreeViewElement parentNode,
			Database db) throws DatabaseException {

		if (nameToProtocol.containsKey(parentNode.getLabel())) {

			Protocol p = nameToProtocol.get(parentNode.getLabel());

			String parentName = p.getName();
			// Check subProtocols
			for (String subProtocolName : p.getSubprotocols_Name()) {

				JQueryTreeViewElement childTree = null;

				String uniqueName = parentName + "_" + subProtocolName;

				if (!protocolsAndMeasurementsinTree.containsKey(uniqueName)) {

					Protocol protocol = nameToProtocol.get(subProtocolName);
					// The tree first time is being created.
					childTree = new JQueryTreeViewElement(uniqueName,
							subProtocolName, Protocol.class.getSimpleName()
									+ protocol.getId().toString(), parentNode);

					protocolsAndMeasurementsinTree.put(
							uniqueName.replaceAll(" ", "_"), childTree);
				}

				List<JQueryTreeViewElement> treeNodes = null;
				if (findNodes.containsKey(subProtocolName)) {
					treeNodes = findNodes.get(subProtocolName);

				} else {
					treeNodes = new ArrayList<JQueryTreeViewElement>();
				}
				treeNodes.add(childTree);
				findNodes.put(subProtocolName, treeNodes);

				createNodesForChild(childTree, db);
			}

			// There are not subprotocols, therefore check features of this
			// protocol
			if (p.getFeatures_Name().size() > 0) {

				for (Measurement feature : db.find(
						Measurement.class,
						new QueryRule(Measurement.NAME, Operator.IN, p
								.getFeatures_Name()))) {

					String labelName = (feature.getLabel() != null ? feature
							.getLabel() : feature.getName());

					String featureName = feature.getName();

					JQueryTreeViewElement childTree = null;

					String uniqueName = parentName + "_" + featureName;

					if (!protocolsAndMeasurementsinTree.containsKey(uniqueName)) {

						childTree = new JQueryTreeViewElement(uniqueName,
								labelName, Measurement.class.getSimpleName()
										+ feature.getId().toString(),
								parentNode);

						childTree.setIsbottom(true);

						String htmlValue = htmlTableForTreeInformation(db,
								feature, featureName);

						childTree.setHtmlValue(htmlValue);

						protocolsAndMeasurementsinTree.put(
								uniqueName.replaceAll(" ", "_"), childTree);

					}
					List<JQueryTreeViewElement> treeNodes = null;
					if (findNodes.containsKey(featureName)) {
						treeNodes = findNodes.get(featureName);

					} else {
						treeNodes = new ArrayList<JQueryTreeViewElement>();
					}
					treeNodes.add(childTree);
					findNodes.put(featureName, treeNodes);
				}
			}
		}

	}

	@Override
	public void handleRequest(Database db, Tuple request) {

	}

	@Override
	public void reload(Database db) {

		loadingProcess = 0;
		topProtocols.clear();
		findNodes.clear();
		nameToProtocol.clear();
		protocolsAndMeasurementsinTree.clear();

		try {

			protocolsTree = new JQueryTreeViewElement("Study_"
					+ investigationName, "", null);

			protocolsTree.setLabel("Study: " + investigationName);

			protocolsAndMeasurementsinTree.put(protocolsTree.getName()
					.replaceAll(" ", "_"), protocolsTree);

			// List<String> topProtocols = getTopProtocols(investigationName,
			// db);
			//
			// for (String protocolName : topProtocols) {
			//
			// JQueryTreeViewElement childTree = null;
			//
			// if (!protocolsAndMeasurementsinTree.containsKey(protocolName)) {
			//
			// Protocol protocol = nameToProtocol.get(protocolName);
			// // The tree first time is being created.
			// childTree = new JQueryTreeViewElement(protocolName,
			// Protocol.class.getSimpleName()
			// + protocol.getId().toString(),
			// protocolsTree);
			//
			// protocolsAndMeasurementsinTree.put(
			// protocolName.replaceAll(" ", "_"), childTree);
			//
			// createNodesForChild(childTree, db);
			// // addedNodes += childTree.toHtml();
			// }
			// }

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String htmlTableForTreeInformation(Database db,
			Measurement measurement, String nodeName) throws DatabaseException {

		List<String> categoryNames = measurement.getCategories_Name();

		String measurementDescription = measurement.getDescription();

		String measurementDataType = measurement.getDataType();

		String displayName = measurement.getName();

		if (measurement.getLabel() != null
				&& !measurement.getLabel().equals("")) {
			displayName = measurement.getLabel();
		}

		// String htmlValue = "<table id = 'detailInformation'  border = 2>" +
		String htmlValue = "<table style='border-spacing: 2px; width: 100%;' class='MeasurementDetails' id = '"
				+ nodeName + "_table'>";
		htmlValue += "<tr><td class='box-body-label'>Current selection:</th><td id=\""
				+ nodeName
				+ "_itemName\"style=\"cursor:pointer\">"
				+ displayName + "</td></tr>";

		if (categoryNames.size() > 0) {

			List<Category> listOfCategory = db.find(Category.class,
					new QueryRule(Category.NAME, Operator.IN, categoryNames));

			htmlValue += "<tr id='"
					+ nodeName
					+ "_category'><td  class='box-body-label'>Category:</td><td><table>";

			String missingCategory = "<tr><td  class='box-body-label'>Missing category:</td><td><table>";

			for (Category c : listOfCategory) {

				String codeString = c.getCode_String();

				if (!codeString.equals("")) {
					codeString += " = ";
				}
				if (!c.getIsMissing()) {
					htmlValue += "<tr><td>";
					htmlValue += codeString + c.getDescription();
					htmlValue += "</td></tr>";
				} else {
					missingCategory += "<tr><td>";
					missingCategory += codeString + c.getDescription();
					missingCategory += "</td></tr>";
				}
			}

			htmlValue += "</table></td></tr>";

			htmlValue += missingCategory + "</table>";
		}

		htmlValue += "<tr id='"
				+ nodeName
				+ "_description'><td class='box-body-label'>Description:</td><td>"
				+ (measurementDescription == null ? "not provided"
						: measurementDescription) + "</td></tr>";

		htmlValue += "<tr id='" + nodeName
				+ "_dataType'><td class='box-body-label'>Data type:</th><td>"
				+ measurementDataType + "</td></tr>";

		Query<ObservedValue> queryDetailInformation = db
				.query(ObservedValue.class);

		queryDetailInformation.addRules(new QueryRule(
				ObservedValue.TARGET_NAME, Operator.EQUALS, measurement
						.getName()));

		if (!queryDetailInformation.find().isEmpty()) {

			for (ObservedValue ov : queryDetailInformation.find()) {

				String featureName = ov.getFeature_Name();
				String value = ov.getValue();

				if (featureName.startsWith("SOP")) {
					htmlValue += "<tr><td class='box-body-label'>"
							+ featureName + "</td><td><a href=" + value + ">"
							+ value + "</a></td></tr>";
				} else {

					if (featureName.startsWith("display name")) {
						featureName = "display name";
					}

					// htmlValue += "<tr><td class='box-body-label'>" +
					// featureName + "</td><td> "
					// + value + "</td></tr>";
				}
			}
		}

		htmlValue += "</table>";

		return htmlValue;
	}

	public List<String> getTopProtocols(String investigationName, Database db)
			throws DatabaseException {

		List<String> topProtocols = new ArrayList<String>();
		List<String> bottomProtocols = new ArrayList<String>();
		List<String> middleProtocols = new ArrayList<String>();

		for (Protocol p : db.find(Protocol.class,
				new QueryRule(Protocol.INVESTIGATION_NAME, Operator.EQUALS,
						investigationName))) {

			if (!p.getName().equalsIgnoreCase("generic")) {

				List<String> subNames = p.getSubprotocols_Name();

				// keep a record of each protocol in a hashmap. Later on we
				// could reference to the Protocol by name
				if (!nameToProtocol.containsKey(p.getName())) {
					nameToProtocol.put(p.getName(), p);
				}

				if (!subNames.isEmpty()) {

					if (!topProtocols.contains(p.getName())) {
						topProtocols.add(p.getName());
					}
					for (String subProtocol : subNames) {
						if (!middleProtocols.contains(subProtocol)) {
							middleProtocols.add(subProtocol);
						}
					}

				} else {

					if (!bottomProtocols.contains(p.getName())) {
						bottomProtocols.add(p.getName());
					}
				}
			}
			middleProtocols.removeAll(bottomProtocols);
			topProtocols.removeAll(middleProtocols);
		}

		if (topProtocols.size() == 0) {
			return bottomProtocols;
		} else {
			return topProtocols;
		}
	}

	public String getTreeView() {
		return protocolsTree.toHtml(null);
	}

	public String getUrl() {
		return "molgenis.do?__target=" + this.getName();
	}

}

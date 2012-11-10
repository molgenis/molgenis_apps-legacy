package plugins.catalogueTreeNewVersion;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.html.JQueryTreeViewElement;
import org.molgenis.pheno.Category;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.protocol.Protocol;
import org.molgenis.util.Tuple;

public class catalogueTreeComponent
{

	private JQueryTreeViewElement protocolsTree = null;
	private HashMap<String, JSONObject> descriptions = new HashMap<String, JSONObject>();
	private HashMap<String, Protocol> nameToProtocol = new HashMap<String, Protocol>();
	private HashMap<String, JQueryTreeViewElement> protocolsAndMeasurementsinTree = new HashMap<String, JQueryTreeViewElement>();
	private HashMap<String, List<JQueryTreeViewElement>> findNodes = new HashMap<String, List<JQueryTreeViewElement>>();
	private List<String> listOfProtocols = new ArrayList<String>();
	private List<String> topProtocols = new ArrayList<String>();
	private int loadingProcess = 0;
	private String investigationName = "";

	public catalogueTreeComponent(String investigationName)
	{
		this.investigationName = investigationName;
		initializeTree();
	}

	public JSONObject requestHandle(Tuple request, Database db, OutputStream out) throws JSONException,
			DatabaseException
	{
		JSONObject json = new JSONObject();

		if ("download_json_loadingTree".equals(request.getAction()))
		{

			if (topProtocols.size() == 0)
			{
				topProtocols = getTopProtocols(investigationName, db);
			}

			int increment = topProtocols.size() / 5;

			int upperLimit = topProtocols.size() < (loadingProcess + 1) * increment ? topProtocols.size()
					: (loadingProcess + 1) * increment;

			for (int i = loadingProcess * increment; i < upperLimit; i++)
			{

				String protocolName = topProtocols.get(i);

				JQueryTreeViewElement childTree = null;

				if (!protocolsAndMeasurementsinTree.containsKey(protocolName))
				{

					Protocol protocol = nameToProtocol.get(protocolName);
					// The tree first time is being created.
					childTree = new JQueryTreeViewElement(protocolName, Protocol.class.getSimpleName()
							+ protocol.getId().toString(), protocolsTree);

					protocolsAndMeasurementsinTree.put(protocolName.replaceAll(" ", "_"), childTree);

				}
				createNodesForChild(childTree, db);
			}

			loadingProcess++;

			json.put("result", loadingProcess);

			if (loadingProcess == 5)
			{
				json.put("status", true);
			}
			else
			{
				json.put("status", false);
			}

		}
		else if (request.getAction().equals("download_json_refreshModel"))
		{

			String selectedModel = request.getString("selectedModel");

			Protocol p = db.find(Protocol.class, new QueryRule(Protocol.NAME, Operator.EQUALS, selectedModel)).get(0);

			json.put("result", p.getFeatures_Name());

		}
		else if (request.getAction().equals("download_json_showInformation"))
		{

			System.out.println("showVariableInformation------------" + request);
			String nodeIdentifier = request.getString("variableName");

			JQueryTreeViewElement node = protocolsAndMeasurementsinTree.get(nodeIdentifier);

			json.put("result", node.getHtmlValue());
		}
		else if ("download_json_toggleNode".equals(request.getAction()))
		{

			String nodeIdentifier = request.getString("nodeIdentifier");

			protocolsAndMeasurementsinTree.get(nodeIdentifier).toggleNode();

		}
		else if ("download_json_clearSearch".equals(request.getAction()))
		{

			String tree = protocolsTree.toHtml();

			json.put("result", tree);

		}
		else if ("download_json_search".equals(request.getAction()))
		{

			String searchToken = request.getString("searchToken");

			List<JQueryTreeViewElement> listOfNodes = new ArrayList<JQueryTreeViewElement>();

			Query<Measurement> query = null;

			// Search in the name of measurements
			{
				query = db.query(Measurement.class);

				query.addRules(new QueryRule(Measurement.INVESTIGATION_NAME, Operator.EQUALS, investigationName));
				query.addRules(new QueryRule(Measurement.NAME, Operator.LIKE, searchToken));

				for (Measurement m : query.find())
				{
					System.out.println(findNodes.get(m.getName()));
					if (findNodes.get(m.getName()) != null)
					{
						listOfNodes.addAll(findNodes.get(m.getName()));
					}
				}
			}
			// Search in the description of the variables
			{
				query = db.query(Measurement.class);

				query.addRules(new QueryRule(Measurement.INVESTIGATION_NAME, Operator.EQUALS, investigationName));
				query.addRules(new QueryRule(Measurement.DESCRIPTION, Operator.LIKE, searchToken));

				for (Measurement m : query.find())
				{
					System.out.println(findNodes.get(m.getName()));
					if (findNodes.get(m.getName()) != null)
					{
						listOfNodes.addAll(findNodes.get(m.getName()));
					}
				}
			}
			// search in the name of protocols
			{
				Query<Protocol> queryProtocol = db.query(Protocol.class);

				queryProtocol.addRules(new QueryRule(Protocol.INVESTIGATION_NAME, Operator.EQUALS, investigationName));
				queryProtocol.addRules(new QueryRule(Protocol.NAME, Operator.LIKE, searchToken));

				for (Protocol p : queryProtocol.find())
				{
					System.out.println(findNodes.get(p.getName()));
					if (findNodes.get(p.getName()) != null)
					{
						listOfNodes.addAll(findNodes.get(p.getName()));
					}
				}
			}

			String tree = searchForInTree(protocolsTree, listOfNodes);

			json.put("result", tree);

		}
		else if ("download_json_getChildren".equals(request.getAction()))
		{

			String nodeIdentifier = request.getString("nodeIdentifier");

			JQueryTreeViewElement node = protocolsAndMeasurementsinTree.get(nodeIdentifier);

			node.toggleNode();

			String addedNodes = "";

			for (JQueryTreeViewElement child : node.getChildren())
			{
				addedNodes += child.toHtml(null);
			}

			json.put("result", addedNodes);

		}
		else if ("download_json_getPosition".equals(request.getAction()))
		{
			String measurementName = request.getString("measurementName");

			List<JQueryTreeViewElement> nodes = findNodes.get(measurementName);

			List<String> identifier = new ArrayList<String>();

			for (JQueryTreeViewElement element : nodes)
			{
				identifier.add(element.getName());

				while (element.getParent() != null)
				{
					element = element.getParent();
					element.setCollapsed(false);
				}
			}

			json.put("treeView", protocolsTree.toHtml());

			json.put("identifier", identifier.get(0));
		}

		return json;
	}

	private String searchForInTree(JQueryTreeViewElement parentNode, List<JQueryTreeViewElement> listOfNodes)
	{
		StringBuilder stringBuilder = new StringBuilder();

		String returnString = "";

		boolean parentCollapse = parentNode.isCollapsed();

		parentNode.setCollapsed(false);

		for (JQueryTreeViewElement childNode : parentNode.getChildren())
		{

			if (listOfNodes.contains(childNode))
			{
				boolean collapse = childNode.isCollapsed();
				childNode.setCollapsed(false);
				stringBuilder.append(childNode.toHtml());
				childNode.setCollapsed(collapse);

			}
			else
			{
				stringBuilder.append(searchForInTree(childNode, listOfNodes));
			}
		}

		returnString = stringBuilder.toString();

		if (!stringBuilder.toString().equals(""))
		{
			returnString = parentNode.toHtml(stringBuilder.toString());
		}

		parentNode.setCollapsed(parentCollapse);

		return returnString;
	}

	private void createNodesForChild(JQueryTreeViewElement parentNode, Database db) throws DatabaseException,
			JSONException
	{
		if (nameToProtocol.containsKey(parentNode.getLabel()))
		{
			Protocol p = nameToProtocol.get(parentNode.getLabel());

			String parentName = p.getName();
			// Check subProtocols
			for (String subProtocolName : p.getSubprotocols_Name())
			{

				JQueryTreeViewElement childTree = null;

				StringBuilder uniqueName = new StringBuilder();

				uniqueName.append(parentName).append("_").append(subProtocolName);

				if (!protocolsAndMeasurementsinTree.containsKey(uniqueName.toString()))
				{
					Protocol protocol = nameToProtocol.get(subProtocolName);

					// The tree first time is being created.
					StringBuilder nodeIdentifier = new StringBuilder();

					childTree = new JQueryTreeViewElement(uniqueName.toString(), subProtocolName, nodeIdentifier
							.append(Protocol.class.getSimpleName()).append(protocol.getId().toString()).toString(),
							parentNode);

					protocolsAndMeasurementsinTree.put(uniqueName.toString().replaceAll(" ", "_"), childTree);
				}

				List<JQueryTreeViewElement> treeNodes = null;
				if (findNodes.containsKey(subProtocolName))
				{
					treeNodes = findNodes.get(subProtocolName);
				}
				else
				{
					treeNodes = new ArrayList<JQueryTreeViewElement>();
				}
				treeNodes.add(childTree);
				findNodes.put(subProtocolName, treeNodes);

				createNodesForChild(childTree, db);
			}

			// There are not subprotocols, therefore check features of this
			// protocol
			if (p.getFeatures_Name().size() > 0)
			{
				for (Measurement feature : db.find(Measurement.class,
						new QueryRule(Measurement.NAME, Operator.IN, p.getFeatures_Name())))
				{
					if (!descriptions.containsKey(feature))
					{
						JSONObject data = new JSONObject();
						data.put("name", feature.getName());
						data.put("label", feature.getLabel());
						data.put("description", feature.getDescription());
						data.put("dataType", feature.getDataType());
						data.put("category", feature.getCategories_Name());
						descriptions.put(feature.getName(), data);
					}

					String labelName = (feature.getLabel() != null ? feature.getLabel() : feature.getName());

					String featureName = feature.getName();

					JQueryTreeViewElement childTree = null;

					StringBuilder uniqueName = new StringBuilder();

					uniqueName.append(parentName).append("_").append(featureName);

					if (!protocolsAndMeasurementsinTree.containsKey(uniqueName.toString()))
					{
						StringBuilder nodeIdentifier = new StringBuilder();

						childTree = new JQueryTreeViewElement(uniqueName.toString(), labelName, nodeIdentifier
								.append(Measurement.class.getSimpleName()).append(feature.getId().toString())
								.toString(), parentNode);

						childTree.setIsbottom(true);

						String htmlValue = htmlTableForTreeInformation(db, feature, featureName);

						childTree.setHtmlValue(htmlValue);

						protocolsAndMeasurementsinTree.put(uniqueName.toString().replaceAll(" ", "_"), childTree);

					}
					List<JQueryTreeViewElement> treeNodes = null;
					if (findNodes.containsKey(featureName))
					{
						treeNodes = findNodes.get(featureName);
					}
					else
					{
						treeNodes = new ArrayList<JQueryTreeViewElement>();
					}
					treeNodes.add(childTree);
					findNodes.put(featureName, treeNodes);
				}
			}
		}

	}

	public void initializeTree()
	{
		loadingProcess = 0;
		topProtocols.clear();
		listOfProtocols.clear();
		findNodes.clear();
		nameToProtocol.clear();
		protocolsAndMeasurementsinTree.clear();

		try
		{
			// Create the first node of the tree
			StringBuilder nodeIdentifier = new StringBuilder();

			protocolsTree = new JQueryTreeViewElement(nodeIdentifier.append("Study_").append(investigationName)
					.toString(), "", null);

			protocolsTree.setLabel(nodeIdentifier.toString());

			protocolsAndMeasurementsinTree.put(protocolsTree.getName().replaceAll(" ", "_"), protocolsTree);

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	public String htmlTableForTreeInformation(Database db, Measurement measurement, String nodeName)
			throws DatabaseException
	{

		List<String> categoryNames = measurement.getCategories_Name();

		String measurementDescription = measurement.getDescription();

		String measurementDataType = measurement.getDataType();

		String displayName = measurement.getName();

		if (measurement.getLabel() != null && !measurement.getLabel().equals(""))
		{
			displayName = measurement.getLabel();
		}

		StringBuilder htmlValue = new StringBuilder();

		htmlValue.append("<table style='border-spacing: 2px; width: 100%;' class='MeasurementDetails' id = '")
				.append(nodeName).append("_table'>")
				.append("<tr><td class='box-body-label'>Current selection:</th><td id=\"").append(nodeName)
				.append("_itemName\"style=\"cursor:pointer\">").append(displayName).append("</td></tr>");

		if (categoryNames.size() > 0)
		{

			List<Category> listOfCategory = db.find(Category.class, new QueryRule(Category.NAME, Operator.IN,
					categoryNames));

			htmlValue.append("<tr id='").append(nodeName)
					.append("_category'><td  class='box-body-label'>Category:</td><td><table>");

			StringBuilder missingCategory = new StringBuilder();

			missingCategory.append("<tr><td  class='box-body-label'>Missing category:</td><td><table>");

			for (Category c : listOfCategory)
			{
				StringBuilder codeString = new StringBuilder();

				codeString.append(c.getCode_String());

				if (!codeString.equals(""))
				{
					codeString.append(" = ");
				}
				if (!c.getIsMissing())
				{
					htmlValue.append("<tr><td>").append(codeString).append(c.getDescription()).append("</td></tr>");
				}
				else
				{
					missingCategory.append("<tr><td>").append(codeString).append(c.getDescription())
							.append("</td></tr>");
				}
			}

			htmlValue.append("</table></td></tr>").append(missingCategory.toString()).append("</table>");
		}

		htmlValue.append("<tr id='").append(nodeName)
				.append("_description'><td class='box-body-label'>Description:</td><td>")
				.append((measurementDescription == null ? "not provided" : measurementDescription))
				.append("</td></tr>").append("<tr id='").append(nodeName)
				.append("_dataType'><td class='box-body-label'>Data type:</th><td>").append(measurementDataType)
				.append("</td></tr>");

		Query<ObservedValue> queryDetailInformation = db.query(ObservedValue.class);

		queryDetailInformation
				.addRules(new QueryRule(ObservedValue.TARGET_NAME, Operator.EQUALS, measurement.getName()));

		if (!queryDetailInformation.find().isEmpty())
		{

			for (ObservedValue ov : queryDetailInformation.find())
			{

				String featureName = ov.getFeature_Name();
				String value = ov.getValue();

				if (featureName.startsWith("SOP"))
				{
					htmlValue.append("<tr><td class='box-body-label'>").append(featureName).append("</td><td><a href=")
							.append(value).append(">").append(value).append("</a></td></tr>");
				}
				else
				{
					if (featureName.startsWith("display name"))
					{
						featureName = "display name";
					}
				}
			}
		}

		htmlValue.append("</table>");

		return htmlValue.toString();
	}

	public List<String> getTopProtocols(String investigationName, Database db) throws DatabaseException
	{

		List<String> topProtocols = new ArrayList<String>();
		List<String> bottomProtocols = new ArrayList<String>();
		List<String> middleProtocols = new ArrayList<String>();

		for (Protocol p : db.find(Protocol.class, new QueryRule(Protocol.INVESTIGATION_NAME, Operator.EQUALS,
				investigationName)))
		{

			if (!p.getName().equalsIgnoreCase("generic"))
			{

				List<String> subNames = p.getSubprotocols_Name();

				// keep a record of each protocol in a hashmap. Later on we
				// could reference to the Protocol by name
				if (!nameToProtocol.containsKey(p.getName()))
				{
					nameToProtocol.put(p.getName(), p);
				}

				if (!subNames.isEmpty())
				{
					if (!topProtocols.contains(p.getName()))
					{
						topProtocols.add(p.getName());
					}
					for (String subProtocol : subNames)
					{
						if (!middleProtocols.contains(subProtocol))
						{
							middleProtocols.add(subProtocol);
						}
					}
				}
				else
				{

					if (!bottomProtocols.contains(p.getName()))
					{
						bottomProtocols.add(p.getName());
					}
				}
			}
			middleProtocols.removeAll(bottomProtocols);
			topProtocols.removeAll(middleProtocols);
		}

		if (topProtocols.size() == 0)
		{
			return bottomProtocols;
		}
		else
		{
			return topProtocols;
		}
	}

	public String getTreeView()
	{
		return protocolsTree.toHtml(null);
	}

	public List<String> getListOfProtocols()
	{
		return listOfProtocols;
	}

	public JSONObject getDescriptions(String measurementName)
	{
		return descriptions.get(measurementName);
	}
}
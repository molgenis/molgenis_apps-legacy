package plugins.catalogueTree;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.json.JSONException;
import org.json.JSONObject;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.server.MolgenisRequest;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.html.JQueryTreeView;
import org.molgenis.framework.ui.html.JQueryTreeViewElement;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Category;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.protocol.Protocol;
import org.molgenis.util.Entity;
import org.molgenis.util.HttpServletRequestTuple;
import org.molgenis.util.Tuple;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

//import org.molgenis.util.XlsWriter;

public class CatalogueTreePlugin extends PluginModel<Entity>
{

	private static final long serialVersionUID = -6143910771849972946L;
	private JQueryTreeView<JQueryTreeViewElement> treeView = null;
	private HashMap<String, Protocol> nameToProtocol;
	private HashMap<String, JQueryTreeViewElement> protocolsAndMeasurementsinTree;

	// private List<Measurement> shoppingCart = new ArrayList<Measurement>();
	private List<String> arrayInvestigations = new ArrayList<String>();
	private List<String> listOfJSONs = new ArrayList<String>();
	private JSONObject variableInformation = new JSONObject();

	private String selectedInvestigation = null;
	// private String InputToken = null;
	private String selectedField = null;
	private String SelectionName = "empty";

	private boolean isSelectedInv = false;
	private List<String> arraySearchFields = new ArrayList<String>();
	private List<String> SearchFilters = new ArrayList<String>();
	private String Status = "";

	// private static int SEARCHINGPROTOCOL = 2;
	//
	// private static int SEARCHINGMEASUREMENT = 3;
	//
	// private static int SEARCHINGALL = 4;
	//
	// private static int SEARCHINGDETAIL = 5;

	Integer mode;
	private String appLoc;

	/**
	 * Multiple inheritance: some measurements might have multiple parents
	 * therefore it will complain about the branch already exists when
	 * constructing the tree, cheating by changing the name of the branch but
	 * keeping display name the same
	 */

	private HashMap<String, Integer> multipleInheritance = new HashMap<String, Integer>();
	private List<JQueryTreeViewElement> directChildrenOfTop = new ArrayList<JQueryTreeViewElement>();
	private List<String> listOfMeasurements = new ArrayList<String>();

	public CatalogueTreePlugin(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	public String getCustomHtmlHeaders()
	{
		return "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/download_list.css\">";
	}

	@Override
	public String getViewName()
	{
		return "plugins_catalogueTree_CatalogueTreePlugin";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/catalogueTree/catalogueTreePlugin.ftl";
	}

	@Override
	public Show handleRequest(Database db, Tuple request, OutputStream out) throws Exception
	{

		if (out == null)
		{

			this.handleRequest(db, request);

		}
		else
		{

			if (request.getAction().equals("download_json_showInformation"))
			{

				System.out.println("showVariableInformation------------" + request);
				List<String> listOfVariables = request.getStringList("variableName");

				PrintWriter writer = new PrintWriter(out);
				JSONObject jsonVariableInformation = new JSONObject();

				String variableHtmlTable = "";

				for (String eachVariable : listOfVariables)
				{

					if (variableInformation.has(eachVariable))
					{

						variableHtmlTable += variableInformation.get(eachVariable);
					}
				}

				if (!variableHtmlTable.equals(""))
				{
					jsonVariableInformation.put("result", variableHtmlTable);
				}
				else
				{
					jsonVariableInformation.put("result", "There is no information for this variable");
				}
				writer.write(jsonVariableInformation.toString());
				writer.flush();
				writer.close();

			}
			else if (request.getAction().equals("download_json_searchAll"))
			{
				PrintWriter writer = new PrintWriter(out);
				writer.write(variableInformation.toString());
				writer.flush();
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

		appLoc = ((MolgenisRequest) request).getAppLocation();

		System.out.println(">>>>>>>>>>>>>>>>>>>>>Handle request<<<<<<<<<<<<<<<<<<<<" + request);

		// for now the cohorts are investigations
		if ("cohortSelect".equals(request.getAction()))
		{
			System.out.println("----------------------" + request);
			selectedInvestigation = request.getString("cohortSelectSubmit");
			this.setSelectedInvestigation(selectedInvestigation);
			System.out.println("The selected investigation is : " + selectedInvestigation);

		}
		else if (request.getAction().equals("downloadButtonEMeasure"))
		{
			// do output stream ourselves
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH.mm");
			Date date = new Date();
			response.setContentType("application/x-download");
			response.setHeader("Content-Disposition", "attachment; filename=" + "EMeasure_" + dateFormat.format(date)
					+ ".xml");

			PrintWriter pw = response.getWriter();

			// Make E-Measure XML file
			List<Measurement> selectedMeasList = getSelectedMeasurements(db, request);
			EMeasure em = new EMeasure(db, "EMeasure_" + dateFormat.format(date));

			String result = em.convert(selectedMeasList);

			pw.print(result);
			pw.close();
		}
		else if (request.getAction().equals("downloadButton"))
		{

			WorkbookSettings ws = new WorkbookSettings();

			ws.setLocale(new Locale("en", "EN"));

			File tmpDir = new File(System.getProperty("java.io.tmpdir"));

			File mappingResult = new File(tmpDir + File.separator + "selectedVariables.xls");

			WritableWorkbook workbook = Workbook.createWorkbook(mappingResult, ws);

			final WritableSheet outputExcel = workbook.createSheet("Sheet1", 0);

			int row = 0;

			outputExcel.addCell(new Label(0, row, "Selected variables"));

			outputExcel.addCell(new Label(1, row, "Descriptions"));

			outputExcel.addCell(new Label(2, row, "Sector/Protocol"));

			List<Protocol> protocols = db.find(Protocol.class, new QueryRule(Protocol.INVESTIGATION_NAME,
					Operator.EQUALS, selectedInvestigation));

			List<Measurement> measurements = db.find(Measurement.class, new QueryRule(Measurement.INVESTIGATION_NAME,
					Operator.EQUALS, selectedInvestigation));

			row++;

			for (Protocol protocol : protocols)
			{
				for (final Integer featureID : protocol.getFeatures_Id())
				{
					String checkboxID = Measurement.class.getSimpleName() + featureID + Protocol.class.getSimpleName()
							+ protocol.getId();

					if (request.getBool(checkboxID) != null)
					{
						Measurement measurement = Iterables.find(measurements, new Predicate<Measurement>()
						{
							@Override
							public boolean apply(Measurement m)
							{
								return m.getId().equals(featureID);
							}

						}, null);

						outputExcel.addCell(new Label(0, row, measurement.getName()));

						String description = measurement.getDescription() != null ? measurement.getDescription() : "";
						outputExcel.addCell(new Label(1, row, description));

						outputExcel.addCell(new Label(2, row, protocol.getName()));

						row++;
					}

				}
			}

			workbook.write();
			workbook.close();

			HttpServletRequestTuple rt = (HttpServletRequestTuple) request;
			OutputStream outSpecial = rt.getResponse().getOutputStream();
			URL localURL = mappingResult.toURI().toURL();
			URLConnection conn = localURL.openConnection();
			InputStream in = new BufferedInputStream(conn.getInputStream());
			rt.getResponse().setContentType("application/vnd.ms-excel");
			rt.getResponse().setContentLength((int) mappingResult.length());
			rt.getResponse().setHeader("Content-disposition",
					"attachment; filename=\"" + "selectedVariables" + ".xls" + "\"");
			byte[] buffer = new byte[2048];
			for (;;)
			{
				int nBytes = in.read(buffer);
				if (nBytes <= 0) break;
				outSpecial.write(buffer, 0, nBytes);
			}
			outSpecial.flush();
			outSpecial.close();
			EasyPluginController.HTML_WAS_ALREADY_SERVED = true;
		}
		else if (request.getAction().equals("viewButton"))
		{
			List<Measurement> selectedMeasurements = getSelectedMeasurements(db, request);
			req.getRequest().getSession().setAttribute("selectedMeasurements", selectedMeasurements);
			response.sendRedirect(req.getAppLocation() + "/molgenis.do?__target=main&select=phenotypeViewer");
		}

	}

	private List<Measurement> getSelectedMeasurements(Database db, Tuple request) throws DatabaseException
	{

		List<Measurement> measurements = db.find(Measurement.class, new QueryRule(Measurement.INVESTIGATION_NAME,
				Operator.EQUALS, selectedInvestigation));

		List<Measurement> selectedMeasurements = new ArrayList<Measurement>();

		for (Measurement m : measurements)
		{
			for (String fieldName : request.getFieldNames())
			{
				if (fieldName.startsWith(Measurement.class.getSimpleName()))
				{
					// It is a measurement checkbox
					String id = getMeasurementID(fieldName);
					if (id.equals(m.getId().toString()))
					{
						selectedMeasurements.add(m);
					}
				}
			}
		}

		return selectedMeasurements;
	}

	// Get the measurementid from a checkbox name
	// It contains some magic, checkbox id's can be Measurement55 or
	// Measurement55Observation8
	private String getMeasurementID(String checkboxName)
	{
		int startIndex = Measurement.class.getSimpleName().length();
		int endIndex = checkboxName.indexOf(Protocol.class.getSimpleName());

		if (endIndex < 0)
		{
			endIndex = checkboxName.length();
		}

		return checkboxName.substring(startIndex, endIndex);
	}

	@Override
	public void reload(Database db)
	{

		System.out.println("-------------In reload---------------------" + appLoc);

		try
		{
			// default set selected investigation to first

			if (this.getSelectedInvestigation() == null)
			{

				List<Investigation> listOfInvestigation = db.query(Investigation.class).find();
				if (listOfInvestigation.size() > 0)
				{

					int count = 0;

					for (Investigation inv : listOfInvestigation)
					{
						if (db.find(Protocol.class,
								new QueryRule(Protocol.INVESTIGATION_NAME, Operator.EQUALS, inv.getName())).size() > 0)
						{
							if (count == 0)
							{

								this.setSelectedInvestigation(inv.getName());
								count++;
							}
							arrayInvestigations.add(inv.getName());
						}
					}
				}
			}
			arraySearchFields.clear();
			// this.searchingInvestigation = null;
			// this.selectedInvestigation = null;

			arraySearchFields.add("All");
			arraySearchFields.add("Protocols");
			arraySearchFields.add("Measurements");

			RetrieveProtocols(db);

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * This method is used to retrieve all the protocols from the database.
	 * Protocol is a bunch of measurements in Molgenis system, some of which
	 * could have sub-protocols. Therefore three different kinds of protocols
	 * are defined in the method to find the topmost protocols (the ancestors of
	 * all the protocols), which are stored in variable topProtocols. The
	 * topmost protocols are the starting point of the tree. The tree extends to
	 * the next level down with sub-protocols. Until the last level of tree
	 * (last branch in the tree), the measurements are stored in there. The
	 * topmost protocols are passed to the another method called
	 * resursiveAddingNodesToTree, which could recursively add new branches to
	 * the tree.
	 * 
	 * @param db
	 * @param mode
	 */
	public void RetrieveProtocols(Database db)
	{

		List<String> topProtocols = new ArrayList<String>();
		List<String> bottomProtocols = new ArrayList<String>();
		List<String> middleProtocols = new ArrayList<String>();
		variableInformation = new JSONObject();
		protocolsAndMeasurementsinTree = new HashMap<String, JQueryTreeViewElement>();
		multipleInheritance.clear();
		listOfMeasurements.clear();

		nameToProtocol = new HashMap<String, Protocol>();

		try
		{

			Query<Protocol> q = db.query(Protocol.class);

			q.addRules(new QueryRule(Protocol.INVESTIGATION_NAME, Operator.EQUALS, this.selectedInvestigation));

			// Iterate through all the found protocols
			for (Protocol p : q.find())
			{

				if (!p.getName().equalsIgnoreCase("generic"))
				{

					setSelectedInv(true);
					List<String> subNames = p.getSubprotocols_Name();

					// keep a record of each protocol in a hashmap. Later on we
					// could reference to the Protocol by name
					if (!nameToProtocol.containsKey(p.getName()))
					{
						nameToProtocol.put(p.getName(), p);
					}

					/**
					 * Algorithm to find the topmost protocols. There are three
					 * kind of protocols needed. 1. The protocols that are
					 * parents of other protocols 2. The protocols that are
					 * children of some other protocols and at the same time are
					 * parents of some other protocols 3. The protocols that are
					 * only children of other protocols Therefore we could do
					 * protocol2 = protocol2.removeAll(protocol3) ----> parent
					 * protocols but not topmost we then do protocol1 =
					 * protocol1.removeAll(protocol2) topmost parent protocols
					 */
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

		}
		catch (DatabaseException e)
		{
			e.printStackTrace();
		}

		// Create a starting point of the tree! The root of the tree!
		JQueryTreeViewElement protocolsTree = new JQueryTreeViewElement("Study_"
				+ this.getSelectedInvestigation().replaceAll(" ", "_"), "", null);
		protocolsTree.setLabel("Study: " + this.getSelectedInvestigation());

		// Variable indicating whether the input token has been found.
		boolean foundInputToken = false;

		if (topProtocols.size() == 0)
		{ // The protocols don`t have
			// sub-protocols and we could directly
			// find the measurements of protocols
			recursiveAddingNodesToTree(bottomProtocols, protocolsTree.getName(), protocolsTree, db, foundInputToken,
					mode);

		}
		else
		{ // The protocols that have sub-protocols, then we recursively
			// find sub-protocols
			recursiveAddingNodesToTree(topProtocols, protocolsTree.getName(), protocolsTree, db, foundInputToken, mode);
		}

		directChildrenOfTop = protocolsTree.getChildren();

		System.out.println(protocolsTree.getName());
		System.out.println(">>>Protocols tree: " + protocolsTree + "tree elements: "
				+ protocolsTree.getTreeElements().containsKey("Questionnaire"));

		boolean freshTree = false;

		for (JQueryTreeViewElement element : directChildrenOfTop)
		{
			if (protocolsTree.getTreeElements().containsKey(element.getName()))
			{
				freshTree = true;
			}
		}

		if (freshTree)
		{
			// After traverse through the tree, all the elements should have
			// fallen
			// in the right places of the tree, now create the tree view
			treeView = new JQueryTreeView<JQueryTreeViewElement>("Protocols", protocolsTree);
		}
		else
		{
			// Search result is empty or tree is empty
			this.getModel()
					.getMessages()
					.add(new ScreenMessage(
							"There are no results to show. Please, redifine your search or import some data.", true));

			// this.setStatus("<h4> There are no results to show. Please, redifine your search or import some data."
			// + "</h4>");

			this.setError("There are no results to show. Please, redifine your search or import some data.");

		}
	}

	/**
	 * This method is used to recursively find all the sub-protocols of topmost
	 * protocols by recursively calling itself. The method returns a boolean
	 * value to indicate whether the input token has been found in its
	 * sub-nodes.
	 * 
	 * @param nextNodes
	 * @param parentClassName
	 * @param parentNode
	 * @param db
	 * @param foundTokenInParentProtocol
	 *            found token in parent protocol but not in its sub-protocols or
	 *            measurements.
	 * @param mode
	 * @return
	 */

	public void recursiveAddingNodesToTree(List<String> nextNodes, String parentClassName,
			JQueryTreeViewElement parentNode, Database db, boolean foundTokenInParentProtocol, Integer mode)
	{

		// Create a findInputInNextAllToken variable to keep track of whether
		// the sub-nodes contain any input token. If neither of the children
		// contains the input token
		// this variable should be false.
		// boolean findInputTokenInNextAllNodes = false;

		// Create a variable to keep track of ONLY ONE sub-node of the current
		// node. If the variable is false, that means there is no token found in
		// this one branch.

		// Loop through all the nodes on this level.
		for (String protocolName : nextNodes)
		{

			Protocol protocol = nameToProtocol.get(protocolName);

			JQueryTreeViewElement childTree = null;

			if (protocol != null)
			{

				/**
				 * Resolve the issue of duplicated names in the tree. For any
				 * sub-protocols or measurements could belong to multiple parent
				 * class, so it`ll throw an error if we try to create the same
				 * element twice Therefore we need to give a unique identifier
				 * to the tree element but assign the same value to the display
				 * name.
				 */

				if (protocolsAndMeasurementsinTree.containsKey(protocolName))
				{
					if (!multipleInheritance.containsKey(protocolName))
					{
						multipleInheritance.put(protocolName, 1);
					}
					else
					{
						int number = multipleInheritance.get(protocolName);
						multipleInheritance.put(protocolName, ++number);
					}

					childTree = new JQueryTreeViewElement(protocolName + "_identifier_"
							+ multipleInheritance.get(protocolName), protocolName, Protocol.class.getSimpleName()
							+ protocol.getId().toString() + "_identifier_" + multipleInheritance.get(protocolName),
							parentNode);

				}
				else
				{

					// The tree first time is being created.
					childTree = new JQueryTreeViewElement(protocolName, Protocol.class.getSimpleName()
							+ protocol.getId().toString(), parentNode);
					childTree.setCollapsed(true);
					protocolsAndMeasurementsinTree.put(protocolName, childTree);
				}

				if (protocolName.equalsIgnoreCase("GenericDCM"))
				{
					childTree.setCheckBox(true);
				}
				if (protocolName.equalsIgnoreCase("stageCatalogue"))
				{
					childTree.setCheckBox(true);
				}
				// else{
				// childTree.setCheckBox(false);
				// }

				if (childTree.getParent().getCheckBox())
				{
					childTree.setCheckBox(true);
				}

				if (!protocolName.equals(parentClassName))
				{

					boolean subProtocolRepeatProtocol = false;

					// find all the sub-protocols and recursively call itself
					if (protocol.getSubprotocols_Name() != null && protocol.getSubprotocols_Name().size() > 0)
					{

						List<String> subProtocolNames = protocol.getSubprotocols_Name();

						if (subProtocolNames.contains(protocolName))
						{
							subProtocolRepeatProtocol = true;
						}
						if (subProtocolNames.contains(parentClassName))
						{
							subProtocolNames.remove(parentClassName);
						}
						recursiveAddingNodesToTree(subProtocolNames, protocol.getName(), childTree, db,
								foundTokenInParentProtocol, mode);
					}

					// On the last branch of the tree, we`ll find measurements
					// and
					// add them to the tree.
					if (subProtocolRepeatProtocol == false && protocol.getFeatures_Name() != null
							&& protocol.getFeatures_Name().size() > 0)
					{ // error
						// checking

						addingMeasurementsToTree(protocol, childTree, db, false, mode); // ..
																						// so
																						// normally
																						// it
																						// goes
																						// always
						// this way
					}

				}
				else if (protocolName.equals(parentClassName))
				{

					if (protocol.getFeatures_Name() != null && protocol.getFeatures_Name().size() > 0)
					{ // error
						// checking

						addingMeasurementsToTree(protocol, childTree, db, false, mode); // ..
																						// so
																						// normally
																						// it
																						// goes
																						// always
						// this way
					}
				}
			}
		}
	}

	/**
	 * this is adding the measurements as references in
	 * recursiveAddingNodesToTree().
	 * 
	 * @param childNode
	 * @param parentNode
	 * @param db
	 * @throws DatabaseException
	 */
	public boolean addingMeasurementsToTree(Protocol protocol, JQueryTreeViewElement parentNode, Database db,
			boolean foundInParent, Integer mode)
	{

		List<String> childNode = protocol.getFeatures_Name();

		// Create a variable to store the boolean value with which we could know
		// whether we need to skip these measurements of the protocol.
		// if none of the measurements contain input token, it`s false meaning
		// these measurements will not be shown in the tree.
		boolean findTokenInMeasurements = false;

		// indicate with the input token has been found in detail information in
		// the measurement.
		// boolean findTokenInDetailInformation = false;

		// This variables store the measurements that conform to the
		// requirements by the mode that has been selected.
		// For example, it only contains the measurement where the input token
		// has been found under mode searchingMeasurement
		// List<String> filteredNode = new ArrayList<String>();

		try
		{

			// // If the input token is available, we need to check which mode
			// it is
			// // and decide what we do with it here
			// if (InputToken != null) {
			//
			// // In mode of searching for measurements, we check if the name of
			// // measurements contain the input token
			// // If the token is not in the name, the measurement is removed
			// from
			// // list.
			// if (mode == SEARCHINGMEASUREMENT) {
			//
			// for(Measurement m : db.find(Measurement.class, new
			// QueryRule(Measurement.NAME, Operator.IN, childNode))){
			//
			// if (m.getName().toLowerCase().matches(".*" +
			// InputToken.toLowerCase() + ".*")) {
			// filteredNode.add(m.getName());
			// findTokenInMeasurements = true;
			// } else if (m.getLabel() != null &&
			// m.getLabel().toLowerCase().matches(".*" +
			// InputToken.toLowerCase() + ".*")) {
			// filteredNode.add(m.getName());
			// findTokenInMeasurements = true;
			// }
			//
			// }
			//
			// } else {
			// // In mode of searching for all fields, details, we need to loop
			// // through all the measurements, therefore
			// // we do not care whether the measurement name contains the
			// // input token or not.
			// filteredNode = childNode;
			// }
			//
			// } else {
			// // Normal mode when the input token is not available
			// filteredNode = childNode;
			// }
			//

			List<Measurement> measurementList = db.find(Measurement.class, new QueryRule(Measurement.NAME, Operator.IN,
					childNode));

			List<Measurement> filteredMeasurementsList = new ArrayList<Measurement>();

			for (Measurement m : measurementList)
			{
				if (m.getName().equals("PA_ID") || m.getName().equals("ID") || m.getName().equals("BEZOEKNR"))
				{

				}
				else
				{
					filteredMeasurementsList.add(m); // FILTERED LIST WITHOUT
														// PA_ID, ID and
														// BEZOEKNR
				}
			}

			for (Measurement measurement : filteredMeasurementsList)
			{

				// reset the the variable to false
				// findTokenInDetailInformation = false;

				JQueryTreeViewElement childTree = null;

				// Query the display name! For some measurements, the labels
				// were stored in the observedValue with feature_name
				// "display name". If the display name is not available, we`ll
				// use the measurement name as label
				String displayName = "";

				if (measurement.getLabel() != null && !measurement.getLabel().equals(""))
				{

					displayName = measurement.getLabel();
				}
				else
				{
					displayName = measurement.getName();
				}

				// Check if the tree has already had the treeElement with the
				// same name cos the name can not be duplicated in
				// jquery tree here. Therefore if the element already existed, a
				// suffix will be added at the end of string to
				// make the name unique

				// displayName = displayName.replaceAll("[%#]", "");

				String uniqueName = "";

				if (displayName.equalsIgnoreCase("VALCOMM_1"))
				{
					System.out.println();
				}

				if (protocolsAndMeasurementsinTree.containsKey(displayName))
				{

					if (!multipleInheritance.containsKey(displayName))
					{
						multipleInheritance.put(displayName, 1);
					}
					else
					{
						int number = multipleInheritance.get(displayName);
						multipleInheritance.put(displayName, ++number);
					}

					childTree = new JQueryTreeViewElement(displayName + "_identifier_"
							+ multipleInheritance.get(displayName), displayName, Measurement.class.getSimpleName()
							+ measurement.getId().toString() + "_identifier_" + multipleInheritance.get(displayName),
							parentNode);

					uniqueName = displayName + "_identifier_" + multipleInheritance.get(displayName);

					listOfMeasurements.add(uniqueName);

				}
				else
				{

					childTree = new JQueryTreeViewElement(displayName, Measurement.class.getSimpleName()
							+ measurement.getId() + Protocol.class.getSimpleName() + protocol.getId(), parentNode);

					uniqueName = displayName;

					listOfMeasurements.add(displayName);

					protocolsAndMeasurementsinTree.put(displayName, childTree);
				}
				// Query the all the detail information about this measurement,
				// in molgenis terminology, the detail information
				// are all the observedValue and some of the fields from the
				// measurement
				String htmlValue = null;

				htmlValue = htmlTableForTreeInformation(db, measurement, uniqueName);

				JSONObject json = new JSONObject();

				try
				{

					json.put(uniqueName.replaceAll(" ", "_"), htmlValue);
					variableInformation.put(uniqueName.replaceAll(" ", "_"), htmlValue);
				}
				catch (JSONException e)
				{
					e.printStackTrace();
				}

				listOfJSONs.add(json.toString());

			}

		}
		catch (DatabaseException e)
		{
			e.printStackTrace();
		}

		// Return this round searching result back to the parent node
		return findTokenInMeasurements;
	}

	/**
	 * This method is used to create a html table populated with all the
	 * information about one specific measurement
	 * 
	 * @param db
	 * @param measurement
	 * @return
	 * @throws DatabaseException
	 */
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

		// String htmlValue = "<table id = 'detailInformation'  border = 2>" +
		String htmlValue = "<table style='border-spacing: 2px; width: 100%;' class='MeasurementDetails' id = '"
				+ nodeName + "_table'>";
		htmlValue += "<tr><td class='box-body-label'>Current selection:</th><td id=\"" + nodeName
				+ "_itemName\"style=\"cursor:pointer\">" + displayName + "</td></tr>";

		if (categoryNames.size() > 0)
		{

			List<Category> listOfCategory = db.find(Category.class, new QueryRule(Category.NAME, Operator.IN,
					categoryNames));

			htmlValue += "<tr id='" + nodeName + "_category'><td  class='box-body-label'>Category:</td><td><table>";

			String missingCategory = "<tr><td  class='box-body-label'>Missing category:</td><td><table>";

			for (Category c : listOfCategory)
			{

				String codeString = c.getCode_String();

				if (!codeString.equals(""))
				{
					codeString += " = ";
				}
				if (!c.getIsMissing())
				{
					htmlValue += "<tr><td>";
					htmlValue += codeString + c.getDescription();
					htmlValue += "</td></tr>";
				}
				else
				{
					missingCategory += "<tr><td>";
					missingCategory += codeString + c.getDescription();
					missingCategory += "</td></tr>";
				}
			}

			htmlValue += "</table></td></tr>";

			htmlValue += missingCategory + "</table>";
		}

		htmlValue += "<tr id='" + nodeName + "_description'><td class='box-body-label'>Description:</td><td>"
				+ (measurementDescription == null ? "not provided" : measurementDescription) + "</td></tr>";

		htmlValue += "<tr id='" + nodeName + "_dataType'><td class='box-body-label'>Data type:</th><td>"
				+ measurementDataType + "</td></tr>";

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
					htmlValue += "<tr><td class='box-body-label'>" + featureName + "</td><td><a href=" + value + ">"
							+ value + "</a></td></tr>";
				}
				else
				{

					if (featureName.startsWith("display name"))
					{
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

	public String getTreeView()
	{

		List<String> selected = new ArrayList<String>();

		// don't select, is confusing...
		// for (Measurement m : shoppingCart) {
		// selected.add(m.getName());
		// }

		String htmlTreeView = treeView.toHtml(selected);

		// This piece of javascript need to be here because some java calls are
		// needed.
		// String measurementClickEvent = "<script>";
		//
		// List<String> uniqueMeasurementName = new ArrayList<String>();
		//
		// System.out.println("listOfMeasurements>>>"+listOfMeasurements);
		//
		// for(String eachMeasurement : listOfMeasurements){
		//
		// if(!uniqueMeasurementName.contains(eachMeasurement)){
		//
		// uniqueMeasurementName.add(eachMeasurement);
		//
		// if(eachMeasurement.equals("Year partner son daughter 3")){
		// System.out.println();
		// }
		// measurementClickEvent += "$('#" + eachMeasurement.replaceAll(" ",
		// "_") + "').click(function() {"
		// + "getHashMapContent(\"" + eachMeasurement + "\");});"
		// + "";
		// }
		// }
		// measurementClickEvent += "</script>";
		//
		// htmlTreeView += measurementClickEvent;

		return htmlTreeView;
	}

	public void setArrayInvestigations(List<String> arrayInvestigations)
	{
		this.arrayInvestigations = arrayInvestigations;
	}

	public List<String> getArrayInvestigations()
	{

		return arrayInvestigations;
	}

	public void setSelectedInv(boolean isSelectedInv)
	{
		this.isSelectedInv = isSelectedInv;
	}

	public boolean isSelectedInv()
	{
		return isSelectedInv;
	}

	public String getSelectedInvestigation()
	{
		return selectedInvestigation;
	}

	public void setSelectedInvestigation(String selectedInvestigation)
	{
		this.selectedInvestigation = selectedInvestigation;
	}

	public void setArraySearchFields(List<String> arraySearchFields)
	{
		this.arraySearchFields = arraySearchFields;
	}

	public List<String> getArraySearchFields()
	{
		return arraySearchFields;
	}

	public void setSelectedField(String selectedField)
	{
		this.selectedField = selectedField;
	}

	public String getSelectedField()
	{
		return selectedField;
	}

	public List<String> getFilters()
	{
		return SearchFilters;
	}

	public String getInheritance()
	{
		return variableInformation.toString();
	}

	public List<String> getListOfJSONs()
	{
		return listOfJSONs;
	}

	public void setSelectionName(String selectionName)
	{
		SelectionName = selectionName;
	}

	public String getSelectionName()
	{
		return SelectionName;
	}

	public void setStatus(String status)
	{
		Status = status;
	}

	public String getStatus()
	{
		return Status;
	}

	public String getUrl()
	{
		return "molgenis.do?__target=" + this.getName();
	}

}

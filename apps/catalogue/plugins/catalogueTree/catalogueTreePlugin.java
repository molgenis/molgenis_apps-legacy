package plugins.catalogueTree;

import gcc.catalogue.ShoppingCart;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.write.WriteException;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.security.Login;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.html.JQueryTreeView;
import org.molgenis.framework.ui.html.JQueryTreeViewElement;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.protocol.Protocol;
import org.molgenis.util.Entity;
import org.molgenis.util.HttpServletRequestTuple;
import org.molgenis.util.Tuple;


//import org.molgenis.util.XlsWriter;



public class catalogueTreePlugin extends PluginModel<Entity> {

	private static final long serialVersionUID = -6143910771849972946L;
	private JQueryTreeView<JQueryTreeViewElement> treeView = null;
	private HashMap<String, Protocol> nameToProtocol;
	private HashMap<String, JQueryTreeViewElement> protocolsAndMeasurementsinTree;

	private List<Measurement> shoppingCart = new ArrayList<Measurement>();
	private List<Investigation> arrayInvestigations = new ArrayList<Investigation>();
	private String selectedInvestigation = null;
	private String InputToken = null;
	private String comparison = null;
	private String selectedField = null;
	private String SelectionName = "empty";

	private boolean isSelectedInv = false;
	private List<String> arraySearchFields = new ArrayList<String>();
	private List<String> SearchFilters = new ArrayList<String>();
	private String Status = "";


	private static int SEARCHINGPROTOCOL = 2;

	private static int SEARCHINGMEASUREMENT = 3;

	private static int SEARCHINGALL = 4;

	private static int SEARCHINGDETAIL = 5;

	Integer mode;

	/** Multiple inheritance: some measurements might have multiple parents therefore it
	 *  will complain about the branch already exists when constructing the tree, cheating by
	 *  changing the name of the branch but keeping display name the same
	 */

	private HashMap<String, Integer> multipleInheritance = new HashMap<String, Integer>();
	private List<JQueryTreeViewElement> directChildrenOfTop = new ArrayList<JQueryTreeViewElement>();
	private List<String> listOfMeasurements = new ArrayList<String>();

	public catalogueTreePlugin(String name, ScreenController<?> parent) {
		super(name, parent);
	}

	public String getCustomHtmlHeaders() {
		return "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/download_list.css\">";
	}

	@Override
	public String getViewName() {
		return "plugins_catalogueTree_catalogueTreePlugin";
	}

	@Override
	public String getViewTemplate() {
		return "plugins/catalogueTree/catalogueTreePlugin.ftl";
	}

	public void handleRequest(Database db, Tuple request) throws Exception {

		System.out.println(">>>>>>>>>>>>>>>>>>>>>Handle request<<<<<<<<<<<<<<<<<<<<" + request);
		//for now the cohorts are investigations 
		if ("cohortSelect".equals(request.getAction())) {
			System.out.println("----------------------"+request);
			selectedInvestigation = request.getString("cohortSelectSubmit");
			this.setSelectedInvestigation(selectedInvestigation);
			System.out.println("The selected investigation is : "+ selectedInvestigation);
			arrayInvestigations.clear();
			this.SearchFilters.clear();

		}else if ("SaveSelectionSubmit".equals(request.getAction())) {

			if (!this.getLogin().isAuthenticated()) {
				this.getModel().getMessages().add(new ScreenMessage("You must register in order to save a selection. Please select register from the main menu. ",  true));
				this.setStatus("<h4> You must register in order to save a selection. Please select register from the main menu. "+ "</h4>" ) ;
			} else {

				try	{
					this.setSelectionName("empty");

					if (request.getString("SelectionName") != null) {

						this.setSelectionName(request.getString("SelectionName").trim());
						System.out.println("The SelectionName is >>> : " + this.getSelectionName());					
						System.out.println("Selection request >>>>>>" + request);
					} else {
						//this.setError("Please insert a name for your selection and try again.");
						this.getModel().getMessages().add(new ScreenMessage("No name was inserted for the selection. An automatic name will be generated. ",  true));
						this.setStatus("<h4> No name was inserted for the selection. An automatic name will be generated. "+ "</h4>" ) ;
					}

					DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
					Date dat = new Date();
					String dateOfDownload = dateFormat.format(dat);
					System.out.println("selected investigaton >>>> "+  selectedInvestigation);
					this.addMeasurementsForDownload(db, request, selectedInvestigation, dateOfDownload, this.getSelectionName());
				} catch (IOException e) {
					e.printStackTrace();
				} catch (WriteException e1) {
					e1.printStackTrace();
				}
			}
		} else if (request.getAction().startsWith("DeleteMeasurement")) {

			String measurementName = request.getString("measurementName"); 
			measurementName = request.getAction().substring( "DeleteMeasurement".length() + 2+ "measurementName".length(),	request.getAction().length());
			this.deleteShoppingItem(measurementName);

		} else if (request.getAction().startsWith("SearchCatalogueTree")) {

			if (request.getString("InputToken") != null) {

				this.setInputToken(request.getString("InputToken").trim());		
				System.out.println("The request string : " + request);
				this.setSelectedField(request.getString("selectedField"));
				System.out.println("Input token: >>>>>>"
						+ this.getInputToken() + ">>> selectedField >>"
						+ selectedField + "comparison >>>"
						+ this.getComparison());
				if (this.getSelectedField().equals("Protocols")) {
					//Show filters label 
					this.getModel().getMessages().add(new ScreenMessage("Showing results for :" + this.getInputToken() + " in Protocols ",  true));
					this.setStatus("<h4> Showing results for :" + this.getInputToken() + " in Protocols "+ "</h4>" ) ;
					SearchFilters.add("Protocols:" + this.getInputToken());

					mode = SEARCHINGPROTOCOL;
					RetrieveProtocols(db, SEARCHINGPROTOCOL);
				}
				// Search "Any field" ==> All fields LIKE input token
				if (this.getSelectedField().equals("Measurements")) {
					//Show filters label 
					this.getModel().getMessages().add(new ScreenMessage("Showing results for :" + this.getInputToken() + " in Measurements ",  true));
					this.setStatus("<h4> Showing results for :" + this.getInputToken() + " in Measurements  "+ "</h4>" ) ;

					SearchFilters.add("Measurements:" + this.getInputToken());
					mode = SEARCHINGMEASUREMENT;
					RetrieveProtocols(db, SEARCHINGMEASUREMENT);
				}
				if (this.getSelectedField().equals("All fields")) {
					//Show filters label 
					this.getModel().getMessages().add(new ScreenMessage("Showing results for :" + this.getInputToken() + " in all fields ",  true));
					this.setStatus("<h4> Showing results for :" + this.getInputToken() + " in all fields "+ "</h4>" ) ;

					SearchFilters.add("All:" + this.getInputToken());

					mode = SEARCHINGALL;
					RetrieveProtocols(db, SEARCHINGALL);
				}
				if (this.getSelectedField().equals("Details")) {
					//Show filters label 
					this.getModel().getMessages().add(new ScreenMessage("Showing results for :" + this.getInputToken() + " in Details ",  true));
					this.setStatus("<h4> Showing results for :" + this.getInputToken() + " in Details " + "</h4>" ) ;

					SearchFilters.add("Details:" + this.getInputToken());

					mode = SEARCHINGDETAIL;
					RetrieveProtocols(db, SEARCHINGDETAIL);
				}


				//show a label with the search filter and an option to remove it 
				//in case of removal of filter , reload the tree

			} else {
				this.getModel().getMessages().add(new ScreenMessage("Empty search string", true));
				this.setStatus("<h4> Empty search string" + "</h4>" ) ;

			}
		} else if (request.getString("measurementId") != null) {
			System.out.println("-request.getString(measurementId-----------" +  request.getString("measurementId"));

		}




	}


	@Override
	public void reload(Database db) {

		//where is request oeo???
		//request content: select='CatalogueTreePlugin' measurementId='1' __target='main' 

		//		this.getParent().getRoot().getModel().getController()
		//		Login login = (Login)request.getRequest().getSession().getAttribute("login");

		//db.getLogin().getClass().getGenericSuperclass().g
		System.out.println("-------------In reload---------------------" + this.getApplicationUrl());



		//		if (this.request!=null && this.request.getString("measurementId") != null) {
		//			System.out.println("-request.getString(measurementId-----------" +  request.getString("measurementId"));
		//		}
		// default set selected investigation to first
		if (this.getSelectedInvestigation() == null) {
			try {
				List<Investigation> inv = db.query(Investigation.class)
						.limit(1).find();
				if (inv.size() > 0)
					this.setSelectedInvestigation(inv.get(0).getName());
			} catch (DatabaseException e1) {
				e1.printStackTrace();
			}
		}

		arraySearchFields.clear();
		// this.searchingInvestigation = null;
		// this.selectedInvestigation = null;

		arraySearchFields.add("All fields");
		arraySearchFields.add("Protocols");
		arraySearchFields.add("Measurements");
		arraySearchFields.add("Details");

		// Query<ShoppingCart> q = db.query(ShoppingCart.class);
		// q.addRules(new QueryRule(ShoppingCart.USERID, Operator.EQUALS, this
		// .getLogin().getUserName()));
		// q.addRules(new QueryRule(ShoppingCart.CHECKEDOUT, Operator.EQUALS,
		// false));
		try {
			// List<ShoppingCart> result = q.find();
			// shoppingCart.clear();
			// for (ShoppingCart cart : result) {
			// shoppingCart.addAll(cart.getMeasurements(db));
			// }

			this.arrayInvestigations.clear();

			for (Investigation i : db.find(Investigation.class)) {
				this.arrayInvestigations.add(i);
			}
		} catch (DatabaseException e) {
			e.printStackTrace();
		}

		if (this.getInputToken() == null)
			RetrieveProtocols(db, 1); // mode 1: gets all protocols without filters!

		this.setInputToken(null);

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
	public void RetrieveProtocols(Database db, Integer mode) {

		List<String> topProtocols = new ArrayList<String>();
		List<String> bottomProtocols = new ArrayList<String>();
		List<String> middleProtocols = new ArrayList<String>();
		protocolsAndMeasurementsinTree = new HashMap<String, JQueryTreeViewElement>();
		listOfMeasurements.clear();

		// measurementsInTree = new HashMap<String, JQueryTreeViewElement>();
		// protocolsInTree = new HashMap<String, JQueryTreeViewElement>();

		nameToProtocol = new HashMap<String, Protocol>();

		try {

			Query<Protocol> q = db.query(Protocol.class);

			q.addRules(new QueryRule(Protocol.INVESTIGATION_NAME,
					Operator.EQUALS, this.selectedInvestigation));

			// Iterate through all the found protocols
			for (Protocol p : q.find()) {

				setSelectedInv(true);
				List<String> subNames = p.getSubprotocols_Name();

				// keep a record of each protocol in a hashmap. Later on we
				// could reference to the Protocol by name
				if (!nameToProtocol.containsKey(p.getName())) {
					nameToProtocol.put(p.getName(), p);
				}

				/**
				 *  Algorithm to find the topmost protocols. There are three kind
				 *   of protocols needed.
				 *   1. The protocols that are parents of other protocols
				 *   2. The protocols that are children of some other protocols
				 *   and at the same time are parents of some other protocols
				 *   3. The protocols that are only children of other protocols
				 *   Therefore we could do protocol2 =
				 *   protocol2.removeAll(protocol3) ----> parent protocols but not topmost
				 *   we then do protocol1 = protocol1.removeAll(protocol2) 
				 *   topmost parent protocols
				 */
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

				middleProtocols.removeAll(bottomProtocols);
				topProtocols.removeAll(middleProtocols);
			}

		} catch (DatabaseException e) {
			e.printStackTrace();
		}

		// Create a starting point of the tree! The root of the tree!
		JQueryTreeViewElement protocolsTree = new JQueryTreeViewElement(
				"Study: " + this.getSelectedInvestigation(), null);

		// Variable indicating whether the input token has been found.
		boolean foundInputToken = false;

		if (topProtocols.size() == 0) { // The protocols don`t have
			// sub-protocols and we could directly
			// find the measurements of protocols
			recursiveAddingNodesToTree(bottomProtocols,
					protocolsTree.getName(), protocolsTree, db,
					foundInputToken, mode);

		} else { // The protocols that have sub-protocols, then we recursively
			// find sub-protocols
			recursiveAddingNodesToTree(topProtocols, protocolsTree.getName(),
					protocolsTree, db, foundInputToken, mode);
		}

		if(mode == 1){
			directChildrenOfTop = protocolsTree.getChildren();
		}

		System.out.println(protocolsTree.getName());
		System.out.println(">>>Protocols tree: "+ protocolsTree + "tree elements: "+ protocolsTree.getTreeElements().containsKey("Questionnaire"));

		boolean freshTree = false;

		for(JQueryTreeViewElement element : directChildrenOfTop){
			if(protocolsTree.getTreeElements().containsKey(element.getName())){
				freshTree = true;
			}
		}

		if (freshTree) { 
			// After traverse through the tree, all the elements should have fallen
			// in the right places of the tree, now create the tree view
			treeView = new JQueryTreeView<JQueryTreeViewElement>("Protocols", protocolsTree);
		} else {
			//Search result is empty or tree is empty 
			this.getModel().getMessages().add(new ScreenMessage("There are no results to show. Please, redifine your search or import some data.",true));
			this.setStatus("<h4> There are no results to show. Please, redifine your search or import some data." + "</h4>" ) ;
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
	 * @param parentTree
	 * @param db
	 * @param foundTokenInParentProtocol
	 *            found token in parent protocol but not in its sub-protocols or
	 *            measurements.
	 * @param mode
	 * @return
	 */

	public boolean recursiveAddingNodesToTree(List<String> nextNodes,
			String parentClassName, JQueryTreeViewElement parentTree,
			Database db, boolean foundTokenInParentProtocol, Integer mode) {

		// Create a findInputInNextAllToken variable to keep track of whether
		// the sub-nodes contain any input token. If neither of the children
		// contains the input token
		// this variable should be false.
		boolean findInputTokenInNextAllNodes = false;

		// Create a variable to keep track of ONLY ONE sub-node of the current
		// node. If the variable is false, that means there is no token found in
		// this one branch.

		// Loop through all the nodes on this level.
		for (String protocolName : nextNodes) {

			boolean findInputTokenInEachNode = false;

			if(protocolName.equalsIgnoreCase("residence")){
				System.out.println();
			}

			Protocol protocol = nameToProtocol.get(protocolName);

			JQueryTreeViewElement childTree = null;

			if (protocol != null) {

				/**
				 *  Resolve the issue of duplicated names in the tree. For any
				 *  sub-protocols or measurements could	 belong to multiple parent class, so it`ll throw an error if
				 *  we try to create the same element twice
				 *  Therefore we need to give a unique identifier to the tree element but assign the same value to the display name.
				 */

				if (protocolsAndMeasurementsinTree.containsKey(protocolName)) {
					if (!multipleInheritance.containsKey(protocolName)) {
						multipleInheritance.put(protocolName, 1);
					} else {
						int number = multipleInheritance.get(protocolName);
						multipleInheritance.put(protocolName, ++number);
					}

					childTree = new JQueryTreeViewElement(protocolName
							+ multipleInheritance.get(protocolName),
							protocolName, parentTree);

				} else {

					// The tree first time is being created.
					childTree = new JQueryTreeViewElement(protocolName,
							parentTree);
					childTree.setCollapsed(true);
					protocolsAndMeasurementsinTree.put(protocolName, childTree);
				}

				if(!protocolName.equals(parentClassName)){

					boolean subProtocolRepeatProtocol = false;

					// find all the sub-protocols and recursively call itself
					if (protocol.getSubprotocols_Name() != null
							&& protocol.getSubprotocols_Name().size() > 0) {

						List<String> subProtocolNames = protocol.getSubprotocols_Name();

						if(subProtocolNames.contains(protocolName)){
							subProtocolRepeatProtocol = true;
						}
						if(subProtocolNames.contains(parentClassName)){
							subProtocolNames.remove(parentClassName);
						}
						findInputTokenInEachNode = recursiveAddingNodesToTree(
								subProtocolNames,
								protocol.getName(), childTree, db,
								foundTokenInParentProtocol, mode);
					}

					// On the last branch of the tree, we`ll find measurements and
					// add them to the tree.
					if (subProtocolRepeatProtocol == false && protocol.getFeatures_Name() != null
							&& protocol.getFeatures_Name().size() > 0) { // error
						// checking

						if (mode != SEARCHINGPROTOCOL) {
							findInputTokenInEachNode = addingMeasurementsToTree(
									protocol.getFeatures_Name(), childTree, db,
									false, mode); // .. so normally it goes always
							// this way
						}
					}
				}else if(protocolName.equals(parentClassName)){

					if (protocol.getFeatures_Name() != null
							&& protocol.getFeatures_Name().size() > 0) { // error
						// checking

						if (mode != SEARCHINGPROTOCOL) {
							findInputTokenInEachNode = addingMeasurementsToTree(
									protocol.getFeatures_Name(), childTree, db,
									false, mode); // .. so normally it goes always
							// this way
						}
					}
				}


				// If the input token is not null, the tree will be filtered, in
				// another word, part of the tree elements
				// will be deleted according to different mode that has been
				// selected.
				if (InputToken != null) {



					// If none of the child nodes, such as none of the
					// measurements of this protocol, contains the input token,
					// this protocol is not added but removed.
					if (findInputTokenInEachNode == true) {

						if (mode == SEARCHINGALL){

							if (protocolName.toLowerCase().matches(
									".*" + InputToken.toLowerCase()
									+ ".*")) {

								for(JQueryTreeViewElement element : childTree.getAllChildren()){
									element.remove();
								}

								if (!foundTokenInParentProtocol
										&& !protocolName.toLowerCase().matches(
												".*" + InputToken.toLowerCase()
												+ ".*")) {

									childTree.remove();

								} else {

									if(!protocolName.equals(parentClassName)){

										boolean subProtocolRepeatProtocol = false;
										// If the input token is found in the current
										// protocol, re-add all its descendants to the
										// tree. Because
										// its sub-nodes might not contain the input
										// token therefore they might have been removed
										// from the tree already.
										// Therefore need to be re-added
										if (protocol.getSubprotocols_Name().size() > 0) {

											List<String> subProtocolNames = protocol.getSubprotocols_Name();

											if(subProtocolNames.contains(protocolName)){
												subProtocolRepeatProtocol = true;
											}
											if(subProtocolNames.contains(parentClassName)){
												subProtocolNames.remove(parentClassName);
											}

											findInputTokenInEachNode = recursiveAddingNodesToTree(
													protocol.getSubprotocols_Name(),
													protocol.getName(), childTree, db,
													true, mode);
											findInputTokenInEachNode = true;
										}
										// This is the case where none of the
										// measurements of this protocol match the input
										// token, but the current protocol
										// matches input token. Therefore its
										// measurements need to be re-added to the tree
										if (subProtocolRepeatProtocol == false && protocol.getFeatures_Name() != null
												&& protocol.getFeatures_Name().size() > 0) {
											findInputTokenInEachNode = addingMeasurementsToTree(
													protocol.getFeatures_Name(),
													childTree, db, true, mode);
											findInputTokenInEachNode = true;
										}
									}else{
										// This is the case where none of the
										// measurements of this protocol match the input
										// token, but the current protocol
										// matches input token. Therefore its
										// measurements need to be re-added to the tree
										if (protocol.getFeatures_Name() != null
												&& protocol.getFeatures_Name().size() > 0) {
											findInputTokenInEachNode = addingMeasurementsToTree(
													protocol.getFeatures_Name(),
													childTree, db, true, mode);
											findInputTokenInEachNode = true;
										}
									}
								}

							}
						}

					}else if (findInputTokenInEachNode == false) {

						if (mode == SEARCHINGMEASUREMENT || mode == SEARCHINGDETAIL) {// filter in measurements

							childTree.remove();

						} else if (mode == SEARCHINGPROTOCOL || mode == SEARCHINGALL) { // get all
							// measurements and
							// protocols in
							// descendant class.
							// Because the input token was found in current
							// protocol!

							// Remove all protocols that don`t match the input
							// token
							if (!foundTokenInParentProtocol
									&& !protocolName.toLowerCase().matches(
											".*" + InputToken.toLowerCase()
											+ ".*")) {

								childTree.remove();

							} else {

								if(!protocolName.equals(parentClassName)){

									boolean subProtocolRepeatProtocol = false;
									// If the input token is found in the current
									// protocol, re-add all its descendants to the
									// tree. Because
									// its sub-nodes might not contain the input
									// token therefore they might have been removed
									// from the tree already.
									// Therefore need to be re-added
									if (protocol.getSubprotocols_Name().size() > 0) {

										List<String> subProtocolNames = protocol.getSubprotocols_Name();

										if(subProtocolNames.contains(protocolName)){
											subProtocolRepeatProtocol = true;
										}
										if(subProtocolNames.contains(parentClassName)){
											subProtocolNames.remove(parentClassName);
										}

										findInputTokenInEachNode = recursiveAddingNodesToTree(
												protocol.getSubprotocols_Name(),
												protocol.getName(), childTree, db,
												true, mode);
										findInputTokenInEachNode = true;
									}
									// This is the case where none of the
									// measurements of this protocol match the input
									// token, but the current protocol
									// matches input token. Therefore its
									// measurements need to be re-added to the tree
									if (subProtocolRepeatProtocol == false && protocol.getFeatures_Name() != null
											&& protocol.getFeatures_Name().size() > 0) {
										findInputTokenInEachNode = addingMeasurementsToTree(
												protocol.getFeatures_Name(),
												childTree, db, true, mode);
										findInputTokenInEachNode = true;
									}
								}else{
									// This is the case where none of the
									// measurements of this protocol match the input
									// token, but the current protocol
									// matches input token. Therefore its
									// measurements need to be re-added to the tree
									if (protocol.getFeatures_Name() != null
											&& protocol.getFeatures_Name().size() > 0) {
										findInputTokenInEachNode = addingMeasurementsToTree(
												protocol.getFeatures_Name(),
												childTree, db, true, mode);
										findInputTokenInEachNode = true;
									}
								}
							}
						}
					}
					// if any branch of node contains input token, we indicate
					// to keep the parent node in the tree.
					// For example protocolA has protocolB and protocolC,
					// protoclB contains input token whereas protocolC dose not.
					// We`ll delete C and tells
					// its parent node A that the token has been found
					if (findInputTokenInEachNode == true) {
						findInputTokenInNextAllNodes = true;
					}
				}
			}
		}

		// If the input token is null, that means it`s not in the searching mode
		// but in normal tree view.
		if (InputToken == null) {

			findInputTokenInNextAllNodes = true;
		}

		return findInputTokenInNextAllNodes;
	}

	/**
	 * this is adding the measurements as references in
	 * recursiveAddingNodesToTree().
	 * 
	 * @param childNode
	 * @param parentTree
	 * @param db
	 * @throws DatabaseException 
	 */
	public boolean addingMeasurementsToTree(List<String> childNode,
			JQueryTreeViewElement parentTree, Database db,
			boolean foundInParent, Integer mode) {

		// Create a variable to store the boolean value with which we could know
		// whether we need to skip these measurements of the protocol.
		// if none of the measurements contain input token, it`s false meaning
		// these measurements will not be shown in the tree.
		boolean findTokenInMeasurements = false;

		// indicate with the input token has been found in detail information in
		// the measurement.
		boolean findTokenInDetailInformation = false;

		// This variables store the measurements that conform to the
		// requirements by the mode that has been selected.
		// For example, it only contains the measurement where the input token
		// has been found under mode searchingMeasurement
		List<String> filteredNode = new ArrayList<String>();

		try {

			// If the input token is available, we need to check which mode it is
			// and decide what we do with it here
			if (InputToken != null) {

				// In mode of searching for measurements, we check if the name of
				// measurements contain the input token
				// If the token is not in the name, the measurement is removed from
				// list.
				if (mode == SEARCHINGMEASUREMENT) {

					for(Measurement m : db.find(Measurement.class, new QueryRule(Measurement.NAME, Operator.IN, childNode))){

						if (m.getName().toLowerCase().matches(".*" + InputToken.toLowerCase() + ".*")) {
							filteredNode.add(m.getName());
							findTokenInMeasurements = true;
						} else if (m.getLabel() != null && m.getLabel().toLowerCase().matches(".*" + InputToken.toLowerCase() + ".*")) {
							filteredNode.add(m.getName());
							findTokenInMeasurements = true;
						}
						
					}

				} else {
					// In mode of searching for all fields, details, we need to loop
					// through all the measurements, therefore
					// we do not care whether the measurement name contains the
					// input token or not.
					filteredNode = childNode;
				}

			} else {
				// Normal mode when the input token is not available
				filteredNode = childNode;
			}

			List<Measurement> measurementList = new ArrayList<Measurement>();

			if (filteredNode.size() > 0)
				measurementList = db.find(Measurement.class, new QueryRule(
						Measurement.NAME, Operator.IN, filteredNode));

			for (Measurement measurement : measurementList) {

				// reset the the variable to false
				findTokenInDetailInformation = false;

				JQueryTreeViewElement childTree = null;

				// Query the display name! For some measurements, the labels
				// were stored in the observedValue with feature_name
				// "display name". If the display name is not available, we`ll
				// use the measurement name as label
				String displayName = "";

				if (measurement.getLabel() != null && !measurement.getLabel().equals("")) {

					displayName = measurement.getLabel();
				} else {
					displayName = measurement.getName();
				}

				// Query the all the detail information about this measurement,
				// in molgenis terminology, the detail information
				// are all the observedValue and some of the fields from the
				// measurement
				String htmlValue = null;

				htmlValue = htmlTableForTreeInformation(db, measurement);

				// Check if the tree has already had the treeElement with the
				// same name cos the name can not be duplicated in
				// jquery tree here. Therefore if the element already existed, a
				// suffix will be added at the end of string to
				// make the name unique

				//				displayName = displayName.replaceAll("[%#]", "");

				if (protocolsAndMeasurementsinTree.containsKey(displayName)) {

					if (!multipleInheritance.containsKey(displayName)) {
						multipleInheritance.put(displayName, 1);
					} else {
						int number = multipleInheritance.get(displayName);
						multipleInheritance.put(displayName, ++number);
					}

					JQueryTreeViewElement previousChildTree = protocolsAndMeasurementsinTree
							.get(displayName);

					childTree = new JQueryTreeViewElement(displayName
							+ multipleInheritance.get(displayName),
							displayName, parentTree,
							previousChildTree.getHtmlValue());

					listOfMeasurements.add(displayName
							+ multipleInheritance.get(displayName));

					childTree.setHtmlValue(htmlValue);

				} else {

					childTree = new JQueryTreeViewElement(displayName,
							parentTree, htmlValue);

					listOfMeasurements.add(displayName);

					protocolsAndMeasurementsinTree.put(displayName, childTree);
				}

				// Searching for the details. Since htmlValue has all the
				// information about this measurement,
				// therefore we search for the input tokenin this variable
				if (mode == SEARCHINGDETAIL) {

					if (htmlValue.toLowerCase().matches(
							".*" + InputToken.toLowerCase() + ".*")) {
						findTokenInDetailInformation = true;
					} else {
						findTokenInDetailInformation = false;
						childTree.remove();
					}

					if (findTokenInDetailInformation == true) {
						findTokenInMeasurements = true;
					}
				}

				// Searching for the details and measurement name. If either
				// name of measurement or detail information
				// of measurement contains the input token, this is a matching!
				if (mode == SEARCHINGALL) {

					if (htmlValue.toLowerCase().matches(
							".*" + InputToken.toLowerCase() + ".*")) {
						findTokenInDetailInformation = true;
					} else {

						if (measurement
								.getName()
								.toLowerCase()
								.matches(".*" + InputToken.toLowerCase() + ".*")) {
							findTokenInDetailInformation = true;
						} else {

							if (foundInParent != true) {
								findTokenInDetailInformation = false;
								childTree.remove();
							} else {
								findTokenInDetailInformation = true;
							}
						}
					}

					if (findTokenInDetailInformation == true) {
						findTokenInMeasurements = true;
					}
				}
			}

		} catch (DatabaseException e) {
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
	public String htmlTableForTreeInformation(Database db,
			Measurement measurement) throws DatabaseException {

		List<String> categoryNames = measurement.getCategories_Name();

		String measurementDescription = measurement.getDescription();

		String measurementDataType = measurement.getDataType();

		// String htmlValue = "<table id = 'detailInformation'  border = 2>" +
		String htmlValue = "<table style='border-spacing: 2px; width: 100%;' class='MeasurementDetails' id = 'measurementDetail"
				+ measurement.getId() + "'  >";
		htmlValue += "<tr><td class='box-body-label'>Item name:</th><td>"
				+ measurement.getName() + "</td></tr>";

		if (categoryNames.size() > 0) {
			htmlValue += "<tr><td  class='box-body-label'>Category:</td><td><table>";

			for (String string : categoryNames) {
				htmlValue += "<tr><td>";
				htmlValue += string;
				htmlValue += "</td></tr>";

			}
			htmlValue += "</table></td></tr>";
		}

		htmlValue += "<tr><td class='box-body-label'>Description:</td><td>"
				+ (measurementDescription == null ? "not provided" : measurementDescription) + "</td></tr>";

		htmlValue += "<tr><td class='box-body-label'>Data type:</th><td>"
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
					htmlValue += "<tr><td class='box-body-label'>" + featureName + "</td><td><a href="
							+ value + ">" + value + "</a></td></tr>";
				} else {

					if (featureName.startsWith("display name")) {
						featureName = "display name";
					}

					//					htmlValue += "<tr><td class='box-body-label'>" + featureName + "</td><td> "
					//							+ value + "</td></tr>";
				}
			}
		}

		htmlValue += "</table>";

		return htmlValue;
	}

	public String getTreeView() {

		List<String> selected = new ArrayList<String>();

		// don't select, is confusing...
		// for (Measurement m : shoppingCart) {
		// selected.add(m.getName());
		// }

		String htmlTreeView = treeView.toHtml(selected);

		// This piece of javascript need to be here because some java calls are needed.  
		String measurementClickEvent = "<script>";

		List<String> uniqueMeasurementName = new ArrayList<String>();

		System.out.println("listOfMeasurements>>>"+listOfMeasurements);

		for(String eachMeasurement : listOfMeasurements){

			if(!uniqueMeasurementName.contains(eachMeasurement)){

				uniqueMeasurementName.add(eachMeasurement);

				if(eachMeasurement.equals("Year partner son daughter 3")){
					System.out.println();
				}
				measurementClickEvent += "$('#" + eachMeasurement.replaceAll(" ", "_") + "').click(function() {"
						+ "getHashMapContent(\"" + eachMeasurement + "\");});"
						+ "";
			}
		}
		measurementClickEvent += "</script>";

		htmlTreeView += measurementClickEvent;

		return htmlTreeView;
	}

	/**
	 * 
	 * @param db
	 * @param request
	 * @param selectedInvestigation
	 * @param dateOfDownload
	 * @param selectionName 
	 * @param x 
	 * @throws Exception 
	 */
	private void addMeasurementsForDownload(Database db, Tuple request, String selectedInvestigation, String dateOfDownload, String selectionName) throws Exception {

		// fill shopping cart using selected selectboxes (measurements)
		// the ID's and names of the selectboxes are the same as the measurement
		// names,
		// so we can easily get them from the request

		this.shoppingCart.clear();

		List<Measurement> allMeasList = db.find(Measurement.class);
		for (Measurement m : allMeasList) {
			if (request.getBool(m.getName()) != null) {
				this.shoppingCart.add(m);
			}
		}

		List<Integer> DownloadedMeasurementIds = new ArrayList<Integer>();

		if (this.shoppingCart.isEmpty()) {
			this.getModel().getMessages().add(new ScreenMessage("Your download list is empty. Please select item and proceed to download",true));
			this.setStatus("<h4> Your download list is empty. Please select item and proceed to download" + "</h4>" ) ;
			this.setError("Your download list is empty. Please select item and proceed to download");

		} else {

			// System.out.println("DownloadedMeasurementIds >>>: " + this.shoppingCart);
			for (Measurement m : this.shoppingCart) {
				DownloadedMeasurementIds.add(m.getId());

				//x.writeRow(m);// System.out.println("DownloadedMeasurementIds >>>: " + m.getId());
			}

			// REWRITE SO USERS CAN HAVE MULTIPLE SHOPPINGCARTS-- there are no shopping carts any more . 

			// Query<ShoppingCart> q = db.query(ShoppingCart.class);	// q.addRules(new QueryRule(ShoppingCart.USERID, Operator.EQUALS, this.getLogin().getUserName()));	// q.addRules(new QueryRule(ShoppingCart.CHECKEDOUT, Operator.EQUALS, false));
			List<ShoppingCart> result = new ArrayList<ShoppingCart>();  // q.find();
			System.out.println("save selection step 0");

			if (result.isEmpty()) {
				String shoppingCartName ;

				// Add to database
				ShoppingCart shoppingCart = new ShoppingCart();
				if (selectionName.compareTo("empty") == 0) {
					shoppingCartName = this.getLogin().getUserName() + "_" + System.currentTimeMillis();
				}else {
					shoppingCartName = selectionName;
				}
				shoppingCart.setName(shoppingCartName );

				System.out.println("save selection step1");
				// shoppingCart.setMeasurements(DownloadedMeasurementIds);
				shoppingCart.setMeasurements_Id(DownloadedMeasurementIds);
				shoppingCart.setUserID(this.getLogin().getUserName());
				// shoppingCart.setCheckedOut(false);
				// shoppingCart.setDateOfOrder(dateOfDownload);
				System.out.println("save selection step2");

				shoppingCart.setApproved(false);

				//check for duplicates
				Query<ShoppingCart> q = db.query(ShoppingCart.class);
				q.addRules(new QueryRule(ShoppingCart.NAME, Operator.EQUALS, shoppingCartName));

				if (q.find().size() > 0) {
					//if user selection already exists use an automated name 
					String shoppingCartName2 = this.getLogin().getUserName() + "_" + System.currentTimeMillis();

					this.setError("A user selection with name : "+ shoppingCartName +" already exists. An automatic generated name will be used: "+ shoppingCartName2);
					//this.getModel().getMessages().add(new ScreenMessage("A user selection with name : "+ shoppingCartName +" already exists. Please insert another name for your selection and try again.", true));
					this.setStatus("<h4> A user selection with name : "+ shoppingCartName +" already exists. An automatic generated name will be used: "+ shoppingCartName2+ "</h4>" ) ;
					shoppingCartName = shoppingCartName2;

				} else {
					try {
						db.add(shoppingCart);
						// System.out.println("Download list has been added to the DB");

						this.getModel().getMessages().add(new ScreenMessage("Selection saved to 'My Selections' under name "+ shoppingCartName , true));
						this.setStatus("<h4> Selection saved to 'My Selections' under name "+ shoppingCartName + "</h4>" ) ;
						this.setSuccess("Selection saved to 'My Selections' under name "+ shoppingCartName);

					}  catch (DatabaseException e) {
						e.printStackTrace();
					}
				}

			} else {
				ShoppingCart shoppingCart = result.get(0); // assuming user can  have only one shopping cart that's NOT checked out
				// shoppingCart.setMeasurements(DownloadedMeasurementIds);
				shoppingCart.setMeasurements_Id(DownloadedMeasurementIds);
				db.update(shoppingCart);

				this.getModel().getMessages().add(new ScreenMessage("Selection saved to 'My Selections' under name " + shoppingCart.getName() + "You can browse them from menu \"My selections\"" , true));
				this.setStatus("<h4> Selection saved to 'My Selections' under name " + shoppingCart.getName() + "You can browse them from menu \"My selections\""+ "</h4>" ) ;
				this.setSuccess("Selection saved to 'My Selections' under name " + shoppingCart.getName() + "You can browse them from menu \"My selections\"");
				// System.out.println("Shopping cart has been updated in the DB");
			}

			HttpServletRequestTuple rt = (HttpServletRequestTuple) request;
			HttpServletRequest httpRequest = rt.getRequest();
			HttpServletResponse httpResponse = rt.getResponse();
			// System.out.println(">>> " + this.getParent().getName()+
			// "or >>>  "+ this.getSelected().getLabel());
			// String redirectURL = httpRequest.getRequestURL() + "?__target=" +
			// this.getParent().getName() + "&select=MeasurementsDownloadForm";
			String redirectURL = httpRequest.getRequestURL() + "?__target="
					+ "Downloads" + "&select=MeasurementsDownloadForm";

			httpResponse.sendRedirect(redirectURL);

		}

	}

	private void deleteShoppingItem(String selected) {
		// search the item
		for (int i = 0; i < this.shoppingCart.size(); i++) {
			if (this.shoppingCart.get(i).getName().equals(selected)) {
				this.shoppingCart.remove(i);
				this.getModel().getMessages().add(new ScreenMessage("The item \"" + selected + "\" has been successfully removed from your shopping cart", true));
				this.setStatus("<h4> The item \"" + selected + "\" has been successfully removed from your shopping cart"+ "</h4>" ) ;

			}
		}
	}

	public List<Measurement> getShoppingCart() {
		return shoppingCart;
	}

	public void setArrayInvestigations(List<Investigation> arrayInvestigations) {
		this.arrayInvestigations = arrayInvestigations;
	}

	public List<Investigation> getArrayInvestigations() {
		return arrayInvestigations;
	}

	public void setSelectedInv(boolean isSelectedInv) {
		this.isSelectedInv = isSelectedInv;
	}

	public boolean isSelectedInv() {
		return isSelectedInv;
	}

	public String getSelectedInvestigation() {
		return selectedInvestigation;
	}

	public void setSelectedInvestigation(String selectedInvestigation) {
		this.selectedInvestigation = selectedInvestigation;
	}

	public void setArraySearchFields(List<String> arraySearchFields) {
		this.arraySearchFields = arraySearchFields;
	}

	public List<String> getArraySearchFields() {
		return arraySearchFields;
	}

	public void setInputToken(String inputToken) {
		InputToken = inputToken;
	}

	public String getInputToken() {
		return InputToken;
	}

	public void setSelectedField(String selectedField) {
		this.selectedField = selectedField;
	}

	public String getSelectedField() {
		return selectedField;
	}

	private String getComparison() {
		return comparison;
	}

	public List<String> getFilters() {
		//		if (!SearchFilters.isEmpty()) {
		//			return this.SearchFilters;
		//		}
		//return "filters";
		return SearchFilters;
	}

	//	@Override
	//	public boolean isVisible()
	//	{
	//		// always visible
	//		return true;
	//	}
	//	
	//	@Override
	//	public boolean isVisible()
	//	{
	//		//you can use this to hide this plugin, e.g. based on user rights.
	//		//e.g.
	//		//if(!this.getLogin().hasEditPermission(myEntity)) return false;
	//		if (!this.getLogin().isAuthenticated()) {
	//			return false;
	//		}
	//		return true;
	//	}

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

	//	/**
	//	 * This function is used by the user interface template to show rules on the
	//	 * screen.
	//	 * 
	//	 * @return a list of query rules that can be managed by the user.
	//	 * @throws DatabaseException
	//	 */
	//	public Vector<String> getFilters() throws DatabaseException
	//	{
	//		Vector<String> filters = new Vector<String>();
	//		//Map<String, String> nameLabelMap = new TreeMap<String, String>();
	//
	//		if (mode == SEARCHINGDETAIL) filters.add("SearchingDetail");
	//		else if (mode != SEARCHINGPROTOCOL) filters.add("SearchingProtocol"); 
	//		else if (mode == SEARCHINGMEASUREMENT) filters.add("SearchingMeasurement");
	//		else if (mode == SEARCHINGDETAIL) filters.add("SearchingDetail"); 
	//		else if (mode == SEARCHINGALL) filters.add("SearchingAll"); 
	//
	//			//filters.add(label + " " + rule.getOperator().toString() + " "+ rule.getValue());
	//
	//		
	//
	//		return filters;
	//	}


}

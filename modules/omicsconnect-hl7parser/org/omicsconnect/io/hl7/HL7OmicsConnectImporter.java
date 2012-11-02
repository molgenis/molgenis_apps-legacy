package org.omicsconnect.io.hl7;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.observ.Category;
import org.molgenis.observ.DataSet;
import org.molgenis.observ.ObservableFeature;
import org.molgenis.observ.Protocol;
import org.omicsconnect.io.hl7.lra.HL7ObservationLRA;
import org.omicsconnect.io.hl7.lra.HL7OrganizerLRA;
import org.omicsconnect.io.hl7.lra.HL7ValueSetLRA;

public class HL7OmicsConnectImporter
{

	private DataSet dataSet;
	private Protocol protocol;
	private Protocol subProtocol;
	private ObservableFeature feature;

	/** make a dataset named 'datasetName', if it is not already existing * */
	private DataSet findDataSet(Database db, String datasetName) throws DatabaseException
	{
		DataSet dataset = new DataSet();
		if (db.find(DataSet.class, new QueryRule(DataSet.NAME, Operator.EQUALS, datasetName)).size() == 0)
		{
			dataset.setName(datasetName);
			dataset.setIdentifier(datasetName);
			db.add(dataset);
		}
		else
		{
			dataset = db.find(DataSet.class, new QueryRule(DataSet.NAME, Operator.EQUALS, datasetName)).get(0);
		}

		return dataset;
	}

	/** make a Protocol named 'protName', if it is not already existing * */
	private Protocol makeProtocol(Database db, DataSet dataSet, String protName) throws DatabaseException
	{
		Protocol protocol;
		if (db.find(Protocol.class, new QueryRule(Protocol.NAME, Operator.EQUALS, protName)).size() == 0)
		{
			protocol = new Protocol();
			protocol.setIdentifier(protName);
			protocol.setName(protName);
			dataSet.setProtocolUsed_Identifier(protName);
			this.protocol = protocol;
		}
		else
		{
			protocol = db.find(Protocol.class, new QueryRule(Protocol.NAME, Operator.EQUALS, protName)).get(0);
			this.protocol = protocol;
		}
		return this.protocol;
	}

	private ObservableFeature checkFeatures(Database db, DataSet dataSet, HL7ObservationLRA meas)
			throws DatabaseException
	{
		ObservableFeature obsFeature;
		if (db.find(ObservableFeature.class,
				new QueryRule(ObservableFeature.NAME, Operator.EQUALS, meas.getMeasurementName())).size() == 1)
		{
			obsFeature = db.find(ObservableFeature.class,
					new QueryRule(ObservableFeature.NAME, Operator.EQUALS, meas.getMeasurementName())).get(0);
		}
		else
		{
			obsFeature = new ObservableFeature();
			// set the description/label
			obsFeature.setDescription(meas.getMeasurementLabel());
			obsFeature.setName(meas.getMeasurementName());
			obsFeature.setIdentifier(meas.getMeasurementName());
			// set the datatype
			String dataType = meas.getMeasurementDataType();
			obsFeature.setDataType(setFeatureDataType(dataType));
			System.out.println(meas.getMeasurementName());
			db.add(obsFeature);

		}
		this.feature = obsFeature;
		return this.feature;
	}

	private Protocol makeSubProtocols(Database db, DataSet dataset, String protName) throws DatabaseException
	{

		if (db.find(Protocol.class, new QueryRule(Protocol.NAME, Operator.EQUALS, protName)).size() == 0)
		{
			subProtocol = new Protocol();
			subProtocol.setIdentifier(protName);
			subProtocol.setName(protName);
			protocol.setSubprotocols(subProtocol);

		}
		else
		{
			subProtocol = db.find(Protocol.class, new QueryRule(Protocol.NAME, Operator.EQUALS, protName)).get(0);
		}
		return subProtocol;

	}

	/** Select which datatype to use */
	private String setFeatureDataType(String dataType)
	{

		String datatype = "";
		if (dataType.equals("INT"))
		{
			datatype = "int";
		}
		else if (dataType.equals("ST"))
		{
			datatype = "string";
		}
		else if (dataType.equals("CO"))
		{
			datatype = "categorical";
		}
		else if (dataType.equals("CD"))
		{
			datatype = "code";
		}
		else if (dataType.equals("PQ"))
		{
			datatype = "decimal";
		}
		else if (dataType.equals("TS"))
		{
			datatype = "datetime";
		}
		else if (dataType.equals("REAL"))
		{
			datatype = "decimal";
		}
		else if (dataType.equals("BL"))
		{
			datatype = "bool";
		}
		return datatype;

	}

	public void start(HL7Data ll, Database db) throws Exception
	{
		try
		{

			db.beginTx();

			String datasetName = "LifeLines";
			// TODO: Wizard for importing HL7 data, select/create dataset name
			dataSet = findDataSet(db, datasetName);

			/**
			 * make a protocol named (name is in last column), if it is not
			 * already existing *
			 */

			// MAKE PROTOCOL
			protocol = makeProtocol(db, dataSet, "stageCatalogue");

			HashMap<String, HL7ValueSetLRA> hashValueSetLRA = ll.getHashValueSetLRA();

			List<Integer> listOfProtocolIds = new ArrayList<Integer>();

			List<String> uniqueListOfObservableFeatureNames = new ArrayList<String>();
			List<ObservableFeature> uniqueListOfObservableFeatures = new ArrayList<ObservableFeature>();
			List<Category> uniqueListOfCategory = new ArrayList<Category>();
			List<String> uniqueCategoryName = new ArrayList<String>();
			List<Protocol> uniqueProtocol = new ArrayList<Protocol>();
			List<String> uniqueListOfProtocolName = new ArrayList<String>();

			/**
			 * Every HL7Organizer object is 1 subprotocol of stageCatalogue
			 * protocol
			 * 
			 * Make a subprotocol with a list of all the measurements of that
			 * protocol and all the categories that belong to the measurement
			 */
			for (HL7OrganizerLRA organizer : ll.getHL7OrganizerLRA())
			{

				System.out.println(organizer.getHL7OrganizerNameLRA());
				String protocolName = organizer.getHL7OrganizerNameLRA().trim();

				// MAKE SUBPROTOCOL
				subProtocol = makeSubProtocols(db, dataSet, protocolName);

				List<String> listProtocolFeatures = new ArrayList<String>();

				/** Every HL7Observation is an ObservableFeature */
				// Here are the features being added to the protocols
				for (HL7ObservationLRA meas : organizer.measurements)
				{
					List<String> measurementCategory = new ArrayList<String>();

					feature = checkFeatures(db, dataSet, meas);
					// add feature to the protocol list
					listProtocolFeatures.add(feature.getName());
					/*
					 * if (hashValueSetLRA.containsKey(protocolName + "." +
					 * meas.getMeasurementName().trim())) {
					 * 
					 * 
					 * HL7ValueSetLRA valueSetLRA = hashValueSetLRA
					 * .get(protocolName + "." + meas.getMeasurementName());
					 * 
					 * 
					 * for (HL7ValueSetAnswerLRA eachAnswer :
					 * valueSetLRA.getListOFAnswers()) {
					 * 
					 * String codeValue = eachAnswer.getCodeValue(); String
					 * categoryName = eachAnswer.getName().trim().toLowerCase();
					 * 
					 * if (!uniqueCategoryName.contains(categoryName)) {
					 * uniqueCategoryName.add(categoryName); Category c = new
					 * Category(); String indentifier = codeValue +
					 * categoryName.replaceAll("[^(a-zA-Z0-9)]", "");
					 * c.setValueLabel(indentifier.trim().toLowerCase());
					 * c.setValueCode(codeValue);
					 * c.setValueDescription(categoryName);
					 * c.setObservableFeature(feature); //
					 * c.setInvestigation(inv); // TODO delete?
					 * uniqueListOfCategory.add(c); } String indentifier =
					 * codeValue + categoryName.replaceAll("[^(a-zA-Z0-9)]",
					 * "");
					 * measurementCategory.add(indentifier.trim().toLowerCase
					 * ()); }
					 * 
					 * 
					 * }
					 * 
					 * // WHY THIS CHECK? if
					 * (!uniqueListOfObservableFeatureNames
					 * .contains(feature.getName())) {
					 * uniqueListOfObservableFeatureNames
					 * .add(feature.getName());
					 * uniqueListOfObservableFeatures.add(feature); }
					 */
				}// END of HL7ObservationLRA (features)

				subProtocol.setFeatures_Identifier(listProtocolFeatures);

				if (subProtocol.getFeatures_Identifier().size() > 0)
				{
					uniqueProtocol.add(subProtocol);
					uniqueListOfProtocolName.add(protocolName);
				}

			}// END of HL7OrganizerLRA (subprotocols)
			db.add(uniqueProtocol);
			db.add(protocol);
			// db.update(uniqueListOfCategory,
			// Database.DatabaseAction.ADD_IGNORE_EXISTING,
			// Category.VALUELABEL);
			/*
			 * for (ObservableFeature m : uniqueListOfObservableFeatures) {
			 * 
			 * if (m.getCategories_Name().size() > 0) {
			 * 
			 * List<Category> listOfCategory = db.find(Category.class, new
			 * QueryRule(Category.NAME, Operator.IN, m.getCategories_Name()));
			 * 
			 * List<Integer> listOfCategoryID = new ArrayList<Integer>();
			 * 
			 * for (Category c : listOfCategory) {
			 * listOfCategoryID.add(c.getId()); }
			 * m.setCategories_Id(listOfCategoryID); } }
			 * db.update(uniqueListOfObservableFeatures);
			 * 
			 * for (Protocol p : uniqueProtocol) {
			 * 
			 * if (p.getFeatures_Name().size() > 0) {
			 * 
			 * List<ObservableFeature> listOfObservableFeature =
			 * db.find(ObservableFeature.class, new QueryRule(
			 * ObservableFeature.NAME, Operator.IN, p.getFeatures_Name()));
			 * 
			 * List<Integer> listOfObservableFeatureID = new
			 * ArrayList<Integer>();
			 * 
			 * for (ObservableFeature m : listOfObservableFeature) {
			 * listOfObservableFeatureID.add(m.getId()); }
			 * p.setFeatures_Id(listOfObservableFeatureID); } }
			 * 
			 * db.update(uniqueProtocol,
			 * Database.DatabaseAction.ADD_IGNORE_EXISTING, Protocol.NAME);
			 * 
			 * uniqueProtocol = db.find(Protocol.class, new
			 * QueryRule(Protocol.NAME, Operator.IN, uniqueListOfProtocolName));
			 * 
			 * for (Protocol p : uniqueProtocol) {
			 * listOfProtocolIds.add(p.getId()); }
			 * 
			 * Protocol otherProtocol = new Protocol();
			 * otherProtocol.setName("NotClassified");
			 * otherProtocol.setInvestigation_Name(investigationName);
			 * List<Integer> listOfFeaturesID = new ArrayList<Integer>(); for
			 * (ObservableFeature m : db.find(ObservableFeature.class, new
			 * QueryRule( ObservableFeature.INVESTIGATION_NAME, Operator.EQUALS,
			 * investigationName))) {
			 * 
			 * if (!uniqueListOfObservableFeatureNames.contains(m.getName())) {
			 * listOfFeaturesID.add(m.getId()); }
			 * 
			 * } if (listOfFeaturesID.size() > 0) {
			 * otherProtocol.setFeatures_Id(listOfFeaturesID);
			 * 
			 * db.add(otherProtocol);
			 * listOfProtocolIds.add(otherProtocol.getId()); }
			 * 
			 * stageCatalogue.setSubprotocols_Id(listOfProtocolIds);
			 * 
			 * db.update(stageCatalogue);
			 * 
			 * db.commitTx();
			 */
		}
		catch (Exception e)
		{
			e.printStackTrace();
			db.rollbackTx();
		}
	}
	/*
	 * public List<Integer> addingOntologyTerm(List<HL7OntologyTerm>
	 * listOfHL7OntologyTerms, Database db) throws Exception {
	 * 
	 * List<Integer> listOfOntologyTermIDs = new ArrayList<Integer>();
	 * 
	 * for (HL7OntologyTerm t : listOfHL7OntologyTerms) {
	 * 
	 * String codeSystemName = t.getCodeSystemName();
	 * 
	 * if (t.getCodeSystemName().toLowerCase().startsWith("snomed") ||
	 * t.getCodeSystemName().equalsIgnoreCase("sct")) { codeSystemName = "SCT";
	 * }
	 * 
	 * Ontology ot = new Ontology();
	 * 
	 * if (db.find(Ontology.class, new QueryRule(Ontology.ONTOLOGYACCESSION,
	 * Operator.EQUALS, t.getCodeSystem())) .size() == 0) {
	 * 
	 * ot.setName(codeSystemName); ot.setOntologyAccession(t.getCodeSystem());
	 * db.add(ot);
	 * 
	 * } else { ot = db.find(Ontology.class, new
	 * QueryRule(Ontology.ONTOLOGYACCESSION, Operator.EQUALS,
	 * t.getCodeSystem())).get(0); }
	 * 
	 * Query<OntologyTerm> q = db.query(OntologyTerm.class); q.addRules(new
	 * QueryRule(OntologyTerm.TERMACCESSION, Operator.EQUALS, t.getCode()));
	 * q.addRules(new QueryRule(OntologyTerm.NAME, Operator.EQUALS,
	 * codeSystemName));
	 * 
	 * OntologyTerm ont = new OntologyTerm();
	 * 
	 * if (q.find().size() == 0) {
	 * 
	 * ont.setOntology_Id(ot.getId()); ont.setName(t.getDisplayName());
	 * ont.setTermAccession(t.getCode()); db.add(ont);
	 * 
	 * } else { ont = q.find().get(0); }
	 * 
	 * listOfOntologyTermIDs.add(ont.getId());
	 * 
	 * System.out.println("The mapped ontology term is " + t.getDisplayName() +
	 * "\t" + t.getCode()); }
	 * 
	 * return listOfOntologyTermIDs; }
	 */
}

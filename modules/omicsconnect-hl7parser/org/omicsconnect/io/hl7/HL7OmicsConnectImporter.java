package org.omicsconnect.io.hl7;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.gwascentral.Investigation;
import org.molgenis.observ.Category;
import org.molgenis.observ.ObservableFeature;
import org.molgenis.observ.Protocol;
import org.molgenis.observ.target.Ontology;
import org.omicsconnect.io.hl7.lra.HL7ObservationLRA;
import org.omicsconnect.io.hl7.lra.HL7OrganizerLRA;
import org.omicsconnect.io.hl7.lra.HL7ValueSetAnswerLRA;
import org.omicsconnect.io.hl7.lra.HL7ValueSetLRA;

import uk.ac.ebi.ontocat.OntologyTerm;

public class HL7OmicsConnectImporter
{

	public void start(HL7Data ll, Database db) throws Exception
	{
		try
		{

			db.beginTx();

			String investigationName = "LifeLines";

			Investigation inv = null;

			if (db.find(Investigation.class, new QueryRule(Investigation.NAME, Operator.EQUALS, investigationName))
					.size() == 0)
			{

				inv = new Investigation();

				inv.setName(investigationName);

				db.add(inv);

			}
			else
			{
				inv = db.find(Investigation.class,
						new QueryRule(Investigation.NAME, Operator.EQUALS, investigationName)).get(0);
			}

			Protocol stageCatalogue;

			if (db.find(Protocol.class, new QueryRule(Protocol.NAME, Operator.EQUALS, "stageCatalogue")).size() == 0)
			{
				stageCatalogue = new Protocol();
				stageCatalogue.setName("stageCatalogue");
				// stageCatalogue.setInvestigation_Name(investigationName);
				// TODO delete?

				db.add(stageCatalogue);
			}
			else
			{
				stageCatalogue = db.find(Protocol.class,
						new QueryRule(Protocol.NAME, Operator.EQUALS, "stageCatalogue")).get(0);
			}

			HashMap<String, HL7ValueSetLRA> hashValueSetLRA = ll.getHashValueSetLRA();

			List<Integer> listOfProtocolIds = new ArrayList<Integer>();

			List<String> uniqueListOfObservableFeatureNames = new ArrayList<String>();
			List<ObservableFeature> uniqueListOfObservableFeatures = new ArrayList<ObservableFeature>();
			List<Category> uniqueListOfCategory = new ArrayList<Category>();
			List<String> uniqueCategoryName = new ArrayList<String>();
			List<Protocol> uniqueProtocol = new ArrayList<Protocol>();
			List<String> uniqueListOfProtocolName = new ArrayList<String>();

			for (HL7OrganizerLRA organizer : ll.getHL7OrganizerLRA())
			{

				System.out.println(organizer.getHL7OrganizerNameLRA());
				String protocolName = organizer.getHL7OrganizerNameLRA().trim();
				Protocol protocol = new Protocol();
				protocol.setName(protocolName);
				// protocol.setInvestigation(inv); // TODO delete?

				List<String> protocolFeature = new ArrayList<String>();

				for (HL7ObservationLRA meas : organizer.measurements)
				{

					List<String> measurementCategory = new ArrayList<String>();

					if (db.find(ObservableFeature.class,
							new QueryRule(ObservableFeature.NAME, Operator.EQUALS, meas.getMeasurementName())).size() > 0)
					{
						ObservableFeature m = db.find(ObservableFeature.class,
								new QueryRule(ObservableFeature.NAME, Operator.EQUALS, meas.getMeasurementName())).get(
								0);

						m.setDescription(meas.getMeasurementLabel());
						// m.setInvestigation(inv); // TODO delete?

						protocolFeature.add(m.getName());

						if (hashValueSetLRA.containsKey(protocolName + "." + meas.getMeasurementName().trim()))
						{

							HL7ValueSetLRA valueSetLRA = hashValueSetLRA.get(protocolName + "."
									+ meas.getMeasurementName());

							for (HL7ValueSetAnswerLRA eachAnswer : valueSetLRA.getListOFAnswers())
							{

								String codeValue = eachAnswer.getCodeValue();
								String categoryName = eachAnswer.getName().trim().toLowerCase();

								if (!uniqueCategoryName.contains(categoryName))
								{
									uniqueCategoryName.add(categoryName);
									Category c = new Category();
									String indentifier = codeValue + categoryName.replaceAll("[^(a-zA-Z0-9)]", "");
									c.setValueLabel(indentifier.trim().toLowerCase());
									c.setValueCode(codeValue);
									c.setValueDescription(categoryName);
									// c.setInvestigation(inv); // TODO delete?
									uniqueListOfCategory.add(c);
								}
								String indentifier = codeValue + categoryName.replaceAll("[^(a-zA-Z0-9)]", "");
								measurementCategory.add(indentifier.trim().toLowerCase());
							}
						}
						// m.setCategories_Name(measurementCategory); //TODO
						// delete?

						String dataType = meas.getMeasurementDataType();

						if (dataType.equals("INT"))
						{
							m.setDataType("int");
						}
						else if (dataType.equals("ST"))
						{
							m.setDataType("string");
						}
						else if (dataType.equals("CO"))
						{
							m.setDataType("categorical");
						}
						else if (dataType.equals("CD"))
						{
							m.setDataType("code");
						}
						else if (dataType.equals("PQ"))
						{
							m.setDataType("decimal");
						}
						else if (dataType.equals("TS"))
						{
							m.setDataType("datetime");
						}
						else if (dataType.equals("REAL"))
						{
							m.setDataType("decimal");
						}
						else if (dataType.equals("BL"))
						{
							m.setDataType("bool");
						}

						if (!uniqueListOfObservableFeatureNames.contains(m.getName()))
						{
							uniqueListOfObservableFeatureNames.add(m.getName());
							uniqueListOfObservableFeatures.add(m);
						}
					}
					protocol.setFeatures_Name(protocolFeature);
					if (protocol.getFeatures_Name().size() > 0)
					{
						uniqueProtocol.add(protocol);
						uniqueListOfProtocolName.add(protocolName);
					}
				}
			}

			for (Category c : uniqueListOfCategory)
			{
				System.out.println("-------------." + c.getValueLabel());
			}

			db.update(uniqueListOfCategory, Database.DatabaseAction.ADD_IGNORE_EXISTING, Category.VALUELABEL);

			for (ObservableFeature m : uniqueListOfObservableFeatures)
			{

				if (m.getCategories_Name().size() > 0)
				{

					List<Category> listOfCategory = db.find(Category.class,
							new QueryRule(Category.NAME, Operator.IN, m.getCategories_Name()));

					List<Integer> listOfCategoryID = new ArrayList<Integer>();

					for (Category c : listOfCategory)
					{
						listOfCategoryID.add(c.getId());
					}
					m.setCategories_Id(listOfCategoryID);
				}
			}
			db.update(uniqueListOfObservableFeatures);

			for (Protocol p : uniqueProtocol)
			{

				if (p.getFeatures_Name().size() > 0)
				{

					List<ObservableFeature> listOfObservableFeature = db.find(ObservableFeature.class, new QueryRule(
							ObservableFeature.NAME, Operator.IN, p.getFeatures_Name()));

					List<Integer> listOfObservableFeatureID = new ArrayList<Integer>();

					for (ObservableFeature m : listOfObservableFeature)
					{
						listOfObservableFeatureID.add(m.getId());
					}
					p.setFeatures_Id(listOfObservableFeatureID);
				}
			}

			db.update(uniqueProtocol, Database.DatabaseAction.ADD_IGNORE_EXISTING, Protocol.NAME);

			uniqueProtocol = db.find(Protocol.class,
					new QueryRule(Protocol.NAME, Operator.IN, uniqueListOfProtocolName));

			for (Protocol p : uniqueProtocol)
			{
				listOfProtocolIds.add(p.getId());
			}

			Protocol otherProtocol = new Protocol();
			otherProtocol.setName("NotClassified");
			otherProtocol.setInvestigation_Name(investigationName);
			List<Integer> listOfFeaturesID = new ArrayList<Integer>();
			for (ObservableFeature m : db.find(ObservableFeature.class, new QueryRule(
					ObservableFeature.INVESTIGATION_NAME, Operator.EQUALS, investigationName)))
			{

				if (!uniqueListOfObservableFeatureNames.contains(m.getName()))
				{
					listOfFeaturesID.add(m.getId());
				}

			}
			if (listOfFeaturesID.size() > 0)
			{
				otherProtocol.setFeatures_Id(listOfFeaturesID);

				db.add(otherProtocol);
				listOfProtocolIds.add(otherProtocol.getId());
			}

			stageCatalogue.setSubprotocols_Id(listOfProtocolIds);

			db.update(stageCatalogue);

			db.commitTx();

		}
		catch (Exception e)
		{
			e.printStackTrace();
			db.rollbackTx();
		}
	}

	public List<Integer> addingOntologyTerm(List<HL7OntologyTerm> listOfHL7OntologyTerms, Database db) throws Exception
	{

		List<Integer> listOfOntologyTermIDs = new ArrayList<Integer>();

		for (HL7OntologyTerm t : listOfHL7OntologyTerms)
		{

			String codeSystemName = t.getCodeSystemName();

			if (t.getCodeSystemName().toLowerCase().startsWith("snomed")
					|| t.getCodeSystemName().equalsIgnoreCase("sct"))
			{
				codeSystemName = "SCT";
			}

			Ontology ot = new Ontology();

			if (db.find(Ontology.class, new QueryRule(Ontology.ONTOLOGYACCESSION, Operator.EQUALS, t.getCodeSystem()))
					.size() == 0)
			{

				ot.setName(codeSystemName);
				ot.setOntologyAccession(t.getCodeSystem());
				db.add(ot);

			}
			else
			{
				ot = db.find(Ontology.class,
						new QueryRule(Ontology.ONTOLOGYACCESSION, Operator.EQUALS, t.getCodeSystem())).get(0);
			}

			Query<OntologyTerm> q = db.query(OntologyTerm.class);
			q.addRules(new QueryRule(OntologyTerm.TERMACCESSION, Operator.EQUALS, t.getCode()));
			q.addRules(new QueryRule(OntologyTerm.NAME, Operator.EQUALS, codeSystemName));

			OntologyTerm ont = new OntologyTerm();

			if (q.find().size() == 0)
			{

				ont.setOntology_Id(ot.getId());
				ont.setName(t.getDisplayName());
				ont.setTermAccession(t.getCode());
				db.add(ont);

			}
			else
			{
				ont = q.find().get(0);
			}

			listOfOntologyTermIDs.add(ont.getId());

			System.out.println("The mapped ontology term is " + t.getDisplayName() + "\t" + t.getCode());
		}

		return listOfOntologyTermIDs;
	}

}

package plugins.xml;

import gme.cacore_cacore._3_2.gov_nih_nci_cbm_domain.CbmNode;
import gme.cacore_cacore._3_2.gov_nih_nci_cbm_domain.CollectionProtocol;
import gme.cacore_cacore._3_2.gov_nih_nci_cbm_domain.ParticipantCollectionSummary;
import gme.cacore_cacore._3_2.gov_nih_nci_cbm_domain.SpecimenCollectionSummary;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.molgenis.cbm.CbmXmlParser;
import org.molgenis.cbm.Collection_Protocol;
import org.molgenis.cbm.Join_Participant_Collection_Summary_To_Race;
import org.molgenis.cbm.Join_Participant_Collection_Summary_Todiagnosis;
import org.molgenis.cbm.Participant_Collection_Summary;
import org.molgenis.cbm.Specimen_Collection_Summary;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.server.MolgenisRequest;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.util.Entity;

public class ImportCbmData extends PluginModel<Entity>
{

	private File currentFile;

	private static final long serialVersionUID = -6143910771849972946L;

	public ImportCbmData(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "plugins_xml_ImportCbmData";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/xml/ImportCbmData.ftl";
	}

	@Override
	public void handleRequest(Database db, MolgenisRequest request) throws Exception
	{
		if (request.getString("__action").equals("upload"))
		{

			// get uploaded file and do checks
			File file = request.getFile("upload");
			if (file == null)
			{
				throw new Exception("No file selected.");
			}
			else if (!file.getName().endsWith(".xml"))
			{
				throw new Exception("File does not end with '.xml', other formats are not supported.");
			}

			// if no error, set file, and continue
			this.setCurrentFile(file);

			System.out.println("current file : " + this.getCurrentFile());

			// Parsing a document with JAXP

			CbmXmlParser cbmXmlParser = new CbmXmlParser();

			File currentXsdfile = new File("/Users/chaopang/Documents/Chao_Work/Data/CBM_data/CBM/CBM.xsd");

			CbmNode result = cbmXmlParser.load(currentFile, currentXsdfile);

			List<CollectionProtocol> collectionProtocolList = result.getProtocols().getCollectionProtocol();

			List<Participant_Collection_Summary> listOfParticipantSummary = new ArrayList<Participant_Collection_Summary>();

			Map<String, Join_Participant_Collection_Summary_To_Race> listOfParticipantRaceLinkTable = new HashMap<String, Join_Participant_Collection_Summary_To_Race>();

			Map<String, Join_Participant_Collection_Summary_Todiagnosis> listOfParticipantDiagnosisLinkTable = new HashMap<String, Join_Participant_Collection_Summary_Todiagnosis>();

			for (CollectionProtocol collectionProtocolFromJaxb : collectionProtocolList)
			{
				List<ParticipantCollectionSummary> participantCollectionSummaryList = collectionProtocolFromJaxb
						.getEnrolls().getParticipantCollectionSummary();

				Collection_Protocol cp = new Collection_Protocol();

				String collectionProtocolName = collectionProtocolFromJaxb.getName();

				String collectionProtocolIdentifier = collectionProtocolFromJaxb.getIdentifier();

				Integer collectionProtocolID = collectionProtocolFromJaxb.getId();

				for (ParticipantCollectionSummary participantSummary : participantCollectionSummaryList)
				{
					// Create molgenis object for it
					Participant_Collection_Summary participantCollectionSummary = new Participant_Collection_Summary();

					// Set participant count
					participantCollectionSummary.setParticipant_Count(participantSummary.getParticipantCount());

					participantCollectionSummary.setParticipant_Collection_Summary_ID(participantSummary.getId());

					// Ethnicity should go to Race table.
					participantCollectionSummary.setEthnicity(participantSummary.getEthnicity());

					// Set gender to participant
					participantCollectionSummary.setGender(participantSummary.getGender());

					// This race is from CBM model, not molgenis
					List<gme.cacore_cacore._3_2.gov_nih_nci_cbm_domain.Race> listOfRaces = participantSummary
							.getIsClassifiedBy().getRace();

					// Collect all the races from Jaxb Object and create
					// Molgenis entities
					for (gme.cacore_cacore._3_2.gov_nih_nci_cbm_domain.Race eachRace : listOfRaces)
					{
						// Make linkTable for ParticipantCollectionSummary and
						// Race, therefore should check the uniqueness of
						// combination of both ids
						StringBuilder uniqueLinktableKey = new StringBuilder();

						uniqueLinktableKey.append(participantSummary.getId()).append(eachRace.getId());

						if (!listOfParticipantRaceLinkTable.containsKey(uniqueLinktableKey.toString().toLowerCase()
								.trim()))
						{
							Join_Participant_Collection_Summary_To_Race linkTable = new Join_Participant_Collection_Summary_To_Race();

							linkTable.setParticipant_Collection_Summary_ID(participantSummary.getId());

							linkTable.setRace_Id(eachRace.getId());

							listOfParticipantRaceLinkTable.put(uniqueLinktableKey.toString().toLowerCase().trim(),
									linkTable);
						}
					}

					// Get the specimenCollection from the participant summary
					List<SpecimenCollectionSummary> listOfSpecimen = participantSummary.getProvides()
							.getSpecimenCollectionSummary();

					List<Integer> listoFSpecimenID = new ArrayList<Integer>();

					for (SpecimenCollectionSummary specimen : listOfSpecimen)
					{
						listoFSpecimenID.add(specimen.getId());
					}

					// participantCollectionSummary.setIs_Collected_FromSpecimen_Collection_SummaryCollection(collection)

					List<Specimen_Collection_Summary> collectionOfSpecimens = db.find(
							Specimen_Collection_Summary.class, new QueryRule(
									Specimen_Collection_Summary.SPECIMEN_COLLECTION_SUMMARY_ID, Operator.IN,
									listoFSpecimenID));

					participantCollectionSummary
							.setIs_Collected_FromSpecimen_Collection_SummaryCollection(collectionOfSpecimens);

					List<gme.cacore_cacore._3_2.gov_nih_nci_cbm_domain.Diagnosis> listOfDiagnosis = participantSummary
							.getReceives().getDiagnosis();

					for (gme.cacore_cacore._3_2.gov_nih_nci_cbm_domain.Diagnosis diagnosis : listOfDiagnosis)
					{

						StringBuilder uniqueIdentifer = new StringBuilder();

						uniqueIdentifer.append(participantSummary.getId()).append(diagnosis.getId());

						if (!listOfParticipantDiagnosisLinkTable.containsKey(uniqueIdentifer.toString().trim()
								.toLowerCase()))
						{
							Join_Participant_Collection_Summary_Todiagnosis joinTableParticipantDiagnosis = new Join_Participant_Collection_Summary_Todiagnosis();

							// joinTableParticipantDiagnosis.setDiagnosis_Id_Diagnosis_ID(diagnosis.getId());

							joinTableParticipantDiagnosis
									.setParticipant_Collection_Summary_ID_Participant_Collection_Summary_ID(participantSummary
											.getId());

							org.molgenis.cbm.Diagnosis diag = db.find(
									org.molgenis.cbm.Diagnosis.class,
									new QueryRule(org.molgenis.cbm.Diagnosis.DIAGNOSIS_ID, Operator.EQUALS, diagnosis
											.getId())).get(0);

							joinTableParticipantDiagnosis.setDiagnosis_Id(diag);

							joinTableParticipantDiagnosis.setDiagnosis_Id_Diagnosis_ID(diag.getDiagnosis_ID());

							joinTableParticipantDiagnosis.setDiagnosis_Id_DiagnosisType(diag.getDiagnosisType());

							// joinTableParticipantDiagnosis.setDiagnosis_Id_DiagnosisType(diag.getDiagnosisType());

							listOfParticipantDiagnosisLinkTable.put(uniqueIdentifer.toString().trim().toLowerCase(),
									joinTableParticipantDiagnosis);
						}

					}

					// Add this participantSummary to the list collection
					listOfParticipantSummary.add(participantCollectionSummary);
					// participantCollectionSummary.setGender_Id(participantCollectionSummaryList.get(i).getGenderId());
					System.out.println("Just before import in db : " + participantSummary);
				}

			}

			db.add(listOfParticipantSummary);

			// db.add(new ArrayList<Race>(racesToAdd.values()));

			// db.add();

			for (Join_Participant_Collection_Summary_To_Race joinRace : new ArrayList<Join_Participant_Collection_Summary_To_Race>(
					listOfParticipantRaceLinkTable.values()))
			{
				System.out.println(joinRace);

				db.add(joinRace);
			}

			for (Join_Participant_Collection_Summary_Todiagnosis diagnosis : listOfParticipantDiagnosisLinkTable
					.values())
			{

				System.out.println(diagnosis);

				db.add(diagnosis);
			}

		}
	}

	private void setCurrentFile(File file)
	{
		this.currentFile = file;

	}

	private File getCurrentFile()
	{
		return this.currentFile;

	}

	@Override
	public void reload(Database db)
	{
		// try
		// {
		// Database db = this.getDatabase();
		// Query q = db.query(Experiment.class);
		// q.like("name", "test");
		// List<Experiment> recentExperiments = q.find();
		//
		// //do something
		// }
		// catch(Exception e)
		// {
		// //...
		// }
	}

	@Override
	public boolean isVisible()
	{
		// you can use this to hide this plugin, e.g. based on user rights.
		// e.g.
		// if(!this.getLogin().hasEditPermission(myEntity)) return false;
		return true;
	}
}

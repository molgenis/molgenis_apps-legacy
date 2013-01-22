package plugins.xml;

import gme.cacore_cacore._3_2.gov_nih_nci_cbm_domain.CbmNode;
import gme.cacore_cacore._3_2.gov_nih_nci_cbm_domain.CollectionProtocol;
import gme.cacore_cacore._3_2.gov_nih_nci_cbm_domain.ParticipantCollectionSummary;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.molgenis.cbm.CbmXmlParser;
import org.molgenis.cbm.Join_Participant_Collection_Summary_To_Race;
import org.molgenis.cbm.Participant_Collection_Summary;
import org.molgenis.cbm.Race;
import org.molgenis.framework.db.Database;
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

			List<CollectionProtocol> collectionProtocol = result.getProtocols().getCollectionProtocol();

			Map<Integer, Race> racesToAdd = new HashMap<Integer, Race>();

			List<Participant_Collection_Summary> listOfParticipantSummary = new ArrayList<Participant_Collection_Summary>();

			Map<String, Join_Participant_Collection_Summary_To_Race> listOfParticipantRaceLinkTable = new HashMap<String, Join_Participant_Collection_Summary_To_Race>();

			for (int i = 0; i < collectionProtocol.size(); i++)
			{
				List<ParticipantCollectionSummary> participantCollectionSummaryList = collectionProtocol.get(i)
						.getEnrolls().getParticipantCollectionSummary();

				for (ParticipantCollectionSummary participantSummary : participantCollectionSummaryList)
				{
					Participant_Collection_Summary participantCollectionSummary = new Participant_Collection_Summary();

					participantCollectionSummary.setParticipant_Count(participantSummary.getParticipantCount());
					// participantCollectionSummary.setRegistered_To(participantCollectionSummaryList.get(i)
					// .getRegistered_To());

					participantCollectionSummary.setParticipant_Collection_Summary_ID(participantSummary.getId());

					// Ethnicity should go to Race table.
					participantCollectionSummary.setEthnicity(participantSummary.getEthnicity());

					// This race is from CBM model, not molgenis
					List<gme.cacore_cacore._3_2.gov_nih_nci_cbm_domain.Race> listOfRaces = participantSummary
							.getIsClassifiedBy().getRace();

					// participantCollectionSummary.setRegistered_To(registered_to)

					for (gme.cacore_cacore._3_2.gov_nih_nci_cbm_domain.Race eachRace : listOfRaces)
					{
						Race race = new Race();

						String identifier = eachRace.getId() + eachRace.getRace();

						race.setRace_ID(eachRace.getId());

						race.setRace(eachRace.getRace());

						if (!racesToAdd.containsKey(identifier.toLowerCase().trim()))
						{
							racesToAdd.put(race.getRace_ID(), race);
						}

						// Make linkTable for ParticipantCollectionSummary and
						// Race, therefore should check the uniqueness of
						// combination of both ids
						StringBuilder uniqueLinktableKey = new StringBuilder();

						uniqueLinktableKey.append(participantSummary.getId()).append(eachRace.getId());

						if (!listOfParticipantRaceLinkTable.containsKey(uniqueLinktableKey.toString().toLowerCase()
								.trim()))
						{
							Join_Participant_Collection_Summary_To_Race linkTable = new Join_Participant_Collection_Summary_To_Race();

							linkTable.setParticipant_Collection_Summary_ID(participantCollectionSummary
									.getParticipant_Collection_Summary_ID());

							linkTable.setRace_Id(race.getRace_ID());

							listOfParticipantRaceLinkTable.put(uniqueLinktableKey.toString().toLowerCase().trim(),
									linkTable);
						}
					}

					// participantCollectionSummary.setEthnicityId(participantCollectionSummaryList.get(i).getEthnicityId());
					participantCollectionSummary.setGender(participantSummary.getGender());

					listOfParticipantSummary.add(participantCollectionSummary);
					// participantCollectionSummary.setGender_Id(participantCollectionSummaryList.get(i).getGenderId());
					System.out.println("Just before import in db : " + participantSummary);
				}
			}

			db.add(listOfParticipantSummary);

			db.add(new ArrayList<Race>(racesToAdd.values()));

			db.add(new ArrayList<Join_Participant_Collection_Summary_To_Race>(listOfParticipantRaceLinkTable.values()));
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

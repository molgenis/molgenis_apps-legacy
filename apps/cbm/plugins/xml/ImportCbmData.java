package plugins.xml;

import gme.cacore_cacore._3_2.gov_nih_nci_cbm_domain.CbmNode;
import gme.cacore_cacore._3_2.gov_nih_nci_cbm_domain.CollectionProtocol;
import gme.cacore_cacore._3_2.gov_nih_nci_cbm_domain.ParticipantCollectionSummary;

import org.molgenis.cbm.CbmXmlParser;
import org.molgenis.cbm.Participant_Collection_Summary;

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

			File currentXsdfile = new File("/Users/despoina/Documents/__CTMM_project/CBM/CBM.xsd");

			CbmNode result = cbmXmlParser.load(currentFile, currentXsdfile);

			List<CollectionProtocol> collectionProtocol = result.getProtocols().getCollectionProtocol();

			for (int i = 0; i < collectionProtocol.size(); i++)
			{
				List<ParticipantCollectionSummary> participantCollectionSummaryList = collectionProtocol.get(i)
						.getEnrolls().getParticipantCollectionSummary();
				System.out.println(participantCollectionSummaryList.get(i).getId());

				Participant_Collection_Summary participantCollectionSummary = new Participant_Collection_Summary();

				participantCollectionSummary.setParticipant_Count(participantCollectionSummaryList.get(i)
						.getParticipantCount());
				participantCollectionSummary.setRegistered_To(participantCollectionSummaryList.get(i)
						.getRegistered_To());

				if (participantCollectionSummaryList.get(i).getId() == null)
				{
					System.out.println("participantCollectionSummaryID is null");
				}
				else
				{
					participantCollectionSummary.setParticipant_Collection_Summary_ID(participantCollectionSummaryList
							.get(i).getId());// --exception ! this field is AUTO

				}
				participantCollectionSummary.setEthnicity(participantCollectionSummaryList.get(i).getEthnicity());
				participantCollectionSummary.setEthnicityId(participantCollectionSummaryList.get(i).getEthnicityId());
				participantCollectionSummary.setGender(participantCollectionSummaryList.get(i).getGender());
				participantCollectionSummary.setGender_Id(participantCollectionSummaryList.get(i).getGenderId());

				System.out.println("Just before import in db : " + participantCollectionSummary);
				db.add(participantCollectionSummary);
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

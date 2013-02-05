package plugins.xml;

import gme.cacore_cacore._3_2.gov_nih_nci_cbm_domain.AnnotationAvailabilityProfile;
import gme.cacore_cacore._3_2.gov_nih_nci_cbm_domain.CbmNode;
import gme.cacore_cacore._3_2.gov_nih_nci_cbm_domain.CollectionProtocol;
import gme.cacore_cacore._3_2.gov_nih_nci_cbm_domain.ParticipantCollectionSummary;
import gme.cacore_cacore._3_2.gov_nih_nci_cbm_domain.SpecimenAvailabilitySummaryProfile;
import gme.cacore_cacore._3_2.gov_nih_nci_cbm_domain.SpecimenCollectionContact;
import gme.cacore_cacore._3_2.gov_nih_nci_cbm_domain.SpecimenCollectionSummary;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.molgenis.cbm.Address;
import org.molgenis.cbm.Annotation_Availability_Profile;
import org.molgenis.cbm.CbmXmlParser;
import org.molgenis.cbm.Collection_Protocol;
import org.molgenis.cbm.Institution;
import org.molgenis.cbm.Join_Collection_Protocol_To_Institution;
import org.molgenis.cbm.Join_Participant_Collection_Summary_To_Race;
import org.molgenis.cbm.Join_Participant_Collection_Summary_Todiagnosis;
import org.molgenis.cbm.Organization;
import org.molgenis.cbm.Participant_Collection_Summary;
import org.molgenis.cbm.Person;
import org.molgenis.cbm.Specimen_Availability_Summary_Profile;
import org.molgenis.cbm.Specimen_Collection_Contact;
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
			File file = request.getFile("uploadData");

			if (file == null)
			{
				throw new Exception("No file selected.");
			}
			else if (!file.getName().endsWith(".xml"))
			{
				throw new Exception("File does not end with '.xml', other formats are not supported.");
			}

			// get uploaded file and do checks
			File currentXsdfile = request.getFile("uploadSchema");

			if (currentXsdfile == null)
			{
				throw new Exception("No file selected.");
			}
			else if (!currentXsdfile.getName().endsWith(".xsd"))
			{
				throw new Exception("File does not end with '.xml', other formats are not supported.");
			}

			// if no error, set file, and continue
			this.setCurrentFile(file);

			System.out.println("current file : " + this.getCurrentFile());

			// Parsing a document with JAXP

			CbmXmlParser cbmXmlParser = new CbmXmlParser();

			CbmNode result = cbmXmlParser.load(currentFile, currentXsdfile);

			Map<Integer, Participant_Collection_Summary> listOfParticipantSummary = new HashMap<Integer, Participant_Collection_Summary>();

			Map<Integer, Collection_Protocol> listOfCollectionProtocol = new HashMap<Integer, Collection_Protocol>();

			Map<String, Join_Participant_Collection_Summary_To_Race> listOfParticipantRaceLinkTable = new HashMap<String, Join_Participant_Collection_Summary_To_Race>();

			Map<String, Join_Participant_Collection_Summary_Todiagnosis> listOfParticipantDiagnosisLinkTable = new HashMap<String, Join_Participant_Collection_Summary_Todiagnosis>();

			Map<Integer, Institution> listOfInstitues = new HashMap<Integer, Institution>();

			Map<Integer, Organization> listOfOrganizations = new HashMap<Integer, Organization>();

			Map<String, Join_Collection_Protocol_To_Institution> listOfCollectionInstituteLinkTable = new HashMap<String, Join_Collection_Protocol_To_Institution>();

			Map<Integer, Specimen_Availability_Summary_Profile> listOfCollectionConstrained = new HashMap<Integer, Specimen_Availability_Summary_Profile>();

			Map<Integer, Annotation_Availability_Profile> listOfAnnotationProfile = new HashMap<Integer, Annotation_Availability_Profile>();

			Map<Integer, Specimen_Collection_Contact> listOfContacts = new HashMap<Integer, Specimen_Collection_Contact>();

			Map<Integer, Address> listOfAddress = new HashMap<Integer, Address>();

			Map<Integer, Person> listOfPersons = new HashMap<Integer, Person>();

			for (CollectionProtocol collectionProtocolFromJaxb : result.getProtocols().getCollectionProtocol())
			{

				if (!listOfCollectionProtocol.containsKey(collectionProtocolFromJaxb.getId()))
				{

					List<ParticipantCollectionSummary> participantCollectionSummaryList = collectionProtocolFromJaxb
							.getEnrolls().getParticipantCollectionSummary();

					for (ParticipantCollectionSummary participantSummary : participantCollectionSummaryList)
					{

						if (!listOfParticipantSummary.containsKey(participantSummary.getId()))
						{

							// Create molgenis object for it
							Participant_Collection_Summary participantCollectionSummary = new Participant_Collection_Summary();

							// Set participant count
							participantCollectionSummary.setParticipant_Count(participantSummary.getParticipantCount());

							participantCollectionSummary.setParticipant_Collection_Summary_ID(participantSummary
									.getId());

							// Ethnicity should go to Race table.
							participantCollectionSummary.setEthnicity(participantSummary.getEthnicity());

							// Set gender to participant
							participantCollectionSummary.setGender(participantSummary.getGender());

							// ############# Collect all the races from Jaxb
							// Object
							// and
							// ###########
							// ############# create Molgenis entities ##########
							for (gme.cacore_cacore._3_2.gov_nih_nci_cbm_domain.Race eachRace : participantSummary
									.getIsClassifiedBy().getRace())
							{
								// Make linkTable for
								// ParticipantCollectionSummary
								// and
								// Race, therefore should check the uniqueness
								// of
								// combination of both ids
								StringBuilder uniqueLinktableKey = new StringBuilder();

								uniqueLinktableKey.append(participantSummary.getId()).append(eachRace.getId());

								if (!listOfParticipantRaceLinkTable.containsKey(uniqueLinktableKey.toString()
										.toLowerCase().trim()))
								{
									org.molgenis.cbm.Race existingRace = db.find(
											org.molgenis.cbm.Race.class,
											new QueryRule(org.molgenis.cbm.Race.RACE_ID, Operator.EQUALS, eachRace
													.getId())).get(0);

									Join_Participant_Collection_Summary_To_Race linkTable = new Join_Participant_Collection_Summary_To_Race();

									linkTable.setParticipant_Collection_Summary_ID(participantCollectionSummary);

									linkTable.setRace_Id(existingRace);

									listOfParticipantRaceLinkTable.put(uniqueLinktableKey.toString().toLowerCase()
											.trim(), linkTable);
								}
							}

							// Get the specimenCollection from the participant
							// summary
							List<Integer> listoFSpecimenID = new ArrayList<Integer>();

							for (SpecimenCollectionSummary specimen : participantSummary.getProvides()
									.getSpecimenCollectionSummary())
							{
								listoFSpecimenID.add(specimen.getId());
							}

							List<Specimen_Collection_Summary> collectionOfSpecimens = db.find(
									Specimen_Collection_Summary.class, new QueryRule(
											Specimen_Collection_Summary.SPECIMEN_COLLECTION_SUMMARY_ID, Operator.IN,
											listoFSpecimenID));

							participantCollectionSummary
									.setIs_Collected_FromSpecimen_Collection_SummaryCollection(collectionOfSpecimens);

							// Diagnosis
							for (gme.cacore_cacore._3_2.gov_nih_nci_cbm_domain.Diagnosis diagnosis : participantSummary
									.getReceives().getDiagnosis())
							{

								StringBuilder uniqueIdentifer = new StringBuilder();

								uniqueIdentifer.append(participantSummary.getId()).append(diagnosis.getId());

								if (!listOfParticipantDiagnosisLinkTable.containsKey(uniqueIdentifer.toString().trim()
										.toLowerCase()))
								{
									Join_Participant_Collection_Summary_Todiagnosis joinTableParticipantDiagnosis = new Join_Participant_Collection_Summary_Todiagnosis();

									joinTableParticipantDiagnosis
											.setParticipant_Collection_Summary_ID(participantCollectionSummary);

									org.molgenis.cbm.Diagnosis diag = db.find(
											org.molgenis.cbm.Diagnosis.class,
											new QueryRule(org.molgenis.cbm.Diagnosis.DIAGNOSIS_ID, Operator.EQUALS,
													diagnosis.getId())).get(0);

									joinTableParticipantDiagnosis.setDiagnosis_Id(diag);

									joinTableParticipantDiagnosis.setDiagnosis_Id_Diagnosis_ID(diag.getDiagnosis_ID());

									joinTableParticipantDiagnosis
											.setDiagnosis_Id_DiagnosisType(diag.getDiagnosisType());

									listOfParticipantDiagnosisLinkTable.put(uniqueIdentifer.toString().trim()
											.toLowerCase(), joinTableParticipantDiagnosis);
								}
							}

							// Add this participantSummary to the list
							// collection
							listOfParticipantSummary.put(participantSummary.getId(), participantCollectionSummary);
						}
					}

					// Collect information for collection_protocol
					Collection_Protocol cp = new Collection_Protocol();

					String collectionProtocolName = collectionProtocolFromJaxb.getName();

					String collectionProtocolIdentifier = collectionProtocolFromJaxb.getIdentifier();

					Integer collectionProtocolID = collectionProtocolFromJaxb.getId();

					cp.setCollectionProtocolID(collectionProtocolID);

					cp.setName(collectionProtocolName);

					cp.setIdentifier(collectionProtocolIdentifier);

					// TODO: The date format comes in XMLDateFormat, need to
					// convert it
					// cp.setDate_Last_Updated(collectionProtocolFromJaxb.getDateLastUpdated().toString());
					//
					// TODO: The date format comes in XMLDateFormat, need to
					// convert it
					// cp.setEnd_Date(collectionProtocolFromJaxb.getEndDate().toString());

					// Collection information for institution and organization
					for (gme.cacore_cacore._3_2.gov_nih_nci_cbm_domain.Institution cbmInsititue : collectionProtocolFromJaxb
							.getResidesAt().getInstitution())
					{

						Institution institue = null;

						if (!listOfOrganizations.containsKey(cbmInsititue.getId()))
						{
							Organization organization = new Organization();

							organization.setName(cbmInsititue.getName());

							organization.setOrganization_ID(cbmInsititue.getId());

							institue = new Institution();

							institue.setInstitution_ID(organization);

							institue.setHomepage_URL(cbmInsititue.getHomepageURL());

							if (!listOfInstitues.containsKey(cbmInsititue.getId()))
							{
								listOfInstitues.put(cbmInsititue.getId(), institue);
							}

							listOfOrganizations.put(cbmInsititue.getId(), organization);

						}
						else
						{
							institue = listOfInstitues.get(cbmInsititue.getId());
						}

						StringBuilder builder = new StringBuilder();

						if (!listOfCollectionInstituteLinkTable.containsKey(builder.append(collectionProtocolID)
								.append(cbmInsititue.getId()).toString()))
						{
							Join_Collection_Protocol_To_Institution joinTable = new Join_Collection_Protocol_To_Institution();

							joinTable.setInstitution_ID(institue);

							joinTable.setCollection_Protocol_ID(cp);

							listOfCollectionInstituteLinkTable.put(
									builder.append(collectionProtocolID).append(cbmInsititue.getId()).toString(),
									joinTable);
						}
					}

					// Set the isconstrained entity
					if (collectionProtocolFromJaxb.getIsConstrainedBy() != null)
					{
						SpecimenAvailabilitySummaryProfile profileJaxb = collectionProtocolFromJaxb
								.getIsConstrainedBy();

						Specimen_Availability_Summary_Profile specimenProfile = null;

						if (!listOfCollectionConstrained.containsKey(profileJaxb.getId()))
						{
							specimenProfile = new Specimen_Availability_Summary_Profile();

							specimenProfile.setIs_Collaboration_Required(profileJaxb.isIsCollaborationRequired());

							specimenProfile.setIs_Available_To_Outside_Institution(profileJaxb
									.isIsAvailableToOutsideInstitution());

							specimenProfile.setIs_Available_To_Foreign_Investigators(profileJaxb
									.isIsAvailableToForeignInvestigators());

							specimenProfile.setIs_Available_To_Commercial_Organizations(profileJaxb
									.isIsAvailableToCommercialOrganizations());

							specimenProfile.setSpecimen_Availability_Summary_Profile_ID(profileJaxb.getId());

							listOfCollectionConstrained.put(profileJaxb.getId(), specimenProfile);

						}
						else
						{
							specimenProfile = listOfCollectionConstrained.get(profileJaxb.getId());
						}
						cp.setIs_Constrained_By(specimenProfile);
					}

					if (collectionProtocolFromJaxb.getMakesAvailable() != null)
					{
						AnnotationAvailabilityProfile annotationProfileJaxb = collectionProtocolFromJaxb
								.getMakesAvailable();

						Annotation_Availability_Profile profile = null;

						if (!listOfAnnotationProfile.containsKey(annotationProfileJaxb.getId()))
						{
							profile = new Annotation_Availability_Profile();

							profile.setHas_Additional_Patient_Demographics(annotationProfileJaxb
									.isHasAdditionalPatientDemographics());

							profile.setHas_Exposure_History(annotationProfileJaxb.isHasExposureHistory());

							profile.setHas_Family_History(annotationProfileJaxb.isHasFamilyHistory());

							profile.setHas_Histopathologic_Information(annotationProfileJaxb
									.isHasHistopathologicInformation());

							profile.setHas_Lab_Data(annotationProfileJaxb.isHasLabData());

							profile.setHas_Longitudinal_Specimens(annotationProfileJaxb.isHasLongitudinalSpecimens());

							profile.setHas_Matched_Specimens(annotationProfileJaxb.isHasMatchedSpecimens());

							profile.setHas_Outcome_Information(annotationProfileJaxb.isHasOutcomeInformation());

							profile.setHas_Participants_Available_For_Followup(annotationProfileJaxb
									.isHasParticipantsAvailableForFollowup());

							profile.setHas_Treatment_Information(annotationProfileJaxb.isHasTreatmentInformation());

							profile.setAnnotation_Availability_Profile_ID(annotationProfileJaxb.getId());

							listOfAnnotationProfile.put(annotationProfileJaxb.getId(), profile);
						}
						else
						{
							profile = listOfAnnotationProfile.get(annotationProfileJaxb.getId());
						}

						cp.setMakes_Available(profile);
					}

					// Collect information on Specimen_collection_contact and
					// Person
					// and Address
					if (collectionProtocolFromJaxb.getIsAssignedTo() != null)
					{
						SpecimenCollectionContact collectionContactJaxb = collectionProtocolFromJaxb.getIsAssignedTo();

						Specimen_Collection_Contact contact = null;

						if (!listOfContacts.containsKey(collectionContactJaxb.getId()))
						{
							Person person = new Person();

							person.setFirst_Name(collectionContactJaxb.getFirstName());

							person.setLast_Name(collectionContactJaxb.getLastName());

							person.setFull_Name(collectionContactJaxb.getFullName());

							person.setEmail_Address(collectionContactJaxb.getEmailAddress());

							person.setMiddle_Name_Or_Initial(collectionContactJaxb.getMiddleNameOrInitial());

							person.setPerson_ID(collectionContactJaxb.getId());

							listOfPersons.put(collectionContactJaxb.getId(), person);

							contact = new Specimen_Collection_Contact();

							contact.setSpecimen_Collection_Contact_ID(person);

							contact.setPhone(collectionContactJaxb.getPhone());

							gme.cacore_cacore._3_2.gov_nih_nci_cbm_domain.Address addressJaxb = collectionContactJaxb
									.getIsLocatedAt();

							if (addressJaxb != null)
							{
								if (listOfAddress.containsKey(addressJaxb.getId()))
								{
									contact.setAddress_Id(listOfAddress.get(addressJaxb.getId()));
								}
								else
								{
									Address address = new Address();

									address.setAddress_ID(addressJaxb.getId());

									address.setCity(addressJaxb.getCity());

									address.setCountry(addressJaxb.getCountry());

									address.setDepartment_Or_Division(addressJaxb.getDepartmentOrDivision());

									address.setEntity_Name(addressJaxb.getEntityName());

									address.setEntity_Number(addressJaxb.getEntityNumber());

									address.setFloor_Or_Premises(addressJaxb.getFloorOrPremises());

									address.setPost_Office_Box(addressJaxb.getPostOfficeBox());

									address.setState(addressJaxb.getState());

									address.setStreet_Or_Thoroughfare_Extension_Name(addressJaxb
											.getStreetOrThoroughfareExtensionName());

									address.setStreet_Or_Thoroughfare_Name_And_Type(addressJaxb
											.getStreetOrThoroughfareNameAndType());

									address.setStreet_Or_Thoroughfare_Number(addressJaxb
											.getStreetOrThoroughfareNumber());

									address.setStreet_Or_Thoroughfare_Section_Name(addressJaxb
											.getStreetOrThoroughfareSectionName());

									address.setStreet_Post_Directional(addressJaxb.getStreetPostDirectional());

									address.setStreet_Pre_Directional(addressJaxb.getStreetPreDirectional());

									address.setZip_Code(addressJaxb.getZipCode());

									listOfAddress.put(addressJaxb.getId(), address);
								}
							}

							listOfContacts.put(collectionContactJaxb.getId(), contact);

						}
						else
						{
							contact = listOfContacts.get(collectionContactJaxb.getId());
						}

						cp.setIs_Assigned_To(contact);
					}

					listOfCollectionProtocol.put(collectionProtocolFromJaxb.getId(), cp);
				}
			}

			db.add(new ArrayList<Person>(listOfPersons.values()));

			db.add(new ArrayList<Address>(listOfAddress.values()));

			db.add(new ArrayList<Specimen_Collection_Contact>(listOfContacts.values()));

			db.add(new ArrayList<Annotation_Availability_Profile>(listOfAnnotationProfile.values()));

			db.add(new ArrayList<Specimen_Availability_Summary_Profile>(listOfCollectionConstrained.values()));

			db.add(new ArrayList<Organization>(listOfOrganizations.values()));

			db.add(new ArrayList<Institution>(listOfInstitues.values()));

			db.add(new ArrayList<Participant_Collection_Summary>(listOfParticipantSummary.values()));

			db.add(new ArrayList<Collection_Protocol>(listOfCollectionProtocol.values()));

			db.add(new ArrayList<Join_Participant_Collection_Summary_To_Race>(listOfParticipantRaceLinkTable.values()));

			db.add(new ArrayList<Join_Participant_Collection_Summary_Todiagnosis>(listOfParticipantDiagnosisLinkTable
					.values()));
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

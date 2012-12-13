package org.molgenis.animaldb.convertors.generic;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.log4j.Logger;
import org.molgenis.animaldb.NamePrefix;
import org.molgenis.animaldb.commonservice.CommonService;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.security.Login;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.pheno.Panel;
import org.molgenis.protocol.ProtocolApplication;
import org.molgenis.util.CsvFileReader;
import org.molgenis.util.Tuple;

public class AnimalImporter
{
	private static final int MILLSECS_PER_DAY = 86400000;
	private Database db;
	private CommonService ct;
	private Logger logger;
	private String userName;
	private String invName;
	private String defaultSpecies;
	private String defaultSpeciesNamePrefix;
	// private String defaultBreedingLine;
	private String defaultResponsibleResearcher;
	// private List<Measurement> measurementsToAddList;
	private List<ProtocolApplication> protocolAppsToAddList;
	private List<Individual> animalsToAddList;
	private List<String> animalNames;
	private List<ObservedValue> valuesToAddList;
	private List<Panel> panelsToAddList;
	private Map<String, String> appMap;
	private SimpleDateFormat dbFormat = new SimpleDateFormat("d-M-yyyy H:mm", Locale.US);
	private SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dobFormat = new SimpleDateFormat("d-M-y", Locale.US);
	// private SimpleDateFormat expDbFormat = new
	// SimpleDateFormat("yyyy-M-d H:mm:ss", Locale.US);
	private SimpleDateFormat newDateOnlyFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
	// private SimpleDateFormat yearOnlyFormat = new SimpleDateFormat("yyyy",
	// Locale.US);
	private Map<String, String> sourceMap;
	private Map<String, Integer> parentgroupNrMap;
	private Map<String, Integer> litterNrMap;
	private Map<String, List<String>> litterMap;
	// private Map<String, String> decMap;
	// private Map<String, String> alternativeDecMap;
	// private Map<String, Integer> researcherMap;
	private Map<String, ObservedValue> activeMap;
	// private Map<String, ObservedValue> projectStartDateMap;
	// private Map<String, ObservedValue> projectEndDateMap;
	private Map<String, String> animalMap;
	private Map<String, Date> removalDateMap;
	private int highestNr;
	private String defaultSourceName;

	// private List<String> lineNamesList;

	public AnimalImporter(Database db, Login login) throws Exception
	{
		userName = login.getUserName();
		this.db = db;
		ct = CommonService.getInstance();
		ct.setDatabase(this.db);
		ct.makeObservationTargetNameMap(userName, false);
		logger = Logger.getLogger("LoadUliDb");

		// highestNr = ct.getHighestNumberForPrefix("mm_") + 1;

		// If needed, make investigation
		invName = "System";
		// Investigation system should be available!
		/*
		 * if (ct.getInvestigationId(invName) == -1) { Investigation newInv =
		 * new Investigation(); newInv.setName(invName);
		 * newInv.setOwns_Name(userName); newInv.setCanRead_Name("admin");
		 * db.add(newInv); }
		 */

		// Add some measurements that we'll need
		// FIXME add if else, for already present.
		// measurementsToAddList = new ArrayList<Measurement>();
		// measurementsToAddList.add(ct.createMeasurement(invName,
		// "OldAnimalId", "String", null, null, false, "string",
		// "To set the previous AnimalID if present", userName));
		// measurementsToAddList.add(ct.createMeasurement(invName,
		// "OldLitterId", "String", null, null, false, "string",
		// "To link an animal to a litter with the previous litterID if present.",
		// userName));

		// Init lists that we can later add to the DB at once
		protocolAppsToAddList = new ArrayList<ProtocolApplication>();
		animalsToAddList = new ArrayList<Individual>();
		animalNames = new ArrayList<String>();
		valuesToAddList = new ArrayList<ObservedValue>();
		panelsToAddList = new ArrayList<Panel>();
		// lineNamesList = new ArrayList<String>();

		appMap = new HashMap<String, String>();
		sourceMap = new HashMap<String, String>();
		parentgroupNrMap = new HashMap<String, Integer>();
		litterNrMap = new HashMap<String, Integer>();
		litterMap = new HashMap<String, List<String>>();
		// decMap = new HashMap<String, String>();
		// alternativeDecMap = new HashMap<String, String>();
		// researcherMap = new HashMap<String, Integer>();
		activeMap = new HashMap<String, ObservedValue>();
		animalMap = new HashMap<String, String>();
		removalDateMap = new HashMap<String, Date>();
		// projectStartDateMap = new HashMap<String, ObservedValue>();
		// projectEndDateMap = new HashMap<String, ObservedValue>();
		//
		// // Create lines
		// createLine("WT");
		// createLine("Per dKO");
		// createLine("Cry dKO");
		// createLine("PerCry");
		// createLine("CBA/CaJ WT_breeding");
		// createLine("C57BL/6j WT_breeding");
		// createLine("C3H/He");
		// createLine("DBA");
		// createLine("ICR(CD-1)");
		// createLine("Swing");
		// createLine("CK1e");
		// createLine("unknown");

		// FIXME also add a line for these animals.
	}

	/*
	 * private void createLine(String lineName) throws DatabaseException,
	 * IOException, ParseException { panelsToAddList.add(ct.createPanel(invName,
	 * lineName, userName)); // Label it as line using the (Set)TypeOfGroup
	 * protocol and feature Date now = new Date();
	 * valuesToAddList.add(ct.createObservedValue(invName,
	 * appMap.get("SetTypeOfGroup"), now, null, "TypeOfGroup", lineName, "Line",
	 * null)); // Set the source of the line (always 'Kweek chronobiologie')
	 * valuesToAddList.add(ct.createObservedValue(invName,
	 * appMap.get("SetSource"), now, null, "Source", lineName, null,
	 * "Kweek chronobiologie")); // Set the species of the line (always 'House
	 * mouse') valuesToAddList.add(ct.createObservedValue(invName,
	 * appMap.get("SetSpecies"), now, null, "Species", lineName, null,
	 * "House mouse")); lineNamesList.add(lineName); }
	 */

	public void convertFromZip(String filename) throws Exception
	{
		// Path to store files from zip
		File tmpDir = new File(System.getProperty("java.io.tmpdir"));
		String path = tmpDir.getAbsolutePath() + File.separatorChar;
		// Extract zip
		ZipFile zipFile = new ZipFile(filename);
		Enumeration<?> entries = zipFile.entries();
		while (entries.hasMoreElements())
		{
			ZipEntry entry = (ZipEntry) entries.nextElement();
			copyInputStream(zipFile.getInputStream(entry),
					new BufferedOutputStream(new FileOutputStream(path + entry.getName())));
		}
		zipFile.close();
		// Run convertor steps
		populateProtocolApplication();
		populateImportDefaults(path + "settings.csv");
		populateAnimal(path + "animals.csv");
		parseParentRelations(path + "litters.csv");
		// populateDec(path + "Experiments.csv");
		// parseDecRelations(path + "IDsInExp.csv");

		writeToDb();
	}

	public void writeToDb() throws Exception
	{

		// db.add(measurementsToAddList);
		// logger.debug("Measurements successfully added");

		db.add(protocolAppsToAddList);
		logger.debug("Protocol applications successfully added");

		db.add(animalsToAddList);
		logger.debug("Animals successfully added");

		// Make entry in name prefix table with highest animal nr.
		// FIXME : find solution for prefixes!! maybe add an extra settins file?
		List<NamePrefix> prefixList = db.query(NamePrefix.class).eq(NamePrefix.TARGETTYPE, "animal")
				.eq(NamePrefix.PREFIX, this.defaultSpeciesNamePrefix).find();
		if (prefixList.size() == 1)
		{
			NamePrefix namePrefix = prefixList.get(0);
			namePrefix.setHighestNumber(highestNr);
			db.update(namePrefix);
		}
		else
		{
			// fail badly //FIXME:
		}

		db.add(panelsToAddList);
		logger.debug("Panels successfully added");

		// Make entries in name prefix table with highest parentgroup nrs.
		prefixList = new ArrayList<NamePrefix>();
		for (String lineName : parentgroupNrMap.keySet())
		{
			NamePrefix namePrefix = new NamePrefix();
			namePrefix.setTargetType("parentgroup");
			namePrefix.setPrefix("PG_" + lineName + "_");
			namePrefix.setHighestNumber(parentgroupNrMap.get(lineName));
			System.out.println("################## pg.get(linename)" + parentgroupNrMap.get(lineName));
			prefixList.add(namePrefix);
		}
		// Make entries in name prefix table with highest litter nrs.
		for (String lineName : litterNrMap.keySet())
		{
			NamePrefix namePrefix = new NamePrefix();
			namePrefix.setTargetType("litter");
			namePrefix.setPrefix("LT_" + lineName + "_");
			namePrefix.setHighestNumber(litterNrMap.get(lineName));
			prefixList.add(namePrefix);
		}
		db.add(prefixList);
		logger.debug("Prefixes successfully added");

		// Add remaining Active, Project StartDate and Project EndDate values to
		// value list
		valuesToAddList.addAll(activeMap.values());
		// valuesToAddList.addAll(projectStartDateMap.values());
		// valuesToAddList.addAll(projectEndDateMap.values());

		int batchSize = 1000;
		for (int valueStart = 0; valueStart < valuesToAddList.size(); valueStart += batchSize)
		{
			int valueEnd = Math.min(valuesToAddList.size(), valueStart + batchSize);
			db.add(valuesToAddList.subList(valueStart, valueEnd));
			logger.debug("Values " + valueStart + " through " + valueEnd + " successfully added");
		}

		ct.makeObservationTargetNameMap(userName, true);
	}

	public void populateImportDefaults(String filename) throws Exception
	{
		// final Date now = new Date();

		File file = new File(filename);
		CsvFileReader reader = new CsvFileReader(file);
		for (Tuple tuple : reader)
		{

			this.defaultSpecies = tuple.getString("Species");
			this.defaultSpeciesNamePrefix = tuple.getString("SpeciesNamePrefix");
			// this.defaultBreedingLine = tuple.getString("Breedingline");
			this.defaultResponsibleResearcher = tuple.getString("Responsible Researcher");
		}
		reader.close();
	}

	public void populateAnimal(String filename) throws Exception
	{
		final Date now = new Date();

		File file = new File(filename);
		CsvFileReader reader = new CsvFileReader(file);
		for (Tuple tuple : reader)
		{
			// FIXME prefix string
			String animalName = this.defaultSpeciesNamePrefix + ct.prependZeros(Integer.toString(highestNr++), 6);
			animalNames.add(animalName);
			Individual newAnimal = ct.createIndividual(invName, animalName, userName);
			animalsToAddList.add(newAnimal);

			// Set some defaults: --> Animal Type,
			String animalType = "A. Gewoon dier";
			valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetAnimalType"), now, null, "AnimalType",
					animalName, animalType, null));

			// convert ID -> OldAnimalId
			String oldAnimalId = tuple.getString("AnimalID");
			valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetOldAnimalId"), now, null, "OldAnimalId",
					animalName, oldAnimalId, null));
			animalMap.put(oldAnimalId, animalName);

			// convert Sex -> Sex
			String sex = tuple.getString("Sex");
			valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetSex"), now, null, "Sex", animalName,
					null, sex));

			// convert Species -> Species
			valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetSpecies"), now, null, "Species",
					animalName, null, this.defaultSpecies));

			// convert GeneticBackground -> Background (default "no background")
			String background = tuple.getString("GeneticBackground");
			String backgroundName;
			if (background != null)
			{
				backgroundName = background;
				// TODO add check on background present in db?
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetBackground"), now, null,
						"Background", animalName, null, backgroundName));
			}
			else
			{
				backgroundName = "No Background";
			}

			// convert BreedingLine -> Breedingline
			// FIXME add not null checks and conversion to default breeding line
			// from settigs if null?
			String breedingline = tuple.getString("BreedingLine");
			valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetSBreedingLine"), now, null,
					"BreedingLine", animalName, null, breedingline));

			// convert litterId -> OldLitterId & create a per litter map of
			// animals, to match litter later
			String litterId = tuple.getString("LitterId");
			valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetOldLitterId"), now, null, "OldLitterId",
					animalName, litterId, null));

			List<String> animalNameList;
			if (litterMap.get(litterId) != null)
			{
				animalNameList = litterMap.get(litterId);
			}
			else
			{
				animalNameList = new ArrayList<String>();
			}
			animalNameList.add(animalName);
			litterMap.put(litterId, animalNameList);

			// convert DateOfBirth -> DateOfBirth
			String dobDateString = tuple.getString("DateOfBirth");
			// FIXME: add not null checks!!!
			if (dobDateString != null)
			{
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetDateOfBirth"), now, null,
						"DateOfBirth", animalName, dobDateString, null));
			}

			// convert WeanDate -> WeanDate
			String weanDateString = tuple.getString("WeanDate");
			// FIXME: add not null checks!!!
			if (weanDateString != null)
			{
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetWeanDate"), now, null, "WeanDate",
						animalName, weanDateString, null));

			}
			else
			{
				// FIXME fail badly
				// Throw weandate is mandatory error

			}

			// convert Weandate -> Weandata and use as facilitystartdate for
			// yearly report.
			String state = "Alive"; // default for all animals to Alive, modify
									// later if appropriate
			String startDateString = tuple.getString("Weandate");

			// arrival date and rem date -> Active start and end time
			// Don't set DeathDate (to rem date) because we do not know if the
			// animal was terminated or removede

			Date startDate = null;
			Date remDate = null;
			if (startDateString != null)
			{
				startDate = inputFormat.parse(startDateString);
			}
			String remDateString = tuple.getString("FacilityRemovalDate");
			if (remDateString != null)
			{
				state = "Dead";
				remDate = inputFormat.parse(remDateString);
				removalDateMap.put(animalName, remDate);
				// check if death
				String removal = tuple.getString("RemovalCause");
				if (removal.toLowerCase().equals("dead"))
				{
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetDeathDate"), remDate, null,
							"DeathDate", animalName, dobFormat.format(remDate), null));
				}
				else
				{
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetRemovalDate"), remDate, null,
							"RemovalDate", animalName, dobFormat.format(remDate), null));
				}
			}

			// rem cause -> Removal
			String removal = tuple.getString("RemovalCause").toLowerCase();
			if (removal != null && remDate != null)
			{
				// onvert english input to dutch official vwa codes.
				if (removal.equals("dead"))
				{
					removal = "dood";
				}
				else if (removal.equals("alive: other rug"))
				{
					removal = "levend afgevoerd andere organisatorische eenheid RuG";
				}
				else if (removal.equals("alive: other registered nl"))
				{
					removal = "levend afgevoerd gereg. onderzoeksinstelling NL";
				}
				else if (removal.equals("alive: other registered eu"))
				{
					removal = "levend afgevoerd gereg. onderzoeksinstelling EU";
				}
				else if (removal.equals("alive: other"))
				{
					removal = "levend afgevoerd andere bestemming";
				}
				else
				{
					// FIXME fail badly if input does not meet this criteria.
				}
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetRemoval"), remDate, null, "Removal",
						animalName, removal, null));
			}
			// add animal with state to active map and create the active value
			activeMap.put(animalName, ct.createObservedValue(invName, appMap.get("SetActive"), startDate, remDate,
					"Active", animalName, state, null));

			// convert Source -> Source
			String sourceName = tuple.getString("Source");
			if (sourceName != null)
			{
				// TODO check if source name exists.
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetSource"), now, null, "Source",
						animalName, null, sourceName));
				sourceMap.put(animalName, sourceName);
			}

			// convert ResponsibleResearcher -> ResponsibleResearcher
			valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetResponsibleResearcher"), now, null,
					"ResponsibleResearcher", animalName, this.defaultResponsibleResearcher, null));

		}
		reader.close();
	}

	public void parseParentRelations(String filename) throws Exception
	{
		// System.out.println("############## Start parsing parent relations");

		File file = new File(filename);
		CsvFileReader reader = new CsvFileReader(file);
		// Active / Inactive --> default to inactive litter
		String active = "Inactive";
		for (Tuple tuple : reader)
		{
			Date now = new Date();

			// litter nr -> skip first two lines
			String litter = tuple.getString("litterID");

			// ID mother
			String motherName = animalMap.get(tuple.getString("MotherID"));
			// lit mtr -> SKIP
			// ID father
			String fatherName = animalMap.get(tuple.getString("FatherID"));
			// lit ftr -> SKIP
			// GMO -> SKIP
			// Pair StartDate -> convert to yyyy-mm-dd format
			String startDate = tuple.getString("PairStartDate");
			if (startDate != null && !startDate.equals(""))
			{
				Date tmpStartDate = inputFormat.parse(startDate);
				startDate = newDateOnlyFormat.format(tmpStartDate);
			}

			// get the all the animals with this litterid
			Query<ObservedValue> OldLitterQuery = db.query(ObservedValue.class);
			OldLitterQuery.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "Litter"));
			OldLitterQuery.addRules(new QueryRule(ObservedValue.RELATION_NAME, Operator.EQUALS, litter));
			List<ObservedValue> individualValueList = OldLitterQuery.find();
			int FemaleCtr = 0;
			int MaleCtr = 0;
			int UnkSexCtr = 0;
			String weanDate = null;
			String dobDate = null;
			String lineName = null;
			for (ObservedValue v : individualValueList)
			{
				if (MaleCtr + FemaleCtr + UnkSexCtr == 0)
				{
					// Get weanDate from first sibling
					weanDate = ct.getMostRecentValueAsString(v.getTarget_Name(), "WeanDate");
					// Get birthDate from first sibling
					dobDate = ct.getMostRecentValueAsString(v.getTarget_Name(), "DateOfBirth");
					// Getbreedigline from first sibling
					lineName = ct.getMostRecentValueAsXrefName(v.getTarget_Name(), "BreedingLine");
				}
				String sex = ct.getMostRecentValueAsXrefName(v.getTarget_Name(), "Sex");
				if (sex.equalsIgnoreCase("Male"))
				{
					MaleCtr++;
				}
				else if (sex.equalsIgnoreCase("Female"))
				{
					FemaleCtr++;
				}
				else
				{
					UnkSexCtr++;
				}
			}

			int nrBorn = 0;
			if (tuple.getInt("NrBorn") != null)
			{
				nrBorn = tuple.getInt("NrBorn");
			}
			else
			{
				nrBorn = FemaleCtr + MaleCtr + UnkSexCtr;
			}
			// remarks
			String remark = tuple.getString("Remarks");

			// Wean date -> convert to yyyy-mm-dd format
			if (weanDate == null || weanDate.equals(""))
			{
				active = "Active"; // set litter active if not weaned
			}

			// Create a parentgroup

			String pgActive = "Inactive";
			int parentgroupNr = 1;
			if (parentgroupNrMap.containsKey(lineName))
			{
				parentgroupNr = parentgroupNrMap.get(lineName) + 1;
			}
			parentgroupNrMap.put(lineName, parentgroupNr);
			String parentgroupNrPart = ct.prependZeros("" + parentgroupNr, 6);
			String parentgroupName = "PG_" + lineName + "_" + parentgroupNrPart;
			// System.out.println("#########pgName: " + parentgroupName);
			panelsToAddList.add(ct.createPanel(invName, parentgroupName, userName));
			valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetTypeOfGroup"), now, null, "TypeOfGroup",
					parentgroupName, "Parentgroup", null));
			// Link parents to parentgroup (if known)
			// System.out.println("#########pgM: " + motherName);
			if (motherName != null)
			{
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetParentgroupMother"), now, null,
						"ParentgroupMother", parentgroupName, null, motherName));
			}
			// System.out.println("#########pgM: " + fatherName);
			if (fatherName != null)
			{
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetParentgroupFather"), now, null,
						"ParentgroupFather", parentgroupName, null, fatherName));
			}
			// Set line of parentgroup
			valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetLine"), now, null, "Line",
					parentgroupName, null, lineName));
			// Set source of parentgroup
			valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetSource"), now, null, "Source",
					parentgroupName, null, this.defaultSourceName));
			// Set StartDate
			valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetStartDate"), now, null, "StartDate",
					parentgroupName, startDate, null));
			// set active to a sensible window
			double deltaDays = (now.getTime() - dbFormat.parse(startDate).getTime()) / MILLSECS_PER_DAY;
			if (deltaDays < 60)
			{
				pgActive = "Active";
			}
			valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetActive"), now, null, "Active",
					parentgroupName, pgActive, null));

			// Make a litter and set birth, wean and genotype dates + sizes
			int litterNr = 1;
			if (litterNrMap.containsKey(lineName))
			{
				litterNr = litterNrMap.get(lineName) + 1;
			}
			litterNrMap.put(lineName, litterNr);
			String litterNrPart = ct.prependZeros("" + litterNr, 6);
			String litterName = "LT_" + lineName + "_" + litterNrPart;
			panelsToAddList.add(ct.createPanel(invName, litterName, userName));
			valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetTypeOfGroup"), now, null, "TypeOfGroup",
					litterName, "Litter", null));
			valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetActive"), now, null, "Active",
					litterName, active, null));
			valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetDateOfBirth"), now, null, "DateOfBirth",
					litterName, dobDate, null));
			valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetWeanDate"), now, null, "WeanDate",
					litterName, weanDate, null));
			valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetSize"), now, null, "Size", litterName,
					Integer.toString(nrBorn), null));
			valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetWeanSize"), now, null, "WeanSize",
					litterName, Integer.toString(FemaleCtr + MaleCtr + UnkSexCtr), null));
			valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetWeanSizeMale"), now, null,
					"WeanSizeMale", litterName, Integer.toString(MaleCtr), null));
			valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetWeanSizeFemale"), now, null,
					"WeanSizeFemale", litterName, Integer.toString(FemaleCtr), null));
			valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetWeanSizeUnknown"), now, null,
					"WeanSizeUnknown", litterName, Integer.toString(UnkSexCtr), null));
			// Set Remark
			if (remark != null && !remark.equals(""))
			{
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetRemark"), now, null, "Remark",
						litterName, remark, null));
			}
			// Set line also on litter
			if (!lineName.equals("unknown"))
			{
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetLine"), now, null, "Line",
						litterName, null, lineName));
			}
			// Set source also on litter
			valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetSource"), now, null, "Source",
					litterName, null, defaultSourceName));
			// Link litter to parentgroup
			valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetParentgroup"), now, null, "Parentgroup",
					litterName, null, parentgroupName));

			// Find animals that came out of this litter, using 'litter' as
			// index for the map of litters and animals
			if (litterMap.get(litter) != null)
			{
				System.out.println("--> litter: " + litter);
				for (String animalName : litterMap.get(litter))
				{
					// Link animal to litter
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetLitter"), now, null, "Litter",
							animalName, null, litterName));
					// Set parents also on animal, using the Mother and
					// Father measurements
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetMother"), now, null, "Mother",
							animalName, null, motherName));
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetFather"), now, null, "Father",
							animalName, null, fatherName));
					// Set birth date, line also on animal
					// Get Active value from map; every animal has one

					ObservedValue activeValue = activeMap.get(animalName);
					System.out.println("----> " + activeValue.getTarget_Name());
					if (activeValue.getTime() == null)
					{
						// FIXME is this still necessary ???

						activeValue.setTime(dbFormat.parse(weanDate));
					}
					activeMap.remove(animalName);
					valuesToAddList.add(activeValue);

					// dob is set from animals, not from litters
					// valuesToAddList.add(ct.createObservedValue(invName,
					// appMap.get("SetDateOfBirth"), now, null,
					// "DateOfBirth", animalName, dob, null));

					// valuesToAddList.add(ct.createObservedValue(invName,
					// appMap.get("SetWeandDate"), now, null,
					// "WeanDate", animalName, weanDate, null));

					if (!lineName.equals("unknown"))
					{
						valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetLine"), now, null, "Line",
								animalName, null, lineName));
					}
				}
			}
		}
		reader.close();
	}

	public void populateProtocolApplication() throws Exception
	{
		// lines
		makeProtocolApplication("SetTypeOfGroup");
		makeProtocolApplication("SetSource");
		makeProtocolApplication("SetSpecies");
		// animals
		makeProtocolApplication("SetOldAnimalId");
		makeProtocolApplication("SetAnimalType");
		makeProtocolApplication("SetOldLitterId");
		makeProtocolApplication("SetSex");
		makeProtocolApplication("SetBackground");

		makeProtocolApplication("SetActive");
		makeProtocolApplication("SetRemoval");
		makeProtocolApplication("SetDateOfBirth");
		makeProtocolApplication("SetEarmark");
		makeProtocolApplication("SetResponsibleResearcher");
		// parent relations
		makeProtocolApplication("SetParentgroupMother");
		makeProtocolApplication("SetParentgroupFather");
		makeProtocolApplication("SetMother");
		makeProtocolApplication("SetFather");
		makeProtocolApplication("SetLine");
		makeProtocolApplication("SetStartDate");
		makeProtocolApplication("SetWeanDate");
		makeProtocolApplication("SetGenotypeDate");
		makeProtocolApplication("SetSize");
		makeProtocolApplication("SetWeanSize");
		makeProtocolApplication("SetWeanSizeMale");
		makeProtocolApplication("SetWeanSizeFemale");
		makeProtocolApplication("SetRemark");
		makeProtocolApplication("SetParentgroup");
		makeProtocolApplication("SetLitter");
		makeProtocolApplication("SetSource");
	}

	public void makeProtocolApplication(String protocolName) throws ParseException, DatabaseException, IOException
	{
		makeProtocolApplication(protocolName, protocolName);
	}

	public void makeProtocolApplication(String protocolName, String protocolLabel) throws ParseException,
			DatabaseException, IOException
	{
		ProtocolApplication app = ct.createProtocolApplication(invName, protocolName);
		protocolAppsToAddList.add(app);
		appMap.put(protocolLabel, app.getName());
	}

	public static final void copyInputStream(InputStream in, OutputStream out) throws IOException
	{
		byte[] buffer = new byte[1024];
		int len;

		while ((len = in.read(buffer)) >= 0)
			out.write(buffer, 0, len);

		in.close();
		out.close();
	}

}

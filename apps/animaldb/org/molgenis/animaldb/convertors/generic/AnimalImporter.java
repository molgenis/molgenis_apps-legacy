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
import java.util.Calendar;
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
	private int highestPGNr;
	private int highestLTNr;
	private String defaultSourceName;
	private String defaultBreedingLine;
	private String importName;

	// private List<String> lineNamesList;

	public AnimalImporter(Database db, Login login) throws Exception
	{
		userName = login.getUserName();
		this.db = db;
		ct = CommonService.getInstance();
		ct.setDatabase(this.db);
		ct.makeObservationTargetNameMap(userName, false);
		logger = Logger.getLogger("LoadUliDb");

		// If needed, make investigation
		invName = "System";

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
		activeMap = new HashMap<String, ObservedValue>();
		animalMap = new HashMap<String, String>();
		removalDateMap = new HashMap<String, Date>();

		// create panel wich will contain all the animals from this importbatch
		ProtocolApplication app = ct.createProtocolApplication(invName, "SetImportTimestamp");
		db.add(app);
		ProtocolApplication app2 = ct.createProtocolApplication(invName, "SetTypeOfGroup");
		db.add(app2);
		Date nowDate = new Date();
		String now = nowDate.toString();
		this.importName = "nonGMOImport_" + now;
		db.add(ct.preparePanel(invName, this.importName));
		db.add(ct.createObservedValue(invName, app2.getName(), nowDate, null, "TypeOfGroup", this.importName,
				"ImportTimestamp", null));

	}

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

	public void writeAnimalsToDb() throws Exception
	{

	}

	public void writeToDb() throws Exception
	{

		db.add(panelsToAddList);
		logger.debug("Panels successfully added");

		// Make entries in name prefix table with highest parentgroup nrs.
		List<NamePrefix> prefixList;
		prefixList = new ArrayList<NamePrefix>();

		for (String lineName : parentgroupNrMap.keySet())
		{
			// check if nameprefix already exists:
			prefixList = db.query(NamePrefix.class).eq(NamePrefix.TARGETTYPE, "parentgroup")
					.eq(NamePrefix.PREFIX, "PG_" + this.defaultBreedingLine + "_").find();
			if (prefixList.isEmpty())
			{
				NamePrefix namePrefix = new NamePrefix();
				namePrefix.setTargetType("parentgroup");
				namePrefix.setPrefix("PG_" + lineName + "_");
				namePrefix.setHighestNumber(parentgroupNrMap.get(lineName));
				db.add(namePrefix);
			}
			else
			{
				prefixList.get(0).setHighestNumber(this.highestPGNr + parentgroupNrMap.get(lineName));
				db.update(prefixList.get(0));
			}
		}
		// Make entries in name prefix table with highest litter nrs.
		for (String lineName : litterNrMap.keySet())
		{
			// check if nameprefix already exists:
			prefixList = db.query(NamePrefix.class).eq(NamePrefix.TARGETTYPE, "litter")
					.eq(NamePrefix.PREFIX, "LT_" + this.defaultBreedingLine + "_").find();
			if (prefixList.isEmpty())
			{
				NamePrefix namePrefix = new NamePrefix();
				namePrefix.setTargetType("litter");
				namePrefix.setPrefix("LT_" + lineName + "_");
				namePrefix.setHighestNumber(litterNrMap.get(lineName));
				prefixList.add(namePrefix);
				db.add(namePrefix);
			}
			else
			{
				prefixList.get(0).setHighestNumber(this.highestLTNr + litterNrMap.get(lineName));
				db.update(prefixList.get(0));
			}
		}
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
			this.defaultBreedingLine = tuple.getString("BreedingLine");
			this.defaultResponsibleResearcher = tuple.getString("ResponsibleResearcher");
			this.highestNr = ct.getHighestNumberForPrefix(this.defaultSpeciesNamePrefix) + 1;
			this.highestPGNr = ct.getHighestNumberForPrefix("PG_" + this.defaultBreedingLine + "_");
			this.highestLTNr = ct.getHighestNumberForPrefix("LT_" + this.defaultBreedingLine + "_");
		}
		reader.close();
	}

	public void populateAnimal(String filename) throws Exception
	{
		final Date now = new Date();

		File file = new File(filename);
		CsvFileReader reader = new CsvFileReader(file);

		// MolgenisRole user = db.find(MolgenisRole.class, new
		// QueryRule(MolgenisRole.NAME, Operator.EQUALS, "admin"))
		// .get(0);

		for (Tuple tuple : reader)
		{
			String animalName = this.defaultSpeciesNamePrefix + ct.prependZeros(Integer.toString(this.highestNr++), 6);
			animalNames.add(animalName);
			Individual newAnimal = ct.createIndividual(invName, animalName);
			animalsToAddList.add(newAnimal);

			// label as part of this import batch
			valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetImportTimestamp"), now, null,
					"ImportTimestamp", animalName, null, this.importName));

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
			if (background != null && background != "" && !background.equalsIgnoreCase("no background"))
			{
				backgroundName = background;
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetBackground"), now, null,
						"Background", animalName, null, backgroundName));
			}
			// convert BreedingLine -> Breedingline
			// FIXME add not null checks and conversion to default breeding line
			// from settigs if null?
			// String breedingline = tuple.getString("BreedingLine");
			valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetLine"), now, null, "Line", animalName,
					null, this.defaultBreedingLine));

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
			// convert Remarks -> Remarks
			String remarkString = tuple.getString("Remarks");
			if (remarkString != null)
			{
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetRemark"), now, null, "Remark",
						animalName, remarkString, null));
			}
			// import genetic modification
			String gm1String = tuple.getString("GeneModification1");
			String gs1String = tuple.getString("GeneState1");
			if (gm1String != null && gs1String != null)
			{
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetGenotype"), now, null,
						"GeneModification", animalName, gm1String, null));
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetGenotype"), now, null, "GeneState",
						animalName, gs1String, null));
			}
			// convert Remarks -> Remarks
			String chipNumberString = tuple.getString("ChipNumber");
			if (chipNumberString != null)
			{
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetChipNumber"), now, null,
						"ChipNumber", animalName, chipNumberString, null));
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
							"DeathDate", animalName, remDateString, null));
				}
				else
				{
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetRemovalDate"), remDate, null,
							"RemovalDate", animalName, remDateString, null));
				}
			}

			// rem cause -> Removal
			String removal = tuple.getString("RemovalCause");
			if (removal != null && remDate != null)
			{
				// onvert english input to dutch official vwa codes.
				if (removal.equalsIgnoreCase("dead"))
				{
					removal = "dood";
				}
				else if (removal.equalsIgnoreCase("alive: other rug"))
				{
					removal = "levend afgevoerd andere organisatorische eenheid RuG";
				}
				else if (removal.equalsIgnoreCase("alive: other registered nl"))
				{
					removal = "levend afgevoerd gereg. onderzoeksinstelling NL";
				}
				else if (removal.equalsIgnoreCase("alive: other registered eu"))
				{
					removal = "levend afgevoerd gereg. onderzoeksinstelling EU";
				}
				else if (removal.equalsIgnoreCase("alive: other"))
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

		try
		{
			db.add(animalsToAddList);
			db.add(valuesToAddList);
			valuesToAddList.clear();
			logger.debug("Animals successfully added");
			// update the prefix table with new highest nr.
			List<NamePrefix> prefixList = db.query(NamePrefix.class).eq(NamePrefix.TARGETTYPE, "animal")
					.eq(NamePrefix.PREFIX, this.defaultSpeciesNamePrefix).find();

			if (prefixList.size() == 1)
			{
				NamePrefix namePrefix = prefixList.get(0);
				namePrefix.setHighestNumber(this.highestNr);
				db.update(namePrefix);
				logger.debug("Nameprefix for animals successfully updated");
				System.out.println("------ > Nameprefix for animals successfully updated");
			}
			else
			{
				logger.error("Nameprefix not updated!!!!!!!! check why, this will cause trouble");
				System.out.println("------ > Nameprefix not updated!!!!!!!! check why, this will cause trouble");
			}

		}
		catch (Exception e)
		{
			logger.debug(e);
		}

	}

	public void parseParentRelations(String filename) throws Exception
	{
		System.out.println("############## Start parsing parent relations");
		animalsToAddList = new ArrayList<Individual>();
		File file = new File(filename);
		CsvFileReader reader = new CsvFileReader(file);
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

			// get the all the animals with this litterid and the correct
			// importTimestamp.
			Query<ObservedValue> TimeStampQuery = db.query(ObservedValue.class);
			TimeStampQuery.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "ImportTimestamp"));
			TimeStampQuery.addRules(new QueryRule(ObservedValue.RELATION_NAME, Operator.EQUALS, this.importName));
			List<ObservedValue> individualValueList = TimeStampQuery.find();
			List<String> importedAnimals = new ArrayList<String>();
			for (ObservedValue v : individualValueList)
			{
				importedAnimals.add(v.getTarget_Name());
			}
			Query<ObservedValue> OldLitterQuery = db.query(ObservedValue.class);
			OldLitterQuery.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "OldLitterId"));
			OldLitterQuery.addRules(new QueryRule(ObservedValue.VALUE, Operator.EQUALS, litter));
			OldLitterQuery.addRules(new QueryRule(ObservedValue.TARGET_NAME, Operator.IN, importedAnimals));
			individualValueList = OldLitterQuery.find();

			int FemaleCtr = 0;
			int MaleCtr = 0;
			int UnkSexCtr = 0;
			String weanDate = null;
			String dobDate = null;
			String lineName = null;
			String oldLitterId = null;
			// Get breedingline from mother (FIXME this assumes that the mother
			// has been imported already, is this really always true?)
			lineName = this.defaultBreedingLine;
			for (ObservedValue v : individualValueList)
			{
				if ((MaleCtr + FemaleCtr + UnkSexCtr) == 0)
				{
					String animal = v.getTarget_Name();
					// Get weanDate from first sibling
					weanDate = ct.getMostRecentValueAsString(animal, "WeanDate");
					// Get birthDate from first sibling
					dobDate = ct.getMostRecentValueAsString(animal, "DateOfBirth");
					// Get old litter id from first sibling
					oldLitterId = ct.getMostRecentValueAsString(animal, "OldLitterId");
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
				// set litter on individual siblings

			}
			int nrBorn = 0;
			if (tuple.getInt("NumberBorn") != null)
			{
				nrBorn = tuple.getInt("NumberBorn");
			}
			else
			{
				nrBorn = FemaleCtr + MaleCtr + UnkSexCtr;
			}
			// remarks
			String remark = tuple.getString("Remarks");

			// Create a parentgroup

			String pgActive = "Inactive";
			int parentgroupNr = 1;
			if (parentgroupNrMap.containsKey(lineName))
			{
				parentgroupNr = parentgroupNrMap.get(lineName) + 1;
			}
			parentgroupNrMap.put(lineName, parentgroupNr);

			String parentgroupNrPart = ct.prependZeros("" + (this.highestPGNr + parentgroupNr), 6);
			String parentgroupName = "PG_" + lineName + "_" + parentgroupNrPart;
			panelsToAddList.add(ct.preparePanel(invName, parentgroupName));
			valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetTypeOfGroup"), now, null, "TypeOfGroup",
					parentgroupName, "Parentgroup", null));
			// Link parents to parentgroup (if known)
			if (motherName != null)
			{
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetParentgroupMother"), now, null,
						"ParentgroupMother", parentgroupName, null, motherName));
			}
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
			if (startDate != null && startDate != "")
			{
				Date pairDate = inputFormat.parse(startDate);
				double deltaDays = (now.getTime() - pairDate.getTime()) / MILLSECS_PER_DAY;
				if (deltaDays < 60)
				{
					pgActive = "Active";
				}
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
			String litterNrPart = ct.prependZeros("" + (this.highestLTNr + litterNr), 6);
			String litterName = "LT_" + lineName + "_" + litterNrPart;
			panelsToAddList.add(ct.preparePanel(invName, litterName));
			valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetTypeOfGroup"), now, null, "TypeOfGroup",
					litterName, "Litter", null));

			// Set old litter nr also on new litter
			valuesToAddList.add(ct.createObservedValue(invName, appMap.get("OldLitterId"), now, null, "OldLitterId",
					litterName, oldLitterId, null));

			// we always need a birthdate, calculate one if it is missing
			if (dobDate == null || dobDate.equals(""))
			{
				if (weanDate != null && weanDate.equals(""))
				{
					long dobDateT = inputFormat.parse(weanDate).getTime() - (MILLSECS_PER_DAY * 28);
					Calendar tmpCal = Calendar.getInstance();
					tmpCal.setTimeInMillis(dobDateT);
					dobDate = inputFormat.format(tmpCal.getTime());
					remark = remark + "; birtdate unknown on import, calculated from weandate (weandate - 4wks)";
				}
				else
				{
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetDateOfBirth"), now, null,
							"DateOfBirth", litterName, "", null));
					remark = remark + "; Birth date unknown";
				}
			}
			else
			{
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetDateOfBirth"), now, null,
						"DateOfBirth", litterName, dobDate, null));
				// only set weandate if it is measured if it is not there it
				// means
				// that litter is not weaned yet.
			}
			if (weanDate != null && !weanDate.equals(""))
			{
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetWeanDate"), now, null, "WeanDate",
						litterName, weanDate, null));
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetWeanSize"), now, null, "WeanSize",
						litterName, Integer.toString(FemaleCtr + MaleCtr + UnkSexCtr), null));
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetWeanSizeMale"), now, null,
						"WeanSizeMale", litterName, Integer.toString(MaleCtr), null));
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetWeanSizeFemale"), now, null,
						"WeanSizeFemale", litterName, Integer.toString(FemaleCtr), null));
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetWeanSizeUnknown"), now, null,
						"WeanSizeUnknown", litterName, Integer.toString(UnkSexCtr), null));
				// set litter inactive
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetActive"), now, null, "Active",
						litterName, "Inactive", null));
			}
			else
			{
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetActive"), now, null, "Active",
						litterName, "Active", null));
			}
			valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetSize"), now, null, "Size", litterName,
					Integer.toString(nrBorn), null));

			// Set Remark
			if (remark != null && !remark.equals(""))
			{
				valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetRemark"), now, null, "Remark",
						litterName, remark, null));
			}
			// Set line also on litter

			valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetLine"), now, null, "Line", litterName,
					null, lineName));
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

				for (String animalName : litterMap.get(litter))
				{
					// Link animal to litter
					// valuesToAddList.add(ct.createObservedValue(invName,
					// appMap.get("SetLitter"), now, null, "Litter",
					// animalName, null, litterName));
					// Set parents also on animal, using the Mother and
					// Father measurements
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetMother"), now, null, "Mother",
							animalName, null, motherName));
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetFather"), now, null, "Father",
							animalName, null, fatherName));
					valuesToAddList.add(ct.createObservedValue(invName, appMap.get("SetLitter"), now, null, "Litter",
							animalName, null, litterName));
					// Set birth date, line also on animal
					// Get Active value from map; every animal has one

					ObservedValue activeValue = activeMap.get(animalName);
					activeMap.remove(animalName);
					valuesToAddList.add(activeValue);

					// dob is set from animals, not from litters
					// valuesToAddList.add(ct.createObservedValue(invName,
					// appMap.get("SetDateOfBirth"), now, null,
					// "DateOfBirth", animalName, dob, null));

					// valuesToAddList.add(ct.createObservedValue(invName,
					// appMap.get("SetWeandDate"), now, null,
					// "WeanDate", animalName, weanDate, null));

					// valuesToAddList.add(ct.createObservedValue(invName,
					// appMap.get("SetLine"), now, null, "Line",
					// animalName, null, lineName));
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

		makeProtocolApplication("SetImportTimestamp");
		makeProtocolApplication("SetActive");
		makeProtocolApplication("SetRemoval");
		makeProtocolApplication("SetDateOfBirth");
		makeProtocolApplication("SetEarmark");
		makeProtocolApplication("SetResponsibleResearcher");
		makeProtocolApplication("SetGenotype");

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
		db.add(app);
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

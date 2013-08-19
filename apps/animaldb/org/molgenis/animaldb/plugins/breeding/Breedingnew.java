/* Date:        November 15, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.animaldb.plugins.breeding;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.ListUtils;
import org.molgenis.animaldb.commonservice.CommonService;
import org.molgenis.animaldb.plugins.administration.LabelGenerator;
import org.molgenis.animaldb.plugins.administration.LabelGeneratorException;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.server.MolgenisRequest;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.html.DateInput;
import org.molgenis.framework.ui.html.HtmlInput;
import org.molgenis.framework.ui.html.JQueryDataTable;
import org.molgenis.framework.ui.html.SelectInput;
import org.molgenis.framework.ui.html.StringInput;
import org.molgenis.framework.ui.html.Table;
import org.molgenis.matrix.component.MatrixViewer;
import org.molgenis.matrix.component.SliceablePhenoMatrix;
import org.molgenis.matrix.component.general.MatrixQueryRule;
import org.molgenis.pheno.Category;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.Location;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationElement;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.pheno.Panel;
import org.molgenis.protocol.ProtocolApplication;
import org.molgenis.util.Entity;

public class Breedingnew extends PluginModel<Entity>
{
	private static final long serialVersionUID = 203412348106990472L;
	private CommonService ct = CommonService.getInstance();
	// private SimpleDateFormat dateOnlyFormat = new
	// SimpleDateFormat("MMMM d, yyyy", Locale.US);
	private SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
	private String startdate = null;
	private List<ObservationTarget> lineList;
	private String line = null;
	private String litterRemarks = null;
	// private String pgStatus = null;
	MatrixViewer motherMatrixViewer = null;
	MatrixViewer fatherMatrixViewer = null;
	MatrixViewer pgMatrixViewer = null;
	MatrixViewer litterMatrixViewer = null;
	private static String MOTHERMATRIX = "mothermatrix";
	private static String FATHERMATRIX = "fathermatrix";
	private static String PGMATRIX = "pgmatrix";
	private static String LITTERMATRIX = "littermatrix";
	private String action = "init";
	private String userName = null;
	private String motherMatrixViewerString;
	private String fatherMatrixViewerString;
	private String pgMatrixViewerString;
	private String litterMatrixViewerString;
	private String entity = "Parentgroups";
	private boolean addNew = false;
	private String birthdate = null;
	private SimpleDateFormat newDateOnlyFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
	private String remarks = null;
	private String selectedParentgroup = null;
	private int litterSize;
	private boolean litterSizeApproximate;
	private String locName = null;
	private String motherLocation = null;
	private String respres = null;
	private int weanSizeFemale;
	private int weanSizeMale;
	private int weanSizeUnknown;
	private String weandate = null;
	private String nameBase = null;
	private int startNumber = -1;
	private String litter;
	private int weansize = 0;
	private String weanparentgroup = null;
	private String prefix = "";
	private List<String> bases;
	private List<ObservationTarget> backgroundList;
	private List<ObservationTarget> sexList;
	private List<String> geneNameList;
	private List<String> geneStateList;
	private List<String> colorList;
	private List<Category> earmarkList;
	private List<Location> locationList;
	private int nrOfGenotypes = 1;
	private Table genotypeTable = null;
	private Table editTable = null;
	private boolean wean = false;
	private String parentInfo = "";
	private String labelDownloadLink;
	Integer numberOfPG = -1;
	List<String> pgName = new ArrayList<String>();
	String sex = "not selected";
	boolean stillToWeanYN = true;
	boolean stillToGenotypeYN = true;
	// HashMap<String,List<String>> hashMothers = new
	// HashMap<String,List<String>>();
	Map<Integer, List<String>> hashMothers = new LinkedHashMap<Integer, List<String>>();
	Map<Integer, List<String>> hashFathers = new LinkedHashMap<Integer, List<String>>();

	public Breedingnew(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	public String getCustomHtmlHeaders()
	{
		return "<script src=\"res/jquery-plugins/ctnotify/lib/jquery.ctNotify.js\" language=\"javascript\"></script>\n"
				+ "<script src=\"res/scripts/custom/addingajax.js\" language=\"javascript\"></script>\n"
				+ "<script src=\"res/scripts/custom/litters.js\" language=\"javascript\"></script>\n"
				+ "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/jquery-plugins/ctnotify/lib/jquery.ctNotify.css\">";
		// "<script type=\"text/javascript\" src=\"res/jquery-plugins/datatables/js/jquery.dataTables.js\"></script>\n"
		// /+
		// "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/jquery-plugins/datatables/css/demo_table_jui.css\">\n"
	}

	public String getAnimalName(Integer id)
	{
		try
		{
			return ct.getObservationTargetLabel(id);
		}
		catch (Exception e)
		{
			return id.toString();
		}
	}

	public String getStartdate()
	{
		return startdate;
	}

	public void setStartdate(String startdate)
	{
		this.startdate = startdate;
	}

	public void setLine(String line)
	{
		this.line = line;
	}

	public String getLine()
	{
		return line;
	}

	public List<ObservationTarget> getLineList()
	{
		return lineList;
	}

	public void setLineList(List<ObservationTarget> lineList)
	{
		this.lineList = lineList;
	}

	public String getRemarks()
	{
		return remarks;
	}

	public void setRemarks(String remarks)
	{
		this.remarks = remarks;
	}

	public String getAction()
	{
		return action;
	}

	public void setAction(String action)
	{
		this.action = action;
	}

	public String getEntity()
	{
		return entity;
	}

	public void setEntity(String entity)
	{
		this.entity = entity;
	}

	public String getMotherMatrixViewer()
	{
		if (motherMatrixViewerString != null)
		{
			return motherMatrixViewerString;
		}
		return "Mother matrix not loaded";
	}

	public void loadMotherMatrixViewer(Database db)
	{
		try
		{
			List<String> investigationNames = ct.getAllUserInvestigationNames(this.getLogin().getUserName());

			List<String> measurementsToShow = new ArrayList<String>();
			measurementsToShow.add("Sex");
			measurementsToShow.add("Active");
			measurementsToShow.add("Line");
			measurementsToShow.add("Background");
			measurementsToShow.add("GeneModification");
			measurementsToShow.add("GeneState");
			measurementsToShow.add("Species");
			// Mother matrix viewer
			List<MatrixQueryRule> motherFilterRules = new ArrayList<MatrixQueryRule>();
			motherFilterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.rowHeader, Individual.INVESTIGATION_NAME,
					Operator.IN, investigationNames));

			// MOTHER IS DISABLED
			/*
			 * motherFilterRules.add(new
			 * MatrixQueryRule(MatrixQueryRule.Type.colValueProperty,
			 * ct.getMeasurementId("Sex"), ObservedValue.RELATION_NAME,
			 * Operator.EQUALS, "Female"));
			 */
			motherFilterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.colValueProperty, ct
					.getMeasurementId("Active"), ObservedValue.VALUE, Operator.EQUALS, "Alive"));
			if (line != null)
			{
				motherFilterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.colValueProperty, ct
						.getMeasurementId("Line"), ObservedValue.RELATION_NAME, Operator.EQUALS, line));
				// Setting filter on the RELATION field with value = line would
				// be more efficient,
				// but gives a very un-userfriendly toString value when shown in
				// the MatrixViewer UI
				String speciesName = ct.getMostRecentValueAsXrefName(line, "Species");
				motherFilterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.colValueProperty, ct
						.getMeasurementId("Species"), ObservedValue.RELATION_NAME, Operator.EQUALS, speciesName));
			}

			SliceablePhenoMatrix<Individual, Measurement> SPMM = new SliceablePhenoMatrix<Individual, Measurement>(
					Individual.class, Measurement.class);
			SPMM.setDatabase(db);
			motherMatrixViewer = new MatrixViewer(this, MOTHERMATRIX, SPMM, true, 2, false, false, motherFilterRules,
					new MatrixQueryRule(MatrixQueryRule.Type.colHeader, Measurement.NAME, Operator.IN,
							measurementsToShow));
			motherMatrixViewer.setShowQuickView(true);
			motherMatrixViewer.setShowfilterSaveOptions(true);
		}
		catch (Exception e)
		{
			String message = "Something went wrong while loading mother matrix viewer";
			if (e.getMessage() != null)
			{
				message += (": " + e.getMessage());
			}
			this.getMessages().add(new ScreenMessage(message, false));
			e.printStackTrace();
		}
	}

	public String getFatherMatrixViewer()
	{
		if (fatherMatrixViewerString != null)
		{
			return fatherMatrixViewerString;
		}
		return "Father matrix not loaded";
	}

	public void loadFatherMatrixViewer(Database db)
	{
		try
		{
			List<String> investigationNames = ct.getAllUserInvestigationNames(this.getLogin().getUserName());

			List<String> measurementsToShow = new ArrayList<String>();
			measurementsToShow.add("Sex");
			measurementsToShow.add("Active");
			measurementsToShow.add("Line");
			measurementsToShow.add("Background");
			measurementsToShow.add("GeneModification");
			measurementsToShow.add("GeneState");
			measurementsToShow.add("Species");
			// Father matrix viewer
			List<MatrixQueryRule> fatherFilterRules = new ArrayList<MatrixQueryRule>();
			fatherFilterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.rowHeader, Individual.INVESTIGATION_NAME,
					Operator.IN, investigationNames));
			fatherFilterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.colValueProperty,
					ct.getMeasurementId("Sex"), ObservedValue.RELATION_NAME, Operator.EQUALS, "Male"));
			fatherFilterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.colValueProperty, ct
					.getMeasurementId("Active"), ObservedValue.VALUE, Operator.EQUALS, "Alive"));
			if (line != null)
			{
				fatherFilterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.colValueProperty, ct
						.getMeasurementId("Line"), ObservedValue.RELATION_NAME, Operator.EQUALS, line));
				// Setting filter on the RELATION field with value = line would
				// be more efficient,
				// but gives a very un-userfriendly toString value when shown in
				// the MatrixViewer UI
				String speciesName = ct.getMostRecentValueAsXrefName(line, "Species");
				fatherFilterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.colValueProperty, ct
						.getMeasurementId("Species"), ObservedValue.RELATION_NAME, Operator.EQUALS, speciesName));
			}
			SliceablePhenoMatrix<Individual, Measurement> SPMF = new SliceablePhenoMatrix<Individual, Measurement>(
					Individual.class, Measurement.class);
			SPMF.setDatabase(db);
			fatherMatrixViewer = new MatrixViewer(this, FATHERMATRIX, SPMF, true, 2, false, false, fatherFilterRules,
					new MatrixQueryRule(MatrixQueryRule.Type.colHeader, Measurement.NAME, Operator.IN,
							measurementsToShow));
			fatherMatrixViewer.setShowQuickView(true);
			fatherMatrixViewer.setShowfilterSaveOptions(true);

		}
		catch (Exception e)
		{
			String message = "Something went wrong while loading father matrix viewer";
			if (e.getMessage() != null)
			{
				message += (": " + e.getMessage());
			}
			this.getMessages().add(new ScreenMessage(message, false));
			e.printStackTrace();
		}
	}

	public String getPgMatrixViewer()
	{
		return pgMatrixViewerString;
	}

	public void loadPgMatrixViewer(Database db)
	{
		try
		{
			List<String> investigationNames = ct.getAllUserInvestigationNames(this.getLogin().getUserName());

			List<String> measurementsToShow = new ArrayList<String>();
			measurementsToShow.add("Active");
			measurementsToShow.add("StartDate");
			measurementsToShow.add("Remark");
			measurementsToShow.add("Line");
			measurementsToShow.add("ParentgroupMother");
			measurementsToShow.add("ParentgroupFather");
			List<MatrixQueryRule> filterRules = new ArrayList<MatrixQueryRule>();
			filterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.rowHeader, Panel.INVESTIGATION_NAME, Operator.IN,
					investigationNames));
			filterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.colValueProperty, ct
					.getMeasurementId("TypeOfGroup"), ObservedValue.VALUE, Operator.EQUALS, "Parentgroup"));
			filterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.colValueProperty, ct.getMeasurementId("Active"),
					ObservedValue.VALUE, Operator.EQUALS, "Active"));
			filterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.colValueProperty, ct.getMeasurementId("Line"),
					ObservedValue.RELATION_NAME, Operator.EQUALS, this.line));
			pgMatrixViewer = new MatrixViewer(this, PGMATRIX, new SliceablePhenoMatrix<Panel, Measurement>(Panel.class,
					Measurement.class), true, 1, false, false, filterRules, new MatrixQueryRule(
					MatrixQueryRule.Type.colHeader, Measurement.NAME, Operator.IN, measurementsToShow));
			pgMatrixViewer.setShowTargetTooltip(true);

		}
		catch (Exception e)
		{
			String message = "Something went wrong while loading parentgroup matrix";
			if (e.getMessage() != null)
			{
				message += (": " + e.getMessage());
			}
			this.getMessages().add(new ScreenMessage(message, false));
			e.printStackTrace();
		}
	}

	public String getLitterMatrixViewer()
	{
		return litterMatrixViewerString;
	}

	private ArrayList<String> litterProtocol()
	{
		ArrayList<String> listOfMeasurements = new ArrayList<String>();
		listOfMeasurements.add("Active");
		listOfMeasurements.add("DateOfBirth");
		listOfMeasurements.add("GenotypeDate");
		listOfMeasurements.add("Line");
		listOfMeasurements.add("Parentgroup");
		listOfMeasurements.add("Remark");
		listOfMeasurements.add("Size");
		listOfMeasurements.add("WeanDate");
		listOfMeasurements.add("WeanSize");

		return listOfMeasurements;
	}

	public void loadLitterMatrixViewer(Database db)
	{
		try
		{
			List<String> investigationNames = ct.getAllUserInvestigationNames(this.getLogin().getUserName());

			List<String> measurementsToShow = new ArrayList<String>();
			for (String e : litterProtocol())
			{
				measurementsToShow.add(e);
			}
			List<MatrixQueryRule> filterRules = new ArrayList<MatrixQueryRule>();
			filterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.rowHeader, Panel.INVESTIGATION_NAME, Operator.IN,
					investigationNames));
			filterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.colValueProperty, ct
					.getMeasurementId("TypeOfGroup"), ObservedValue.VALUE, Operator.EQUALS, "Litter"));
			filterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.colValueProperty, ct.getMeasurementId("Active"),
					ObservedValue.VALUE, Operator.EQUALS, "Active"));
			filterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.colValueProperty, ct.getMeasurementId("Line"),
					ObservedValue.RELATION_NAME, Operator.EQUALS, this.line));
			litterMatrixViewer = new MatrixViewer(this, LITTERMATRIX, new SliceablePhenoMatrix<Panel, Measurement>(
					Panel.class, Measurement.class), true, 1, false, false, filterRules, new MatrixQueryRule(
					MatrixQueryRule.Type.colHeader, Measurement.NAME, Operator.IN, measurementsToShow));
			litterMatrixViewer.setShowTargetTooltip(true);
		}
		catch (Exception e)
		{
			String message = "Something went wrong while loading litter matrix";
			if (e.getMessage() != null)
			{
				message += (": " + e.getMessage());
			}
			this.getMessages().add(new ScreenMessage(message, false));
			e.printStackTrace();
		}
	}

	@Override
	public String getViewName()
	{
		return "org_molgenis_animaldb_plugins_breeding_Breedingnew";
	}

	@Override
	public String getViewTemplate()
	{
		return "org/molgenis/animaldb/plugins/breeding/Breedingnew.ftl";
	}

	private void AddParents(Database db, List<String> parentNameList, String protocolName, String featureName,
			String parentgroupName, Date eventDate) throws DatabaseException, ParseException, IOException
	{

		String invName = ct.getOwnUserInvestigationNames(this.getLogin().getUserName()).get(0);

		// Init lists that we can later add to the DB at once
		List<ObservedValue> valuesToAddList = new ArrayList<ObservedValue>();
		boolean updateDb = true;
		for (String parentName : parentNameList)
		{
			if (parentName != null && !parentName.equals(""))
			{
				// Find the 'SetParentgroupMother'/'SetParentgroupFather' event
				// type
				// TODO: SetParentgroupMother/SetParentgroupFather are now plain
				// event types with only the ParentgroupMother/ParentgroupFather
				// feature
				// and no longer the Certain feature. Solve this!
				// Make the event
				ProtocolApplication app = ct.createProtocolApplication(invName, protocolName);
				db.add(app);
				// Make 'ParentgroupMother'/'ParentgroupFather' feature-value
				// pair and link to event
				valuesToAddList.add(ct.createObservedValue(invName, app.getName(), eventDate, null, featureName,
						parentgroupName, null, parentName));
				// Make 'Certain' feature-value pair and link to event
				String valueString;
				if (parentNameList.size() == 1)
				{
					valueString = "1"; // if there's only one parent of this
										// gender, it's certain
				}
				else
				{
					valueString = "0"; // ... otherwise, not
				}
				valuesToAddList.add(ct.createObservedValue(invName, app.getName(), eventDate, null, "Certain",
						parentName, valueString, null));
			}
			else
			{
				updateDb = false;
			}
		}
		// Add everything to DB
		if (updateDb)
		{
			db.add(valuesToAddList);
		}
	}

	@Override
	public void handleRequest(Database db, MolgenisRequest request)
	{
		ct.setDatabase(db);
		action = request.getString("__action");
		try
		{
			if (motherMatrixViewer != null && action.startsWith(motherMatrixViewer.getName()))
			{
				motherMatrixViewer.setDatabase(db);
				motherMatrixViewer.handleRequest(db, request);
				motherMatrixViewerString = motherMatrixViewer.render();
				this.action = "selectParents"; // return to mother selection
												// screen
				return;
			}

			if (fatherMatrixViewer != null && action.startsWith(fatherMatrixViewer.getName()))
			{
				fatherMatrixViewer.setDatabase(db);
				fatherMatrixViewer.handleRequest(db, request);
				fatherMatrixViewerString = fatherMatrixViewer.render();
				this.action = "addParentgroupScreen3"; // return to father
														// selection screen
				// this.action = "selectParents";
				return;
			}

			if (pgMatrixViewer != null && action.startsWith(pgMatrixViewer.getName()))
			{
				pgMatrixViewer.setDatabase(db);
				pgMatrixViewer.handleRequest(db, request);
				pgMatrixViewerString = pgMatrixViewer.render();
				this.action = "init"; // return to start screen
				this.entity = "Parentgroups";
				return;
			}

			if (litterMatrixViewer != null && action.startsWith(litterMatrixViewer.getName()))
			{
				litterMatrixViewer.setDatabase(db);
				litterMatrixViewer.handleRequest(db, request);
				litterMatrixViewerString = litterMatrixViewer.render();
				this.action = "init"; // return to start screen
				this.entity = "Litters";
				return;
			}

			if (action.equals("init"))
			{
				// do nothing here
			}

			if (action.equals("selectParents"))
			{
				hashFathers.clear();
				hashMothers.clear();

				List<String> lis = new ArrayList<String>();
				lis.add("");
				numberOfPG = request.getInt("numberPG");
				if (numberOfPG == null)
				{
					numberOfPG = -1;
				}
				for (int i = 0; i < numberOfPG; i++)
				{
					hashMothers.put(i, lis);
					hashFathers.put(i, lis);
					pgName.add(i + "");

				}
			}
			if (pgName.size() != 0)
			{
				for (int i = 0; i < pgName.size(); i++)
				{
					if (action.equals("selectParentsM" + (i)))
					{
						List<String> selectedFatherNameList = new ArrayList<String>();
						@SuppressWarnings("unchecked")
						List<ObservationElement> rows = (List<ObservationElement>) motherMatrixViewer.getSelection(db);
						int rowCnt = 0;
						sex = "not selected";
						for (ObservationElement row : rows)
						{
							if (request.getBoolean(MOTHERMATRIX + "_selected_" + rowCnt) != null)
							{
								String fatherName = row.getName();
								sex = ct.getMostRecentValueAsXrefName(fatherName, "Sex");
								if (sex.equals("Male"))
								{
									if (!selectedFatherNameList.contains(fatherName))
									{
										selectedFatherNameList.add(fatherName);
									}
								}
								else
								{
									this.setMessages(new ScreenMessage("Not a Male", false));
								}
							}
							rowCnt++;
						}
						hashFathers.put(i, selectedFatherNameList);
					}

					if (action.equals("selectParentsF" + (i)))
					{
						List<String> selectedMotherNameList = new ArrayList<String>();
						@SuppressWarnings("unchecked")
						List<ObservationElement> rows = (List<ObservationElement>) motherMatrixViewer.getSelection(db);
						int rowCnt = 0;
						sex = "not selected";
						for (ObservationElement row : rows)
						{

							if (request.getBoolean(MOTHERMATRIX + "_selected_" + rowCnt) != null)
							{
								String motherName = row.getName();
								sex = ct.getMostRecentValueAsXrefName(motherName, "Sex");
								if (sex.equals("Female"))
								{
									if (!selectedMotherNameList.contains(motherName))
									{
										selectedMotherNameList.add(motherName);
									}
								}
								else
								{
									this.setMessages(new ScreenMessage("Not a Female", false));
								}
							}
							rowCnt++;
						}
						hashMothers.put(i, selectedMotherNameList);

					}
				}
			}
			try
			{
				if (action.equals("init"))
				{
					hashFathers.clear();
					hashMothers.clear();
					numberOfPG = null;
				}

				if (action.equals("JensonButton"))
				{
					boolean papa = false;
					boolean mama = false;
					List<String> pgNames = new ArrayList<String>();

					// check for empty mother and father fields.
					for (Entry<Integer, List<String>> entry : hashFathers.entrySet())
					{

						for (String s : entry.getValue())
						{
							if (s.isEmpty())
							{
								papa = true;
							}
						}
					}
					for (Entry<Integer, List<String>> entry : hashMothers.entrySet())
					{
						for (String s : entry.getValue())
						{
							if (s.isEmpty())
							{
								mama = true;
							}
						}
					}
					// disable the check on empty partents for now to allow PG's
					// without known parents
					if (papa == true && mama == true)
					{
						this.setMessages(new ScreenMessage("fill in at least one parent for each parentgroup.", false));
						action = "selectParents";

					}
					else
					{
						for (Entry<Integer, List<String>> entry : hashFathers.entrySet())
						{

							String x = "";
							String y = "";
							if (request.getDate("startdate" + entry.getKey()) != null)
							{
								x = request.getString("startdate" + entry.getKey());
							}
							if (request.getString("remarks" + entry.getKey()) != null)
							{
								y = request.getString("remarks" + entry.getKey());
							}

							pgNames.add(AddParentgroup2(db, request, entry.getValue(), hashMothers.get(entry.getKey()),
									x, y));

							// Reset matrix and add filter on name of newly
							// added PG:
							loadPgMatrixViewer(db);
							pgMatrixViewer.setDatabase(db);
							// pgMatrixViewer.getMatrix().getRules().add(new
							// MatrixQueryRule(MatrixQueryRule.Type.rowHeader,
							// Individual.NAME, Operator.EQUALS, pgNames));
							pgMatrixViewer.reloadMatrix(db, null);
							pgMatrixViewerString = pgMatrixViewer.render();
							// Reset other fields
							this.action = "init";
							this.startdate = newDateOnlyFormat.format(new Date());
							this.remarks = null;
							motherMatrixViewer = null;
							this.setSuccess("Parentgroup(s) " + pgNames + " successfully created. ");
						}
						hashFathers.clear();
						hashMothers.clear();
						numberOfPG = null;

					}

				}
			}
			catch (Exception e)
			{
				// this.setMessages(new
				// ScreenMessage("Not all fathers or mothers are filled in",
				// false));
			}

			if (action.equals("createParentgroup"))
			{
				numberOfPG = -1;
				loadMotherMatrixViewer(db);
				motherMatrixViewer.setDatabase(db);
				motherMatrixViewerString = motherMatrixViewer.render();
			}

			if (action.equals("changeLine"))
			{
				this.line = request.getString("breedingLine");
				this.setSuccess("Breeding line changed to " + this.line);
				// Reset matrices and return to main screen
				loadPgMatrixViewer(db);
				pgMatrixViewer.setDatabase(db);
				pgMatrixViewerString = pgMatrixViewer.render();
				loadLitterMatrixViewer(db);
				litterMatrixViewer.setDatabase(db);
				litterMatrixViewerString = litterMatrixViewer.render();
				this.action = "init";
			}

			if (action.equals("switchParentgroups"))
			{
				this.entity = "Parentgroups";
				this.action = "init";
			}

			if (action.equals("switchLitters"))
			{
				this.entity = "Litters";
				this.action = "init";
			}

			if (action.equals("deActivate"))
			{
				List<?> rows = pgMatrixViewer.getSelection(db);
				String pgName;
				try
				{
					int row = request.getInt(PGMATRIX + "_selected");
					pgName = ((ObservationElement) rows.get(row)).getName();
				}
				catch (Exception e)
				{
					this.action = "init";
					throw new Exception("No parentgroup selected");
				}
				ObservedValue activeVal = db.query(ObservedValue.class).eq(ObservedValue.TARGET_NAME, pgName)
						.eq(ObservedValue.FEATURE_NAME, "Active").find().get(0);
				if (activeVal.getValue().equals("Active"))
				{
					activeVal.setValue("Inactive");
				}
				else
				{
					activeVal.setValue("Active");
				}
				db.update(activeVal);
				// Reset matrix and return to main screen
				loadPgMatrixViewer(db);
				pgMatrixViewer.setDatabase(db);
				pgMatrixViewerString = pgMatrixViewer.render();
				this.action = "init";
				this.entity = "Parentgroups";
			}

			if (action.equals("deActivateLitter"))
			{
				List<?> rows = litterMatrixViewer.getSelection(db);
				String litterName;
				try
				{
					int row = request.getInt(LITTERMATRIX + "_selected");
					litterName = ((ObservationElement) rows.get(row)).getName();
				}
				catch (Exception e)
				{
					this.action = "init";
					throw new Exception("No litter selected");
				}
				ObservedValue activeVal = db.query(ObservedValue.class).eq(ObservedValue.TARGET_NAME, litterName)
						.eq(ObservedValue.FEATURE_NAME, "Active").find().get(0);
				if (activeVal.getValue().equals("Active"))
				{
					activeVal.setValue("Inactive");
				}
				else
				{
					activeVal.setValue("Active");
				}
				db.update(activeVal);
				// Reset matrix and return to main screen
				loadLitterMatrixViewer(db);
				litterMatrixViewer.setDatabase(db);
				litterMatrixViewerString = litterMatrixViewer.render();
				this.action = "init";
				this.entity = "Litters";
			}

			if (action.equals("createLitter"))
			{
				this.entity = "Litters"; // switch to litter view
				// Get selected parentgroup from PARENTGROUP matrix
				List<?> rows = pgMatrixViewer.getSelection(db);
				try
				{
					int row = request.getInt(PGMATRIX + "_selected");
					this.selectedParentgroup = ((ObservationElement) rows.get(row)).getName();
				}
				catch (Exception e)
				{
					this.action = "init";
					throw new Exception("No parentgroup selected");
				}
			}

			if (action.equals("addLitter"))
			{
				String newLitterName = ApplyAddLitter(db, request);
				// Reload litter matrix and set filter on name of newly added
				// litter
				loadLitterMatrixViewer(db);
				litterMatrixViewer.setDatabase(db);
				litterMatrixViewer.getMatrix();

				// Do not add filter
				// .getRules()
				// .add(new MatrixQueryRule(MatrixQueryRule.Type.rowHeader,
				// Individual.NAME, Operator.EQUALS,
				// newLitterName));
				litterMatrixViewer.reloadMatrix(db, null);
				litterMatrixViewerString = litterMatrixViewer.render();
				// Reset other fields
				this.birthdate = null;
				this.litterSize = 0;
				this.litterRemarks = null;
				// Return to start screen for litters
				this.action = "init";
				this.entity = "Litters";
				// this.setSuccess("Litter " + newLitterName +
				// " successfully added; adding filter to matrix: name = "
				// + newLitterName);
				this.setSuccess("Litter " + newLitterName + " successfully created. ");
			}

			if (action.equals("WeanLitter"))
			{
				stillToWeanYN = true;
				// Get selected litter
				List<?> rows = litterMatrixViewer.getSelection(db);

				try
				{
					int row = request.getInt(LITTERMATRIX + "_selected");
					this.litter = ((ObservationElement) rows.get(row)).getName();
					this.birthdate = ct.getMostRecentValueAsString(this.litter, "DateOfBirth");
				}
				catch (Exception e)
				{
					this.action = "init";
					this.entity = "Litters";
					throw new Exception("No litter selected");
				}
				if (isWeaned(db))
				{
					stillToWeanYN = false;
					this.setMessages(new ScreenMessage("Already weaned", false));
					action = "addLitter";
				}
				else
				{
					weanLitter(db);
				}

			}

			if (action.equals("EditLitter"))
			{
				List<?> rows = litterMatrixViewer.getSelection(db);
				try
				{
					if (request.getInt(LITTERMATRIX + "_selected") != null)
					{
						int row = request.getInt(LITTERMATRIX + "_selected");

						this.litter = ((ObservationElement) rows.get(row)).getName();
					}

				}
				catch (Exception e)
				{
					this.action = "init";
					this.entity = "Litters";
					throw new Exception("No litter selected");
				}

				editLitter(db);

			}

			if (action.equals("editIndividual"))
			{
				editIndividuals(db, this.litter, request);
			}

			if (action.equals("GenotypeLitter"))
			{
				// Prepare parent info
				stillToGenotypeYN = true;
				List<?> rows = litterMatrixViewer.getSelection(db);
				try
				{
					int row = request.getInt(LITTERMATRIX + "_selected");
					this.litter = ((ObservationElement) rows.get(row)).getName();
				}
				catch (Exception e)
				{
					this.action = "init";
					this.entity = "Litters";
					throw new Exception("No litter selected");
				}

				if (!isWeaned(db))
				{
					this.setMessages(new ScreenMessage("The litter is not weaned yet!", false));
					action = "addLitter";
				}
				else
				{
					if (isGenotyped(db))
					{
						stillToGenotypeYN = false;
						this.setMessages(new ScreenMessage("Already genotyped", false));
						action = "addLitter";
					}
					else
					{
						String parentgroupName = ct.getMostRecentValueAsXrefName(this.litter, "Parentgroup");
						parentInfo = "";
						parentInfo += ("Parentgroup: " + parentgroupName + "<br />");
						parentInfo += ("Line: " + getLineInfo(parentgroupName) + "<br />");
						String motherName = findParentForParentgroup(parentgroupName, "Mother", db);
						parentInfo += ("Mother: " + getGenoInfo(motherName, db) + "<br />");
						String fatherName = findParentForParentgroup(parentgroupName, "Father", db);
						parentInfo += ("Father: " + getGenoInfo(fatherName, db) + "<br />");
						genotypeLitter(db);
					}
				}

			}

			if (action.equals("applyWean"))
			{
				int weanSize = Wean(db, request);
				// Update custom label map now new animals have been added
				ct.makeObservationTargetNameMap(this.getLogin().getUserName(), true);
				// Reset all values
				this.wean = false;
				this.weandate = null;
				this.weanSizeFemale = 0;
				this.weanSizeMale = 0;
				this.weanSizeUnknown = 0;
				this.remarks = null;
				this.respres = null;
				this.selectedParentgroup = null;
				this.locName = null;
				// Reload litter matrix and set filter on name of newly weaned
				// litter
				loadLitterMatrixViewer(db);
				litterMatrixViewer.setDatabase(db);
				litterMatrixViewer
						.getMatrix()
						.getRules()
						.add(new MatrixQueryRule(MatrixQueryRule.Type.rowHeader, Individual.NAME, Operator.EQUALS,
								this.litter));
				litterMatrixViewer.reloadMatrix(db, null);
				litterMatrixViewerString = litterMatrixViewer.render();
				this.action = "init";
				this.entity = "Litters";
				this.setSuccess("All " + weanSize + " animals successfully weaned; adding filter to matrix: name = "
						+ this.litter);
			}
			if (action.equals("applyEdit"))
			{
				editLitterToDb(db, request);
			}

			if (action.equals("applyGenotype"))
			{
				int animalCount = Genotype(db, request);
				// Reload litter matrix and set filter on name of newly weaned
				// litter
				loadLitterMatrixViewer(db);
				litterMatrixViewer.setDatabase(db);
				litterMatrixViewer
						.getMatrix()
						.getRules()
						.add(new MatrixQueryRule(MatrixQueryRule.Type.rowHeader, Individual.NAME, Operator.EQUALS,
								this.litter));
				litterMatrixViewer.reloadMatrix(db, null);
				litterMatrixViewerString = litterMatrixViewer.render();
				// Reset other fields
				this.action = "init";
				this.entity = "Litters";
				this.setSuccess("All " + animalCount
						+ " animals successfully genotyped; adding filter to matrix: name = " + this.litter);
			}

			if (action.equals("applyLitterIndividuals"))
			{
				String invName = ct.getObservationTargetByName(this.litter).getInvestigation_Name();

				List<String> investigationNames = ct.getAllUserInvestigationNames(this.getLogin().getUserName());

				updateLitterIndividuals(db, request, investigationNames, invName);

				editIndividuals(db, this.litter, request);

				this.action = "editIndividual";

				// Reload litter matrix and set filter on name of newly weaned
				// litter

				this.setSuccess("Animals were successfully updated; for litter: " + this.litter);
			}

			if (action.equals("makeLabels"))
			{
				// Get selected litter
				List<?> rows = litterMatrixViewer.getSelection(db);
				try
				{
					int row = request.getInt(LITTERMATRIX + "_selected");
					this.litter = ((ObservationElement) rows.get(row)).getName();
				}
				catch (Exception e)
				{
					this.action = "init";
					this.entity = "Litters";
					throw new Exception("No litter selected");
				}
				if (ct.getMostRecentValueAsString(this.litter, "WeanDate") == null)
				{
					this.action = "init";
					this.entity = "Litters";
					throw new Exception("Cannot make labels for an unweaned litter");
					// disable temporary cage label for now, because it is not
					// part of the flow anymore
					// } else if (ct.getMostRecentValueAsString(this.litter,
					// "GenotypeDate") == null) {
					// makeTempCageLabels(db);
				}
				else
				{
					createDefCageLabels(db);
				}
			}

			if (action.equals("addIndividualToWeanGroup"))
			{
				addIndividualToWeanGroup(db);
			}

			if (action.equals("AddGenoCol"))
			{
				storeGenotypeTable(db, request);
				AddGenoCol(db, request);
				this.action = "GenotypeLitter";
				this.setSuccess("Gene modification + state pair successfully added");
			}

			if (action.equals("RemGenoCol"))
			{
				if (nrOfGenotypes > 1)
				{
					int currCol = 5 + ((nrOfGenotypes - 1) * 2);
					genotypeTable.removeColumn(currCol); // NB: nr. of cols is
															// now 1 lower!
					genotypeTable.removeColumn(currCol);
					nrOfGenotypes--;
					this.setSuccess("Gene modification + state pair successfully removed");
				}
				else
				{
					this.setError("Cannot remove - at least one Gene modification + state pair has to remain");
				}
				storeGenotypeTable(db, request);
				this.action = "GenotypeLitter";
			}

		}
		catch (Exception e)
		{
			String message = "Something went wrong";
			if (e.getMessage() != null)
			{
				message += ": " + e.getMessage();
			}
			this.setError(message);
			e.printStackTrace();
		}
	}

	/**
	 * Prepares the genotype editable table
	 * 
	 * @param db
	 */
	private void genotypeLitter(Database db)
	{
		nrOfGenotypes = 1;
		// Prepare table
		genotypeTable = new JQueryDataTable("GenoTable", "");
		genotypeTable.addColumn("Birth date");
		genotypeTable.addColumn("Sex");
		genotypeTable.addColumn("Color");
		genotypeTable.addColumn("Earmark");
		genotypeTable.addColumn("Background");
		genotypeTable.addColumn("Gene modification");
		genotypeTable.addColumn("Gene state");
		int row = 0;
		for (Individual animal : getAnimalsInLitter(db))
		{
			String animalName = animal.getName();
			genotypeTable.addRow(animalName);
			// Birth date
			DateInput dateInput = new DateInput("0_" + row);
			dateInput.setDateFormat("yyyy-MM-dd");
			// get the weandate, in order to limit the input for the birthdate
			// field
			String maxDate = "";
			try
			{
				maxDate = ct.getMostRecentValueAsString(animalName, "dateOfBirth");
			}
			catch (Exception e)
			{
				// do nothing.
			}
			dateInput.setJqueryproperties("dateFormat: 'yy-mm-dd', maxDate: '" + maxDate
					+ "', changeMonth: true, changeYear: true, showButtonPanel: true, numberOfMonths: 1");
			dateInput.setValue(getAnimalBirthDate(animalName));

			genotypeTable.setCell(0, row, dateInput);

			// Sex
			SelectInput sexInput = new SelectInput("1_" + row);
			for (ObservationTarget sex : this.sexList)
			{
				sexInput.addOption(sex.getName(), sex.getName());
			}
			sexInput.setValue(getAnimalSex(animalName));
			sexInput.setWidth(-1);
			genotypeTable.setCell(1, row, sexInput);

			// Color
			SelectInput colorInput = new SelectInput("2_" + row);
			for (String color : this.colorList)
			{
				colorInput.addOption(color, color);
			}
			colorInput.setValue(getAnimalColor(animalName));
			colorInput.setWidth(-1);
			genotypeTable.setCell(2, row, colorInput);

			// Earmark
			SelectInput earmarkInput = new SelectInput("3_" + row);
			for (Category earmark : this.earmarkList)
			{
				earmarkInput.addOption(earmark.getCode_String(), earmark.getCode_String());
			}
			earmarkInput.setValue(getAnimalEarmark(animalName));
			earmarkInput.setWidth(-1);
			genotypeTable.setCell(3, row, earmarkInput);

			// Background
			SelectInput backgroundInput = new SelectInput("4_" + row);
			for (ObservationTarget background : this.backgroundList)
			{
				backgroundInput.addOption(background.getName(), background.getName());
			}
			backgroundInput.setValue(getAnimalBackground(animalName));
			backgroundInput.setWidth(-1);
			genotypeTable.setCell(4, row, backgroundInput);

			// TODO: show columns and selectboxes for ALL set geno mods

			// Gene mod name (1)
			SelectInput geneNameInput = new SelectInput("5_" + row);
			for (String geneName : this.geneNameList)
			{
				geneNameInput.addOption(geneName, geneName);
			}
			geneNameInput.setValue(getAnimalGeneInfo("GeneModification", animalName, 0, db));
			geneNameInput.setWidth(-1);
			genotypeTable.setCell(5, row, geneNameInput);

			// Gene state (1)
			SelectInput geneStateInput = new SelectInput("6_" + row);
			for (String geneState : this.geneStateList)
			{
				geneStateInput.addOption(geneState, geneState);
			}
			geneStateInput.setValue(getAnimalGeneInfo("GeneState", animalName, 0, db));
			geneStateInput.setWidth(-1);
			genotypeTable.setCell(6, row, geneStateInput);
			row++;
		}
	}

	private void addIndividualToWeanGroup(Database db) throws DatabaseException, ParseException, IOException
	{

		String userName = this.getLogin().getUserName();
		// String newValue = request.getString(e);

		Query<ObservedValue> litterTypeQuery = db.query(ObservedValue.class);
		litterTypeQuery.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "Litter"));
		litterTypeQuery.addRules(new QueryRule(ObservedValue.RELATION_NAME, Operator.EQUALS, this.litter));
		List<ObservedValue> individualValueList = litterTypeQuery.find();
		List<ObservationTarget> listObsTargets = null;
		List<String> listinvName = ct.getOwnUserInvestigationNames(this.getLogin().getUserName());
		String invName = ct.getObservationTargetByName(this.litter).getInvestigation_Name();
		String nameBas = null;
		String weaner = null;
		String dob = null;
		String aType = null;

		String colorA = null;
		String targetName = null;
		// Al bestaande individuen
		for (ObservedValue v : individualValueList)
		{
			listObsTargets = db.find(ObservationTarget.class,
					new QueryRule(ObservationTarget.NAME, Operator.EQUALS, v.getTarget_Name()));
			targetName = listObsTargets.get(0).getName();
			nameBas = targetName.substring(0, 3);

			// Birthdate
			ObservedValue birthDate = ct.getObservedValuesByTargetAndFeature(targetName, "DateOfBirth", listinvName,
					listinvName.get(0)).get(0);
			ObservedValue wean = ct.getObservedValuesByTargetAndFeature(targetName, "WeanDate", listinvName,
					listinvName.get(0)).get(0);
			ObservedValue type = ct.getObservedValuesByTargetAndFeature(targetName, "AnimalType", listinvName,
					listinvName.get(0)).get(0);

			ObservedValue color = ct.getObservedValuesByTargetAndFeature(targetName, "Color", listinvName,
					listinvName.get(0)).get(0);

			weaner = wean.getValue();
			dob = birthDate.getValue();
			aType = type.getValue();
			colorA = color.getValue();

		}
		Date weanDate = newDateOnlyFormat.parse(weaner);

		int siz = individualValueList.size();

		String nrPart = (ct.getHighestNumberForPrefix(nameBas) + 1) + "";
		nrPart = ct.prependZeros(nrPart, 6);
		ObservationTarget animalToAdd = ct.createIndividual(invName, nameBas + nrPart);
		// animalsToAddList.add(animalToAdd);
		String animalName = nameBas + nrPart;
		db.add(animalToAdd);
		ct.updatePrefix("animal", nameBas, ct.getHighestNumberForPrefix(nameBas) + 1);
		// getAnimalsInLitter(db);
		int row = siz + 1;
		ArrayList<ObservedValue> individualValuesToAddList = new ArrayList<ObservedValue>();
		individualValuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, weanDate, null, "SetSex",
				"Sex", animalName, null, "Male"));
		individualValuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, weanDate, null,
				"SetLitter", "Litter", animalName, null, this.litter));
		individualValuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, weanDate, null,
				"SetAnimalType", "AnimalType", animalName, aType, null));
		individualValuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, weanDate, null,
				"SetSpecies", "Species", animalName, null, ct.getMostRecentValueAsXrefName(targetName, "Species")));

		// Set wean date on animal
		individualValuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, weanDate, null,
				"SetWeanDate", "WeanDate", animalName, newDateOnlyFormat.format(weanDate), null));
		// Set 'Active' -> starts at wean date
		individualValuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, weanDate, null,
				"SetActive", "Active", animalName, "Alive", null));
		// Set 'Date of Birth'
		individualValuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, weanDate, null,
				"SetDateOfBirth", "DateOfBirth", animalName, dob, null));

		individualValuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, weanDate, null,
				"SetLocation", "Location", animalName, null, ct.getMostRecentValueAsXrefName(targetName, "Location")));
		individualValuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, weanDate, null, "SetLine",
				"Line", animalName, null, ct.getMostRecentValueAsXrefName(targetName, "Line")));
		individualValuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, weanDate, null,
				"SetFather", "Father", animalName, null, ct.getMostRecentValueAsXrefName(targetName, "Father")));
		individualValuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, weanDate, null,
				"SetMother", "Mother", animalName, null, ct.getMostRecentValueAsXrefName(targetName, "Mother")));
		individualValuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, weanDate, null,
				"SetBackground", "Background", animalName, null,
				ct.getMostRecentValueAsXrefName(targetName, "Background")));
		individualValuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, weanDate, null,
				"SetSource", "Source", animalName, null, ct.getMostRecentValueAsXrefName(targetName, "Source")));
		individualValuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, weanDate, null,
				"SetColor", "Color", animalName, colorA, null));

		db.add(individualValuesToAddList);

		genotypeTable.addRow(animalName);
		// Birth date
		DateInput dateInput = new DateInput("0_" + row);
		dateInput.setDateFormat("yyyy-MM-dd");
		// get the weandate, in order to limit the input for the birthdate
		// field
		String maxDate = "";
		try
		{
			maxDate = ct.getMostRecentValueAsString(animalName, "dateOfBirth");
		}
		catch (Exception e)
		{
			// do nothing.
		}
		dateInput.setJqueryproperties("dateFormat: 'yy-mm-dd', maxDate: '" + maxDate
				+ "', changeMonth: true, changeYear: true, showButtonPanel: true, numberOfMonths: 1");
		dateInput.setValue(getAnimalBirthDate(animalName));

		genotypeTable.setCell(0, row, dateInput);
		// Sex
		SelectInput sexInput = new SelectInput("1_" + row);
		for (ObservationTarget sex : this.sexList)
		{
			sexInput.addOption(sex.getName(), sex.getName());
		}
		sexInput.setValue(getAnimalSex(animalName));
		sexInput.setWidth(-1);
		genotypeTable.setCell(1, row, sexInput);
		// Color
		SelectInput colorInput = new SelectInput("2_" + row);
		for (String color : this.colorList)
		{
			colorInput.addOption(color, color);
		}
		colorInput.setValue(getAnimalColor(animalName));
		colorInput.setWidth(-1);
		genotypeTable.setCell(2, row, colorInput);
		// Earmark
		SelectInput earmarkInput = new SelectInput("3_" + row);
		for (Category earmark : this.earmarkList)
		{
			earmarkInput.addOption(earmark.getCode_String(), earmark.getCode_String());
		}
		earmarkInput.setValue(getAnimalEarmark(animalName));
		earmarkInput.setWidth(-1);
		genotypeTable.setCell(3, row, earmarkInput);
		// Background
		SelectInput backgroundInput = new SelectInput("4_" + row);
		for (ObservationTarget background : this.backgroundList)
		{
			backgroundInput.addOption(background.getName(), background.getName());
		}
		backgroundInput.setValue(getAnimalBackground(animalName));
		backgroundInput.setWidth(-1);
		genotypeTable.setCell(4, row, backgroundInput);

		// TODO: show columns and selectboxes for ALL set geno mods

		// Gene mod name (1)
		SelectInput geneNameInput = new SelectInput("5_" + row);
		for (String geneName : this.geneNameList)
		{
			geneNameInput.addOption(geneName, geneName);
		}
		geneNameInput.setValue(getAnimalGeneInfo("GeneModification", animalName, 0, db));
		geneNameInput.setWidth(-1);
		genotypeTable.setCell(5, row, geneNameInput);
		// Gene state (1)
		SelectInput geneStateInput = new SelectInput("6_" + row);
		for (String geneState : this.geneStateList)
		{
			geneStateInput.addOption(geneState, geneState);
		}
		geneStateInput.setValue(getAnimalGeneInfo("GeneState", animalName, 0, db));
		geneStateInput.setWidth(-1);
		genotypeTable.setCell(6, row, geneStateInput);
		row++;
		action = "editIndividual";
	}

	private void weanLitter(Database db) throws DatabaseException, ParseException
	{
		// Find out species of litter user wants to wean, so we can provide the
		// right name prefix
		String parentgroupName = ct.getMostRecentValueAsXrefName(litter, "Parentgroup");
		String motherName = findParentForParentgroup(parentgroupName, "Mother", db);
		String fatherName = findParentForParentgroup(parentgroupName, "Father", db);
		String speciesName = "";
		if (motherName == null || motherName.equalsIgnoreCase("") || motherName.equalsIgnoreCase("Unknown"))
		{
			speciesName = ct.getMostRecentValueAsXrefName(fatherName, "Species");
		}
		else
		{
			speciesName = ct.getMostRecentValueAsXrefName(motherName, "Species");
		}// this.setMotherLocation(ct.getMostRecentValueAsXrefName(motherName,
			// "Location"));
			// TODO: get rid of duplication with AddAnimalPlugin
			// TODO: put this hardcoded info in the database (NamePrefix table)
		if (speciesName.equals("House mouse"))
		{
			this.prefix = "mm_";
		}
		if (speciesName.equals("Brown rat"))
		{
			this.prefix = "rn_";
		}
		if (speciesName.equals("Common vole"))
		{
			this.prefix = "mar_";
		}
		if (speciesName.equals("Tundra vole"))
		{
			this.prefix = "mo_";
		}
		if (speciesName.equals("Syrian hamster"))
		{
			this.prefix = "ma_";
		}
		if (speciesName.equals("European groundsquirrel"))
		{
			this.prefix = "sc_";
		}
		if (speciesName.equals("Siberian hamster"))
		{
			this.prefix = "ps_";
		}
		if (speciesName.equals("Domestic guinea pig"))
		{
			this.prefix = "cp_";
		}
		if (speciesName.equals("Fat-tailed dunnart"))
		{
			this.prefix = "sg_";
		}
	}

	private Boolean isWeaned(Database db) throws Exception
	{
		List<QueryRule> filterRules = new ArrayList<QueryRule>();
		filterRules.add(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "WeanDate"));
		filterRules.add(new QueryRule(ObservedValue.TARGET_NAME, Operator.EQUALS, this.litter));
		List<ObservedValue> listId = db.find(ObservedValue.class, new QueryRule(filterRules));

		if (listId.isEmpty())
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	private Boolean isGenotyped(Database db) throws Exception
	{
		List<QueryRule> filterRules = new ArrayList<QueryRule>();
		filterRules.add(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "GenotypeDate"));
		filterRules.add(new QueryRule(ObservedValue.TARGET_NAME, Operator.EQUALS, this.litter));
		List<ObservedValue> listId = db.find(ObservedValue.class, new QueryRule(filterRules));

		if (listId.isEmpty())
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	private void editIndividuals(Database db, String litter, MolgenisRequest request) throws DatabaseException,
			ParseException, IOException
	{
		if (request.getString("addNew") != null)
		{
			if (request.getString("addNew").equals("true"))
			{

				addIndividualToWeanGroup(db);
			}
		}
		nrOfGenotypes = 1;
		// Prepare table
		genotypeTable = new JQueryDataTable("GenoTable", "");
		genotypeTable.addColumn("Birth date");
		genotypeTable.addColumn("Sex");
		genotypeTable.addColumn("Color");
		genotypeTable.addColumn("Earmark");
		genotypeTable.addColumn("Background");
		genotypeTable.addColumn("Gene modification");
		genotypeTable.addColumn("Gene state");
		int row = 0;
		for (Individual animal : getAnimalsInLitter(db))
		{
			String animalName = animal.getName();
			genotypeTable.addRow(animalName);
			// Birth date
			DateInput dateInput = new DateInput("0_" + row);
			dateInput.setDateFormat("yyyy-MM-dd");
			// get the weandate, in order to limit the input for the birthdate
			// field
			String maxDate = "";
			try
			{
				maxDate = ct.getMostRecentValueAsString(animalName, "dateOfBirth");
			}
			catch (Exception e)
			{
				// do nothing.
			}
			dateInput.setJqueryproperties("dateFormat: 'yy-mm-dd', maxDate: '" + maxDate
					+ "', changeMonth: true, changeYear: true, showButtonPanel: true, numberOfMonths: 1");
			dateInput.setValue(getAnimalBirthDate(animalName));

			genotypeTable.setCell(0, row, dateInput);
			// Sex
			SelectInput sexInput = new SelectInput("1_" + row);
			for (ObservationTarget sex : this.sexList)
			{
				sexInput.addOption(sex.getName(), sex.getName());
			}
			sexInput.setValue(getAnimalSex(animalName));
			sexInput.setWidth(-1);
			genotypeTable.setCell(1, row, sexInput);
			// Color
			SelectInput colorInput = new SelectInput("2_" + row);
			for (String color : this.colorList)
			{
				colorInput.addOption(color, color);
			}
			colorInput.setValue(getAnimalColor(animalName));
			colorInput.setWidth(-1);
			genotypeTable.setCell(2, row, colorInput);
			// Earmark
			SelectInput earmarkInput = new SelectInput("3_" + row);
			for (Category earmark : this.earmarkList)
			{
				earmarkInput.addOption(earmark.getCode_String(), earmark.getCode_String());
			}
			earmarkInput.setValue(getAnimalEarmark(animalName));
			earmarkInput.setWidth(-1);
			genotypeTable.setCell(3, row, earmarkInput);
			// Background
			SelectInput backgroundInput = new SelectInput("4_" + row);
			for (ObservationTarget background : this.backgroundList)
			{
				backgroundInput.addOption(background.getName(), background.getName());
			}
			backgroundInput.setValue(getAnimalBackground(animalName));
			backgroundInput.setWidth(-1);
			genotypeTable.setCell(4, row, backgroundInput);

			// TODO: show columns and selectboxes for ALL set geno mods

			// Gene mod name (1)
			SelectInput geneNameInput = new SelectInput("5_" + row);
			for (String geneName : this.geneNameList)
			{
				geneNameInput.addOption(geneName, geneName);
			}
			geneNameInput.setValue(getAnimalGeneInfo("GeneModification", animalName, 0, db));
			geneNameInput.setWidth(-1);
			genotypeTable.setCell(5, row, geneNameInput);
			// Gene state (1)
			SelectInput geneStateInput = new SelectInput("6_" + row);
			for (String geneState : this.geneStateList)
			{
				geneStateInput.addOption(geneState, geneState);
			}
			geneStateInput.setValue(getAnimalGeneInfo("GeneState", animalName, 0, db));
			geneStateInput.setWidth(-1);
			genotypeTable.setCell(6, row, geneStateInput);
			row++;
		}
		action = "editIndividual";
	}

	private void editLitter(Database db) throws DatabaseException, ParseException
	{

		// Prepare table
		// genotypeTable = new Table("GenoTable", "");
		editTable = new JQueryDataTable("EditTable", "");

		for (String e : litterProtocol())
		{
			// editTable.addColumn(e);
			editTable.addRow(e);
		}

		int row = 0;

		List<ObservedValue> listObservedValues = db.find(ObservedValue.class, new QueryRule(ObservedValue.TARGET_NAME,
				Operator.EQUALS, this.litter));
		editTable.addColumn(this.litter);

		List<ObservationTarget> listObsTargets = new ArrayList<ObservationTarget>();
		Map<String, String> observableFeat = new HashMap<String, String>();

		for (ObservedValue val : listObservedValues)
		{

			listObsTargets = db.find(ObservationTarget.class, new QueryRule(ObservationTarget.NAME, Operator.EQUALS,
					val.getTarget_Name()));

			if (val.getFeature_Name().equals("Parentgroup"))
			{
				observableFeat.put(val.getFeature_Name(), val.getRelation_Name());
			}
			else
			{
				observableFeat.put(val.getFeature_Name(), val.getValue());
			}
		}

		// Active
		StringInput inputActive = new StringInput("Active");
		inputActive.setId("Active");
		if (observableFeat.containsKey("Active"))
		{

			inputActive.setValue(observableFeat.get("Active"));
		}
		else
		{
			inputActive.setValue("");

		}
		// editTable.setCell(row, 0, inputActive);
		editTable.setCell(0, row, inputActive);
		row++;
		// DateOfBirth
		DateInput dateInputBirthDate = new DateInput("DateOfBirth");
		dateInputBirthDate.setDateFormat("yyyy-MM-dd");
		String maxD = observableFeat.get("WeanDate") == null ? "" : observableFeat.get("WeanDate");
		// dateInputBirthDate.setJqueryproperties("maxDate: "newDateOnlyFormat.parse(observableFeat.get("WeanDate")"")
		if (observableFeat.containsKey("DateOfBirth"))
		{

			dateInputBirthDate.setJqueryproperties("dateFormat: 'yy-mm-dd', maxDate: '" + maxD
					+ "', changeMonth: true, changeYear: true, showButtonPanel: true, numberOfMonths: 1");

			if (observableFeat.get("DateOfBirth").isEmpty())
			{
				dateInputBirthDate.setValue(null);
			}
			else
			{
				dateInputBirthDate.setValue(newDateOnlyFormat.parse(observableFeat.get("DateOfBirth")));
			}
		}
		else
		{
			dateInputBirthDate.setValue(null);

		}
		editTable.setCell(0, row, dateInputBirthDate);
		row++;
		// GenotypeDate
		DateInput dateInputGenotypeDate = new DateInput("GenotypeDate");
		dateInputGenotypeDate.setDateFormat("yyyy-MM-dd");
		if (observableFeat.containsKey("GenotypeDate"))
		{

			if (observableFeat.get("GenotypeDate").isEmpty())
			{
				dateInputGenotypeDate.setValue(null);
			}
			else
			{
				dateInputGenotypeDate.setValue(newDateOnlyFormat.parse(observableFeat.get("GenotypeDate")));
			}

		}
		else
		{
			dateInputGenotypeDate.setValue(null);

		}

		editTable.setCell(0, row, dateInputGenotypeDate);
		row++;
		// Line

		StringInput inputLine = new StringInput("Line");
		inputLine.setId("Line");
		inputLine.setReadonly(true);

		// for (ObservationTarget background : this.lineList)
		// {
		// inputLine.addOption(background.getName(), background.getName());
		// }
		// inputLine.setName("Line");
		// inputLine.setOptions("disabled", "disabled");
		if (observableFeat.containsKey("Line"))
		{
			inputLine.setValue(this.line);
		}
		else
		{
			inputLine.setValue("");

		}
		editTable.setCell(0, row, inputLine);
		row++;
		// Parentgroup

		Query<ObservedValue> query = db.query(ObservedValue.class);

		query.addRules(new QueryRule(ObservedValue.TARGET_NAME, Operator.EQUALS, this.litter));

		query.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "Parentgroup"));

		query.find().get(0);

		StringInput inputParentGroup = new StringInput("Parentgroup");
		inputParentGroup.setId("Parentgroup");
		inputParentGroup.setReadonly(true);
		if (observableFeat.containsKey("Parentgroup"))
		{
			inputParentGroup.setValue(observableFeat.get("Parentgroup"));
			weanparentgroup = observableFeat.get("Parentgroup");
		}
		else
		{
			inputParentGroup.setValue("");

		}
		editTable.setCell(0, row, inputParentGroup);
		row++;
		// Remark
		StringInput inputRemark = new StringInput("Remark");
		if (observableFeat.containsKey("Remark"))
		{

			inputRemark.setValue(observableFeat.get("Remark"));
		}
		else
		{
			inputRemark.setValue("");

		}
		editTable.setCell(0, row, inputRemark);

		row++;
		// Size
		StringInput inputSize = new StringInput("Size");
		if (observableFeat.containsKey("Size"))
		{

			inputSize.setValue(observableFeat.get("Size"));
		}
		else
		{
			inputSize.setValue("");

		}
		editTable.setCell(0, row, inputSize);
		row++;
		// WeanDate
		DateInput dateInputWeanDate = new DateInput("WeanDate");
		dateInputWeanDate.setDateFormat("yyyy-MM-dd");
		if (observableFeat.containsKey("WeanDate"))
		{
			if (observableFeat.get("WeanDate").isEmpty())
			{
				dateInputWeanDate.setValue(null);
			}
			else
			{
				dateInputWeanDate.setValue(newDateOnlyFormat.parse(observableFeat.get("WeanDate")));
			}
		}
		else
		{
			dateInputWeanDate.setValue(null);

		}
		editTable.setCell(0, row, dateInputWeanDate);
		row++;
		// WeanSize
		StringInput inputWeanSize = new StringInput("WeanSize");
		inputWeanSize.setReadonly(true);
		if (observableFeat.containsKey("WeanSize"))
		{

			inputWeanSize.setValue(Integer.toString(getAnimalsInLitter(db).size()));
			weansize = getAnimalsInLitter(db).size();
			stillToWeanYN = false;
		}
		else
		{
			stillToWeanYN = true;
			inputWeanSize.setValue("");

		}
		editTable.setCell(0, row, inputWeanSize);
	}

	private void editLitterToDb(Database db, MolgenisRequest request) throws Exception
	{

		Query<ObservedValue> query = db.query(ObservedValue.class);

		query.addRules(new QueryRule(ObservedValue.TARGET_NAME, Operator.EQUALS, this.litter));

		query.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.IN, litterProtocol()));

		List<String> existingColumns = new ArrayList<String>();

		String applicationProtocolName = null;

		List<ObservedValue> deleteObservedValues = new ArrayList<ObservedValue>();

		for (ObservedValue ov : query.find())
		{
			if (applicationProtocolName == null)
			{
				applicationProtocolName = ov.getProtocolApplication_Name();
			}
			String e = ov.getFeature_Name();

			existingColumns.add(e);
			Query<ObservedValue> litterTypeQuery = db.query(ObservedValue.class);
			litterTypeQuery.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "Litter"));
			litterTypeQuery.addRules(new QueryRule(ObservedValue.RELATION_NAME, Operator.EQUALS, this.litter));
			List<ObservedValue> individualValueList = litterTypeQuery.find();
			// This has to go this way, when a field is disabled it will return
			// a null when you do a request.getString(e)
			if (e.equalsIgnoreCase("Parentgroup") || e.equalsIgnoreCase("Line"))
			{
				// do nothing
			}

			else if (request.getString(e) == null)
			{
				deleteObservedValues.add(ov);
			}
			else if (e.equalsIgnoreCase("DateOfBirth"))
			{

				String newValue = request.getString(e);

				List<ObservationTarget> listObsTargets = null;
				List<String> invName = ct.getOwnUserInvestigationNames(this.getLogin().getUserName());
				if (individualValueList.isEmpty())
				{
					ov.setValue(newValue);
					db.update(ov);

				}
				else
				{
					for (ObservedValue v : individualValueList)
					{
						listObsTargets = db.find(ObservationTarget.class, new QueryRule(ObservationTarget.NAME,
								Operator.EQUALS, v.getTarget_Name()));
						String targetName = listObsTargets.get(0).getName();

						// Birthdate
						ObservedValue birthDate = ct.getObservedValuesByTargetAndFeature(targetName, "DateOfBirth",
								invName, invName.get(0)).get(0);
						birthDate.setValue(newValue);
						db.update(birthDate);
						ov.setValue(newValue);
						db.update(ov);

					}
				}

			}
			else if (e.equalsIgnoreCase("WeanDate"))
			{

				String newValue = request.getString(e);

				List<ObservationTarget> listObsTargets = null;
				List<String> invName = ct.getOwnUserInvestigationNames(this.getLogin().getUserName());

				for (ObservedValue v : individualValueList)
				{
					listObsTargets = db.find(ObservationTarget.class, new QueryRule(ObservationTarget.NAME,
							Operator.EQUALS, v.getTarget_Name()));
					String targetName = listObsTargets.get(0).getName();

					// Weandate
					ObservedValue weanDate = ct.getObservedValuesByTargetAndFeature(targetName, "WeanDate", invName,
							invName.get(0)).get(0);
					ObservedValue active = ct.getObservedValuesByTargetAndFeature(targetName, "Active", invName,
							invName.get(0)).get(0);
					active.setTime(newDateOnlyFormat.parse(newValue));
					weanDate.setValue(newValue);
					db.update(weanDate);
					db.update(active);
					ov.setValue(newValue);

					db.update(ov);

				}

			}
			else
			{
				String newValue = request.getString(e);

				System.out.println("########### " + ov);
				String val = ov.getValue();
				String rel = ov.getRelation_Name();
				if (val != null || rel != null)
				{
					if (val != null)
					{
						ov.setValue(newValue);
						System.out.println("###################--> " + val);
					}
					else
					{
						ov.setRelation_Name(rel);
						System.out.println("###################--> " + rel);
					}
				}

				db.update(ov);
			}

		}
		db.remove(deleteObservedValues);

		// This part is when there was no value in the database for that
		// measurement
		for (String e : litterProtocol())
		{
			if (request.getString(e) != null)
			{
				if (!existingColumns.contains(e))
				{
					ObservedValue ov = new ObservedValue();
					ov.setTarget_Name(this.litter);
					ov.setFeature_Name(e);
					ov.setValue(request.getString(e));

					if (applicationProtocolName != null)
					{
						ov.setProtocolApplication_Name(applicationProtocolName);
					}

					db.add(ov);
				}
			}
		}
		litterMatrixViewer.setDatabase(db);
		// litterMatrixViewer.handleRequest(db, request);
		litterMatrixViewerString = litterMatrixViewer.render();
		this.action = "init"; // return to start screen
		this.entity = "Litters";
		return;
	}

	private String AddParentgroup2(Database db, MolgenisRequest request, List<String> papa, List<String> mama,
			String startdate, String remarks) throws Exception
	{
		Date now = new Date();
		String invName = ct.getOwnUserInvestigationNames(this.getLogin().getUserName()).get(0);

		Date eventDate = dbFormat.parse(startdate);
		// Make parentgroup
		String groupPrefix = "PG_" + line + "_";
		int groupNr = ct.getHighestNumberForPrefix(groupPrefix) + 1;
		String groupNrPart = "" + groupNr;
		groupNrPart = ct.prependZeros(groupNrPart, 6);
		String groupName = groupPrefix + groupNrPart;
		ct.createPanel(invName, groupName);
		// Make or update name prefix entry
		ct.updatePrefix("parentgroup", groupPrefix, groupNr);
		// Mark group as parent group using a special event
		db.add(ct.createObservedValueWithProtocolApplication(invName, now, null, "SetTypeOfGroup", "TypeOfGroup",
				groupName, "Parentgroup", null));
		db.add(ct.createObservedValueWithProtocolApplication(invName, now, null, "SetLine", "Line", groupName, null,
				line));
		// Add parent(s)
		System.out.println("m>>>>>>>>>>>>>>>>>>>>>>>>>>> " + mama.size() + mama);
		System.out.println("p>>>>>>>>>>>>>>>>>>>>>>>>>>> " + papa.size() + papa);
		if (mama.size() > 0)
		{
			AddParents(db, mama, "SetParentgroupMother", "ParentgroupMother", groupName, eventDate);
		}
		if (papa.size() > 0)
		{
			AddParents(db, papa, "SetParentgroupFather", "ParentgroupFather", groupName, eventDate);

		}

		// Set line

		// Set start date
		db.add(ct.createObservedValueWithProtocolApplication(invName, now, null, "SetStartDate", "StartDate",
				groupName, dbFormat.format(eventDate), null));
		// Set remarks
		if (remarks != null)
		{
			db.add(ct.createObservedValueWithProtocolApplication(invName, now, null, "SetRemark", "Remark", groupName,
					remarks, null));
		}

		// Add Set Active, with (start)time = entrydate and endtime = null
		db.add(ct.createObservedValueWithProtocolApplication(invName, now, null, "SetActive", "Active", groupName,
				"Active", null));

		return groupName;
	}

	/*
	 * private String AddParentgroup(Database db, MolgenisRequest request)
	 * throws Exception { Date now = new Date(); String invName =
	 * ct.getOwnUserInvestigationNames(this.getLogin().getUserName()).get(0); //
	 * Save start date and remarks that were set in screen 4 if
	 * (request.getString("startdate") != null) {
	 * setStartdate(request.getString("startdate")); } if
	 * (request.getString("remarks") != null) {
	 * setRemarks(request.getString("remarks")); } Date eventDate =
	 * dateOnlyFormat.parse(startdate); // Make parentgroup String groupPrefix =
	 * "PG_" + line + "_"; int groupNr =
	 * ct.getHighestNumberForPrefix(groupPrefix) + 1; String groupNrPart = "" +
	 * groupNr; groupNrPart = ct.prependZeros(groupNrPart, 6); String groupName
	 * = groupPrefix + groupNrPart; ct.makePanel(invName, groupName, userName);
	 * // Make or update name prefix entry ct.updatePrefix("parentgroup",
	 * groupPrefix, groupNr); // Mark group as parent group using a special
	 * event db.add(ct.createObservedValueWithProtocolApplication(invName, now,
	 * null, "SetTypeOfGroup", "TypeOfGroup", groupName, "Parentgroup", null));
	 * // Add parent(s) AddParents(db, this.selectedMotherNameList,
	 * "SetParentgroupMother", "ParentgroupMother", groupName, eventDate);
	 * AddParents(db, this.selectedFatherNameList, "SetParentgroupFather",
	 * "ParentgroupFather", groupName, eventDate); // Set line
	 * db.add(ct.createObservedValueWithProtocolApplication(invName, now, null,
	 * "SetLine", "Line", groupName, null, line)); // Set start date
	 * db.add(ct.createObservedValueWithProtocolApplication(invName, now, null,
	 * "SetStartDate", "StartDate", groupName, dbFormat.format(eventDate),
	 * null)); // Set remarks if (remarks != null) {
	 * db.add(ct.createObservedValueWithProtocolApplication(invName, now, null,
	 * "SetRemark", "Remark", groupName, remarks, null)); }
	 * 
	 * //Add Set Active, with (start)time = entrydate and endtime = null
	 * db.add(ct.createObservedValueWithProtocolApplication(invName, now, null,
	 * "SetActive", "Active", groupName, "Active", null));
	 * 
	 * return groupName; }
	 */

	@Override
	public void reload(Database db)
	{
		ct.setDatabase(db);
		List<String> investigationNames = ct.getAllUserInvestigationNames(this.getLogin().getUserName());

		// Populate lists (do this on every reload so they keep fresh, and do it
		// here
		// because we need the lineList in the init part that comes after)

		try
		{
			// Populate line list

			lineList = ct.getAllMarkedPanels("Line", investigationNames);
			// Default selected is first line
			if (line == null && lineList.size() > 0)
			{
				line = lineList.get(0).getName();
			}
			/*
			 * if (selectedMotherNameList == null) { selectedMotherNameList =
			 * new ArrayList<String>(); } if (selectedFatherNameList == null) {
			 * selectedFatherNameList = new ArrayList<String>(); }
			 */

		}
		catch (Exception e)
		{
			String message = "Something went wrong while loading lists";
			if (e.getMessage() != null)
			{
				message += (": " + e.getMessage());
			}
			this.setError(message);
			e.printStackTrace();
		}
		// Some init that only needs to be done once after login
		if (userName != this.getLogin().getUserName())
		{
			userName = this.getLogin().getUserName();
			ct.makeObservationTargetNameMap(userName, false);
			this.setStartdate(dbFormat.format(new Date()));
			// Prepare pg matrix
			if (pgMatrixViewer == null)
			{
				loadPgMatrixViewer(db);
			}
			pgMatrixViewer.setDatabase(db);
			pgMatrixViewerString = pgMatrixViewer.render();
			// Prepare litter matrix
			if (litterMatrixViewer == null)
			{
				loadLitterMatrixViewer(db);
			}
			litterMatrixViewer.setDatabase(db);
			litterMatrixViewerString = litterMatrixViewer.render();
			try
			{
				// Populate backgrounds list
				// this.setBackgroundList(ct.getAllMarkedPanels("Background",
				// investigationNames));
				// FIXME, filter by species
				String species = ct.getMostRecentValueAsXrefName(line, "Species");
				List<ObservationTarget> bckgrlist = new ArrayList<ObservationTarget>();
				for (ObservationTarget b : ct.getAllMarkedPanels("Background", investigationNames))
				{
					// Only show if background belongs to chosen species
					if (ct.getMostRecentValueAsXrefName(b.getName(), "Species").equals(species))
					{
						bckgrlist.add(b);
					}
				}
				this.setBackgroundList(bckgrlist);

				// Populate sexes list
				this.setSexList(ct.getAllMarkedPanels("Sex", investigationNames));
				// Populate gene name list
				this.setGeneNameList(ct.getAllCodesForFeatureAsStrings("GeneModification"));
				// Populate gene state list
				this.setGeneStateList(ct.getAllCodesForFeatureAsStrings("GeneState"));
				// Populate color list
				this.setColorList(ct.getAllCodesForFeatureAsStrings("Color"));
				// Populate earmark list
				this.setEarmarkList(ct.getAllCodesForFeature("Earmark"));
				// Populate name prefixes list for the animals
				this.bases = new ArrayList<String>();
				List<String> tmpPrefixes = ct.getPrefixes("animal");
				for (String tmpPrefix : tmpPrefixes)
				{
					if (!tmpPrefix.equals(""))
					{
						this.bases.add(tmpPrefix);
					}
				}
				// Populate location list
				this.setLocationList(ct.getAllLocations());
			}
			catch (Exception e)
			{
				String message = "Something went wrong while loading lists";
				if (e.getMessage() != null)
				{
					message += (": " + e.getMessage());
				}
				this.setError(message);
				e.printStackTrace();
			}
		}
	}

	public String getBirthdate()
	{
		if (birthdate != null)
		{
			return birthdate;
		}
		return newDateOnlyFormat.format(new Date());
	}

	public int getLitterSize()
	{
		return litterSize;
	}

	public String getLitterRemarks()
	{
		return litterRemarks;
	}

	public String getSelectedParentgroup()
	{
		if (selectedParentgroup != null)
		{
			return selectedParentgroup;
		}
		return "Error: no parentgoup selected";
	}

	private String ApplyAddLitter(Database db, MolgenisRequest request) throws Exception
	{
		Date now = new Date();

		setUserFields(request, false);
		Date eventDate = newDateOnlyFormat.parse(birthdate);
		String userName = this.getLogin().getUserName();
		String invName = ct.getOwnUserInvestigationNames(userName).get(0);
		String lineName = ct.getMostRecentValueAsXrefName(selectedParentgroup, "Line");
		// Init lists that we can later add to the DB at once
		List<ObservedValue> valuesToAddList = new ArrayList<ObservedValue>();
		// Make group
		String litterPrefix = "LT_" + lineName + "_";
		int litterNr = ct.getHighestNumberForPrefix(litterPrefix) + 1;
		String litterNrPart = "" + litterNr;
		litterNrPart = ct.prependZeros(litterNrPart, 6);
		String litterName = litterPrefix + litterNrPart;
		ct.createPanel(invName, litterName);
		// Make or update name prefix entry
		ct.updatePrefix("litter", litterPrefix, litterNr);
		// Mark group as a litter
		db.add(ct.createObservedValueWithProtocolApplication(invName, now, null, "SetTypeOfGroup", "TypeOfGroup",
				litterName, "Litter", null));
		// Apply other fields using event
		ProtocolApplication app = ct.createProtocolApplication(invName, "SetLitterSpecs");
		db.add(app);
		String paName = app.getName();
		// Parentgroup

		valuesToAddList.add(ct.createObservedValue(invName, paName, eventDate, null, "Parentgroup", litterName, null,
				selectedParentgroup));
		// ParentgroupMother / parentgroupFater

		// Set Line also on Litter
		if (lineName != null)
		{
			valuesToAddList.add(ct.createObservedValue(invName, paName, eventDate, null, "Line", litterName, null,
					lineName));
		}
		// Date of Birth
		valuesToAddList.add(ct.createObservedValue(invName, paName, eventDate, null, "DateOfBirth", litterName,
				newDateOnlyFormat.format(eventDate), null));
		// Size
		valuesToAddList.add(ct.createObservedValue(invName, paName, eventDate, null, "Size", litterName,
				Integer.toString(litterSize), null));
		// Size approximate (certain)?
		String valueString = "0";
		if (litterSizeApproximate == true)
		{
			valueString = "1";
		}
		valuesToAddList.add(ct.createObservedValue(invName, paName, eventDate, null, "Certain", litterName,
				valueString, null));
		// Remarks
		if (litterRemarks != null)
		{
			db.add(ct.createObservedValueWithProtocolApplication(invName, now, null, "SetRemark", "Remark", litterName,
					litterRemarks, null));
		}
		// Try to get Source via Line
		String sourceName = ct.getMostRecentValueAsXrefName(lineName, "Source");
		if (sourceName != null)
		{
			valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, eventDate, null, "SetSource",
					"Source", litterName, null, sourceName));
		}
		// Active
		valuesToAddList.add(ct.createObservedValue(invName, paName, eventDate, null, "Active", litterName, "Active",
				null));
		// Add everything to DB
		db.add(valuesToAddList);

		return litterName;
	}

	private void setUserFields(MolgenisRequest request, boolean wean) throws Exception
	{
		if (wean == true)
		{
			locName = request.getString("location");
			if (locName != null && locName.equals(""))
			{
				locName = null;
			}
			respres = request.getString("respres");
			if (request.getString("weandate") == null || request.getString("weandate").equals(""))
			{
				throw new Exception("Wean date cannot be empty");
			}
			weandate = request.getString("weandate"); // in old date format!
			weanSizeFemale = 0;
			if (request.getInt("weansizefemale") != null)
			{
				weanSizeFemale = request.getInt("weansizefemale");
			}
			weanSizeMale = 0;
			if (request.getInt("weansizemale") != null)
			{
				weanSizeMale = request.getInt("weansizemale");
			}
			weanSizeUnknown = 0;
			if (request.getInt("weansizeunknown") != null)
			{
				weanSizeUnknown = request.getInt("weansizeunknown");
			}
			remarks = request.getString("remarks");
			if (request.getString("namebase") != null)
			{
				nameBase = request.getString("namebase");
				if (nameBase.equals("New"))
				{
					if (request.getString("newnamebase") != null)
					{
						nameBase = request.getString("newnamebase");
					}
					else
					{
						nameBase = "";
					}
				}
			}
			else
			{
				nameBase = "";
			}
			if (request.getInt("startnumber") != null)
			{
				startNumber = request.getInt("startnumber");
			}
			else
			{
				startNumber = 1; // standard start at 1
			}

		}
		else
		{
			if (request.getString("birthdate") == null || request.getString("birthdate").equals(""))
			{
				throw new Exception("Birth date cannot be empty");
			}
			birthdate = request.getString("birthdate"); // in old date format!
			this.litterSize = request.getInt("littersize");
			if (request.getBoolean("sizeapp_toggle") != null)
			{
				this.litterSizeApproximate = true;
			}
			else
			{
				this.litterSizeApproximate = false;
			}
			this.litterRemarks = request.getString("litterremarks");
		}
	}

	private String findParentForParentgroup(String parentgroupName, String parentSex, Database db)
			throws DatabaseException, ParseException
	{
		ct.setDatabase(db);
		Query<ObservedValue> parentQuery = db.query(ObservedValue.class);
		parentQuery.addRules(new QueryRule(ObservedValue.TARGET_NAME, Operator.EQUALS, parentgroupName));
		parentQuery.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "Parentgroup" + parentSex));
		List<ObservedValue> parentValueList = parentQuery.find();

		if (parentValueList.size() > 0)
		{
			return parentValueList.get(0).getRelation_Name();
		}
		else
		{
			return "Unknown";
			// throw new DatabaseException("Fatal error: no " + parentSex +
			// " found for parentgroup " + parentgroupName);
		}
	}

	private int Wean(Database db, MolgenisRequest request) throws Exception
	{
		Date now = new Date();
		String invName = ct.getObservationTargetByName(litter).getInvestigation_Name();
		setUserFields(request, true);
		Date weanDate = newDateOnlyFormat.parse(weandate);
		String userName = this.getLogin().getUserName();

		// Init lists that we can later add to the DB at once
		List<ObservedValue> valuesToAddList = new ArrayList<ObservedValue>();
		List<ObservationTarget> animalsToAddList = new ArrayList<ObservationTarget>();

		// Source (take from litter)
		String sourceName = ct.getMostRecentValueAsXrefName(litter, "Source");
		// Get litter birth date
		String litterBirthDateString = ct.getMostRecentValueAsString(litter, "DateOfBirth");
		// Date litterBirthDate =
		// newDateOnlyFormat.parse(litterBirthDateString);
		// Find Parentgroup for this litter
		String parentgroupName = ct.getMostRecentValueAsXrefName(litter, "Parentgroup");
		// Find Line for this Parentgroup
		String lineName = ct.getMostRecentValueAsXrefName(parentgroupName, "Line");
		String animalType = "";
		String speciesName = "";
		String color = "";
		String motherBackgroundName = null;
		String fatherBackgroundName = null;
		List<ObservedValue> motherAllGeneMods = null;
		List<ObservedValue> fatherAllGeneMods = null;
		// Find first mother, plus her animal type, species, color, background,
		// gene modification and gene state

		// TODO: handle situation where one of the parents is not known
		// properly.
		// TODO: properly handle the situation of harem breeding (basicly group
		// of possible mothers/fathers) / maybe make a totally different plugin
		// for this situation?

		List<Integer> investigationIds = ct.getAllUserInvestigationIds(userName);
		// handle situation that only one of the parents is known.
		String motherName = findParentForParentgroup(parentgroupName, "Mother", db);

		if (motherName.equals("Unknown"))
		{
			speciesName = "Unknown";
			String motherAnimalType = "Unknown";
			color = "Unknown";
			motherBackgroundName = "Unknown";

		}
		else
		{
			speciesName = ct.getMostRecentValueAsXrefName(motherName, "Species");
			String motherAnimalType = ct.getMostRecentValueAsString(motherName, "AnimalType");
			animalType = motherAnimalType;
			color = ct.getMostRecentValueAsString(motherName, "Color");
			motherBackgroundName = ct.getMostRecentValueAsXrefName(motherName, "Background");

			// HUGE
			// to each other?, via prot app??
			motherAllGeneMods = ct.getObservedValuesByTargetAndMeasurement(motherName, "GeneModification",
					investigationIds); // this does not
										// work,
										// only get the ones
										// for
										// the current
										// animal.
			// List<ObservedValue> motherAllGeneStates =
			// ct.getObservedValuesByTargetAndMeasurement(motherName,
			// "GeneModification", investigationIds);

		}

		// Find father and his background

		String fatherName = findParentForParentgroup(parentgroupName, "Father", db);
		if (fatherName.equalsIgnoreCase("Unknown") && motherName.equalsIgnoreCase("Unknown"))
		{
			throw new DatabaseException("Fatal error: no parents found for parentgroup " + parentgroupName);
		}
		else if (fatherName.equalsIgnoreCase("Unknown"))
		{
			fatherBackgroundName = "Unknown";
			String fatherAnimalType = "Unknown";

		}
		else
		{
			if (!motherName.equalsIgnoreCase("Unknown"))
			{
				speciesName = ct.getMostRecentValueAsXrefName(fatherName, "Species");
				color = ct.getMostRecentValueAsString(fatherName, "Color");
			}

			fatherBackgroundName = ct.getMostRecentValueAsXrefName(fatherName, "Background");
			String fatherAnimalType = ct.getMostRecentValueAsString(fatherName, "AnimalType");
			fatherAllGeneMods = ct.getObservedValuesByTargetAndMeasurement(fatherName, "GeneModification",
					investigationIds); // this does not
										// work,
										// only get the ones
										// for
										// the current
										// animal.
			// List<ObservedValue> fatherAllGeneStates =
			// ct.getObservedValuesByTargetAndMeasurement(fatherName,
			// "GeneModification", investigationIds);

			// If one of the parents is GMO, animal is GMO
			if (animalType.equals("B. Transgeen dier") || fatherAnimalType.equals("B. Transgeen dier"))
			{
				animalType = "B. Transgeen dier";
			}
			else
			{
				animalType = fatherAnimalType;
			}

		}

		// Keep normal and transgene types, but set type of child from wild
		// mother to normal
		if (animalType.equals("C. Wildvang") || animalType.equals("D. Biotoop"))
		{
			animalType = "A. Gewoon dier";
		}
		// Set wean sizes
		int weanSize = weanSizeFemale + weanSizeMale + weanSizeUnknown;
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, now, null, "SetWeanSize",
				"WeanSize", litter, Integer.toString(weanSize), null));
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, now, null, "SetWeanSizeFemale",
				"WeanSizeFemale", litter, Integer.toString(weanSizeFemale), null));
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, now, null, "SetWeanSizeMale",
				"WeanSizeMale", litter, Integer.toString(weanSizeMale), null));
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, now, null, "SetWeanSizeUnknown",
				"WeanSizeUnknown", litter, Integer.toString(weanSizeUnknown), null));
		// Set wean date on litter -> this is how we mark a litter as weaned
		// (but not genotyped)
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, weanDate, null, "SetWeanDate",
				"WeanDate", litter, newDateOnlyFormat.format(weanDate), null));
		// Set weaning remarks on litter
		if (remarks != null)
		{
			db.add(ct.createObservedValueWithProtocolApplication(invName, now, null, "SetRemark", "Remark", litter,
					remarks, null));
		}
		// Make animal, link to litter, parents and set wean dates etc.
		for (int animalNumber = 0; animalNumber < weanSize; animalNumber++)
		{
			String nrPart = "" + (startNumber + animalNumber);
			nrPart = ct.prependZeros(nrPart, 6);
			ObservationTarget animalToAdd = ct.createIndividual(invName, nameBase + nrPart);
			animalsToAddList.add(animalToAdd);
		}
		db.add(animalsToAddList);
		// Make or update name prefix entry
		ct.updatePrefix("animal", nameBase, startNumber + weanSize - 1);
		int animalNumber = 0;
		for (ObservationTarget animal : animalsToAddList)
		{
			String animalName = animal.getName();
			// TODO: link every value to a single Wean protocol application
			// instead of to its own one
			// Link to litter
			valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, weanDate, null, "SetLitter",
					"Litter", animalName, null, litter));
			// Link to parents using the Mother and Father measurements
			if (motherName != null && !motherName.equalsIgnoreCase("Unknown"))
			{
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, weanDate, null, "SetMother",
						"Mother", animalName, null, motherName));
			}
			if (fatherName != null && !fatherName.equalsIgnoreCase("Unknown"))
			{
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, weanDate, null, "SetFather",
						"Father", animalName, null, fatherName));
			}
			// Set line also on animal itself
			if (lineName != null)
			{
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, weanDate, null, "SetLine",
						"Line", animalName, null, lineName));
			}
			// Set responsible researcher
			if (respres != null)
			{
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, now, null,
						"SetResponsibleResearcher", "ResponsibleResearcher", animalName, respres, null));
			}
			// Set location
			if (locName != null)
			{
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, now, null, "SetLocation",
						"Location", animalName, null, locName));
			}
			// Set sex
			String sexName = "Male";
			if (animalNumber >= weanSizeMale)
			{
				if (animalNumber < weanSizeFemale + weanSizeMale)
				{
					sexName = "Female";
				}
				else
				{
					sexName = "UnknownSex";
				}
			}
			valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, weanDate, null, "SetSex", "Sex",
					animalName, null, sexName));
			// Set wean date on animal
			valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, weanDate, null, "SetWeanDate",
					"WeanDate", animalName, newDateOnlyFormat.format(weanDate), null));
			// Set 'Active' -> starts at wean date
			valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, weanDate, null, "SetActive",
					"Active", animalName, "Alive", null));
			// Set 'Date of Birth'
			valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, weanDate, null,
					"SetDateOfBirth", "DateOfBirth", animalName, litterBirthDateString, null));
			// Set species
			if (speciesName != null)
			{
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, weanDate, null,
						"SetSpecies", "Species", animalName, null, speciesName));
			}
			// Set animal type
			valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, weanDate, null, "SetAnimalType",
					"AnimalType", animalName, animalType, null));
			// Set source
			valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, weanDate, null, "SetSource",
					"Source", animalName, null, sourceName));
			// Set color based on mother's (can be changed during genotyping)
			if (color != null && !color.equals(""))
			{
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, weanDate, null, "SetColor",
						"Color", animalName, color, null));
			}
			// Set background based on mother's and father's (can be changed
			// during genotyping)
			String backgroundName = null;
			if (motherBackgroundName != null && fatherBackgroundName == null)
			{
				backgroundName = motherBackgroundName;
			}
			else if (motherBackgroundName == null && fatherBackgroundName != null)
			{
				backgroundName = fatherBackgroundName;
			}
			else if (motherBackgroundName != null && fatherBackgroundName != null)
			{
				// Make new or use existing cross background
				if (motherBackgroundName.equals(fatherBackgroundName))
				{
					backgroundName = fatherBackgroundName;
				}
				else
				{
					// check for cross background
					// split motherbckgr
					List<String> mbgNames = Arrays.asList(motherBackgroundName.split(" X "));
					List<String> fbgNames = Arrays.asList(fatherBackgroundName.split(" X "));

					int fbgSize = fbgNames.size();
					int mbgSize = mbgNames.size();
					List<String> newbgs;
					List<String> oldbgs;
					backgroundName = "";
					if (fbgSize + mbgSize == 2)
					{
						oldbgs = ListUtils.sum(mbgNames, fbgNames);
						Collections.sort(oldbgs);
						backgroundName = "";
						backgroundName += oldbgs.get(0);
						backgroundName += " X ";
						backgroundName += oldbgs.get(1);
					}
					else
					{
						// sort the single background options in categories same
						// and different
						if (fbgSize >= mbgSize)
						{
							oldbgs = fbgNames;
							newbgs = ListUtils.subtract(mbgNames, fbgNames);
						}
						else
						{
							oldbgs = mbgNames;
							newbgs = ListUtils.subtract(fbgNames, mbgNames);
						}

						int oldbgsSize = oldbgs.size();
						backgroundName += oldbgs.get(0);
						for (int i = 1; i < oldbgsSize; i++)
						{
							backgroundName += " X ";
							backgroundName += oldbgs.get(i);
						}
						if (newbgs.size() > 0)
						{
							Collections.sort(newbgs);
							for (int i = 0; i < oldbgsSize; i++)
							{
								backgroundName += " X ";
								backgroundName += newbgs.get(i);
							}
						}
					}
				}

				if (ct.getObservationTargetByName(backgroundName) == null)
				{
					// create the new mixed background if it does not exist
					ct.createPanel(invName, backgroundName);
					valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, weanDate, null,
							"SetTypeOfGroup", "TypeOfGroup", backgroundName, "Background", null));
					// set the correct species on the newly created background
					valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, weanDate, null,
							"SetSpecies", "Species", backgroundName, null, speciesName));
				}
			}
			if (backgroundName != null)
			{
				valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invName, weanDate, null,
						"SetBackground", "Background", animalName, null, backgroundName));
			}
			// Set genotype
			// TODO: Set based on mother's X father's and ONLY if you can know
			// the outcome, like homozygous double knockouts for both parents
			// For now just set all genestates of children to unknown.

			if (motherAllGeneMods != null && fatherAllGeneMods != null) // first
																		// handle
																		// situation
																		// where
																		// both
																		// parents
																		// have
																		// genemods
			{
				// compare the list of genemods of father and mother, and keep
				// unique ones.
				List<String> mfGeneModNames = new ArrayList<String>();
				for (ObservedValue mgm : motherAllGeneMods)
				{
					mfGeneModNames.add(mgm.getValue());
				}
				for (ObservedValue fgm : fatherAllGeneMods)
				{
					if (!mfGeneModNames.contains(fgm.getValue()))
					{
						mfGeneModNames.add(fgm.getValue());
					}

				}

				for (String gm : mfGeneModNames)
				{
					String paName = ct.makeProtocolApplication(invName, "SetGenotype");
					// Set gene mod name based on mother's (can be changed
					// during genotyping)
					valuesToAddList.add(ct.createObservedValue(invName, paName, weanDate, null, "GeneModification",
							animalName, gm, null));
					// Set gene state always on unknown, based on mother's (can
					// be changed during genotyping)
					valuesToAddList.add(ct.createObservedValue(invName, paName, weanDate, null, "GeneState",
							animalName, "unknown", null));
				}

			}
			else if (motherAllGeneMods != null && fatherAllGeneMods == null) // than
																				// handle
																				// situation
																				// where
																				// only
																				// the
																				// mother
																				// has
																				// genemods
			{
				for (ObservedValue mgm : motherAllGeneMods)
				{
					String paName = ct.makeProtocolApplication(invName, "SetGenotype");
					// Set gene mod name based on mother's (can be changed
					// during genotyping)
					valuesToAddList.add(ct.createObservedValue(invName, paName, weanDate, null, "GeneModification",
							animalName, mgm.getValue(), null));
					// Set gene state always on unknown, based on mother's (can
					// be changed during genotyping)
					valuesToAddList.add(ct.createObservedValue(invName, paName, weanDate, null, "GeneState",
							animalName, "unknown", null));
				}
			}
			else if (motherAllGeneMods == null && fatherAllGeneMods != null) // than
																				// handle
																				// situation
																				// where
																				// only
																				// the
																				// father
																				// has
																				// genemods
			{
				for (ObservedValue fgm : fatherAllGeneMods)
				{
					String paName = ct.makeProtocolApplication(invName, "SetGenotype");
					// Set gene mod name based on mother's (can be changed
					// during genotyping)
					valuesToAddList.add(ct.createObservedValue(invName, paName, weanDate, null, "GeneModification",
							animalName, fgm.getValue(), null));
					// Set gene state always on unknown, based on mother's (can
					// be changed during genotyping)
					valuesToAddList.add(ct.createObservedValue(invName, paName, weanDate, null, "GeneState",
							animalName, "unknown", null));
				}
			}
			else
			{
				// do not add any genotypes because there are none.
			}

			animalNumber++;
		}

		db.add(valuesToAddList);

		return weanSize;
	}

	public String getLitter()
	{
		return this.litter;
	}

	public String getSpeciesBase()
	{
		if (this.prefix != null)
		{
			return this.prefix;
		}
		return "";
	}

	public List<String> getBases()
	{
		return bases;
	}

	public List<ObservationTarget> getBackgroundList()
	{
		return backgroundList;
	}

	public void setBackgroundList(List<ObservationTarget> backgroundList)
	{
		this.backgroundList = backgroundList;
	}

	public List<String> getGeneNameList()
	{
		return geneNameList;
	}

	public void setLocationList(List<Location> locationList)
	{
		this.locationList = locationList;
	}

	public String getStartNumberHelperContent()
	{
		try
		{
			String helperContents = "";
			helperContents += (ct.getHighestNumberForPrefix("") + 1);
			helperContents += ";1";
			for (String base : this.bases)
			{
				if (!base.equals(""))
				{
					helperContents += (";" + (ct.getHighestNumberForPrefix(base) + 1));
				}
			}
			return helperContents;
		}
		catch (Exception e)
		{
			return "";
		}
	}

	public int getStartNumberForPreselectedBase()
	{
		try
		{
			return ct.getHighestNumberForPrefix(this.prefix) + 1;
		}
		catch (DatabaseException e)
		{
			return 1;
		}
	}

	public List<Individual> getAnimalsInLitter(String litterName, Database db)
	{
		List<Individual> returnList = new ArrayList<Individual>();
		try
		{
			Query<ObservedValue> q = db.query(ObservedValue.class);
			q.addRules(new QueryRule(ObservedValue.RELATION_NAME, Operator.EQUALS, litterName));
			q.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "Litter"));
			List<ObservedValue> valueList = q.find();
			for (ObservedValue value : valueList)
			{
				int animalId = value.getTarget_Id();
				returnList.add(ct.getIndividualById(animalId));
			}
			return returnList;
		}
		catch (Exception e)
		{
			// On fail, return empty list to UI
			return new ArrayList<Individual>();
		}
	}

	public List<Individual> getAnimalsInLitter(Database db)
	{
		try
		{
			return getAnimalsInLitter(this.litter, db);
		}
		catch (Exception e)
		{
			// On fail, return empty list to UI
			return new ArrayList<Individual>();
		}
	}

	public Date getAnimalBirthDate(String animalName)
	{
		try
		{
			String birthDateString = ct.getMostRecentValueAsString(animalName, "DateOfBirth");
			return newDateOnlyFormat.parse(birthDateString);
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public String getAnimalSex(String animalName)
	{
		try
		{
			return ct.getMostRecentValueAsXrefName(animalName, "Sex");
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public String getAnimalColor(String animalName)
	{
		try
		{
			if (ct.getMostRecentValueAsString(animalName, "Color") != null)
			{
				return ct.getMostRecentValueAsString(animalName, "Color");
			}
			else
			{
				return "";
			}
		}
		catch (Exception e)
		{
			return "unknown";
		}
	}

	public String getAnimalEarmark(String animalName)
	{
		try
		{
			if (ct.getMostRecentValueAsString(animalName, "Earmark") != null)
			{
				return ct.getMostRecentValueAsString(animalName, "Earmark");
			}
			else
			{
				return "";
			}
		}
		catch (Exception e)
		{
			return "";
		}
	}

	public String getAnimalBackground(String animalName)
	{
		try
		{
			return ct.getMostRecentValueAsXrefName(animalName, "Background");
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public String getAnimalGeneInfo(String measurementName, String animalName, int genoNr, Database db)
	{
		Query<ObservedValue> q = db.query(ObservedValue.class);
		q.addRules(new QueryRule(ObservedValue.TARGET_NAME, Operator.EQUALS, animalName));
		q.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, measurementName));
		List<ObservedValue> valueList;
		try
		{
			valueList = q.find();
		}
		catch (DatabaseException e)
		{
			return "";
		}
		if (valueList.size() > genoNr)
		{
			return valueList.get(genoNr).getValue();
		}
		else
		{
			return "";
		}
	}

	private int Genotype(Database db, MolgenisRequest request) throws Exception
	{
		Date now = new Date();

		String invName = ct.getObservationTargetByName(this.litter).getInvestigation_Name();

		List<String> investigationNames = ct.getAllUserInvestigationNames(this.getLogin().getUserName());

		// Set genotype date on litter -> this is how we mark a litter as
		// genotyped
		if (request.getString("genodate") == null)
		{
			throw new Exception("Genotype date not filled in - litter not genotyped");
		}

		Date genoDate = newDateOnlyFormat.parse(request.getString("genodate"));

		String genodate = dbFormat.format(genoDate);

		db.add(ct.createObservedValueWithProtocolApplication(invName, now, null, "SetGenotypeDate", "GenotypeDate",
				this.litter, genodate, null));
		// Set genotyping remarks on litter
		if (request.getString("remarks") != null)
		{
			db.add(ct.createObservedValueWithProtocolApplication(invName, now, null, "SetRemark", "Remark",
					this.litter, request.getString("remarks"), null));
		}

		int animalCount = updateLitterIndividuals(db, request, investigationNames, invName);

		return animalCount;
	}

	private int updateLitterIndividuals(Database db, MolgenisRequest request, List<String> investigationNames,
			String invName) throws DatabaseException, ParseException, IOException
	{

		int animalCount = 0;

		for (Individual animal : this.getAnimalsInLitter(db))
		{
			// Set sex
			String sexName = request.getString("1_" + animalCount);
			ObservedValue value = ct.getObservedValuesByTargetAndFeature(animal.getName(), "Sex", investigationNames,
					invName).get(0);
			value.setRelation(ct.getObservationTargetByName(sexName).getId());

			if (value.getProtocolApplication_Id() == null)
			{
				String paName = ct.makeProtocolApplication(invName, "SetSex");
				value.setProtocolApplication_Name(paName);
				db.add(value);
			}
			else
			{
				db.update(value);
			}
			// Set birth date
			String dob = request.getString("0_" + animalCount); // already in
																// new format
			value = ct
					.getObservedValuesByTargetAndFeature(animal.getName(), "DateOfBirth", investigationNames, invName)
					.get(0);
			value.setValue(dob);
			if (value.getProtocolApplication_Id() == null)
			{
				String paName = ct.makeProtocolApplication(invName, "SetDateOfBirth");
				value.setProtocolApplication_Name(paName);
				db.add(value);
			}
			else
			{
				db.update(value);
			}
			// Set color
			String color = request.getString("2_" + animalCount);
			value = ct.getObservedValuesByTargetAndFeature(animal.getName(), "Color", investigationNames, invName).get(
					0);
			value.setValue(color);
			if (value.getProtocolApplication_Id() == null)
			{
				String paName = ct.makeProtocolApplication(invName, "SetColor");
				value.setProtocolApplication_Name(paName);
				db.add(value);
			}
			else
			{
				db.update(value);
			}
			// Set earmark
			String earmark = request.getString("3_" + animalCount);
			value = ct.getObservedValuesByTargetAndFeature(animal.getName(), "Earmark", investigationNames, invName)
					.get(0);
			value.setValue(earmark);
			if (value.getProtocolApplication_Id() == null)
			{
				String paName = ct.makeProtocolApplication(invName, "SetEarmark");
				value.setProtocolApplication_Name(paName);
				db.add(value);
			}
			else
			{
				db.update(value);
			}
			// Set background
			// check for null

			String backgroundName = request.getString("4_" + animalCount);
			if (backgroundName != null && !backgroundName.equals(""))
			{
				value = ct.getObservedValuesByTargetAndFeature(animal.getName(), "Background", investigationNames,
						invName).get(0);
				value.setRelation(ct.getObservationTargetByName(backgroundName).getId());
				if (value.getProtocolApplication_Id() == null)
				{
					String paName = ct.makeProtocolApplication(invName, "SetBackground");
					value.setProtocolApplication_Name(paName);
					db.add(value);
				}
				else
				{
					db.update(value);
				}
			}

			// Set genotype(s)
			for (int genoNr = 0; genoNr < nrOfGenotypes; genoNr++)
			{
				int currCol = 5 + (genoNr * 2);
				String paName = ct.makeProtocolApplication(invName, "SetGenotype");
				String geneName = request.getString(currCol + "_" + animalCount);
				if (geneName == null || geneName.equals(""))
				{
					continue; // skip genes that have not been filled in
				}
				List<ObservedValue> valueList = ct.getObservedValuesByTargetAndFeature(animal.getName(),
						"GeneModification", investigationNames, invName);
				if (genoNr < valueList.size())
				{
					value = valueList.get(genoNr);
				}
				else
				{
					value = new ObservedValue();
					value.setFeature_Name("GeneModification");
					value.setTarget_Name(animal.getName());
					value.setInvestigation_Name(invName);
				}
				value.setValue(geneName);
				if (value.getProtocolApplication_Id() == null)
				{
					value.setProtocolApplication_Name(paName);
					db.add(value);
				}
				else
				{
					db.update(value);
				}
				String geneState = request.getString((currCol + 1) + "_" + animalCount);
				valueList = ct.getObservedValuesByTargetAndFeature(animal.getName(), "GeneState", investigationNames,
						invName);
				if (genoNr < valueList.size())
				{
					value = valueList.get(genoNr);
				}
				else
				{
					value = new ObservedValue();
					value.setFeature_Name("GeneState");
					value.setTarget_Name(animal.getName());
					value.setInvestigation_Name(invName);
				}
				value.setValue(geneState);
				if (value.getProtocolApplication_Id() == null)
				{
					value.setProtocolApplication_Name(paName);
					db.add(value);
				}
				else
				{
					db.update(value);
				}
			}

			animalCount++;
		}
		return animalCount;
	}

	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	private void storeGenotypeTable(Database db, MolgenisRequest request)
	{
		HtmlInput input;
		for (int animalCount = 0; animalCount < this.getAnimalsInLitter(db).size(); animalCount++)
		{

			if (request.getString("0_" + animalCount) != null)
			{
				String dob = request.getString("0_" + animalCount); // already
																	// in new
																	// format
				input = (HtmlInput) genotypeTable.getCell(0, animalCount);
				input.setValue(dob);
				genotypeTable.setCell(0, animalCount, input);
			}

			if (request.getString("1_" + animalCount) != null)
			{
				String sexName = request.getString("1_" + animalCount);
				input = (HtmlInput) genotypeTable.getCell(1, animalCount);
				input.setValue(sexName);
				genotypeTable.setCell(1, animalCount, input);
			}

			if (request.getString("2_" + animalCount) != null)
			{
				String color = request.getString("2_" + animalCount);
				input = (HtmlInput) genotypeTable.getCell(2, animalCount);
				input.setValue(color);
				genotypeTable.setCell(2, animalCount, input);
			}

			if (request.getString("3_" + animalCount) != null)
			{
				String earmark = request.getString("3_" + animalCount);
				input = (HtmlInput) genotypeTable.getCell(3, animalCount);
				input.setValue(earmark);
				genotypeTable.setCell(3, animalCount, input);
			}

			if (request.getString("4_" + animalCount) != null)
			{
				String backgroundName = request.getString("4_" + animalCount);
				input = (HtmlInput) genotypeTable.getCell(4, animalCount);
				input.setValue(backgroundName);
				genotypeTable.setCell(4, animalCount, input);
			}

			for (int genoNr = 0; genoNr < nrOfGenotypes; genoNr++)
			{
				int currCol = 5 + (genoNr * 2);

				if (request.getString(currCol + "_" + animalCount) != null)
				{
					String geneName = request.getString(currCol + "_" + animalCount);
					input = (HtmlInput) genotypeTable.getCell(currCol, animalCount);
					input.setValue(geneName);
					genotypeTable.setCell(currCol, animalCount, input);
				}

				if (request.getString((currCol + 1) + "_" + animalCount) != null)
				{
					String geneState = request.getString((currCol + 1) + "_" + animalCount);
					input = (HtmlInput) genotypeTable.getCell(currCol + 1, animalCount);
					input.setValue(geneState);
					genotypeTable.setCell(currCol + 1, animalCount, input);
				}
			}

			animalCount++;
		}
	}

	private void AddGenoCol(Database db, MolgenisRequest request)
	{
		nrOfGenotypes++;
		genotypeTable.addColumn("Gene modification");
		genotypeTable.addColumn("Gene state");
		int row = 0;
		for (Individual animal : getAnimalsInLitter(db))
		{
			String animalName = animal.getName();
			// Check for already selected genes for this animal
			List<String> selectedGenes = new ArrayList<String>();
			for (int genoNr = 0; genoNr < nrOfGenotypes - 1; genoNr++)
			{
				int currCol = 5 + (genoNr * 2);
				if (request.getString(currCol + "_" + row) != null)
				{
					selectedGenes.add(request.getString(currCol + "_" + row));
				}
			}
			// Make new gene mod name box
			int newCol = 5 + ((nrOfGenotypes - 1) * 2);
			SelectInput geneNameInput = new SelectInput(newCol + "_" + row);
			for (String geneName : this.geneNameList)
			{
				if (!selectedGenes.contains(geneName))
				{
					geneNameInput.addOption(geneName, geneName);
				}
			}
			geneNameInput.setValue(getAnimalGeneInfo("GeneModification", animalName, nrOfGenotypes, db));
			geneNameInput.setWidth(-1);
			genotypeTable.setCell(newCol, row, geneNameInput);
			// Make new gene state box
			SelectInput geneStateInput = new SelectInput((newCol + 1) + "_" + row);
			for (String geneState : this.geneStateList)
			{
				geneStateInput.addOption(geneState, geneState);
			}
			geneStateInput.setValue(getAnimalGeneInfo("GeneState", animalName, nrOfGenotypes, db));
			geneStateInput.setWidth(-1);
			genotypeTable.setCell(newCol + 1, row, geneStateInput);
			row++;
		}
	}

	public boolean getWean()
	{
		return this.wean;
	}

	public String getParentInfo()
	{
		return this.parentInfo;
	}

	private String getLineInfo(String parentgroupName) throws DatabaseException, ParseException
	{
		String lineName = ct.getMostRecentValueAsXrefName(parentgroupName, "Line");
		return lineName;
	}

	private String getGenoInfo(String animalName, Database db) throws DatabaseException, ParseException
	{
		String returnString = "";
		String animalBackgroundName = ct.getMostRecentValueAsXrefName(animalName, "Background");
		returnString += ("background: " + animalBackgroundName + "; ");
		Query<ObservedValue> q = db.query(ObservedValue.class);
		q.addRules(new QueryRule(ObservedValue.TARGET_NAME, Operator.EQUALS, animalName));
		q.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "GeneModification"));
		List<ObservedValue> valueList = q.find();
		if (valueList != null)
		{
			for (ObservedValue value : valueList)
			{
				String geneName = value.getValue();
				String geneState = "";
				Query<ObservedValue> geneStateQuery = db.query(ObservedValue.class);
				geneStateQuery.addRules(new QueryRule(ObservedValue.TARGET_NAME, Operator.EQUALS, animalName));
				geneStateQuery.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "GeneState"));
				geneStateQuery.addRules(new QueryRule(ObservedValue.PROTOCOLAPPLICATION, Operator.EQUALS, value
						.getProtocolApplication_Id()));
				List<ObservedValue> geneStateValueList = geneStateQuery.find();
				if (geneStateValueList != null && geneStateValueList.size() > 0)
				{
					geneState = geneStateValueList.get(0).getValue();
				}
				if (geneName == null || geneName.equals("null") || geneName.equals(""))
				{
					geneName = "unknown";
				}
				if (geneState == null || geneState.equals("null") || geneState.equals(""))
				{
					geneState = "unknown";
				}
				returnString += ("gene: " + geneName + ": " + geneState + "; ");
			}
		}
		if (returnString.length() > 0)
		{
			returnString = returnString.substring(0, returnString.length() - 2);
		}
		return returnString;
	}

	public String getEditTable()
	{
		return editTable.render();
	}

	public String getGenotypeTable()
	{
		return genotypeTable.render();
	}

	private void createDefCageLabels(Database db) throws LabelGeneratorException, DatabaseException, ParseException
	{

		// PDF file stuff
		boolean first = true;
		String lastSex = "";

		File tmpDir = new File(System.getProperty("java.io.tmpdir"));
		File pdfFile = new File(tmpDir.getAbsolutePath() + File.separatorChar + "deflabels.pdf");
		String filename = pdfFile.getName();
		LabelGenerator labelgenerator = new LabelGenerator(2);
		labelgenerator.startDocument(pdfFile);

		// Litter stuff
		String parentgroupName = ct.getMostRecentValueAsXrefName(litter, "Parentgroup");
		String line = this.getLineInfo(parentgroupName);
		String motherName = findParentForParentgroup(parentgroupName, "Mother", db);
		// String motherInfo = this.getGenoInfo(motherName, db);
		String fatherName = findParentForParentgroup(parentgroupName, "Father", db);
		// String fatherInfo = this.getGenoInfo(fatherName, db);

		List<String> elementLabelList;
		List<String> elementList;
		int sexctr = 0;
		for (Individual animal : this.getAnimalsInLitter(litter, db))
		{
			String animalName = animal.getName();
			elementList = new ArrayList<String>();
			elementLabelList = new ArrayList<String>();
			sexctr += 1;
			// Name / custom label
			elementLabelList.add("Name:");
			elementList.add(animalName);
			// Species
			elementLabelList.add("Species:");
			elementList.add(ct.getMostRecentValueAsXrefName(animalName, "Species"));
			// Sex
			String sex = ct.getMostRecentValueAsXrefName(animalName, "Sex");
			elementLabelList.add("Sex:");
			elementList.add(sex);
			if (first)
			{
				lastSex = sex;
			}
			// Earmark
			elementLabelList.add("Earmark:");
			elementList.add(ct.getMostRecentValueAsString(animalName, "Earmark"));
			// Line
			elementLabelList.add("Line:");
			elementList.add(line);
			// Background + GeneModification + GeneState
			elementLabelList.add("Background:");
			elementList.add(ct.getMostRecentValueAsXrefName(animalName, "Background"));

			// FIXME (can only show one gene modification....

			elementLabelList.add("Genotype:");
			String genotypeValue = "";
			String geneMod = ct.getMostRecentValueAsString(animalName, "GeneModification");
			String geneState = ct.getMostRecentValueAsString(animalName, "GeneState");
			if (geneMod != null)
			{
				// on request per 2013-08-08: suppress output of Genestate,
				// when state unknown.
				if (geneState.equalsIgnoreCase("unknown"))
				{
					genotypeValue += (geneMod + ":    ");
				}
				else
				{
					genotypeValue += (geneMod + ": " + geneState);
				}
			}
			elementList.add(genotypeValue);

			// Birthdate
			elementLabelList.add("Birthdate:");
			elementList.add(ct.getMostRecentValueAsString(animalName, "DateOfBirth"));
			// Geno mother
			elementLabelList.add("Father:\nMother:");
			String fatherValue = ct.getMostRecentValueAsXrefName(animalName, "Father");
			String motherValue = ct.getMostRecentValueAsXrefName(animalName, "Mother");
			if (fatherValue == null)
			{
				fatherValue = "";
			}
			if (motherValue == null)
			{
				motherValue = "";
			}
			elementList.add(fatherValue + "\n" + motherValue);
			// litter
			elementLabelList.add("Litter:");
			elementList.add(litter);
			// Add DEC nr, if present, or empty if not
			elementLabelList.add("Researcher: ");
			elementList.add(ct.getMostRecentValueAsString(animalName, "ResponsibleResearcher"));

			elementLabelList.add("DEC:");
			String decNr = ct.getMostRecentValueAsString(animalName, "DecNr");
			String expNr = ct.getMostRecentValueAsString(animalName, "ExperimentNr");
			String decInfo = (decNr != null ? decNr : "") + " " + (expNr != null ? expNr : "");
			elementList.add(decInfo);
			elementLabelList.add("Remarks");
			elementList.add("\n\n\n\n\n");

			if (sex.equals(lastSex))
			{
				System.out.println(sexctr + " equals: " + sex);
				labelgenerator.addLabelToDocument(elementLabelList, elementList);
			}
			else
			{
				System.out.println(sexctr + " not equals: " + sex);
				// add empty label on odd labelnr.
				if ((sexctr - 1) % 2 != 0)
				{
					labelgenerator.addLabelToDocument(new ArrayList<String>(), new ArrayList<String>());
				}
				labelgenerator.finishPage();
				labelgenerator.nextPage();
				sexctr = 1; // reset the sexcounter to keep track of odd and
							// even numbers.
				labelgenerator.addLabelToDocument(elementLabelList, elementList);
			}

			lastSex = sex;

			if (first)
			{
				first = false;
			}
		}

		if (sexctr % 2 != 0)
		{
			labelgenerator.addLabelToDocument(new ArrayList<String>(), new ArrayList<String>());
		}

		labelgenerator.finishPage();
		labelgenerator.finishDocument();
		this.setLabelDownloadLink("<a href=\"tmpfile/" + filename
				+ "\" target=\"blank\">Download cage labels as pdf</a>");
	}

	public void setGeneNameList(List<String> geneNameList)
	{
		this.geneNameList = geneNameList;
	}

	public List<String> getGeneStateList()
	{
		return geneStateList;
	}

	public void setGeneStateList(List<String> geneStateList)
	{
		this.geneStateList = geneStateList;
	}

	public List<ObservationTarget> getSexList()
	{
		return sexList;
	}

	public void setSexList(List<ObservationTarget> sexList)
	{
		this.sexList = sexList;
	}

	public List<String> getColorList()
	{
		return colorList;
	}

	public void setColorList(List<String> colorList)
	{
		this.colorList = colorList;
	}

	public List<Category> getEarmarkList()
	{
		return earmarkList;
	}

	public void setEarmarkList(List<Category> earmarkList)
	{
		this.earmarkList = earmarkList;
	}

	public List<Location> getLocationList()
	{
		return locationList;
	}

	public String getLabelDownloadLink()
	{
		return labelDownloadLink;
	}

	public void setLabelDownloadLink(String labelDownloadLink)
	{
		this.labelDownloadLink = labelDownloadLink;
	}

	public String getWeandate()
	{
		if (weandate != null)
		{
			return weandate;
		}
		return dbFormat.format(new Date());
	}

	public String getGenodate()
	{
		return dbFormat.format(new Date());
	}

	public int getNumberOfPG()
	{
		return numberOfPG;
	}

	public List<String> getPgName()
	{
		return pgName;
	}

	// public HashMap<String, List<String>> getHashMothers()
	// {
	// return hashMothers;
	// }

	public Map<Integer, List<String>> getHashMothers()
	{
		return hashMothers;
	}

	public List<String> getMotherElement(Integer key)
	{
		return hashMothers.get(key);
	}

	public List<String> getFatherElement(Integer key)
	{
		return hashFathers.get(key);
	}

	public String getSex()
	{
		return sex;
	}

	public Map<Integer, List<String>> getHashFathers()
	{
		return hashFathers;
	}

	public boolean isStillToWeanYN()
	{
		return stillToWeanYN;
	}

	public boolean isStillToGenotypeYN()
	{
		return stillToGenotypeYN;
	}

	public boolean isAddNew()
	{
		return addNew;
	}

	public void setAddNew(boolean addNew)
	{
		this.addNew = addNew;
	}

	public String getMotherLocation()
	{
		return motherLocation;
	}

	public void setMotherLocation(String motherLocation)
	{
		this.motherLocation = motherLocation;
	}
}

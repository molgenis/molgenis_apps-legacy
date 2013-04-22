package org.molgenis.animaldb.plugins.animal;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.molgenis.animaldb.commonservice.CommonService;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.server.MolgenisRequest;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.html.DateInput;
import org.molgenis.framework.ui.html.JQueryDataTable;
import org.molgenis.framework.ui.html.SelectInput;
import org.molgenis.framework.ui.html.StringInput;
import org.molgenis.framework.ui.html.Table;
import org.molgenis.matrix.component.MatrixViewer;
import org.molgenis.matrix.component.SliceablePhenoMatrix;
import org.molgenis.matrix.component.general.MatrixQueryRule;
import org.molgenis.pheno.Category;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.util.Entity;

/**
 * ViewFamilyController takes care of all user requests and application logic.
 * 
 * <li>Each user request is handled by its own method based action=methodName.
 * <li>MOLGENIS takes care of db.commits and catches exceptions to show to the
 * user <li>ViewFamilyModel holds application state and business logic on top of
 * domain model. Get it via this.getModel()/setModel(..) <li>ViewFamilyView
 * holds the template to show the layout. Get/set it via
 * this.getView()/setView(..).
 */
public class EditAnimalPlugin extends PluginModel<Entity>
{
	private static final long serialVersionUID = -7609580651170222454L;
	private List<Integer> animalIdList;
	private String action = "init";
	private String info = "";
	private CommonService cs = CommonService.getInstance();
	MatrixViewer animalMatrixViewer = null;
	static String ANIMALMATRIX = "animalmatrix";
	private SimpleDateFormat newDateOnlyFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
	private String animalMatrixRendered;
	private boolean loaded = false;
	private Table editTable = null;
	private List<ObservationTarget> sexList;
	private List<ObservationTarget> speciesList;
	private List<ObservationTarget> sourceList;
	private List<Category> animalTypeList;
	private List<String> colorList;
	private List<String> earmarkList;
	private List<String> geneModList;
	private List<String> geneStateList;
	private List<Individual> listOfSelectedIndividuals = new ArrayList<Individual>();

	// map to store tablecell locations of measurements
	private Map<String, String> fpMap = new HashMap<String, String>();

	public EditAnimalPlugin(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	public String getCustomHtmlHeaders()
	{
		return "<script src=\"res/jquery-plugins/ctnotify/lib/jquery.ctNotify.js\" language=\"javascript\"></script>\n"
				+ "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/jquery-plugins/ctnotify/lib/jquery.ctNotify.css\">"
				+ "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/animaldb.css\">";
	}

	// Animal related methods:
	public List<Integer> getAnimalIdList()
	{
		return animalIdList;
	}

	public void setAnimalIdList(List<Integer> animalIdList)
	{
		this.animalIdList = animalIdList;
	}

	public String getAnimalName(Integer id)
	{
		try
		{
			return cs.getObservationTargetLabel(id);
		}
		catch (Exception e)
		{
			return id.toString();
		}
	}

	public String getAnimalMatrix()
	{
		if (animalMatrixRendered != null)
		{
			return animalMatrixRendered;
		}
		return "Error - animal matrix not initialized";
	}

	/**
	 * At each page view: reload data from database into model and/or change.
	 * 
	 * Exceptions will be caught, logged and shown to the user automatically via
	 * setMessages(). All db actions are within one transaction.
	 */
	@Override
	public void reload(Database db)
	{
		cs.setDatabase(db);
		cs.makeObservationTargetNameMap(this.getLogin().getUserName(), false);
		if (animalMatrixViewer != null)
		{
			animalMatrixViewer.setDatabase(db);
		}
		else
		{
			try
			{
				List<String> investigationNames = cs.getAllUserInvestigationNames(this.getLogin().getUserName());
				List<String> measurementsToShow = new ArrayList<String>();
				measurementsToShow.add("Active");
				measurementsToShow.add("Background");
				measurementsToShow.add("DateOfBirth");
				measurementsToShow.add("AnimalType");
				measurementsToShow.add("Earmark");
				measurementsToShow.add("GeneModification");
				measurementsToShow.add("GeneState");
				measurementsToShow.add("Line");
				measurementsToShow.add("Litter");
				measurementsToShow.add("Location");
				measurementsToShow.add("Sex");
				measurementsToShow.add("Species");
				List<MatrixQueryRule> filterRules = new ArrayList<MatrixQueryRule>();
				filterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.rowHeader, Individual.INVESTIGATION_NAME,
						Operator.IN, investigationNames));
				filterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.colValueProperty, cs
						.getMeasurementId("Active"), ObservedValue.VALUE, Operator.EQUALS, "Alive"));
				animalMatrixViewer = new MatrixViewer(this, ANIMALMATRIX,
						new SliceablePhenoMatrix<Individual, Measurement>(Individual.class, Measurement.class), true,
						2, true, false, filterRules, new MatrixQueryRule(MatrixQueryRule.Type.colHeader,
								Measurement.NAME, Operator.IN, measurementsToShow));
				animalMatrixViewer.setFilterVisibility(false);
				animalMatrixViewer.setDatabase(db);
			}
			catch (Exception e)
			{
				this.setError("Could not initialize matrix");
			}
		}

		animalMatrixRendered = animalMatrixViewer.render();
		List<String> investigationNames = cs.getAllUserInvestigationNames(this.getLogin().getUserName());

		if (!loaded)
		{
			try
			{
				// fill some global lookup lists.
				this.setSexList(cs.getAllMarkedPanels("Sex", investigationNames));
				this.setSpeciesList(cs.getAllMarkedPanels("Species", investigationNames));
				this.setAnimalTypeList(cs.getAllCodesForFeature("AnimalType"));
				this.setSourceList(cs.getAllMarkedPanels("Source", investigationNames));
				this.setColorList(cs.getAllCodesForFeatureAsStrings("Color"));
				this.setEarmarkList(cs.getAllCodesForFeatureAsStrings("Earmark"));
				this.setGeneModList(cs.getAllCodesForFeatureAsStrings("GeneModification"));
				this.setGeneStateList(cs.getAllCodesForFeatureAsStrings("GeneState"));

				loaded = true;
			}
			catch (DatabaseException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (ParseException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	@Override
	public String getViewName()
	{
		return "org_molgenis_animaldb_plugins_animal_EditAnimalPlugin";
	}

	public String getEditTable()
	{
		return editTable.render();
	}

	@Override
	public String getViewTemplate()
	{
		return "org/molgenis/animaldb/plugins/animal/EditAnimalPlugin.ftl";
	}

	@Override
	public void handleRequest(Database db, MolgenisRequest request)
	{
		cs.setDatabase(db);
		if (animalMatrixViewer != null)
		{
			animalMatrixViewer.setDatabase(db);
		}
		try
		{
			action = request.getString("__action");

			if (action.startsWith(animalMatrixViewer.getName()))
			{
				animalMatrixViewer.handleRequest(db, request);
			}

			if (action.equals("editAnimals"))
			{
				List<Individual> selectedIndividuals = new ArrayList<Individual>();
				List<Individual> rows = (List<Individual>) animalMatrixViewer.getSelection(db);

				int rowCnt = 0;
				int selRowCnt = 0;
				for (Individual row : rows)
				{
					if (request.getBoolean(ANIMALMATRIX + "_selected_" + rowCnt) != null)
					{
						selectedIndividuals.add(row);
						selRowCnt++;
					}
					rowCnt++;
				}
				if (selRowCnt == 0)
				{
					this.setMessages(new ScreenMessage("No animals selected", false));
					this.action = "start";
				}
				else
				{
					listOfSelectedIndividuals = selectedIndividuals;
					makeTable(db, selectedIndividuals);
				}
			}
			if (action.equals("saveAnimals"))
			{
				cs.setDatabase(db);

				saveAnimalToDB(db, request, listOfSelectedIndividuals);
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	public void makeTable(Database db, List<Individual> obsTargets) throws ParseException
	{
		List<String> investigationNames = cs.getAllUserInvestigationNames(this.getLogin().getUserName());
		Map<String, String> observableFeat = new HashMap<String, String>();
		int row = 0;
		try
		{

			editTable = new JQueryDataTable("EditTable", "");

			// editTable.addColumn("Active");
			editTable.addColumn("Background");
			this.fpMap.put("Background", "1_");
			editTable.addColumn("Color");
			this.fpMap.put("Color", "2_");
			editTable.addColumn("DateOfBirth");
			this.fpMap.put("DateOfBirth", "3_");
			editTable.addColumn("Earmark");
			this.fpMap.put("EarMark", "4_");
			editTable.addColumn("Line");
			this.fpMap.put("Line", "5_");
			editTable.addColumn("Sex");
			this.fpMap.put("Sex", "6_");
			editTable.addColumn("ResponsibleResearcher");
			this.fpMap.put("ResponsibleResearcher", "7_");
			editTable.addColumn("WeanDate");
			this.fpMap.put("WeanDate", "8_");
			editTable.addColumn("GeneModification");
			this.fpMap.put("GeneModification", "9_");
			editTable.addColumn("GeneState");
			this.fpMap.put("GeneState", "10_");

			// these fields are only available for editing by admin.
			if (this.getLogin().getUserName().equalsIgnoreCase("admin"))
			{
				editTable.addColumn("AnimalType");
				this.fpMap.put("AnimalType", "100_");
				editTable.addColumn("Source");
				this.fpMap.put("Source", "101_");
				editTable.addColumn("Species");
				this.fpMap.put("Species", "102_");
			}

			// add a row for each selected animal.
			// for (Individual e : obsTargets)
			// {
			// editTable.addColumn(e);
			// editTable.addRow(e.getName());
			// }

			for (Individual e : obsTargets)
			{
				String invName = cs.getObservationTargetByName(e.getName()).getInvestigation_Name();
				// add a row in the eit table for each selected animal.
				editTable.addRow(e.getName());

				List<ObservedValue> listObservedValues = db.find(ObservedValue.class, new QueryRule(
						ObservedValue.TARGET_NAME, Operator.EQUALS, e.getName()));
				for (ObservedValue val : listObservedValues)
				{
					observableFeat.put(val.getFeature_Name(), val.getValue());
				}

				// Add all the inputs for each column.
				// AnimalType

				// Background
				List<ObservationTarget> bckgrlist = new ArrayList<ObservationTarget>();

				for (ObservationTarget b : cs.getAllMarkedPanels("Background", investigationNames))
				{
					// Only show if background belongs to chosen species
					if (cs.getMostRecentValueAsXrefName(b.getName(), "Species").equals(getAnimalSpecies(e.getName())))
					{
						bckgrlist.add(b);
					}
				}

				SelectInput backgroundInput = new SelectInput(fpMap.get("Background") + e.getName());
				backgroundInput.setId(fpMap.get("Background") + e.getName());
				backgroundInput.addOption("", "");
				for (ObservationTarget background : bckgrlist)
				{
					backgroundInput.addOption(background.getName(), background.getName());
				}

				backgroundInput.setValue(getAnimalBackground(e.getName()));
				backgroundInput.setWidth(-1);
				editTable.setCell(0, row, backgroundInput);

				// Color
				SelectInput colorInput = new SelectInput(fpMap.get("Color") + e.getName());
				colorInput.setId(fpMap.get("Color") + e.getName());

				for (String color : this.colorList)
				{
					colorInput.addOption(color, color);
				}
				colorInput.setValue(getAnimalColor(e.getName()));
				colorInput.setWidth(-1);
				editTable.setCell(1, row, colorInput);

				// DateOfbirth
				DateInput dateOfBirthInput = new DateInput(this.fpMap.get("DateOfBirth") + e.getName());
				dateOfBirthInput.setId(this.fpMap.get("DateOfBirth") + e.getName());
				dateOfBirthInput.setDateFormat("yyyy-MM-dd");
				dateOfBirthInput.setValue(getAnimalBirthDate(e.getName()));
				dateOfBirthInput
						.setJqueryproperties("dateFormat: 'yy-mm-dd', changeMonth: true, changeYear: true, showButtonPanel: true, numberOfMonths: 1");
				editTable.setCell(2, row, dateOfBirthInput);

				// Earmark
				SelectInput earmarkInput = new SelectInput(this.fpMap.get("Earmark") + e.getName());
				earmarkInput.setId(this.fpMap.get("Earmark") + e.getName());

				for (String earmark : this.earmarkList)
				{
					earmarkInput.addOption(earmark, earmark);
				}
				earmarkInput.addOption("", "");
				earmarkInput.setValue(getAnimalEarmark(e.getName()));
				earmarkInput.setWidth(-1);
				editTable.setCell(3, row, earmarkInput);

				// Line
				SelectInput lineInput = new SelectInput(this.fpMap.get("Line") + e.getName());
				lineInput.setId(this.fpMap.get("Line") + e.getName());
				lineInput.addOption("", "");

				for (ObservationTarget b : cs.getAllMarkedPanels("Line", investigationNames))
				{
					// Only show if line belongs to chosen species
					if (cs.getMostRecentValueAsXrefName(b.getName(), "Species").equals(getAnimalSpecies(e.getName())))
					{
						lineInput.addOption(b.getName(), b.getName());
					}
				}

				lineInput.setValue(getAnimalLine(e.getName()));
				lineInput.setWidth(-1);
				editTable.setCell(4, row, lineInput);

				// Sex
				SelectInput sexInput = new SelectInput(this.fpMap.get("Sex") + e.getName());
				sexInput.setId(this.fpMap.get("Sex") + e.getName());
				for (ObservationTarget sex : this.sexList)
				{
					sexInput.addOption(sex.getName(), sex.getName());
				}
				sexInput.setValue(getAnimalSex(e.getName()));
				sexInput.setWidth(-1);
				editTable.setCell(5, row, sexInput);

				// Responsible researcher
				StringInput respresInput = new StringInput(this.fpMap.get("ResponsibleResearcher") + e.getName());
				respresInput.setId(this.fpMap.get("ResponsibleResearcher") + e.getName());

				respresInput.setValue(getAnimalResponsibleResearcher(e.getName()));

				editTable.setCell(6, row, respresInput);

				// weandate
				DateInput weandateInput = new DateInput(this.fpMap.get("WeanDate") + e.getName());
				weandateInput.setId(this.fpMap.get("WeanDate") + e.getName());
				weandateInput.setDateFormat("yyyy-MM-dd");
				weandateInput.setValue(getAnimalWeanDate(e.getName()));
				weandateInput
						.setJqueryproperties("dateFormat: 'yy-mm-dd', changeMonth: true, changeYear: true, showButtonPanel: true, numberOfMonths: 1");
				editTable.setCell(7, row, weandateInput);

				// GeneModification
				List<ObservedValue> allValuesGeneMod = cs.getObservedValuesByTargetAndFeature(e.getName(),
						"GeneModification", investigationNames, invName);
				int countGeneMod = 0;
				List<SelectInput> listOfSelectInput = new ArrayList<SelectInput>();
				for (ObservedValue value : allValuesGeneMod)
				{
					SelectInput geneModInput = new SelectInput(this.fpMap.get("GeneModification") + e.getName() + "_"
							+ countGeneMod);
					geneModInput.setId(this.fpMap.get("GeneModification") + e.getName() + "_" + countGeneMod);

					for (String geneMod : this.geneModList)
					{
						geneModInput.addOption(geneMod, geneMod);
					}
					geneModInput.setValue(value.getValue());
					geneModInput.setWidth(-1);

					listOfSelectInput.add(geneModInput);
					countGeneMod++;
				}

				editTable.setCell(8, row, listOfSelectInput);

				// GeneState
				List<ObservedValue> allValues = cs.getObservedValuesByTargetAndFeature(e.getName(), "GeneState",
						investigationNames, invName);
				int countGeneState = 0;
				listOfSelectInput = new ArrayList<SelectInput>();
				for (ObservedValue value : allValues)
				{
					SelectInput geneStateInput = new SelectInput(this.fpMap.get("GeneState") + e.getName() + "_"
							+ countGeneState);
					geneStateInput.setId(this.fpMap.get("GeneState") + e.getName() + "_" + countGeneState);

					for (String geneMod : this.geneStateList)
					{
						geneStateInput.addOption(geneMod, geneMod);
					}
					geneStateInput.setValue(value.getValue());
					geneStateInput.setWidth(-1);

					listOfSelectInput.add(geneStateInput);
					countGeneState++;
				}

				editTable.setCell(9, row, listOfSelectInput);

				if (this.getLogin().getUserName().equalsIgnoreCase("admin"))
				{
					// AnimalType
					SelectInput animalTypeInput = new SelectInput(fpMap.get("AnimalType") + e.getName());
					animalTypeInput.setId(fpMap.get("AnimalType") + e.getName());

					for (Category animalType : this.animalTypeList)
					{
						animalTypeInput.addOption(animalType.getDescription(), animalType.getDescription());
					}
					// animalTypeInput.setValue(getAnimalType(e.getName()));
					animalTypeInput.setValue(cs.getMostRecentValueAsString(e.getName(), "AnimalType"));
					animalTypeInput.setWidth(-1);
					editTable.setCell(10, row, animalTypeInput);

					// Source
					SelectInput sourceInput = new SelectInput(this.fpMap.get("Source") + e.getName());
					sourceInput.setId(this.fpMap.get("Source") + e.getName());

					for (ObservationTarget source : this.sourceList)
					{
						sourceInput.addOption(source.getName(), source.getName());
					}
					sourceInput.setValue(getAnimalSource(e.getName()));
					sourceInput.setWidth(-1);
					editTable.setCell(11, row, sourceInput);

					// Species
					SelectInput speciesInput = new SelectInput(this.fpMap.get("Species") + e.getName());
					speciesInput.setId(this.fpMap.get("Species") + e.getName());
					for (ObservationTarget species : this.speciesList)
					{
						speciesInput.addOption(species.getName(), species.getName());
					}
					speciesInput.setValue(getAnimalSpecies(e.getName()));
					speciesInput.setWidth(-1);
					editTable.setCell(12, row, speciesInput);

				}
				row++;

			}

		}
		catch (DatabaseException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public void saveAnimalToDB(Database db, MolgenisRequest request, List<Individual> obsTargets)
			throws DatabaseException, ParseException, IOException
	{

		String invName = null;

		List<String> investigationNames = cs.getAllUserInvestigationNames(this.getLogin().getUserName());
		Date now = new Date();

		for (Individual e : obsTargets)
		{
			invName = cs.getObservationTargetByName(e.getName()).getInvestigation_Name();
			ObservedValue value = new ObservedValue();

			// Background
			String backgroundName = request.getString(this.fpMap.get("Background") + e.getName());
			value = cs.getObservedValuesByTargetAndFeature(e.getName(), "Background", investigationNames, invName).get(
					0);

			if (value.getProtocolApplication_Id() == null)
			{
				if (backgroundName != null && !backgroundName.equals(""))
				{
					db.add(cs.createObservedValueWithProtocolApplication(invName, now, null, "SetBackground",
							"Background", e.getName(), null, backgroundName));
				}
			}
			else
			{
				if (backgroundName == null || backgroundName.equals(""))
				{
					// delete value to make it empty
					db.remove(value);
					db.remove(cs.getProtocolApplicationByName(value.getProtocolApplication_Name()));
				}
				else
				{
					value.setRelation(cs.getObservationTargetByName(backgroundName).getId());
					db.update(value);
				}
			}

			// Color
			String colorName = request.getString(this.fpMap.get("Color") + e.getName());
			value = cs.getObservedValuesByTargetAndFeature(e.getName(), "Color", investigationNames, invName).get(0);
			value.setValue(colorName);
			if (value.getProtocolApplication_Id() == null)
			{
				if (colorName != null && !colorName.equals(""))
				{
					db.add(cs.createObservedValueWithProtocolApplication(invName, now, null, "SetColor", "Color",
							e.getName(), colorName, null));
				}
			}
			else
			{
				if (colorName == null || colorName.equals(""))
				{
					// delete value to make it empty
					db.remove(value);
					db.remove(cs.getProtocolApplicationByName(value.getProtocolApplication_Name()));
				}
				else
				{
					value.setValue(colorName);
					db.update(value);
				}
			}

			// Date of Birth
			String birthName = request.getString(this.fpMap.get("DateOfBirth") + e.getName());
			value = cs.getObservedValuesByTargetAndFeature(e.getName(), "DateOfBirth", investigationNames, invName)
					.get(0);
			value.setValue(birthName);
			if (value.getProtocolApplication_Id() == null)
			{
				if (birthName != null && !birthName.equals(""))
				{
					db.add(cs.createObservedValueWithProtocolApplication(invName, now, null, "SetDateOfBirth",
							"DateOfBirth", e.getName(), birthName, null));
				}
			}
			else
			{
				if (birthName == null || birthName.equals(""))
				{
					// delete value to make it empty
					db.remove(value);
					db.remove(cs.getProtocolApplicationByName(value.getProtocolApplication_Name()));
				}
				else
				{
					value.setValue(birthName);
					db.update(value);
				}
			}

			// Earmark
			String earmarkName = request.getString(this.fpMap.get("Earmark") + e.getName());
			value = cs.getObservedValuesByTargetAndFeature(e.getName(), "Earmark", investigationNames, invName).get(0);
			if (value.getProtocolApplication_Id() == null)
			{
				if (earmarkName != null && !earmarkName.equals(""))
				{
					db.add(cs.createObservedValueWithProtocolApplication(invName, now, null, "SetEarmark", "Earmark",
							e.getName(), earmarkName, null));
				}
			}
			else
			{
				if (earmarkName == null || earmarkName.equals(""))
				{
					// delete value to make it empty
					db.remove(value);
					db.remove(cs.getProtocolApplicationByName(value.getProtocolApplication_Name()));
				}
				else
				{
					value.setValue(earmarkName);
					db.update(value);
				}
			}

			// Line
			String lineName = request.getString(this.fpMap.get("Line") + e.getName());
			value = cs.getObservedValuesByTargetAndFeature(e.getName(), "Line", investigationNames, invName).get(0);

			// value.setRelation(cs.getObservationTargetByName(lineName).getId());
			if (value.getProtocolApplication_Id() == null)
			{
				if (lineName != null && !lineName.equals(""))
				{
					db.add(cs.createObservedValueWithProtocolApplication(invName, now, null, "SetLine", "Line",
							e.getName(), null, lineName));
				}
			}
			else
			{
				if (lineName == null || lineName.equals(""))
				{
					// delete value to make it empty
					db.remove(value);
					db.remove(cs.getProtocolApplicationByName(value.getProtocolApplication_Name()));
				}
				else
				{
					value.setRelation(cs.getObservationTargetByName(lineName).getId());
					db.update(value);
				}
			}

			// Sex
			String sexName = request.getString(this.fpMap.get("Sex") + e.getName());
			value = cs.getObservedValuesByTargetAndFeature(e.getName(), "Sex", investigationNames, invName).get(0);
			value.setRelation(cs.getObservationTargetByName(sexName).getId());
			db.update(value);

			// ResponsibleResearcher
			String responsibleResearcher = request.getString(this.fpMap.get("ResponsibleResearcher") + e.getName());
			value = cs.getObservedValuesByTargetAndFeature(e.getName(), "ResponsibleResearcher", investigationNames,
					invName).get(0);
			value.setValue(responsibleResearcher);
			if (value.getProtocolApplication_Id() == null)
			{
				if (responsibleResearcher != null && !responsibleResearcher.equals(""))
				{
					db.add(cs.createObservedValueWithProtocolApplication(invName, now, null,
							"SetResponsibleResearcher", "ResponsibleResearcher", e.getName(), responsibleResearcher,
							null));
				}
			}
			else
			{
				if (responsibleResearcher == null || responsibleResearcher.equals(""))
				{
					// delete value to make it empty
					db.remove(value);
					db.remove(cs.getProtocolApplicationByName(value.getProtocolApplication_Name()));
				}
				else
				{
					value.setValue(responsibleResearcher);
					db.update(value);
				}
			}

			// Date of Birth
			String weanDate = request.getString(this.fpMap.get("WeanDate") + e.getName());
			value = cs.getObservedValuesByTargetAndFeature(e.getName(), "WeanDate", investigationNames, invName).get(0);
			value.setValue(weanDate);
			if (value.getProtocolApplication_Id() == null)
			{
				if (weanDate != null && !weanDate.equals(""))
				{
					db.add(cs.createObservedValueWithProtocolApplication(invName, now, null, "SetWeanDate", "WeanDate",
							e.getName(), weanDate, null));
				}
			}
			else
			{
				if (weanDate == null || weanDate.equals(""))
				{
					// delete value to make it empty
					db.remove(value);
					db.remove(cs.getProtocolApplicationByName(value.getProtocolApplication_Name()));
				}
				else
				{
					value.setValue(weanDate);
					db.update(value);
				}
			}

			// GeneModificiation
			List<ObservedValue> allValuesGeneModification = cs.getObservedValuesByTargetAndFeature(e.getName(),
					"GeneModification", investigationNames, invName);
			int countGeneMod = 0;

			for (ObservedValue avgm : allValuesGeneModification)
			{

				value = avgm;

				String geneModification = request.getString((this.fpMap.get("GeneModification")) + e.getName() + "_"
						+ countGeneMod);
				value.setValue(geneModification);
				db.update(value);
				countGeneMod++;

			}
			// GeneModificiation
			List<ObservedValue> allValuesGeneState = cs.getObservedValuesByTargetAndFeature(e.getName(), "GeneState",
					investigationNames, invName);
			int countGeneState = 0;

			for (ObservedValue avgm : allValuesGeneState)
			{

				value = avgm;

				String geneState = request
						.getString((this.fpMap.get("GeneState")) + e.getName() + "_" + countGeneState);
				value.setValue(geneState);
				db.update(value);
				countGeneState++;

			}
			// if (value.getProtocolApplication_Id() == null)
			// {
			// if (geneModification != null && !geneModification.equals(""))
			// {
			// db.add(cs.createObservedValueWithProtocolApplication(invName,
			// now, null, "SetEarmark", "Earmark",
			// e.getName(), earmarkName, null));
			// }
			// }
			// else
			// {
			// if (geneModification == null || geneModification.equals(""))
			// {
			// // delete value to make it empty
			// db.remove(value);
			// db.remove(cs.getProtocolApplicationByName(value.getProtocolApplication_Name()));
			// }
			// else
			// {
			// value.setValue(geneModification);
			// db.update(value);
			// }
			// }

			if (this.getLogin().getUserName().equalsIgnoreCase("admin"))
			{

				// AnimalType
				String animalTypeName = request.getString(this.fpMap.get("AnimalType") + e.getName());
				value = cs.getObservedValuesByTargetAndFeature(e.getName(), "AnimalType", investigationNames, invName)
						.get(0);
				value.setValue(animalTypeName);

				if (value.getProtocolApplication_Id() == null)
				{
					String paName = cs.makeProtocolApplication(invName, "SetAnimalType");
					value.setProtocolApplication_Name(paName);
					db.add(value);
				}
				else
				{

					db.update(value);
				}

				// Source
				String sourceName = request.getString(this.fpMap.get("Source") + e.getName());
				value = cs.getObservedValuesByTargetAndFeature(e.getName(), "Source", investigationNames, invName).get(
						0);
				value.setRelation(cs.getObservationTargetByName(sourceName).getId());

				if (value.getProtocolApplication_Id() == null)
				{
					String paName = cs.makeProtocolApplication(invName, "SetSource");
					value.setProtocolApplication_Name(paName);
					db.add(value);
				}
				else
				{
					db.update(value);
				}

				// Species
				String speciesName = request.getString(this.fpMap.get("Species") + e.getName());
				ObservationTarget species = db.find(ObservationTarget.class,
						new QueryRule(ObservationTarget.NAME, Operator.EQUALS, speciesName)).get(0);
				value = cs.getObservedValuesByTargetAndFeature(e.getName(), "Species", investigationNames, invName)
						.get(0); //
				value.setRelation(cs.getObservationTargetByName(speciesName).getId());
				value.setRelation(cs.getObservationTargetById(species.getId()).getId());
				db.update(value);
			}
		}
	}

	public void setAction(String action)
	{
		this.action = action;
	}

	public String getAction()
	{
		return action;
	}

	public void setInfo(String info)
	{
		this.info = info;
	}

	public String getInfo()
	{
		return info;
	}

	public String getAnimalSex(String animalName)
	{
		try
		{
			return cs.getMostRecentValueAsXrefName(animalName, "Sex");
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public String getAnimalSpecies(String line)
	{
		try
		{
			return cs.getMostRecentValueAsXrefName(line, "Species");
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public String getAnimalLine(String animalname)
	{
		try
		{
			return cs.getMostRecentValueAsXrefName(animalname, "Line");
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public String getAnimalSource(String animalName)
	{
		try
		{
			return cs.getMostRecentValueAsXrefName(animalName, "Source");
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public String getAnimalType(String animal)
	{
		try
		{
			return cs.getMostRecentValueAsString(animal, "AnimalType");
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public String getAnimalResponsibleResearcher(String animal)
	{
		try
		{
			return cs.getMostRecentValueAsString(animal, "ResponsibleResearcher");
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public Date getAnimalWeanDate(String animal)
	{
		try
		{
			String weanDateString = cs.getMostRecentValueAsString(animal, "WeanDate");
			return newDateOnlyFormat.parse(weanDateString);
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public String getAnimalDateOfBirth(String animal)
	{
		try
		{
			return cs.getMostRecentValueAsString(animal, "DateOfBirth");
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public String getAnimalGeneMod(String animalname)
	{
		try
		{
			if (cs.getMostRecentValueAsString(animalname, "GeneModificiation") != null)
			{
				return cs.getMostRecentValueAsString(animalname, "GeneModificiation");
			}
			else
			{
				return "";
			}
		}
		catch (Exception e)
		{
			return null;
		}
	}

	// public String getAnimalGeneState(String animalname)
	// {
	// try
	// {
	//
	// cs.getObservedValuesByTargetAndFeature(animalname, "GeneState",inves);
	// cs.getAllObservedValues(measurementId, investigationIds) !=null)
	// if (cs.getMostRecentValueAsString(targetName, featureName)(animalname,
	// "GeneState") != null)
	// {
	// return cs.getMostRecentValueAsString(animalname, "GeneState");
	// }
	// else
	// {
	// return "";
	// }
	// }
	// catch (Exception e)
	// {
	// return null;
	// }
	// }

	public String getAnimalColor(String animalname)
	{
		try
		{
			if (cs.getMostRecentValueAsString(animalname, "Color") != null)
			{
				return cs.getMostRecentValueAsString(animalname, "Color");
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

	public String getAnimalBackground(String animalname)
	{
		try
		{
			if (cs.getMostRecentValueAsXrefName(animalname, "Background") != null)
			{
				return cs.getMostRecentValueAsXrefName(animalname, "Background");
			}
			else
			{
				return "";
			}
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public String getAnimalEarmark(String animalname)
	{
		try
		{
			if (cs.getMostRecentValueAsString(animalname, "Earmark") != null)
			{
				return cs.getMostRecentValueAsString(animalname, "Earmark");
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

	public List<ObservationTarget> getSexList()
	{
		return sexList;
	}

	public void setSexList(List<ObservationTarget> sexList)
	{
		this.sexList = sexList;
	}

	public List<ObservationTarget> getSpeciesList()
	{
		return speciesList;
	}

	public void setSpeciesList(List<ObservationTarget> speciesList)
	{
		this.speciesList = speciesList;
	}

	public List<ObservationTarget> getSourceList()
	{
		return sourceList;
	}

	public void setSourceList(List<ObservationTarget> sourceList)
	{
		this.sourceList = sourceList;
	}

	public List<Category> getAnimalTypeList()
	{
		return animalTypeList;
	}

	public void setAnimalTypeList(List<Category> animalTypeList)
	{
		this.animalTypeList = animalTypeList;
	}

	public Date getAnimalBirthDate(String animalName)
	{
		try
		{
			String birthDateString = cs.getMostRecentValueAsString(animalName, "DateOfBirth");
			return newDateOnlyFormat.parse(birthDateString);
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public List<String> getColorList()
	{
		return colorList;
	}

	public void setColorList(List<String> colorList)
	{
		this.colorList = colorList;
	}

	public List<String> getEarmarkList()
	{
		return earmarkList;
	}

	public void setEarmarkList(List<String> earmarkList)
	{
		this.earmarkList = earmarkList;
	}

	public List<String> getGeneModList()
	{
		return geneModList;
	}

	public void setGeneModList(List<String> geneModList)
	{
		this.geneModList = geneModList;
	}

	public List<String> getGeneStateList()
	{
		return geneStateList;
	}

	public void setGeneStateList(List<String> geneStateList)
	{
		this.geneStateList = geneStateList;
	}

}

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
import org.molgenis.framework.ui.html.DateInput;
import org.molgenis.framework.ui.html.JQueryDataTable;
import org.molgenis.framework.ui.html.SelectInput;
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
	private List<ObservationTarget> lineList;
	private List<String> colorList;

	private List<String> earmarkList;
	private List<Individual> listOfSelectedIndividuals = new ArrayList<Individual>();

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
				measurementsToShow.add("DateOfBirth");
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
				filterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.colValueProperty, cs
						.getMeasurementId("Litter"), ObservedValue.RELATION, Operator.NOT, null));
				animalMatrixViewer = new MatrixViewer(this, ANIMALMATRIX,
						new SliceablePhenoMatrix<Individual, Measurement>(Individual.class, Measurement.class), true,
						2, false, false, filterRules, new MatrixQueryRule(MatrixQueryRule.Type.colHeader,
								Measurement.NAME, Operator.IN, measurementsToShow));
				animalMatrixViewer.setDatabase(db);
				animalMatrixViewer.setLabel("Choose animal:");
			}
			catch (Exception e)
			{
				this.setError("Could not initialize matrix");
			}
		}

		animalMatrixRendered = animalMatrixViewer.render();
		// TODO Auto-generated method stub
		cs.setDatabase(db);
		List<String> investigationNames = cs.getAllUserInvestigationNames(this.getLogin().getUserName());

		if (!loaded)
		{
			try
			{
				this.setSexList(cs.getAllMarkedPanels("Sex", investigationNames));
				this.setSpeciesList(cs.getAllMarkedPanels("Species", investigationNames));
				this.setAnimalTypeList(cs.getAllCodesForFeature("AnimalType"));
				this.setSourceList(cs.getAllMarkedPanels("Source", investigationNames));
				this.setLineList(cs.getAllMarkedPanels("Line", investigationNames));
				this.setColorList(cs.getAllCodesForFeatureAsStrings("Color"));
				// this.setBackgroundList(cs.getAllMarkedPanels("Background",
				// investigationNames));
				this.setEarmarkList(cs.getAllCodesForFeatureAsStrings("Earmark"));

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
				for (Individual row : rows)
				{
					if (request.getBoolean(ANIMALMATRIX + "_selected_" + rowCnt) != null)
					{
						selectedIndividuals.add(row);
						System.out.println(row);
					}
					rowCnt++;
				}
				listOfSelectedIndividuals = selectedIndividuals;
				makeTable(db, selectedIndividuals);
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

			editTable.addColumn("Active");
			editTable.addColumn("Species");
			editTable.addColumn("Sex");
			editTable.addColumn("AnimalType");
			editTable.addColumn("Source");
			editTable.addColumn("Line");
			editTable.addColumn("DateOfBirth");
			editTable.addColumn("Color");
			editTable.addColumn("Background");
			editTable.addColumn("Earmark");

			for (Individual e : obsTargets)
			{
				// editTable.addColumn(e);
				editTable.addRow(e.getName());
			}

			for (Individual e : obsTargets)
			{

				List<ObservedValue> listObservedValues = db.find(ObservedValue.class, new QueryRule(
						ObservedValue.TARGET_NAME, Operator.EQUALS, e.getName()));
				for (ObservedValue val : listObservedValues)
				{
					observableFeat.put(val.getFeature_Name(), val.getValue());
				}
				// String line = getAnimalLine(e.getName());
				// String specie = null;

				// Species
				SelectInput speciesInput = new SelectInput("1_" + e.getName());
				speciesInput.setId("1_" + e.getName());
				for (ObservationTarget species : this.speciesList)
				{
					speciesInput.addOption(species.getName(), species.getName());
				}
				speciesInput.setValue(getAnimalSpecies(e.getName()));

				speciesInput.setWidth(-1);
				editTable.setCell(1, row, speciesInput);

				// Sex
				SelectInput sexInput = new SelectInput("2_" + e.getName());
				sexInput.setId("2_" + e.getName());
				for (ObservationTarget sex : this.sexList)
				{
					sexInput.addOption(sex.getName(), sex.getName());
				}
				sexInput.setValue(getAnimalSex(e.getName()));
				sexInput.setWidth(-1);
				editTable.setCell(2, row, sexInput);

				// AnimalType
				SelectInput animalTypeInput = new SelectInput("3_" + e.getName());
				animalTypeInput.setId("3_" + e.getName());

				for (Category animalType : this.animalTypeList)
				{
					animalTypeInput.addOption(animalType.getName(), animalType.getDescription());
				}
				// animalTypeInput.setValue(getAnimalType(e.getName()));
				animalTypeInput.setValue(cs.getMostRecentValueAsString(e.getName(), "AnimalType"));
				// System.out.println("### " +
				// cs.getMostRecentValueAsString(e.getName(), "AnimalType"));
				animalTypeInput.setWidth(-1);
				editTable.setCell(3, row, animalTypeInput);

				// Source
				SelectInput sourceInput = new SelectInput("4_" + e.getName());
				sourceInput.setId("4_" + e.getName());

				for (ObservationTarget source : this.sourceList)
				{
					sourceInput.addOption(source.getName(), source.getName());

				}
				sourceInput.setValue(getAnimalSource(e.getName()));
				sourceInput.setWidth(-1);
				editTable.setCell(4, row, sourceInput);

				// Line
				SelectInput lineInput = new SelectInput("5_" + e.getName());
				lineInput.setId("5_" + e.getName());
				lineInput.addOption("", "");
				for (ObservationTarget lineT : this.lineList)
				{

					lineInput.addOption(lineT.getName(), lineT.getName());
				}
				lineInput.setValue(getAnimalLine(e.getName()));
				lineInput.setWidth(-1);
				editTable.setCell(5, row, lineInput);

				// DateOfbirth
				DateInput dateOfBirthInput = new DateInput("6_" + e.getName());
				dateOfBirthInput.setId("6_" + e.getName());
				dateOfBirthInput.setDateFormat("yyyy-MM-dd");
				dateOfBirthInput.setValue(getAnimalBirthDate(e.getName()));
				dateOfBirthInput
						.setJqueryproperties("dateFormat: 'yy-mm-dd', changeMonth: true, changeYear: true, showButtonPanel: true, numberOfMonths: 1");
				editTable.setCell(6, row, dateOfBirthInput);

				// Color
				SelectInput colorInput = new SelectInput("7_" + e.getName());
				colorInput.setId("7_" + e.getName());

				for (String color : this.colorList)
				{
					colorInput.addOption(color, color);

				}
				colorInput.setValue(getAnimalColor(e.getName()));
				colorInput.setWidth(-1);
				editTable.setCell(7, row, colorInput);

				List<ObservationTarget> bckgrlist = new ArrayList<ObservationTarget>();

				for (ObservationTarget b : cs.getAllMarkedPanels("Background", investigationNames))
				{
					// Only show if background belongs to chosen species
					if (cs.getMostRecentValueAsXrefName(b.getName(), "Species").equals(getAnimalSpecies(e.getName())))
					{
						bckgrlist.add(b);
					}
				}

				// Background
				SelectInput backgroundInput = new SelectInput("8_" + e.getName());
				backgroundInput.setId("8_" + e.getName());
				backgroundInput.addOption("", "");
				for (ObservationTarget background : bckgrlist)
				{
					backgroundInput.addOption(background.getName(), background.getName());
				}

				backgroundInput.setValue(getAnimalBackground(e.getName()));
				backgroundInput.setWidth(-1);
				editTable.setCell(8, row, backgroundInput);

				// Earmark
				SelectInput earmarkInput = new SelectInput("9_" + e.getName());
				earmarkInput.setId("9_" + e.getName());

				for (String earmark : this.earmarkList)
				{
					earmarkInput.addOption(earmark, earmark);

				}
				earmarkInput.addOption("", "");
				earmarkInput.setValue(getAnimalEarmark(e.getName()));
				earmarkInput.setWidth(-1);
				editTable.setCell(9, row, earmarkInput);

				// Responsible researcher

				// weandate

				// experiment

				// Genotype

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
		for (Individual e : obsTargets)
		{
			invName = cs.getObservationTargetByName(e.getName()).getInvestigation_Name();

			String speciesName = request.getString("1_" + e.getName());

			ObservationTarget species = db.find(ObservationTarget.class,
					new QueryRule(ObservationTarget.NAME, Operator.EQUALS, speciesName)).get(0);

			ObservedValue value = cs.getObservedValuesByTargetAndFeature(e.getName(), "Species", investigationNames,
					invName).get(0);
			// value.setRelation(cs.getObservationTargetByName(speciesName).getId());
			value.setRelation(cs.getObservationTargetById(species.getId()).getId());
			db.update(value);

			// AnimalType
			String animalTypeName = request.getString("3_" + e.getName());

			value = cs.getObservedValuesByTargetAndFeature(e.getName(), "AnimalType", investigationNames, invName).get(
					0);
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
			// Sex
			String sexName = request.getString("2_" + e.getName());
			value = cs.getObservedValuesByTargetAndFeature(e.getName(), "Sex", investigationNames, invName).get(0);
			value.setRelation(cs.getObservationTargetByName(sexName).getId());

			if (value.getProtocolApplication_Id() == null)
			{
				String paName = cs.makeProtocolApplication(invName, "SetSex");
				value.setProtocolApplication_Name(paName);
				db.add(value);
			}
			else
			{
				db.update(value);
			}

			// Source
			String sourceName = request.getString("4_" + e.getName());
			value = cs.getObservedValuesByTargetAndFeature(e.getName(), "Source", investigationNames, invName).get(0);
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
			// Line
			String lineName = request.getString("5_" + e.getName());
			value = cs.getObservedValuesByTargetAndFeature(e.getName(), "Line", investigationNames, invName).get(0);
			value.setRelation(cs.getObservationTargetByName(lineName).getId());

			if (value.getProtocolApplication_Id() == null)
			{
				String paName = cs.makeProtocolApplication(invName, "SetLine");
				value.setProtocolApplication_Name(paName);
				db.add(value);
			}
			else
			{
				db.update(value);
			}

			// Date of Birth
			String birthName = request.getString("6_" + e.getName());
			value = cs.getObservedValuesByTargetAndFeature(e.getName(), "DateOfBirth", investigationNames, invName)
					.get(0);
			value.setValue(birthName);
			if (value.getProtocolApplication_Id() == null)
			{
				String paName = cs.makeProtocolApplication(invName, "SetDateOfBirth");
				value.setProtocolApplication_Name(paName);
				db.add(value);
			}
			else
			{
				db.update(value);
			}

			// Color
			String colorName = request.getString("7_" + e.getName());
			value = cs.getObservedValuesByTargetAndFeature(e.getName(), "Color", investigationNames, invName).get(0);
			value.setValue(colorName);

			if (value.getProtocolApplication_Id() == null)
			{
				String paName = cs.makeProtocolApplication(invName, "SetColor");
				value.setProtocolApplication_Name(paName);
				db.add(value);
			}
			else
			{
				db.update(value);
			}
			Date now = new Date();
			// Background
			String backgroundName = request.getString("8_" + e.getName());
			value = cs.getObservedValuesByTargetAndFeature(e.getName(), "Background", investigationNames, invName).get(
					0);

			if (value.getProtocolApplication_Id() == null)
			{
				if (!backgroundName.equals("") || backgroundName == null)
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

			// Earmark
			String earmarkName = request.getString("9_" + e.getName());
			value = cs.getObservedValuesByTargetAndFeature(e.getName(), "Earmark", investigationNames, invName).get(0);
			value.setValue(earmarkName);
			if (value.getProtocolApplication_Id() == null)
			{

				String paName = cs.makeProtocolApplication(invName, "SetEarmark");
				value.setProtocolApplication_Name(paName);
				db.add(value);
			}
			else
			{
				if (!value.equals(""))
				{

					db.update(value);
				}
				else
				{
					db.remove(value);

					db.remove(cs.getProtocolApplicationById(value.getProtocolApplication_Id()));
				}

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

	public List<ObservationTarget> getLineList()
	{
		return lineList;
	}

	public void setLineList(List<ObservationTarget> lineList)
	{
		this.lineList = lineList;
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

}

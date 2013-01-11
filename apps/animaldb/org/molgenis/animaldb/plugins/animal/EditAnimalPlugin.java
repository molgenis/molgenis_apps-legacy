package org.molgenis.animaldb.plugins.animal;

import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import org.molgenis.framework.ui.html.JQueryDataTable;
import org.molgenis.framework.ui.html.SelectInput;
import org.molgenis.framework.ui.html.Table;
import org.molgenis.pheno.Category;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.util.Entity;
import org.molgenis.util.HandleRequestDelegationException;

public class EditAnimalPlugin extends PluginModel<Entity>
{

	private CommonService ct = CommonService.getInstance();
	private Table editTable = null;
	private SimpleDateFormat newDateOnlyFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
	private List<ObservationTarget> sexList;
	private List<ObservationTarget> speciesList;
	private List<ObservationTarget> sourceList;
	private List<Category> animalTypeList;
	private List<ObservationTarget> lineList;

	private boolean loaded = false;

	public EditAnimalPlugin(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "org_molgenis_animaldb_plugins_animal_EditAnimalPlugin";
	}

	@Override
	public String getViewTemplate()
	{
		return "org/molgenis/animaldb/plugins/animal/EditAnimalPlugin.ftl";
	}

	@Override
	public void reload(Database db)
	{
		// TODO Auto-generated method stub
		ct.setDatabase(db);
		List<String> investigationNames = ct.getAllUserInvestigationNames(this.getLogin().getUserName());
		try
		{
			if (!loaded)
			{
				this.setSexList(ct.getAllMarkedPanels("Sex", investigationNames));
				this.setSpeciesList(ct.getAllMarkedPanels("Species", investigationNames));
				this.setAnimalTypeList(ct.getAllCodesForFeature("AnimalType"));
				this.setSourceList(ct.getAllMarkedPanels("Source", investigationNames));
				this.setLineList(ct.getAllMarkedPanels("Line", investigationNames));
				loaded = true;
			}

		}
		catch (DatabaseException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		catch (ParseException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try
		{
			makeTable(db);
		}
		catch (ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public Show handleRequest(Database db, MolgenisRequest request, OutputStream out)
			throws HandleRequestDelegationException, DatabaseException, ParseException, IOException
	{
		ct.setDatabase(db);
		String action = request.getString("__action");
		if (action.equals("editAnimal"))
		{
			saveAnimalToDB(db, request);
		}

		return Show.SHOW_MAIN;
	}

	public String getEditTable()
	{
		return editTable.render();
	}

	public void saveAnimalToDB(Database db, MolgenisRequest request) throws DatabaseException, ParseException,
			IOException
	{
		List<Individual> obsTargets = db.find(Individual.class);
		String invName = null;

		List<String> investigationNames = ct.getAllUserInvestigationNames(this.getLogin().getUserName());
		for (Individual e : obsTargets)
		{
			invName = ct.getObservationTargetByName(e.getName()).getInvestigation_Name();

			String speciesName = request.getString("1_" + e.getName());

			ObservationTarget species = db.find(ObservationTarget.class,
					new QueryRule(ObservationTarget.NAME, Operator.EQUALS, speciesName)).get(0);

			ObservedValue value = ct.getObservedValuesByTargetAndFeature(e.getName(), "Species", investigationNames,
					invName).get(0);
			// value.setRelation(ct.getObservationTargetByName(speciesName).getId());
			value.setRelation(ct.getObservationTargetById(species.getId()).getId());
			db.update(value);

			// Sex
			String sexName = request.getString("2_" + e.getName());
			value = ct.getObservedValuesByTargetAndFeature(e.getName(), "Sex", investigationNames, invName).get(0);
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

			// AnimalType
			String animalTypeName = request.getString("3_" + e.getName());

			value = ct.getObservedValuesByTargetAndFeature(e.getName(), "AnimalType", investigationNames, invName).get(
					0);
			// value.setRelation_Name(ct.getObservationTargetByName(animalTypeName).getName());
			db.update(value);

			// Source
			String sourceName = request.getString("4_" + e.getName());
			value = ct.getObservedValuesByTargetAndFeature(e.getName(), "Source", investigationNames, invName).get(0);
			value.setRelation(ct.getObservationTargetByName(sourceName).getId());

			if (value.getProtocolApplication_Id() == null)
			{
				String paName = ct.makeProtocolApplication(invName, "SetSource");
				value.setProtocolApplication_Name(paName);
				db.add(value);
			}
			else
			{
				db.update(value);
			}
			String lineName = request.getString("5_" + e.getName());
			value = ct.getObservedValuesByTargetAndFeature(e.getName(), "Line", investigationNames, invName).get(0);
			value.setRelation(ct.getObservationTargetByName(lineName).getId());

			if (value.getProtocolApplication_Id() == null)
			{
				String paName = ct.makeProtocolApplication(invName, "SetLine");
				value.setProtocolApplication_Name(paName);
				db.add(value);
			}
			else
			{
				db.update(value);
			}

		}
	}

	public void makeTable(Database db) throws ParseException
	{
		List<Individual> obsTargets;
		// List<String> investigationNames =
		// ct.getAllUserInvestigationNames(db.getLogin().getUserName());
		// List<MatrixQueryRule> filterRules = new ArrayList<MatrixQueryRule>();
		// filterRules.add(new MatrixQueryRule(Individual.class,
		// Individual.INVESTIGATION_NAME,Operator.IN, investigationNames));
		Map<String, String> observableFeat = new HashMap<String, String>();
		int row = 0;
		try
		{

			obsTargets = db.find(Individual.class);

			editTable = new JQueryDataTable("EditTable", "");

			editTable.addColumn("Active");
			editTable.addColumn("Species");
			editTable.addColumn("Sex");
			editTable.addColumn("AnimalType");
			editTable.addColumn("Source");
			editTable.addColumn("Line");

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
				String line = getAnimalLine(e.getName());
				String specie = null;

				// Species
				SelectInput speciesInput = new SelectInput("1_" + e.getName());
				speciesInput.setId("Species");
				for (ObservationTarget species : this.speciesList)
				{
					speciesInput.addOption(species.getName(), species.getName());
				}
				speciesInput.setValue(getAnimalSpecies(line));
				specie = getAnimalSpecies(line);
				speciesInput.setWidth(-1);
				editTable.setCell(1, row, speciesInput);

				// Sex
				SelectInput sexInput = new SelectInput("2_" + e.getName());
				sexInput.setId("Sex");
				for (ObservationTarget sex : this.sexList)
				{
					sexInput.addOption(sex.getName(), sex.getName());
				}
				sexInput.setValue(getAnimalSex(e.getName()));
				sexInput.setWidth(-1);
				editTable.setCell(2, row, sexInput);

				// AnimalType
				SelectInput animalTypeInput = new SelectInput("3_" + e.getName());
				animalTypeInput.setId("AnimalType");
				for (Category animalType : this.animalTypeList)
				{
					animalTypeInput.addOption(animalType.getDescription(), animalType.getDescription());
				}
				animalTypeInput.setValue(getAnimalType(line));
				animalTypeInput.setWidth(-1);
				editTable.setCell(3, row, animalTypeInput);

				// Source
				SelectInput sourceInput = new SelectInput("4_" + e.getName());
				sourceInput.setId("Source");
				for (ObservationTarget source : this.sourceList)
				{
					List<ObservedValue> sourceTypeValueList = db.query(ObservedValue.class)
							.eq(ObservedValue.TARGET, source.getId()).eq(ObservedValue.FEATURE_NAME, "SourceType")
							.find();
					if (sourceTypeValueList.size() > 0)
					{
						String sourcetype = sourceTypeValueList.get(0).getValue();
						if (!sourcetype.equals("Eigen fok binnen uw organisatorische werkeenheid"))
						{
							sourceInput.addOption(source.getName(), source.getName());
						}
					}

				}
				sourceInput.setValue(getAnimalSource(e.getName()));
				sourceInput.setWidth(-1);
				editTable.setCell(4, row, sourceInput);

				// Line
				SelectInput lineInput = new SelectInput("5_" + e.getName());
				lineInput.setId("Line");
				for (ObservationTarget lineT : this.lineList)
				{

					lineInput.addOption(lineT.getName(), lineT.getName());
				}
				lineInput.setValue(getAnimalLine(e.getName()));
				lineInput.setWidth(-1);
				editTable.setCell(5, row, lineInput);

				row++;

			}

		}
		catch (DatabaseException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
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

	public String getAnimalSpecies(String line)
	{
		try
		{
			return ct.getMostRecentValueAsXrefName(line, "Species");
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
			return ct.getMostRecentValueAsXrefName(animalname, "Line");
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
			return ct.getMostRecentValueAsXrefName(animalName, "Source");
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public String getAnimalType(String line)
	{
		try
		{
			return ct.getMostRecentValueAsXrefName(line, "AnimalType");
		}
		catch (Exception e)
		{
			return null;
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

}

/* Date:        March 11, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.animaldb.plugins.settings;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.molgenis.animaldb.commonservice.CommonService;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.server.MolgenisRequest;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.pheno.Category;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.util.Entity;

public class SpeciesPlugin extends PluginModel<Entity>
{
	private static final long serialVersionUID = 6637437260773077373L;
	private List<ObservationTarget> speciesList;
	private List<Category> nvwaCodeList;
	private String action = "init";
	private Map<Integer, String> dutchNameMap;
	private Map<Integer, String> latinNameMap;
	private Map<Integer, String> vwaNameMap;

	public Map<Integer, String> getPrefixStringMap()
	{
		return prefixStringMap;
	}

	private Map<Integer, String> prefixStringMap;
	private CommonService ct = CommonService.getInstance();

	public SpeciesPlugin(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	public String getCustomHtmlHeaders()
	{
		return "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/animaldb.css\">";
	}

	public List<ObservationTarget> getSpeciesList()
	{
		return speciesList;
	}

	public void setSpeciesList(List<ObservationTarget> speciesList)
	{
		this.speciesList = speciesList;
	}

	@Override
	public String getViewName()
	{
		return "org_molgenis_animaldb_plugins_settings_SpeciesPlugin";
	}

	@Override
	public String getViewTemplate()
	{
		return "org/molgenis/animaldb/plugins/settings/SpeciesPlugin.ftl";
	}

	public String getAction()
	{
		return action;
	}

	public void setAction(String action)
	{
		this.action = action;
	}

	public String getDutchName(int speciesId)
	{
		return dutchNameMap.get(speciesId);
	}

	public String getLatinName(int speciesId)
	{
		return latinNameMap.get(speciesId);
	}

	public String getVwaName(int speciesId)
	{
		return vwaNameMap.get(speciesId);
	}

	public String getPrefixString(int speciesId)
	{
		return prefixStringMap.get(speciesId);
	}

	public List<Category> getNvwaCodeList()
	{
		return nvwaCodeList;
	}

	public void setNvwaCodeList(List<Category> nvwaCodeList)
	{
		this.nvwaCodeList = nvwaCodeList;
	}

	private String getSpeciesName(Database db, int speciesId, String measurementName) throws DatabaseException
	{

		Query<ObservedValue> q = db.query(ObservedValue.class);
		q.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, measurementName));
		q.addRules(new QueryRule(ObservedValue.TARGET, Operator.EQUALS, speciesId));
		if (q.find().size() == 1)
		{
			return q.find().get(0).getValue();
		}
		return "";
	}

	@Override
	public void handleRequest(Database db, MolgenisRequest request)
	{
		ct.setDatabase(db);
		try
		{
			action = request.getString("__action");

			if (action.equals("Add"))
			{
				//
			}

			if (action.equals("Import"))
			{
				//
			}

			if (action.equals("addSpecies"))
			{

				String speciesName = request.getString("speciesname");
				String speciesLatinName = request.getString("specieslatinname");
				String speciesDutchName = request.getString("speciesdutchname");
				String nvwaCategory = request.getString("nvwacategory");
				String prefixString = request.getString("prefixstring");
				String investigationName = ct.getOwnUserInvestigationName(this.getLogin().getUserName());
				ct.createPanel(investigationName, speciesName);
				db.add(ct.createObservedValueWithProtocolApplication(investigationName, new Date(), null,
						"SetTypeOfGroup", "TypeOfGroup", speciesName, "Species", null));
				db.add(ct.createObservedValueWithProtocolApplication(investigationName, new Date(), null,
						"SetLatinSpecies", "LatinSpecies", speciesName, speciesLatinName, null));
				db.add(ct.createObservedValueWithProtocolApplication(investigationName, new Date(), null,
						"SetDutchSpecies", "DutchSpecies", speciesName, speciesDutchName, null));
				db.add(ct.createObservedValueWithProtocolApplication(investigationName, new Date(), null,
						"SetVWASpecies", "VWASpecies", speciesName, nvwaCategory, null));
				db.add(ct.createObservedValueWithProtocolApplication(investigationName, new Date(), null,
						"SetPrefixString", "PrefixString", speciesName, prefixString, null));
				ct.updatePrefix("animal", prefixString, 0);
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (e.getMessage() != null)
			{
				this.setError(e.getMessage());
			}
		}
	}

	public void reload(Database db)
	{
		ct.setDatabase(db);

		// Populate species list and property maps
		try
		{
			List<String> investigationNames = ct.getAllUserInvestigationNames(this.getLogin().getUserName());
			this.speciesList = ct.getAllMarkedPanels("Species", investigationNames);

			// Populate nvwaCategoryList code list
			this.setNvwaCodeList(ct.getAllCodesForFeature("VWASpecies"));

			dutchNameMap = new HashMap<Integer, String>();
			latinNameMap = new HashMap<Integer, String>();
			vwaNameMap = new HashMap<Integer, String>();
			prefixStringMap = new HashMap<Integer, String>();
			for (ObservationTarget species : speciesList)
			{
				dutchNameMap.put(species.getId(), this.getSpeciesName(db, species.getId(), "DutchSpecies"));
				latinNameMap.put(species.getId(), this.getSpeciesName(db, species.getId(), "LatinSpecies"));
				vwaNameMap.put(species.getId(), this.getSpeciesName(db, species.getId(), "VWASpecies"));
				prefixStringMap.put(species.getId(), this.getSpeciesName(db, species.getId(), "PrefixString"));
			}

		}
		catch (Exception e)
		{
			String message = "Something went wrong while loading species";
			if (e.getMessage() != null)
			{
				message += (": " + e.getMessage());
			}
			this.setError(message);
			e.printStackTrace();
		}
	}
}

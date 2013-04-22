/* Date:        September 14, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.animaldb.plugins.breeding;

import java.util.ArrayList;
import java.util.List;

import org.molgenis.animaldb.commonservice.CommonService;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.server.MolgenisRequest;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.util.Entity;

public class BreedingOverview extends PluginModel<Entity>
{
	private static final long serialVersionUID = 1906962555512398640L;
	private CommonService cs = CommonService.getInstance();
	private String action = "init";
	private String lineName;
	private String sourceName;
	private String speciesName;
	private String remarks;
	private Database DB = null;
	private List<String> investigationNames;

	private List<ObservationTarget> sourceList;
	private List<ObservationTarget> lineList;
	private List<ObservationTarget> speciesList;

	public BreedingOverview(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	public String getCustomHtmlHeaders()
	{
		return "<script type=\"text/javascript\" src=\"res/jquery-plugins/datatables/js/jquery.dataTables.js\"></script>\n"
				+ "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/jquery-plugins/datatables/css/demo_table_jui.css\">\n"
				+ "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/animaldb.css\">";
	}

	@Override
	public String getViewName()
	{
		return "org_molgenis_animaldb_plugins_breeding_BreedingOverview";
	}

	@Override
	public String getViewTemplate()
	{
		return "org/molgenis/animaldb/plugins/breeding/BreedingOverview.ftl";
	}

	public void setAction(String action)
	{
		this.action = action;
	}

	public String getAction()
	{
		return action;
	}

	public String getFullName(String lineName)
	{
		String fullName = "";
		try
		{
			if (cs.getMostRecentValueAsString(lineName, "LineFullName") != null)
			{
				fullName = cs.getMostRecentValueAsString(lineName, "LineFullName");
			}
		}
		catch (Exception e)
		{
			fullName = "Error when retrieving full name";
		}
		return fullName;
	}

	public String getSourceName(String lineName)
	{
		String sourceName;
		try
		{
			sourceName = cs.getMostRecentValueAsXrefName(lineName, "Source");
		}
		catch (Exception e)
		{
			sourceName = "Error when retrieving source";
		}
		return sourceName;
	}

	public String getSpeciesName(String lineName)
	{
		String speciesName;
		try
		{
			speciesName = cs.getMostRecentValueAsXrefName(lineName, "Species");
		}
		catch (Exception e)
		{
			speciesName = "Error when retrieving species";
		}
		return speciesName;
	}

	public String getRemarksString(String lineName) throws DatabaseException
	{
		// List<String> remarksList = cs.getRemarks(lineId);
		String returnString = "";
		// for (String remark : remarksList) {
		// returnString += (remark + "<br>");
		// }
		// if (returnString.length() > 0) {
		// returnString = returnString.substring(0, returnString.length() - 4);
		// }
		try
		{
			if (cs.getMostRecentValueAsString(lineName, "Remark") != null)
			{
				returnString = cs.getMostRecentValueAsString(lineName, "Remark");
			}
		}
		catch (Exception e)
		{
			returnString = "Error when retrieving remarks";
		}
		return returnString;
	}

	// public String getCountPerSex(String lineName, String sex)
	public String getCountPerSex(String lineNameString, String sexString)
	{
		try
		{
			Query<ObservedValue> q1 = this.DB.query(ObservedValue.class);
			QueryRule r1 = new QueryRule(ObservedValue.INVESTIGATION_NAME, Operator.IN, investigationNames);
			QueryRule r2 = new QueryRule(ObservedValue.RELATION, Operator.EQUALS, cs.getObservationTargetByName(
					lineNameString).getId());
			QueryRule r3 = new QueryRule(ObservedValue.RELATION, Operator.EQUALS, cs.getObservationTargetByName(
					sexString).getId());
			q1.addRules(r1, r2);
			List<ObservedValue> ovl1 = q1.find();
			Integer size = q1.find().size();
			if (!size.equals(0))
			{
				List<Integer> il1 = new ArrayList<Integer>();
				for (ObservedValue val : ovl1)
				{
					il1.add(val.getTarget_Id());
				}
				QueryRule r4 = new QueryRule(ObservedValue.TARGET, Operator.IN, il1);

				Query<ObservedValue> q2 = this.DB.query(ObservedValue.class);
				q2.addRules(r1, r3, r4);
				size = q2.find().size();
			}
			return size.toString();
		}
		catch (Exception e)
		{
			speciesName = "Error when retrieving species";
		}
		return speciesName;
	}

	@Override
	public void handleRequest(Database db, MolgenisRequest request)
	{
		try
		{
			cs.setDatabase(db);

			this.setAction(request.getAction());

			if (action.equals("JumpTo"))
			{
				// do someting useful
			}

		}
		catch (Exception e)
		{
			this.getMessages().clear();
			if (e.getMessage() != null)
			{
				this.getMessages().add(new ScreenMessage(e.getMessage(), false));
			}
			e.printStackTrace();
		}

	}

	@Override
	public void reload(Database db)
	{
		this.DB = db;
		cs.setDatabase(db);
		cs.makeObservationTargetNameMap(this.getLogin().getUserName(), false);

		try
		{
			this.investigationNames = cs.getAllUserInvestigationNames(this.getLogin().getUserName());
			// Populate source list
			// All source types pertaining to
			// "Eigen fok binnen uw organisatorische werkeenheid"
			sourceList = new ArrayList<ObservationTarget>();
			List<ObservationTarget> tmpSourceList = cs.getAllMarkedPanels("Source", investigationNames);
			for (ObservationTarget tmpSource : tmpSourceList)
			{
				int featid = cs.getMeasurementId("SourceType");
				Query<ObservedValue> sourceTypeQuery = db.query(ObservedValue.class);
				sourceTypeQuery.addRules(new QueryRule(ObservedValue.TARGET, Operator.EQUALS, tmpSource.getId()));
				sourceTypeQuery.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, featid));
				List<ObservedValue> sourceTypeValueList = sourceTypeQuery.find();
				if (sourceTypeValueList.size() > 0)
				{
					String sourcetype = sourceTypeValueList.get(0).getValue();
					if (sourcetype.equals("Eigen fok binnen uw organisatorische werkeenheid"))
					{
						sourceList.add(tmpSource);
					}
				}
			}
			// Populate species list
			this.speciesList = cs.getAllMarkedPanels("Species", investigationNames);
			// Populate existing lines list
			this.lineList = cs.getAllMarkedPanels("Line", investigationNames);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			this.getMessages().clear();
			if (e.getMessage() != null)
			{
				this.getMessages().add(new ScreenMessage(e.getMessage(), false));
			}
		}

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
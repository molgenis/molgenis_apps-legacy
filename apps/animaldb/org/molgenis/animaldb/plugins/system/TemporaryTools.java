package org.molgenis.animaldb.plugins.system;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.molgenis.animaldb.commonservice.CommonService;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.server.MolgenisRequest;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.protocol.ProtocolApplication;
import org.molgenis.util.Entity;

public class TemporaryTools extends PluginModel<Entity>
{
	private static final long serialVersionUID = -1778222062030344381L;
	private CommonService ct = CommonService.getInstance();
	private List<String> warningsList;
	private String userName;
	private List<String> investigationNames = new ArrayList<String>();

	public TemporaryTools(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	public String getCustomHtmlHeaders()
	{
		return "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/animaldb.css\">";
	}

	@Override
	public String getViewName()
	{
		return "org_molgenis_animaldb_plugins_system_TemporaryTools";
	}

	@Override
	public String getViewTemplate()
	{
		return "org/molgenis/animaldb/plugins/system/TemporaryTools.ftl";
	}

	@Override
	public void handleRequest(Database db, MolgenisRequest request)
	{
		try
		{
			ct.setDatabase(db);
			String action = request.getString("__action");

			if (action.equals("entryexitdateupdate"))
			{
				// SimpleDateFormat sdf = new
				// SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

				// Go through all animals owned by the current user
				// ct.getall

				List<String> targetNameList = ct.getAllObservationTargetNames("Individual", false,
						this.investigationNames);

				// Init lists that we can later add to the DB at once
				List<ObservedValue> valuesToAddList = new ArrayList<ObservedValue>();
				// List<Individual> animalsToAddList = new
				// ArrayList<Individual>();
				List<ProtocolApplication> appsToAddList = new ArrayList<ProtocolApplication>();

				ProtocolApplication entryApp = ct.createProtocolApplication("System", "SetFacilityEntryDate");
				ProtocolApplication exitApp = ct.createProtocolApplication("System", "SetFacilityExitDate");

				String dateString = "";
				Date date = null;
				for (String animalName : targetNameList)
				{
					System.out.println("------------> " + animalName);
					// get the active value and check if there are entry/exit
					// vals
					ObservedValue activeVal = ct.getObservedValuesByTargetAndFeature(animalName, "Active",
							investigationNames, "System").get(0);

					if (activeVal.getValue().equals(""))
					{
						// skip this animal if there is no active val.
						this.warningsList.add(" no active value found for " + animalName);
						System.out.println(" no active val for " + animalName);
						// continue;
					}
					else
					{
						if (ct.getObservedValuesByTargetAndFeature(animalName, "FacilityEntryDate", investigationNames,
								"System").get(0).getValue().equals(""))
						{
							System.out.println(" ...2 entry: ");
							valuesToAddList.add(ct.createObservedValue("System", entryApp.getName(),
									activeVal.getTime(), null, "FacilityEntryDate", animalName,
									sdf.format(activeVal.getTime()), null));
							// na value present yet, so convert active.starttime
							// to
							// facilityentry date

							// do some datetime conversion shabba here

						}
						if ((activeVal.getEndtime() != null)
								&& ct.getObservedValuesByTargetAndFeature(animalName, "FacilityExitDate",
										investigationNames, "System").get(0).getValue().equals(""))
						{
							System.out.println(" ...3 exit: ");
							valuesToAddList.add(ct.createObservedValue("System", exitApp.getName(),
									activeVal.getEndtime(), null, "FacilityExitDate", animalName,
									sdf.format(activeVal.getEndtime()), null));
							// na value present yet, so convert active.starttime
							// to
							// facilityentry date

							// do some datetime conversion shabba here

						}

					}
				}
				db.add(entryApp);
				db.add(exitApp);
				db.add(valuesToAddList);
				System.out.println(warningsList);
				// check all animals (individuals) check active value,
				// add new value FacilityEntryDate if active starttime is set
				// add new value FacilityExitDate if active endtime is set
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
			this.setError("Something went wrong:  " + e.getMessage());
		}
	}

	@Override
	public void reload(Database db)
	{
		try
		{

			ct.setDatabase(db);
			this.warningsList = new ArrayList<String>();
			this.userName = this.getLogin().getUserName();

			this.investigationNames.add("fdd");
			// this.investigsationNames.add("Admin");
			this.investigationNames.add("System");
			this.investigationNames.add("a");

			// this.investigationNames =
			// ct.getOwnUserInvestigationNames(userName);

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}

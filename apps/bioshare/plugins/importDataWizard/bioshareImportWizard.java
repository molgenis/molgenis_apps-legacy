package plugins.importDataWizard;

import gcc.bioshare.StudyDescription;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

public class bioshareImportWizard extends PluginModel<Entity>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7294262856742516691L;
	private List<String> listOfCohortStudies = new ArrayList<String>();
	private List<String> listOfPredictionModels = new ArrayList<String>();

	public bioshareImportWizard(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "plugins_importDataWizard_bioshareImportWizard";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/importDataWizard/bioshareImportWizard.ftl";
	}

	@Override
	public Show handleRequest(Database db, Tuple request, OutputStream out) throws Exception
	{

		// The request is submit request, therefore the request goes to the
		// normal handle request
		if (out == null)
		{
			this.handleRequest(db, request);
		}
		else
		{

			PrintWriter writer = new PrintWriter(out);
			JSONObject json = new JSONObject();

			// The request is ajax request, handle it here
			if ("download_json_addNewStudy".equals(request.getAction()))
			{

				// Report back the html that study has been added
				json.put("status", "false");
				json.put("message", "It failed to add this new study in the database!");
				try
				{

					db.beginTx();

					JSONObject jsonObject = new JSONObject(request.getString("studyInfo"));

					String investigationName = "catalogueCohortStudy";

					String studyName = jsonObject.getString("studyNameInput");

					// Check if this is add or update
					boolean addRecord = true;

					ObservationTarget targetStudy = null;

					List<QueryRule> rules = new ArrayList<QueryRule>();

					rules.add(new QueryRule(ObservationTarget.NAME, Operator.EQUALS, studyName));
					rules.add(new QueryRule(ObservationTarget.INVESTIGATION_NAME, Operator.EQUALS, investigationName));

					if (db.find(ObservationTarget.class, new QueryRule(rules)).size() == 0)
					{

						targetStudy = new ObservationTarget();
						targetStudy.setName(studyName);
						targetStudy.setInvestigation_Name(investigationName);
						db.add(targetStudy);

					}
					else
					{
						addRecord = false;
					}

					if (addRecord)
					{

						List<StudyDescription> listOfValues = new ArrayList<StudyDescription>();

						@SuppressWarnings("unchecked")
						Iterator<String> iterator = jsonObject.keys();

						while (iterator.hasNext())
						{

							String key = iterator.next();

							if (!key.equals("studyNameInput"))
							{
								String measurementName = key.toString().replaceAll("Input", "_" + investigationName);
								String value = jsonObject.get(key).toString();

								if (addRecord)
								{

									if (value != null && !value.equals(""))
									{
										StudyDescription ov = new StudyDescription();
										ov.setFeature_Name(measurementName);
										ov.setTarget_Name(studyName);
										ov.setValue(value);
										ov.setInvestigation_Name(investigationName);
										listOfValues.add(ov);

									}
								}
							}
						}

						db.add(listOfValues);
						db.commitTx();

						json.put("status", "true");
						json.put("message", "The study was added in the database!");
					}
					else
					{
						json.put("status", "false");
						json.put("message", "The study already existed!");
					}

				}
				catch (Exception e)
				{
					db.rollbackTx();
					e.printStackTrace();
				}

			}
			else if ("download_json_refreshStudy".equals(request.getAction()))
			{

				String investigationName = "catalogueCohortStudy";

				Query<StudyDescription> rules = db.query(StudyDescription.class);
				rules.addRules(new QueryRule(StudyDescription.TARGET_NAME, Operator.EQUALS, request
						.getString("studyName")));
				rules.addRules(new QueryRule(StudyDescription.INVESTIGATION_NAME, Operator.EQUALS, investigationName));

				for (StudyDescription ov : rules.find())
				{
					json.put(ov.getFeature_Name().replaceAll("_" + investigationName, ""), ov.getValue());
				}
			}

			writer.write(json.toString());
			writer.flush();
			writer.close();
		}

		return Show.SHOW_MAIN;
	}

	@Override
	public void handleRequest(Database db, Tuple request) throws Exception
	{

	}

	@Override
	public void reload(Database db)
	{

		try
		{
			listOfCohortStudies.clear();
			listOfPredictionModels.clear();
			// Get all the investigations from database except for Prediction
			// model
			for (ObservationTarget target : db.find(ObservationTarget.class, new QueryRule(
					ObservationTarget.INVESTIGATION_NAME, Operator.EQUALS, "catalogueCohortStudy")))
			{
				listOfCohortStudies.add(target.getName());
			}
			for (ObservationTarget target : db.find(ObservationTarget.class, new QueryRule(
					ObservationTarget.INVESTIGATION_NAME, Operator.EQUALS, "cataloguePredictionModel")))
			{
				listOfPredictionModels.add(target.getName());
			}

		}
		catch (DatabaseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public List<String> getlistOfCohortStudies()
	{
		return listOfCohortStudies;
	}

	public List<String> getListOfPredictionModels()
	{
		return listOfPredictionModels;
	}

	public String getUrl()
	{
		return "molgenis.do?__target=" + this.getName();
	}
}

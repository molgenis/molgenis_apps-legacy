/* Date:        December 3, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 */

package plugins.harmonization;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.molgenis.compute.ComputeProtocol;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.pheno.Measurement;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

//import plugins.autohidelogin.AutoHideLoginModel; 

public class Harmonization extends PluginModel<Entity>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4255876428416189905L;
	private List<String> listOfPredictionModels = new ArrayList<String>();

	public Harmonization(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "plugins_harmonization_Harmonization";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/harmonization/Harmonization.ftl";
	}

	@Override
	public Show handleRequest(Database db, Tuple request, OutputStream out) throws Exception
	{

		if (out == null)
		{
			this.handleRequest(db, request);
		}
		else
		{
			JSONObject status = new JSONObject();

			try
			{
				// Handle request that adds a new prediction model to db
				if ("download_json_newPredictionModel".equals(request.getAction()))
				{
					String nameOfModel = request.getString("name");
					ComputeProtocol cp = new ComputeProtocol();
					cp.setName(nameOfModel);
					cp.setInvestigation_Name("Prediction Model");
					cp.setScriptTemplate("Not provided");
					db.add(cp);
					status.put("message",
							"You successfully added a new prediction model</br>Please define the predictors");
					status.put("success", true);

				}
				else if ("download_json_removePredictionModel".equals(request.getAction()))
				{
					String nameOfModel = request.getString("name");

					ComputeProtocol cp = db.find(ComputeProtocol.class,
							new QueryRule(ComputeProtocol.NAME, Operator.EQUALS, nameOfModel)).get(0);

					List<String> features = cp.getFeatures_Name();

					if (features.size() > 0)
					{
						List<Measurement> listOfFeatures = db.find(Measurement.class, new QueryRule(Measurement.NAME,
								Operator.IN, features));
						db.remove(listOfFeatures);
					}
					db.remove(cp);

					status.put("message", "You successfully removed a prediction model from the database!");
					status.put("success", true);
				}
			}
			catch (Exception e)
			{
				status.put("message", e.getMessage());
				status.put("success", false);
			}

			PrintWriter writer = new PrintWriter(out);
			writer.write(status.toString());
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
			// clear the old variable content
			listOfPredictionModels.clear();

			for (ComputeProtocol cp : db.find(ComputeProtocol.class, new QueryRule(ComputeProtocol.INVESTIGATION_NAME,
					Operator.EQUALS, "Prediction Model")))
			{
				listOfPredictionModels.add(cp.getName());
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
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
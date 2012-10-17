/* Date:        December 3, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 */

package plugins.harmonization;

import java.util.ArrayList;
import java.util.List;

import org.molgenis.compute.ComputeProtocol;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
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
}
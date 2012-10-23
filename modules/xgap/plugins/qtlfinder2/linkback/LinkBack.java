/* Date:        October 28, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.qtlfinder2.linkback;

import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.FormController;
import org.molgenis.framework.ui.FormModel;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.pheno.ObservationElement;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;
import org.molgenis.xgap.Gene;
import org.molgenis.xgap.Probe;

public class LinkBack extends PluginModel<Entity>
{

	private static final long serialVersionUID = 7832540415673199206L;

	public LinkBack(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	private LinkBackModel model = new LinkBackModel();

	public LinkBackModel getMyModel()
	{
		return model;
	}

	@Override
	public String getViewName()
	{
		return "plugins_qtlfinder2_linkback_LinkBack";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/qtlfinder2/linkback/LinkBack.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{

	}

	@Override
	public void reload(Database db)
	{

		try
		{

			ScreenController<?> parentController = (ScreenController<?>) this.getParent();
			FormModel<ObservationElement> parentForm = (FormModel<ObservationElement>) ((FormController) parentController)
					.getModel();

			List<ObservationElement> phenotype = parentForm.getRecords();

			this.model.setId(null);

			if (phenotype.size() == 1)
			{
				// special: Gene is always translated to Probe!
				// if Gene is measured directly, this code must be updated.
				if (phenotype.get(0).get(ObservationElement.__TYPE).equals("Gene"))
				{
					List<Probe> probes = db.find(Probe.class, new QueryRule(Probe.REPORTSFOR, Operator.EQUALS,
							phenotype.get(0).get(Gene.ID)), new QueryRule(Operator.OR), new QueryRule(Probe.SYMBOL,
							Operator.EQUALS, phenotype.get(0).get(Gene.NAME)));

					// if(probes.size() == 0)
					// {
					// probes = db.find(Probe.class, new QueryRule(Probe.SYMBOL,
					// Operator.EQUALS, phenotype.get(0).get(Gene.NAME)));
					// }

					if (probes.size() > 0)
					{
						StringBuilder ids = new StringBuilder();
						for (Probe p : probes)
						{
							ids.append(p.getId() + ",");
						}
						ids.deleteCharAt(ids.length() - 1);
						this.model.setId(ids.toString());
					}
				}
				else
				{
					this.model.setId(phenotype.get(0).getId().toString());
				}
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
			this.setMessages(new ScreenMessage(e.getMessage() != null ? e.getMessage() : "null", false));
		}
	}
}

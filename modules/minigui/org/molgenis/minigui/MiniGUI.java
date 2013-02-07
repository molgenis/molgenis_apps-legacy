/* Date:        April 22, 2011
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.minigui;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.server.MolgenisRequest;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.util.Entity;

import app.servlet.UsedMolgenisOptions;

public class MiniGUI<E extends Entity> extends PluginModel<E>
{
	/**
	 * Experimental minimal GUI for MOLGENIS
	 */
	private static final long serialVersionUID = 5163895580240779726L;

	public MiniGUI(String name, ScreenController<?> parent)
	{
		super(name, parent);

		if (new UsedMolgenisOptions().appName.equals("omicsconnect"))
		{
			this.model.setLogoLocation("omicsconnect/images/oc_logo_alt_150px.png");
		}
		else
		{
			this.model.setLogoLocation("clusterdemo/logos/molgenis_logo.png");
		}
	}

	private MiniGUIModel model = new MiniGUIModel();

	public MiniGUIModel getVO()
	{
		return model;
	}

	@Override
	public String getViewName()
	{
		return "org_molgenis_minigui_MiniGUI";
	}

	@Override
	public String getViewTemplate()
	{
		return "org/molgenis/minigui/MiniGUI.ftl";
	}

	@Override
	public void handleRequest(Database db, MolgenisRequest request)
	{
	}

	@Override
	public void reload(Database db)
	{
		this.model.setUiTree(this.getParent().getChildren());
	}

	@Override
	public boolean isVisible()
	{
		return true;
	}

	@Override
	public String getCustomHtmlHeaders()
	{
		String style = "\n";
		style += "<link rel=\"stylesheet\" type=\"text/css\" href=\"jquery/superfish/superfish.css\" media=\"screen\">\n";
		style += "<script type=\"text/javascript\" src=\"jquery/superfish/superfish.js\"></script>\n";
		style += "<script type=\"text/javascript\">\n";
		style += "	jQuery(function(){\n";
		style += "	jQuery('ul.sf-menu').superfish();\n";
		style += "});\n";
		style += "</script>\n";

		// search box
		style += "<link rel=\"stylesheet\" style=\"text/css\" href=\"clusterdemo/qtlfinder.css\">\n";

		// plain style
		style += "<link rel=\"stylesheet\" style=\"text/css\" href=\"jquery/superfish/colors.css\">\n";
		style += "<link rel=\"stylesheet\" style=\"text/css\" href=\"jquery/superfish/main_override.css\">";
		return style;
	}
}
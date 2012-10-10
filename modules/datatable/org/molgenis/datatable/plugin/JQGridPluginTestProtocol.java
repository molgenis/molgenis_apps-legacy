package org.molgenis.datatable.plugin;

import org.molgenis.framework.ui.ScreenController;

public class JQGridPluginTestProtocol extends JQGridPluginProtocol
{

	private static final long serialVersionUID = 1L;

	static String topProtocol = "stageCatalogue";
	static String target = "Pa_Id";

	public JQGridPluginTestProtocol(String name, ScreenController<?> parent)
	{
		super(name, parent, topProtocol, target);
		// TODO Auto-generated constructor stub
	}

}

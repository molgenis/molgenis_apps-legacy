package org.molgenis.examples.ui;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.framework.ui.tilebrowser.AppTile;
import org.molgenis.framework.ui.tilebrowser.TileBrowser;

public class TiledBrowserDemo extends EasyPluginController
{
	private static final long serialVersionUID = 7794050660074280454L;

	public TiledBrowserDemo(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public void reload(Database db) throws Exception
	{
		// this method is called on every page reload
	}

	@Override
	public ScreenView getView()
	{
		TileBrowser browser = new TileBrowser("mybrowser", "Apps");

		browser.add(new AppTile("News", "aa").tag("view"));

		browser.add(new AppTile("Find QTLs", "aa").tag("analyze").tag("omics"));
		browser.add(new AppTile("Genome browser", "aa").tag("analyze").tag("ngs").tag("omics").tag("patho"));

		browser.add(new AppTile("Biobanks", "aa").tag("view").tag("biobank"));
		browser.add(new AppTile("Studies", "aa").tag("view").tag("prot").tag("omics"));

		browser.add(new AppTile("Protocols", "aa").tag("view").tag("edit").tag("ngs").tag("biobank").tag("patho"));
		browser.add(new AppTile("Mutation reports", "aa").tag("view").tag("patho"));

		browser.add(new AppTile("Cohort builder", "aa").tag("analyze").tag("biobank"));

		browser.add(new AppTile("Pipelines", "aa").tag("view").tag("edit").tag("ngs"));

		browser.add(new AppTile("Data Sets", "aa").tag("view").tag("biobank").tag("omics"));

		browser.add(new AppTile("Harmonize Data Items", "aa").tag("edit").tag("biobank"));

		browser.add(new AppTile("Electronic Lab", "aa").tag("edit").tag("ngs"));
		browser.add(new AppTile("Compute", "aa").tag("analyze").tag("ngs").tag("omics"));

		browser.add(new AppTile("Upload data", "aa").tag("edit").tag("biobank"));
		browser.add(new AppTile("Ontology Manager", "aa").tag("edit").tag("biobank"));
		browser.add(new AppTile("Literature", "aa").tag("edit").tag("patho").tag("omics"));
		browser.add(new AppTile("My Settings", "aa"));

		browser.addProperty("view").addProperty("edit").addProperty("analyze").addProperty("biobank")
				.addProperty("patho").addProperty("ngs").addProperty("omics");

		return browser;
	}
}

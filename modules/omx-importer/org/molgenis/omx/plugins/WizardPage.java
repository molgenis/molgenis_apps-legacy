package org.molgenis.omx.plugins;

import jxl.common.Logger;

import org.molgenis.framework.db.Database;
import org.molgenis.util.Tuple;

public class WizardPage
{
	protected final Logger logger = Logger.getLogger(getClass());

	private String title;
	private String viewTemplate;
	private ImportWizard wizard;

	public WizardPage(String title, String viewTemplate)
	{
		super();
		this.title = title;
		this.viewTemplate = viewTemplate;
	}

	protected ImportWizard getWizard()
	{
		return wizard;
	}

	protected void setWizard(ImportWizard wizard)
	{
		this.wizard = wizard;
	}

	public String getTitle()
	{
		return title;
	}

	public String getViewTemplate()
	{
		return viewTemplate;
	}

	public void handleRequest(Database db, Tuple request)
	{

	}
}

/* Date:        April 4, 2011
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.chd7.ui;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.mutation.ServiceLocator;
import org.molgenis.mutation.service.StatisticsService;

/**
 * Information on the background of CHD7 database
 */
public class Background extends EasyPluginController<BackgroundModel>
{
	private static final long serialVersionUID = 1L;

	public Background(String name, ScreenController<?> parent)
	{
		super(name, parent);
		this.setModel(new BackgroundModel(this));
	}

	public ScreenView getView()
	{
		return new FreemarkerView("Background.ftl", getModel());
	}

	@Override
	public void reload(Database db)
	{
		try
		{
			StatisticsService statisticsService = ServiceLocator.instance().getStatisticsService();
			statisticsService.setDatabase(db);

			// this.getModel().setNumPathogenicMutations(statisticsService.getNumMutationsByPathogenicity("pathogenic"));
			// this.getModel().setNumPathogenicPatients(statisticsService.getNumPatientsByPathogenicity("pathogenic"));
			// this.getModel().setNumUnclassifiedMutations(statisticsService.getNumMutationsByPathogenicity("unclassified variant"));
			// this.getModel().setNumUnclassifiedPatients(statisticsService.getNumPatientsByPathogenicity("unclassified variant"));
			// this.getModel().setNumBenignMutations(statisticsService.getNumMutationsByPathogenicity("benign"));
			this.getModel().setNumPatientsUnpub(statisticsService.getNumUnpublishedPatients());
		}
		catch (Exception e)
		{
			// ...
		}
	}
}

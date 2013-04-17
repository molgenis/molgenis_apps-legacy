/* Date:        June 25, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.animaldb.plugins.system;

import org.molgenis.animaldb.convertors.generic.AnimalImporter;
import org.molgenis.animaldb.convertors.oldadb.LoadAnimalDB;
import org.molgenis.animaldb.convertors.prefill.PrefillAnimalDB;
import org.molgenis.animaldb.convertors.rhutdb.ConvertRhutDbToPheno;
import org.molgenis.animaldb.convertors.ulidb.ConvertUliDbToPheno;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.server.MolgenisRequest;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.util.Entity;

public class LoadLegacyPlugin extends PluginModel<Entity>
{
	private static final long serialVersionUID = -5634663322794444817L;

	public LoadLegacyPlugin(String name, ScreenController<?> parent)
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
		return "org_molgenis_animaldb_plugins_system_LoadLegacyPlugin";
	}

	@Override
	public String getViewTemplate()
	{
		return "org/molgenis/animaldb/plugins/system/LoadLegacyPlugin.ftl";
	}

	@Override
	public void handleRequest(Database db, MolgenisRequest request)
	{
		try
		{
			String action = request.getString("__action");

			if (action.equals("load"))
			{
				String filename = request.getString("zip");
				String legacy = request.getString("source");
				if (legacy.equals("prefill"))
				{
					PrefillAnimalDB myPrefill = new PrefillAnimalDB(db, this.getLogin());
					myPrefill.prefillFromZip(filename);
					this.setSuccess("Pre-filling AnimalDB successful");
				}
				else if (legacy.equals("ulidb"))
				{
					ConvertUliDbToPheno myLoadUliDb = new ConvertUliDbToPheno(db, this.getLogin());
					myLoadUliDb.convertFromFile(filename);
					this.setSuccess("Legacy import from Uli Eisel DB successful");
				}
				else if (legacy.equals("oldadb"))
				{
					LoadAnimalDB myLoadAnimalDB = new LoadAnimalDB(db, this.getLogin());
					myLoadAnimalDB.convertFromZip(filename);
					this.setSuccess("Legacy import from old AnimalDB successful");
				}
				else if (legacy.equals("rhutdb"))
				{
					ConvertRhutDbToPheno myLoadRhutDb = new ConvertRhutDbToPheno(db, this.getLogin());
					myLoadRhutDb.convertFromZip(filename);
					this.setSuccess("Legacy import from Roelof Hut DB successful");
				}
				else if (legacy.equals("generic"))
				{
					AnimalImporter myLoadAnimalsDb = new AnimalImporter(db, this.getLogin());
					myLoadAnimalsDb.convertFromZip(filename);
					this.setSuccess("import successful");
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			this.setError("Something went wrong while loading your legacy database: " + e.getMessage());
		}
	}

	@Override
	public void reload(Database db)
	{
		// try
		// {
		// Database db = this.getDatabase();
		// Query q = db.query(Experiment.class);
		// q.like("name", "test");
		// List<Experiment> recentExperiments = q.find();
		//
		// //do something
		// }
		// catch(Exception e)
		// {
		// //...
		// }
	}

}

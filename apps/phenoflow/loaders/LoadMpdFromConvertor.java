package loaders;

import java.io.File;
import java.util.Arrays;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.Database.DatabaseAction;

import app.DatabaseFactory;
import app.EntitiesImporterImpl;

public class LoadMpdFromConvertor
{
	public static void main(String[] args) throws Exception
	{

		Database db = DatabaseFactory.create("handwritten/apps/org/molgenis/pheno/pheno.properties");

		// okay, this is wrong because multiple investigations!
		// defaults.set("investigation_name","mouse phenome database");

		// alternatively load the defaults from a file
		// Properties p = new Properties();
		// p.load(new FileInputStream(new File("default.properties"));
		// Tuple defaults = new PropertiesTuple(p);

		// Tuple t = new PropertiesTuple(new Properties());

		// empty the database
		// MolgenisUpdateDatabase.main(null);

		// full import
		new EntitiesImporterImpl(db).importEntities(
				Arrays.asList(new File("../molgenis4phenotype/data/MPD/output").listFiles()),
				DatabaseAction.ADD_IGNORE_EXISTING);
		// CsvImport.importAll(new File("data/Europhenome"), db, defaults, null,
		// DatabaseAction.ADD_IGNORE_EXISTING, "MISSINGVALUE?");
	}
}

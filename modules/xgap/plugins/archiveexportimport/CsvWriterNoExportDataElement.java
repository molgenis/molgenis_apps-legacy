package plugins.archiveexportimport;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;

import app.CsvEntityExporter;

public class CsvWriterNoExportDataElement extends CsvEntityExporter
{
	/**
	 * we override export to export not a decimal data element list but instead
	 * a matrix in subfolder data. This has to be repeated for each 'data'
	 * entry.
	 */
	@Override
	public void exportDecimalDataElement(Database db, File f, List<String> filesToExport, QueryRule... rules)
			throws DatabaseException, IOException, ParseException
	{
		// do nothing: we instead will produce matrix files
	}

	@Override
	public void exportTextDataElement(Database db, File f, List<String> filesToExport, QueryRule... rules)
			throws DatabaseException, IOException, ParseException
	{
		// do nothing: we instead will produce matrix files
	}
}

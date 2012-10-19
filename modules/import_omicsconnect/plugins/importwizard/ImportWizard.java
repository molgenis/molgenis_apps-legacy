/* Date:        February 2, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.importwizard;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.util.Entity;
import org.molgenis.util.SimpleTuple;
import org.molgenis.util.Tuple;

import app.ExcelImport;
import app.ImportWizardExcelPrognosis;

public class ImportWizard extends PluginModel<Entity>
{
	private static final long serialVersionUID = -6011550003937663086L;
	private static final Logger LOG = Logger.getLogger(ImportWizard.class);
	private ImportWizardModel model;

	public ImportWizard(String name, ScreenController<?> parent)
	{
		super(name, parent);
		model = new ImportWizardModel();
	}

	public ImportWizardModel getMyModel()
	{
		return model;
	}

	@Override
	public String getViewName()
	{
		return "ImportWizard";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/importwizard/ImportWizard.ftl";
	}

	public void handleRequest(Database db, Tuple request)
	{
		String action = request.getString("__action");
		if (action == null) return;

		try
		{
			if (action.equals("upload"))
			{
				updateAndValidateAction(db, request.getFile("upload"));
			}
			else if (action.equals("toScreenOne"))
			{
				toScreenOneAction();
			}
			else if (action.equals("import"))
			{
				importAction(db);
			}
			else
			{
				LOG.warn("unknown action: " + action);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			this.setMessages(new ScreenMessage(e.getMessage() != null ? e.getMessage() : "null", false));
		}
	}

	private void updateAndValidateAction(Database db, File file) throws Exception
	{
		// get uploaded file and do checks
		if (file == null)
		{
			throw new Exception("No file selected.");
		}
		else if (!file.getName().endsWith(".xls"))
		{
			throw new Exception("File does not end with '.xls', other formats are not supported.");
		}

		// run prognosis
		ImportWizardExcelPrognosis iwep = new ImportWizardExcelPrognosis(db, file);
		Map<String, Boolean> sheetsImportable = iwep.getSheetsImportable();

		if (sheetsImportable.containsKey("dataset") && sheetsImportable.get("dataset") == true)
		{
			// collect dataset instance sheet names
			List<String> dataSetSheetNames = getDataSetSheetNames(file);
			for (Iterator<String> it = dataSetSheetNames.iterator(); it.hasNext();)
			{
				String datasetSheetName = it.next();
				if (!sheetsImportable.containsKey(datasetSheetName))
				{
					it.remove();
				}
			}

			// update prognosis
			for (String datasetSheetName : dataSetSheetNames)
			{
				if (sheetsImportable.containsKey(datasetSheetName)) sheetsImportable.put(datasetSheetName, true);
			}

			// update model
			this.model.setDataSetSheetNames(dataSetSheetNames);
		}

		// if no error, set prognosis, set file, and continue
		this.model.setIwep(iwep);
		this.model.setCurrentFile(file);
		this.model.setWhichScreen("two");
	}

	private void toScreenOneAction()
	{
		// goto screen one
		this.model.setWhichScreen("one");

		// reset stuff
		this.model.setCurrentFile(null);
		this.model.setIwep(null);
		this.model.setImportSuccess(false);
	}

	private void importAction(Database db) throws Exception
	{
		// goto screen three
		this.model.setWhichScreen("three");

		// set import success to false (again), to be sure
		this.model.setImportSuccess(false);
		ExcelImport.importAll(this.model.getCurrentFile(), db, new SimpleTuple());

		// import dataset instances
		new DataSetImporter(db).importXLS(this.model.getCurrentFile(), this.model.getDataSetSheetNames());

		// when no error, set success to true
		this.model.setImportSuccess(true);
	}

	private List<String> getDataSetSheetNames(File file) throws BiffException, IOException
	{
		Sheet sheet = Workbook.getWorkbook(file).getSheet("dataset");
		int rows = sheet.getRows();
		if (rows <= 1) return Collections.emptyList();

		List<String> nameList = new ArrayList<String>();
		Cell[] cells = sheet.getRow(0);
		for (int col = 0; col < cells.length; ++col)
		{
			String colStr = cells[col].getContents();
			if (colStr.toLowerCase().trim().equals("identifier"))
			{
				for (int row = 1; row < rows; ++row)
				{
					String datasetName = sheet.getCell(col, row).getContents();
					nameList.add("dataset_" + datasetName);
				}
			}
		}
		return nameList;
	}

	@Override
	public void reload(Database db)
	{

	}

}

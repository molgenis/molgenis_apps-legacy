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
import java.util.List;
import java.util.Map;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

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
	private ImportWizardModel model = new ImportWizardModel();

	public ImportWizardModel getMyModel()
	{
		return model;
	}

	public ImportWizard(String name, ScreenController<?> parent)
	{
		super(name, parent);
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
		if (request.getString("__action") != null)
		{

			try
			{

				// BUTTONS ON SCREEN ONE
				if (request.getString("__action").equals("upload"))
				{

					// get uploaded file and do checks
					File file = request.getFile("upload");
					if (file == null)
					{
						throw new Exception("No file selected.");
					}
					else if (!file.getName().endsWith(".xls"))
					{
						throw new Exception("File does not end with '.xls', other formats are not supported.");
					}

					// run prognosis
					ImportWizardExcelPrognosis iwep = new ImportWizardExcelPrognosis(file);
					Map<String, Boolean> sheetsImportable = iwep.getSheetsImportable();

					if (sheetsImportable.containsKey("dataset") && sheetsImportable.get("dataset") == true)
					{
						// filter prognosis by checking dataset instances
						List<String> datasetSheetNames = getDatasetSheetNames(file);
						for (String datasetSheetName : datasetSheetNames)
						{
							if (sheetsImportable.containsKey(datasetSheetName)) sheetsImportable.put(datasetSheetName,
									true);
						}
					}

					// if no error, set prognosis, set file, and continue
					this.model.setIwep(iwep);
					this.model.setCurrentFile(file);
					this.model.setWhichScreen("two");

					// BUTTONS ON SCREEN TWO
				}
				else if (request.getString("__action").equals("toScreenOne"))
				{

					// goto screen one
					this.model.setWhichScreen("one");

					// reset stuff
					this.model.setCurrentFile(null);
					this.model.setIwep(null);
					this.model.setImportSuccess(false);

				}
				else if (request.getString("__action").equals("import"))
				{

					// goto screen three
					this.model.setWhichScreen("three");

					// set import success to false (again), to be sure
					this.model.setImportSuccess(false);
					ExcelImport.importAll(this.model.getCurrentFile(), db, new SimpleTuple());

					// import dataset instances

					// when no error, set success to true
					this.model.setImportSuccess(true);
				}

			}
			catch (Exception e)
			{
				e.printStackTrace();
				this.setMessages(new ScreenMessage(e.getMessage() != null ? e.getMessage() : "null", false));
			}
		}
	}

	private List<String> getDatasetSheetNames(File file) throws BiffException, IOException
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

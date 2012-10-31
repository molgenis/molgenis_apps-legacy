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
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.Database.DatabaseAction;
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
	private static final int NR_WIZARD_PAGES = 5;
	private ImportWizardModel model;

	public ImportWizard(String name, ScreenController<?> parent)
	{
		super(name, parent);
		this.model = new ImportWizardModel(NR_WIZARD_PAGES);
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
			if (action.equals("screen0")) // select input
			{
				this.model.setPage(0);
			}
			else if (action.equals("screen1")) // input validation report
			{
				this.model.setPage(1);
				File file = request.getFile("upload");
				if (file == null) file = model.getFile();
				if (file == null) throw new IOException("input file is null");
				validateInput(db, file);
			}
			else if (action.equals("screen2")) // select entity import options
			{
				this.model.setPage(2);
			}
			else if (action.equals("screen3")) // select data import options
			{
				this.model.setPage(3);
				String entityImportOption = request.getString("entity_option");
				if (entityImportOption != null) this.model.setEntityImportOption(entityImportOption);
			}
			else if (action.equals("screen4")) // import summary
			{
				this.model.setPage(4);
				String dataTargetImportOption = request.getString("data_target_option");
				if (dataTargetImportOption != null) this.model.setDataTargetImportOption(dataTargetImportOption);
				String dataFeatureImportOption = request.getString("data_feature_option");
				if (dataFeatureImportOption != null) this.model.setDataFeatureImportOption(dataFeatureImportOption);

				doImport(db, this.model.getEntityImportOption(), this.model.getDataTargetImportOption(),
						this.model.getDataFeatureImportOption());
			}
			else if (action.equals("cancel") || action.equals("finish"))
			{
				this.model = new ImportWizardModel(NR_WIZARD_PAGES);
			}
			else
			{
				LOG.warn("unknown action: " + action);
			}
		}
		catch (Exception e)
		{
			LOG.warn("Exception occurred importing data", e);
			this.setMessages(new ScreenMessage(e.getMessage() != null ? e.getMessage() : "null", false));
		}
	}

	private void validateInput(Database db, File file) throws Exception
	{
		// get uploaded file and do checks
		if (file == null)
		{
			throw new Exception("No file selected.");
		}
		if (!file.getName().endsWith(".xls"))
		{
			throw new Exception("File does not end with '.xls', other formats are not supported.");
		}

		// validate entity sheets
		ImportWizardExcelPrognosis xlsValidator = new ImportWizardExcelPrognosis(db, file);

		// remove data sheets
		Map<String, Boolean> entitiesImportable = xlsValidator.getSheetsImportable();
		for (Iterator<Entry<String, Boolean>> it = entitiesImportable.entrySet().iterator(); it.hasNext();)
		{
			if (it.next().getKey().toLowerCase().startsWith("dataset_")) it.remove();

		}

		Map<String, Boolean> dataSetsImportable = validateDataSetInstances(file);

		// determine if validation succeeded
		boolean ok = true;
		if (entitiesImportable != null)
		{
			for (Boolean b : entitiesImportable.values())
				ok = ok & b;

			for (Collection<String> fields : xlsValidator.getFieldsRequired().values())
				ok = ok & (fields == null || fields.isEmpty());
		}
		if (dataSetsImportable != null)
		{
			for (Boolean b : dataSetsImportable.values())
				ok = ok & b;
		}

		// if no error, set prognosis, set file, and continue
		this.model.setFile(file);
		this.model.setEntitiesImportable(entitiesImportable);
		this.model.setDataImportable(dataSetsImportable);
		this.model.setFieldsDetected(xlsValidator.getFieldsImportable());
		this.model.setFieldsRequired(xlsValidator.getFieldsRequired());
		this.model.setFieldsAvailable(xlsValidator.getFieldsAvailable());
		this.model.setFieldsUnknown(xlsValidator.getFieldsUnknown());
		this.model.setValidationError(!ok);
	}

	private void doImport(Database db, String entityAction, String targetAction, String featureAction) throws Exception
	{
		try
		{
			// convert input to database action
			DatabaseAction entityDbAction = toDatabaseAction(entityAction);
			if (entityDbAction == null) throw new IOException("unknown database action: " + entityAction);

			// import entities
			ExcelImport.importAll(this.model.getFile(), db, new SimpleTuple(), null, entityDbAction, "", true);

			// import dataset instances
			if (this.model.getDataImportable() != null)
			{
				DatabaseAction targetDbAction = toDatabaseAction(targetAction);
				if (targetDbAction == null) throw new IOException("unknown database action: " + targetDbAction);

				DatabaseAction featureDbAction = toDatabaseAction(featureAction);
				if (featureDbAction == null) throw new IOException("unknown database action: " + featureDbAction);

				List<String> dataSetSheetNames = new ArrayList<String>();
				for (Entry<String, Boolean> entry : this.model.getDataImportable().entrySet())
					if (entry.getValue() == true) dataSetSheetNames.add("dataset_" + entry.getKey());

				new DataSetImporter(db).importXLS(this.model.getFile(), dataSetSheetNames, targetDbAction,
						featureDbAction);
			}

			this.model.setImportError(false);
		}
		catch (Exception e)
		{
			this.model.setImportError(true);
			throw e;
		}
	}

	private DatabaseAction toDatabaseAction(String actionStr)
	{
		// convert input to database action
		DatabaseAction dbAction;

		if (actionStr.equals("add")) dbAction = DatabaseAction.ADD;
		else if (actionStr.equals("add_ignore")) dbAction = DatabaseAction.ADD_IGNORE_EXISTING;
		else if (actionStr.equals("add_update")) dbAction = DatabaseAction.ADD_UPDATE_EXISTING;
		else if (actionStr.equals("update")) dbAction = DatabaseAction.UPDATE;
		else if (actionStr.equals("update_ignore")) dbAction = DatabaseAction.UPDATE_IGNORE_MISSING;
		else
			dbAction = null;

		return dbAction;
	}

	private Map<String, Boolean> validateDataSetInstances(File file) throws BiffException, IOException
	{
		// get dataset entity names
		Sheet sheet = Workbook.getWorkbook(file).getSheet("dataset");
		if (sheet == null) return null;

		// get sheet names
		String[] sheetNames = Workbook.getWorkbook(file).getSheetNames();
		List<String> dataSetSheets = new ArrayList<String>();
		for (String sheetName : sheetNames)
		{
			if (sheetName.toLowerCase().startsWith("dataset_"))
			{
				String dataSetName = sheetName.substring("dataset_".length());
				dataSetSheets.add(dataSetName);
			}
		}

		int rows = sheet.getRows();
		if (rows <= 1) return Collections.emptyMap();

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
					nameList.add(datasetName.toLowerCase());
				}
			}
		}

		Map<String, Boolean> dataSetValidationMap = new LinkedHashMap<String, Boolean>();
		for (String dataSetSheet : dataSetSheets)
		{
			dataSetValidationMap.put(dataSetSheet, nameList.contains(dataSetSheet));
		}

		return dataSetValidationMap;
	}

	@Override
	public void reload(Database db)
	{

	}

}

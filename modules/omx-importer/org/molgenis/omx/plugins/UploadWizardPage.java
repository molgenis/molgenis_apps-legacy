package org.molgenis.omx.plugins;

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

import org.molgenis.framework.db.Database;
import org.molgenis.util.Tuple;

import app.ImportWizardExcelPrognosis;

public class UploadWizardPage extends WizardPage
{
	public UploadWizardPage()
	{
		super("Upload file", "UploadWizardPage.ftl");
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		File file = request.getFile("upload");

		if (file == null)
		{
			getWizard().setErrorMessage("No file selected");
		}
		else if (!file.getName().endsWith(".xls"))
		{
			getWizard().setErrorMessage("File does not end with '.xls', other formats are not supported.");
		}
		else
		{
			try
			{
				validateInput(db, file);
			}
			catch (Exception e)
			{
				getWizard().setErrorMessage("Error validating import file: " + e.getMessage());
				logger.error("Exception validating import file", e);
			}
		}
	}

	private void validateInput(Database db, File file) throws Exception
	{
		// validate entity sheets
		ImportWizardExcelPrognosis xlsValidator = new ImportWizardExcelPrognosis(db, file);

		// remove data sheets
		Map<String, Boolean> entitiesImportable = xlsValidator.getSheetsImportable();
		for (Iterator<Entry<String, Boolean>> it = entitiesImportable.entrySet().iterator(); it.hasNext();)
		{
			if (it.next().getKey().toLowerCase().startsWith("dataset_"))
			{
				it.remove();
			}
		}

		Map<String, Boolean> dataSetsImportable = validateDataSetInstances(file);

		// determine if validation succeeded
		boolean ok = true;
		if (entitiesImportable != null)
		{
			for (Boolean b : entitiesImportable.values())
			{
				ok = ok & b;
			}

			for (Collection<String> fields : xlsValidator.getFieldsRequired().values())
			{
				ok = ok & (fields == null || fields.isEmpty());
			}
		}

		if (dataSetsImportable != null)
		{
			for (Boolean b : dataSetsImportable.values())
			{
				ok = ok & b;
			}
		}

		if (ok)
		{
			getWizard().setFile(file);
			getWizard().setSuccessMessage("File is validated and can be imported.");
		}
		else
		{
			getWizard().setValidationMessage(
					"File did not pass validation see results below. Please resolve the errors and try again.");
		}

		// if no error, set prognosis, set file, and continue
		getWizard().setEntitiesImportable(entitiesImportable);
		getWizard().setDataImportable(dataSetsImportable);
		getWizard().setFieldsDetected(xlsValidator.getFieldsImportable());
		getWizard().setFieldsRequired(xlsValidator.getFieldsRequired());
		getWizard().setFieldsAvailable(xlsValidator.getFieldsAvailable());
		getWizard().setFieldsUnknown(xlsValidator.getFieldsUnknown());
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

}

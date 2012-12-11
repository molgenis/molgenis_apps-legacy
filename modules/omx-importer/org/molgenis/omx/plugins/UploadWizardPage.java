package org.molgenis.omx.plugins;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.observ.DataSet;
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

		Map<String, Boolean> dataSetsImportable = validateDataSetInstances(db, file);

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

	private Map<String, Boolean> validateDataSetInstances(Database db, File file) throws IOException,
			DatabaseException, InvalidFormatException
	{
		Map<String, Boolean> dataSetValidationMap = new LinkedHashMap<String, Boolean>();

		Workbook workbook = WorkbookFactory.create(file);

		// Get the dataset identifiers from the 'Dataset' sheet
		Sheet datasetSheet = workbook.getSheet("dataset");
		int datasetSheetRows = datasetSheet == null ? 0 : datasetSheet.getPhysicalNumberOfRows();

		List<String> datasetIdentifiers = new ArrayList<String>();
		if (datasetSheetRows > 1)
		{
			Row row = datasetSheet.getRow(0);
			if (row != null)
			{
				for (int col = 0; col < row.getPhysicalNumberOfCells(); col++)
				{
					Cell cell = row.getCell(col);
					if (cell != null)
					{
						cell.setCellType(Cell.CELL_TYPE_STRING);

						if ((cell.getStringCellValue() != null)
								&& cell.getStringCellValue().trim().equalsIgnoreCase("identifier"))
						{
							for (int rowNum = 1; rowNum < datasetSheetRows; rowNum++)
							{
								Row r = datasetSheet.getRow(rowNum);
								if (r != null)
								{
									cell = r.getCell(col);
									if (cell != null)
									{
										cell.setCellType(Cell.CELL_TYPE_STRING);
										if (cell.getStringCellValue() != null)
										{
											datasetIdentifiers.add(cell.getStringCellValue().toLowerCase());
										}
									}
								}

							}
						}
					}
				}
			}
		}

		// Check the matrix sheets
		for (int i = 0; i < workbook.getNumberOfSheets(); i++)
		{
			String sheetName = workbook.getSheetName(i);

			if (sheetName.toLowerCase().startsWith("dataset_"))
			{
				String identifier = sheetName.substring("dataset_".length());

				if (!db.find(DataSet.class, new QueryRule(DataSet.IDENTIFIER, Operator.EQUALS, identifier)).isEmpty())
				{
					// Dataset exists in database, so can be imported
					dataSetValidationMap.put(identifier, true);
				}
				else
				{
					// Dataset does not exist in db, check if the identifier is
					// in the 'Dataset' sheet
					dataSetValidationMap.put(identifier, datasetIdentifiers.contains(identifier));
				}
			}
		}

		return dataSetValidationMap;
	}
}

package matrix.general;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import matrix.implementations.binary.BinaryDataMatrixInstance;
import matrix.implementations.binary.BinaryDataMatrixWriter;
import matrix.implementations.csv.CSVDataMatrixWriter;
import matrix.implementations.database.DatabaseDataMatrixWriter;

import org.molgenis.core.MolgenisFile;
import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.util.CsvWriter;
import org.molgenis.util.SimpleTuple;
import org.molgenis.util.Tuple;

import decorators.NameConvention;
import filehandling.generic.PerformUpload;

public class Importer
{

	public void performImport(Tuple request, Data data, Database db) throws Exception
	{

		File importFile = null;

		// special: upload existing xQTL binary matrix
		if (request.getString("__action").equals("uploadBinary"))
		{
			importFile = request.getFile("uploadBinaryFile");

			// update this 'Data' with the info from the binary file
			// but not name/investigationname
			// additionally, set storage to Binary :)
			BinaryDataMatrixInstance bmi = new BinaryDataMatrixInstance(importFile);
			data.setFeatureType(bmi.getData().getFeatureType());
			data.setTargetType(bmi.getData().getTargetType());
			data.setValueType(bmi.getData().getValueType());
			data.setStorage("Binary");

			db.update(data);

			// code from
			// /molgenis_apps/modules/xgap/matrix/implementations/binary/BinaryDataMatrixWriter.java
			// upload as a MolgenisFile, type 'BinaryDataMatrix'
			HashMap<String, String> extraFields = new HashMap<String, String>();
			extraFields.put("data_" + Data.ID, data.getId().toString());
			extraFields.put("data_" + Data.NAME, data.getName());

			PerformUpload.doUpload(db, true, data.getName() + ".bin", "BinaryDataMatrix", importFile, extraFields,
					false);

			return;
		}

		if (request.getString("__action").equals("uploadTextArea"))
		{
			String content = request.getString("inputTextArea");
			File inputTextAreaContent = new File(System.getProperty("java.io.tmpdir") + File.separator
					+ "tmpTextAreaInput" + System.nanoTime() + ".txt");
			BufferedWriter out = new BufferedWriter(new FileWriter(inputTextAreaContent));
			out.write(content);
			out.close();
			importFile = inputTextAreaContent;
		}
		else
		{
			importFile = request.getFile("upload");
		}

		// apply preprocessing if called for
		PreProcessMatrix pm = null;
		if (request.getString("prependToRows") != null || request.getString("prependToCols") != null
				|| request.getString("escapeRows") != null || request.getString("escapeCols") != null
				|| request.getString("trimTextElements") != null)
		{
			pm = new PreProcessMatrix(importFile);
		}
		if (request.getString("prependToRows") != null)
		{
			pm.prependUnderscoreToRowNames();
		}
		if (request.getString("prependToCols") != null)
		{
			pm.prependUnderscoreToColNames();
		}
		if (request.getString("escapeRows") != null)
		{
			pm.escapeRowNames();
		}
		if (request.getString("escapeCols") != null)
		{
			pm.escapeColNames();
		}
		if (request.getString("trimTextElements") != null)
		{
			pm.trimTextElements();
		}

		if (request.getString("prependToRows") != null || request.getString("prependToCols") != null
				|| request.getString("escapeRows") != null || request.getString("escapeCols") != null
				|| request.getString("trimTextElements") != null)
		{
			File result = pm.getResult();
			if (!result.exists())
			{
				throw new Exception("Import file '" + result.getAbsolutePath() + " does not exist");
			}
			performImport(result, data, db);
		}
		else
		{
			if (importFile == null || !importFile.exists())
			{
				throw new Exception("No valid import file provided");
			}
			performImport(importFile, data, db);
		}
	}

	public void performImport(File uploadedFile, Data data, Database db) throws Exception
	{

		DataMatrixHandler dmh = new DataMatrixHandler(db);

		// check if uploaded file is there
		if (uploadedFile == null || !uploadedFile.exists())
		{
			throw new DatabaseException("No file selected for upload.");
		}

		// check if the matrix elements exists in another source of data first
		if (data.getStorage().equals("Database"))
		{
			if (dmh.isDataMatrixStoredInDatabase(data, db))
			{
				throw new DatabaseException("Database source already exists for source type '" + data.getStorage()
						+ "'");
			}
		}
		else
		{
			boolean realFilePresent;
			try
			{
				String type = data.getStorage() + "DataMatrix";
				dmh.getFileDirectly(NameConvention.escapeFileName(data.getName()), dmh.getExtension(data.getStorage()),
						type, db);
				realFilePresent = true;
			}
			catch (FileNotFoundException fnfe)
			{
				realFilePresent = false;
			}

			// original check here: dmh.findSourceFile(data, db) != null which
			// failed when there is a leftover MF but no actual file
			// so now there's this check instead
			if (realFilePresent)
			{
				throw new DatabaseException("There is already a storage file named '"
						+ NameConvention.escapeFileName(data.getName()) + "' which is used when escaping the name '"
						+ data.getName() + "'. Please rename your Data matrix or contact your admin.");
			}

			// no file present, remove the MF link if there is one
			// this is needed when you throw away the file on the file system
			// 'without telling the database'
			// and subsequently try to upload a new matrix, because otherwise
			// the names would clash
			MolgenisFile mf = dmh.findMolgenisFile(data, db);
			if (mf != null)
			{
				db.remove(mf);
			}
		}

		if (!(uploadedFile.getName().endsWith(".txt") || uploadedFile.getName().endsWith(".csv") || uploadedFile
				.getName().endsWith(".xls")))
		{
			throw new Exception("Expecting a file with extension .txt, .csv or .xls");
		}

		if (uploadedFile.getName().endsWith(".xls"))
		{
			uploadedFile = excelToCsv(uploadedFile);
		}

		// do import
		if (data.getStorage().equals("Database"))
		{
			// new DatabaseDataMatrixWriter(data, uploadedFile, db, false);
			new DatabaseDataMatrixWriter(data, uploadedFile, db);
		}
		else if (data.getStorage().equals("Binary"))
		{
			new BinaryDataMatrixWriter(data, uploadedFile, db);
		}
		else if (data.getStorage().equals("CSV"))
		{
			new CSVDataMatrixWriter(data, uploadedFile, db);
		}
		else
		{
			throw new DatabaseException("Unknown matrix source: " + data.getStorage() + "");
		}

	}

	private File excelToCsv(File excelFile) throws Exception
	{
		System.out.println("Converting Excel file " + excelFile.getName() + " to CSV..");
		Workbook workbook = Workbook.getWorkbook(excelFile);
		if (workbook.getSheetNames().length > 1)
		{
			throw new Exception("Expected 1 sheet in Excel file containing your matrix data");
		}
		Sheet sheet = workbook.getSheet(0);

		List<String> headers = new ArrayList<String>();
		Cell[] headerCells = sheet.getRow(0); // assume headers are on first
												// line
		if (headerCells.length == 0)
		{
			throw new Exception("No headers found in Excel file");
		}

		if (!headerCells[0].getContents().equals(""))
		{
			throw new Exception("Top-left cell must be empty to ensure correct matrix format");
		}
		else
		{
			headers.add("");
		}

		for (int i = 1; i < headerCells.length; i++)
		{
			if (headerCells[i].getContents().equals(""))
			{
				throw new Exception("Empty header at cell nr " + (i + 1));
			}
			else
			{
				headers.add(headerCells[i].getContents());
			}
		}

		File out = new File(System.getProperty("java.io.tmpdir") + File.separator + "excel_to_csv_"
				+ System.currentTimeMillis() + ".txt");

		PrintWriter pw = new PrintWriter(out);
		CsvWriter cw = new CsvWriter(pw, headers);
		cw.setMissingValue("");
		cw.writeHeader();

		for (int rowIndex = 1; rowIndex < sheet.getRows(); rowIndex++)
		{
			Tuple t = new SimpleTuple();

			Cell[] row = sheet.getRow(rowIndex);
			for (int colIndex = 0; colIndex < row.length; colIndex++)
			{
				Cell c = row[colIndex];
				if (colIndex > (headers.size() - 1))
				{
					throw new Exception("More columns than headers (bad element at rowindex " + rowIndex + " colindex "
							+ colIndex + ")");
				}
				if (colIndex == 0 && c.getContents().equals(""))
				{
					throw new Exception("Empty first cell in row " + rowIndex);
				}

				if (c.getContents() != null)
				{
					t.set(headers.get(colIndex), c.getContents());
				}
			}
			cw.writeRow(t);
		}
		cw.close();
		System.out.println("..completed, returning " + out.getName());
		return out;
	}

}

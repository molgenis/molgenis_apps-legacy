package org.molgenis.omx.dataset;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.tupletable.TableException;
import org.molgenis.framework.tupletable.TupleTable;
import org.molgenis.framework.tupletable.impl.CsvTable;
import org.molgenis.model.elements.Field;
import org.molgenis.observ.DataSet;
import org.molgenis.observ.ObservableFeature;
import org.molgenis.observ.ObservationSet;
import org.molgenis.observ.ObservationTarget;
import org.molgenis.observ.ObservedValue;
import org.molgenis.util.CsvWriter;
import org.molgenis.util.SimpleTuple;
import org.molgenis.util.Tuple;

public class DataSetImporter
{
	private static final Logger LOG = Logger.getLogger(DataSetImporter.class);
	private static final String ENTITY_DATASET_PREFIX = "DataSet";
	private Database db;

	public DataSetImporter(Database db)
	{
		if (db == null) throw new IllegalArgumentException();
		this.db = db;
	}

	public void importXLS(File file, List<String> dataSetSheetNames) throws IOException, DatabaseException
	{
		// fixes the problem where, even though decimals have a "." they are
		// still read as "," because of the locale!
		// TODO: dangerous: entire application locale changes! but workbook
		// locale settings don't seem to have an effect...
		Locale defaultLocale = Locale.getDefault();
		Locale.setDefault(Locale.US);

		Workbook workbook = null;
		try
		{
			String sheetPrefix = ENTITY_DATASET_PREFIX.toLowerCase() + '_';
			workbook = Workbook.getWorkbook(file);
			for (Sheet sheet : workbook.getSheets())
			{
				if (dataSetSheetNames.contains(sheet.getName()))
				{
					String identifier = sheet.getName().substring(sheetPrefix.length());
					File csvFile = convertToCSVFile(sheet, identifier);
					importCSV(csvFile, identifier);
				}
			}
		}
		catch (BiffException e)
		{
			throw new IOException(e);
		}
		finally
		{
			Locale.setDefault(defaultLocale); // restore locale
			if (workbook != null) workbook.close();
		}
	}

	private void importCSV(File file, String identifier) throws IOException, DatabaseException
	{
		LOG.info("importing dataset " + identifier + " from file " + file + "...");

		List<DataSet> dataSets = db.find(DataSet.class, new QueryRule(DataSet.IDENTIFIER, Operator.EQUALS, identifier));
		if (dataSets == null || dataSets.isEmpty())
		{
			LOG.warn("dataset " + identifier + " does not exist in db");
			return;
		}
		else if (dataSets.size() > 1)
		{
			LOG.warn("multiple datasets exist for identifier " + identifier);
			return;
		}

		DataSet dataSet = dataSets.get(0);

		TupleTable csvTable = null;
		try
		{
			db.beginTx();
			csvTable = new CsvTable(file);
			List<Field> headerFields = csvTable.getAllColumns();
			for (Tuple row : csvTable.getRows())
			{
				// find current observation target
				String observationTargetIdentifier = row.getString(0);
				List<ObservationTarget> observationTargets = db.find(ObservationTarget.class, new QueryRule(
						ObservationTarget.IDENTIFIER, Operator.EQUALS, observationTargetIdentifier));
				if (observationTargets == null || observationTargets.isEmpty()) throw new DatabaseException(
						"ObservationTarget " + observationTargetIdentifier + " does not exist in db");
				ObservationTarget observationTarget = observationTargets.get(0);

				// create observation set
				ObservationSet observationSet = new ObservationSet();
				observationSet.setTarget(observationTarget);
				observationSet.setPartOfDataSet(dataSet);
				db.add(observationSet);

				for (int col = 1; col < headerFields.size(); ++col)
				{
					// find current observation feature
					String observableFeatureIdentifier = headerFields.get(col).getLabel();
					List<ObservableFeature> observableFeatures = db.find(ObservableFeature.class, new QueryRule(
							ObservableFeature.IDENTIFIER, Operator.EQUALS, observableFeatureIdentifier));
					if (observableFeatures == null || observableFeatures.isEmpty()) throw new IOException(
							"ObservableFeature " + observableFeatureIdentifier + " does not exist in db");
					ObservableFeature observableFeature = observableFeatures.get(0);

					// create observed value
					String value = row.getString(col);
					ObservedValue observedValue = new ObservedValue();
					observedValue.setFeature(observableFeature);
					observedValue.setValue(value);
					observedValue.setObservationSet(observationSet);

					// add to db
					db.add(observedValue);
				}
			}
			db.commitTx();
		}
		catch (DatabaseException e)
		{
			db.rollbackTx();
			throw e;
		}
		catch (Exception e)
		{
			db.rollbackTx();
			throw new IOException(e);
		}
		finally
		{
			if (csvTable != null) try
			{
				csvTable.close();
			}
			catch (TableException e)
			{
				throw new IOException(e);
			}
		}
	}

	private File convertToCSVFile(Sheet sheet, String identifier) throws IOException
	{
		String tmpFileName = "tmp" + ENTITY_DATASET_PREFIX + '_' + identifier + ".txt";

		// copied from *ExcelReader.java
		File tmpDataSet = new File(System.getProperty("java.io.tmpdir") + File.separator + tmpFileName);
		if (tmpDataSet.exists())
		{
			boolean deleteSuccess = tmpDataSet.delete();
			if (!deleteSuccess)
			{
				throw new IOException("Deletion of tmp file '" + tmpFileName + "' failed, cannot proceed.");
			}
		}
		boolean createSuccess = tmpDataSet.createNewFile();
		if (!createSuccess)
		{
			throw new IOException("Creation of tmp file '" + tmpFileName + "' failed, cannot proceed.");
		}
		boolean fileHasHeaders = writeSheetToFile(sheet, tmpDataSet);
		if (fileHasHeaders)
		{
			return tmpDataSet;
		}
		else
		{
			tmpDataSet.delete();
			throw new IOException("error occured writing sheet to file: " + tmpFileName);
		}
	}

	private boolean writeSheetToFile(Sheet sheet, File file) throws IOException
	{
		// get headers
		Cell[] headerCells = sheet.getRow(0);
		final int nrHeaders = headerCells.length;
		if (nrHeaders == 0) return false;

		List<String> headers = new ArrayList<String>(nrHeaders);
		for (Cell headerCell : headerCells)
			headers.add(headerCell.getContents());

		// create writer
		CsvWriter cw;
		try
		{
			cw = new CsvWriter(new PrintWriter(file, "UTF-8"), headers);
		}
		catch (UnsupportedEncodingException e)
		{
			throw new RuntimeException(e);
		}
		cw.setMissingValue("");

		// write csv to file
		try
		{
			cw.writeHeader();
			final int nrRows = sheet.getRows();
			for (int rowIndex = 1; rowIndex < nrRows; rowIndex++)
			{
				Tuple t = new SimpleTuple();
				int colIndex = 0;
				for (Cell c : sheet.getRow(rowIndex))
				{
					if (colIndex < headers.size() && c.getContents() != null)
					{
						t.set(headers.get(colIndex), c.getContents());
					}
					colIndex++;
				}
				cw.writeRow(t);
			}
		}
		finally
		{
			cw.close();
		}
		return true;
	}
}

package org.molgenis.omx.dataset;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
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
import org.molgenis.util.ExcelUtils;
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
		Workbook workbook = null;

		String sheetPrefix = ENTITY_DATASET_PREFIX.toLowerCase() + '_';
		try
		{
			workbook = WorkbookFactory.create(file);
		}
		catch (InvalidFormatException e)
		{
			String msg = "InvalidFormatException creating Workbook for file [" + file.getName() + "]";
			LOG.error(msg, e);
			throw new IOException(msg, e);
		}

		for (int i = 0; i < workbook.getNumberOfSheets(); i++)
		{
			String sheetName = workbook.getSheetName(i);
			if (dataSetSheetNames.contains(sheetName))
			{
				String identifier = sheetName.substring(sheetPrefix.length());
				File csvFile = convertToCSVFile(workbook.getSheetAt(i), identifier);
				importCSV(csvFile, identifier);
			}
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
		boolean fileHasHeaders = ExcelUtils.writeSheetToFile(sheet, tmpDataSet, false);
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

}

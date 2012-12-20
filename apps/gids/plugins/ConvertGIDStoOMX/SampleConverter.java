package plugins.ConvertGIDStoOMX;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.molgenis.io.TupleWriter;
import org.molgenis.io.csv.CsvWriter;
import org.molgenis.io.excel.ExcelReader;
import org.molgenis.io.excel.ExcelSheetReader;
import org.molgenis.io.excel.ExcelSheetWriter;
import org.molgenis.io.excel.ExcelWriter;
import org.molgenis.io.excel.ExcelWriter.FileFormat;
import org.molgenis.io.processor.LowerCaseProcessor;
import org.molgenis.io.processor.TrimProcessor;
import org.molgenis.util.tuple.KeyValueTuple;
import org.molgenis.util.tuple.Tuple;

public class SampleConverter
{

	private Set<String> listOfDoubleSamples = new HashSet<String>();
	private int counter = 0;
	// private Map<String, String> observationTargetMap = new HashMap<String,
	// String>();

	private static String OUTPUTDIR = null;
	private static String PROJECT = null;
	private Tuple features = null;
	MakeEntityNameAndIdentifier mkObsTarget = null;
	MakeEntityNameAndIdentifier mkObsFeature = null;
	List<MakeEntityNameAndIdentifier> mkObsTargetlist = new ArrayList<MakeEntityNameAndIdentifier>();
	List<MakeEntityNameAndIdentifier> mkObsFeaturelist = new ArrayList<MakeEntityNameAndIdentifier>();

	public void convert(InputStream in, OutputStream out, String outputdir, String projectName) throws IOException
	{

		OUTPUTDIR = outputdir;
		PROJECT = projectName;
		ExcelReader excelReader = new ExcelReader(in);
		excelReader.addCellProcessor(new TrimProcessor(false, true));
		TupleWriter csvWriter = new CsvWriter(new OutputStreamWriter(out, Charset.forName("UTF-8")));
		csvWriter.addCellProcessor(new LowerCaseProcessor(true, false));
		ArrayList<String> listOfEntity = new ArrayList<String>();
		listOfEntity.add("dataset");
		listOfEntity.add("observationtarget");
		listOfEntity.add("observableFeature");
		listOfEntity.add("dataset_" + PROJECT.toLowerCase().trim());

		mkmetadataExcelFile(listOfEntity);

		try
		{
			// write data
			for (ExcelSheetReader sheetReader : excelReader)
			{
				boolean writeHeader = true;
				for (Iterator<Tuple> it = sheetReader.iterator(); it.hasNext();)
				{
					Tuple row = it.next();
					if (writeHeader)
					{
						csvWriter.writeColNames(row);
						writeHeader = false;

						features = row;
					}
					// If the sample name exists
					if (row.getString("id_sample") != null && !row.getString("id_sample").isEmpty())
					{
						String sampleId = row.getString("id_sample");
						if (checkIfDouble(sampleId))
						{
							System.out.println("Double entry: " + sampleId + " has been removed");

						}
						else
						{
							// Get the targets for metadatafile
							mkObsTarget = new MakeEntityNameAndIdentifier(sampleId, sampleId);
							mkObsTargetlist.add(mkObsTarget);
							// Write the real data
							csvWriter.write(row);
						}
					}
					else
					{
						HashMap<String, String> hash = new HashMap<String, String>();
						for (Iterator<String> it2 = row.getColNames(); it2.hasNext();)
						{
							String keyTuple = it2.next();
							if (keyTuple.equals("id_sample"))
							{
								hash.put(keyTuple, "unknown");
								// Make a identifier for this sample
								hash.put(keyTuple, emptySample());
							}
							else
							{
								hash.put(keyTuple, row.getString(keyTuple));
							}

						}
						KeyValueTuple tup = new KeyValueTuple(hash);
						// The identifier and sample are now set for this former
						// empty sample name
						csvWriter.write(tup);
					}
				}
			}
			// Fill a list with all the ObservableFeatures in the file
			if (features != null)
			{
				makeFeaturesList();
			}

			// Write the metadata to a file for the features
			mkMetadataFileObservableFeature();
			// Write the metadata to a file for the targets
			mkMetadataFileObservationTarget();

		}
		finally
		{
			try
			{
				excelReader.close();
			}
			catch (IOException e)
			{
			}
			try
			{
				csvWriter.close();
			}
			catch (IOException e)
			{
			}
		}
	}

	public boolean checkIfDouble(String sample)
	{

		if (!listOfDoubleSamples.contains(sample))
		{
			listOfDoubleSamples.add(sample);
			return false;
		}
		else
		{
			return true;
		}
	}

	public String emptySample()
	{
		String sample = "Dummy_2012_" + PROJECT + "_" + (++counter);
		mkObsTarget = new MakeEntityNameAndIdentifier("unknown", sample);
		mkObsTargetlist.add(mkObsTarget);
		return sample;
	}

	public void mkMetadataFileObservationTarget() throws IOException
	{

		OutputStream osMD = new FileOutputStream(OUTPUTDIR + PROJECT + "_MetaDataObservationTarget.csv");

		TupleWriter csvWriterMD = new CsvWriter(new OutputStreamWriter(osMD, Charset.forName("UTF-8")));
		csvWriterMD.addCellProcessor(new LowerCaseProcessor(true, false));

		Map<String, String> hashObs = new HashMap<String, String>();

		KeyValueTuple kvtHeader = new KeyValueTuple(hashObs);

		hashObs.put("identifier", "identifier");
		hashObs.put("name", "name");
		csvWriterMD.writeColNames(kvtHeader);

		for (MakeEntityNameAndIdentifier i : mkObsTargetlist)
		{
			hashObs = new HashMap<String, String>();
			KeyValueTuple kvt = new KeyValueTuple(hashObs);
			hashObs.put("identifier", i.getIdentifier());
			hashObs.put("name", i.getName());
			csvWriterMD.write(kvt);

		}
		csvWriterMD.close();

	}

	private void makeFeaturesList()
	{
		for (Iterator<String> it2 = features.getColNames(); it2.hasNext();)
		{
			String keyTuple = it2.next();

			mkObsFeature = new MakeEntityNameAndIdentifier(keyTuple, keyTuple);
			mkObsFeaturelist.add(mkObsFeature);
		}

	}

	public void mkMetadataFileObservableFeature() throws IOException
	{

		OutputStream osMD = new FileOutputStream(OUTPUTDIR + PROJECT + "_MetaDataObservableFeature.csv");

		TupleWriter csvWriterMD = new CsvWriter(new OutputStreamWriter(osMD, Charset.forName("UTF-8")));
		csvWriterMD.addCellProcessor(new LowerCaseProcessor(true, false));

		Map<String, String> hashFeatures = new HashMap<String, String>();

		hashFeatures.put("identifier", "identifier");
		hashFeatures.put("name", "name");
		KeyValueTuple kvtHeader = new KeyValueTuple(hashFeatures);
		csvWriterMD.writeColNames(kvtHeader);
		for (MakeEntityNameAndIdentifier m : mkObsFeaturelist)
		{
			KeyValueTuple kvt = new KeyValueTuple(hashFeatures);
			hashFeatures.put("identifier", m.getIdentifier());
			hashFeatures.put("name", m.getName());
			csvWriterMD.write(kvt);
		}
		csvWriterMD.close();
	}

	public void mkmetadataExcelFile(ArrayList<String> listOfEntity) throws IOException
	{

		OutputStream osMD = new FileOutputStream(OUTPUTDIR + PROJECT + "_metadata.xls");

		ExcelWriter excelWriterMD = new ExcelWriter(osMD, FileFormat.XLS);
		excelWriterMD.addCellProcessor(new LowerCaseProcessor(true, false));
		Map<String, String> hashdataset = new HashMap<String, String>();

		hashdataset.put("identifier", "identifier");
		hashdataset.put("name", "name");
		hashdataset.put("protocolused_identifier", "protocolused_identifier");
		KeyValueTuple kvtHeader = new KeyValueTuple(hashdataset);
		for (String sheetName : listOfEntity)
		{
			if (sheetName.equals("dataset"))
			{
				ExcelSheetWriter esw = excelWriterMD.createSheet("dataset");
				esw.write(kvtHeader);
				KeyValueTuple kvt = new KeyValueTuple(hashdataset);
				hashdataset.put("protocolused_identifier", "");
				hashdataset.put("identifier", PROJECT.toLowerCase());
				hashdataset.put("name", PROJECT.toLowerCase());
				esw.write(kvt);
			}
			else
			{
				excelWriterMD.createSheet(sheetName);
			}
		}
		excelWriterMD.close();

	}

}

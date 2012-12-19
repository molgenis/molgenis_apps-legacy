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
	private Tuple features = null;
	MakeObservationTarget mkObsT = null;
	List<MakeObservationTarget> mkObsTlist = new ArrayList<MakeObservationTarget>();

	public void convert(InputStream in, OutputStream out) throws IOException
	{

		ExcelReader excelReader = new ExcelReader(in);
		excelReader.addCellProcessor(new TrimProcessor(false, true));
		TupleWriter csvWriter = new CsvWriter(new OutputStreamWriter(out, Charset.forName("UTF-8")));
		csvWriter.addCellProcessor(new LowerCaseProcessor(true, false));

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
					if (row.getString("id_sample") != null)
					{
						String sampleId = row.getString("id_sample");
						if (checkIfDouble(sampleId))
						{
							System.out.println("Double entry: " + sampleId + " has been removed");

						}
						else
						{
							mkObsT = new MakeObservationTarget(sampleId, sampleId);
							mkObsTlist.add(mkObsT);
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
								hash.put(keyTuple, emptySample());
							}
							else
							{
								hash.put(keyTuple, row.getString(keyTuple));
							}

						}
						KeyValueTuple tup = new KeyValueTuple(hash);
						// Write the edited row
						csvWriter.write(tup);
					}
				}
			}
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
		String sample = "Dummy_2012_" + (++counter);
		mkObsT = new MakeObservationTarget("unknown", sample);
		mkObsTlist.add(mkObsT);
		return sample;
	}

	public void mkMetadataFileObservationTarget() throws IOException
	{

		OutputStream osMD = new FileOutputStream(
				"/Users/Roan/Work/NewGIDS/Export GIDS/Cohorts/Converted/CeliacMetaDataObservationTarget.csv");

		TupleWriter csvWriterMD = new CsvWriter(new OutputStreamWriter(osMD, Charset.forName("UTF-8")));
		csvWriterMD.addCellProcessor(new LowerCaseProcessor(true, false));
		// KeyValueTuple
		Map<String, String> hashObs = new HashMap<String, String>();

		KeyValueTuple kvtHeader = new KeyValueTuple(hashObs);

		hashObs.put("identifier", "identifier");
		hashObs.put("name", "name");
		csvWriterMD.writeColNames(kvtHeader);

		for (MakeObservationTarget i : mkObsTlist)
		{
			hashObs = new HashMap<String, String>();
			KeyValueTuple kvt = new KeyValueTuple(hashObs);
			hashObs.put("identifier", i.getIdentifier());
			hashObs.put("name", i.getName());
			csvWriterMD.write(kvt);

		}

		System.out.println("CLOSING");
		csvWriterMD.close();
		System.out.println("CLOSED");
		// closeMe(csvWriterMD);
	}

	// public void closeMe(TupleWriter twriter)
	// {
	//
	// try
	// {
	// twriter.close();
	// }
	// catch (IOException e)
	// {
	// }
	//
	// }
}

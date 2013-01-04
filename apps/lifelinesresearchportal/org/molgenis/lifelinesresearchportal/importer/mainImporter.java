package org.molgenis.lifelinesresearchportal.importer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.io.csv.CsvReader;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.protocol.Protocol;
import org.molgenis.protocol.ProtocolApplication;
import org.molgenis.util.tuple.Tuple;

import app.DatabaseFactory;

import com.google.common.collect.Lists;

public class mainImporter
{

	public mainImporter(Database db) throws FileNotFoundException, SQLException, IOException, Exception
	{
		this(new File("/Users/pc_iverson/Desktop/Input/HL7Files/voorbeeld1_dataset.csv"), db);
	}

	public mainImporter(File voorbeeld1_dataset, Database db) throws FileNotFoundException, SQLException, IOException,
			Exception
	{
		// BasicConfigurator.configure();

		// empty db
		// new Molgenis(
		// "apps/lifelinesresearchportal/org/molgenis/lifelinesresearchportal/lifelinesresearchportal.properties")
		// .updateDb(true);

		Logger.getRootLogger().setLevel(Level.ERROR);

		try
		{

			db.beginTx();

			// BufferedReader read = new BufferedReader(new FileReader(
			// "/Users/Roan/Work/LifeLines/voorbeeld1_dataset.csv"));

			List<Measurement> listOfMeas = new ArrayList<Measurement>();
			List<Individual> listOfIndv = new ArrayList<Individual>();
			List<ObservedValue> listOfValues = new ArrayList<ObservedValue>();
			List<String> listOfFeatures = new ArrayList<String>();
			List<ProtocolApplication> paList = new ArrayList<ProtocolApplication>();
			Investigation i = new Investigation();
			i.setName("LifeLines");
			db.add(i);
			Protocol p = new Protocol();
			p.setName(i.getName());
			db.add(p);

			CsvReader reader = new CsvReader(voorbeeld1_dataset);
			try
			{
				List<String> colNames = Lists.newArrayList(reader.colNamesIterator());

				// add measurements
				for (String name : colNames)
				{
					if (!"Pa_Id".equals(name))
					{
						Measurement m = new Measurement();
						m.setInvestigation(i);
						m.setName(name + "_" + i.getName());
						m.setLabel(name);

						listOfFeatures.add(m.getName());
						listOfMeas.add(m);
					}
				}
				db.add(listOfMeas);

				// read the rows into protocolApp and values
				int count = 1;
				for (Tuple row : reader)
				{
					Individual indi = new Individual();
					indi.setName(row.getString("Pa_Id"));
					indi.setInvestigation(i);
					listOfIndv.add(indi);

					final ProtocolApplication pa = new ProtocolApplication();
					pa.setName("pa" + count++);
					pa.setProtocol(p);
					pa.setInvestigation_Name(i.getName());
					paList.add(pa);

					for (String column : colNames)
					{
						if (!"Pa_Id".equals(column))
						{
							ObservedValue ob = new ObservedValue();
							ob.setFeature_Name(column + "_" + i.getName());
							ob.setTarget_Name(indi.getName());
							ob.setValue(row.getString(column));
							ob.setProtocolApplication_Name(pa.getName());
							ob.setInvestigation(i);

							listOfValues.add(ob);
						}
					}

					// write if list too long
					if (listOfValues.size() > 100000)
					{
						System.out.println("Done");
						db.add(listOfIndv);
						db.add(paList);
						db.add(listOfValues);

						listOfIndv.clear();
						paList.clear();
						listOfValues.clear();
					}
				}
			}
			finally
			{
				reader.close();
			}
			// add remaining
			db.add(listOfIndv);
			db.add(paList);
			db.add(listOfValues);

			listOfIndv.clear();
			paList.clear();
			listOfValues.clear();
			System.out.println("Finished");
			db.commitTx();

			// Set measurementnames to protocol
		}
		catch (Exception e)
		{
			db.rollbackTx();
			throw e;
		}
	}

	public static void main(String[] args) throws SQLException, Exception
	{
		new mainImporter(DatabaseFactory.create());
	}

}

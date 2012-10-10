package org.molgenis.gids.converters.phenoModelconverterandloader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.molgenis.datatable.model.CsvTable;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.util.Tuple;

public class CheckDOBandGender
{

	private String dob = "";
	private String gender = "";
	private Database db;

	public CheckDOBandGender(Database db) throws DatabaseException
	{
		this.db = db;
	}

	public List<Tuple> checkGenderAndDOB(CsvTable csvTable, String dobIdentifier, String genderIdentifier)
			throws DatabaseException
	{

		List<Individual> availableIndividuals = db.find(Individual.class);
		List<String> identifiersIndividual = new ArrayList<String>();
		List<String> checkingFeatures = new ArrayList<String>();
		checkingFeatures.add("gender");
		checkingFeatures.add("date_of_birth");
		List<Tuple> checkingTuple = new ArrayList<Tuple>();
		// Get all the individuals that are available in the database, because
		// we want to check
		// whether the individual from input file already exists in the database
		// based on gender and date of birth.
		if (availableIndividuals != null)
		{
			for (Individual i : availableIndividuals)
			{
				identifiersIndividual.add(i.getName());
			}

			// Make a complex query to get all the values that were created for
			// all
			// individuals on the two
			// variables "gender and date of birth"
			Query<ObservedValue> query = db.query(ObservedValue.class);

			query.addRules(new QueryRule(ObservedValue.TARGET_NAME, Operator.IN, identifiersIndividual));

			query.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.IN, checkingFeatures));

			Map<String, DOBandGender> hashMap = new HashMap<String, DOBandGender>();

			// Loop through all the values and collect information on gender and
			// date of birth for all individuals
			// and store these information in the model "DOBandGender" that is
			// created for each individual
			for (ObservedValue ov : query.find())
			{
				String targetName = ov.getTarget_Name();

				if (hashMap.containsKey(targetName))
				{

					if (hashMap.get(targetName).getGender() == null)
					{
						hashMap.get(targetName).setGender(ov.getValue());
					}
					else
					{
						hashMap.get(targetName).setDateOfBirth(ov.getValue());
					}

				}
				else
				{
					DOBandGender checker = new DOBandGender(targetName);
					checker.setGender(ov.getValue());
					hashMap.put(targetName, checker);
				}
			}
			//
			List<DOBandGender> listOfValues = (List<DOBandGender>) hashMap.values();

			// Read in the CscTupleTable to go through all the rows in the input
			// file,
			// compare the two data sources in terms of date of birth and gender
			for (Tuple tuple : csvTable.getRows())
			{
				String genderValue = tuple.getString(genderIdentifier);
				String dobValue = tuple.getString(dobIdentifier);

				for (DOBandGender each : listOfValues)
				{

					if (dobValue.equals(each.getDateOfBirth()))
					{
						if (genderValue.equals(each.getGender()))
						{
							checkingTuple.add(tuple);
							break;
						}
					}
				}
			}

		}
		return checkingTuple;
	}

	public void checkDOB(String dobValue, String genderValue) throws DatabaseException
	{
		List<ObservedValue> listOfValues = db.find(ObservedValue.class, new QueryRule(ObservedValue.FEATURE_NAME,
				Operator.EQUALS, "date_of_birth"));
		List<DOBandGender> allIndividualsInDB = new ArrayList<DOBandGender>();
		for (ObservedValue o : listOfValues)
		{
			if (o.getFeature_Name().equals("date_of_birth"))
			{
				DOBandGender dog = new DOBandGender(o.getTarget_Name());
				dog.setDateOfBirth(o.getValue());

				if (o.getValue().equals(dobValue))
				{
					checkGender(o.getTarget_Name(), genderValue);
				}
			}
		}
	}

	public void checkGender(String target, String gender) throws DatabaseException
	{
		List<ObservedValue> listOfValues = db.find(ObservedValue.class, new QueryRule(ObservedValue.TARGET_NAME,
				Operator.EQUALS, target));

		for (ObservedValue o : listOfValues)
		{
			if (o.getFeature_Name().equals("gender"))
			{
				if (o.getValue().equals(gender))
				{
					System.out.println(target + "! You guys look the same man!");
				}
			}
		}
	}
}

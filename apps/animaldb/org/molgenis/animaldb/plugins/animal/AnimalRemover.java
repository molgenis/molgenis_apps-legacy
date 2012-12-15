package org.molgenis.animaldb.plugins.animal;

import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.ObservedValue;

public class AnimalRemover
{
	private Database db;

	public AnimalRemover(Database db)
	{
		this.db = db;
	}

	public boolean removeAnimal(String animalName) throws DatabaseException
	{
		List<ObservedValue> selectedObsTargetValues = db.find(ObservedValue.class, new QueryRule(
				ObservedValue.TARGET_NAME, Operator.EQUALS, animalName));
		Individual selectedIndividual = db.find(Individual.class,
				new QueryRule(Individual.NAME, Operator.EQUALS, animalName)).get(0);

		db.remove(selectedObsTargetValues);
		db.remove(selectedIndividual);
		System.out.println("Deleted animal " + animalName);

		return true;

	}

	public boolean removeAnimal(Integer animalId) throws DatabaseException
	{

		List<ObservedValue> selectedObsTargetValues = db.find(ObservedValue.class, new QueryRule(ObservedValue.TARGET,
				Operator.EQUALS, animalId));
		List<Individual> selectedIndividual = db.find(Individual.class, new QueryRule(Individual.ID, Operator.EQUALS,
				animalId));
		db.remove(selectedObsTargetValues);
		db.remove(selectedIndividual);
		System.out.println("Deleted animal ");

		return true;

	}

}

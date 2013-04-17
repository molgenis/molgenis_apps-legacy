package org.molgenis.animaldb.plugins.animal;

import java.util.ArrayList;
import java.util.List;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.ObservedValue;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AnimalRemoverTest
{

	Database db;

	@Test
	public void removeString() throws DatabaseException
	{

		String name = "Bokito";
		List<ObservedValue> ovs = new ArrayList<ObservedValue>();
		List<Individual> inds = new ArrayList<Individual>();
		inds.add(new Individual());

		db = Mockito.mock(Database.class);
		Mockito.when(db.find(ObservedValue.class, new QueryRule(ObservedValue.TARGET_NAME, Operator.EQUALS, name)))
				.thenReturn(ovs);
		Mockito.when(db.find(Individual.class, new QueryRule(Individual.NAME, Operator.EQUALS, name))).thenReturn(inds);

		AnimalRemover animalRemover = new AnimalRemover(db);
		animalRemover.removeAnimal(name);

		ArgumentCaptor<List> listCaptor = ArgumentCaptor.forClass(List.class);
		Mockito.verify(db).remove(listCaptor.capture());
		Assert.assertTrue(ovs == listCaptor.getAllValues().get(0));

		Mockito.verify(db).remove(inds.get(0));

	}

}

package org.molgenis.load.cmdline;

import java.io.File;
import java.util.Arrays;

import org.apache.log4j.BasicConfigurator;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.Database.DatabaseAction;

import app.DatabaseFactory;
import app.EntitiesImporterImpl;

public class LoadFromDirectory
{
	public static void main(String[] args) throws Exception
	{
		BasicConfigurator.configure();

		File directory = new File("/tmp/");

		Database db = DatabaseFactory.create();

		new EntitiesImporterImpl(db).importEntities(Arrays.asList(directory.listFiles()), DatabaseAction.ADD);

		System.out.println("upload complete");
	}
}

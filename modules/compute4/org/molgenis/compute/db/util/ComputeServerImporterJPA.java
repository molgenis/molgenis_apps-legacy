package org.molgenis.compute.db.util;

import app.DatabaseFactory;
import org.molgenis.compute.runtime.ComputeServer;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;

/**
 * Created with IntelliJ IDEA. User: georgebyelas Date: 15/08/2012 Time: 13:15
 * To change this template use File | Settings | File Templates.
 */
public class ComputeServerImporterJPA
{
	public static void main(String[] args)
	{

		if (args.length == 3)
		{
			System.out.println("*** START ComputeServer IMPORT");
		}
		else
		{
			System.out.println("Not enough parameters");
			System.exit(1);
		}

		try
		{
			new ComputeServerImporterJPA().process(args[0], args[1], args[2]);
		}
		catch (DatabaseException e)
		{
			e.printStackTrace();
		}
	}

	private void process(String name, String ip, String port)
			throws DatabaseException
	{

		Database db = DatabaseFactory.create();
		try
		{
			db.beginTx();

			ComputeServer server = new ComputeServer();
            server.setName(name);
            server.setIp(ip);
            server.setPort(port);

            db.add(server);
            db.commitTx();
			System.out.println("... done");
		}
		catch (Exception e)
		{
			db.rollbackTx();
			e.printStackTrace();
		}

	}

}

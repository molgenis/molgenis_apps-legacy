package org.molgenis.wormqtl.etc;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import matrix.DataMatrixInstance;
import matrix.general.DataMatrixHandler;

import org.apache.commons.dbcp.BasicDataSource;
import org.molgenis.auth.DatabaseLogin;
import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.security.Login;
import org.molgenis.framework.server.TokenFactory;
import org.molgenis.util.HandleRequestDelegationException;
import org.molgenis.xgap.Chromosome;
import org.molgenis.xgap.Gene;
import org.molgenis.xgap.Probe;

import app.DatabaseFactory;

public class ExampleQueries
{
	
	public ExampleQueries(String usr, String pwd) throws HandleRequestDelegationException, Exception
	{
		Database db = getDb(usr, pwd);
		
		//get all chromosomes
		List<Chromosome> chromosomes = db.find(Chromosome.class);
		System.out.println(chromosomes.size() + " chromosomes found");
		
		//get count of genes per chromosome
		for(Chromosome chr : chromosomes)
		{
			List<Gene> genesOnThisChr = db.find(Gene.class, new QueryRule(Gene.CHROMOSOME_NAME, Operator.EQUALS, chr.getName()));
			System.out.println(genesOnThisChr.size() + " genes on chromosome " + chr.getName());
		}
		
		//get probes in a certain region
		//BEWARE: probe bp location is CUMULATIVE over all chromosomes (by order nr) !!
		List<Probe> probesInRegion = db.find(Probe.class, new QueryRule(Probe.BPSTART, Operator.GREATER, 100000), new QueryRule(Probe.BPSTART, Operator.LESS, 900000));
		System.out.println(probesInRegion.size() + " probes in region");
		
		//get all datasets ('feature' = columns, 'target' = rows)
		List<Data> datasets = db.find(Data.class);
		DataMatrixHandler dmh = new DataMatrixHandler(db);
		for(Data data : datasets){
			DataMatrixInstance dataMatrix = dmh.createInstance(data, db);
			System.out.println("data matrix: " + dataMatrix.getData().getName() + " (" + dataMatrix.getData().getFeatureType() + " x " + dataMatrix.getData().getTargetType() + ")");
			System.out.println("row name 0: " + dataMatrix.getRowNames().get(0));
			System.out.println("col name 0: " + dataMatrix.getColNames().get(0));
			System.out.println("value at 0,0: " + dataMatrix.getElement(0, 0));
			//etc
		}
		
	}
	
	/**
	 * Helper: create database with this login info
	 * @param usr
	 * @param pwd
	 * @return
	 * @throws HandleRequestDelegationException
	 * @throws Exception
	 */
	private Database getDb(String usr, String pwd) throws HandleRequestDelegationException, Exception
	{
		//create db
		BasicDataSource data_src = new BasicDataSource();
		data_src.setDriverClassName("org.hsqldb.jdbcDriver");
		data_src.setUsername("sa");
		data_src.setPassword("");
		data_src.setUrl("jdbc:hsqldb:file:hsqldb/molgenisdb;shutdown=true");
		data_src.setInitialSize(10);
		data_src.setTestOnBorrow(true);
		DataSource dataSource = (DataSource)data_src;
		Connection conn = dataSource.getConnection();
		//Database db = new app.JDBCDatabase(conn);
		Database db = DatabaseFactory.create(conn);	
		
		//login
		Login login = new DatabaseLogin(new TokenFactory());
		login.login(db, usr, pwd);
		db.setLogin(login);
		System.out.println("logged in as: " + db.getLogin().getUserName());
		return db;
	}
	
	/**
	 * Helper: make chromosome basepair lengths cumulative
	 * @param chromosomes
	 * @return
	 * @throws Exception
	 */
	public List<Chromosome> makeChrBpLenghtCumulative(List<Chromosome> chromosomes) throws Exception
	{
		List<Chromosome> result = new ArrayList<Chromosome>();
		// result.addAll(arg0)

		// start accumulation with 0 on the chromosome with the first order nr
		// then add bp length of chr 1 on chr2, etc
		int cumulativeBp = 0;

		// find the other number for every chromosome
		// must start at 1 and have a unique number for each
		for (int i = 1; i <= chromosomes.size(); i++)
		{
			boolean chrForOrderNrFound = false;
			for (Chromosome chr : chromosomes)
			{
				if (chr.getOrderNr().equals(i))
				{
					chrForOrderNrFound = true;
					cumulativeBp += chr.getBpLength();
					Chromosome cumulativeBpChr = new Chromosome(chr);
					cumulativeBpChr.setBpLength(cumulativeBp);
					result.add(cumulativeBpChr);
					System.out.println("added cumulative bp chromosome: " + cumulativeBpChr);
				}
			}
			if (!chrForOrderNrFound)
			{
				throw new Exception("Chromosome for order nr " + i + " not found!");
			}
		}
		return result;
	}

	/**
	 * @param args
	 * @throws Exception 
	 * @throws HandleRequestDelegationException 
	 */
	public static void main(String[] args) throws HandleRequestDelegationException, Exception
	{
		
		//arg checking
		if(args.length != 2 || args[0].length() == 0 || args[1].length() == 0)
		{
			throw new IllegalArgumentException("You must supply username and password. Add e.g. 'admin admin' to program arguments. Add '-Xmx2g' to VM arguments to make it fast.");
		}
		
		System.out.println("starting ExampleQueries, arguments ok");
		System.out.println("user: " + args[0]);
		char[] stars = new char[args[1].length()];
		Arrays.fill(stars, '*');
		String starString = new String(stars);
		System.out.println("pass: " + starString);
		
		new ExampleQueries(args[0], args[1]);
	}

}

package org.molgenis.wormqtl.header;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.molgenis.auth.DatabaseLogin;
import org.molgenis.auth.MolgenisPermission;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.security.Login;
import org.molgenis.framework.server.TokenFactory;
import org.molgenis.util.HandleRequestDelegationException;

import app.DatabaseFactory;

public class MakeWormQTLPrivate
{

	/**
	 * @param args
	 * @throws Exception 
	 * @throws HandleRequestDelegationException 
	 */
	public static void main(String[] args) throws HandleRequestDelegationException, Exception
	{
		
		//arg checking
		if(args.length != 2 || args[0].length() == 0 || args[1].length() == 0 || args[0].equals("${username}") || args[1].equals("${password}"))
		{
			throw new IllegalArgumentException("You must supply username and password! e.g.\nant -f build_wormqtl.xml makePrivate -Dusername=admin -Dpassword=admin");
		}
		
		System.out.println("user: " + args[0]);
		char[] stars = new char[args[1].length()];
		Arrays.fill(stars, '*');
		String starString = new String(stars);
		System.out.println("pass: " + starString);
		
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
		login.login(db, args[0], args[1]);
		db.setLogin(login);
		System.out.println("logged in as: " + db.getLogin().getUserName());
		
		//remove every permission object for 'anonymous'
		List<MolgenisPermission> deleteMe = db.find(MolgenisPermission.class, new QueryRule(MolgenisPermission.ROLE__NAME, Operator.EQUALS, "anonymous"));
	
		System.out.println("attempting to delete " + deleteMe.size() + " objects");
		
		for(MolgenisPermission mp : deleteMe)
		{
			//System.out.println("deleting: " + mp.toString());
			int removed = db.remove(mp);
			if(removed == 0)
			{
				System.out.println("DELETE OF OBJECT "+mp.toString()+" FAILED");
			}
		}
		
		System.out.println("delete complete!");
		
		List<MolgenisPermission> checkIfEmpty = db.find(MolgenisPermission.class, new QueryRule(MolgenisPermission.ROLE__NAME, Operator.EQUALS, "anonymous"));
		
		System.out.println("now " + checkIfEmpty.size() + " objects remain the database (if not 0, something went wrong)");
		
		
		db.close();
		
		for (String e : HomePage.qtlFinderPerms)
		{
			
//			MolgenisPermission deleteMe = db.find(MolgenisPermission.class, new QueryRule(MolgenisPermission.ENTITY_CLASSNAME, Operator.EQUALS, e)).get(0);
//			System.out.println("Removing permissions for entity " + deleteMe.getEntity_ClassName());
//			db.remove(deleteMe);
			
//			MolgenisPermission mp = new MolgenisPermission();
//			mp.setEntity_ClassName(e);
//			mp.setRole_Name("bio-user");
//			mp.setPermission("read");
//			db.add(mp);
			
		}

	}

}

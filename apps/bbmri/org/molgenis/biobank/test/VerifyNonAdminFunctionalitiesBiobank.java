package org.molgenis.biobank.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

import org.openqa.selenium.server.RemoteControlConfiguration;
import org.openqa.selenium.server.SeleniumServer;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.HttpCommandProcessor;
import com.thoughtworks.selenium.Selenium;

public class VerifyNonAdminFunctionalitiesBiobank {
	
	/**
	 *Login as non-admin user
	** Verify that you do not have access to the Admin only functionalities
	** Verify that you can see all the biobanks
	** Verify that you cannot edit the biobanks
	* Verify that when you're not logged in you do not have access to the list of biobanks
	* Create a biobank
	* Edit a biobank
	* Create a cošrdinator
	* Edit a cošrdinator
	* Edit your own contact details
	* Change your password and log in with your new password
	 */

	private final String PAGE_LOAD_TIME_OUT = "60000";
	
	SeleniumServer server;
	HttpCommandProcessor proc;
	private Selenium selenium;
	
	@BeforeSuite(alwaysRun = true)
	public void setupBeforeSuite(ITestContext context) {
	  String seleniumHost = "localhost";
	  String seleniumPort = "9080";
	  String seleniumBrowser = "firefox";
	  String seleniumUrl = "http://localhost:8080/molgenis_apps/";
	  
	  RemoteControlConfiguration rcc = new RemoteControlConfiguration();
	  rcc.setSingleWindow(true);
	  rcc.setPort(Integer.parseInt(seleniumPort));
	  
	  try {
	    server = new SeleniumServer(false, rcc);
	    server.boot();
	  } catch (Exception e) {
	    throw new IllegalStateException("Can't start selenium server", e);
	  }
	  
	  proc = new HttpCommandProcessor(seleniumHost, Integer.parseInt(seleniumPort),
	      seleniumBrowser, seleniumUrl);
	  selenium = new DefaultSelenium(proc);
	  selenium.start();
	}
	
	
	@BeforeClass
	public void setUp() {
		
	}
	
	@Test
	public void TestBiobankData() throws FileNotFoundException, SQLException, IOException, Exception {
		selenium.open("/molgenis_apps/molgenis.do");
		selenium.waitForPageToLoad("3000");
		selenium.open("/molgenis_apps/molgenis.do?__target=main&select=SimpleUserLogin");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		
		selenium.type("username", "despoinatest");
		selenium.type("password", "despoinatest");
		selenium.click("id=Login");
		selenium.waitForPageToLoad("10000");
	
		selenium.open("/molgenis_apps/molgenis.do?__target=main&select=BiobankOverview"); 
		selenium.waitForPageToLoad("30000");
		
		selenium.click("id=Cohorts_edit_new");
		selenium.waitForPopUp("molgenis_edit_new", "3000");
		
		
		Thread.sleep(10000);

	}
	

}

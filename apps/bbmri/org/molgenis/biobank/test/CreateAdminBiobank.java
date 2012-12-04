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

public class CreateAdminBiobank
{

	/**
	 * Test: Create a new biobank. 1. Login as admin (user: BbmriAdmin) 2. Go to
	 * Admin Biobank 3. Click on the add icon 4. Fill in the form and click on
	 * save 5. Verify that the biobank is present in the list 6. Log out and log
	 * in as a non-admin user (user: �.) 7. Go to Admin Biobank 8. Verify that
	 * the biobank created in 4. is visible.
	 */

	private final String PAGE_LOAD_TIME_OUT = "60000";

	SeleniumServer server;
	HttpCommandProcessor proc;
	private Selenium selenium;

	@BeforeSuite(alwaysRun = true)
	public void setupBeforeSuite(ITestContext context)
	{
		String seleniumHost = "localhost";
		String seleniumPort = "9080";
		String seleniumBrowser = "firefox";
		String seleniumUrl = "http://localhost:8080/molgenis_apps/";

		RemoteControlConfiguration rcc = new RemoteControlConfiguration();
		rcc.setSingleWindow(true);
		rcc.setPort(Integer.parseInt(seleniumPort));

		try
		{
			server = new SeleniumServer(false, rcc);
			server.boot();
		}
		catch (Exception e)
		{
			throw new IllegalStateException("Can't start selenium server", e);
		}

		proc = new HttpCommandProcessor(seleniumHost, Integer.parseInt(seleniumPort), seleniumBrowser, seleniumUrl);
		selenium = new DefaultSelenium(proc);
		selenium.start();
	}

	@BeforeClass
	public void setUp()
	{

	}

	@Test
	public void TestBiobankData() throws FileNotFoundException, SQLException, IOException, Exception
	{
		selenium.open("/molgenis_apps/molgenis.do");
		selenium.waitForPageToLoad("3000");
		selenium.open("/molgenis_apps/molgenis.do?__target=main&select=SimpleUserLogin");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);

		selenium.type("username", "admin");
		selenium.type("password", "admin");
		selenium.click("id=Login");
		selenium.waitForPageToLoad("10000");

		selenium.open("/molgenis_apps/molgenis.do?__target=main&select=Admin");
		selenium.open("/molgenis_apps/molgenis.do?__target=DataViews&select=AdminCohorts");

		selenium.waitForPageToLoad("30000");

		selenium.click("id=AdminCohorts_edit_new");
		selenium.waitForPopUp("AdminCohorts_edit_new", "3000");
		selenium.selectWindow("AdminCohorts_edit_new");

		selenium.focus("id=owns");
		selenium.type("id=owns", "AllUsers");
		selenium.select("id=owns", "label=AllUsers");

		selenium.focus("id=Biobank_Category");
		selenium.type("id=Biobank_Category", "A. Core Biobanks, DNA available");
		selenium.select("Biobank_Category", "A. Core Biobanks, DNA available");
		// selenium.select("id=Biobank_Category","label=B. Supporting biobanks (DNA not yet available)");
		// selenium.select("Biobank_Category","A. Core Biobanks, DNA available");
		// //
		// selenium.select("SubCategory","A. Core Biobanks, DNA available");

		selenium.focus("id=Approved");
		selenium.select("id=Approved", "label=yes");
		selenium.click("id=Add");

		Thread.sleep(10000);

	}

}

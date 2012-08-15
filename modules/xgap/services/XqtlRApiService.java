package services;

import generic.Utils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.text.ParseException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.molgenis.cluster.RScript;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.server.FrontControllerAuthenticator;
import org.molgenis.framework.server.FrontControllerAuthenticator.LoginStatus;
import org.molgenis.framework.server.FrontControllerAuthenticator.LogoutStatus;
import org.molgenis.framework.server.MolgenisContext;
import org.molgenis.framework.server.MolgenisRequest;
import org.molgenis.framework.server.MolgenisResponse;
import org.molgenis.framework.server.MolgenisService;
import org.molgenis.framework.server.MolgenisServiceAuthenticationHelper;

import decorators.MolgenisFileHandler;

/** Use seperate servlet because of the custom R script that needs to be added */
public class XqtlRApiService implements MolgenisService
{

	private MolgenisContext mc;

	public XqtlRApiService(MolgenisContext mc)
	{
		this.mc = mc;
	}

	@Override
	public void handleRequest(MolgenisRequest request, MolgenisResponse response) throws ParseException,
			DatabaseException, IOException
	{

		// as used in /molgenis/src/org/molgenis/generators/R/RApiGen.R.ftl,
		// must match!
		String pwdString = MolgenisServiceAuthenticationHelper.LOGIN_PASSWORD;
		String usrString = MolgenisServiceAuthenticationHelper.LOGIN_USER_NAME;

		// Utils.console("starting RApiServlet");
		OutputStream outs = response.getResponse().getOutputStream();
		PrintStream out = new PrintStream(new BufferedOutputStream(outs), false, "UTF8"); // 1.4

		if (request.getString(usrString) != null && request.getString(pwdString) != null)
		{
			String usr = request.getString(usrString);
			String pwd = request.getString(pwdString);

			LoginStatus login = FrontControllerAuthenticator.login(request, usr, pwd);

			String responseLine;
			if (login == LoginStatus.ALREADY_LOGGED_IN)
			{
				responseLine = "You are already logged in. Log out first.";
			}
			else if (login == LoginStatus.SUCCESSFULLY_LOGGED_IN)
			{
				responseLine = "Welcome, " + usr + "!";
			}
			else if (login == LoginStatus.AUTHENTICATION_FAILURE)
			{
				responseLine = "User or password unknown.";
			}
			else if (login == LoginStatus.EXCEPTION_THROWN)
			{
				responseLine = "An error occurred. Contact your administrator.";
			}
			else
			{
				throw new IOException("Unknown login status: " + login);
			}

			writeResponse(response, responseLine, out);
			return;
		}

		if (request.getString("logout") != null && request.getString("logout").equals("logout"))
		{

			LogoutStatus logout = FrontControllerAuthenticator.logout(request);

			String responseLine;
			if (logout == LogoutStatus.ALREADY_LOGGED_OUT)
			{
				responseLine = "You are already logged out. Log in first.";
			}
			else if (logout == LogoutStatus.SUCCESSFULLY_LOGGED_OUT)
			{
				responseLine = "You are successfully logged out.";
			}
			else if (logout == LogoutStatus.EXCEPTION_THROWN)
			{
				responseLine = "An error occurred. Contact your administrator.";
			}
			else
			{
				throw new IOException("Unknown logout status: " + logout);
			}

			writeResponse(response, responseLine, out);
			return;
		}

		// Utils.console("URI path: " +request.getRequest().getRequestURI());
		String fullServicePath = request.getRequest().getServletPath() + request.getServicePath();
		// Utils.console("servlet path: " +fullServicePath);
		int loc = request.getRequest().getRequestURI().lastIndexOf(fullServicePath);
		String filename = request.getRequest().getRequestURI().substring(loc + fullServicePath.length());

		// Utils.console("filename is now: " + filename);

		String s = "";

		if (filename.startsWith("/"))
		{
			filename = filename.substring(1);
		}
		// if R file exists, return that
		if (!filename.equals("") && !filename.endsWith(".R"))
		{
			// Utils.console("bad request: no R extension");
			s += "you can only load .R files\n";
		}
		else if (filename.equals(""))
		{
			//FIXME: rework this!! use String db_path = this.getApplicationController().getApplicationUrl(); ???
			
			// Utils.console("getting default file");
			String server = "http://" + request.getRequest().getLocalName() + ":" + request.getRequest().getLocalPort()
					+ "/" + mc.getVariant();
			String rSource = server + "/api/R/";
			// getRequestURL omits port!
			s += ("#first time only: install RCurl and bitops\n");
			s += ("#install.packages(\"RCurl\", lib=\"~/libs\")\n");
			s += ("#install.packages(\"bitops\", lib=\"~/libs\")\n");
			s += ("\n");
			s += ("#load RCurl and bitops\n");
			s += ("library(bitops, lib.loc=\"~/libs\")\n");
			s += ("library(RCurl, lib.loc=\"~/libs\")\n");
			s += ("\n");
			s += ("#get server paths to R API\n");
			s += ("molgenispath <- paste(\"" + rSource + "\")\n");
			s += ("serverpath <- paste(\"" + server + "\")\n");
			s += ("\n");
			s += ("#load autogenerated R interfaces\n");
			s += ("source(\"" + rSource + "source.R\")\n");
//			//s +=("source(\"" + rSource + "source.R\")\n");
//			String localSource = "http://" + "localhost" + ":" + request.getRequest().getLocalPort() + "/"+mc.getVariant() + "/api/R/";
//			s +=("sourceSuccess <- tryCatch(source(\"" + rSource + "source.R\"), error=function(e) e)\n");
//			s +=("if(is.null(sourceSuccess$visible)){ source(\"" + localSource + "source.R\") } #fallback: if the previous location fails, try localhost\n");
//	//		y <- tryCatch(source("broken.R"), error=function(e) e)
//	//		works <- !is.null(y$visible) #y$visible would be null if there were an error
//			
			s += ("\n");
			s += ("#load XGAP specific extension to use R/qtl\n");
			s += ("source(\"" + rSource + "xgap/R/RqtlTools.R\")\n");
			s += ("\n");
			s += ("#load XGAP specific extension to ease use of the Data <- DataElement structure as matrices\n");
			s += ("source(\"" + rSource + "xgap/R/DataMatrix.R\")\n");
			s += ("\n");
			s += ("#load cluster calculation scripts\n");

			File[] listing = new File((this.getClass().getResource("../plugins/cluster/R/ClusterJobs/R")).getFile())
					.listFiles();
			if (listing != null)
			{
				for (File f : listing)
				{
					s += ("source(\"" + rSource + "plugins/cluster/R/ClusterJobs/R/" + f.getName() + "\")\n");

				}
			}
			else
			{
				s = ("#No R files seem available; did you generate R?");
			}
			s += ("\n");

			// quick addition for demo purposes
			s += ("#loading user defined scripts\n");
			// MolgenisServlet ms = new MolgenisServlet();
			try
			{
				List<RScript> scripts = request.getDatabase().find(RScript.class);
				for (RScript script : scripts)
				{
					s += ("source(\"" + rSource + "userscripts/" + script.getName() + ".R\")\n");
				}
				s += ("\n");
				s += ("#connect to the server\n");
				s += ("MOLGENIS.connect(\"" + server + "\")\n");
				s += ("\n");
				s += ("#--> login/logout using:\n");
				s += ("# MOLGENIS.login(\"username\",\"password\")\n");
				s += ("# MOLGENIS.logout()\n");
				s += ("\n");
			}
			catch (Exception e)
			{
				s += "#No database connection available to handle R-api";
				// throw new IOException(e);
			}

			// quick addition for demo purposes
		}
		else if (filename.startsWith("userscripts/"))
		{
			// MolgenisServlet ms = new MolgenisServlet();
			try
			{
				Database db = request.getDatabase();
				String name = filename.substring(12, filename.length() - 2);
				// Utils.console("getting '"+name+".r'");
				QueryRule q = new QueryRule("name", Operator.EQUALS, name);
				RScript script = db.find(RScript.class, q).get(0);
				MolgenisFileHandler mfh = new MolgenisFileHandler(db);
				File source = mfh.getFile(script, db);
				Utils.console("printing file: '" + source.getAbsolutePath() + "'");
				String str = this.printUserScript(source.toURI().toURL(), "", name);
				s += (str);
			}
			catch (Exception e)
			{
				throw new IOException(e);
			}

		}
		else
		{
			// otherwise return the default R code to source all
			// Utils.console("getting specific R file");
			filename = filename.replace(".", "/");
			filename = filename.substring(0, filename.length() - 2) + ".R";
			// map to hard drive, minus path app/servlet
			File root = new File(app.servlet.MolgenisServlet.class.getResource("source.R").getFile()).getParentFile()
					.getParentFile().getParentFile();

			if (filename.equals("source.R"))
			{
				root = new File(root.getAbsolutePath() + "/app/servlet");
			}
			File source = new File(root.getAbsolutePath() + "/" + filename);

			// up to root of app
			// Utils.console("trying to load R file: " + filename +
			// " from path " + source);
			if (source.exists())
			{
				String str = this.printScript(source.toURI().toURL(), "");
				s += (str);
			}
			else
			{
				s += ("File '" + filename + "' not found\n");
			}
			// Utils.console("done getting specific R file");
		}
		writeResponse(response, s, out);
		// Utils.console("closed & flushed");

	}

	private void writeResponse(MolgenisResponse response, String responseLine, PrintStream out) throws IOException
	{
		response.getResponse().setStatus(HttpServletResponse.SC_OK);
		response.getResponse().setContentLength(responseLine.length());
		response.getResponse().setCharacterEncoding("UTF8");
		response.getResponse().setContentType("text/plain");
		out.print(responseLine);
		out.flush();
		out.close();
		response.getResponse().flushBuffer();
	}

	private String printScript(URL source, String out) throws IOException
	{
		// Utils.console("reading file to be outputted");
		BufferedReader reader = new BufferedReader(new InputStreamReader(source.openStream()));
		String sourceLine;
		while ((sourceLine = reader.readLine()) != null)
		{
			out += sourceLine + "\n";
		}
		reader.close();
		// Utils.console("done reading");
		return out;
	}

	private String printUserScript(URL source, String out, String scriptName) throws IOException
	{
		// Utils.console("reading file to be outputted");
		BufferedReader reader = new BufferedReader(new InputStreamReader(source.openStream()));
		out += "run_"
				+ scriptName
				+ " <- function(dbpath, subjob, item, jobid, outname, myanalysisfile, jobparams, investigationname, libraryloc){\n";
		String sourceLine;
		while ((sourceLine = reader.readLine()) != null)
		{
			if (!sourceLine.trim().equals(""))
			{
				if (!sourceLine.trim().endsWith("{") && !sourceLine.trim().endsWith("}"))
				{
					out += "cat(Generate_Statement(\"" + sourceLine.replace("\"", "'")
							+ "\"),file=myanalysisfile,append=T)\n";
				}
				else
				{
					out += "cat(\"" + sourceLine.replace("\"", "'") + "\n\",file=myanalysisfile,append=T)\n";
				}
			}
			else
			{
				// Utils.console("Removing empty line");
			}
		}
		out += "}";
		reader.close();
		// Utils.console("done reading");
		return out;
	}
}
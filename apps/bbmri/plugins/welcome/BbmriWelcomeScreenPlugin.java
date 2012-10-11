/* Date:        November 19, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 */

package plugins.welcome;

import java.util.Date;
import java.util.List;

import org.molgenis.bbmri.Welcome;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.html.JQueryEditable;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

import app.DatabaseFactory;


public class BbmriWelcomeScreenPlugin<E extends Entity> extends PluginModel<E>
{
	protected static Database db;

	private JQueryEditable jqe;
	private String welcomeTitle;
	private String welcomeText;


	private static final long serialVersionUID = -2848815736940818733L;

	//temporary variable for distinguish version of vm7 (without editable welcome message) with new version. 
	private String server  = "noneditable"; //editable or noneditable
	
	public void setDatabase(Database db) {
		BbmriWelcomeScreenPlugin.db = db;
	}
	
	
	public Database getDatabase() {
		return db;
	}
	  
	//todo remove all jquery to JQueryEditable module
	//todo : remove functionality to reload where database is available . currently not working , check why. 
	public BbmriWelcomeScreenPlugin(String name, ScreenController<?> parent)
	{
		super(name, parent);
		jqe = new JQueryEditable("Welcome", "test");
	
		//If the welcome page has a content 
		try {
			//Database db = this.getDatabase();
			Database db = DatabaseFactory.create();
			
			List<Welcome> welcomeList;
			Welcome welcome = new Welcome(); 

		
			System.out.println(db);
			welcomeList = db.find(Welcome.class, new QueryRule(Welcome.STATUS, Operator.EQUALS, "new"), new QueryRule(Operator.SORTASC, Welcome.WELCOMEDATETIME)); //get latest new
		
			//String query = "SELECT * FROM welcome where status=\"new\" ORDER BY welcomeDatetime DESC limit 1;";
			//List<Tuple> aaa = db.sql(query);
		
			if (welcomeList.isEmpty()) {
				
				//create a new welcome message 
				this.setWelcomeTitle("<h3>Welcome to the Catalogue of Dutch biobanks</h3>");
//				String contactLink = "molgenis.do?__target=main&select=BbmriContact";
//				String helpLink = "molgenis.do?__target=main&select=BbmriHelp";
				
				this.setWelcomeText("<p>This catalogue provides a systematic database of collections of biomaterial and associated data subsumed under the umbrella of BBMRI-NL." +
						"BBMRI-NL is designed to provide infrastructure for biomedical studies. Over 170 major clinical and population biobanks in the Netherlands" +
						"(size 500 subjects or more) are associated with BBMRI-NL.</p>" +
						"<p>Material and data of biobanks associated with BBMRI-NL are available for biomedical research in the public domain. Access conditions for" +
						"scientific cooperation are subject to legal and ethical constraints, which may vary between biobanks. BBMRI-NL aims to harmonize and" +
						"enrich these biobanks in order to stimulate cooperative studies.</p>");
//						"<p>To apply for inclusion of your biobank in this catalogue, please <a href=\"molgenis.do?__target=main&select=BbmriContact\">contact the BBMRI-NL office</a>.</p>" +
//						"<p>To find your way around the application, you might want to check out the <a href=\"molgenis.do?__target=main&select=BbmriHelp\">User manual</a>.</p>" +
//						"<p>If you have any questions or remarks, please do not hesitate to <a href=\"molgenis.do?__target=main&select=BbmriContact\">contact us</a>.</p>") ;
//						"<p>To apply for inclusion of your biobank in this catalogue, please <a href=\" "+ contactLink + "\">contact the BBMRI-NL office</a>.</p>" +
//						"<p>To find your way around the application, you might want to check out the <a href=\" "+ helpLink + "\">User manual</a>.</p>" +
//						"<p>If you have any questions or remarks, please do not hesitate to <a href=\" "+contactLink + "\">contact us</a>.</p>") ;
				welcome.setWelcomeTitle(this.getWelcomeTitle());
				welcome.setWelcomeText(this.getWelcomeText());
				welcome.setStatus("new");
				Date now = new Date();
				welcome.setWelcomeDatetime(now);
				db.add(welcome);
			} else {
				System.out.println("------->"+ welcomeList);
				this.setWelcomeTitle(welcomeList.get(0).getWelcomeTitle());
				this.setWelcomeText(welcomeList.get(0).getWelcomeText());
				
			}

		} catch (DatabaseException e) {
			e.printStackTrace();
		}
	}

	
	public String getCustomHtmlHeaders()	{
		return  "<script src=\"res/jquery-plugins/editable/jquery.editable-1.3.3.js\" language=\"javascript\"></script>\n" +
				 "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/editableJQText.css\">";
	}
	
	@Override
	public String getViewName()
	{
		return "plugins_welcome_BbmriWelcomeScreenPlugin";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/welcome/BbmriWelcomeScreenPlugin.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		System.out.println("Request : " + request + ">>>>> "+  request.getString("title") + ">>>>" + request.getString("welcomeText"));

		if ("resetWelcomeTitleText".equals(request.getAction())) {
			//Get 'backup' record from db 
			List<Welcome> backWelcome ;
			try {
				//backup 
				//retrieving backup 
				backWelcome = db.find(Welcome.class, new QueryRule(Welcome.STATUS, Operator.EQUALS, "backup"));
				//Welcome backWelcome = db.query(Welcome.class).eq(Welcome.STATUS, "backup").find().get(0);

				this.setWelcomeTitle(backWelcome.get(0).getWelcomeTitle());
				this.setWelcomeText(backWelcome.get(0).getWelcomeText());
			} catch (DatabaseException e) {
				e.printStackTrace();
			}
		} else if ("submitChanges".equals(request.getAction())){
		
		}
			Welcome welcome = new Welcome();
			
			
			welcome.setWelcomeTitle(request.getString("title"));
			welcome.setWelcomeText(request.getString("welcomeText"));
			welcome.setStatus("new");
			Date today=new Date();
			
			welcome.setWelcomeDatetime(today);
			try {
				db.add(welcome);
			} catch (DatabaseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.setWelcomeTitle(request.getString("title"));
			this.setWelcomeText(request.getString("welcomeText"));
		
		
		
		
	}

	@Override
	public void reload(Database db){
		
		System.out.println("test");
	}
	
	@Override
	public boolean isVisible()
	{
		//you can use this to hide this plugin, e.g. based on user rights.
		//e.g.
		//if(!this.getLogin().hasEditPermission(myEntity)) return false;
		return true;
	}
	
	public int getUserId() {
		if (this.getLogin().isAuthenticated() == true) {
			return this.getLogin().getUserId();
		} else {
			return 0;
		}
	}
	
	public String getEditableArea(){
		System.out.println("this is the geteditable area");
		return jqe.toHtml();
	}
	

	public String getWelcomeText() {
		return welcomeText;
	}

	public void setWelcomeText(String welcomeText) {
		this.welcomeText = welcomeText;
	}

	public String getWelcomeTitle() {
		return welcomeTitle;
	}

	public void setWelcomeTitle(String welcomeTitle) {
		this.welcomeTitle = welcomeTitle;
	}
	
	public String getVm7(){
		return this.server;
	}
}

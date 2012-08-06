/* Date:        February 2, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.omicsconnect.plugins.ontocat;


import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

import uk.ac.ebi.ontocat.Ontology;
import uk.ac.ebi.ontocat.OntologyService;
import uk.ac.ebi.ontocat.OntologyServiceException;
import uk.ac.ebi.ontocat.OntologyTerm;
import uk.ac.ebi.ontocat.bioportal.BioportalOntologyService;

/**
 * Shows table of experiment information for WormQTL
 */
public class OntoCat extends PluginModel<Entity>
{

	private static final long serialVersionUID = 1L;
	private OntologyService os;

	private OntoCatModel model = new OntoCatModel();
	
	public OntoCatModel getMyModel()
	{
		return model;
	}

	public OntoCat(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "OntoCatTest";
	}

	@Override
	public String getViewTemplate()
	{
		return "org/molgenis/omicsconnect/plugins/OntoCat/OntoCat.ftl";
	}
	
	public void handleRequest(Database db, Tuple request)
	{
		if (request.getString("__action") != null)
		{
			String action = request.getString("__action");
			try
			{
			
			if (action.equals("query")){
				
				String select_ont = request.getString("ontology");
				List<OntologyTerm> results  = os.getRootTerms(select_ont);
				model.setResults(results);
			
			
			}
				
			}
			catch (Exception e)
			{
				e.printStackTrace();
				this.setMessages(new ScreenMessage(e.getMessage() != null ? e.getMessage() : "null", false));
			}
		}
	}
	
	
	
	@Override
	public void reload(Database db)
	{
		if (model.getOntologies() == null) {
			 this.os = new BioportalOntologyService();

			// List all available ontologies
			try {
				// for (Ontology o : os.getOntologies()) {
				// System.out.println(o.toString());

				// }

				model.setOntologies(os.getOntologies());

				this.setMessages(new ScreenMessage("Loaded ontologies", true));

			} catch (OntologyServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				this.setMessages(new ScreenMessage(e.getMessage() != null ? e
						.getMessage() : "null", false));

			}

		}
	}

}

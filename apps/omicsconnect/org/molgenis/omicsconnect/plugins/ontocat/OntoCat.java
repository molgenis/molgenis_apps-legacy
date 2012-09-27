/* Date:        February 2, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.omicsconnect.plugins.ontocat;

import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

import uk.ac.ebi.ontocat.Ontology;
import uk.ac.ebi.ontocat.OntologyService;
import uk.ac.ebi.ontocat.OntologyService.SearchOptions;
import uk.ac.ebi.ontocat.OntologyServiceException;
import uk.ac.ebi.ontocat.OntologyTerm;
import uk.ac.ebi.ontocat.bioportal.BioportalOntologyService;

/**
 * Shows table of experiment information for WormQTL
 */
public class OntoCat extends PluginModel<Entity> {

	private static final long serialVersionUID = 1L;
	private OntologyService os;

	private OntoCatModel model = new OntoCatModel();

	public OntoCatModel getMyModel() {
		return model;
	}

	public OntoCat(String name, ScreenController<?> parent) {
		super(name, parent);
	}

	@Override
	public String getViewName() {
		return "OntoCatTest";
	}

	@Override
	public String getViewTemplate() {
		return "org/molgenis/omicsconnect/plugins/OntoCat/OntoCat.ftl";
	}

	@SuppressWarnings("unchecked")
	public void handleRequest(Database db, Tuple request) {
		if (request.getString("__action") != null) {
			String action = request.getString("__action");
			try {

				if (action.equals("query")) {
					this.setMessages(new ScreenMessage("performed lookup", true));

					// String select_ont = request.getString("ontology");
					// <OntologyTerm> results = os.getRootTerms(select_ont);
					// model.setResults(results);

					OntologyService os = new BioportalOntologyService();
					List<Ontology> results = os.getOntologies();

					JSONArray jsonarray = new JSONArray();

					// outer.addAll(results);

					for (Ontology o : results) {

						JSONObject inner = new JSONObject();

						// System.out.println(o.getDescription());
						// System.out.println(o.getOntologyAccession() + " " +
						// o.getLabel());
						String acc = o.getOntologyAccession();
						String label = o.getLabel();

						// inner.put(acc, label);

						inner.put("value", acc);
						inner.put("label", label);
						jsonarray.add(inner);

					}

					model.setJsonarray(jsonarray);
					String jsonString = jsonarray.toJSONString();
					model.setJsonstring(jsonString);
					model.setOntologies(results);

				}
			} catch (Exception e) {
				e.printStackTrace();
				this.setMessages(new ScreenMessage(e.getMessage() != null ? e
						.getMessage() : "null", false));
			}
		}

		if (request.getString("__action") != null) {

			String action = request.getString("__action");

			OntologyService os = new BioportalOntologyService();
			if (action.equals("ontoQuery")) {

				String ontologySubmitted = request.getString("Ontology");
				List<OntologyTerm> ontoresults = null;
				try {
					ontoresults = os.searchOntology(ontologySubmitted,
							"diabetes", SearchOptions.INCLUDE_PROPERTIES);
				} catch (OntologyServiceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				model.setOntoresults(ontoresults);

				for (OntologyTerm o : ontoresults) {
					// System.out.println(o.);

				}

			}

		}

	}

	@Override
	public void reload(Database db) {
		// if (model.getOntologies() == null) {
		// this.os = new BioportalOntologyService();
		//
		// // List all available ontologies
		// try {
		// // for (Ontology o : os.getOntologies()) {
		// // System.out.println(o.toString());
		//
		// // }
		//
		// model.setOntologies(os.getOntologies());
		//
		// this.setMessages(new ScreenMessage("Loaded ontologies", true));
		//
		// } catch (OntologyServiceException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// this.setMessages(new ScreenMessage(e.getMessage() != null ? e
		// .getMessage() : "null", false));
		//
		// }
		//
		// }
	}

}

package org.molgenis.omicsconnect.plugins.ontocat;

import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import uk.ac.ebi.ontocat.Ontology;
import uk.ac.ebi.ontocat.OntologyService;
import uk.ac.ebi.ontocat.OntologyService.SearchOptions;
import uk.ac.ebi.ontocat.OntologyServiceException;
import uk.ac.ebi.ontocat.OntologyTerm;
import uk.ac.ebi.ontocat.bioportal.BioportalOntologyService;

public class OntoCatTest {

	public static void main(String[] args) throws OntologyServiceException {
		// Instantiate BioPortal service
		// Note that this uses ontocat's default apkikey
		// but you can pass your private apikey into the service
		// by using the alternative constructor
		// new BioportalOntologyServcie( "your apkiey" );
		OntologyService os = new BioportalOntologyService();
		List<Ontology> results = os.getOntologies();
		JSONArray outer = new JSONArray();

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
			outer.add(inner);

		}

		String jsonString = outer.toJSONString();
		System.out.println(jsonString);
		// String jsonString = inner.toJSONString();

		// System.out.println(outer.toString());

		// List<OntologyTerm> hp = os.searchOntology("1125", "ear",
		// SearchOptions.INCLUDE_PROPERTIES);

		// search multiple ontologies

		// return the results to the browser

		// System.out.println(hp.toString());

		// Find all terms containing string adipocyte
		for (OntologyTerm ot : os.searchAll("adipocyte",
				SearchOptions.INCLUDE_PROPERTIES)) {
			// System.out.println(ot);
		}
	}

	class Data {
		private String title;
		private Long id;
		private Boolean children;
		private List<Data> groups;

		public String getTitle() {
			return title;
		}

		public Long getId() {
			return id;
		}

		public Boolean getChildren() {
			return children;
		}

		public List<Data> getGroups() {
			return groups;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public void setChildren(Boolean children) {
			this.children = children;
		}

		public void setGroups(List<Data> groups) {
			this.groups = groups;
		}

		public String toString() {
			return String.format("title:%s,id:%d,children:%s,groups:%s", title,
					id, children, groups);

		}
	}

}

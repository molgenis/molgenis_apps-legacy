package org.molgenis.omicsconnect.plugins.ontocat;

import java.util.List;

import org.json.simple.JSONArray;

import uk.ac.ebi.ontocat.Ontology;
import uk.ac.ebi.ontocat.OntologyTerm;

//mac shift o 

public class OntoCatModel {
	private List<Ontology> ontologies;
	private List<OntologyTerm> results;
	JSONArray jsonarray;
	String jsonstring;
	private List<OntologyTerm> ontoresults;

	public List<OntologyTerm> getOntoresults() {
		return ontoresults;
	}

	public void setOntoresults(List<OntologyTerm> ontoresults) {
		this.ontoresults = ontoresults;
	}

	public String getJsonstring() {
		return jsonstring;
	}

	public void setJsonstring(String jsonstring) {
		this.jsonstring = jsonstring;
	}

	public JSONArray getJsonarray() {
		return jsonarray;
	}

	// public void setJsonArray(JSONArray jsonArray) {
	// this.jsonArray = jsonArray;
	// }

	public List<Ontology> getOntologies() {
		return ontologies;
	}

	public void setOntologies(List<Ontology> ontologies) {
		this.ontologies = ontologies;
	}

	public List<OntologyTerm> getResults() {
		return results;
	}

	public void setResults(List<OntologyTerm> results) {
		this.results = results;
	}

	public void setJsonarray(JSONArray jsonarray) {
		// TODO Auto-generated method stub
		this.jsonarray = jsonarray;
	}

}

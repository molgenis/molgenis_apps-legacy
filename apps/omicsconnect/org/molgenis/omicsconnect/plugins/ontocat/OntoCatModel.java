package org.molgenis.omicsconnect.plugins.ontocat;

import java.util.List;

import uk.ac.ebi.ontocat.Ontology;
import uk.ac.ebi.ontocat.OntologyTerm;

//mac shift o 


public class OntoCatModel
{
	private List <Ontology> ontologies;
	private List <OntologyTerm> results;

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
	
	
	
	
	
	
	
}

package plugins.HarmonizationComponent;

// Search a local OWL ontology
/**
 * Copyright (c) 2010 - 2011 European Molecular Biology Laboratory and University of Groningen
 *
 * Contact: ontocat-users@lists.sourceforge.net
 * 
 * This file is part of OntoCAT
 * 
 * OntoCAT is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * OntoCAT is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along
 * with OntoCAT. If not, see <http://www.gnu.org/licenses/>.
 */

import java.io.File;
import java.net.URISyntaxException;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import uk.ac.ebi.ontocat.OntologyServiceException;

/**
 * Example 3
 * 
 * Shows how to search an OWL Ontology
 * 
 */
public class Example3 {

	private OWLDataFactory factory = null;
	private OWLOntology owlontology = null;
	private OWLOntologyManager manager = null;

	public Example3() throws OWLOntologyCreationException {
		start();
	}

	public static void main(String[] args) throws OntologyServiceException,
			URISyntaxException, OWLOntologyCreationException {

		// Instantiate a FileOntologyService
		new Example3();
	}

	private void start() throws OWLOntologyCreationException {

		this.manager = OWLManager.createOWLOntologyManager();
		this.factory = manager.getOWLDataFactory();
		manager.loadOntologyFromOntologyDocument(new File(
				"/Users/pc_iverson/Desktop/Input/PredictionModel.owl"));

	}
}

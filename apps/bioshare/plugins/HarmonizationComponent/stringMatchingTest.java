package plugins.HarmonizationComponent;

import java.util.List;

import uk.ac.ebi.ontocat.Ontology;
import uk.ac.ebi.ontocat.OntologyService;
import uk.ac.ebi.ontocat.OntologyService.SearchOptions;
import uk.ac.ebi.ontocat.OntologyServiceException;
import uk.ac.ebi.ontocat.OntologyTerm;
import uk.ac.ebi.ontocat.bioportal.BioportalOntologyService;

public class stringMatchingTest {

	public static void main(String[] args) throws OntologyServiceException {

		String query = "parent diabetes";
		String testString = "father diabetes";

		// Expand the query using ontocat.
		OntologyService os = new BioportalOntologyService();
		// FileOntologyService os = new FileOntologyService(new URI(
		// "http://www.ebi.ac.uk/efo"), "EFO");
		List<Ontology> list = os.getOntologies();
		for (OntologyTerm ot : os.searchAll("adipocyte",
				SearchOptions.INCLUDE_PROPERTIES)) {
			System.out.println("The matched term is from " + ot.getOntology()
					+ ". the ontology accession is " + ot.getAccession()
					+ ", the term is " + ot.getLabel());
			for (OntologyTerm childTerm : os.getChildren(ot)) {
				System.out.println("------->The subClass is "
						+ childTerm.getLabel());
			}
			System.out.println();
		}

		LevenshteinDistanceModel model = new LevenshteinDistanceModel();
		double similarity = model.stringMatching(query, testString, false);
		System.out.println(similarity);
	}
}

package plugins.harmonization;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import uk.ac.ebi.ontocat.Ontology;
import uk.ac.ebi.ontocat.OntologyService;
import uk.ac.ebi.ontocat.OntologyServiceException;
import uk.ac.ebi.ontocat.bioportal.BioportalOntologyService;

public class ontocatTest
{
	public static void main(String args[]) throws OntologyServiceException, URISyntaxException
	{
		OntologyService os = new BioportalOntologyService();

		String[] ontologies =
		{ "1351", "1136", "1353", "2018", "1032" };

		List<String> ontologyAccessions = Arrays.asList(ontologies);

		for (Ontology on : os.getOntologies())
		{
			System.out.println(on);
		}
		// 1351,1136,1353,2018
		// for (OntologyTerm ot : os.searchAll("height", SearchOptions.EXACT))
		// {
		// if (ontologyAccessions.contains(ot.getOntologyAccession()))
		// {
		// System.out.println(ot);
		//
		// System.out.println();
		//
		// // if (os.getChildren(ot) != null)
		// // {
		// // for (OntologyTerm subOt : os.getChildren(ot))
		// // {
		// // System.out.println(subOt);
		// // }
		// // }
		// }
		// }
	}
}

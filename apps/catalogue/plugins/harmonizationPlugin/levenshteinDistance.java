package plugins.harmonizationPlugin;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;


import uk.ac.ebi.ontocat.OntologyService;
import uk.ac.ebi.ontocat.OntologyServiceException;
import uk.ac.ebi.ontocat.OntologyTerm;
import uk.ac.ebi.ontocat.file.FileOntologyService;
import uk.ac.ebi.ontocat.virtual.CompositeDecorator;

public class levenshteinDistance {

	//Choose n-grams to tokenize the input string by default nGrams is 2
	private int nGrams = 2;

	private String separator = ";";

	private HashMap<String, OWLClass> labelToOWLClass = null;

	private HashMap<String, List<String>> normalizedOntologyTerms = null;

	private OWLOntologyManager manager = null;

	private OWLDataFactory factory = null;

	private OWLFunction owlFunction = null;

	private OntologyService os = null;

	private HashMap<String, String> synonymToLabel = new HashMap<String, String>();

	private HashMap<String, List<String>> foundTermInDataDescription = new HashMap<String, List<String>>();

	//private HashMap<String, List<String>> foundDescriptionByCutOff = new HashMap<String, List<String>>();

	private HashMap<String, List<String>> expandedQueries = new HashMap<String, List<String>>();

	private OWLOntology referenceOntology = null;

	private OWLOntology localOntology = null;

	private String matchingResult = "";

	private static String regex = "[!?/]";

	private HashMap<String, List<String>> parameterToExpandedQuery = new HashMap<String, List<String>>();

	public HashMap<String, List<String>> getParameterToExpandedQuery() {
		return parameterToExpandedQuery;
	}

	List<String> tableContent = new ArrayList<String>();

	public static final String[] STOP_WORDS = {"a","you","about","above","after","again",
		"against","all","am","an","and","any","are","aren't","as","at","be","because","been",
		"before","being","below","between","both","but","by","can't","cannot","could","couldn't",
		"did","didn't","do","does","doesn't","doing","don't","down","during","each","few","for","from",
		"further","had","hadn't","has","hasn't","have","haven't","having","he","he'd","he'll","he's","her",
		"here","here's","hers","herself","him","himself","his","how","how's","i","i'd","i'll","i'm","i've",
		"if","in","into","is","isn't","it","it's","its","itself","let's","me","more","most","mustn't","my",
		"myself","no","nor","not","of","off","on","once","only","or","other","ought","our","ours "," ourselves",
		"out","over","own","same","shan't","she","she'd","she'll","she's","should","shouldn't","so","some","such",
		"than","that","that's","the","their","theirs","them","themselves","then","there","there's","these","they",
		"they'd","they'll","they're","they've","this","those","through","to","too","under","until","up","very","was",
		"wasn't","we","we'd","we'll","we're","we've","were","weren't","what","what's","when","when's","where","where's",
		"which","while","who","who's","whom","why","why's","with","won't","would","wouldn't","you","you'd","you'll","you're",
		"you've","your","yours","yourself","yourselves","many"};

	public static List<String> STOPWORDSLIST = new ArrayList<String>();

	//Constructor
	public levenshteinDistance(int nGrams){

		this.nGrams = nGrams;

		manager = OWLManager.createOWLOntologyManager();

		factory = manager.getOWLDataFactory();

		this.owlFunction = new OWLFunction();

		for(int i = 0; i <STOP_WORDS.length; i++){
			STOPWORDSLIST.add(STOP_WORDS[i]);
		}
	}

	public List<String> getMatchingResult(){
		return tableContent;
	}

	public static void main(String args[]) throws OWLOntologyCreationException, OntologyServiceException{

		levenshteinDistance test = new levenshteinDistance(2);

		test.startMatching();

	}

	public void startMatching() throws OWLOntologyCreationException{

		//Read in annotated ontology terms for the KORA model
		String fileName = "/Users/pc_iverson/Desktop/Ontology_term_pilot/InputForOntologyBuild.xls";

		tableModel model = new tableModel(fileName, false);

		model.processingTable();

		//Read in additional information for ontology terms
		readInOntologyTermFromLocalFile("/Users/pc_iverson/Desktop/Input/PredictionModel.owl");

		List<String> descriptions = new ArrayList<String>();

		descriptions.add("Original terms");

		descriptions.add("Ontology terms");

		descriptions.add("Definition");

		descriptions.add("Building blocks");

		fileName = "/Users/pc_iverson/Desktop/Ontology_term_pilot/PREVEND.xls";

		//fileName = "/Users/pc_iverson/Desktop/Ontology_term_pilot/LifeLines_Data_itmes.xls";

		tableModel model_2 = new tableModel(fileName, true);

		model_2.setStartingRow(11);

		model_2.processingTable();

		//HashMap<String, String> descriptionForVariable = model_2.getDescriptionForVariable("Data", "Description");

		HashMap<String, String> descriptionForVariable = model_2.getDescriptionForVariable("Veldnaam", "SPSS Omschrijving");

		System.out.println("Parsing the ontology");

		List<String> listOfAnnotationProperty = new ArrayList<String>();

		//listOfAnnotationProperty.add("http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#FULL_SYN");

		//parseOntology("/Users/pc_iverson/Desktop/Input/Thesaurus.owl", "http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#FULL_SYN");

		System.out.println("Ontology has been loaded");

		//System.out.println("Annotating the description of data items");

		//test.annotateTextWithOntologyTerm(descriptionForVariable);

		//System.out.println("The description has been annotated");

		//test.findOntologyTerms(descriptions, model, descriptionForVariable, 1);

		//test.findOntologyTerms(descriptions, model, descriptionForVariable, 2);

		findOntologyTerms(descriptions, model, descriptionForVariable);

		System.out.println();

		System.out.println("Parsing the ontology");

		//List<String> ontologies = new ArrayList<String>();

		//ontologies.add("/Users/pc_iverson/Desktop/Input/Thesaurus.owl");

		//test.ontoCatSearching(ontologies);

		System.out.println("Ontology has been loaded");
	}

	/**
	 * This method is used to find the exact matching between input terms and ontology terms. If there is any matching found,
	 * the record will be stored in mappingResult and ontologyTermAndDataItems variables. 
	 * 
	 * @param annotations
	 * @param model
	 * @param descriptionForVariable
	 * @param level
	 */
	public void findOntologyTerms(List<String> annotations, tableModel model, HashMap<String, String> descriptionForVariable){

		HashMap<String, String> levelAnnotation = model.getDescriptionForVariable(annotations.get(0), annotations.get(3));

		searchingForOntologyTermByStringMatching(levelAnnotation, descriptionForVariable);
	}

	public void outPutMappingDetails (HashMap<String, LinkedMap>mappingResultAndSimiarity, 
			HashMap<String, String> dataItemNameToDescription) {

		System.out.println();


		//		for(Entry<String, HashMap<String, Double>> entry: mappingResultAndSimiarity.entrySet()){
		//			entry.getKey();
		//			entry.getValue();
		//			for(Entry<String,Double> entry2: entry.getValue().entrySet()){
		//				
		//			}
		//		}

		for(String eachOriginalQuery : mappingResultAndSimiarity.keySet()){

			//			matchingResult = "<div id=\"" + eachOriginalQuery + "_button\" />";

			matchingResult = "Click to see the matching for parameter: <div class=\"parameters\" onclick=\"toggle('" + eachOriginalQuery + "')\">" + eachOriginalQuery + "</div>";

			matchingResult += "<table id=\"" + eachOriginalQuery + "\" style='border-spacing: 3px; width: 100%; display:none;'>";

			matchingResult += "<tr><td>Expanded Query</td><td>Matched data item</td><td>Similarity score</td><td>verfication</td></tr>";

			LinkedMap map = mappingResultAndSimiarity.get(eachOriginalQuery);

			List<LinkedInformation> links = map.getSortedInformation();

			int size = links.size();

			for(int i = size - 1; i > 0; i--){

				LinkedInformation eachRow = links.get(i);
				String expandedQuery = eachRow.expandedQuery;
				String matchedItem = eachRow.matchedItem;
				Double similarity = eachRow.similarity;
				matchingResult += "<tr id=\"" + eachOriginalQuery + "\"><td>" + expandedQuery + "</td><td>" 
						+ matchedItem + "</td><td>" + similarity + "</td><td><input type=\"checkbox\" name=\"" + expandedQuery + "\" id=\"" 
						+ expandedQuery + "\"></td></tr>";
				System.out.print(eachOriginalQuery + "\t" + expandedQuery + "\t" + matchedItem + "\t" + similarity);
				System.out.println();

				List<String> temp = null;
				
				if(parameterToExpandedQuery.containsKey(eachOriginalQuery)){

					temp = parameterToExpandedQuery.get(eachOriginalQuery);
					temp.add(expandedQuery);
					parameterToExpandedQuery.put(eachOriginalQuery, temp);
				}else{
					temp = new ArrayList<String>();
					temp.add(expandedQuery);
					parameterToExpandedQuery.put(eachOriginalQuery, temp);
				}

			}
			matchingResult += "</table>";

			tableContent.add(matchingResult);
		}
	}

	public void searchingForOntologyTermByStringMatching(HashMap<String, String> levelAnnotation, HashMap<String, String> descriptionForVariable){

		HashMap<String, LinkedMap> mappingResultAndSimiarity = new HashMap<String, LinkedMap>();

		for(String key : levelAnnotation.keySet()){

			if(!levelAnnotation.get(key).equals("")){

				List<String> expansion = expandedQueries.get(key.toLowerCase());

				List<String> queries = new ArrayList<String>();

				queries.add(levelAnnotation.get(key).toLowerCase());

				if(expansion == null){
					expansion = new ArrayList<String>();
					expansion.add(levelAnnotation.get(key));
				}

				for(String eachExpansion : expansion){

					String definitions[] = eachExpansion.split(separator);

					for(int i = 0; i < definitions.length; i++){
						if(!queries.contains(definitions[i].toLowerCase()))
							queries.add(definitions[i].toLowerCase());
					}

					if(!queries.contains(eachExpansion.toLowerCase()))
						queries.add(eachExpansion.toLowerCase());
				}

				for(String eachQuery : queries){

					eachQuery = eachQuery.replaceAll(separator, " ");

					double maxSimilarity = 0;

					String matchedDataItem = "";

					List<String> tokens = createNGrams(eachQuery.toLowerCase().trim(), " ", nGrams, false);

					for(String dataItem : descriptionForVariable.keySet()){

						List<String> dataItemTokens = createNGrams(descriptionForVariable.get(dataItem).toLowerCase().trim(), " ", nGrams, true);

						double similarity = calculateScore(dataItemTokens, tokens);

						if(similarity > maxSimilarity){

							maxSimilarity = similarity;
							matchedDataItem = descriptionForVariable.get(dataItem);
						}
					}

					LinkedMap temp = null;

					if(mappingResultAndSimiarity.containsKey(key)){
						temp = mappingResultAndSimiarity.get(key);
					}else{
						temp = new LinkedMap();
					}

					try {
						temp.add(eachQuery, matchedDataItem, maxSimilarity);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					mappingResultAndSimiarity.put(key, temp);

				}
			}
			System.out.println();
		}

		outPutMappingDetails(mappingResultAndSimiarity, descriptionForVariable);

	}

	public void addingNewMatchedItem(HashMap<String, List<String>> ontologyTermAndDataItems, String key, String dataItem){

		if(!ontologyTermAndDataItems.containsKey(key)){

			List<String> dataItems = new ArrayList<String>();
			if(!dataItems.contains(dataItem)){
				dataItems.add(dataItem);
				ontologyTermAndDataItems.put(key, dataItems);
			}
		}else{

			List<String> dataItems = ontologyTermAndDataItems.get(key);

			if(!dataItems.contains(dataItem))
				dataItems.add(dataItem);
			ontologyTermAndDataItems.put(key, dataItems);
		}
	}

	public int getnGrams() {
		return nGrams;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}

	public List<String> arrayToList(String[] array){

		List<String> list = new ArrayList<String>();

		for(int i = 0; i < array.length; i++){
			if(!list.contains(array[i]))
				list.add(array[i]);
		}

		return list;
	}

	public HashMap<String, List<String>> getNormalizedOntologyTerms() {
		return normalizedOntologyTerms;
	}

	public void setOntologyService (List<String> ontologyFiles) throws OntologyServiceException{

		List<FileOntologyService> services = new ArrayList<FileOntologyService>();

		for(String ontologyName : ontologyFiles){
			File ontologyFile = new File(ontologyName);
			services.add(new FileOntologyService(ontologyFile.toURI(), ontologyName));
		}

		os = CompositeDecorator.getService(services);
	}

	public void ontoCatSearching(List<String> ontologies) throws OntologyServiceException{

		this.setOntologyService(ontologies);

		List<String> allTerms = new ArrayList<String>();

		for(String ontologyName : ontologies){

			for(OntologyTerm ot : os.getAllTerms(ontologyName)){

				for(String eachSynonym : os.getSynonyms(ot)){
					synonymToLabel.put(eachSynonym, ot.getLabel());
				}
				allTerms.add(ot.getLabel());
				allTerms.addAll(os.getSynonyms(ot));
			}
		}

		normalizedOntologyTerms = this.createNGrams(allTerms, " ", this.nGrams, false);
	}

	public void readInOntologyTermFromLocalFile(String ontologyName) throws OWLOntologyCreationException{

		localOntology  = manager.loadOntologyFromOntologyDocument(new File(ontologyName));

		List<String> listOfAnnotationProperty = new ArrayList<String>();

		listOfAnnotationProperty.add(localOntology.getOntologyID().getOntologyIRI().toString() + "#additionalInfo");

		OWLFunction owlFunctionLocal = new OWLFunction(factory, localOntology);

		owlFunctionLocal.labelMapURI(null, localOntology.getOntologyID().getOntologyIRI().toString() + "#additionalInfo");

		this.expandedQueries = owlFunctionLocal.getExpandedQueries();
	}

	/**
	 * This is method is to load the ontology file from local system and create
	 * a hash table where the label is key and owlClass is the content
	 * 
	 * @param ontologyFilePath
	 * @throws OWLOntologyCreationException 
	 */
	public void parseOntology(String ontologyFilePath, String... annotationProperty) throws OWLOntologyCreationException{

		referenceOntology  = manager.loadOntologyFromOntologyDocument(new File(ontologyFilePath));

		owlFunction = new OWLFunction(factory, referenceOntology);

		owlFunction.labelMapURI(null, annotationProperty);
		
		labelToOWLClass = owlFunction.getLabelToClass();

		List<String> listOfOntologyTerms = new ArrayList<String>();

		listOfOntologyTerms.addAll(labelToOWLClass.keySet());

		//Levenshtein TODO
		normalizedOntologyTerms = createNGrams(listOfOntologyTerms, " ", nGrams, false);

		//Method from BBMRI plugin! All the possibilities of string term
		//normalizedOntologyTerms = createNGrams(listOfOntologyTerms);

		System.out.println("Ontology has been loaded and stored in the hash table");
	}


	public List<String> removeStopWords(String[] listOfWords, List<String> STOPWORDSLIST){

		List<String> removedStopWordsList = new ArrayList<String>();

		for(int index = 0; index < listOfWords.length; index++){

			if(STOPWORDSLIST == null){
				removedStopWordsList.add(listOfWords[index]);
			}else if(!STOPWORDSLIST.contains(listOfWords[index])){
				removedStopWordsList.add(listOfWords[index]);
			}
		}

		return removedStopWordsList;
	}


	/**
	 * //create n-grams tokens of the string.
	 * @param inputString
	 * @param nGrams
	 * @return
	 */
	public List<String> createNGrams(String eachString, String separator, int nGrams, boolean stopWords){

		String [] singleWords = eachString.split(separator);

		List<String> removedStopWordsList = new ArrayList<String>();

		List<String> tokens = new ArrayList<String>();

		if(stopWords == true){
			removedStopWordsList = removeStopWords(singleWords, STOPWORDSLIST);
		}else{
			removedStopWordsList = removeStopWords(singleWords, null);
		}

		//Padding the string
		for(String singleWord : removedStopWordsList){
			//TODO what if there is overlapping between different words such diebetes mellitus. 
			//The s$ will be the produced from two words. 
			singleWord = singleWord.toLowerCase();
			singleWord = "^" + singleWord;
			singleWord = singleWord + "$";

			for(int i = 0; i < singleWord.length(); i++){

				if(i + nGrams < singleWord.length()){
					tokens.add(singleWord.substring(i, i + nGrams));
				}else{
					if(!tokens.contains(singleWord.substring(singleWord.length() - 2))){
						tokens.add(singleWord.substring(singleWord.length() - 2).toLowerCase());
					}
				}
			}
		}
		return tokens;
	}

	/**
	 * //create n-grams tokens of the string.
	 * @param inputString
	 * @param nGrams
	 * @return
	 */
	public HashMap<String, List<String>> createNGrams(List<String> inputString, String separator, int nGrams, boolean stopWords){

		HashMap<String, List<String>> normalizedInputString = new HashMap<String, List<String>>();

		for(String eachString : inputString){

			String [] singleWords = eachString.split(separator);

			List<String> tokens = new ArrayList<String>();

			List<String> removedStopWordsList = new ArrayList<String>();

			if(stopWords == true){
				removedStopWordsList = removeStopWords(singleWords, STOPWORDSLIST);
			}else{
				removedStopWordsList = removeStopWords(singleWords, null);
			}

			//Padding the string
			for(String singleWord : removedStopWordsList){
				//TODO what if there is overlapping between different words such diebetes mellitus. 
				//The s$ will be the produced from two words. 
				singleWord = singleWord.toLowerCase();
				singleWord = "^" + singleWord;
				singleWord = singleWord + "$";

				for(int i = 0; i < singleWord.length(); i++){

					if(i + nGrams < singleWord.length()){
						tokens.add(singleWord.substring(i, i + nGrams));
					}else{
						if(!tokens.contains(singleWord.substring(singleWord.length() - 2))){
							tokens.add(singleWord.substring(singleWord.length() - 2).toLowerCase());
						}
					}
				}
			}

			normalizedInputString.put(eachString, tokens);
		}

		return normalizedInputString;
	}

	/**
	 * Calculate the levenshtein distance
	 * @param inputStringTokens
	 * @param ontologyTermTokens
	 * @return
	 */
	public double calculateScore(List<String> inputStringTokens, List<String> ontologyTermTokens){

		int matchedTokens = 0;
		double similarity = 0;

		for(String eachToken : inputStringTokens){
			if(ontologyTermTokens.contains(eachToken)){
				matchedTokens++;
			}
		}
		double totalToken = Math.max(inputStringTokens.size(), ontologyTermTokens.size());
		similarity = matchedTokens/totalToken*100;
		DecimalFormat df = new DecimalFormat("#0.000");
		return Double.parseDouble(df.format(similarity));
	}
}

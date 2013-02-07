package plugins.harmonization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.molgenis.framework.ui.EasyPluginModel;
import org.molgenis.pheno.Measurement;
import org.quartz.Scheduler;

import plugins.HarmonizationComponent.NGramMatchingModel;
import plugins.catalogueTreeNewVersion.catalogueTreeComponent;
import uk.ac.ebi.ontocat.bioportal.BioportalOntologyService;

public class HarmonizationModel extends EasyPluginModel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4404912460247332113L;

	private final AtomicInteger countForFinishedQueries = new AtomicInteger(0);

	private final AtomicInteger countForFinishedJobs = new AtomicInteger(0);

	private int totalNumber = 0;

	private String retrieveResult = Boolean.FALSE.toString();

	private String selectedValidationStudy = null;

	private String selectedPredictionModel = null;

	private Scheduler scheduler = null;

	private BioportalOntologyService os = null;

	private NGramMatchingModel model = null;

	private catalogueTreeComponent catalogue = null;

	private Map<String, Measurement> measurements = null;

	private List<String> predictionModels = new ArrayList<String>();

	private List<String> validationStudies = new ArrayList<String>();

	private List<String> reservedInv = new ArrayList<String>();

	private Map<String, PredictorInfo> predictors = new HashMap<String, PredictorInfo>();

	// private String[] ontologies = { "1351", "1136", "1353", "2018", "1032" };
	private String[] ontologies =
	{ "1032", "1136", "1353" };

	private List<String> ontologyAccessions = Arrays.asList(ontologies);

	private String freeMakerTemplate = "Harmonization.ftl";

	private int totalJobs = 0;

	private long startTime = 0;

	private String estimatedTime = "";

	private String processedTime = "";

	private boolean isStringMatching = false;

	private Map<Measurement, List<Set<String>>> nGramsMapForMeasurements;

	public HarmonizationModel(Harmonization controller)
	{
		super(controller);
	}

	public String getFreeMakerTemplate()
	{
		return freeMakerTemplate;
	}

	public void setFreeMakerTemplate(String freeMakerTemplate)
	{
		this.freeMakerTemplate = freeMakerTemplate;
	}

	public List<String> getDataTypes()
	{
		List<String> dataTypeOptions = new ArrayList<String>();
		dataTypeOptions.add("string");
		dataTypeOptions.add("int");
		dataTypeOptions.add("datetime");
		dataTypeOptions.add("categorical");
		dataTypeOptions.add("decimal");
		return dataTypeOptions;
	}

	public List<String> getPredictionModels()
	{
		return predictionModels;
	}

	public String getUrl()
	{
		return "molgenis.do?__target=" + this.getName();
	}

	public Scheduler getScheduler()
	{
		return scheduler;
	}

	public void setScheduler(Scheduler scheduler)
	{
		this.scheduler = scheduler;
	}

	public BioportalOntologyService getOs()
	{
		return os;
	}

	public void setOs(BioportalOntologyService os)
	{
		this.os = os;
	}

	public NGramMatchingModel getMatchingModel()
	{
		return model;
	}

	public void setMatchingModel(NGramMatchingModel model)
	{
		this.model = model;
	}

	public catalogueTreeComponent getCatalogue()
	{
		return catalogue;
	}

	public void setCatalogue(catalogueTreeComponent catalogue)
	{
		this.catalogue = catalogue;
	}

	public Map<String, Measurement> getMeasurements()
	{
		return measurements;
	}

	public void setMeasurements(HashMap<String, Measurement> measurements)
	{
		this.measurements = measurements;
	}

	public List<String> getValidationStudies()
	{
		return validationStudies;
	}

	public void setValidationStudies(List<String> validationStudies)
	{
		this.validationStudies = validationStudies;
	}

	public List<String> getReservedInv()
	{
		return reservedInv;
	}

	public Map<String, PredictorInfo> getPredictors()
	{
		return predictors;
	}

	public void setPredictors(HashMap<String, PredictorInfo> predictors)
	{
		this.predictors = predictors;
	}

	public List<String> getOntologyAccessions()
	{
		return ontologyAccessions;
	}

	public int getTotalNumber()
	{
		return totalNumber;
	}

	public void setTotalNumber(int totalNumber)
	{
		this.totalNumber = totalNumber;
	}

	public int getFinishedNumber()
	{
		return countForFinishedQueries.get();
	}

	public int getFinishedJobs()
	{
		return countForFinishedJobs.get();
	}

	public void setTotalJobs(int totalJobs)
	{
		this.totalJobs = totalJobs;
	}

	public int getTotalJobs()
	{
		return totalJobs;
	}

	public void setStartTime(long startTime)
	{
		this.startTime = startTime;
	}

	public long getStartTime()
	{
		return startTime;
	}

	public String getSelectedValidationStudy()
	{
		return selectedValidationStudy;
	}

	public void setSelectedValidationStudy(String selectedValidationStudy)
	{
		this.selectedValidationStudy = selectedValidationStudy;
	}

	public String getSelectedPredictionModel()
	{
		return selectedPredictionModel;
	}

	public void setSelectedPredictionModel(String selectedPredictionModel)
	{
		this.selectedPredictionModel = selectedPredictionModel;
	}

	public void setEstimatedTime(String estimatedTime)
	{
		this.estimatedTime = estimatedTime;
	}

	public String getEstimatedTime()
	{
		return estimatedTime;
	}

	public void setProcessedTime(String processedTime)
	{
		this.processedTime = processedTime;
	}

	public String getProcessedTime()
	{
		return processedTime;
	}

	public String isRetrieveResult()
	{
		return retrieveResult;
	}

	public void setRetrieveResult(boolean retrieveResult)
	{
		if (retrieveResult)
		{
			this.retrieveResult = Boolean.TRUE.toString();
		}
		else
		{
			this.retrieveResult = Boolean.FALSE.toString();
		}
	}

	public boolean isStringMatching()
	{
		return isStringMatching;
	}

	public void setIsStringMatching(boolean isStringMatching)
	{
		this.isStringMatching = isStringMatching;
	}

	public void setNGramsMapForMeasurements(Map<Measurement, List<Set<String>>> maps)
	{
		this.nGramsMapForMeasurements = maps;
	}

	public Map<Measurement, List<Set<String>>> getnGramsMapForMeasurements()
	{
		return nGramsMapForMeasurements;
	}

	public int incrementFinishedQueries()
	{
		return countForFinishedQueries.incrementAndGet();
	}

	public void setInitialFinishedQueries(int value)
	{
		countForFinishedQueries.set(value);
	}

	public int incrementFinishedJob()
	{
		return countForFinishedJobs.incrementAndGet();
	}

	public void setInitialFinishedJob(int value)
	{
		countForFinishedJobs.set(value);
	}
}
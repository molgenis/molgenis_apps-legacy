/* Date:        September 14, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.animaldb.plugins.breeding;

import static com.googlecode.charts4j.Color.ALICEBLUE;
import static com.googlecode.charts4j.Color.BLACK;
import static com.googlecode.charts4j.Color.BLUE;
import static com.googlecode.charts4j.Color.PINK;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.molgenis.animaldb.commonservice.CommonService;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.server.MolgenisRequest;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.util.Entity;

import com.googlecode.charts4j.AxisLabels;
import com.googlecode.charts4j.AxisLabelsFactory;
import com.googlecode.charts4j.AxisStyle;
import com.googlecode.charts4j.AxisTextAlignment;
import com.googlecode.charts4j.BarChart;
import com.googlecode.charts4j.BarChartPlot;
import com.googlecode.charts4j.DataUtil;
import com.googlecode.charts4j.Fills;
import com.googlecode.charts4j.GCharts;
import com.googlecode.charts4j.Plots;

public class BreedingOverview extends PluginModel<Entity>
{
	private static final long serialVersionUID = 1906962555512398640L;
	private CommonService cs = CommonService.getInstance();
	private String action = "init";
	private String lineName;
	private String sourceName;
	private String speciesName;
	private String remarks;
	private Database DB = null;
	private List<String> investigationNames;
	private List<Integer> investigationIDs;
	private SimpleDateFormat dateOnlyFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

	private List<ObservationTarget> sourceList;
	private List<ObservationTarget> lineList;
	private List<ObservationTarget> speciesList;
	private List<ObservationTarget> litterList;

	private List<Integer> aliveAnimalIDs = new ArrayList<Integer>();
	// line info
	private Integer lineObsValSize;
	private List<ObservedValue> lineObsVal;
	private List<Individual> lineIndInObsVal;
	private List<Integer> lineIndIDsInObsVal = new ArrayList<Integer>();
	private List<String> lineIndNamesInObsVal = new ArrayList<String>();
	private List<ObservedValue> dobValList = new ArrayList<ObservedValue>();
	private List<ObservedValue> sexValList = new ArrayList<ObservedValue>();
	private List<ObservedValue> gmValList = new ArrayList<ObservedValue>();
	private List<ObservedValue> gsValList = new ArrayList<ObservedValue>();

	// report values:a
	private Map<String, String> maleCnt = new HashMap<String, String>();
	private Map<String, List<ObservedValue>> maleOv = new HashMap<String, List<ObservedValue>>();
	private Map<String, String> femaleCnt = new HashMap<String, String>();
	private Map<String, List<ObservedValue>> femaleOv = new HashMap<String, List<ObservedValue>>();
	private Map<String, String> unknSexCnt = new HashMap<String, String>();

	public BreedingOverview(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	public String getCustomHtmlHeaders()
	{
		return "<script type=\"text/javascript\" src=\"res/jquery-plugins/datatables/js/jquery.dataTables.js\"></script>\n"
				+ "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/jquery-plugins/datatables/css/demo_table_jui.css\">\n"
				+ "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/animaldb.css\">";
	}

	@Override
	public String getViewName()
	{
		return "org_molgenis_animaldb_plugins_breeding_BreedingOverview";
	}

	@Override
	public String getViewTemplate()
	{
		return "org/molgenis/animaldb/plugins/breeding/BreedingOverview.ftl";
	}

	public void setAction(String action)
	{
		this.action = action;
	}

	public String getAction()
	{
		return action;
	}

	public String getFullName(String lineName)
	{
		String fullName = "";
		try
		{
			if (cs.getMostRecentValueAsString(lineName, "LineFullName") != null)
			{
				fullName = cs.getMostRecentValueAsString(lineName, "LineFullName");
			}
		}
		catch (Exception e)
		{
			fullName = "Error when retrieving full name";
		}
		return fullName;
	}

	public String getSourceName(String lineName)
	{
		String sourceName;
		try
		{
			sourceName = cs.getMostRecentValueAsXrefName(lineName, "Source");
		}
		catch (Exception e)
		{
			sourceName = "Error when retrieving source";
		}
		return sourceName;
	}

	public String getSpeciesName(String lineName)
	{
		String speciesName;
		try
		{
			speciesName = cs.getMostRecentValueAsXrefName(lineName, "Species");
		}
		catch (Exception e)
		{
			speciesName = "Error when retrieving species";
		}
		return speciesName;
	}

	public String getRemarksString(String lineName) throws DatabaseException
	{
		String returnString = "";
		try
		{
			if (cs.getMostRecentValueAsString(lineName, "Remark") != null)
			{
				returnString = cs.getMostRecentValueAsString(lineName, "Remark");
			}
		}
		catch (Exception e)
		{
			returnString = "Error when retrieving remarks";
		}
		return returnString;
	}

	public void prepareLineInfo(String lineNameString)
	{
		try
		{
			// query the observedValue table for all obsval with relation line
			// limited to living animals.
			// store the results

			Query<ObservedValue> q1 = this.DB.query(ObservedValue.class);
			QueryRule r1 = new QueryRule(ObservedValue.INVESTIGATION_NAME, Operator.IN, investigationNames);
			QueryRule r2 = new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "Line");
			QueryRule r3 = new QueryRule(ObservedValue.RELATION, Operator.EQUALS, cs.getObservationTargetByName(
					lineNameString).getId());
			QueryRule r4 = new QueryRule(ObservedValue.TARGET, Operator.IN, this.aliveAnimalIDs);
			q1.addRules(r1, r2, r3, r4);
			this.lineObsVal = q1.find();
			this.lineObsValSize = q1.find().size();
			this.lineIndIDsInObsVal.clear();
			this.lineIndNamesInObsVal.clear();

			Integer mCnt = 0;
			Integer fCnt = 0;
			Integer uCnt = 0;

			if (!this.lineObsValSize.equals(0))
			{
				for (ObservedValue val : this.lineObsVal)
				{
					this.lineIndIDsInObsVal.add(val.getTarget_Id());
					this.lineIndNamesInObsVal.add(val.getTarget_Name());
				}

				this.dobValList = cs.getObservedValuesByTargetsAndMeasurement(this.lineIndNamesInObsVal, "DateOfBirth",
						this.investigationIDs);
				this.sexValList = cs.getObservedValuesByTargetsAndMeasurement(this.lineIndNamesInObsVal, "Sex",
						this.investigationIDs);
				this.gmValList = cs.getObservedValuesByTargetsAndMeasurement(this.lineIndNamesInObsVal,
						"GeneModification", this.investigationIDs);
				this.gsValList = cs.getObservedValuesByTargetsAndMeasurement(this.lineIndNamesInObsVal, "GeneState",
						this.investigationIDs);

				if (sexValList != null)
				{
					List<ObservedValue> m = new ArrayList<ObservedValue>();
					List<ObservedValue> f = new ArrayList<ObservedValue>();
					for (ObservedValue ovSex : sexValList)
					{
						if (ovSex.getRelation_Name().equalsIgnoreCase("male"))
						{
							m.add(ovSex);
							mCnt++;
						}
						else if (ovSex.getRelation_Name().equalsIgnoreCase("female"))
						{
							f.add(ovSex);
							fCnt++;
						}
						else
						{
							uCnt++;
						}
						this.maleOv.put(lineNameString, m);
						this.femaleOv.put(lineNameString, f);
					}
				}
			}
			this.maleCnt.put(lineNameString, mCnt.toString());
			this.femaleCnt.put(lineNameString, fCnt.toString());
			this.unknSexCnt.put(lineNameString, fCnt.toString());

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public String getLineSexCnt(String lineNameString, String sexString)
	{
		if (sexString.equalsIgnoreCase("male"))
		{
			return this.maleCnt.get(lineNameString);
		}
		else if (sexString.equalsIgnoreCase("female"))
		{
			return this.femaleCnt.get(lineNameString);
		}
		else
		{
			return this.unknSexCnt.get(lineNameString);
		}
	}

	public String createAgeHistogram(String lineNameString) throws Exception
	{
		if (this.dobValList != null && !this.lineObsValSize.equals(0))
		{
			ArrayList<String> males = new ArrayList<String>();
			ArrayList<String> females = new ArrayList<String>();
			List<ObservedValue> mov = this.maleOv.get(lineNameString);
			List<ObservedValue> fov = this.femaleOv.get(lineNameString);
			for (ObservedValue male : mov)
			{
				males.add(male.getTarget_Name());
			}
			for (ObservedValue female : fov)
			{
				females.add(female.getTarget_Name());
			}
			// Date dobDate;
			ArrayList<Date> mdobDates = new ArrayList<Date>();
			ArrayList<Date> fdobDates = new ArrayList<Date>();
			for (ObservedValue dobOv : this.dobValList)
			{
				if (males.contains(dobOv.getTarget_Name()))
				{
					mdobDates.add(dateOnlyFormat.parse(dobOv.getValue()));
				}
				if (females.contains(dobOv.getTarget_Name()))
				{
					fdobDates.add(dateOnlyFormat.parse(dobOv.getValue()));
				}
			}
			ArrayList<ArrayList<Date>> dobDates = new ArrayList<ArrayList<Date>>();
			dobDates.add(mdobDates);
			dobDates.add(fdobDates);
			List<double[]> histDataList = new ArrayList<double[]>();
			for (ArrayList<Date> al : dobDates)
			{
				double[] histData =
				{ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };// new
				long twelveWeeks = 7257600000l;
				Date startDate = new Date();
				startDate.setTime(startDate.getTime() - twelveWeeks);
				long sDate = startDate.getTime();

				for (Date d : al)
				{

					boolean sorted = false;
					long dobDate = d.getTime();
					for (int i = 0; i < 9; i++)
					{
						if (dobDate >= sDate - (i * twelveWeeks) && !sorted)
						{
							histData[i]++;
							sorted = true;
						}
					}
					if (!sorted)
					{
						histData[9]++;
					}

				}
				histDataList.add(histData);
			}
			try
			{
				String chart = "";
				chart = createBarChart(histDataList.get(0), histDataList.get(1));
				return "<img src=\"" + chart + "\" />";
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return "na";
			}
		}
		else
		{
			return "na";
		}
	}

	public String getGenoTypes(String lineNameString) throws DatabaseException, ParseException
	{
		String genoTypes = "";

		if (this.lineIndIDsInObsVal.size() > 0)
		{
			List<ObservationTarget> allLineAnimals = cs.getObservationTargetsbyId(this.lineIndIDsInObsVal);
			if (allLineAnimals.size() > 0)
			{
				List<ObservedValue> allGenoTypes = cs.getObservedValuesByTargetAndMeasurement(allLineAnimals.get(0)
						.getName(), "GeneModification", this.investigationIDs);

				if (allGenoTypes != null)
				{
					for (ObservedValue ov : allGenoTypes)
					{
						genoTypes += ov.getValue() + "<br />";
					}
				}
				else
				{
					genoTypes = "na";
				}

				// count occurences of genotypes

			}
			else
			{
				genoTypes = "na";
			}
		}
		else
		{
			genoTypes = "na";
		}

		return genoTypes;
	}

	public String getUnWeaned(String lineNameString) throws DatabaseException, ParseException
	{
		List<Integer> allLitterIDs = new ArrayList<Integer>();

		for (ObservationTarget ot : this.litterList)
		{
			allLitterIDs.add(ot.getId());
		}

		if (allLitterIDs.size() > 0)
		{
			Query<ObservedValue> lq = this.DB.query(ObservedValue.class);
			QueryRule ra1 = new QueryRule(ObservedValue.INVESTIGATION_NAME, Operator.IN, investigationNames);
			QueryRule ra2 = new QueryRule(ObservedValue.TARGET, Operator.IN, allLitterIDs);
			QueryRule ra3 = new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "Line");
			QueryRule ra4 = new QueryRule(ObservedValue.RELATION_NAME, Operator.EQUALS, lineNameString);

			lq.addRules(ra1, ra2, ra3, ra4);
			List<ObservedValue> litterList = lq.find();

			if (litterList.size() > 0)
			{
				List<Integer> activeLitterListIDs = new ArrayList<Integer>();
				for (ObservedValue ov : litterList)
				{
					activeLitterListIDs.add(ov.getTarget_Id());
				}
				if (activeLitterListIDs.size() > 0)
				{
					Query<ObservedValue> bSizeq = this.DB.query(ObservedValue.class);
					QueryRule r1 = new QueryRule(ObservedValue.INVESTIGATION_NAME, Operator.IN, investigationNames);
					QueryRule r2 = new QueryRule(ObservedValue.TARGET, Operator.IN, activeLitterListIDs);
					QueryRule r3 = new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "Size");

					lq.addRules(r1, r2, r3);
					List<ObservedValue> bSizeList = bSizeq.find();

					Integer size = bSizeList.size();
					return size.toString();
				}
				else
				{
					return "0";
				}

			}
			else
			{
				return "na";
			}

		}

		return "na";
	}

	public String getLastLitter(String lineNameString) throws DatabaseException
	{
		List<Integer> allLitterIDs = new ArrayList<Integer>();

		for (ObservationTarget ot : this.litterList)
		{
			allLitterIDs.add(ot.getId());
		}

		if (allLitterIDs.size() > 0)
		{
			Query<ObservedValue> lq = this.DB.query(ObservedValue.class);
			QueryRule r1 = new QueryRule(ObservedValue.INVESTIGATION_NAME, Operator.IN, investigationNames);
			QueryRule r2 = new QueryRule(ObservedValue.TARGET, Operator.IN, allLitterIDs);
			QueryRule r3 = new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "Line");
			QueryRule r4 = new QueryRule(ObservedValue.RELATION_NAME, Operator.EQUALS, lineNameString);
			QueryRule r5 = new QueryRule(Operator.SORTDESC, ObservedValue.TIME);

			lq.addRules(r1, r2, r3, r4, r5);

			List<ObservedValue> lastLitterList = lq.find();
			if (lastLitterList.size() > 0)
			{
				return this.dateOnlyFormat.format(lastLitterList.get(0).getTime());
			}
			else
			{
				return "na";
			}

		}

		return "na";
	}

	public String createBarChart(double[] mdataArray, double[] fdataArray)
	{

		double maxVal = 0.0;

		for (int counter = 1; counter < mdataArray.length; counter++)
		{
			if (mdataArray[counter] > maxVal)
			{
				maxVal = mdataArray[counter];
			}
		}
		for (int counter = 1; counter < fdataArray.length; counter++)
		{
			if (fdataArray[counter] > maxVal)
			{
				maxVal = fdataArray[counter];
			}
		}
		if (maxVal < 40)
		{
			maxVal = 40;
		}
		else
		{
			maxVal += 10;
		}
		// Defining data plots.

		BarChartPlot mAgeHistPlot = Plots.newBarChartPlot(DataUtil.scaleWithinRange(0, maxVal, mdataArray), BLUE);

		BarChartPlot fAgeHistPlot = Plots.newBarChartPlot(DataUtil.scaleWithinRange(0, maxVal, fdataArray), PINK);

		// Instantiating chart.
		// BarChartPlot ageHistPlot =
		// Plots.newBarChartPlot(DataUtil.scale(dataArrayList.get(0)), BLACK);
		// BarChart chart = GCharts.newBarChart(ageHistPlot);
		BarChart chart = GCharts.newBarChart(mAgeHistPlot, fAgeHistPlot);

		// BarChartPlot ageHistFemale =
		// Plots.newBarChartPlot(DataUtil.scale(dataArray), PINK);

		// Defining axis info and styles
		AxisStyle axisStyle = AxisStyle.newAxisStyle(BLACK, 12, AxisTextAlignment.CENTER);

		// Adding axis info to chart.
		AxisLabels xAxisLabels = AxisLabelsFactory.newAxisLabels("0", "12", "24", "36", "48", "60", "72", "84", "96",
				">96");
		xAxisLabels.setAxisStyle(axisStyle);
		chart.addXAxisLabels(xAxisLabels);

		AxisLabels yAxisLabels = AxisLabelsFactory.newNumericRangeAxisLabels(0.0, maxVal);
		yAxisLabels.setAxisStyle(axisStyle);
		chart.addYAxisLabels(yAxisLabels);

		chart.setSize(240, 120);
		chart.setBarWidth(10);
		chart.setSpaceWithinGroupsOfBars(0);
		chart.setSpaceBetweenGroupsOfBars(1);
		chart.setDataStacked(false);
		// chart.setTitle("Team Scores", BLACK, 16);
		// chart.setGrid(100, 10, 3, 2);
		chart.setBackgroundFill(Fills.newSolidFill(ALICEBLUE));
		// LinearGradientFill fill = Fills.newLinearGradientFill(0, LAVENDER,
		// 100);
		// fill.addColorAndOffset(WHITE, 0);
		// chart.setAreaFill(fill);

		String url = chart.toURLString();
		return url;
	}

	@Override
	public void handleRequest(Database db, MolgenisRequest request)
	{
		try
		{
			cs.setDatabase(db);

			this.setAction(request.getAction());

			if (action.equals("JumpTo"))
			{
				// do someting useful
			}

		}
		catch (Exception e)
		{
			this.getMessages().clear();
			if (e.getMessage() != null)
			{
				this.getMessages().add(new ScreenMessage(e.getMessage(), false));
			}
			e.printStackTrace();
		}

	}

	@Override
	public void reload(Database db)
	{
		this.DB = db;
		cs.setDatabase(db);
		cs.makeObservationTargetNameMap(this.getLogin().getUserName(), false);

		try
		{
			this.investigationNames = cs.getAllUserInvestigationNames(this.getLogin().getUserName());
			this.investigationIDs = cs.getAllUserInvestigationIds(this.getLogin().getUserName());
			// Populate source list
			// All source types pertaining to
			// "Eigen fok binnen uw organisatorische werkeenheid"
			sourceList = new ArrayList<ObservationTarget>();
			List<ObservationTarget> tmpSourceList = cs.getAllMarkedPanels("Source", investigationNames);
			for (ObservationTarget tmpSource : tmpSourceList)
			{
				int featid = cs.getMeasurementId("SourceType");
				Query<ObservedValue> sourceTypeQuery = db.query(ObservedValue.class);
				sourceTypeQuery.addRules(new QueryRule(ObservedValue.TARGET, Operator.EQUALS, tmpSource.getId()));
				sourceTypeQuery.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, featid));
				List<ObservedValue> sourceTypeValueList = sourceTypeQuery.find();
				if (sourceTypeValueList.size() > 0)
				{
					String sourcetype = sourceTypeValueList.get(0).getValue();
					if (sourcetype.equals("Eigen fok binnen uw organisatorische werkeenheid"))
					{
						sourceList.add(tmpSource);
					}
				}
			}
			// Populate species list
			// this.speciesList = cs.getAllMarkedPanels("Species",
			// investigationNames);
			// Populate existing lines list
			this.lineList = cs.getAllMarkedPanels("Line", investigationNames);

			this.litterList = cs.getAllMarkedPanels("Litter", investigationNames);

			// make a list of all living animalIDs
			// query the observedValue table for all obsval with relation line
			// name and store the result and the size of the array
			Query<ObservedValue> q1 = this.DB.query(ObservedValue.class);
			QueryRule r1 = new QueryRule(ObservedValue.INVESTIGATION_NAME, Operator.IN, investigationNames);
			QueryRule r2 = new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "Active");
			QueryRule r3 = new QueryRule(ObservedValue.VALUE, Operator.EQUALS, "Alive");
			q1.addRules(r1, r2, r3);
			List<ObservedValue> ovl = q1.find();

			for (ObservedValue val : ovl)
			{
				this.aliveAnimalIDs.add(val.getTarget_Id());
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
			this.getMessages().clear();
			if (e.getMessage() != null)
			{
				this.getMessages().add(new ScreenMessage(e.getMessage(), false));
			}
		}

	}

	public List<ObservationTarget> getLineList()
	{
		return lineList;
	}

	public void setLineList(List<ObservationTarget> lineList)
	{
		this.lineList = lineList;
	}

}
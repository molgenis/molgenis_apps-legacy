/* Date:        September 14, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.animaldb.plugins.breeding;

import static com.googlecode.charts4j.Color.ALICEBLUE;
import static com.googlecode.charts4j.Color.BLACK;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
		// List<String> remarksList = cs.getRemarks(lineId);
		String returnString = "";
		// for (String remark : remarksList) {
		// returnString += (remark + "<br>");
		// }
		// if (returnString.length() > 0) {
		// returnString = returnString.substring(0, returnString.length() - 4);
		// }
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
			// name and store the result and the size of the array

			Query<ObservedValue> q1 = this.DB.query(ObservedValue.class);
			QueryRule r1 = new QueryRule(ObservedValue.INVESTIGATION_NAME, Operator.IN, investigationNames);
			QueryRule r2 = new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "Line");
			QueryRule r3 = new QueryRule(ObservedValue.RELATION, Operator.EQUALS, cs.getObservationTargetByName(
					lineNameString).getId());
			QueryRule r4 = new QueryRule(ObservedValue.TARGET, Operator.IN, this.aliveAnimalIDs);
			q1.addRules(r1, r2, r3, r4);
			this.lineObsVal = q1.find();
			this.lineObsValSize = q1.find().size();
			// System.out.println("\n }}}}} : " + lineNameString + " " +
			// this.lineObsValSize);
			this.lineIndIDsInObsVal.clear();

			if (!this.lineObsValSize.equals(0))
			{
				for (ObservedValue val : this.lineObsVal)
				{
					this.lineIndIDsInObsVal.add(val.getTarget_Id());
					// System.out.println("\n )))))))))))) : " +
					// val.getTarget_Name() + " " + val.getTarget_Id());
				}
			}

			// store the relevant info

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	// public String getCountPerSex(String lineName, String sex)
	public String getCountPerSex(String lineNameString, String sexString)
	{
		try
		{
			// Query<ObservedValue> q1 = this.DB.query(ObservedValue.class);
			QueryRule r1 = new QueryRule(ObservedValue.INVESTIGATION_NAME, Operator.IN, investigationNames);
			QueryRule r2 = new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "Sex");
			QueryRule r3 = new QueryRule(ObservedValue.RELATION, Operator.EQUALS, cs.getObservationTargetByName(
					sexString).getId());
			// QueryRule r4 = new QueryRule(ObservedValue.TARGET, Operator.IN,
			// this.aliveAnimalIDs);
			// q1.addRules(r1, r2);
			// List<ObservedValue> ovl1 = q1.find();
			// Integer size = q1.find().size();
			if (!this.lineObsValSize.equals(0)) // TODO check if we can improve
												// by checking size of
												// individual ids.
			{
				QueryRule r5 = new QueryRule(ObservedValue.TARGET, Operator.IN, this.lineIndIDsInObsVal);
				Query<ObservedValue> q2 = this.DB.query(ObservedValue.class);
				q2.addRules(r1, r2, r3, r5);
				Integer size = q2.find().size();
				return size.toString();
				// return "0";
			}
			else
			{
				return "na";
			}

		}
		catch (Exception e)
		{
			speciesName = "Error when retrieving species";
		}
		return speciesName;
	}

	public String createAgeHistogram(String lineNameString) throws DatabaseException
	{
		double[] histData = new double[10];
		long twelveWeeks = 7257600000l;
		Date startDate = new Date();
		Date endDate = new Date();
		startDate.setTime(startDate.getTime() - twelveWeeks);

		// List<ObservedValue> ovl1 = q1.find();
		// Integer size = q1.find().size();
		if (!this.lineObsValSize.equals(0))
		{
			QueryRule r1 = new QueryRule(ObservedValue.INVESTIGATION_NAME, Operator.IN, investigationNames);
			QueryRule r2 = new QueryRule(ObservedValue.TARGET, Operator.IN, this.aliveAnimalIDs);
			QueryRule r3 = new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "DateOfBirth");
			QueryRule r6 = new QueryRule(ObservedValue.TARGET, Operator.IN, this.lineIndIDsInObsVal);
			System.out.println("## " + lineNameString);
			for (int i = 0; i < 9; i++)
			{

				QueryRule r4 = new QueryRule(ObservedValue.VALUE, Operator.GREATER_EQUAL,
						dateOnlyFormat.format(startDate));
				QueryRule r5 = new QueryRule(ObservedValue.VALUE, Operator.LESS, dateOnlyFormat.format(endDate));

				// System.out.println("\n####### " + " " + startDate.getTime() +
				// " startdate: "
				// + dateOnlyFormat.format(startDate));
				// System.out.println("\n####### " + endDate.getTime() +
				// " enddate: " + dateOnlyFormat.format(endDate));
				Query<ObservedValue> q2 = this.DB.query(ObservedValue.class);
				q2.addRules(r1, r2, r3, r4, r5, r6);
				histData[i] = (double) q2.find().size();
				// System.out.println("\n#### " + histData[i]);

				startDate.setTime(startDate.getTime() - twelveWeeks);
				endDate.setTime(endDate.getTime() - twelveWeeks);

			}
			Query<ObservedValue> q2 = this.DB.query(ObservedValue.class);
			QueryRule r5 = new QueryRule(ObservedValue.VALUE, Operator.LESS, dateOnlyFormat.format(endDate));
			q2.addRules(r1, r2, r3, r5, r6);
			histData[9] = (double) q2.find().size();
			return "<img src=\"" + createBarChart(histData) + "\" />";
		}
		else
		{
			return "na";
		}

		// double[] dataArray =
		// { 10, 3, 12, 30, 14, 15, 16, 55, 18, 19 };

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

	public String createBarChart(double[] dataArray)
	{
		// Defining data plots.
		BarChartPlot ageHistMale = Plots.newBarChartPlot(DataUtil.scale(dataArray), BLACK);
		// BarChartPlot ageHistFemale =
		// Plots.newBarChartPlot(DataUtil.scale(dataArray), PINK);

		// Instantiating chart.
		// BarChart chart = GCharts.newBarChart(ageHistMale, ageHistFemale);
		BarChart chart = GCharts.newBarChart(ageHistMale);

		// Defining axis info and styles
		AxisStyle axisStyle = AxisStyle.newAxisStyle(BLACK, 12, AxisTextAlignment.CENTER);

		// Adding axis info to chart.
		AxisLabels xAxisLabels = AxisLabelsFactory.newAxisLabels("0", "12", "24", "36", "48", "60", "72", "84", "96",
				">96");
		xAxisLabels.setAxisStyle(axisStyle);
		chart.addXAxisLabels(xAxisLabels);

		double maxVal = 0.0;
		for (int counter = 1; counter < dataArray.length; counter++)
		{
			if (dataArray[counter] > maxVal)
			{
				maxVal = dataArray[counter];
			}
		}

		AxisLabels yAxisLabels = AxisLabelsFactory.newNumericRangeAxisLabels(0, maxVal);
		yAxisLabels.setAxisStyle(axisStyle);
		chart.addYAxisLabels(yAxisLabels);

		chart.setSize(250, 110);
		chart.setBarWidth(20);
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
			this.speciesList = cs.getAllMarkedPanels("Species", investigationNames);
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
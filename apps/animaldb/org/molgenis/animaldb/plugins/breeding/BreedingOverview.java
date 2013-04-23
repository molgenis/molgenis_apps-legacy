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

import com.googlecode.charts4j.AxisLabelsFactory;
import com.googlecode.charts4j.AxisStyle;
import com.googlecode.charts4j.AxisTextAlignment;
import com.googlecode.charts4j.BarChart;
import com.googlecode.charts4j.BarChartPlot;
import com.googlecode.charts4j.Data;
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
				return "<italic>na</italic>";
			}

		}
		catch (Exception e)
		{
			speciesName = "Error when retrieving species";
		}
		return speciesName;
	}

	public String createAgeHistogram(String LineNameString) throws DatabaseException
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
			for (int i = 0; i < 9; i++)
			{

				QueryRule r4 = new QueryRule(ObservedValue.VALUE, Operator.GREATER_EQUAL,
						dateOnlyFormat.format(startDate));
				QueryRule r5 = new QueryRule(ObservedValue.VALUE, Operator.LESS, dateOnlyFormat.format(endDate));

				System.out.println("\n####### " + " " + startDate.getTime() + " startdate: "
						+ dateOnlyFormat.format(startDate));
				System.out.println("\n####### " + endDate.getTime() + " enddate: " + dateOnlyFormat.format(endDate));
				Query<ObservedValue> q2 = this.DB.query(ObservedValue.class);
				q2.addRules(r1, r2, r3, r4, r5, r6);
				histData[i] = (double) q2.find().size();
				System.out.println("\n#### " + histData[i]);

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
			return "<italic>na</italic>";
		}

		// double[] dataArray =
		// { 10, 3, 12, 30, 14, 15, 16, 55, 18, 19 };

	}

	public String createBarChart(double[] dataArray)
	{
		// EXAMPLE CODE START
		// Defining data plots.

		// double[] blaat = {8,9,10,11,12,13,14};
		// BarChartPlot test1 =
		// Plots.newBarChartPlot(Data.newData(1,2,3,4,5,6,7),ORANGERED);
		// BarChartPlot test2 =
		// Plots.newBarChartPlot(Data.newData(blaat),LIMEGREEN);
		BarChartPlot ageHist = Plots.newBarChartPlot(Data.newData(dataArray), BLACK);

		// BarChartPlot team1 = Plots.newBarChartPlot(Data.newData(25, 43, 12,
		// 30), BLUEVIOLET, "Team A");
		// BarChartPlot team2 = Plots.newBarChartPlot(Data.newData(8, 35, 11,
		// 5), ORANGERED, "Team B");
		// BarChartPlot team3 = Plots.newBarChartPlot(Data.newData(10, 20, 30,
		// 30), LIMEGREEN, "Team C");

		// Instantiating chart.
		BarChart chart = GCharts.newBarChart(ageHist);

		// Defining axis info and styles
		AxisStyle axisStyle = AxisStyle.newAxisStyle(BLACK, 13, AxisTextAlignment.CENTER);
		// AxisStyle xAxisStyle = AxisStyle.newAxisStyle(null, 0, null);
		// AxisStyle yAxisStyle = AxisStyle.newAxisStyle(null, 0, null);

		// AxisLabels freq = AxisLabelsFactory.newAxisLabels("Frequency", 50.0);
		// freq.setAxisStyle(axisStyle);
		// AxisLabels age = AxisLabelsFactory.newAxisLabels("Age (months)",
		// 50.0);
		// age.setAxisStyle(axisStyle);

		// Adding axis info to chart.

		chart.addXAxisLabels(AxisLabelsFactory
				.newAxisLabels("0", "12", "24", "36", "48", "60", "72", "84", "96", ">96"));
		chart.addYAxisLabels(AxisLabelsFactory.newNumericRangeAxisLabels(0, 100));
		// chart.addXAxisLabels(age);
		// chart.addYAxisLabels(freq);

		chart.setSize(300, 150);
		chart.setBarWidth(25);
		chart.setSpaceWithinGroupsOfBars(0);
		chart.setSpaceBetweenGroupsOfBars(0);
		chart.setDataStacked(true);
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
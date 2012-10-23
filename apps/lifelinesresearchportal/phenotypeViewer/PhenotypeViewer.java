package phenotypeViewer;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.json.JSONObject;
import org.molgenis.datatable.model.ProtocolTable;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.server.MolgenisRequest;
import org.molgenis.framework.tupletable.TableException;
import org.molgenis.framework.tupletable.TupleTable;
import org.molgenis.framework.tupletable.impl.CsvTable;
import org.molgenis.framework.tupletable.impl.MemoryTable;
import org.molgenis.framework.tupletable.view.JQGridView;
import org.molgenis.framework.tupletable.view.JQGridViewCallback;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.model.elements.Field;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Category;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.protocol.Protocol;
import org.molgenis.protocol.ProtocolApplication;
import org.molgenis.util.Entity;
import org.molgenis.util.HttpServletRequestTuple;
import org.molgenis.util.Tuple;
import org.molgenis.util.ValueLabel;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.gson.Gson;

public class PhenotypeViewer extends PluginModel<Entity> implements JQGridViewCallback
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4743753566046137438L;
	private String loadingMatrix = null;
	private String mappingMessage = null;
	private String report = null;
	private String uploadFileErrorMessage = null;
	private CsvTable csvTable = null;
	private JQGridView tableView = null;
	private String STATUS = "showMatrix";
	private String investigationName = null;
	private String tempalteFilePath = null;
	private String jsonForMapping = null;
	private List<String> listOfProtocols = new ArrayList<String>();
	// private List<String> colHeaders = new ArrayList<String>();
	private List<String> rowHeaders = new ArrayList<String>();
	private List<String> newTargets = new ArrayList<String>();
	private List<String> projects = new ArrayList<String>();
	private HashMap<String, String> hashMeasurement = new HashMap<String, String>();
	private HashMap<String, String> newFeatures = new HashMap<String, String>();

	private String projectName = "";

	private String importMessage = null;

	public PhenotypeViewer(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	public void resetVariables()
	{
		// colHeaders.clear();
		projects.clear();
		hashMeasurement.clear();
		newFeatures.clear();
		rowHeaders.clear();
		newTargets.clear();
		listOfProtocols.clear();
		jsonForMapping = null;
		loadingMatrix = null;
		uploadFileErrorMessage = null;
		mappingMessage = null;
		tempalteFilePath = null;
		jsonForMapping = null;
		importMessage = null;
		csvTable = null;
		report = null;
	}

	public Show handleRequest(Database db, Tuple request, OutputStream out) throws Exception
	{

		if (out != null)
		{

			if (request.getAction().equals("download_json_test"))
			{

				tableView.handleRequest(db, request, out);

			}
			else if (request.getAction().equals("download_json_loadPreview"))
			{

				if (csvTable != null)
				{

					// If there are no new records and columns at all, show all
					// the
					// table
					if (newTargets.size() > 0)
					{// If there are new records,
						// show new records only
						String targetString = csvTable.getAllColumns().get(0).getName();

						List<Tuple> newRecords = new ArrayList<Tuple>();

						for (Tuple tuple : csvTable.getRows())
						{
							String targetName = tuple.getString(targetString);
							if (newTargets.contains(targetName))
							{
								newRecords.add(tuple);
							}
						}

						MemoryTable table = new MemoryTable(newRecords);

						tableView = new JQGridView("test", this, table);

					}
					else
					{

						tableView = new JQGridView("test", this, csvTable);
					}

					JSONObject json = new JSONObject();
					json.put("result", tableView.getHtml());
					// ((MolgenisRequest)
					// request).getResponse().getOutputStream()
					// .print(json.toString());
					PrintWriter writer = new PrintWriter(out);
					writer.write(json.toString());
					writer.flush();
					writer.close();
				}

			}
			else if (request.getAction().equals("download_json_reloadGridByInves"))
			{
				investigationName = request.getString("investigation");
				ProtocolTable table = new ProtocolTable(investigationName);
				table.setTargetString("target");
				table.setInvestigation(investigationName);
				// add editable decorator

				JSONObject json = new JSONObject();
				// check which table to show
				json.put("result", tableChecker(db, table).getHtml());

				PrintWriter writer = new PrintWriter(out);
				writer.write(json.toString());
				writer.flush();
				writer.close();

			}
			else if (request.getAction().equals("download_json_removeMessage"))
			{
				importMessage = null;
			}
		}
		else
		{
			this.handleRequest(db, request);
		}

		return Show.SHOW_MAIN;
	}

	@Override
	public void handleRequest(Database db, Tuple request) throws Exception
	{

		if (request.getAction().equals("uploadFile"))
		{

			// If the checkbox is checked
			if (request.getBool("createNewInvest") != null)
			{
				projectName = request.getString("newInvestigation");
				createInvestigation(projectName, db);
			}
			else
			{
				projectName = request.getString("project");

			}
			investigationName = projectName;

			resetVariables();

			STATUS = "CheckFile";

			String fileName = request.getString("uploadFileName");

			File tmpDir = new File(System.getProperty("java.io.tmpdir"));

			File templateMapping = new File(tmpDir.getAbsolutePath() + "/tempalteMapping.xls");

			tempalteFilePath = templateMapping.getAbsolutePath();

			checkHeaders(db, request, fileName);

		}
		else if (request.getAction().equals("showMatrix"))
		{

			STATUS = "showMatrix";
			try
			{

				// create table
				ProtocolTable table = new ProtocolTable(investigationName);
				table.setTargetString("target");
				table.setInvestigation(investigationName);
				// add editable decorator
				tableChecker(db, table);

			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		else if (request.getAction().equals("previewFileAction"))
		{

			STATUS = "previewFile";

			if (csvTable != null)
			{

				// If there are no new records and columns at all, show all the
				// table
				if (newTargets.size() > 0)
				{// If there are new records,
					// show new records only
					String targetString = csvTable.getAllColumns().get(0).getName();

					List<Tuple> newRecords = new ArrayList<Tuple>();

					for (Tuple tuple : csvTable.getRows())
					{
						String targetName = tuple.getString(targetString);
						if (newTargets.contains(targetName))
						{
							newRecords.add(tuple);
						}
					}

					MemoryTable table = new MemoryTable(newRecords);

					tableView = new JQGridView("test", this, table);

				}
				else
				{

					tableView = new JQGridView("test", this, csvTable);
				}
			}
		}
		else if (request.getAction().equals("previousStepSummary"))
		{

			STATUS = "CheckFile";

		}
		else if (request.getAction().equals("importUploadFile"))
		{

			STATUS = "showMatrix";

			importUploadFile(db, request);

			ProtocolTable table = new ProtocolTable(investigationName);

			table.setTargetString(table.getTargetString());
			// add editable decorator

			// check which table to show
			tableChecker(db, table);

		}
		else if (request.getAction().equals("uploadMapping"))
		{

			try
			{
				mappingMessage = null;

				String mappingFileName = request.getString("mappingForColumns");

				CsvTable mappingTable = new CsvTable(new File(mappingFileName));

				mappingClass allMappings = new mappingClass();

				for (Tuple tuple : mappingTable.getRows())
				{

					String variableName = tuple.getString("variable");
					String dataType = tuple.getString("datatype");
					String category = tuple.getString("category");
					String code = tuple.getString("code");
					String table = tuple.getString("table");
					allMappings.addMapping(variableName, dataType, table, category, code);

				}

				jsonForMapping = new Gson().toJson(allMappings.getMapping());

			}
			catch (Exception e)
			{
				mappingMessage = "There are errors in your mapping file, please check your mapping file!";
				e.printStackTrace();
			}

		}
		else if (request.getAction().equals("downloadTemplate"))
		{

			WorkbookSettings ws = new WorkbookSettings();

			ws.setLocale(new Locale("en", "EN"));

			File tmpDir = new File(System.getProperty("java.io.tmpdir"));

			File mappingResult = new File(tmpDir + File.separator + "template.xls");

			WritableWorkbook workbook = Workbook.createWorkbook(mappingResult, ws);

			WritableSheet outputExcel = workbook.createSheet("Sheet1", 0);

			outputExcel.addCell(new Label(0, 0, "variable"));

			outputExcel.addCell(new Label(1, 0, "datatype"));

			outputExcel.addCell(new Label(2, 0, "category"));

			outputExcel.addCell(new Label(3, 0, "code"));

			outputExcel.addCell(new Label(4, 0, "table"));

			workbook.write();

			workbook.close();

			HttpServletRequestTuple rt = (HttpServletRequestTuple) request;

			HttpServletResponse httpResponse = rt.getResponse();

			String redirectURL = "tmpfile/template.xls";

			httpResponse.sendRedirect(redirectURL);

		}
		else if (request.getAction().equals("showNewRecordsOnly"))
		{

			STATUS = "previewFile";

			String targetString = csvTable.getAllColumns().get(0).getName();

			List<Tuple> newRecords = new ArrayList<Tuple>();

			for (Tuple tuple : csvTable.getRows())
			{
				String targetName = tuple.getString(targetString);
				if (newTargets.contains(targetName))
				{
					newRecords.add(tuple);
				}
			}

			MemoryTable table = new MemoryTable(newRecords);

			tableView = new JQGridView("test", this, table);

		}
		else
		{
			tableView.handleRequest(db, request, null);
			loadingMatrix = "Loading the matrix";
		}
	}

	/**
	 * Create investigation if it doesn't exist in the database
	 * 
	 * @param investigationName
	 * @param db
	 * @throws DatabaseException
	 */
	private void createInvestigation(String investigationName, Database db) throws DatabaseException
	{
		if (investigationName == null || investigationName.isEmpty()) return;

		QueryRule queryRule = new QueryRule(Investigation.NAME, Operator.EQUALS, investigationName);
		List<Investigation> investigationList = db.find(Investigation.class, queryRule);
		if (investigationList == null || investigationList.isEmpty())
		{
			Investigation inv = new Investigation();
			inv.setName(investigationName);
			db.add(inv);
		}

	}

	private void importUploadFile(Database db, Tuple request) throws DatabaseException, TableException
	{

		try
		{
			db.beginTx();

			List<Individual> listOfTargets = new ArrayList<Individual>();
			List<Measurement> listOfFeatures = new ArrayList<Measurement>();
			List<ObservedValue> listOfValues = new ArrayList<ObservedValue>();
			List<ProtocolApplication> listOfPA = new ArrayList<ProtocolApplication>();
			HashMap<String, Category> listOfCategories = new HashMap<String, Category>();
			HashMap<String, List<String>> featureToProtocolTable = new HashMap<String, List<String>>();

			List<Field> allColumns = csvTable.getAllColumns();

			List<String> addedColumns = new ArrayList<String>();

			String targetString = allColumns.get(0).getName();

			String existingColumn = null;
			// for (String eachHeader :colHeaders))
			for (String eachHeader : hashMeasurement.keySet())
			{
				if (!newFeatures.containsKey(eachHeader))
				{
					existingColumn = eachHeader;
					break;
				}
			}

			HashMap<String, String> targetToProtocolApplication = new HashMap<String, String>();

			if (rowHeaders.size() > 0 && existingColumn != null)
			{

				Query<ObservedValue> query = db.query(ObservedValue.class);

				query.addRules(new QueryRule(ObservedValue.TARGET_NAME, Operator.IN, rowHeaders));
				query.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, existingColumn));
				List<ObservedValue> listOfExistingValues = query.find();

				if (listOfExistingValues.size() > 0)
				{
					for (ObservedValue ov : query.find())
					{
						targetToProtocolApplication.put(ov.getTarget_Name(), ov.getProtocolApplication_Name());
					}
				}

				if (rowHeaders.size() != listOfExistingValues.size())
				{

					for (String oldTarget : rowHeaders)
					{
						if (!targetToProtocolApplication.containsKey(oldTarget))
						{
							ProtocolApplication pa = new ProtocolApplication();
							pa.setName("pa" + oldTarget);
							pa.setInvestigation_Name(investigationName);
							pa.setProtocol_Name(investigationName);
							targetToProtocolApplication.put(oldTarget, pa.getName());
							listOfPA.add(pa);
						}
					}
				}
			}

			// new rows are added
			if (newTargets.size() > 0)
			{

				for (String targetName : newTargets)
				{
					Individual inv = new Individual();
					inv.setName(targetName);
					inv.setInvestigation_Name(investigationName);
					listOfTargets.add(inv);
				}
			}

			List<String> ingoredColumn = new ArrayList<String>();

			// new columns are added.
			if (newFeatures.size() > 0)
			{

				for (String feature : newFeatures.keySet())
				{
					int length = investigationName.length();

					String feat = feature.substring(0, (feature.length() - length - 1));
					String identifier = feat.replaceAll(" ", "_");
					// Check if there the checkboxes are checked (if the new
					// measurement should be added)
					if (request.getBool(identifier + "_check") != null)
					{

						Measurement m = new Measurement();

						String dataType = request.getString(identifier + "_dataType");

						m.setName(feature);
						m.setLabel(newFeatures.get(feature));
						m.setInvestigation_Name(investigationName);
						m.setDataType(dataType);

						String protocolTable = request.getString(identifier + "_protocolTable");

						// Put the new features in the corresponding protocols
						if (!featureToProtocolTable.containsKey(protocolTable))
						{

							List<String> features = new ArrayList<String>();
							features.add(feature);
							featureToProtocolTable.put(protocolTable, features);

						}
						else
						{
							List<String> features = featureToProtocolTable.get(protocolTable);
							if (!features.contains(feature))
							{
								features.add(feature);
								featureToProtocolTable.put(protocolTable, features);
							}
						}

						if (dataType.equals("categorical"))
						{

							List<String> categoryNameClean = new ArrayList<String>();

							for (String eachCategory : request.getStringList(identifier + "_categoryString"))
							{

								String standardName = eachCategory.replaceAll("[^(a-zA-Z0-9_\\s)]", " ").trim();

								if (!listOfCategories.containsKey(standardName.toLowerCase()))
								{

									String code = eachCategory.split("=")[0].trim();
									String codeLabel = eachCategory.split("=")[1].trim();
									Category c = new Category();
									c.setCode_String(code);
									c.setDescription(codeLabel);
									c.setName(standardName);
									listOfCategories.put(standardName.toLowerCase(), c);
								}
								categoryNameClean.add(standardName);
							}
							m.setCategories_Name(categoryNameClean);
						}

						listOfFeatures.add(m);

						addedColumns.add(feat);
					}
					else
					{

						ingoredColumn.add(feature);
					}
				}
			}

			for (Tuple row : csvTable.getRows())
			{

				String targetName = row.getString(targetString);

				// Add values for new records only
				if (newTargets.contains(targetName))
				{

					ProtocolApplication pa = new ProtocolApplication();
					// FIXME: the hard coded protocol name would be fixed
					pa.setProtocol_Name(investigationName);

					pa.setName("pa_" + row.getString(targetString));
					pa.setInvestigation_Name(investigationName);
					listOfPA.add(pa);

					for (Field field : allColumns)
					{

						String eachColumn = field.getName();

						if (!eachColumn.equals(targetString)
								&& !ingoredColumn.contains(eachColumn + "_" + investigationName))
						{
							ObservedValue ov = new ObservedValue();
							ov.setTarget_Name(row.getString(targetString));
							ov.setFeature_Name(eachColumn + "_" + investigationName);
							ov.setValue(row.getString(eachColumn));
							ov.setInvestigation_Name(investigationName);
							ov.setProtocolApplication_Name(pa.getName());
							listOfValues.add(ov);
						}
					}
				}
				else
				{

					// Add values for new columns only
					if (addedColumns.size() > 0)
					{

						for (String eachNewColumn : addedColumns)
						{
							ObservedValue ov = new ObservedValue();
							ov.setTarget_Name(row.getString(targetString));
							ov.setFeature_Name(eachNewColumn + "_" + investigationName);
							ov.setValue(row.getString(eachNewColumn));
							ov.setInvestigation_Name(investigationName);
							ov.setProtocolApplication_Name(targetToProtocolApplication.get(ov.getTarget_Name()));
							listOfValues.add(ov);
						}
					}
				}
			}

			// Dependency, have to add the categories to database first before
			// adding the measurements
			if (listOfCategories.keySet().size() > 0)
			{
				for (Category c : db.find(Category.class, new QueryRule(Category.NAME, Operator.IN,
						new ArrayList<String>(listOfCategories.keySet()))))
				{
					listOfCategories.remove(c.getName().toLowerCase());
				}
			}

			List<Category> uniqueCategories = new ArrayList<Category>(listOfCategories.values());

			db.add(listOfTargets);
			db.add(uniqueCategories);

			for (Measurement m : listOfFeatures)
			{

				if (m.getCategories_Name().size() > 0)
				{

					List<Integer> listOfCategoryID = new ArrayList<Integer>();

					for (Category c : db.find(Category.class,
							new QueryRule(Category.NAME, Operator.IN, m.getCategories_Name())))
					{
						listOfCategoryID.add(c.getId());
					}
					m.setCategories_Id(listOfCategoryID);
				}
			}

			db.add(listOfFeatures);

			if (db.find(Protocol.class, new QueryRule(Protocol.NAME, Operator.EQUALS, investigationName)).size() == 0)
			{
				Protocol protocolInvest = new Protocol();
				protocolInvest.setName(investigationName);
				protocolInvest.setInvestigation_Name(investigationName);

				List<Integer> measurementIDs = new ArrayList<Integer>();

				for (Measurement m : db.find(Measurement.class, new QueryRule(Measurement.INVESTIGATION_NAME,
						Operator.EQUALS, investigationName)))
				{
					measurementIDs.add(m.getId());
				}

				protocolInvest.setFeatures_Id(measurementIDs);
				db.add(protocolInvest);
			}

			db.add(listOfPA);
			db.add(listOfValues);

			if (featureToProtocolTable.size() > 0)
			{
				// Add the features to the catalogue node
				for (Protocol p : db.find(Protocol.class, new QueryRule(Protocol.NAME, Operator.IN,
						new ArrayList<String>(featureToProtocolTable.keySet()))))
				{
					List<Integer> oldFeatures = p.getFeatures_Id();
					for (Measurement m : listOfFeatures)
					{
						oldFeatures.add(m.getId());
					}
					p.setFeatures_Id(oldFeatures);
					db.update(p);
				}
			}
			db.commitTx();

			importMessage = "success";

		}
		catch (DatabaseException e)
		{

			db.rollbackTx();
			importMessage = "It fails to import the file, please check your file please!</br>"
					+ e.getMessage().toString();
			e.printStackTrace();
		}
	}

	@Override
	public void reload(Database db)
	{

		try
		{

			projects.clear();
			List<Investigation> listProjects = new ArrayList<Investigation>();
			listProjects = db.find(Investigation.class);

			int index = 0;

			for (Investigation i : listProjects)
			{
				projects.add(i.getName());
				if (index == 0 && investigationName == null)
				{
					investigationName = i.getName();
					index++;
				}
			}

			if (db.find(Investigation.class).size() > 0)
			{
				// p = db.query(Protocol.class).eq(Protocol.NAME,
				// "stageCatalogue").find().get(0);
				if (db.find(Protocol.class).size() > 0)
				{
					// create table
					ProtocolTable table = new ProtocolTable(investigationName);

					table.setTargetString(table.getTargetString());
					// add editable decorator

					// check which table to show
					tableChecker(db, table);

				}
			}

		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public JQGridView tableChecker(Database db, ProtocolTable table)
	{
		tableView = new JQGridView("test", this, table);
		String selectedInv = "";
		String selectInvestigationHTML = "<select id=\"selectInvestigation\" class=\"ui-widget-content ui-corner-all\" onChange=\"updateInvestigation()\">";

		for (String inv : projects)
		{
			if (inv.equals(investigationName))
			{
				selectedInv = inv;
				selectInvestigationHTML += "<option selected=\"selected\">" + selectedInv + "</option>";
			}
			else
			{

				selectInvestigationHTML += "<option>" + inv + "</option>";
			}
		}

		selectInvestigationHTML += "</select>";
		tableView.setLabel("Select project: " + selectInvestigationHTML);
		return tableView;

	}

	public void checkHeaders(Database db, Tuple request, String filePath)
	{

		File file = new File(filePath);

		try
		{

			csvTable = new CsvTable(file);

			// TODO: User should decide which column is the target
			String targetString = csvTable.getAllColumns().get(0).getName();

			// check the existing variables in measurements
			// EXTRA: added the projectname to the measurementLABEL
			for (Field field : csvTable.getAllColumns())
			{

				if (!field.getName().equals(targetString))
				{
					hashMeasurement.put(field.getName() + "_" + projectName, field.getName());
					newFeatures.put(field.getName() + "_" + projectName, field.getName());
				}
			}

			// LABEL!!
			// We query these entire list of columns and see whether they
			// already existed in the database
			List<Measurement> colHeadersMeasurement = db.find(Measurement.class, new QueryRule(Measurement.NAME,
					Operator.IN, new ArrayList<String>(hashMeasurement.keySet())));

			// we remove the existing features from the new feature hashmap.
			for (Measurement m : colHeadersMeasurement)
			{
				if (newFeatures.containsKey(m.getName()))
				{
					newFeatures.remove(m.getName());
				}

			}

			// check the existing records
			for (Tuple tuple : csvTable.getRows())
			{
				rowHeaders.add(tuple.getString(targetString));
				newTargets.add(tuple.getString(targetString));
			}

			List<ObservationTarget> rowHeadersTarget = db.find(ObservationTarget.class, new QueryRule(
					ObservationTarget.NAME, Operator.IN, rowHeaders));

			for (ObservationTarget ot : rowHeadersTarget)
			{
				if (rowHeaders.contains(ot.getName()))
				{
					newTargets.remove(ot.getName());
				}
			}

			for (String existingFeature : newFeatures.keySet())
			{

				hashMeasurement.remove(existingFeature);

			}

			rowHeaders.removeAll(newTargets);

			report = new Gson().toJson(ReportUploadStatus.createReportUploadStatus(hashMeasurement, newFeatures,
					rowHeaders, newTargets));

			for (Protocol p : db.find(Protocol.class, new QueryRule(Protocol.INVESTIGATION_NAME, Operator.EQUALS,
					investigationName)))
			{
				listOfProtocols.add(p.getName());
			}

		}
		catch (Exception e)
		{
			STATUS = "showMatrix";
			uploadFileErrorMessage = "There are errors in your file, please upload before check";
			e.printStackTrace();
		}

	}

	@Override
	// from JQGridViewCallback
	public void beforeLoadConfig(MolgenisRequest request, TupleTable tupleTable)
	{
		HttpSession session = request.getRequest().getSession();
		@SuppressWarnings("unchecked")
		List<Measurement> selectedMeasurements = (List<Measurement>) session.getAttribute("selectedMeasurements");

		if (selectedMeasurements != null)
		{
			try
			{
				for (final Field field : tupleTable.getAllColumns())
				{
					Measurement measurement = Iterables.find(selectedMeasurements, new Predicate<Measurement>()
					{
						@Override
						public boolean apply(Measurement m)
						{
							return m.getName().equals(field.getName() + "_" + investigationName);
						}

					}, null);

					if (measurement == null)
					{
						tupleTable.hideColumn(field.getName());
					}
					else
					{
						tupleTable.showColumn(field.getName());
					}
				}

				session.removeAttribute("selectedMeasurements");
			}
			catch (TableException e)
			{
				e.printStackTrace();
			}

		}
	}

	public class mappingClass
	{

		HashMap<String, eachMapping> allMappings = null;

		public mappingClass()
		{
			allMappings = new HashMap<String, eachMapping>();
		}

		public void addMapping(String variableName, String dataType, String table, String category, String code)
		{

			if (allMappings.containsKey(variableName))
			{
				allMappings.get(variableName).addCategory(category, code);
			}
			else
			{
				if (variableName != null)
				{
					eachMapping newMapping = new eachMapping(variableName, dataType, table, category, code);
					allMappings.put(variableName, newMapping);
				}
			}
		}

		public int getSize()
		{
			return allMappings.size();
		}

		public List<eachMapping> getMapping()
		{
			return new ArrayList<eachMapping>(allMappings.values());
		}

		private class eachMapping
		{

			private String variableName;
			private String dataType;
			private String table;
			private Map<String, String> listOfCategories;

			private eachMapping(String variableName, String dataType, String table, String category, String code)
			{

				this.variableName = variableName;
				this.dataType = dataType;
				this.table = table;
				this.listOfCategories = new LinkedHashMap<String, String>();
				this.listOfCategories.put(code, category);
			}

			private void addCategory(String category, String code)
			{

				this.listOfCategories.put(code, category);
			}
		}
	}

	public static class ReportUploadStatus
	{

		boolean success = true;
		List<String> colHeaders;
		List<String> newFeatures;
		List<String> rowHeaders;
		List<String> newTargets;

		public static ReportUploadStatus createReportUploadStatus(HashMap<String, String> hashMeasurements,
				HashMap<String, String> newFeatures, List<String> rowHeaders, List<String> newTargets)
		{
			ReportUploadStatus instance = new ReportUploadStatus();
			instance.colHeaders = new ArrayList<String>(hashMeasurements.values());
			instance.newFeatures = new ArrayList<String>(newFeatures.values());
			instance.rowHeaders = rowHeaders;
			instance.newTargets = newTargets;
			return instance;
		}
	}

	public String getReport()
	{
		return report;
	}

	public String getSTATUS()
	{
		return STATUS;
	}

	public String getTableView()
	{
		if (tableView != null)
		{
			return tableView.getHtml();
		}
		return "";
	}

	public List<String> getFeatureDataTypes() throws Exception
	{

		List<String> dataTypes = new ArrayList<String>();
		for (ValueLabel label : Measurement.class.newInstance().getDataTypeOptions())
		{
			dataTypes.add(label.getLabel());
		}

		return dataTypes;
	}

	public List<String> getProtocolTables() throws Exception
	{

		return listOfProtocols;
	}

	public String getTempalteFilePath()
	{
		return tempalteFilePath;
	}

	public String getUrl()
	{
		return "molgenis.do?__target=" + this.getName();
	}

	public String getJsonForMapping()
	{
		return jsonForMapping;
	}

	public String getUploadFileErrorMessage()
	{
		return uploadFileErrorMessage;
	}

	public String getMappingMessage()
	{
		return mappingMessage;
	}

	public String getImportMessage()
	{
		return importMessage;
	}

	public String getLoadingMatrix()
	{
		return loadingMatrix;
	}

	public List<String> getProjects()
	{
		return projects;
	}

	@Override
	public String getViewTemplate()
	{
		// TODO Auto-generated method stub
		return "phenotypeViewer/PhenotypeViewer.ftl";
	}

	@Override
	public String getViewName()
	{
		// TODO Auto-generated method stub
		return "PhenotypeViewer";
	}
}
/* Date:        December 3, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 */

package plugins.harmonization;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.json.JSONObject;
import org.molgenis.compute.ComputeProtocol;
import org.molgenis.core.OntologyTerm;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.Database.DatabaseAction;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Category;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

import plugins.HarmonizationComponent.OWLFunction;

//import plugins.autohidelogin.AutoHideLoginModel; 

public class Harmonization extends PluginModel<Entity>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4255876428416189905L;
	private List<String> listOfPredictionModels = new ArrayList<String>();
	private List<String> listOfCohortStudies = new ArrayList<String>();
	private List<String> reservedInv = new ArrayList<String>();

	public Harmonization(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "plugins_harmonization_Harmonization";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/harmonization/Harmonization.ftl";
	}

	@Override
	public Show handleRequest(Database db, Tuple request, OutputStream out) throws Exception
	{

		if (out == null)
		{
			this.handleRequest(db, request);
		}
		else
		{
			JSONObject status = new JSONObject();

			try
			{
				db.beginTx();
				// Handle request that adds a new prediction model to db
				if ("download_json_newPredictionModel".equals(request.getAction()))
				{
					String nameOfModel = request.getString("name").trim();
					ComputeProtocol cp = new ComputeProtocol();
					cp.setName(nameOfModel);
					cp.setInvestigation_Name("Prediction Model");
					cp.setScriptTemplate("Not provided");
					db.add(cp);
					status.put("message",
							"You successfully added a new prediction model</br>Please define the predictors");
					status.put("success", true);

				}
				else if ("download_json_removePredictionModel".equals(request.getAction()))
				{
					String nameOfModel = request.getString("name").trim();

					ComputeProtocol cp = db.find(ComputeProtocol.class,
							new QueryRule(ComputeProtocol.NAME, Operator.EQUALS, nameOfModel)).get(0);

					if (cp.getFeatures_Name().size() > 0)
					{
						for (Measurement m : db.find(Measurement.class,
								new QueryRule(Measurement.NAME, Operator.IN, cp.getFeatures_Name())))
						{
							this.removePredictor(m.getLabel(), nameOfModel, db);
						}
					}

					db.remove(cp);

					status.put("message", "You successfully removed a prediction model from the database!");

					status.put("success", true);

				}
				else if ("download_json_addPredictor".equals(request.getAction()))
				{
					JSONObject data = new JSONObject(request.getString("data"));

					Measurement m = new Measurement();

					String predictionModelName = data.getString("selected").trim();
					String unitName = data.getString("unit").trim();
					String categories = data.getString("category").trim();
					String buildingBlockString = data.getString("buildingBlocks").trim();

					m.setName(data.getString("name").trim() + "_" + predictionModelName);
					m.setLabel(data.getString("name").trim());
					m.setDescription(data.getString("description"));
					m.setDataType(data.getString("dataType").trim());
					m.setUnit_Name(unitName);
					m.setInvestigation_Name("Prediction Model");

					// Handle unit since it has to be an ontology term
					if (!unitName.equals("")
							&& db.find(OntologyTerm.class, new QueryRule(OntologyTerm.NAME, Operator.EQUALS, unitName))
									.size() == 0)
					{
						OntologyTerm ot = new OntologyTerm();
						ot.setName(unitName);
						db.add(ot);
					}

					List<String> categoryRefs = new ArrayList<String>();

					// Handle the categories
					if (!categories.equals(""))
					{
						String categoryElements[] = categories.split(",");

						List<Category> newCategories = new ArrayList<Category>();

						for (String eachCategory : removeDuplicate(categoryElements))
						{
							String uniqueName = eachCategory.replaceAll("[^(a-zA-Z0-9_\\s)]", " ").trim();

							String codeAndString[] = eachCategory.split("=");

							Category c = new Category();

							c.setName(uniqueName + "_" + predictionModelName);
							c.setCode_String(codeAndString[0].trim());
							c.setDescription(codeAndString[1].trim());
							c.setInvestigation_Name("Prediction Model");
							newCategories.add(c);

							categoryRefs.add(c.getName());
						}

						db.update(newCategories, DatabaseAction.ADD_IGNORE_EXISTING, Category.NAME,
								Category.INVESTIGATION_NAME);
					}

					m.setCategories_Name(categoryRefs);

					db.add(m);

					ComputeProtocol cp = db.find(ComputeProtocol.class,
							new QueryRule(ComputeProtocol.NAME, Operator.EQUALS, predictionModelName)).get(0);

					List<Integer> listOfPredictors = cp.getFeatures_Id();
					listOfPredictors.add(m.getId());
					cp.setFeatures_Id(listOfPredictors);
					db.update(cp);

					// Deal with building blocks
					if (db.find(Measurement.class, new QueryRule(Measurement.NAME, Operator.EQUALS, "BuildingBlocks"))
							.size() == 0)
					{
						Measurement buildingBlock = new Measurement();
						buildingBlock.setName("BuildingBlocks");
						buildingBlock.setLabel("Building blocks");
						buildingBlock.setInvestigation_Name("Prediction Model");
						db.add(buildingBlock);
					}

					if (buildingBlockString != null && !buildingBlockString.equals(""))
					{
						ObservedValue ov = new ObservedValue();
						ov.setTarget_Name(m.getName());
						ov.setFeature_Name("BuildingBlocks");
						ov.setValue(buildingBlockString);
						ov.setInvestigation_Name("Prediction Model");
						db.add(ov);
					}

					status.put("message", "You successfully added a new predictor!");

					status.put("success", true);

				}
				else if ("download_json_removePredictors".equals(request.getAction()))
				{
					String predictor = request.getString("name");

					String predictionModel = request.getString("predictionModel");

					this.removePredictor(predictor, predictionModel, db);

					status.put("message", "You successfully deleted the predictor: " + predictor);

					status.put("success", true);
				}
				else if ("download_json_showPredictors".equals(request.getAction()))
				{
					String predictionModel = request.getString("name");

					ComputeProtocol cp = db.find(ComputeProtocol.class,
							new QueryRule(ComputeProtocol.NAME, Operator.EQUALS, predictionModel)).get(0);

					if (cp.getFeatures_Name().size() > 0)
					{
						for (Measurement eachPredictor : db.find(Measurement.class, new QueryRule(Measurement.NAME,
								Operator.IN, cp.getFeatures_Name())))
						{
							JSONObject jsonForPredictor = new JSONObject();
							jsonForPredictor.put("name", eachPredictor.getLabel());
							jsonForPredictor.put("identifier", eachPredictor.getLabel().replaceAll(" ", "_"));
							jsonForPredictor.put("description", (eachPredictor.getDescription() == null ? ""
									: eachPredictor.getDescription()));
							jsonForPredictor.put("dataType", eachPredictor.getDataType());
							jsonForPredictor.put("unit",
									(eachPredictor.getUnit_Name() == null ? "" : eachPredictor.getUnit_Name()));

							String categories = "";

							if (eachPredictor.getCategories_Name().size() > 0)
							{
								for (Category c : db.find(Category.class, new QueryRule(Category.NAME, Operator.IN,
										eachPredictor.getCategories_Name())))
								{
									categories += c.getCode_String() + "=" + c.getDescription() + ",";
								}
								categories = categories.substring(0, categories.length() - 1);
							}
							jsonForPredictor.put("category", categories);

							status.put(eachPredictor.getName(), jsonForPredictor);
						}

						Query<ObservedValue> query = db.query(ObservedValue.class);
						query.addRules(new QueryRule(ObservedValue.TARGET_NAME, Operator.IN, cp.getFeatures_Name()));
						query.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "BuildingBlocks"));

						// Count how many variables have defined buildingBlocks
						int definedBlocks = 0;
						for (ObservedValue ov : query.find())
						{
							if (status.has(ov.getTarget_Name()))
							{
								definedBlocks++;
								JSONObject json = (JSONObject) status.get(ov.getTarget_Name());
								json.put("buildingBlocks", ov.getValue());
								status.put(ov.getTarget_Name(), json);
							}
						}
						status.put("buildingBlocksDefined", definedBlocks);

					}
					// Meta-data for the summary
					status.put("selected", predictionModel);
					status.put("numberOfPredictors", cp.getFeatures_Name().size());
					status.put("formula", cp.getScriptTemplate());

				}
				else if ("download_json_defineFormula".equals(request.getAction()))
				{
					JSONObject data = new JSONObject(request.getString("data"));

					ComputeProtocol cp = db.find(ComputeProtocol.class,
							new QueryRule(ComputeProtocol.NAME, Operator.EQUALS, data.getString("selected"))).get(0);

					if (cp.getScriptTemplate().equals(data.getString("formula").trim())
							|| cp.getScriptTemplate().equals(""))
					{
						status.put("message", "The formula has not changed!");
						status.put("success", false);
					}
					else
					{
						cp.setScriptTemplate(data.getString("formula"));
						db.update(cp);
						status.put("message", "You successfully updated the formula in database!");
						status.put("success", true);
					}
				}
				else if ("download_json_validateStudy".equals(request.getAction()))
				{
					String predictionModel = request.getString("predictionModel");

					String validationStudy = request.getString("validationStudy");

					ComputeProtocol cp = db.find(ComputeProtocol.class,
							new QueryRule(ComputeProtocol.NAME, Operator.EQUALS, predictionModel)).get(0);

					if (cp.getFeatures_Name().size() > 0)
					{
						HashMap<String, PredictorInfo> predictors = new HashMap<String, PredictorInfo>();

						for (Measurement m : db.find(Measurement.class,
								new QueryRule(Measurement.NAME, Operator.IN, cp.getFeatures_Name())))
						{
							PredictorInfo predictor = new PredictorInfo(m.getName());

							predictor.setLabel(m.getLabel());

							HashMap<String, String> categories = new HashMap<String, String>();

							for (Category c : db.find(Category.class,
									new QueryRule(Category.NAME, Operator.EQUALS, m.getCategories_Name())))
							{
								categories.put(c.getCode_String(), c.getDescription());
							}

							predictor.setCategory(categories);

							predictors.put(m.getName(), predictor);
						}

						Query<ObservedValue> query = db.query(ObservedValue.class);

						query.addRules(new QueryRule(ObservedValue.TARGET_NAME, Operator.IN, cp.getFeatures_Name()));

						query.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "BuildingBlocks"));

						for (ObservedValue ov : query.find())
						{
							String targetName = ov.getTarget_Name();

							String value = ov.getValue();

							predictors.get(targetName).setBuildingBlocks(value.split(","));
						}

						// TODO ontocat dynamically searching ontology terms
						String ontologyFileName = "/Users/pc_iverson/Desktop/Input/PredictionModel.owl";

						OWLFunction owlFunction = new OWLFunction(ontologyFileName);

						for (Entry<String, PredictorInfo> entry : predictors.entrySet())
						{
							PredictorInfo predictor = entry.getValue();

							createExpandQuery(predictor.getBuildingBlocks(), owlFunction);
						}
					}

				}

				db.commitTx();
			}
			catch (Exception e)
			{
				status.put("message", e.getMessage());
				status.put("success", false);
				db.rollbackTx();
			}

			PrintWriter writer = new PrintWriter(out);
			writer.write(status.toString());
			writer.flush();
			writer.close();
		}

		return Show.SHOW_MAIN;
	}

	@Override
	public void handleRequest(Database db, Tuple request) throws Exception
	{

	}

	@Override
	public void reload(Database db)
	{
		try
		{
			// clear the old variable content
			listOfPredictionModels.clear();
			listOfCohortStudies.clear();

			if (reservedInv.size() == 0)
			{
				reservedInv.add("catalogueCohortStudy");
				reservedInv.add("cataloguePredictionModel");
				reservedInv.add("Prediction Model");
			}

			for (ComputeProtocol cp : db.find(ComputeProtocol.class, new QueryRule(ComputeProtocol.INVESTIGATION_NAME,
					Operator.EQUALS, "Prediction Model")))
			{
				listOfPredictionModels.add(cp.getName());
			}

			for (Investigation inv : db.find(Investigation.class))
			{
				if (!reservedInv.contains(inv.getName()))
				{
					listOfCohortStudies.add(inv.getName());
				}
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void createExpandQuery(List<String> buildingBlocks, OWLFunction owlFunction)
	{

	}

	public void removePredictor(String predictor, String predictionModel, Database db) throws DatabaseException
	{

		Measurement m = db.find(Measurement.class,
				new QueryRule(Measurement.NAME, Operator.EQUALS, predictor + "_" + predictionModel)).get(0);

		ComputeProtocol cp = db.find(ComputeProtocol.class,
				new QueryRule(ComputeProtocol.NAME, Operator.EQUALS, predictionModel)).get(0);

		cp.getFeatures_Id().remove(m.getId());
		cp.getFeatures_Name().remove(m.getName());

		db.update(cp);

		Query<ObservedValue> query = db.query(ObservedValue.class);

		query.addRules(new QueryRule(ObservedValue.TARGET_NAME, Operator.EQUALS, m.getName()));
		query.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "BuildingBlocks"));

		if (query.find().size() > 0)
		{
			db.remove(query.find());
		}

		String unit = m.getUnit_Name();

		List<String> categories = m.getCategories_Name();

		db.remove(m);

		// Check if unit is used by other measurements
		if (unit != null && !unit.equals(""))
		{
			if (db.find(Measurement.class, new QueryRule(Measurement.UNIT_NAME, Operator.EQUALS, unit)).size() == 0)
			{
				OntologyTerm ot = db.find(OntologyTerm.class, new QueryRule(OntologyTerm.NAME, Operator.EQUALS, unit))
						.get(0);
				db.remove(ot);
			}
		}
		// Check if categories are used by other measurements
		if (categories.size() > 0)
		{
			for (String category : categories)
			{
				if (db.find(Measurement.class, new QueryRule(Measurement.CATEGORIES_NAME, Operator.EQUALS, category))
						.size() == 0)
				{
					Category c = db.find(Category.class, new QueryRule(Category.NAME, Operator.EQUALS, category))
							.get(0);
					db.remove(c);
				}
			}
		}
	}

	public List<String> removeDuplicate(String... elements)
	{
		List<String> uniqueList = new ArrayList<String>();

		for (String eachElement : elements)
		{

			if (!uniqueList.contains(eachElement.trim()))
			{
				uniqueList.add(eachElement.trim());
			}
		}

		return uniqueList;
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

	public List<String> getListOfPredictionModels()
	{
		return listOfPredictionModels;
	}

	public String getUrl()
	{
		return "molgenis.do?__target=" + this.getName();
	}

	public List<String> getListOfCohortStudies()
	{
		return listOfCohortStudies;
	}
}
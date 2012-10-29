/* Date:        December 3, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 */

package plugins.harmonization;

import gcc.catalogue.MappingMeasurement;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
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
import org.molgenis.protocol.Protocol;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

import plugins.HarmonizationComponent.LevenshteinDistanceModel;
import plugins.HarmonizationComponent.MappingList;
import plugins.HarmonizationComponent.OWLFunction;
import plugins.catalogueTreeNewVersion.catalogueTreeComponent;

//import plugins.autohidelogin.AutoHideLoginModel; 

public class Harmonization extends PluginModel<Entity>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4255876428416189905L;

	private int loadingProcess = 0;
	private OWLFunction owlFunction = null;
	private catalogueTreeComponent catalogue = null;
	private List<Measurement> measurementsInStudy = null;
	private List<String> listOfPredictionModels = new ArrayList<String>();
	private List<String> listOfCohortStudies = new ArrayList<String>();
	private List<String> reservedInv = new ArrayList<String>();
	private LevenshteinDistanceModel model = new LevenshteinDistanceModel();
	private HashMap<String, PredictorInfo> predictors = new HashMap<String, PredictorInfo>();

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

					this.loadingProcess = 0;
					this.predictors.clear();

					status.put("validationStudy", validationStudy);
					status.put("predictionModel", predictionModel);

					ComputeProtocol cp = db.find(ComputeProtocol.class,
							new QueryRule(ComputeProtocol.NAME, Operator.EQUALS, predictionModel)).get(0);

					if (cp.getFeatures_Name().size() > 0)
					{
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

							predictors.put(m.getName().replaceAll(" ", "_"), predictor);
						}

						Query<ObservedValue> query = db.query(ObservedValue.class);

						query.addRules(new QueryRule(ObservedValue.TARGET_NAME, Operator.IN, cp.getFeatures_Name()));

						query.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "BuildingBlocks"));

						for (ObservedValue ov : query.find())
						{
							String targetName = ov.getTarget_Name();

							String value = ov.getValue();

							predictors.get(targetName.replaceAll(" ", "_")).setBuildingBlocks(value.split(";"));
						}

						for (MappingMeasurement mapping : db.find(MappingMeasurement.class, new QueryRule(
								MappingMeasurement.MAPPING_NAME, Operator.IN, cp.getFeatures_Name())))
						{
							predictors.get(mapping.getMapping_Name().replaceAll(" ", "_")).setFinalMappings(
									mapping.getFeature_Name());
						}

						measurementsInStudy = db.find(Measurement.class, new QueryRule(Measurement.INVESTIGATION_NAME,
								Operator.EQUALS, validationStudy));

						if (measurementsInStudy.size() > 0)
						{
							// TODO ontocat dynamically searching ontology terms
							String ontologyFileName = "/Users/pc_iverson/Desktop/Input/PredictionModel.owl";

							owlFunction = new OWLFunction(ontologyFileName);

							status.put("success", true);
						}
					}

				}
				else if ("download_json_loadMappingResult".equals(request.getAction()))
				{

					List<String> keys = new ArrayList<String>(predictors.keySet());

					String eachKey = keys.get(loadingProcess);

					loadingProcess++;

					PredictorInfo predictor = predictors.get(eachKey);

					for (String eachDefintion : predictor.getBuildingBlocks())
					{
						predictor.getExpandedQuery().addAll(createExpandQuery(eachDefintion.split(","), owlFunction));
					}

					executeMapping(predictor, measurementsInStudy);

					String mappingResult = makeMappingTable(predictor);

					String existingMapping = makeExistingMappingTable(predictor);

					status.put("label", predictor.getLabel());
					status.put("mappingResult", mappingResult);
					status.put("identifier", predictor.getName());
					status.put("existingMapping", existingMapping);
					status.put("loadingProcess", loadingProcess);
					status.put("total", predictors.size());

					if (loadingProcess == predictors.size())
					{
						String validationStudy = request.getString("validationStudy");

						catalogue = new catalogueTreeComponent(validationStudy);

						status.put("treeView", catalogue.getTreeView());
					}
				}
				else if ("download_json_retrieveExpandedQuery".equals(request.getAction()))
				{
					String predictor = request.getString("predictor");
					String matchedVariable = request.getString("matchedVariable");

					String table = "<div id=\"" + matchedVariable.replaceAll(" ", "_")
							+ "\" style=\"display:none;with:300px;height:400px;overflow:auto;\">"
							+ "<table style=\"width:100%;overflow:auto;\">"
							+ "<tr style=\"font-size:16px;\"><th>Expanded queries</th>"
							+ "<th>Matched variable</th><th>Similarity score</th></tr>";

					matchedVariable = matchedVariable.replaceAll(predictor + "_", "");

					for (String query : predictors.get(predictor).getExpandedQueryForOneMapping(matchedVariable))
					{
						table += "<tr style=\"font-size:12px;text-align:center;\"><td>" + query + "</td><td>"
								+ matchedVariable + "</td><td>" + predictors.get(predictor).getSimilarity(query)
								+ "</td></tr>";
					}
					table += "</table></div>";

					status.put("table", table);
				}
				else if ("download_json_saveMapping".equals(request.getAction()))
				{
					JSONObject mappingResult = new JSONObject(request.getString("mappingResult"));

					String validationStudyName = request.getString("validationStudy");

					String predictionModel = request.getString("predictionModel");

					Protocol validationStudyProtocol = null;

					if (db.find(Protocol.class, new QueryRule(Protocol.NAME, Operator.EQUALS, validationStudyName))
							.size() != 0)
					{
						validationStudyProtocol = db.find(Protocol.class,
								new QueryRule(Protocol.NAME, Operator.EQUALS, validationStudyName)).get(0);
					}
					else
					{
						validationStudyProtocol = new Protocol();
						validationStudyProtocol.setName(validationStudyName);
						validationStudyProtocol.setInvestigation_Name(validationStudyName);
						db.add(validationStudyProtocol);
					}

					@SuppressWarnings("unchecked")
					Iterator<String> iterator = mappingResult.keys();

					while (iterator.hasNext())
					{
						String predictor = iterator.next();

						StringBuilder identifier = new StringBuilder();

						StringBuilder predictorName = new StringBuilder();

						predictorName.append(predictors.get(predictor).getLabel()).append("_").append(predictionModel);

						identifier.append(predictors.get(predictor).getLabel()).append("_").append(validationStudyName);

						if (db.find(Measurement.class,
								new QueryRule(Measurement.NAME, Operator.EQUALS, identifier.toString())).size() == 0)
						{
							Measurement m = new Measurement();
							m.setName(identifier.toString());
							m.setLabel(predictors.get(predictor).getLabel());
							m.setInvestigation_Name(validationStudyName);
							db.add(m);

							validationStudyProtocol.getFeatures_Id().add(m.getId());
						}

						JSONArray features = mappingResult.getJSONArray(predictor);

						List<String> listOfFeatures = new ArrayList<String>();

						List<Integer> listOfFeatureIds = new ArrayList<Integer>();

						for (int i = 0; i < features.length(); i++)
						{
							listOfFeatures.add(features.getString(i));
						}

						for (Measurement m : db.find(Measurement.class, new QueryRule(Measurement.NAME, Operator.IN,
								listOfFeatures)))
						{
							listOfFeatureIds.add(m.getId());
						}

						MappingMeasurement mapping = null;

						Query<MappingMeasurement> queryForMapping = db.query(MappingMeasurement.class);

						queryForMapping.addRules(new QueryRule(MappingMeasurement.TARGET_NAME, Operator.EQUALS,
								identifier.toString()));

						queryForMapping.addRules(new QueryRule(MappingMeasurement.MAPPING_NAME, Operator.EQUALS,
								predictorName.toString()));

						if (queryForMapping.find().size() == 0)
						{
							mapping = new MappingMeasurement();

							mapping.setTarget_Name(identifier.toString());

							mapping.setInvestigation_Name(validationStudyName);

							mapping.setDataType("pairingrule");

							mapping.setMapping_Name(predictorName.toString());

							mapping.setFeature_Id(listOfFeatureIds);

							mapping.setFeature_Name(listOfFeatures);

							db.add(mapping);
						}
						else
						{
							mapping = queryForMapping.find().get(0);

							for (Integer eachID : listOfFeatureIds)
							{
								if (!mapping.getFeature_Id().contains(eachID))
								{
									mapping.getFeature_Id().add(eachID);
								}
							}

							for (String eachFeature : listOfFeatures)
							{
								if (!mapping.getFeature_Name().contains(eachFeature))
								{
									mapping.getFeature_Name().add(eachFeature);
								}
							}

							db.update(mapping);
						}

						db.update(validationStudyProtocol);

						predictors.get(predictor).setFinalMappings(mapping.getFeature_Name());

						status.put(predictorName.toString().replaceAll(" ", "_"),
								makeExistingMappingTable(predictors.get(predictor)));

						status.put("success", true);
						status.put("message", "The mapping has updated");
					}
				}
				else if ("download_json_removeMapping".equals(request.getAction()))
				{
					String predictor = request.getString("predictor");

					String measurementName = request.getString("measurementName");

					StringBuilder identifier = new StringBuilder();

					StringBuilder predictorName = new StringBuilder();

					identifier.append(predictor).append("_").append(request.getString("validationStudy"));

					predictorName.append(predictor).append("_").append(request.getString("predictionModel"));

					Query<MappingMeasurement> queryForMapping = db.query(MappingMeasurement.class);

					queryForMapping.addRules(new QueryRule(MappingMeasurement.TARGET_NAME, Operator.EQUALS, identifier
							.toString()));

					queryForMapping.addRules(new QueryRule(MappingMeasurement.MAPPING_NAME, Operator.EQUALS,
							predictorName.toString()));

					Measurement m = db.find(Measurement.class,
							new QueryRule(Measurement.NAME, Operator.EQUALS, measurementName)).get(0);

					MappingMeasurement mapping = queryForMapping.find().get(0);

					mapping.getFeature_Name().remove(measurementName);

					mapping.getFeature_Id().remove(m.getId());

					if (mapping.getFeature_Id().size() == 0)
					{
						db.remove(mapping);

						Protocol validationStudyProtocol = db.find(Protocol.class,
								new QueryRule(Protocol.NAME, Operator.EQUALS, request.getString("validationStudy")))
								.get(0);

						Measurement derivedPredictor = db.find(Measurement.class,
								new QueryRule(Measurement.NAME, Operator.EQUALS, identifier)).get(0);

						validationStudyProtocol.getFeatures_Id().remove(derivedPredictor.getId());

						if (validationStudyProtocol.getFeatures_Id().size() == 0)
						{
							db.remove(validationStudyProtocol);
						}
						else
						{
							db.update(validationStudyProtocol);
						}

						db.remove(derivedPredictor);
					}
					else
					{
						db.update(mapping);
					}

					status.put("message", "The variable " + measurementName + " was removed from the mapping for "
							+ predictor);

					status.put("success", true);

				}
				else
				{
					status = catalogue.requestHandle(request, db, out);
				}

				db.commitTx();
			}
			catch (Exception e)
			{
				status.put("message", e.getMessage());
				status.put("success", false);
				db.rollbackTx();
				e.printStackTrace();
			}

			PrintWriter writer = new PrintWriter(out);
			writer.write(status.toString());
			writer.flush();
			writer.close();
		}

		return Show.SHOW_MAIN;
	}

	public String makeMappingTable(PredictorInfo predictor)
	{
		String table = "<table id=\"mapping_" + predictor.getName().replaceAll(" ", "_")
				+ "\" style=\"display:none;position:relative;top:5px;width:100%;overflow:auto;\""
				+ " class=\"ui-widget-content ui-corner-all\">"
				+ "<tr class=\"ui-widget-header ui-corner-all\"><th>Mapped varaibles"
				+ "</th><th>Description</th><th>Select the mapping</th></tr>";

		String predictorName = predictor.getName();

		for (String measurementName : predictor.getMappedVariables())
		{
			String description = predictor.getDescription(measurementName);

			String identifier = predictorName + "_" + measurementName;

			table += "<tr id=\"" + identifier.replaceAll(" ", "_")
					+ "_row\"><td style=\"text-align:center;cursor:pointer;\"><span>" + measurementName
					+ "</span><div id=\"" + identifier.replaceAll(" ", "_")
					+ "_details\" style=\"cursor:pointer;height:18px;width:18px;float:right;margin-right:10px;\" "
					+ "class=\"ui-state-default ui-corner-all\" title=\"Check expanded queries\">"
					+ "<span class=\"ui-icon ui-icon-plus\"></span></div>" + "</td><td style=\"text-align:center;\">"
					+ description + "</td><td style=\"text-align:center;\"><input type=\"checkbox\" id=\"" + identifier
					+ "_checkBox\" /></td></tr>";
		}

		table += "</table>";

		return table;
	}

	public String makeExistingMappingTable(PredictorInfo predictor)
	{
		StringBuilder table = new StringBuilder();

		table.append("<table id=\"matched_").append(predictor.getName().replaceAll(" ", "_"))
				.append("\" style=\"display:none;position:relative;top:5px;width:100%;overflow:auto;\"")
				.append(" class=\"ui-widget-content ui-corner-all\">")
				.append("<tr class=\"ui-widget-header ui-corner-all\"><th>Mapped varaibles")
				.append("</th><th>Description</th><th>Remove the mapping</th></tr>");

		String predictorName = predictor.getName();

		for (String measurementName : predictor.getFinalMappings())
		{
			String description = predictor.getDescription(measurementName);

			StringBuilder identifier = new StringBuilder();

			identifier.append(predictorName).append("_").append(measurementName);

			table.append("<tr id=\"").append(identifier.toString().replaceAll(" ", "_"))
					.append("_matchedRow\"><td style=\"text-align:center;cursor:pointer;\"><span>")
					.append(measurementName).append("</span></td><td style=\"text-align:center;\">")
					.append(description).append("</td><td style=\"text-align:center;\"><div id=\"")
					.append(identifier.toString().replaceAll(" ", "_"))
					.append("_remove\" style=\"cursor:pointer;height:18px;width:18px;margin-left:30px;\" ")
					.append("class=\"ui-state-default ui-corner-all\" title=\"remove\">")
					.append("<span class=\"ui-icon ui-icon-trash\"></span></div></td></tr>");
		}

		table.append("</table>");

		return table.toString();
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
			predictors.clear();

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

	private void executeMapping(PredictorInfo predictor, List<Measurement> measurementsInStudy) throws Exception
	{
		MappingList mappings = new MappingList();

		for (String eachQuery : predictor.getExpandedQuery())
		{
			double maxSimilarity = 0;

			String matchedDataItem = "";

			String measurementName = "";

			List<String> tokens = model.createNGrams(eachQuery.toLowerCase().trim(), true);

			for (Measurement m : measurementsInStudy)
			{
				List<String> fields = new ArrayList<String>();

				if (m.getDescription() != null && !m.getDescription().equals(""))
				{
					fields.add(m.getDescription());

					if (m.getCategories_Name().size() > 0)
					{
						for (String categoryName : m.getCategories_Name())
						{
							fields.add(categoryName + " " + m.getDescription());
						}
					}
				}

				for (String question : fields)
				{
					List<String> dataItemTokens = model.createNGrams(question.toLowerCase().trim(), true);

					double similarity = model.calculateScore(dataItemTokens, tokens);

					if (similarity > maxSimilarity)
					{
						if (m.getDescription() != null)
						{
							matchedDataItem = m.getDescription();
						}
						else
						{
							matchedDataItem = question;
						}
						maxSimilarity = similarity;
						measurementName = m.getName();
					}
				}
			}
			mappings.add(eachQuery, matchedDataItem, maxSimilarity, measurementName);
		}

		predictor.setMappings(mappings);
	}

	private List<String> createExpandQuery(String[] buildingBlocksArray, OWLFunction owlFunction)
	{
		List<String> buildingBlocks = new ArrayList<String>(Arrays.asList(buildingBlocksArray));

		List<String> expandedQueries = new ArrayList<String>();

		if (buildingBlocks.size() == 1)
		{
			expandedQueries.addAll(collectInfoFromOntology(buildingBlocks.get(0).trim(), owlFunction));
		}
		else if (buildingBlocks.size() >= 2)
		{
			List<String> expansionForFirst = collectInfoFromOntology(buildingBlocks.get(0).trim(), owlFunction);

			List<String> expansionForSeconed = collectInfoFromOntology(buildingBlocks.get(1).trim(), owlFunction);

			List<String> concatenatedString = new ArrayList<String>();

			for (String tokenFromFirst : expansionForFirst)
			{
				expandedQueries.add(tokenFromFirst);

				for (String tokenFromSecond : expansionForSeconed)
				{
					expandedQueries.add(tokenFromSecond);
					concatenatedString.add(tokenFromFirst + " " + tokenFromSecond);
				}
			}

			String first = buildingBlocks.get(0);

			String second = buildingBlocks.get(1);

			buildingBlocks.remove(first);

			buildingBlocks.remove(second);

			while (buildingBlocks.size() > 0)
			{
				List<String> expansionForNext = collectInfoFromOntology(buildingBlocks.get(0).trim(), owlFunction);

				List<String> fragments = new ArrayList<String>();

				for (String fragmentString : concatenatedString)
				{
					for (String tokenForNext : expansionForNext)
					{
						fragments.add(fragmentString + " " + tokenForNext);

						expandedQueries.add(tokenForNext);
					}
				}

				String next = buildingBlocks.get(0);

				buildingBlocks.remove(next);

				concatenatedString = fragments;
			}

			expandedQueries.addAll(concatenatedString);
		}

		return expandedQueries;
	}

	public List<String> collectInfoFromOntology(String queryToExpand, OWLFunction owlFunction)
	{

		List<String> expandedQueries = new ArrayList<String>();
		expandedQueries.add(queryToExpand);
		expandedQueries.addAll(owlFunction.getSynonyms(queryToExpand));
		expandedQueries.addAll(owlFunction.getAllChildren(queryToExpand, new ArrayList<String>(), 1));

		return expandedQueries;
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
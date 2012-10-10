package plugins.welcome;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Measurement;
import org.molgenis.protocol.Protocol;
import org.molgenis.util.Tuple;

/**
 * catalogueWelcomeScreenPluginController takes care of all user requests and
 * application logic.
 * 
 * <li>Each user request is handled by its own method based action=methodName.
 * <li>MOLGENIS takes care of db.commits and catches exceptions to show to the
 * user <li>catalogueWelcomeScreenPluginModel holds application state and
 * business logic on top of domain model. Get it via
 * this.getModel()/setModel(..) <li>catalogueWelcomeScreenPluginView holds the
 * template to show the layout. Get/set it via this.getView()/setView(..).
 */
public class bioshareWelcomeScreenPlugin extends
		EasyPluginController<bioshareWelcomeScreenPluginModel> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5739079926760041071L;

	public bioshareWelcomeScreenPlugin(String name, ScreenController<?> parent) {
		super(name, parent);
		this.setModel(new bioshareWelcomeScreenPluginModel(this)); // the
																	// default
																	// model
	}

	public ScreenView getView() {
		return new FreemarkerView("bioshareWelcomeScreenPluginView.ftl",
				getModel());
	}

	public void preLoadData(Database db, String investigationName,
			String protocolName, HashMap<String, String> featuresForStudies)
			throws DatabaseException {

		// Pre-load the catalogueStudy investigation to hold the information
		// to describe the cohort studies and prediction models
		if (db.find(
				Investigation.class,
				new QueryRule(Investigation.NAME, Operator.EQUALS,
						investigationName)).size() == 0) {
			Investigation inv = new Investigation();
			inv.setName(investigationName);
			db.add(inv);
		}

		List<String> listOfFeatures = new ArrayList<String>(
				featuresForStudies.keySet());

		for (Measurement m : db.find(Measurement.class, new QueryRule(
				Measurement.NAME, Operator.IN, new ArrayList<String>(
						featuresForStudies.keySet())))) {
			featuresForStudies.remove(m.getName());
		}

		if (featuresForStudies.size() > 0) {

			List<Measurement> newMeasurements = new ArrayList<Measurement>();

			for (Entry<String, String> newFeature : featuresForStudies
					.entrySet()) {

				Measurement m = new Measurement();
				m.setName(newFeature.getKey());
				m.setLabel(newFeature.getValue());
				m.setInvestigation_Name(investigationName);
				newMeasurements.add(m);
			}
			db.add(newMeasurements);
		}

		// Add the protocol to which the features belong
		Protocol p = null;
		if (db.find(Protocol.class,
				new QueryRule(Protocol.NAME, Operator.EQUALS, protocolName))
				.size() == 0) {
			p = new Protocol();
			p.setName(protocolName);
			p.setInvestigation_Name(investigationName);
			p.setFeatures_Name(listOfFeatures);
			db.add(p);
		} else {
			p = db.find(Protocol.class,
					new QueryRule(Protocol.NAME, Operator.EQUALS, protocolName))
					.get(0);
			p.setFeatures_Name(listOfFeatures);
			db.update(p);
		}
	}

	/**
	 * At each page view: reload data from database into model and/or change.
	 * 
	 * Exceptions will be caught, logged and shown to the user automatically via
	 * setMessages(). All db actions are within one transaction.
	 */
	@Override
	public void reload(Database db) throws Exception {

		HashMap<String, String> featuresForStudies = new HashMap<String, String>();

		featuresForStudies.put("studyName_catalogueStudy", "Study name");
		featuresForStudies.put("studyDescription_catalogueStudy",
				"Study description");
		featuresForStudies.put("launchYear_catalogueStudy", "Launched year");
		featuresForStudies.put("countryOfStudy_catalogueStudy",
				"Country of study");
		featuresForStudies.put("numberOfParticipants_catalogueStudy",
				"Number of participants");
		featuresForStudies.put("ageGroun_catalogueStudy", "Age group");
		featuresForStudies.put("ethnicGroup_catalogueStudy", "Ethnic group");
		// Pre-load the metadata that is used to describe the cohort studies.
		preLoadData(db, "catalogueStudy", "studyCharacteristic",
				featuresForStudies);

		// Pre-load the metadata that is used to describe the prediction model.
		featuresForStudies.clear();
		featuresForStudies.put("predictionModelName_catalogueStudy",
				"Prediction model");
		featuresForStudies.put("numberOfPredictors_catalogueStudy",
				"Number of predictors");
		featuresForStudies.put("statisticalModel_catalogueStudy",
				"Statistical model");
		featuresForStudies.put("diseasePrediction_catalogueStudy",
				"Predicted disease");
		featuresForStudies.put("discrimination_catalogueStudy",
				"Discrimination");
		featuresForStudies.put("calibration_catalogueStudy", "Calibration");
		// Pre-load the metadata that is used to describe the cohort studies.
		preLoadData(db, "catalogueStudy", "predictionModelCharacteristic",
				featuresForStudies);
	}

	/**
	 * When action="updateDate": update model and/or view accordingly.
	 * 
	 * Exceptions will be logged and shown to the user automatically. All db
	 * actions are within one transaction.
	 */
	public void updateDate(Database db, Tuple request) throws Exception {
		getModel().date = request.getDate("date");

		// //Easily create object from request and add to database
		// Investigation i = new Investigation(request);
		// db.add(i);
		// this.setMessage("Added new investigation");

		getModel().setSuccess("Update successful");
	}
}
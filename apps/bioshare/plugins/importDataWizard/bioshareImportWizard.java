package plugins.importDataWizard;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

public class bioshareImportWizard extends PluginModel<Entity> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7294262856742516691L;
	private List<String> listOfCohortStudies = new ArrayList<String>();
	private List<String> listOfPredictionModels = new ArrayList<String>();

	public bioshareImportWizard(String name, ScreenController<?> parent) {
		super(name, parent);
	}

	@Override
	public String getViewName() {
		return "plugins_importDataWizard_bioshareImportWizard";
	}

	@Override
	public String getViewTemplate() {
		return "plugins/importDataWizard/bioshareImportWizard.ftl";
	}

	@Override
	public Show handleRequest(Database db, Tuple request, OutputStream out)
			throws Exception {

		// The request is submit request, therefore the request goes to the
		// normal
		// handle request
		if (out == null) {
			this.handleRequest(db, request);
		} else {
			// The request is ajax request, handle it here
			if ("download_json_addNewStudy".equals(request.getAction())) {
				System.out.println("The request is --------- "
						+ request.getAction());
				System.out.println(request.getString("studyInfo"));
				JSONObject jsonObject = new JSONObject(
						request.getString("studyInfo"));
				System.out.println(jsonObject);
			}
		}

		return Show.SHOW_MAIN;
	}

	@Override
	public void handleRequest(Database db, Tuple request) throws Exception {

	}

	@Override
	public void reload(Database db) {

		try {
			listOfCohortStudies.clear();
			listOfPredictionModels.clear();
			// Get all the investigations from database except for Prediction
			// model
			for (ObservationTarget target : db.find(ObservationTarget.class,
					new QueryRule(ObservationTarget.INVESTIGATION_NAME,
							Operator.EQUALS, "catalogueCohortStudy"))) {
				listOfCohortStudies.add(target.getName());
			}
			for (ObservationTarget target : db.find(ObservationTarget.class,
					new QueryRule(ObservationTarget.INVESTIGATION_NAME,
							Operator.EQUALS, "cataloguePredictionModel"))) {
				listOfPredictionModels.add(target.getName());
			}

		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public List<String> getlistOfCohortStudies() {
		return listOfCohortStudies;
	}

	public List<String> getListOfPredictionModels() {
		return listOfPredictionModels;
	}

	public String getUrl() {
		return "molgenis.do?__target=" + this.getName();
	}
}

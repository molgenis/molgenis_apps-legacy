package plugins.importDataWizard;

import java.util.ArrayList;
import java.util.List;

import org.molgenis.compute.ComputeProtocol;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.organization.Investigation;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

public class bioshareImportWizard extends PluginModel<Entity> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7294262856742516691L;
	private List<String> listOfInvestigationNames = new ArrayList<String>();
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
	public void handleRequest(Database db, Tuple request) throws Exception {

	}

	@Override
	public void reload(Database db) {

		try {

			// Get all the investigations from database except for Prediction
			// model
			for (Investigation inv : db.find(Investigation.class)) {

				if (!inv.getName().equals("Prediction Model")) {
					listOfInvestigationNames.add(inv.getName());
				}
			}

			// Get all the protocols from Prediction model Investigation
			for (ComputeProtocol p : db.find(ComputeProtocol.class,
					new QueryRule(ComputeProtocol.INVESTIGATION_NAME,
							Operator.EQUALS, "Prediction Model"))) {
				listOfPredictionModels.add(p.getName());
			}

		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public List<String> getListOfInvestigationNames() {
		return listOfInvestigationNames;
	}

	public List<String> getListOfPredictionModels() {
		return listOfPredictionModels;
	}
}

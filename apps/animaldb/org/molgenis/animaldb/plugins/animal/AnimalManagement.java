/* Date:        March 11, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.animaldb.plugins.animal;

import java.util.ArrayList;
import java.util.List;

import org.molgenis.animaldb.commonservice.CommonService;
import org.molgenis.auth.MolgenisUser;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.server.MolgenisRequest;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.matrix.component.MatrixViewer;
import org.molgenis.matrix.component.SliceablePhenoMatrix;
import org.molgenis.matrix.component.general.MatrixQueryRule;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationElement;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.util.Entity;

public class AnimalManagement extends PluginModel<Entity>
{
	private static final long serialVersionUID = -7609580651170222454L;
	private List<Integer> animalIdList;
	private String action = "init";
	private String info = "";
	private CommonService cs = CommonService.getInstance();
	MatrixViewer animalMatrixViewer = null;
	static String ANIMALMATRIX = "animalmatrix";
	private String animalMatrixRendered;
	List<String> listOfUsersString = new ArrayList<String>();

	public AnimalManagement(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	public String getCustomHtmlHeaders()
	{
		return "<script src=\"res/jquery-plugins/ctnotify/lib/jquery.ctNotify.js\" language=\"javascript\"></script>\n"
				+ "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/jquery-plugins/ctnotify/lib/jquery.ctNotify.css\">"
				+ "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/animaldb.css\">";
	}

	// Animal related methods:
	public List<Integer> getAnimalIdList()
	{
		return animalIdList;
	}

	public void setAnimalIdList(List<Integer> animalIdList)
	{
		this.animalIdList = animalIdList;
	}

	public String getAnimalName(Integer id)
	{
		try
		{
			return cs.getObservationTargetLabel(id);
		}
		catch (Exception e)
		{
			return id.toString();
		}
	}

	public String getAnimalMatrix()
	{
		if (animalMatrixRendered != null)
		{
			return animalMatrixRendered;
		}
		return "Error - animal matrix not initialized";
	}

	/**
	 * At each page view: reload data from database into model and/or change.
	 * 
	 * Exceptions will be caught, logged and shown to the user automatically via
	 * setMessages(). All db actions are within one transaction.
	 */
	@Override
	public void reload(Database db)
	{
		cs.setDatabase(db);
		cs.makeObservationTargetNameMap(this.getLogin().getUserName(), false);
		List<MolgenisUser> listOfUsers = new ArrayList<MolgenisUser>();
		listOfUsersString = new ArrayList<String>();
		try
		{
			listOfUsers = db.find(MolgenisUser.class);
			for (MolgenisUser m : listOfUsers)
			{
				listOfUsersString.add(m.getName());
			}
		}
		catch (DatabaseException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (animalMatrixViewer != null)
		{
			animalMatrixViewer.setDatabase(db);
		}
		else
		{
			try
			{

				List<String> investigationNames = cs.getAllUserInvestigationNames(this.getLogin().getUserName());
				List<String> measurementsToShow = new ArrayList<String>();
				measurementsToShow.add("Active");
				measurementsToShow.add("DateOfBirth");
				measurementsToShow.add("GeneModification");
				measurementsToShow.add("GeneState");
				measurementsToShow.add("Line");
				measurementsToShow.add("Litter");
				measurementsToShow.add("Location");
				measurementsToShow.add("Sex");
				measurementsToShow.add("Species");
				List<MatrixQueryRule> filterRules = new ArrayList<MatrixQueryRule>();
				filterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.rowHeader, Individual.INVESTIGATION_NAME,
						Operator.IN, investigationNames));
				filterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.colValueProperty, cs
						.getMeasurementId("Active"), ObservedValue.VALUE, Operator.EQUALS, "Alive"));
				filterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.colValueProperty, cs
						.getMeasurementId("Litter"), ObservedValue.RELATION, Operator.NOT, null));
				animalMatrixViewer = new MatrixViewer(this, ANIMALMATRIX,
						new SliceablePhenoMatrix<Individual, Measurement>(Individual.class, Measurement.class), true,
						2, false, false, filterRules, new MatrixQueryRule(MatrixQueryRule.Type.colHeader,
								Measurement.NAME, Operator.IN, measurementsToShow));
				animalMatrixViewer.setDatabase(db);
				animalMatrixViewer.setLabel("Choose animal:");
			}
			catch (Exception e)
			{
				this.setError("Could not initialize matrix");
			}
		}
		animalMatrixRendered = animalMatrixViewer.render();
	}

	@Override
	public String getViewName()
	{
		return "org_molgenis_animaldb_plugins_animal_AnimalManagement";
	}

	@Override
	public String getViewTemplate()
	{
		return "org/molgenis/animaldb/plugins/animal/AnimalManagement.ftl";
	}

	@Override
	public void handleRequest(Database db, MolgenisRequest request)
	{
		cs.setDatabase(db);
		if (animalMatrixViewer != null)
		{
			animalMatrixViewer.setDatabase(db);
		}
		try
		{
			action = request.getString("__action");

			if (action.startsWith(animalMatrixViewer.getName()))
			{
				animalMatrixViewer.handleRequest(db, request);
			}

			List<ObservationElement> animalList = new ArrayList<ObservationElement>();
			if (action.equals("Give read rights"))
			{
				@SuppressWarnings("unchecked")
				List<ObservationElement> rows = (List<ObservationElement>) animalMatrixViewer.getSelection(db);
				int rowCnt = 0;
				for (ObservationElement row : rows)
				{
					if (request.getBoolean(ANIMALMATRIX + "_selected_" + rowCnt) != null)
					{
						animalList.add(row);
					}
					rowCnt++;
				}

				String selectedUser = request.getString("usersList");

				MolgenisUser selectedMolgenisUser = db.find(MolgenisUser.class,
						new QueryRule(MolgenisUser.NAME, Operator.EQUALS, selectedUser)).get(0);

				for (ObservationElement animal : animalList)
				{
					// animal.setCanRead(selectedMolgenisUser);
					animal.setCanWrite(selectedMolgenisUser);

					db.update(animal);
					// System.out.println("AnimalName: " + animalName + "_" +
					// selectedUser);

				}
			}
			if (action.equals("Chosen Animals"))
			{
				@SuppressWarnings("unchecked")
				List<ObservationElement> rows = (List<ObservationElement>) animalMatrixViewer.getSelection(db);
				int rowCnt = 0;
				for (ObservationElement row : rows)
				{
					if (request.getBoolean(ANIMALMATRIX + "_selected_" + rowCnt) != null)
					{
						animalList.add(row);
					}
					rowCnt++;
				}

				String selectedUser = request.getString("usersList");

				MolgenisUser selectedMolgenisUser = db.find(MolgenisUser.class,
						new QueryRule(MolgenisUser.NAME, Operator.EQUALS, selectedUser)).get(0);

				for (ObservationElement animal : animalList)
				{
					// animal.setCanRead(selectedMolgenisUser);
					animal.setCanWrite(selectedMolgenisUser);

					db.update(animal);
					// System.out.println("AnimalName: " + animalName + "_" +
					// selectedUser);

				}
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (e.getMessage() != null)
			{
				this.setMessages(new ScreenMessage(e.getMessage(), false));
			}
		}
	}

	public void setAction(String action)
	{
		this.action = action;
	}

	public String getAction()
	{
		return action;
	}

	public void setInfo(String info)
	{
		this.info = info;
	}

	public String getInfo()
	{
		return info;
	}

	public List<String> getListOfUsersString()
	{
		return listOfUsersString;
	}
}

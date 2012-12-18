/* Date:        October 5, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.plantqtl.header;

import java.io.File;

import matrix.general.DataMatrixHandler;

import org.molgenis.auth.MolgenisPermission;
import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.Database.DatabaseAction;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.util.tuple.Tuple;

import plugins.system.database.Settings;
import app.ExcelEntityImporter;
import app.ExcelImport;

public class HomePage extends plugins.cluster.demo.ClusterDemo
{

	private static final long serialVersionUID = -3744678801173089268L;

	public HomePage(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "org_molgenis_plantqtl_header_HomePage";
	}

	@Override
	public String getViewTemplate()
	{
		return "org/molgenis/plantqtl/header/HomePage.ftl";
	}

	@Override
	public void handleRequest(Database db, MolgenisRequest request)
	{
		String action = request.getString("__action");
		if (action.equals("setPathAndLoad"))
		{
			setupStorageAndLoadExample(db, request.getString("fileDirPath"));
			addPanaceaPermissionsAndTryToImportData(db);
		}
	}

	private void addPanaceaPermissionsAndTryToImportData(Database db)
	{
		try
		{

			String[] qtlFinderPerms = new String[]
			{

					// allow to see the QTL finder
					"app.ui.QtlFinderPublic2Plugin",

					// allow to see genome browser
					"app.ui.GenomeBrowserPlugin",

					// allow to see experiment overview
					"app.ui.ExpTablePlugin",

					// allow to see help
					"app.ui.HelpPlugin",

					// enable the Browse Data menu (minus Inspector and matrix
					// removal)
					"app.ui.InvestigationsFormController",
					"app.ui.DatasFormController",
					"app.ui.ManagerPlugin",

					// needed to query elements for investigation overview
					"org.molgenis.pheno.ObservationElement",

					// needed to view the generated annotation menus
					// some unused datatypes in the current wormqtl release are
					// left out for anonymous
					"app.ui.PanelsFormController", "org.molgenis.pheno.Panel",

					"app.ui.ChromosomesFormController", "org.molgenis.xgap.Chromosome",

					"app.ui.MarkersFormController", "org.molgenis.xgap.Marker",

					"app.ui.GenesFormController", "org.molgenis.xgap.Gene",

					"app.ui.TranscriptsFormController", "org.molgenis.xgap.Transcript",

					"app.ui.MeasurementsFormController", "org.molgenis.pheno.Measurement",

					"app.ui.DerivedTraitsFormController", "org.molgenis.xgap.DerivedTrait",

					"app.ui.ProbesFormController", "org.molgenis.xgap.Probe",

					"app.ui.SamplesFormController",
					"org.molgenis.xgap.Sample",

					// allow reading datasets and investigations
					"org.molgenis.organization.Investigation", "org.molgenis.data.Data",
					"org.molgenis.data.BinaryDataMatrix", "org.molgenis.data.CSVDataMatrix",
					"org.molgenis.data.DecimalDataElement", "org.molgenis.data.TextDataElement",

					// allow reading dataset backend files (include seperate
					// InvestigationFile for overview plugin)
					"org.molgenis.core.MolgenisFile", "org.molgenis.xgap.InvestigationFile",

					// allow to see how uploaded this dataset
					"org.molgenis.protocol.ProtocolApplication_Performer",

					// allow to see analysis metadata
					"org.molgenis.cluster.DataSet", "org.molgenis.cluster.DataName", "org.molgenis.cluster.DataValue",

			};

			for (String e : qtlFinderPerms)
			{
				MolgenisPermission mp = new MolgenisPermission();
				mp.setEntity_ClassName(e);
				mp.setRole_Name("anonymous");
				mp.setPermission("read");
				db.add(mp);
			}

			DataMatrixHandler dmh = new DataMatrixHandler(db);

			if (dmh.hasValidFileStorage(db))
			{
				// check metadata.xls
				String importDir = dmh.getFileStorage(true, db).getAbsolutePath();
				File metadata = new File(importDir + File.separator + "metadata.xls");
				if (!metadata.exists())
				{
					throw new Exception("Annotation Excel file is missing!");
				}

				new ExcelEntityImporter(db).importData(metadata, db, DatabaseAction.ADD);

				// relink datasets
				// fails if filenames have uppercase characters!
				// convert using :
				// for i in *; do mv "$i" "$(echo $i|tr A-Z a-z)"; done
				relinkDatasets(db, dmh);

				// remove clusterdemo example investigation
				Settings.deleteExampleInvestigation("ClusterDemo", db);

				// all done
				this.setMessages(new ScreenMessage("Annotation import and data relink succeeded", true));
			}
			else
			{
				this.setMessages(new ScreenMessage(
						"Permissions loaded, but could not import annotations because storagedir setup failed", false));
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
			this.setMessages(new ScreenMessage(e.getMessage(), false));
		}
	}

	/**
	 * Relink datasets if needed: but expected is that ALL are relinked when the
	 * function ends, or else error
	 * 
	 * @param db
	 * @param dmh
	 * @throws Exception
	 */
	private void relinkDatasets(Database db, DataMatrixHandler dmh) throws Exception
	{
		for (Data data : db.find(Data.class))
		{
			// find out if the 'Data' has a proper backend
			boolean hasLinkedStorage = dmh.isDataStoredIn(data, data.getStorage(), db);

			// if not, it doesn't mean the source file is not there! e.g. after
			// updating your database
			if (!hasLinkedStorage)
			{
				// attempt to relink
				boolean relinked = dmh.attemptStorageRelink(data, data.getStorage(), db);

				if (!relinked)
				{
					throw new Exception("Could not relink data matrix '" + data.getName() + "'");
				}

				if (!dmh.isDataStoredIn(data, data.getStorage(), db))
				{
					throw new Exception("SEVERE: Data matrix '" + data.getName()
							+ "' is supposed to be relinked, but the isDataStoredIn check failed!");
				}

			}
		}

	}

}

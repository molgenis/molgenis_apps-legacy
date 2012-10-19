package plugins.importwizard;

import java.io.File;
import java.util.List;

import app.ImportWizardExcelPrognosis;

public class ImportWizardModel
{

	private String whichScreen;
	private File currentFile;
	private ImportWizardExcelPrognosis iwep;
	private boolean importSuccess;
	private List<String> dataSetSheetNames;

	public String getWhichScreen()
	{
		return whichScreen;
	}

	public void setWhichScreen(String whichScreen)
	{
		this.whichScreen = whichScreen;
	}

	public File getCurrentFile()
	{
		return currentFile;
	}

	public void setCurrentFile(File currentFile)
	{
		this.currentFile = currentFile;
	}

	public ImportWizardExcelPrognosis getIwep()
	{
		return iwep;
	}

	public void setIwep(ImportWizardExcelPrognosis iwep)
	{
		this.iwep = iwep;
	}

	public boolean isImportSuccess()
	{
		return importSuccess;
	}

	public void setImportSuccess(boolean importSuccess)
	{
		this.importSuccess = importSuccess;
	}

	public List<String> getDataSetSheetNames()
	{
		return dataSetSheetNames;
	}

	public void setDataSetSheetNames(List<String> dataSetSheetNames)
	{
		this.dataSetSheetNames = dataSetSheetNames;
	}

}

package org.molgenis.omicsconnect.plugins;

import java.io.File;
import java.util.Collection;
import java.util.Map;

/**
 * Import wizard model
 */
public class ImportWizard extends WizardModel
{
	private File file;

	private Map<String, Boolean> entitiesImportable;
	private Map<String, Boolean> dataImportable;
	private Map<String, Collection<String>> fieldsDetected;
	private Map<String, Collection<String>> fieldsUnknown;
	private Map<String, Collection<String>> fieldsRequired;
	private Map<String, Collection<String>> fieldsAvailable;

	private boolean importError;
	private boolean validationError;

	private String entityImportOption;
	private String dataFeatureImportOption;
	private String dataTargetImportOption;

	public ImportWizard(int nrPages)
	{
		super(nrPages);
	}

	public File getFile()
	{
		return file;
	}

	public void setFile(File file)
	{
		this.file = file;
	}

	public Map<String, Boolean> getEntitiesImportable()
	{
		return entitiesImportable;
	}

	public void setEntitiesImportable(Map<String, Boolean> entitiesImportable)
	{
		this.entitiesImportable = entitiesImportable;
	}

	public Map<String, Collection<String>> getFieldsDetected()
	{
		return fieldsDetected;
	}

	public Map<String, Collection<String>> getFieldsUnknown()
	{
		return fieldsUnknown;
	}

	public Map<String, Collection<String>> getFieldsRequired()
	{
		return fieldsRequired;
	}

	public Map<String, Collection<String>> getFieldsAvailable()
	{
		return fieldsAvailable;
	}

	public void setFieldsDetected(Map<String, Collection<String>> fieldsDetected)
	{
		this.fieldsDetected = fieldsDetected;
	}

	public void setFieldsUnknown(Map<String, Collection<String>> fieldsUnknown)
	{
		this.fieldsUnknown = fieldsUnknown;
	}

	public void setFieldsRequired(Map<String, Collection<String>> fieldsRequired)
	{
		this.fieldsRequired = fieldsRequired;
	}

	public void setFieldsAvailable(Map<String, Collection<String>> fieldsAvailable)
	{
		this.fieldsAvailable = fieldsAvailable;
	}

	public Map<String, Boolean> getDataImportable()
	{
		return dataImportable;
	}

	public void setDataImportable(Map<String, Boolean> dataImportable)
	{
		this.dataImportable = dataImportable;
	}

	public boolean isImportError()
	{
		return importError;
	}

	public void setImportError(boolean importError)
	{
		this.importError = importError;
	}

	public boolean isValidationError()
	{
		return validationError;
	}

	public void setValidationError(boolean validationError)
	{
		this.validationError = validationError;
	}

	public String getEntityImportOption()
	{
		return entityImportOption;
	}

	public void setEntityImportOption(String entityImportOption)
	{
		this.entityImportOption = entityImportOption;
	}

	public String getDataFeatureImportOption()
	{
		return dataFeatureImportOption;
	}

	public void setDataFeatureImportOption(String dataFeatureImportOption)
	{
		this.dataFeatureImportOption = dataFeatureImportOption;
	}

	public String getDataTargetImportOption()
	{
		return dataTargetImportOption;
	}

	public void setDataTargetImportOption(String dataTargetImportOption)
	{
		this.dataTargetImportOption = dataTargetImportOption;
	}
}

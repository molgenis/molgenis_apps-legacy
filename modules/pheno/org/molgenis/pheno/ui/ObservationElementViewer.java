package org.molgenis.pheno.ui;

import java.io.OutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.molgenis.MolgenisFieldTypes;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.server.MolgenisRequest;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.FormController;
import org.molgenis.framework.ui.FormModel;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.framework.ui.html.HiddenInput;
import org.molgenis.framework.ui.html.HtmlInputException;
import org.molgenis.framework.ui.html.SelectInput;
import org.molgenis.pheno.ObservationElement;
import org.molgenis.pheno.dto.FeatureDTO;
import org.molgenis.pheno.dto.ObservationElementDTO;
import org.molgenis.pheno.dto.ObservedValueDTO;
import org.molgenis.pheno.dto.ProtocolApplicationDTO;
import org.molgenis.pheno.dto.ProtocolDTO;
import org.molgenis.pheno.service.PhenoService;
import org.molgenis.pheno.ui.form.ApplyProtocolForm;
import org.molgenis.pheno.ui.form.ObservationTargetForm;
import org.molgenis.pheno.ui.form.SelectProtocolForm;
import org.molgenis.util.ValueLabel;

public class ObservationElementViewer extends EasyPluginController<ObservationElementViewerModel>
{
	/* The serial version UID of this class. Needed for serialization. */
	private static final long serialVersionUID = 8333436730236052413L;

	private ScreenView view;

	public ObservationElementViewer(String name, ScreenController<?> parent)
	{
		super(name, parent);
		this.setModel(new ObservationElementViewerModel(this));
		this.getModel().setAction("show");
		this.setView(new FreemarkerView("show.ftl", getModel()));
	}

	public void setView(ScreenView view)
	{
		this.view = view;
	}

	@Override
	public ScreenView getView()
	{
		return view;
	}

	@Override
	public Show handleRequest(Database db, MolgenisRequest request, OutputStream out)
	{
		// if (StringUtils.isNotEmpty(request.getAction()))
		// this.getModel().setAction(request.getAction());

		try
		{
			if ("show".equals(request.getAction()))
			{
				this.setView(new FreemarkerView("show.ftl", getModel()));
			}
			else if ("select".equals(request.getAction()))
			{
				this.handleSelect(db, request);
			}
			else if ("add".equals(request.getAction()))
			{
				this.handleAdd(db, request);
			}
			else if ("edit".equals(request.getAction()))
			{
				this.setView(new FreemarkerView("edit.ftl", getModel()));
			}
			else if ("insert".equals(request.getAction()))
			{
				this.handleInsert(db, request);
			}
			else if ("update".equals(request.getAction()))
			{
				this.handleUpdate(db, request);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			this.getModel().getMessages().add(new ScreenMessage(e.getMessage(), false));
		}
		return Show.SHOW_DIALOG;
	}

	/**
	 * Select a Protocol to be applied
	 * 
	 * @param db
	 * @param request
	 * @throws HtmlInputException
	 */
	private void handleSelect(Database db, MolgenisRequest request) throws HtmlInputException
	{
		this.setView(new FreemarkerView("select.ftl", getModel()));

		final PhenoService phenoService = new PhenoService(db);
		List<ProtocolDTO> protocolDTOList = phenoService.findProtocols();

		SelectProtocolForm form = new SelectProtocolForm();
		this.populateSelectProtocolForm(form, protocolDTOList);
	}

	/**
	 * Add a new ProtocolApplication aka "Apply Protocol"
	 * 
	 * @param db
	 * @param request
	 * @throws HtmlInputException
	 */
	private void handleAdd(Database db, MolgenisRequest request) throws HtmlInputException
	{
		try
		{
			this.setView(new FreemarkerView("add.ftl", getModel()));

			final PhenoService phenoService = new PhenoService(db);
			ProtocolDTO protocolDTO = phenoService.findProtocol(request.getInt("Protocol"));

			ApplyProtocolForm form = new ApplyProtocolForm();
			this.populateApplyProtocolForm(form, protocolDTO);

			this.getModel().setProtocolDTO(protocolDTO);
			this.getModel().setApplyProtocolForm(form);
		}
		catch (Exception e)
		{
			String message = "Please select a protocol.";
			this.getModel().getMessages().add(new ScreenMessage(message, false));
			this.handleSelect(db, request);
		}
	}

	private void populateSelectProtocolForm(SelectProtocolForm selectProtocolForm, List<ProtocolDTO> protocolDTOList)
	{
		((HiddenInput) selectProtocolForm.get("__target")).setValue(this.getName());

		List<ValueLabel> protocolOptions = new ArrayList<ValueLabel>();
		protocolOptions.add(new ValueLabel("", "Select a Protocol"));
		for (ProtocolDTO protocolDTO : protocolDTOList)
			protocolOptions.add(new ValueLabel(protocolDTO.getProtocolId(), protocolDTO.getProtocolName()));
		((SelectInput) selectProtocolForm.get("Protocol")).setOptions(protocolOptions);

		this.getModel().setSelectProtocolForm(selectProtocolForm);
	}

	private void populateApplyProtocolForm(ApplyProtocolForm applyProtocolForm, ProtocolDTO protocolDTO)
			throws HtmlInputException
	{
		((HiddenInput) applyProtocolForm.get("__target")).setValue(this.getName());

		for (FeatureDTO featureDTO : protocolDTO.getFeatureDTOList())
		{
			String fieldType = featureDTO.getFeatureType();
			String fieldName = featureDTO.getFeatureKey();
			applyProtocolForm.add(MolgenisFieldTypes.createInput(fieldType, fieldName, ""));
		}

		this.getModel().setProtocolDTO(protocolDTO);
		this.getModel().setApplyProtocolForm(applyProtocolForm);
	}

	/**
	 * Insert a new ProtocolApplication
	 * 
	 * @param db
	 * @param request
	 * @throws ParseException
	 */
	private void handleInsert(Database db, MolgenisRequest request) throws ParseException
	{
		this.setView(new FreemarkerView("show.ftl", getModel()));

		this.loadObservationElementDTO(db);

		List<ObservedValueDTO> insertList = new ArrayList<ObservedValueDTO>();
		Iterable<String> parameterNameList = request.getColNames();

		PhenoService phenoService = new PhenoService(db);

		ProtocolApplicationDTO paDTO = new ProtocolApplicationDTO();
		paDTO.setName(request.getString("paName"));
		paDTO.setTime(request.getDate("paTime"));
		paDTO.setProtocolId(this.getModel().getProtocolDTO().getProtocolId());
		List<Integer> performerIdList = new ArrayList<Integer>();
		for (String s : request.getList("paPerformer"))
		{
			performerIdList.add(Integer.parseInt(s));
		}
		paDTO.setPerformerIdList(performerIdList);

		Integer paId = phenoService.insert(paDTO);

		for (String parameterName : parameterNameList)
		{
			if (parameterName.startsWith("Feature"))
			{
				// insert a new ObservedValue
				// String[] parameterNameParts =
				// StringUtils.split(parameterName, ".");
				// String protocolApplicationIdString =
				// StringUtils.removeStartIgnoreCase(parameterNameParts[0],
				// "ProtocolApplication");
				String featureIdString = StringUtils.removeStartIgnoreCase(parameterName, "Feature");

				ObservedValueDTO observedValueDTO = new ObservedValueDTO();
				observedValueDTO.setValue(request.getString(parameterName));
				observedValueDTO.setTargetId(this.getModel().getObservationElementDTO().getObservationElementId());
				observedValueDTO.setProtocolApplicationId(paId);
				FeatureDTO featureDTO = new FeatureDTO();
				featureDTO.setFeatureId(Integer.parseInt(featureIdString));
				observedValueDTO.setFeatureDTO(featureDTO);
				insertList.add(observedValueDTO);
			}
		}

		phenoService.insert(insertList);

		this.getModel().getMessages().add(new ScreenMessage("Save successful", true));
	}

	/**
	 * Update ObservedValues of an Individual
	 * 
	 * @param db
	 * @param request
	 */
	private void handleUpdate(Database db, MolgenisRequest request)
	{
		this.setView(new FreemarkerView("edit.ftl", getModel()));

		this.loadObservationElementDTO(db);

		List<ObservedValueDTO> updateList = new ArrayList<ObservedValueDTO>();

		PhenoService phenoService = new PhenoService(db);

		for (String parameterName : request.getColNames())
		{
			if (parameterName.startsWith("ObservedValue"))
			{
				String observedValueIdString = StringUtils.removeStartIgnoreCase(parameterName, "ObservedValue");
				ObservedValueDTO observedValueDTO = phenoService.findObservedValue(Integer
						.parseInt(observedValueIdString));
				observedValueDTO.setValue(request.getString(parameterName));
				updateList.add(observedValueDTO);
			}
		}

		phenoService.update(updateList);

		this.getModel().getMessages().add(new ScreenMessage("Save successful", true));
	}

	/**
	 * Load details for ObservationElement in parent form
	 * 
	 * @param db
	 */
	private ObservationElementDTO loadObservationElementDTO(Database db)
	{
		ScreenController<?> parentController = this.getParent();
		@SuppressWarnings("unchecked")
		FormModel<ObservationElement> parentForm = ((FormController<ObservationElement>) parentController).getModel();

		if (parentForm.getRecords().size() == 0) return null;

		ObservationElement observationElement = parentForm.getRecords().get(0);

		PhenoService phenoService = new PhenoService(db);
		ObservationElementDTO observationElementDTO = phenoService.findPhenotypeDetails(observationElement);

		return observationElementDTO;
	}

	/**
	 * Populate the TargetForm with widgets for the measurements
	 * 
	 * @param targetForm
	 * @param observationElementDTO
	 * @throws HtmlInputException
	 */
	@SuppressWarnings("unchecked")
	private void populateTargetForm(ObservationTargetForm targetForm, ObservationElementDTO observationElementDTO)
			throws HtmlInputException
	{
		targetForm.get("__target").setValue(this.getName());

		for (ProtocolDTO protocolDTO : observationElementDTO.getProtocolList())
		{
			// create form input widgets
			// name of each widget conforms with "Protocol" + Protocol.id +
			// ".Feature" + Measurement.id
			for (FeatureDTO featureDTO : protocolDTO.getFeatureDTOList())
			{
				String fieldType = featureDTO.getFeatureType();
				String fieldKey = featureDTO.getFeatureKey();

				targetForm.add(MolgenisFieldTypes.createInput(fieldType, fieldKey, ""));
			}

			// set values for the input widgets
			// if present, name is changed to "ObservedValue" + ObservedValue.id
			// key of the widget inside the form remains unchanged
			if (observationElementDTO.getObservedValues().containsKey(protocolDTO.getProtocolKey()))
			{
				List<String> protocolApplicationKeyList = Arrays.asList(observationElementDTO.getObservedValues()
						.get(protocolDTO.getProtocolKey()).keySet().toArray(new String[0]));

				for (String protocolApplicationKey : protocolApplicationKeyList)
				{
					List<ObservedValueDTO> observedValueDTOList = observationElementDTO.getObservedValues()
							.get(protocolDTO.getProtocolKey()).get(protocolApplicationKey);

					for (ObservedValueDTO observedValueDTO : observedValueDTOList)
					{
						String fieldType = observedValueDTO.getFeatureDTO().getFeatureType();
						// String fieldKey = protocolApplicationKey + "." +
						// observedValueDTO.getFeatureDTO().getFeatureKey();
						String fieldName = "ObservedValue" + observedValueDTO.getObservedValueId();
						targetForm.add(MolgenisFieldTypes.createInput(fieldType, fieldName, ""));
						targetForm.get(fieldName).setName(fieldName);
						targetForm.get(fieldName).setValue(observedValueDTO.getValue());
					}
				}
			}
		}

		this.getModel().setObservationTargetForm(targetForm);
	}

	@Override
	public void reload(Database db) throws Exception
	{
		ObservationElementDTO observationElementDTO = this.loadObservationElementDTO(db);

		if (observationElementDTO != null)
		{
			ObservationTargetForm targetForm = new ObservationTargetForm();
			this.populateTargetForm(targetForm, observationElementDTO);

			this.getModel().setObservationElementDTO(observationElementDTO);
			this.getModel().setObservationTargetForm(targetForm);

			PhenoService phenoService = new PhenoService(db);
			this.getModel().setProtocolDTOList(phenoService.findProtocols());
		}
	}
}

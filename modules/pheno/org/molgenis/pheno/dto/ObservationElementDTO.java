package org.molgenis.pheno.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ObservationElementDTO implements Serializable {
	/* The serial version UID of this class. Needed for serialization. */
	private static final long serialVersionUID = 7524289064806520907L;

	private Integer observationElementId;
	private List<ProtocolDTO> protocolList = new ArrayList<ProtocolDTO>();
	// (protocolName, List<ObservedValueVO>)
	private HashMap<String, HashMap<String, List<ObservedValueDTO>>> observedValues = new HashMap<String, HashMap<String, List<ObservedValueDTO>>>();
	private List<ObservedValueDTO> observedValueDTOList = new ArrayList<ObservedValueDTO>();

	public Integer getObservationElementId() {
		return observationElementId;
	}

	public void setObservationElementId(Integer observationElementId) {
		this.observationElementId = observationElementId;
	}

	public List<ProtocolDTO> getProtocolList() {
		return protocolList;
	}

	public void setProtocolList(List<ProtocolDTO> protocolList) {
		this.protocolList = protocolList;
	}

	public HashMap<String, HashMap<String, List<ObservedValueDTO>>> getObservedValues() {
		return observedValues;
	}

	public void setObservedValues(
			HashMap<String, HashMap<String, List<ObservedValueDTO>>> featureValues) {
		this.observedValues = featureValues;
	}

	public List<ObservedValueDTO> getObservedValueDTOList() {
		return observedValueDTOList;
	}

	public void setObservedValueDTOList(
			List<ObservedValueDTO> observedValueDTOList) {
		this.observedValueDTOList = observedValueDTOList;
	}
}

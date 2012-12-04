package org.molgenis.omx.hl7;

import org.molgenis.observ.ObservableFeature;
import org.molgenis.observ.ObservationSet;
import org.molgenis.observ.ObservedValue;
import org.molgenis.observ.target.OntologyTerm;
import org.molgenis.omx.generated.ANY;
import org.molgenis.omx.generated.BL;
import org.molgenis.omx.generated.CD;
import org.molgenis.omx.generated.INT;
import org.molgenis.omx.generated.PQ;
import org.molgenis.omx.generated.REAL;
import org.molgenis.omx.generated.REPCMT000100UV01Observation;
import org.molgenis.omx.generated.ST;
import org.molgenis.omx.generated.TS;

public class HL7ObservationConvertor
{
	private HL7ObservationConvertor()
	{
	}

	public static String toObservableFeatureIdentifier(REPCMT000100UV01Observation observation)
	{
		CD code = observation.getCode();
		return code.getCodeSystem() + '.' + code.getCode();
	}

	public static ObservableFeature toObservableFeature(REPCMT000100UV01Observation observation)
	{
		ObservableFeature feature = new ObservableFeature();
		feature.setIdentifier(toObservableFeatureIdentifier(observation));
		feature.setName(observation.getCode().getDisplayName());

		// determine data type
		ANY anyValue = observation.getValue();
		String dataType = HL7DataTypeMapper.get(anyValue);
		if (dataType == null) throw new RuntimeException("HL7 data type not supported: "
				+ anyValue.getClass().getSimpleName());
		feature.setDataType(dataType);

		// determine unit
		if (anyValue instanceof PQ)
		{
			PQ value = (PQ) anyValue;

			OntologyTerm ontologyTerm = new OntologyTerm();
			ontologyTerm.setIdentifier(value.getUnit());
			ontologyTerm.setName(value.getUnit());
			feature.setUnit(ontologyTerm);
		}

		return feature;
	}

	public static String toOntologyTermIdentifier(REPCMT000100UV01Observation observation)
	{
		ANY anyValue = observation.getValue();
		if (anyValue instanceof PQ)
		{
			PQ value = (PQ) anyValue;
			return value.getUnit();
		}

		return null;
	}

	public static OntologyTerm toOntologyTerm(REPCMT000100UV01Observation observation)
	{
		OntologyTerm ontologyTerm = null;

		ANY anyValue = observation.getValue();
		if (anyValue instanceof PQ)
		{
			PQ value = (PQ) anyValue;

			ontologyTerm = new OntologyTerm();
			ontologyTerm.setIdentifier(value.getUnit());
			ontologyTerm.setName(value.getUnit());
		}
		return ontologyTerm;
	}

	public static ObservedValue toObservedValue(REPCMT000100UV01Observation observation, ObservableFeature feature,
			ObservationSet observationSet)
	{
		ObservedValue observedValue = new ObservedValue();
		observedValue.setFeature(feature);
		observedValue.setObservationSet(observationSet);

		ANY anyValue = observation.getValue();
		if (anyValue instanceof INT)
		{
			// integer
			INT value = (INT) anyValue;
			observedValue.setValue(value.getValue().toString());
		}
		else if (anyValue instanceof ST)
		{
			// string
			ST value = (ST) anyValue;
			observedValue.setValue(value.getRepresentation().value());
		}
		else if (anyValue instanceof PQ)
		{
			// physical quantity
			PQ value = (PQ) anyValue;
			observedValue.setValue(value.getValue());
		}
		else if (anyValue instanceof TS)
		{
			// time
			TS value = (TS) anyValue;
			observedValue.setValue(value.getValue());
		}
		else if (anyValue instanceof REAL)
		{
			// fractional number
			REAL value = (REAL) anyValue;
			observedValue.setValue(value.getValue());
		}
		else if (anyValue instanceof BL)
		{
			// boolean
			BL value = (BL) anyValue;
			observedValue.setValue(value.isValue().toString());
		}
		else
		{
			throw new RuntimeException("ANY instance not supported");
		}

		return observedValue;
	}
}

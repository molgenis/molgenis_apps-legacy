package org.molgenis.omx.hl7;

import org.molgenis.observ.ObservableFeature;
import org.molgenis.observ.ObservationSet;
import org.molgenis.observ.ObservedValue;
import org.molgenis.omx.generated.COCTMT050000UV01Patient;
import org.molgenis.omx.generated.II;
import org.molgenis.omx.generated.REPCMT000100UV01RecordTarget;

public class HL7RecordTargetConvertor
{
	private static final String FEATURE_NAME = "patient";

	private HL7RecordTargetConvertor()
	{
	}

	public static ObservableFeature toObservableFeature(REPCMT000100UV01RecordTarget recordTarget)
	{
		COCTMT050000UV01Patient patient = recordTarget.getPatient().getValue();
		II id = patient.getId().iterator().next();

		ObservableFeature feature = new ObservableFeature();
		feature.setIdentifier(id.getRoot());
		feature.setName(FEATURE_NAME);
		return feature;
	}

	public static String toObservableFeatureIdentifier(REPCMT000100UV01RecordTarget recordTarget)
	{
		COCTMT050000UV01Patient patient = recordTarget.getPatient().getValue();
		II id = patient.getId().iterator().next();
		return id.getRoot();
	}

	public static ObservedValue toObservedValue(REPCMT000100UV01RecordTarget recordTarget, ObservableFeature feature,
			ObservationSet observationSet)
	{
		COCTMT050000UV01Patient patient = recordTarget.getPatient().getValue();
		II id = patient.getId().iterator().next();

		ObservedValue value = new ObservedValue();
		value.setFeature(feature);
		value.setObservationSet(observationSet);
		value.setValue(id.getExtension());
		return value;
	}
}

package org.molgenis.omx.hl7;

import org.molgenis.observ.ObservationTarget;
import org.molgenis.omx.generated.COCTMT050000UV01Patient;
import org.molgenis.omx.generated.II;
import org.molgenis.omx.generated.REPCMT000100UV01RecordTarget;

public class HL7RecordTargetConvertor
{
	private HL7RecordTargetConvertor()
	{
	}

	public static ObservationTarget toObservationTarget(REPCMT000100UV01RecordTarget recordTarget)
	{
		COCTMT050000UV01Patient patient = recordTarget.getPatient().getValue();
		II id = patient.getId().iterator().next();

		ObservationTarget target = new ObservationTarget();
		target.setIdentifier(id.getRoot() + '.' + id.getExtension());
		target.setName(id.getExtension());
		return target;
	}
}

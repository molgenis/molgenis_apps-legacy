package org.omicsconnect.io.hl7;

import java.util.ArrayList;
import java.util.HashMap;

import org.omicsconnect.io.hl7.generic.HL7GenericDCM;
import org.omicsconnect.io.hl7.generic.HL7OrganizerDCM;
import org.omicsconnect.io.hl7.generic.HL7ValueSetDCM;
import org.omicsconnect.io.hl7.lra.HL7OrganizerLRA;
import org.omicsconnect.io.hl7.lra.HL7StageLRA;
import org.omicsconnect.io.hl7.lra.HL7ValueSetLRA;

/**
 * 
 * @author roankanninga
 */
interface HL7Data
{

	//
	ArrayList<HL7OrganizerLRA> getHL7OrganizerLRA();

	ArrayList<HL7OrganizerDCM> getHL7OrganizerDCM();

	HashMap<String, HL7ValueSetLRA> getHashValueSetLRA();

	HashMap<String, HL7ValueSetDCM> getHashValueSetDCM();

	HL7GenericDCM getHl7GenericDCM();

	HL7StageLRA getHl7StageLRA();

}

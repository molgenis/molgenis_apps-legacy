package org.molgenis.omx.hl7;

import org.molgenis.observ.Protocol;
import org.molgenis.omx.generated.CD;
import org.molgenis.omx.generated.REPCMT000100UV01Organizer;

public class HL7OrganizerConvertor
{
	private HL7OrganizerConvertor()
	{
	}

	public static Protocol toProtocol(REPCMT000100UV01Organizer organizer)
	{
		CD code = organizer.getCode();
		Protocol protocol = new Protocol();
		protocol.setIdentifier(code.getCodeSystem() + '.' + code.getCode());
		protocol.setName(code.getDisplayName());
		return protocol;
	}
}

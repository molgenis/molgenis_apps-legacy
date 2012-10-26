package org.molgenis.bbmriOmicsconnect;

import org.molgenis.Molgenis;

public class bbmriOmicsconnectGenerate
{
	public static void main(String[] args) throws Exception
	{
		new Molgenis("apps/bbmriOmicsconnect/org/molgenis/bbmriOmicsconnect/bbmriOmicsconnect.properties").generate();
	}
}

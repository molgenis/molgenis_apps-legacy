package org.molgenis.cbm_omx;

import org.molgenis.Molgenis;

public class Cbm_omxUpdateDatabase
{
	public static void main(String[] args) throws Exception
	{
		new Molgenis("apps/cbm_omx/org/molgenis/cbm_omx/cbm_omx.properties").updateDb(false);
	}
}
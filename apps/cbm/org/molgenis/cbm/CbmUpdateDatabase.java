package org.molgenis.cbm;

import org.molgenis.Molgenis;

//import cmdline.CmdLineException;

public class CbmUpdateDatabase
{
	public static void main(String[] args) throws Exception
	{
		new Molgenis("apps/cbm/org/molgenis/cbm/cbm.properties").updateDb();
	}
}

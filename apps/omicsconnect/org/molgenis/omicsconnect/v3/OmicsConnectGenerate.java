package org.molgenis.omicsconnect.v3;

import org.molgenis.Molgenis;

public class OmicsConnectGenerate
{
	public static void main(String[] args) throws Exception
	{
		new Molgenis("apps/omicsconnect/org/molgenis/omicsconnect/v3/omicsconnect.properties").generate();

		// /molgenis_apps/apps/gwascentral/org/molgenis/hgvbaseg2p/hgvbase.properties

	}
}
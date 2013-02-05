package org.molgenis.gids;

import org.molgenis.Molgenis;

public class GidsGenerate
{
	public static void main(String[] args) throws Exception
	{
		new Molgenis("apps/gids/org/molgenis/gids/gids.properties").generate();

		// /molgenis_apps/apps/gwascentral/org/molgenis/hgvbaseg2p/hgvbase.properties

	}
}
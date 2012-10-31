package org.molgenis.bioshare;

import org.molgenis.Molgenis;

public class bioshareGenerate
{
	public static void main(String[] args) throws Exception
	{
		// new
		// Molgenis("apps/catalogue/org/molgenis/catalogue/catalogue.molgenis.properties",
		// UsedMolgenisOptionsGen.class).generate();
		new Molgenis("apps/bioshare/org/molgenis/bioshare/bioshare.molgenis.properties").generate();
	}
}

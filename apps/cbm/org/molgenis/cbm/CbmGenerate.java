package org.molgenis.cbm;

import org.molgenis.Molgenis;

/**
 * Generates the MOLGENIS application from the *db.xml and *ui.xml as set in
 * molgenis.properties
 */
public class CbmGenerate
{
	public static void main(String[] args) throws Exception
	{
		new Molgenis("apps/cbm/org/molgenis/cbm/cbm.properties").generate();
	}
}

package org.molgenis.omicsconnect.researchportal;

import org.molgenis.Molgenis;

public class OmicsConnectUpdatedbResearchPortal
{

	/**
	 * 
	 * Experimental GUI, development mock-up. Uses the same build settings as
	 * the regular Omics Connect.
	 * 
	 */
	public static void main(String[] args) throws Exception
	{
		try
		{
			new Molgenis("apps/omicsconnect/org/molgenis/omicsconnect/researchportal/omicsconnect.properties")
					.updateDb();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}

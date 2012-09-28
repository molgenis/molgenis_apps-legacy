package org.molgenis.omicsconnect.minigui;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.molgenis.Molgenis;

public class OmicsConnectGenerateMiniGUI
{

	/**
	 * 
	 * Experimental GUI, development mock-up.
	 * 
	 * Add to build path: modules/minigui, modules/file, modules/decorators/code
	 * 
	 */
	public static void main(String[] args) throws Exception
	{
		try
		{

			File src = new File("apps/omicsconnect/org/molgenis/omicsconnect/minigui/MiniApplicationView.ftl");
			File dest = new File("../molgenis/src/org/molgenis/framework/ui/ApplicationView.ftl");
			FileUtils.copyFile(src, dest);

			new Molgenis("apps/omicsconnect/org/molgenis/omicsconnect/minigui/omicsconnect_minigui.properties")
					.generate();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}

package org.molgenis.plantqtl;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.molgenis.Molgenis;

public class PlantqtlGenerate
{
	public static void main(String[] args) throws Exception
	{
		try
		{
			FileUtils.deleteDirectory(new File("hsqldb"));
			new Molgenis("apps/wormqtl/org/molgenis/plantqtl/plantqtl.properties").generate();

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}

<<<<<<< HEAD
package plugins.ConvertGIDStoOMX;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ConvertGidsMain
{
	private final static String OUTPUTDIR = "/Users/Roan/github/molgenis_apps/apps/gids/testoutput/testData";

	public static void main(String[] args) throws IOException
	{

		SampleConverter sample = new SampleConverter();
		InputStream is = new FileInputStream(
				"/Users/Roan/github/molgenis_apps/apps/gids/org/molgenis/gids/testInput.xls");

		OutputStream os = new FileOutputStream(OUTPUTDIR + "Output.csv");

		// SampleConverter sample = new SampleConverter();
		// InputStream is = new
		// FileInputStream("/Users/Roan/Work/NewGIDS/Export GIDS/Cohorts/Celiac Sprue.xls");
		//
		// OutputStream os = new FileOutputStream(
		// "/Users/Roan/Work/NewGIDS/Export GIDS/Cohorts/Converted/CeliacSprueData.csv");

		sample.convert(is, os, OUTPUTDIR);

	}

=======
package plugins.ConvertGIDStoOMX;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ConvertGidsMain
{
	public static void main(String[] args) throws IOException
	{
		SampleConverter sample = new SampleConverter();
		InputStream is = new FileInputStream("/Users/Roan/Work/NewGIDS/Export GIDS/Cohorts/Celiac Sprue.xls");

		OutputStream os = new FileOutputStream(
				"/Users/Roan/Work/NewGIDS/Export GIDS/Cohorts/Converted/CeliacSprueData.csv");
		OutputStream osMD = new FileOutputStream(
				"/Users/Roan/Work/NewGIDS/Export GIDS/Cohorts/Converted/CeliacMetaData.csv");

		sample.convert(is, os, osMD);

	}

=======
package plugins.ConvertGIDStoOMX;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ConvertGidsMain
{
	public static void main(String[] args) throws IOException
	{
		SampleConverter sample = new SampleConverter();
		InputStream is = new FileInputStream("/Users/Roan/Work/NewGIDS/Export GIDS/Cohorts/Celiac Sprue.xls");

		OutputStream os = new FileOutputStream(
				"/Users/Roan/Work/NewGIDS/Export GIDS/Cohorts/Converted/CeliacSprueData.csv");
		OutputStream osMD = new FileOutputStream(
				"/Users/Roan/Work/NewGIDS/Export GIDS/Cohorts/Converted/CeliacMetaData.csv");

		sample.convert(is, os, osMD);

	}
>>>>>>> 7b43b717aa01132a3984cc663a32c7174785ada5
}
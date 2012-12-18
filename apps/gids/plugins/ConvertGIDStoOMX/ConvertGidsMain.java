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

}
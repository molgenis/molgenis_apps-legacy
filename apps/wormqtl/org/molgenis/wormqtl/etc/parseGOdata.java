package org.molgenis.wormqtl.etc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class parseGOdata
{

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException
	{
		File go = new File("goterms_wormbaseids.txt");
		File id = new File("wormqtl_geneslist.txt");

		File out = new File("wormqtl_geneslist_go.txt");

		Scanner gosc = new Scanner(go);

		HashMap<String, String> idToGo = new HashMap<String, String>();

		while (gosc.hasNext())
		{
			String s = gosc.nextLine();
			// System.out.print(".");
			String[] split = s.split("\t");

			if (idToGo.containsKey(split[1]))
			{
				idToGo.put(split[1], idToGo.get(split[1]) + " / " + split[2] + "-" + split[3]);
			}
			else
			{
				idToGo.put(split[1], split[2] + "-" + split[3]);
			}
		}

		Scanner idsc = new Scanner(id);

		FileWriter fstream = new FileWriter(out);
		BufferedWriter write = new BufferedWriter(fstream);

		while (idsc.hasNext())
		{
			String s = idsc.nextLine();

			if (idToGo.containsKey(s))
			{
				write.write(s + "\t" + idToGo.get(s) + "\n");
			}
			else
			{
				write.write(s + "\t" + "\n");
			}

		}

		write.close();

	}

}

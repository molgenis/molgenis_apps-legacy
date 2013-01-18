package org.molgenis.plantqtl.etc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.molgenis.xgap.Chromosome;

public class MatchAnnotations
{

	/**
	 * "Script" to help format data. Read in a file with only names (of eg.
	 * genes) in a single column, that are in the order as you want to keep
	 * them. Read a second file with the format "name [tab] [description]",
	 * match the names, and add the [description] to the names in the original
	 * order. Output result to a file.
	 */
	public MatchAnnotations() throws Exception
	{
		File names = new File("/Users/jvelde/data/etc/brassica_genes.tsv");
		File annotations = new File("/Users/jvelde/data/etc/tair_at_ids_annotation.txt");
		File out = new File("/Users/jvelde/data/etc/out.tsv");
		
		Scanner namesReader = new Scanner(names);
		ArrayList<String> namesList = new ArrayList<String>();

		while(namesReader.hasNextLine())
		{
			namesList.add(namesReader.nextLine());
		}
		
		Scanner annotationsReader = new Scanner(annotations);
		Map<String, String> annotationsMap = new HashMap<String, String>();
		
		while(annotationsReader.hasNextLine())
		{
			String line = annotationsReader.nextLine();
			String name = line.split("\t")[0];
			String desc = line.split("\t")[1];
			annotationsMap.put(name, desc);
		}
		
		FileWriter fstream = new FileWriter(out);
		BufferedWriter write = new BufferedWriter(fstream);
		
		for(String name : namesList)
		{
			write.write(name + "\t" + (annotationsMap.get(name) == null ? "" : annotationsMap.get(name)) + "\n");
		}
		write.close();
		
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception
	{
		new MatchAnnotations();

	}

	public List<Chromosome> makeChrBpLenghtCumulative(List<Chromosome> chromosomes) throws Exception
	{
		List<Chromosome> result = new ArrayList<Chromosome>();
		// result.addAll(arg0)

		// start accumulation with 0 on the chromosome with the first order nr
		// then add bp length of chr 1 on chr2, etc
		int cumulativeBp = 0;

		// find the other number for every chromosome
		// must start at 1 and have a unique number for each
		for (int i = 1; i <= chromosomes.size(); i++)
		{
			boolean chrForOrderNrFound = false;
			for (Chromosome chr : chromosomes)
			{
				if (chr.getOrderNr().equals(i))
				{
					chrForOrderNrFound = true;
					cumulativeBp += chr.getBpLength();
					Chromosome cumulativeBpChr = new Chromosome(chr);
					cumulativeBpChr.setBpLength(cumulativeBp);
					result.add(cumulativeBpChr);
					System.out.println("added cumulative bp chromosome: " + cumulativeBpChr);
				}
			}
			if (!chrForOrderNrFound)
			{
				throw new Exception("Chromosome for order nr " + i + " not found!");
			}
		}
		return result;
	}
}

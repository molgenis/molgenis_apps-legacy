package org.molgenis.plantqtl.etc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.molgenis.io.csv.CsvReader;
import org.molgenis.util.tuple.Tuple;
import org.molgenis.xgap.Chromosome;
import org.molgenis.xgap.Gene;

public class MakeCumulative
{

	/**
	 * "Script" to help format data. It reads in Gene and Chromosome objects
	 * (XGAP datamodel) and uses the chromosome order number and bp length to
	 * create cumulative lengths for the chromosomes - these lenghts are then
	 * used to make the bp start and end locations of the genes also cumulative.
	 * This helps for plotting. (ie. WormQTL) The original bp location is often
	 * stored in the description field.
	 */
	public MakeCumulative() throws Exception
	{
		File genes = new File("/Users/jvelde/data/etc/brassica_genes.tsv");
		File chromosomes = new File("/Users/jvelde/data/etc/brassica_chromosomes.tsv");
		File out = new File("/Users/jvelde/data/etc/out.tsv");

		CsvReader chrReader = new CsvReader(chromosomes, '\t', true);
		List<Chromosome> chrList = new ArrayList<Chromosome>();
		for (Tuple t : chrReader)
		{
			Chromosome c = new Chromosome();
			c.set(t);
			chrList.add(c);
		}
		chrReader.close();

		CsvReader geneReader = new CsvReader(genes, '\t', true);
		List<Gene> genesList = new ArrayList<Gene>();
		for (Tuple t : geneReader)
		{
			Gene g = new Gene();
			g.set(t);
			genesList.add(g);
		}
		geneReader.close();

		// make a new list of chromosomes with cumulative bp positions
		// does NOT alter the input list of chromosomes!
		List<Chromosome> cumulativeChrList = makeChrBpLenghtCumulative(chrList);

		// put untouched and cumulative chromosomes into maps by name
		Map<String, Chromosome> nameToChr = new HashMap<String, Chromosome>();
		for (Chromosome c : chrList)
		{
			nameToChr.put(c.getName(), c);
		}
		Map<String, Chromosome> nameToCumulativeChr = new HashMap<String, Chromosome>();
		for (Chromosome c : cumulativeChrList)
		{
			nameToCumulativeChr.put(c.getName(), c);
		}

		FileWriter fstream = new FileWriter(out);
		BufferedWriter write = new BufferedWriter(fstream);

		for (Gene g : genesList)
		{
			System.out.println("checking gene " + g.getName());

			if (g.getChromosome_Name() == null)
			{
				System.out.println("gene " + g.getName() + " does not have a chromosome name, skipping..");
				write.write("\n");
				continue;
			}

			// get the original chromosome bp length
			long chrLenght = nameToChr.get(g.getChromosome_Name()).getBpLength().longValue();

			long cumulativeBpGeneStartLoc = -1;
			if (g.getBpStart() != null)
			{

				// check if it exceeds the current gene bp location (may never
				// happen)
				if (g.getBpStart().longValue() > chrLenght)
				{
					throw new Exception("bp start pos of " + g.getName() + " is greater than length of "
							+ g.getChromosome_Name() + ": " + g.getBpStart() + " > " + chrLenght);
				}

				// if it's ok, get the cumulative bp position of the chromosome
				// is on
				// however, substract the bp length of the chromosome itself
				// or else the gene will be pushed to the 'next' chromosome :)
				// not very pretty to do here, but the alternative is making a
				// cumulative map 'lagging behind'
				// with the first chromosome starting at 0 etc
				cumulativeBpGeneStartLoc = g.getBpStart()
						+ (nameToCumulativeChr.get(g.getChromosome_Name()).getBpLength() - chrLenght);
			}

			// same as above for end location
			long cumulativeBpGeneEndLoc = -1;
			if (g.getBpEnd() != null)
			{
				if (g.getBpEnd().longValue() > chrLenght)
				{
					throw new Exception("bp end pos of " + g.getName() + " is greater than length of "
							+ g.getChromosome_Name() + ": " + g.getBpEnd() + " > " + chrLenght);
				}
				cumulativeBpGeneEndLoc = g.getBpEnd()
						+ (nameToCumulativeChr.get(g.getChromosome_Name()).getBpLength() - chrLenght);
			}

			write.write(g.getName() + "\t" + (cumulativeBpGeneStartLoc == -1 ? "" : cumulativeBpGeneStartLoc) + "\t"
					+ (cumulativeBpGeneEndLoc == -1 ? "" : cumulativeBpGeneEndLoc) + "\n");
		}

		write.close();
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception
	{
		new MakeCumulative();

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

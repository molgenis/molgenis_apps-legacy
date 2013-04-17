package org.molgenis.gonl.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.molgenis.core.OntologyTerm;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.ObservableFeature;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.pheno.Panel;
import org.molgenis.pheno.Species;
import org.molgenis.util.CsvFileWriter;
import org.molgenis.util.CsvWriter;
import org.molgenis.util.Entity;
import org.molgenis.util.tuple.KeyValueTuple;
import org.molgenis.util.tuple.WritableTuple;
import org.molgenis.util.vcf.VcfReader;
import org.molgenis.util.vcf.VcfReaderListener;
import org.molgenis.util.vcf.VcfRecord;
import org.molgenis.variant.Chromosome;
import org.molgenis.variant.GenomeBuild;
import org.molgenis.variant.SequenceVariant;

/**
 * This method can extract cohort level or individual level data from vcf files
 * and convert this into a pheno model compatible data set.
 * 
 * Currently, only aggregate data is exported as observedvalue.
 */
public class VcfToGoNLVariantConverter
{
	private static final Logger logger = Logger.getLogger(VcfToGoNLVariantConverter.class);

	public static final int BATCH_SIZE = 100000;

	public static void main(String[] args) throws Exception
	{

		if (args.length != 4)
		{
			System.err.println("Usage: <input.vcf> <output.dir> <panel_name> <encryption_salt>");
			return;
		}

		BasicConfigurator.configure();

		File vcfFile = new File(args[0]);
		if (!vcfFile.exists()) throw new FileNotFoundException("input does not exist: " + args[0]);
		else if (vcfFile.isDirectory()) throw new IOException("input is a directory: " + args[0]);
		File outputDir = new File(args[1]);
		if (!outputDir.exists()) if (!outputDir.mkdir()) throw new IOException("Could not create directory: "
				+ outputDir);
		else if (!outputDir.isDirectory()) throw new IOException("output directory is not a directory");

		VcfToGoNLVariantConverter convert = new VcfToGoNLVariantConverter(args[2], args[3]);
		convert.convertVariants(vcfFile, outputDir);
	}

	private final String panelName;
	/**
	 * Random data used as additional input for individual name encryption
	 */
	private final String salt;

	public VcfToGoNLVariantConverter(String panelName, String salt)
	{
		if (panelName == null) throw new IllegalArgumentException();
		if (salt == null) throw new IllegalArgumentException();
		this.panelName = panelName;
		this.salt = salt;
	}

	public void convertVariants(final File vcfFile, final File outputDir) throws Exception
	{
		System.out.println("converting aggregate data from vcf=" + vcfFile + " to directory " + outputDir);
		final VcfReader vcf = new VcfReader(vcfFile);

		final List<SequenceVariant> variants = new ArrayList<SequenceVariant>();
		final List<ObservedValue> values = new ArrayList<ObservedValue>();
		final List<String> chromosomes = new ArrayList<String>();
		final List<GenomeBuild> builds = new ArrayList<GenomeBuild>();
		final List<Species> species = new ArrayList<Species>();
		final Map<String, Individual> individuals = new TreeMap<String, Individual>();
		final List<ObservableFeature> features = new ArrayList<ObservableFeature>();
		final Panel panel = new Panel();

		// create file names
		final File fileVariants = new File(outputDir.getAbsolutePath() + File.separatorChar
				+ SequenceVariant.class.getSimpleName() + ".txt");
		final File fileObservedValues = new File(outputDir.getAbsolutePath() + File.separatorChar
				+ ObservedValue.class.getSimpleName() + ".txt");

		// create file headers
		final String[] variantHeaders = new String[]
		{ SequenceVariant.NAME, SequenceVariant.REF, SequenceVariant.ALT, SequenceVariant.CHR_NAME,
				SequenceVariant.STARTBP, SequenceVariant.ENDBP, SequenceVariant.DESCRIPTION,
				SequenceVariant.ALTERNATEID_NAME };

		final String[] ovHeaders = new String[]
		{ ObservedValue.TARGET_NAME, ObservedValue.RELATION_NAME, ObservedValue.FEATURE_NAME, ObservedValue.VALUE };

		// create files
		createFileAndHeader(fileVariants, variantHeaders);
		createFileAndHeader(fileObservedValues, ovHeaders);

		final List<Integer> count = new ArrayList<Integer>();
		count.add(0);

		final String encryptionSalt = this.salt;
		final Map<String, String> encIndividualMap = new HashMap<String, String>();
		vcf.parse(new VcfReaderListener()
		{
			@Override
			public void handleLine(int lineNumber, VcfRecord record) throws Exception
			{
				// create hgvs notation, one record per variant reported (even
				// if they
				// are on same location)
				List<String> alt = record.getAlt();
				for (int i = 0; i < alt.size(); i++)
				{
					SequenceVariant v = new SequenceVariant();
					ObservedValue observedValue = new ObservedValue();

					// TODO result is not used, do we need this?
					// String result = "chr" + record.getChrom() + ":g.";
					v.setName("chr" + record.getChrom() + ":g." + record.getPos() + record.getRef() + ">" + alt.get(i));
					v.setName(v.getName().replace("|", "_"));

					// ref
					v.setRef(record.getRef());

					// alt
					v.setAlt(alt.get(i));

					// chr
					String chromName = record.getChrom().replace("|", "_");
					v.setChr_Name(chromName);

					// check if chrom exists, otherwise add
					System.out.println(">>>>" + chromosomes);
					if (!chromosomes.contains(chromName)) chromosomes.add(chromName);

					// pos
					v.setStartBP(record.getPos());
					v.setEndBP(record.getPos());

					v.setDescription("" + record.getId());

					// put alt allele counts in description
					System.out.println(vcf.getInfoFields());
					for (int j = 0; j != vcf.getInfoFields().size(); j++)
					{
						System.out.println(vcf.getInfoFields().get(j));
						if (record.getInfo(vcf.getInfoFields().get(j)) != null)
						{
							// System.out.println(record.getInfo(vcf.getInfoFields().get(j)).get(i));
							List<String> var3 = vcf.getInfoFields();
							String key = var3.get(j);
							List<String> info = record.getInfo(key);
							if (info == null | info.isEmpty()) observedValue.setValue(info.get(0));
							else
								logger.warn("unknown key: " + key);

						}
					}

					for (String individualName : vcf.getSampleList())
					{
						// encrypt individual name
						String encIndividualName = encrypt(individualName, encryptionSalt);

						encIndividualMap.put(individualName, encIndividualName);

						// create individual
						Individual individual = individuals.get(encIndividualName);
						if (individual == null)
						{
							individual = new Individual();
							individual.setName(encIndividualName);
							individuals.put(encIndividualName, individual);
						}

						// create genotype for patient-mutation
						observedValue.setTarget_Name(panelName);
						observedValue.setRelation_Name("Allele count");
						observedValue.setFeature_Name(v.getName());

						observedValue.setValue(record.getSampleValue(individualName, "GT"));
					}

					variants.add(v);
					values.add(observedValue);
				}

				if (variants.size() >= BATCH_SIZE)
				{
					writeBatch(variants, fileVariants, variantHeaders);
					variants.clear();
					writeBatch(values, fileObservedValues, ovHeaders);
					values.clear();

					count.set(0, count.get(0) + BATCH_SIZE);

					System.out.println(new Date() + " converted variants:" + count.get(0));
				}
			}
		});

		// write individual encryption information
		writeEncryptedIndividuals(outputDir, encIndividualMap);

		// write remaining data for last batch.
		writeBatch(variants, fileVariants, variantHeaders);
		writeBatch(values, fileObservedValues, ovHeaders);

		// write chromsomes
		List<Chromosome> chrList = new ArrayList<Chromosome>();
		int order = 0;
		for (String chr : chromosomes)
		{
			Chromosome c = new Chromosome();
			c.setName(chr);
			c.setGenomeBuild_Name("hg19");
			c.setOrderNr(++order);

			if (chr.matches("[a-zA-Z]+"))
			{
				c.setIsAutosomal(false);
			}
			else if (chr.startsWith("UN"))
			{
				c.setIsAutosomal(null);
			}
			else
			{
				c.setIsAutosomal(true);
			}

			chrList.add(c);
		}

		// write dbXrefs
		List<OntologyTerm> ontoList = new ArrayList<OntologyTerm>();

		// add ontlogy terms
		Species s = new Species();
		s.setName("homo sapiens");
		species.add(s);

		File chrFile = new File(outputDir.getAbsolutePath() + File.separatorChar + Chromosome.class.getSimpleName()
				+ ".txt");
		String[] chrHeader = new String[]
		{ Chromosome.NAME, Chromosome.GENOMEBUILD_NAME, Chromosome.ORDERNR, Chromosome.ISAUTOSOMAL };
		createFileAndHeader(chrFile, chrHeader);
		writeBatch(chrList, chrFile, chrHeader);

		File ontoFile = new File(outputDir.getAbsolutePath() + File.separatorChar + OntologyTerm.class.getSimpleName()
				+ ".txt");
		String[] ontoHeader = new String[]
		{ "name" };
		createFileAndHeader(ontoFile, ontoHeader);
		writeBatch(ontoList, ontoFile, ontoHeader);

		GenomeBuild build = new GenomeBuild();
		build.setSpecies_Name("homo sapiens");
		build.setName("hg19");
		builds.add(build);

		final File buildsFile = new File(outputDir.getAbsolutePath() + File.separatorChar
				+ GenomeBuild.class.getSimpleName() + ".txt");
		String[] buildsHeader = new String[]
		{ "name", "species_name" };
		createFileAndHeader(buildsFile, buildsHeader);
		writeBatch(builds, buildsFile, buildsHeader);

		panel.setName(this.panelName);
		panel.setIndividuals(new ArrayList<Individual>(individuals.values()));

		final File panelFile = new File(outputDir.getAbsolutePath() + File.separatorChar + Panel.class.getSimpleName()
				+ ".txt");
		String[] panelHeader = new String[]
		{ "name", "individuals_name" };
		createFileAndHeader(panelFile, panelHeader);
		writeBatch(Collections.singletonList(panel), panelFile, panelHeader);

		final File individualFile = new File(outputDir.getAbsolutePath() + File.separatorChar
				+ Individual.class.getSimpleName() + ".txt");
		String[] patientHeader = new String[]
		{ "name", "phenotype", "submission_identifier", "mutations_name" };
		createFileAndHeader(individualFile, patientHeader);
		writeBatch(new ArrayList<Individual>(individuals.values()), individualFile, patientHeader);

		ObservableFeature f = new ObservableFeature();
		f.setName("Allele count");
		features.add(f);

		final File featureFile = new File(outputDir.getAbsolutePath() + File.separatorChar
				+ ObservableFeature.class.getSimpleName() + ".txt");
		String[] featureHeader = new String[]
		{ "name" };
		createFileAndHeader(featureFile, featureHeader);
		writeBatch(features, featureFile, featureHeader);

		final File speciesFile = new File(outputDir.getAbsolutePath() + File.separatorChar
				+ Species.class.getSimpleName() + ".txt");
		String[] speciesHeader = new String[]
		{ "name" };
		createFileAndHeader(speciesFile, speciesHeader);
		writeBatch(species, speciesFile, speciesHeader);

	}

	private String encrypt(String value, String salt)
	{
		return DigestUtils.md5Hex(value + salt);
	}

	private void writeEncryptedIndividuals(File outputDir, Map<String, String> encIndividualMap) throws IOException
	{
		String colIndividualName = "individual_name";
		String colIndividualNameMD5Salted = "individual_name_md5_salted";

		File patientFile = new File(outputDir.getAbsolutePath() + File.separatorChar + "encrypted_individuals.csv");
		org.molgenis.io.csv.CsvWriter csvWriter = new org.molgenis.io.csv.CsvWriter(patientFile);
		try
		{
			csvWriter.writeColNames(Arrays.asList(colIndividualName, colIndividualNameMD5Salted));
			for (Entry<String, String> entry : encIndividualMap.entrySet())
			{
				WritableTuple tuple = new KeyValueTuple();
				tuple.set(colIndividualName, entry.getKey());
				tuple.set(colIndividualNameMD5Salted, entry.getValue());
				csvWriter.write(tuple);
			}
		}
		finally
		{
			csvWriter.close();
		}
	}

	private void createFileAndHeader(File file, String[] fields) throws IOException
	{
		CsvWriter writer = new CsvFileWriter(file, Arrays.asList(fields));
		writer.setSeparator(',');
		try
		{
			writer.writeHeader();
		}
		finally
		{
			writer.close();
		}
	}

	private void writeBatch(List<? extends Entity> entities, File file, String[] fields) throws IOException
	{
		if (entities.size() > 0)
		{
			System.out.println("Writing to " + file);

			// create appending csvWriter using the selected headers
			CsvWriter writer = new CsvFileWriter(file, Arrays.asList(fields), true);
			writer.setSeparator(',');
			try
			{
				// write batch to csv
				for (Entity e : entities)
				{
					writer.writeRow(e);
				}
			}
			finally
			{
				writer.close();
			}
		}
	}
}

package org.molgenis.patho;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.jackrabbit.uuid.UUID;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.molgenis.core.OntologyTerm;
import org.molgenis.pheno.ObservableFeature;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.pheno.Species;
import org.molgenis.submission.Submission;
import org.molgenis.util.CsvFileWriter;
import org.molgenis.util.CsvWriter;
import org.molgenis.util.Entity;
import org.molgenis.util.vcf.VcfReader;
import org.molgenis.util.vcf.VcfReaderListener;
import org.molgenis.util.vcf.VcfRecord;
import org.molgenis.variant.Chromosome;
import org.molgenis.variant.GenomeBuild;
import org.molgenis.variant.Patient;
import org.molgenis.variant.Variant;

/**
 * This method can extract cohort level or individual level data from vcf files
 * and convert this into a pheno model compatible data set.
 * 
 * Currently, only aggregate data is exported as observedvalue.
 */
public class ConvertVcfToPatho
{
	private static final Logger logger = Logger.getLogger(ConvertVcfToPatho.class);

	public static final int BATCH_SIZE = 100000;

	public static void main(String[] args) throws Exception
	{
		BasicConfigurator.configure();

		File vcfFile = null, outputDir = null;

		if (args.length == 2)
		{

		}
		else
		{
			vcfFile = new File(
					"/Users/erwin/Documents/test_S0_L001_R1_001_converted_Unique_Output_MutationReport_CARDIO.vcf");
			outputDir = new File("/Users/erwin/Documents/vcf");
		}

		ConvertVcfToPatho convert = new ConvertVcfToPatho();
		convert.convertVariants(vcfFile, outputDir);
	}

	public void convertVariants(final File vcfFile, final File outputDir) throws Exception
	{
		System.out.println("converting aggregate data from vcf=" + vcfFile + " to directory " + outputDir);
		final VcfReader vcf = new VcfReader(vcfFile);

		final Submission submission = new Submission();
		submission.setIdentifier(UUID.randomUUID().toString());

		SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy");
		String date = sdf.format(new Date());
		submission.setDate(date);
		submission.setReleasedate(date);

		final List<Variant> variants = new ArrayList<Variant>();
		final List<ObservedValue> values = new ArrayList<ObservedValue>();
		final List<String> chromosomes = new ArrayList<String>();
		final List<String> dbXrefs = new ArrayList<String>();
		final List<GenomeBuild> builds = new ArrayList<GenomeBuild>();
		final List<Species> species = new ArrayList<Species>();
		final Map<String, Patient> patients = new TreeMap<String, Patient>();
		final List<ObservableFeature> features = new ArrayList<ObservableFeature>();

		// Create target dir for patient so we can import the patient first.
		// We need a patient before we can import the ObservedValues
		File targetDir = new File(outputDir.getAbsolutePath(), "target");
		if (!targetDir.exists())
		{
			targetDir.mkdir();
		}

		// create file names
		final File fileVariants = new File(targetDir, "Variant.txt");
		final File fileObservedValues = new File(outputDir.getAbsolutePath() + File.separatorChar + "ObservedValue.txt");

		// create file headers
		final String[] variantHeaders = new String[]
		{ Variant.NAME, Variant.CHROMOSOME_NAME, Variant.STARTGDNA, Variant.ENDGDNA, Variant.RESIDUES,
				Variant.ALTRESIDUES, Variant.DESCRIPTION, Variant.ALTERNATEID_NAME };

		final String[] ovHeaders = new String[]
		{ ObservedValue.TARGET_NAME, ObservedValue.RELATION_NAME, ObservedValue.FEATURE_NAME, ObservedValue.VALUE };

		// create files
		createFileAndHeader(fileVariants, variantHeaders);
		createFileAndHeader(fileObservedValues, ovHeaders);

		final List<Integer> count = new ArrayList<Integer>();
		count.add(0);

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
					Variant v = new Variant();
					ObservedValue o = new ObservedValue();

					String result = "chr" + record.getChrom() + ":g.";
					v.setName("chr" + record.getChrom() + ":g." + record.getPos() + record.getRef() + ">" + alt.get(i));
					v.setName(v.getName().replace("|", "_"));

					// ref
					v.setResidues(record.getRef());

					// alt
					v.setAltResidues(alt.get(i));

					// chr
					String chromName = record.getChrom().replace("|", "_");
					v.setChromosome_Name(chromName);

					// check if chrom exists, otherwise add
					System.out.println(">>>>" + chromosomes);
					if (!chromosomes.contains(chromName)) chromosomes.add(chromName);

					// pos
					v.setStartGdna(record.getPos());
					v.setEndGdna(record.getPos());

					// dbrefs name
					// if (record.getId().size() > 0
					// && !".".equals(record.getId().get(0)))
					// {
					// v.setDbRefs_Name(record.getId());
					// for (String ref : record.getId())
					// {
					// if (!dbXrefs.contains(ref)) dbXrefs.add(ref);
					// }
					// }
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
							if (info == null | info.isEmpty()) o.setValue(info.get(0));
							else
								logger.warn("unknown key: " + key);

						}
						// o.setValue(record.getInfo("AC").get(i));
					}
					// TODO: fetch panel and relation from VCF if possible...
					// and create it first, so we can use it here.

					for (String patientName : vcf.getSampleList())
					{
						// create patient object if missing
						Patient patientObject = patients.get(patientName);
						if (patientObject == null)
						{
							patientObject = new Patient();
							patientObject.setName(patientName);
							patientObject.setSubmission_Identifier(submission.getIdentifier());
							patientObject.setPhenotype("dummy");
							// List<String> mutations_name =
							// record.getSampleValue(pateintName, key)
							// patientObject.setMutations_Name(mutations_name);
							//
							if (!patients.containsKey(patientName)) patients.put(patientName, patientObject);
							// patientObject.setMutations_Name()
						}
						// link mutation to patient
						if (!patientObject.getMutations_Name().contains(v.getName())) patientObject.getMutations_Name()
								.add(v.getName());

						// create genotype for patient-mutation
						o.setTarget_Name(patientName);
						o.setRelation_Name("Allele count");
						o.setFeature_Name(v.getName());

						o.setValue(record.getSampleValue(patientName, "GT"));
					}

					variants.add(v);
					values.add(o);
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

			// if (chr.matches("^(\\d+.*_-\\d+.*)"))
			// {
			// c.setIsAutosomal("yes");

			// }
			if (chr.matches("[a-zA-Z]+"))
			{
				c.setIsAutosomal("no");
			}
			// else if (chr.matches("^CH?R?"))
			// {
			// c.setIsAutosomal("no");
			// }
			else if (chr.startsWith("UN"))
			{
				c.setIsAutosomal("unknown");
			}
			else
			{
				c.setIsAutosomal("yes");
			}

			chrList.add(c);
		}

		// write dbXrefs
		List<OntologyTerm> ontoList = new ArrayList<OntologyTerm>();
		// for (String dbXref : dbXrefs)
		// {
		// OntologyTerm t = new OntologyTerm();
		// t.setName(dbXref);
		// ontoList.add(t);
		// }

		// add ontlogy terms
		Species s = new Species();
		s.setName("homo sapiens");
		species.add(s);

		File chrFile = new File(targetDir, "Chromosome.txt");
		String[] chrHeader = new String[]
		{ Chromosome.NAME, Chromosome.GENOMEBUILD_NAME, Chromosome.ORDERNR, Chromosome.ISAUTOSOMAL };
		createFileAndHeader(chrFile, chrHeader);
		writeBatch(chrList, chrFile, chrHeader);

		File ontoFile = new File(outputDir.getAbsolutePath() + File.separatorChar + "OntologyTerm.txt");
		String[] ontoHeader = new String[]
		{ "name" };
		createFileAndHeader(ontoFile, ontoHeader);
		writeBatch(ontoList, ontoFile, ontoHeader);

		GenomeBuild build = new GenomeBuild();
		build.setSpecies_Name("homo sapiens");
		build.setName("hg19");
		builds.add(build);

		final File buildsFile = new File(targetDir, "GenomeBuild.txt");
		String[] buildsHeader = new String[]
		{ "name", "species_name" };
		createFileAndHeader(buildsFile, buildsHeader);
		writeBatch(builds, buildsFile, buildsHeader);

		final File submissionFile = new File(targetDir, "Submission.txt");
		String[] submissionHeader = new String[]
		{ "identifier", "date_", "releasedate" };
		createFileAndHeader(submissionFile, submissionHeader);
		writeBatch(Arrays.asList(submission), submissionFile, submissionHeader);

		final File patientFile = new File(targetDir, "Patient.txt");
		String[] patientHeader = new String[]
		{ "name", "phenotype", "submission_identifier", "mutations_name" };
		createFileAndHeader(patientFile, patientHeader);
		writeBatch(new ArrayList<Patient>(patients.values()), patientFile, patientHeader);

		ObservableFeature f = new ObservableFeature();
		f.setName("Allele count");
		features.add(f);

		final File featureFile = new File(outputDir.getAbsolutePath() + File.separatorChar + "ObservableFeature.txt");
		String[] featureHeader = new String[]
		{ "name" };
		createFileAndHeader(featureFile, featureHeader);
		writeBatch(features, featureFile, featureHeader);

		final File speciesFile = new File(targetDir, "Species.txt");
		String[] speciesHeader = new String[]
		{ "name" };
		createFileAndHeader(speciesFile, speciesHeader);
		writeBatch(species, speciesFile, speciesHeader);

	}

	private String toCsv(List<String> values)
	{
		String result = "";
		boolean first = true;
		for (String val : values)
		{
			if (first)
			{
				result += val;
				first = false;
			}
			else
			{
				result += "," + val;
			}
		}
		return result;
	}

	private void createFileAndHeader(File file, String[] fields) throws IOException
	{
		CsvWriter writer = new CsvFileWriter(file, Arrays.asList(fields));
		writer.writeHeader();
		writer.close();
	}

	private void writeBatch(List<? extends Entity> entities, File file, String[] fields) throws IOException
	{
		if (entities.size() > 0)
		{
			System.out.println("Writing to " + file);

			// create appending csvWriter using the selected headers
			CsvWriter writer = new CsvFileWriter(file, Arrays.asList(fields), true);

			// write batch to csv
			for (Entity e : entities)
			{
				writer.writeRow(e);
			}

			writer.close();

		}
	}

}

package org.molgenis.mutation.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.mutation.dto.LovdDTO;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.variant.Gene;
import org.molgenis.variant.Patient;
import org.molgenis.variant.Variant;
import org.springframework.stereotype.Service;

@Service
public class LovdService
{
	private Database db;
	
	// @Autowired
	public void setDatabase(Database db)
	{
		this.db = db;
	}

	/**
	 * Export database contents into an LovdDTO
	 * 
	 * @return LovdDTO
	 */
	public LovdDTO export()
	{
		try
		{
			LovdDTO lovdDTO = new LovdDTO();

			List<Gene> geneList = this.db.query(Gene.class).find();
			lovdDTO.setGeneMap(this.geneListToGeneMap(geneList));

			// lovdDTO.setDiseaseMap(this.diseaseListToDiseaseMap(diseaseList));

			List<Patient> patientList = this.db.query(Patient.class).find();
			lovdDTO.setIndividualMap(this.patientListToPatientMap(patientList));
			lovdDTO.setPhenotypeMap(this.patientListToPhenotypeMap(patientList));

			List<Variant> variantList = this.db.query(Variant.class).find();
			lovdDTO.setVariantGenomeMap(this.variantListToVariantGenomeMap(variantList));
			lovdDTO.setVariantTranscriptMap(this.variantListToVariantTranscriptMap(variantList));

			return lovdDTO;
		}
		catch (DatabaseException e)
		{
			e.printStackTrace();
			throw new LovdServiceException(e.getMessage());
		}
	}

	private List<Map<String, String>> geneListToGeneMap(List<Gene> geneList)
	{
		List<Map<String, String>> result = new ArrayList<Map<String, String>>();

		for (Gene gene : geneList)
		{
			Map<String, String> geneMap = new LinkedHashMap<String, String>();

			geneMap.put("id", gene.getId().toString());
			geneMap.put("name", gene.getName());
			geneMap.put("chromosome", "");
			geneMap.put("chrom_band", "");
			geneMap.put("id_hgnc", "");
			geneMap.put("id_entrez", "");
			geneMap.put("id_omim", "");

			result.add(geneMap);
		}

		return result;
	}

//	private List<Map<String, String>> diseaseListToDiseaseMap(List<String> diseaseList)
//	{
//		List<Map<String, String>> result = new ArrayList<Map<String, String>>();
//
//		for (String disease : diseaseList)
//		{
//			Map<String, String> diseaseMap = new LinkedHashMap<String, String>();
//
//			diseaseMap.put("id", disease);
//			diseaseMap.put("symbol", "");
//			diseaseMap.put("name", disease);
//			diseaseMap.put("id_omim", "");
//
//			result.add(diseaseMap);
//		}
//
//		return result;
//	}

	private List<Map<String, String>> patientListToPatientMap(List<Patient> patientList)
	{
		List<Map<String, String>> result = new ArrayList<Map<String, String>>();

		for (Patient patient : patientList)
		{
			Map<String, String> patientMap = new LinkedHashMap<String, String>();

			patientMap.put("id", patient.getId().toString());
			patientMap.put("fatherid", "");
			patientMap.put("motherid", "");
			patientMap.put("panelid", "");
			patientMap.put("panel_size", "");
			patientMap.put("owned_by", "");
			patientMap.put("statusid", "");
			patientMap.put("created_by", "");
			patientMap.put("created_date", patient.getSubmission().getDate().toString());
			patientMap.put("edited_by", "");
			patientMap.put("edited_date", "");
			patientMap.put("Individual/Lab_ID", "");
			patientMap.put("Individual/Reference", "");
			patientMap.put("Individual/Remarks", "");
			patientMap.put("Individual/Remarks_Non_Public", "");
			patientMap.put("Individual/Gender", "");
			patientMap.put("Individual/Origin/Ethnic", "");
			patientMap.put("Individual/Consanguinity", "");
			patientMap.put("Individual/Origin/Geographic", "");
			patientMap.put("Individual/Age_of_death", "");
			patientMap.put("Individual/Death/Cause", "");
			patientMap.put("Individual/Origin/Population", "");

			List<ObservedValue> valueList = Arrays.asList(patient.getTargetObservedValueCollection().toArray(
					new ObservedValue[0]));

			for (ObservedValue value : valueList)
			{
				if (value.getFeature().getName().equalsIgnoreCase("Gender"))
				{
					patientMap.put("Individual/Gender", value.getValue());
				}
				else if (value.getFeature().getName().equalsIgnoreCase("Ethnicity"))
				{
					patientMap.put("Individual/Origin/Ethnic", value.getValue());
				}
				else if (value.getFeature().getName().equalsIgnoreCase("Consanguinity"))
				{
					patientMap.put("Individual/Consanguinity", value.getValue());
				}
				else if (value.getFeature().getName().equalsIgnoreCase("Cause of death"))
				{
					patientMap.put("Individual/Death/Cause", "");
				}
				else if (value.getFeature().getName().equalsIgnoreCase("Population"))
				{
					patientMap.put("Individual/Origin/Population", "");
				}
			}
			result.add(patientMap);
		}

		return result;
	}

	private List<Map<String, String>> patientListToPhenotypeMap(List<Patient> patientList)
	{
		List<Map<String, String>> result = new ArrayList<Map<String, String>>();

		for (Patient patient : patientList)
		{
			Map<String, String> phenotypeMap = new LinkedHashMap<String, String>();

			phenotypeMap.put("id", patient.getId().toString());
			phenotypeMap.put("dieseaseid", "");
			phenotypeMap.put("individualid", patient.getId().toString());
			phenotypeMap.put("owned_by", "");
			phenotypeMap.put("statusid", "");
			phenotypeMap.put("created_by", "");
			phenotypeMap.put("created_date", "");
			phenotypeMap.put("edited_by", "");
			phenotypeMap.put("edited_date", "");
			phenotypeMap.put("Phenotype/Enzyme/IVD/Activity", patient.getPhenotype());

			result.add(phenotypeMap);
		}

		return result;
	}

	private List<Map<String, String>> variantListToVariantGenomeMap(List<Variant> variantList)
	{
		List<Map<String, String>> result = new ArrayList<Map<String, String>>();

		for (Variant variant : variantList)
		{
			Map<String, String> variantMap = new LinkedHashMap<String, String>();

			variantMap.put("id", variant.getId().toString());
			variantMap.put("allele", "");
			variantMap.put("effectid", "");
			variantMap.put("chromosome", variant.getGene().getName());
			variantMap.put("position_g_start", variant.getStartGdna().toString());
			variantMap.put("position_g_end", variant.getEndGdna().toString());
			variantMap.put("mapping_flags", "");
			variantMap.put("owned_by", "");
			variantMap.put("statusid", "");
			variantMap.put("created_by", "");
			variantMap.put("created_date", "");
			variantMap.put("edited_by", "");
			variantMap.put("edited_date", "");
			variantMap.put("VariantOnGenome/DBID", "");
			variantMap.put("VariantOnGenome/DNA", "");
			variantMap.put("VariantOnGenome/Frequency", "");
			variantMap.put("VariantOnGenome/Reference", "");
			variantMap.put("VariantOnGenome/Restriction_site", "");
			variantMap.put("VariantOnGenome/Published_as", "");
			variantMap.put("VariantOnGenome/Remarks", "");
			variantMap.put("VariantOnGenome/Genetic_origin", "");
			variantMap.put("VariantOnGenome/Segregation", "");
			variantMap.put("VariantOnGenome/dbSNP", "");

			result.add(variantMap);
		}

		return result;
	}

	private List<Map<String, String>> variantListToVariantTranscriptMap(List<Variant> variantList)
	{
		List<Map<String, String>> result = new ArrayList<Map<String, String>>();

		for (Variant variant : variantList)
		{
			Map<String, String> variantMap = new LinkedHashMap<String, String>();

			String[] startCdnaIntron = StringUtils.split(variant.getNameCdna(), "+-");

			variantMap.put("id", variant.getId().toString());
			variantMap.put("transcriptid", "");
			variantMap.put("effectid", "");
			variantMap.put("position_c_start", variant.getStartCdna().toString());
			variantMap.put("position_c_start_intron", (variant.getExon().getIsIntron() ? startCdnaIntron[1] : "0"));
			variantMap.put("position_c_end", variant.getEndCdna().toString());
			variantMap.put("position_c_end_intron", (variant.getExon().getIsIntron() ? startCdnaIntron[1] : "0"));
			variantMap.put("VariantOnTranscript/DNA", variant.getNameCdna());
			variantMap.put("VariantOnTranscript/RNA", "r.?");
			variantMap.put("VariantOnTranscript/Protein", variant.getNameAa());
			variantMap.put("VariantOnTranscript/Published_as", "");
			variantMap.put("VariantOnTranscript/Exon", variant.getExon().getName());
			variantMap.put("VariantOnTranscript/PolyPhen", "");
			variantMap.put("VariantOnTranscript/GVS/Function", "");

			result.add(variantMap);
		}

		return result;
	}
}

package org.molgenis.mutation.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.molgenis.core.Publication;
import org.molgenis.framework.db.Database;
import org.molgenis.mutation.dto.CafeVariomeDTO;
import org.molgenis.pheno.AlternateId;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.variant.Patient;
import org.molgenis.variant.Variant;
import org.springframework.stereotype.Service;

@Service
public class CafeVariomeService
{
	private Database db;
	
	// @Autowired
	public void setDatabase(Database db)
	{
		this.db = db;
	}

	public List<CafeVariomeDTO> export()
	{
		try
		{
			List<Variant> variantList               = this.db.query(Variant.class).find();
	
			List<CafeVariomeDTO> cafeVariomeDTOList = this.variantListToCafeVariomeDTOList(variantList);
			
			return cafeVariomeDTOList;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new CafeVariomeServiceException(e.getMessage());
		}
	}

	private List<CafeVariomeDTO> variantListToCafeVariomeDTOList(List<Variant> variantList)
	{
		List<CafeVariomeDTO> cafeVariomeDTOList = new ArrayList<CafeVariomeDTO>();

		for (Variant variant : variantList)
		{
			if (variant.getMutationsPatientCollection().size() > 0)
			{
				cafeVariomeDTOList.add(this.variantToCafeVariomeDTO(variant));
			}
		}
		return cafeVariomeDTOList;
	}

	private CafeVariomeDTO variantToCafeVariomeDTO(Variant variant)
	{
		List<ObservedValue> variantValueList = Arrays.asList(variant.getTargetObservedValueCollection().toArray(new ObservedValue[0]));
		List<AlternateId> variantAltIdList   = variant.getAlternateId();

		List<Patient> patientList            = Arrays.asList(variant.getMutationsPatientCollection().toArray(new Patient[0]));
		//TODO: How to describe multiple patients (phenotypes, publications etc.) in CafeVariome?
		Patient patient                      = patientList.get(0);
		List<AlternateId> patientAltIdList   = patient.getAlternateId();
		List<ObservedValue> phenoValueList   = Arrays.asList(patient.getTargetObservedValueCollection().toArray(new ObservedValue[0]));
		Publication publication              = patient.getPatientreferences().get(0);

		CafeVariomeDTO cafeVariomeDTO = new CafeVariomeDTO();
		
		for (ObservedValue phenoValue : phenoValueList)
		{
			if (phenoValue.getFeature().getName().equals("Detection method"))
			{
				cafeVariomeDTO.setDetection(phenoValue.getValue());
			}
			else if (phenoValue.getFeature().getName().equals("Ethnicity"))
			{
				cafeVariomeDTO.setEthnicity(phenoValue.getValue());
			}
			else if (phenoValue.getFeature().getName().equals("Gender"))
			{
				cafeVariomeDTO.setGender(phenoValue.getValue());
			}
		}

		for (ObservedValue variantValue : variantValueList)
		{
			if (variantValue.getFeature().getName().equals("Germline"))
			{
				cafeVariomeDTO.setGermline(variantValue.getValue());
			}
			else if (variantValue.getFeature().getName().equals("Pathogenicity"))
			{
				cafeVariomeDTO.setPatientPathogenicity(variantValue.getValue());
			}
			else if (variantValue.getFeature().getName().equals("Zygosity"))
			{
				cafeVariomeDTO.setZygosity(variantValue.getValue());
			}
		}

		for (AlternateId alternateId : patientAltIdList)
		{
			if (alternateId.getDefinition().equals("molgenis_patient_id"))
			{
				cafeVariomeDTO.setPatientIdentifier(alternateId.getName());
			}
		}

		for (AlternateId alternateId : variantAltIdList)
		{
			if (alternateId.getDefinition().equals("molgenis_variant_id"))
			{
				cafeVariomeDTO.setVariantIdentifier(alternateId.getName());
			}
		}

		if (publication.getPubmedID() != null)
		{
			cafeVariomeDTO.setReference("PM:" + publication.getPubmedID().getName());
		}

		if (variant.getGene() != null && CollectionUtils.isNotEmpty(variant.getGene().getAlternateId()))
		{
			for (AlternateId alternateId : variant.getGene().getAlternateId())
			{
				if ("genbank_id".equals(alternateId.getDefinition()))
				{
					cafeVariomeDTO.setRefnum(alternateId.getName());
				}
			}
		}
		cafeVariomeDTO.setName(variant.getNameCdna());
		cafeVariomeDTO.setPhenotype(patient.getPhenotype());
		cafeVariomeDTO.setPolicy("openAccess");
		cafeVariomeDTO.setPositionGdna(variant.getStartGdna().toString());
		cafeVariomeDTO.setSymbol(variant.getGene().getName());

		return cafeVariomeDTO;
	}
}

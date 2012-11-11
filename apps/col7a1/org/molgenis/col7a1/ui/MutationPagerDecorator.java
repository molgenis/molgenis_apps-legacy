package org.molgenis.col7a1.ui;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.molgenis.core.dto.PublicationDTO;
import org.molgenis.mutation.dto.MutationSummaryDTO;
import org.molgenis.mutation.dto.PatientSummaryDTO;
import org.molgenis.mutation.dto.VariantDTO;
import org.molgenis.mutation.ui.html.DisplaytagTableDecorator;

public class MutationPagerDecorator extends DisplaytagTableDecorator
{
	private String createPublicationRows(int numRows)
	{
		StringBuffer result = new StringBuffer();

		if (numRows > 0)
		{
			for (int i = 0; i < numRows; ++i)
			{
				result.append("<br/>");
			}
		}
		else
		{
			result.append("<br/>");
		}
		return result.toString();
	}

	public String getMutationId()
	{
		MutationSummaryDTO mutationSummaryDTO = (MutationSummaryDTO) getCurrentRowObject();

		StringBuffer result = new StringBuffer();

		result.append("<div class=\"unwrapped\">");
		result.append(this.createMutationLink(mutationSummaryDTO.getIdentifier()));
		result.append("<br/>");

		for (PatientSummaryDTO patientSummaryDTO : mutationSummaryDTO.getPatientSummaryDTOList())
		{
			result.append("+ ");
			if (CollectionUtils.isNotEmpty(patientSummaryDTO.getVariantDTOList()))
			{
				result.append(this.createMutationLink(patientSummaryDTO.getVariantDTOList().get(0).getIdentifier()));
			}
			result.append("<br/>");
			result.append(this.createPublicationRows(patientSummaryDTO.getPublicationDTOList().size()));
		}
		result.append("</div>");

		return result.toString();
	}
	
	public String getNameCdna()
	{
		MutationSummaryDTO mutationSummaryDTO = (MutationSummaryDTO) getCurrentRowObject();

		StringBuffer result = new StringBuffer();
		
		result.append("<div class=\"unwrapped\">");
		result.append(mutationSummaryDTO.getCdnaNotation());
		result.append("<br/>");

		for (PatientSummaryDTO patientSummaryDTO : mutationSummaryDTO.getPatientSummaryDTOList())
		{
			if (CollectionUtils.isNotEmpty(patientSummaryDTO.getVariantDTOList()))
			{
				result.append(patientSummaryDTO.getVariantDTOList().get(0).getCdnaNotation());
			}
			else
			{
				if (StringUtils.equalsIgnoreCase(mutationSummaryDTO.getInheritance(), "dominant"))
				{
					result.append("NA");
				}
				else
				{
					result.append("Unknown");
				}
			}
			result.append("<br/>");

			result.append(this.createPublicationRows(patientSummaryDTO.getPublicationDTOList().size()));
		}
		result.append("</div>");

		return result.toString();
	}
	
	public String getNameAa()
	{
		MutationSummaryDTO mutationSummaryDTO = (MutationSummaryDTO) getCurrentRowObject();

		StringBuffer result = new StringBuffer();

		result.append("<div class=\"unwrapped\">");
		result.append(mutationSummaryDTO.getAaNotation());
		result.append("<br/>");

		for (PatientSummaryDTO patientSummaryDTO : mutationSummaryDTO.getPatientSummaryDTOList())
		{
			if (CollectionUtils.isNotEmpty(patientSummaryDTO.getVariantDTOList()))
			{
				result.append(patientSummaryDTO.getVariantDTOList().get(0).getAaNotation());
			}
			result.append("<br/>");

			result.append(this.createPublicationRows(patientSummaryDTO.getPublicationDTOList().size()));
		}
		result.append("</div>");

		return result.toString();
	}
	
	public String getExonIntron()
	{
		MutationSummaryDTO mutationSummaryDTO = (MutationSummaryDTO) getCurrentRowObject();

		StringBuffer result = new StringBuffer();

		result.append("<div class=\"unwrapped\">");
		result.append(this.createExonLink(mutationSummaryDTO.getExonId().toString(), mutationSummaryDTO.getExonName()));
		result.append("<br/>");

		for (PatientSummaryDTO patientSummaryDTO : mutationSummaryDTO.getPatientSummaryDTOList())
		{
			if (CollectionUtils.isNotEmpty(patientSummaryDTO.getVariantDTOList()))
			{
				VariantDTO variantDTO = patientSummaryDTO.getVariantDTOList().get(0);
				result.append(this.createExonLink(variantDTO.getExonId().toString(), variantDTO.getExonName()));
			}
			result.append("<br/>");

			result.append(this.createPublicationRows(patientSummaryDTO.getPublicationDTOList().size()));
		}
		result.append("</div>");

		return result.toString();
	}
	
	public String getConsequence()
	{
		MutationSummaryDTO mutationSummaryDTO = (MutationSummaryDTO) getCurrentRowObject();

		StringBuffer result = new StringBuffer();

		result.append("<div class=\"unwrapped\">");
		result.append(mutationSummaryDTO.getConsequence());
		result.append("<br/>");

		for (PatientSummaryDTO patientSummaryDTO : mutationSummaryDTO.getPatientSummaryDTOList())
		{
			if (CollectionUtils.isNotEmpty(patientSummaryDTO.getVariantDTOList()))
			{
				result.append(patientSummaryDTO.getVariantDTOList().get(0).getConsequence());
			}
			result.append("<br/>");

			result.append(this.createPublicationRows(patientSummaryDTO.getPublicationDTOList().size()));
		}
		result.append("</div>");

		return result.toString();
	}

	public String getInheritance()
	{
		MutationSummaryDTO mutationSummaryDTO = (MutationSummaryDTO) getCurrentRowObject();

		StringBuffer result = new StringBuffer();

		result.append("<div class=\"unwrapped\">");
		result.append(mutationSummaryDTO.getInheritance());
		result.append("<br/>");

		for (PatientSummaryDTO patientSummaryDTO : mutationSummaryDTO.getPatientSummaryDTOList())
		{
			if (CollectionUtils.isNotEmpty(patientSummaryDTO.getVariantDTOList()))
			{
				result.append(patientSummaryDTO.getVariantDTOList().get(0).getInheritance());
			}
			result.append("<br/>");

			result.append(this.createPublicationRows(patientSummaryDTO.getPublicationDTOList().size()));
		}
		result.append("</div>");

		return result.toString();
	}

	public String getPatientId()
	{
		MutationSummaryDTO mutationSummaryDTO = (MutationSummaryDTO) getCurrentRowObject();

		StringBuffer result = new StringBuffer();

		result.append("<div class=\"unwrapped\">");
		result.append("<br/>");

		for (PatientSummaryDTO patientSummaryDTO : mutationSummaryDTO.getPatientSummaryDTOList())
		{
			result.append(this.createPatientLink(patientSummaryDTO.getPatientIdentifier()));
			result.append("<br/>");

			result.append(this.createPublicationRows(patientSummaryDTO.getPublicationDTOList().size()));
		}
		result.append("</div>");

		return result.toString();
	}

	public String getPhenotype()
	{
		MutationSummaryDTO mutationSummaryDTO = (MutationSummaryDTO) getCurrentRowObject();

		StringBuffer result = new StringBuffer();

		result.append("<div class=\"unwrapped\">");

		for (PatientSummaryDTO patientSummaryDTO : mutationSummaryDTO.getPatientSummaryDTOList())
		{
			result.append("<br/>");
			result.append(patientSummaryDTO.getPhenotypeMajor());
			if (StringUtils.isNotBlank(patientSummaryDTO.getPhenotypeSub()))
			{
				result.append(", " + patientSummaryDTO.getPhenotypeSub());
			}

			if (CollectionUtils.isNotEmpty(patientSummaryDTO.getPublicationDTOList()))
			{
				for (PublicationDTO publicationDTO : patientSummaryDTO.getPublicationDTOList())
				{
					result.append("<br/>");
					result.append(this.createPublicationLink(mutationSummaryDTO.getPubmedURL(), publicationDTO));
				}
			}
			else
			{
				result.append("<br/>");
				result.append("Unpublished");
			}
		}
		result.append("</div>");

		return result.toString();
	}
}

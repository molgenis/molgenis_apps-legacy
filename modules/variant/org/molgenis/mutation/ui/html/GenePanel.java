package org.molgenis.mutation.ui.html;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrBuilder;
import org.molgenis.framework.ui.html.HtmlInput;
import org.molgenis.mutation.dto.ExonDTO;
import org.molgenis.mutation.dto.ProteinDomainDTO;

/*
 * A panel that prints clickable exon-intron boxes
 */
public class GenePanel extends HtmlInput<List<ProteinDomainDTO>> implements Serializable
{
	/* The serial version UID of this class. Needed for serialization. */
	private static final long serialVersionUID = 600688454568632400L;

//	private final double SCALE_FACTOR                         = 0.003;
	private final double SCALE_FACTOR                         = 0.1;
	private List<ProteinDomainDTO> proteinDomainDTOList;
	private String baseUrl;
	private Boolean showNames                                 = true;

	public GenePanel(List<ProteinDomainDTO> proteinDomainDTOList, String baseUrl)
	{
		this.proteinDomainDTOList = proteinDomainDTOList;
		this.baseUrl              = baseUrl;
	}

	public void setShowNames(Boolean showNames)
	{
		this.showNames = showNames;
	}

	@Override
	public String toHtml()
	{
		StrBuilder result = new StrBuilder();

		result.appendln("<br/><br/>");
		result.appendln("<h3>" + this.getLabel() + "</h3>");
		result.appendln("<p>Click anywhere on this schematic representation to graphically browse the gene. With every click you will zoom in deeper on the gene. Mutated nucleotides are depicted in red. If the cursor is placed over the mutated nucleotide(s), the corresponding mutation is shown.</p>");
		result.appendln("<br/>");

		result.appendln("<div class=\"scrollable\">");
		result.appendln("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\"  width=\"1%\">");

		// first row: names
		if (this.showNames)
		{
			result.appendln("<tr>");
			for (ProteinDomainDTO proteinDomainDTO : proteinDomainDTOList)
			{
				List<ExonDTO> exonDTOs = proteinDomainDTO.getExonDTOList();

				if (exonDTOs.size() > 0)
				{
					String domainName = proteinDomainDTO.getDomainName();
					String beginName = exonDTOs.get(0).getName();
					String endName = exonDTOs.get(exonDTOs.size() - 1).getName();
					result.appendln("<td align=\"center\" valign=\"bottom\" colspan=\"" + exonDTOs.size()
							+ "\" width=\"1%\">" + domainName + " (" + beginName + " - " + endName + ")</td>");
				}
			}
			result.appendln("</tr>");
		}

		result.appendln("<tr>");

		// second row: boxes
		for (ProteinDomainDTO proteinDomainDTO : proteinDomainDTOList)
		{
			for (ExonDTO exonDTO : proteinDomainDTO.getExonDTOList())
			{
				result.append("<td align=\"left\">");
				result.append("<div class=\"pd" + proteinDomainDTO.getDomainId() + "\" style=\"display: block; width: "
						+ exonDTO.getLength() * this.SCALE_FACTOR + "px; height: 26px;\">");
				String url = this.baseUrl;
				url = StringUtils.replace(url, "domain_id=", "domain_id=" + proteinDomainDTO.getDomainId());
				url = StringUtils.replace(url, "#exon", "#exon" + exonDTO.getId());
				result.append("<a style=\"display: block; height: 100%; width: 100%;\" href=\"" + url + "\" alt=\""
						+ exonDTO.getName() + "\" title=\"" + exonDTO.getName() + "\"></a>");
				result.append("</div>");
				result.appendln("</td>");
			}
		}

		result.appendln("</tr>");
		result.appendln("</table>");
		result.appendln("</div>");

		return result.toString();
	}
}

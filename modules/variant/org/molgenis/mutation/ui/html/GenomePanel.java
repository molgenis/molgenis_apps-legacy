package org.molgenis.mutation.ui.html;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrBuilder;
import org.molgenis.framework.ui.html.HtmlInput;
import org.molgenis.mutation.dto.GeneDTO;

/*
 * A panel that prints clickable gene boxes
 */
public class GenomePanel extends HtmlInput<List<GeneDTO>> implements Serializable
{
	/* The serial version UID of this class. Needed for serialization. */
	private static final long serialVersionUID = 600688454568632400L;

	private final double SCALE_FACTOR = 0.01;
	// private final double SCALE_FACTOR = 0.1;
	private List<GeneDTO> geneDTOList;
	private String baseUrl;
	private Boolean showNames = true;

	public GenomePanel(List<GeneDTO> geneDTOList, String baseUrl)
	{
		this.geneDTOList = geneDTOList;
		this.baseUrl     = baseUrl;
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

		// first row: gene names
		if (this.showNames)
		{
			result.appendln("<tr>");
			for (int i = 0; i < geneDTOList.size(); i++)
			{
				GeneDTO geneDTO = geneDTOList.get(i);
				if (i > 0)
				{
					result.append("<td width=\"50px\">&nbsp;</td>");
				}
				result.append("<td align=\"center\">");
				result.appendln("<div style=\"display: block; width: " + geneDTO.getLength() * this.SCALE_FACTOR + "px;\">" + geneDTO.getName() + "</div>");
				result.appendln("</td>");
			}
			result.appendln("</tr>");
		}

		result.appendln("<tr>");

		// second row: boxes
		for (int i = 0; i < geneDTOList.size(); i++)
		{
			GeneDTO geneDTO = geneDTOList.get(i);
			if (i > 0)
			{
				result.append("<td><img src=\"res/img/col7a1/intron.png\" width=\"50px\" height=\"30px\"/></td>");
			}
			result.append("<td>");
			result.append("<div style=\"display: block; background: #6dcbfe; border-color:#000000; width: " + geneDTO.getLength() * this.SCALE_FACTOR + "px; height: 26px;\">");
			String url = this.baseUrl;
			url = StringUtils.replace(url, "gene_id=", "gene_id=" + geneDTO.getId());
			result.append("<a style=\"display: block; height: 100%; width: 100%;\" href=\"" + url + "\" alt=\"" + geneDTO.getName() + "\" title=\"" + geneDTO.getName() + "\"></a>");
			result.append("</div>");
			result.appendln("</td>");
		}

		result.appendln("</tr>");
		result.appendln("</table>");
		result.appendln("</div>");

		return result.toString();
	}
}

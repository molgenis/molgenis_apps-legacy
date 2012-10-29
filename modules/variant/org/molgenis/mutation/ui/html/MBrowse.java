package org.molgenis.mutation.ui.html;

import java.io.Serializable;
import java.util.List;

import org.molgenis.mutation.dto.ExonDTO;
import org.molgenis.mutation.dto.GeneDTO;
import org.molgenis.mutation.dto.MutationSummaryDTO;
import org.molgenis.mutation.dto.ProteinDomainDTO;

public class MBrowse implements Serializable
{
	private static final long serialVersionUID = 1L;

	private String target;
	private Boolean showNames;
	private Boolean isVisible;

	public MBrowse()
	{
		this.showNames = true;
		this.isVisible = true;
	}

	public String getTarget()
	{
		return target;
	}

	public void setTarget(String target)
	{
		this.target = target;
	}

	public Boolean getShowNames() {
		return showNames;
	}

	public void setShowNames(Boolean showNames)
	{
		this.showNames = showNames;
	}

	public GenomePanel createGenomePanel(List<GeneDTO> geneDTOList)
	{
		String baseUrl = "molgenis.do?__target=" + this.getTarget() + "&select=" + this.getTarget() + "&__action=showGene&gene_id=";
		GenomePanel genomePanel = new GenomePanel(geneDTOList, baseUrl);
		genomePanel.setShowNames(this.showNames);
		
		return genomePanel;
	}

	public GenePanel createGenePanel(List<ProteinDomainDTO> proteinDomainDTOList)
	{
		String baseUrl = "molgenis.do?__target=" + this.getTarget() + "&select=" + this.getTarget() + "&__action=showProteinDomain&domain_id=&snpbool=1#exon";
		GenePanel genePanel = new GenePanel(proteinDomainDTOList, baseUrl);
		genePanel.setShowNames(this.showNames);

		return genePanel;
	}

	public ProteinDomainPanel createProteinDomainPanel(ProteinDomainDTO proteinDomainDTO)
	{
		String baseUrl = "molgenis.do?__target=" + this.getTarget() + "&select=" + this.getTarget() + "&__action=showProteinDomain&domain_id=&snpbool=1#exon";
		ProteinDomainPanel proteinDomainPanel = new ProteinDomainPanel(proteinDomainDTO, baseUrl);

		return proteinDomainPanel;
	}

	public ExonIntronPanel createExonIntronPanel(List<ExonDTO> exonDTOList)
	{
		String baseUrl = "molgenis.do?__target=" + this.getTarget() + "&select=" + this.getTarget() + "&__action=showExon&exon_id=#results";
		ExonIntronPanel exonIntronPanel = new ExonIntronPanel(exonDTOList, baseUrl);
		exonIntronPanel.setShowIntrons(true);

		return exonIntronPanel;
	}

	public SequencePanel createSequencePanel(ExonDTO exonDTO, List<MutationSummaryDTO> mutationSummaryDTOList)
	{
		String baseUrl = "molgenis.do?__target=" + this.getTarget() + "&select=" + this.getTarget() + "&__action=showMutation&mid=#results";
		SequencePanel sequencePanel = new SequencePanel(exonDTO, mutationSummaryDTOList, baseUrl);

		return sequencePanel;
	}

	public Boolean getIsVisible()
	{
		return isVisible;
	}

	public void setIsVisible(Boolean isVisible)
	{
		this.isVisible = isVisible;
	}
}

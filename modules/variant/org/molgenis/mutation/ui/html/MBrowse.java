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

<<<<<<< HEAD
	public Boolean getShowNames() {
=======
	public List<ProteinDomainDTO> getProteinDomainDTOList()
	{
		return proteinDomainDTOList;
	}

	public void setProteinDomainDTOList(List<ProteinDomainDTO> proteinDomainDTOList)
	{
		this.proteinDomainDTOList = proteinDomainDTOList;
	}

	public ProteinDomainDTO getProteinDomainDTO()
	{
		return proteinDomainDTO;
	}

	public void setProteinDomainDTO(ProteinDomainDTO proteinDomainDTO)
	{
		this.proteinDomainDTO = proteinDomainDTO;
	}

	public List<MutationSummaryDTO> getMutationSummaryDTOList()
	{
		return mutationSummaryDTOList;
	}

	public void setMutationSummaryDTOList(List<MutationSummaryDTO> mutationSummaryDTOList)
	{
		this.mutationSummaryDTOList = mutationSummaryDTOList;
	}

	public String getMutationPager()
	{
		return mutationPager;
	}

	public void setMutationPager(String mutationPager)
	{
		this.mutationPager = mutationPager;
	}

	public GeneDTO getGeneDTO()
	{
		return geneDTO;
	}

	public void setGeneDTO(GeneDTO geneDTO)
	{
		this.geneDTO = geneDTO;
	}

	public ExonDTO getExonDTO()
	{
		return exonDTO;
	}

	public void setExonDTO(ExonDTO exonDTO)
	{
		this.exonDTO = exonDTO;
	}

	public List<ExonDTO> getExonDTOList()
	{
		return exonDTOList;
	}

	public void setExonDTOList(List<ExonDTO> exonDTOList)
	{
		this.exonDTOList = exonDTOList;
	}

	public Boolean getShowNames()
	{
>>>>>>> fdfd48ac8ea094c7d3eed80aa1dd0b8a1fc5d716
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
<<<<<<< HEAD
=======
		genePanel.setProteinDomainSummaryVOList(this.getProteinDomainDTOList());
		genePanel.setBaseUrl("molgenis.do?__target=" + this.getTarget() + "&select=" + this.getTarget()
				+ "&__action=showProteinDomain&domain_id=&snpbool=1#exon");
>>>>>>> fdfd48ac8ea094c7d3eed80aa1dd0b8a1fc5d716

		return genePanel;
	}

	public ProteinDomainPanel createProteinDomainPanel(ProteinDomainDTO proteinDomainDTO)
	{
<<<<<<< HEAD
		String baseUrl = "molgenis.do?__target=" + this.getTarget() + "&select=" + this.getTarget() + "&__action=showProteinDomain&domain_id=&snpbool=1#exon";
		ProteinDomainPanel proteinDomainPanel = new ProteinDomainPanel(proteinDomainDTO, baseUrl);
=======
		ProteinDomainPanel proteinDomainPanel = new ProteinDomainPanel();
		proteinDomainPanel.setProteinDomainDTO(this.getProteinDomainDTO());
		proteinDomainPanel.setBaseUrl("molgenis.do?__target=" + this.getTarget() + "&select=" + this.getTarget()
				+ "&__action=showProteinDomain&domain_id=&snpbool=1#exon");
>>>>>>> fdfd48ac8ea094c7d3eed80aa1dd0b8a1fc5d716

		return proteinDomainPanel;
	}

	public ExonIntronPanel createExonIntronPanel(List<ExonDTO> exonDTOList)
	{
		String baseUrl = "molgenis.do?__target=" + this.getTarget() + "&select=" + this.getTarget() + "&__action=showExon&exon_id=#results";
		ExonIntronPanel exonIntronPanel = new ExonIntronPanel(exonDTOList, baseUrl);
		exonIntronPanel.setShowIntrons(true);
<<<<<<< HEAD
=======
		exonIntronPanel.setBaseUrl("molgenis.do?__target=" + this.getTarget() + "&select=" + this.getTarget()
				+ "&__action=showExon&exon_id=#results");
>>>>>>> fdfd48ac8ea094c7d3eed80aa1dd0b8a1fc5d716

		return exonIntronPanel;
	}

	public SequencePanel createSequencePanel(ExonDTO exonDTO, List<MutationSummaryDTO> mutationSummaryDTOList)
	{
<<<<<<< HEAD
		String baseUrl = "molgenis.do?__target=" + this.getTarget() + "&select=" + this.getTarget() + "&__action=showMutation&mid=#results";
		SequencePanel sequencePanel = new SequencePanel(exonDTO, mutationSummaryDTOList, baseUrl);
=======
		SequencePanel sequencePanel = new SequencePanel();
		sequencePanel.setExonDTO(this.getExonDTO());
		sequencePanel.setMutationSummaryVOs(this.getMutationSummaryDTOList());
		sequencePanel.setBaseUrl("molgenis.do?__target=" + this.getTarget() + "&select=" + this.getTarget()
				+ "&__action=showMutation&mid=#results");
>>>>>>> fdfd48ac8ea094c7d3eed80aa1dd0b8a1fc5d716

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

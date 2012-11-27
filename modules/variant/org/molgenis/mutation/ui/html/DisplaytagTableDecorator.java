package org.molgenis.mutation.ui.html;

import org.displaytag.decorator.TableDecorator;
import org.molgenis.core.dto.PublicationDTO;

public class DisplaytagTableDecorator extends TableDecorator
{
	protected String createMutationLink(String mid)
	{
		return "<a href=\"molgenis.do?__target=SearchPlugin&__action=showMutation&mid=" + mid + "#results\">" + mid + "</a>";
	}

	protected String createExonLink(String exonId, String exonName)
	{
		return "<a href=\"molgenis.do?__target=SearchPlugin&__action=showExon&exon_id=" + exonId + "#results\">" + exonName + "</a>";
	}

	protected String createPatientLink(String pid)
	{
		return "<a href=\"molgenis.do?__target=SearchPlugin&__action=showPatient&pid=" + pid + "#results\">" + pid + "</a>";
	}

	protected String createPublicationLink(String pubmedUrl, PublicationDTO publicationDTO)
	{
		return "<a href=\"" + pubmedUrl + publicationDTO.getPubmedId() + "\" title=\"" + publicationDTO.getTitle() + "\" target=\"_new\">" + publicationDTO.getFirstAuthor() + " (" + publicationDTO.getYear() + ") " + publicationDTO.getJournal() + "</a>";
	}
}

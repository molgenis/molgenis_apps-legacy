package org.molgenis.mutation.web;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.commons.lang.StringUtils;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.server.MolgenisContext;
import org.molgenis.framework.server.MolgenisRequest;
import org.molgenis.framework.server.MolgenisResponse;
import org.molgenis.framework.server.MolgenisService;
import org.molgenis.mutation.ServiceLocator;
import org.molgenis.mutation.dto.GeneDTO;
import org.molgenis.mutation.dto.MutationSummaryDTO;
import org.molgenis.mutation.service.SearchService;

public class AtomFeed implements MolgenisService 
{
	private static Abdera abdera = null;
	
	public AtomFeed(@SuppressWarnings("unused") MolgenisContext mc)
	{
		if (abdera == null)
			abdera = new Abdera();
	}

	@Override
	public void handleRequest(MolgenisRequest req, MolgenisResponse resp)
			throws ParseException, DatabaseException, IOException
	{
		HttpServletRequest request   = req.getRequest();
		HttpServletResponse response = resp.getResponse();

		response.setContentType("application/atom+xml");

		try
		{
			if (StringUtils.startsWith(request.getPathInfo(), "/gene"))
			{
				this.handleGeneFeed(req, resp);
			}
			else if (StringUtils.startsWith(request.getPathInfo(), "/variants"))
			{
				this.handleVariantsFeed(req, resp);
			}
		}
		catch (Exception e)
		{
			try
			{
				response.sendError(500, e.getMessage());
			}
			catch (Exception e2)
			{
				// bad luck
			}
		}

	}

	private void handleGeneFeed(MolgenisRequest req, MolgenisResponse resp) throws IOException
	{
		HttpServletRequest request   = req.getRequest();
		HttpServletResponse response = resp.getResponse();

		SearchService searchService  = ServiceLocator.instance().getSearchService();
		searchService.setDatabase(req.getDatabase());
	
		String geneURL               = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/molgenis.do?__target=SearchPlugin&__action=listAllMutations";
		GeneDTO geneSummaryDTO       = searchService.findGene();
			
		StringBuffer content         = new StringBuffer();
		content.append("id:" + geneSummaryDTO.getSymbol() + "\n");
		content.append("entrez_id:NA\n");
		content.append("symbol:" + geneSummaryDTO.getSymbol() + "\n");
		content.append("name:" + geneSummaryDTO.getName() + "\n");
		content.append("chromosome_location:" + geneSummaryDTO.getChromosome() + "\n");
		content.append("position_start:" + geneSummaryDTO.getChromosome() + ":" + geneSummaryDTO.getBpStart() + "\n");
		content.append("position_end:" + geneSummaryDTO.getChromosome() + ":" + geneSummaryDTO.getBpEnd() + "\n");
		content.append("refseq_genomic:" + geneSummaryDTO.getGenbankId() + "." + geneSummaryDTO.getGenomeBuild() + "\n");
		content.append("refseq_mrna:NA");
		content.append("refseq_build:NA");

		Feed feed = abdera.newFeed();
		feed.addLink(request.getRequestURL().toString(), "self");
		feed.setUpdated(new Date());
		feed.setGenerator("http://www.molgenis.org/", "4.0.0", "MOLGENIS database generator");
		feed.setRights("Copyright (c), the curators of this database");

		Entry entry = feed.addEntry();
		entry.setId(geneSummaryDTO.getSymbol());
		entry.setTitle(geneSummaryDTO.getSymbol());
		entry.setRights("Copyright (c), the curators of this database");
		entry.addAuthor("Peter van den Akker");
		entry.addContributor("Peter van den Akker");
		entry.addLink(geneURL, "alternate");
		entry.setPublished(new Date());
		entry.setUpdated(new Date());
		entry.setContent(content.toString());

		feed.writeTo(response.getOutputStream());
	}
	
	private void handleVariantsFeed(MolgenisRequest req, MolgenisResponse resp) throws IOException
	{
		HttpServletRequest request   = req.getRequest();
		HttpServletResponse response = resp.getResponse();

		SearchService searchService  = (SearchService) ServiceLocator.instance().getService("searchService");
		searchService.setDatabase(req.getDatabase());
	
		String geneURL               = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/molgenis.do?__target=SearchPlugin&__action=listAllMutations";

		GeneDTO geneSummaryDTO = searchService.findGene();

		Feed feed                    = abdera.newFeed();

		feed.setTitle("Listing of all public variants in the " + geneSummaryDTO.getSymbol() + " database");
		feed.addLink(geneURL, "alternate");
		feed.addLink(request.getRequestURL().toString(), "self");
		feed.setUpdated(new Date());
		feed.setGenerator("http://www.molgenis.org/", "4.0.0", "MOLGENIS database generator");
		feed.setRights("Copyright (c), the curators of this database");

		List<MutationSummaryDTO> mutationSummaryVOList = searchService.findAllMutationSummaries();

		for (MutationSummaryDTO mutationSummaryVO : mutationSummaryVOList)
		{
			Entry entry = feed.addEntry();
			entry.setId(mutationSummaryVO.getIdentifier());
			entry.setTitle(geneSummaryDTO.getSymbol() + ":" + mutationSummaryVO.getCdnaNotation());
			String mutationURL = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/molgenis.do?__target=SearchPlugin&__action=showMutation&mid=" + mutationSummaryVO.getIdentifier() + "#results";
			entry.addLink(mutationURL, "alternate");
			entry.addLink(request.getRequestURL().toString(), "self");
			entry.addAuthor("Peter van den Akker");
			entry.addContributor("Peter van den Akker");
			entry.setPublished(new Date());
			entry.setUpdated(new Date());

			StringBuffer content = new StringBuffer();
			content.append("symbol:" + geneSummaryDTO.getSymbol() + "\n");
			content.append("id:" + mutationSummaryVO.getIdentifier() + "\n");
			content.append("position_mRNA:" + geneSummaryDTO.getGenbankId() + "." + geneSummaryDTO.getGenomeBuild() + ":c." + mutationSummaryVO.getCdnaStart() + "\n");
			content.append("position_genomic:" + geneSummaryDTO.getChromosome() + ":" + mutationSummaryVO.getGdnaStart() + "\n");
			content.append("Variant/DNA:" + mutationSummaryVO.getCdnaNotation() + "\n");
			content.append("Variant/DBID:" + mutationSummaryVO.getIdentifier() + "\n");
			content.append("Times_reported:" + mutationSummaryVO.getPatientSummaryDTOList().size() + "\n");
			entry.setContent(content.toString());
		}
		
		feed.writeTo(response.getOutputStream());
	}
}

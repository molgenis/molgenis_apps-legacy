package org.molgenis.mutation.web;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
//import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.server.MolgenisContext;
import org.molgenis.framework.server.MolgenisRequest;
import org.molgenis.framework.server.MolgenisResponse;
import org.molgenis.framework.server.MolgenisService;
import org.molgenis.mutation.ServiceLocator;
import org.molgenis.mutation.dto.LovdDTO;
import org.molgenis.mutation.service.LovdService;

public class LovdServlet implements MolgenisService
{
	public LovdServlet(@SuppressWarnings("unused")
	MolgenisContext mc)
	{
	}

	@Override
	public void handleRequest(MolgenisRequest req, MolgenisResponse resp) throws ParseException, DatabaseException,
			IOException
	{
//		HttpServletRequest request = req.getRequest();
		HttpServletResponse response = resp.getResponse();

		try
		{
			response.setContentType("text/plain");

			LovdService lovdService = ServiceLocator.instance().getLovdService();
			lovdService.setDatabase(req.getDatabase());

			LovdDTO lovdDTO = lovdService.export();

			ServletOutputStream out = response.getOutputStream();

			out.println("### LOVD-version 2999-090 ### Full data download ### To import, do not remove or alter this header ###");
			out.println("# charset = UTF-8");

			out.println("## Genes ## Do not remove or alter this header ##");
			this.printMap(lovdDTO.getGeneMap(), out);
			out.println();

			out.println("## Transcripts ## Do not remove or alter this header ##");
			out.println();

			out.println("## Diseases ## Do not remove or alter this header ##");
			out.println();

			out.println("## Individuals ## Do not remove or alter this header ##");
			this.printMap(lovdDTO.getIndividualMap(), response.getOutputStream());
			out.println();

			out.println("## Phenotypes ## Do not remove or alter this header ##");
			this.printMap(lovdDTO.getIndividualDiseaseMap(), out);
			out.println();

			out.println("## Screenings ## Do not remove or alter this header ##");
			out.println();

			out.println("## Screenings_To_Genes ## Do not remove or alter this header ##");
			out.println();

			out.println("## Variants_On_Genome ## Do not remove or alter this header ##");
			this.printMap(lovdDTO.getVariantGenomeMap(), out);
			out.println();

			out.println("## Variants_On_Transcripts ## Do not remove or alter this header ##");
			this.printMap(lovdDTO.getVariantTranscriptMap(), out);
			out.println();

			out.println("## Screenings_To_Variants ## Do not remove or alter this header ##");
			out.println();
		}
		catch (Exception e)
		{
			try
			{
				e.printStackTrace();
				response.sendError(500, e.getMessage());
			}
			catch (Exception e2)
			{
				// bad luck
			}
		}

	}

	private void printMap(List<Map<String, String>> lovdList, ServletOutputStream out) throws IOException
	{
		if (CollectionUtils.isEmpty(lovdList))
			return;

		for (String key : lovdList.get(0).keySet())
		{
			out.print("\"{{" + key + "}}\"\t");
		}
		out.println();

		for (Map<String, String> lovdMap : lovdList)
		{
			for (String key : lovdMap.keySet())
			{
				out.print(lovdMap.get(key) + "\t");
			}
			out.println();
		}
	}
}

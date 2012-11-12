package org.molgenis.mutation.web;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import javax.servlet.ServletOutputStream;
//import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.server.MolgenisContext;
import org.molgenis.framework.server.MolgenisRequest;
import org.molgenis.framework.server.MolgenisResponse;
import org.molgenis.framework.server.MolgenisService;
import org.molgenis.mutation.ServiceLocator;
import org.molgenis.mutation.dto.CafeVariomeDTO;
import org.molgenis.mutation.service.CafeVariomeService;

public class CafeVariomeServlet implements MolgenisService
{
	public CafeVariomeServlet(@SuppressWarnings("unused")
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

			CafeVariomeService cafeVariomeService = ServiceLocator.instance().getCafeVariomeService();
			cafeVariomeService.setDatabase(req.getDatabase());

			List<CafeVariomeDTO> cafeVariomeDTOList = cafeVariomeService.export();
			
			ServletOutputStream out = response.getOutputStream();

			out.print("HGVS name");
			out.print("\tReference sequence");
			out.print("\tHGNC gene symbol");
			out.print("\tPhenotype");
			out.print("\tSharing Policy");
			out.print("\tGenomic coordinates");
			out.print("\tVariant ID");
			out.print("\tSubject ID");
			out.print("\tSubject Gender");
			out.print("\tEthnicity");
			out.print("\tZygosity");
			out.print("\tGermline");
			out.print("\tPatient Pathogenicity");
			out.print("\tDetection methods");
			out.print("\tPublication reference");
			out.println("\tSource URL");

			for (CafeVariomeDTO cafeVariomeDTO : cafeVariomeDTOList)
			{
				out.println(cafeVariomeDTO.toString());
			}
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
}

package org.molgenis.mutation.service;

import java.util.List;

import org.molgenis.cms.Header;
import org.molgenis.cms.Paragraph;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CmsService
{
	private Database db;

	//@Autowired
	public void setDatabase(Database db)
	{
		this.db = db;
	}

	public Header findHeader()
	{
		try
		{
			List<Header> headerList = this.db.query(Header.class).find();
			
			if (headerList.size() != 1)
			{
				throw new CmsServiceException("Not exactly one header found");
			}
			else
			{
				return headerList.get(0);
			}
		}
		catch (DatabaseException e)
		{
			throw new CmsServiceException(e.getMessage());
		}
	}

	public void updateHeader(Header header)
	{
		try
		{
			this.db.update(header);
		}
		catch (DatabaseException e)
		{
			throw new CmsServiceException(e.getMessage());
		}
	}
	public String findContentByName(String name) throws DatabaseException
	{
		List<Paragraph> paragraphList = this.db.query(Paragraph.class).equals(Paragraph.NAME, name).find();
		
		if (paragraphList.size() == 1)
			return paragraphList.get(0).getContent();
		else
			return "Add your content here";
	}
}

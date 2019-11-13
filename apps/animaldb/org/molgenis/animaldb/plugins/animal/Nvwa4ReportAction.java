package org.molgenis.animaldb.plugins.animal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.molgenis.animaldb.commonservice.CommonService;
import org.molgenis.animaldb.plugins.administration.AnimalDBReport;
import org.molgenis.animaldb.plugins.administration.VWAReport4;
import org.molgenis.framework.server.MolgenisRequest;

public class Nvwa4ReportAction
{
	private AnimalDBReport report = null;
	private int year;
	private List<Integer> lastYearsList;
	private Calendar calendar;
	private String form;
	private CommonService cs = CommonService.getInstance();
	private String userName = null;

	public Nvwa4ReportAction(String userName)
	{
		this.userName = userName;
		this.calendar = Calendar.getInstance();
		int currentYear = calendar.get(Calendar.YEAR);
		this.lastYearsList = new ArrayList<Integer>();
		for (int earlier = 0; earlier < 5; earlier++)
		{
			this.lastYearsList.add(currentYear - earlier);
		}

	}

	public AnimalDBReport createReport4(List<String> targetNameList)
	{
		report = new VWAReport4(cs.getDatabase(), userName);

		report.makeReport(this.year, this.form, targetNameList);
		return report;
	}

	public void ReadForm(MolgenisRequest request)
	{
		try
		{
			String action = request.getString("__action");

			this.year = request.getInt("year");
			this.form = request.getString("form");

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void setYear(int year)
	{
		this.year = year;
	}

	public int getYear()
	{
		return year;
	}

	public List<Integer> getLastYearsList()
	{
		return lastYearsList;
	}

	public AnimalDBReport getReport()
	{
		return report;
	}

	public void setReport(AnimalDBReport report)
	{
		this.report = report;
	}
}

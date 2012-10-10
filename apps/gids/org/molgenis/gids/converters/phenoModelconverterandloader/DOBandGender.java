package org.molgenis.gids.converters.phenoModelconverterandloader;

public class DOBandGender
{

	private String dateOfBirth = null;
	private String gender = null;
	private String obsT;

	public DOBandGender(String target)
	{
		this.obsT = target;
	}

	public String getObsT()
	{
		return obsT;
	}

	public String getDateOfBirth()
	{
		return dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth)
	{
		this.dateOfBirth = dateOfBirth;
	}

	public String getGender()
	{
		return gender;
	}

	public void setGender(String gender)
	{
		this.gender = gender;
	}

}

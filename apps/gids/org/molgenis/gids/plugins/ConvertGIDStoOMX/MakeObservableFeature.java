package org.molgenis.gids.plugins.ConvertGIDStoOMX;

public class MakeObservableFeature
{
	private String name;
	private String identifier;
	private String datetype;
	private String description;

	public MakeObservableFeature(String name, String identifier, String description, String datetype)
	{
		this.identifier = identifier;
		this.name = name;
		this.datetype = datetype;
		this.description = description;
	}

	public String getName()
	{
		return name;
	}

	public String getIdentifier()
	{
		return identifier;
	}

	public String getDescription()
	{
		return description;
	}

	public String getDateType()
	{

		return datetype;
	}

}

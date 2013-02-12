package org.molgenis.gids.plugins.converters.ConvertGIDStoOMX;

public class MakeObservationTarget
{
	private String name;
	private String identifier;

	public MakeObservationTarget(String name, String identifier)
	{
		this.identifier = identifier;
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public String getIdentifier()
	{
		return identifier;
	}

}

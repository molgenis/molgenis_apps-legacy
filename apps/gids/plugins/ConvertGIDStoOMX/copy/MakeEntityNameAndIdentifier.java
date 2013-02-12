package plugins.ConvertGIDStoOMX.copy;

public class MakeEntityNameAndIdentifier
{
	private String name;
	private String identifier;

	public MakeEntityNameAndIdentifier(String name, String identifier)
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

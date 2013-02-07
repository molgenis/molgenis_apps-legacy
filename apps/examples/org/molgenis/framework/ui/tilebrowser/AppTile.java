package org.molgenis.framework.ui.tilebrowser;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class AppTile extends Tile
{
	private static Random randomGenerator = new Random();

	public String style = "color: white; width: 300px; height: 200px;  border: solid thin black; margin: 5px; float: left; overflow: hidden; position: relative; text-align: center;";
	public List<String> colors = Arrays.asList(new String[]
	{ "B3002D", "003CF0", "00B32D", "8600B3", "FFCB2E", "B30086", "0086B3", "86B300" });

	private String color;
	private String title;
	private String href;

	public AppTile(String title, String href)
	{
		super(null);
		this.title = title;
		this.href = href;
		this.color = colors.get(randomGenerator.nextInt(colors.size()));
	}

	public void setHref(String href)
	{
		this.href = href;
	}

	public void setHexColor(String color)
	{
		this.color = color;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getHtml()
	{
		return "<a href=\""
				+ this.href
				+ "\" style=\"padding: 5px;\" onmouseover=\"this.style.backgroundColor='black'\" onmouseout=\"this.style.backgroundColor='white'\"><div style=\"background: #"
				+ color + "; " + style + "\"><h1>" + title + "<h1></div></a>";
	}
}

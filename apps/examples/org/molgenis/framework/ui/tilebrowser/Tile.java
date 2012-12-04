package org.molgenis.framework.ui.tilebrowser;

import java.util.ArrayList;
import java.util.List;

/** One tile for the tile browser */
public class Tile
{
	// properties used for filtering and sorting
	private List<String> tags = new ArrayList<String>();

	// rendering of the tile contents
	private String html = "unknown";

	public Tile(String html)
	{
		this.html = html;
	}

	/** Properties of the Tile, used for filtering and sorting */
	public List<String> getTags()
	{
		return tags;
	}

	public Tile tag(String value)
	{
		this.tags.add(value);
		return this;
	}

	/** Visualization of the Tile contents */
	public String getHtml()
	{
		return html;
	}

	public void setHtml(String html)
	{
		this.html = html;
	}
}

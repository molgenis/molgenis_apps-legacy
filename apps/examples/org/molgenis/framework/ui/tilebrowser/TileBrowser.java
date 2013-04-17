package org.molgenis.framework.ui.tilebrowser;

import java.util.ArrayList;
import java.util.List;

import org.molgenis.framework.ui.html.HtmlWidget;
import org.molgenis.framework.ui.tilebrowser.Tile;

/**
 * Tile browser to show a list of contents in the form of a mosaic of tiles. The
 * tiles can be filtered and sorted by the user ...
 * 
 * Implemented using 'isotope' <br/>
 * http://isotope.metafizzy.co/ <br/>
 * Copyright © 2011-2012 David DeSandro / Metafizzy LLC
 */
public class TileBrowser extends HtmlWidget
{
	List<Tile> tiles = new ArrayList<Tile>();
	List<String> properties = new ArrayList<String>();

	public TileBrowser(String name, String label)
	{
		super(name, label);
	}

	public void add(Tile tile)
	{
		this.tiles.add(tile);
	}

	@Override
	public String toHtml()
	{
		StringBuffer html = new StringBuffer();

		// render the filter
		html.append("<ul id=\"" + getId() + "-filters\">\n");
		html.append("<a href=\"#\" data-filter=\"*\">show all</a>");
		for (String property : this.properties)
		{
			html.append("<a href=\"#\" data-filter=\"." + property + "\">" + property + "</a>");
		}
		html.append("</ul>");
		html.append("<script>\n$( '#" + getId() + "-filters' ).buttonset();\n</script>\n");

		// render the items
		html.append("<div id=\"" + getId() + "\" class=\"clearfix\">\n");

		for (Tile link : tiles)
		{
			html.append("\t<div class=\"tile");

			// add each property value as class for filtering
			boolean first = true;
			for (String tag : link.getTags())
			{
				html.append(" ");
				html.append(tag);
			}

			html.append("\"");

			html.append(">\n" + link.getHtml() + "</div>\n");
		}

		html.append("</div>\n");

		// jquery enhance
		html.append("<script>\n");
		html.append("// cache container\n");
		html.append("var $container = $('#" + getId() + "');\n");
		html.append("$container.isotope();\n");
		html.append("// filter items when filter link is clicked\n");
		html.append("$('#" + getId() + "-filters a').click(function(){\n");
		html.append("  var selector = $(this).attr('data-filter');\n");
		html.append("  $container.isotope({ filter: selector });\n");
		html.append("  return false;\n");
		html.append("});\n</script>");

		return html.toString();
	}

	public TileBrowser addProperty(String property)
	{
		this.properties.add(property);
		return this;
	}
}

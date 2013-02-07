package org.molgenis.framework.ui.tilebrowser;

public class ImageTitleTile extends Tile
{
	public ImageTitleTile(String img, String title)
	{
		super("<div style=\"width: 100px; height: 100px;  " + "border: solid thin black; margin: 5px; float: left; "
				+ "overflow: hidden; position: relative; text-align: center;\"><img src=\"" + img + "\"><h1>" + title
				+ "<h1></div>");
	}
}

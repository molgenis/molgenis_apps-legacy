/* Date:        February 5, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.mutation.ui.header;

import org.apache.commons.lang.StringUtils;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.mutation.ServiceLocator;
import org.molgenis.mutation.service.CmsService;
import org.molgenis.mutation.service.CmsServiceException;

public class Header extends EasyPluginController<HeaderModel> {
	private static final long serialVersionUID = 5933871906981851063L;
	private transient CmsService cmsService;

	public Header(String name, ScreenController<?> parent) {
		super(name, parent);
		this.setModel(new HeaderModel(this));
		this.getModel().setLogo(" ");
		this.getModel().setTitle(" ");
		this.cmsService = ServiceLocator.instance().getCmsService();
	}

	public ScreenView getView() {
		return new FreemarkerView("Header.ftl", getModel());
	}

	@Override
	public String getCustomHtmlHeaders() {
		String cssFormat = "<link rel=\"stylesheet\" style=\"text/css\" type=\"text/css\" href=\"%s\">\n";
		String jsFormat = "<script src=\"%s\" type=\"text/javascript\" language=\"javascript\"></script>";
		String headers = "";

		// cp res/css/colors.css generated-res/css
		// cp res/css/data.css generated-res/css
		// cp res/css/main.css generated-res/css
		// cp res/css/menu.css generated-res/css
		// cp res/scripts/all.js generated-res/scripts
		// cp res/img/*.jpg generated-res/img

		headers += String
				.format(cssFormat, "res/displaytag/css/displaytag.css");
		// headers += String.format(cssFormat, "res/displaytag/css/screen.css");
		// headers += String.format(cssFormat, "res/displaytag/css/site.css");
//		headers += String.format(cssFormat, "res/css/col7a1/colors.css");
//		headers += String.format(cssFormat, "res/css/col7a1/data.css");
		// headers += String.format(cssFormat, "res/css/main.css");
		// headers += String.format(cssFormat, "res/css/menu.css");
		// headers += String.format(jsFormat, "res/scripts/all.js");

		return headers;
	}

	@Override
	public void reload(Database db) {
		try {
			this.cmsService.setDatabase(db);
			org.molgenis.cms.Header header = this.cmsService.findHeader();

			if (StringUtils.isNotEmpty(header.getLogo())) {
				this.getModel().setLogo(header.getLogo());
			}
			if (StringUtils.isNotEmpty(header.getTitle())) {
				this.getModel().setTitle(header.getTitle());
			}
		} catch (CmsServiceException e) {
			// TODO: What to do here?
		}
	}
}

/* Date:        April 4, 2011
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.chd7.ui;

import org.molgenis.framework.ui.ScreenController;
import org.molgenis.mutation.ui.search.SearchPlugin;

/**
 * Genome browser for CHD7. TODO replace patientpager and mutationpager with
 * TableView when available.
 */
public class Search extends SearchPlugin
{
	private static final long serialVersionUID = 4159412082076885902L;

	public Search(String name, ScreenController<?> parent)
	{
		super(name, parent);
		this.getModel().setPatientPager("generated-res/mutation/patientPager.jsp");
		this.getModel().setMutationPager("generated-res/mutation/mutationPager.jsp");
		this.getModel().setPatientViewer("/org/molgenis/chd7/ui/patient.ftl");
		this.getModel().setMutationViewer("/org/molgenis/chd7/ui/mutation.ftl");
	}
}

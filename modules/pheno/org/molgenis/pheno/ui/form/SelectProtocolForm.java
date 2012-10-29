package org.molgenis.pheno.ui.form;

import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.Container;
import org.molgenis.framework.ui.html.HiddenInput;
import org.molgenis.framework.ui.html.SelectInput;

public class SelectProtocolForm extends Container
{
	/* The serial version UID of this class. Needed for serialization. */
	private static final long serialVersionUID = -4335059390415978096L;

	public SelectProtocolForm()
	{
		this.add(new HiddenInput("__target", ""));
		this.add(new HiddenInput("select", ""));
		this.add(new HiddenInput("__action", ""));
		this.add(new SelectInput("Protocol", ""));

		this.add(new ActionInput("show"));
		((ActionInput) this.get("show")).setButtonValue("Back to List mode");
		((ActionInput) this.get("show")).setLabel("Back to List mode");
		((ActionInput) this.get("show")).setTooltip("Back to List mode");

		this.add(new ActionInput("add"));
		((ActionInput) this.get("add")).setButtonValue("Apply Protocol");
		((ActionInput) this.get("add")).setLabel("Apply Protocol");
		((ActionInput) this.get("add")).setTooltip("Apply Protocol");
	}
}

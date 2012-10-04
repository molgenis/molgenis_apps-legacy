package org.molgenis.pheno.ui.form;

import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.Container;
import org.molgenis.framework.ui.html.HiddenInput;
import org.molgenis.framework.ui.html.Input;
import org.molgenis.framework.ui.html.TextLineInput;

public class IndividualForm extends Container
{
	/* The serial version UID of this class. Needed for serialization. */
	private static final long serialVersionUID = -4335059390415978096L;

	public IndividualForm()
	{
		this.add(new HiddenInput("__target", ""));
		this.add(new HiddenInput("select", ""));
		this.add(new HiddenInput("__action", ""));
		
		this.add(new ActionInput("show"));
		((ActionInput) this.get("show")).setButtonValue("Back to List mode");
		((ActionInput) this.get("show")).setLabel("Back to List mode");
		((ActionInput) this.get("show")).setTooltip("Back to List mode");

		this.add(new ActionInput("update"));
		((ActionInput) this.get("update")).setButtonValue("Save");
		((ActionInput) this.get("update")).setLabel("Save");
		((ActionInput) this.get("update")).setTooltip("Save");

		this.add(new ActionInput("edit"));
		((ActionInput) this.get("edit")).setButtonValue("Edit");
		((ActionInput) this.get("edit")).setLabel("Edit");
		((ActionInput) this.get("edit")).setTooltip("Edit");

		this.add(new ActionInput("select"));
		((ActionInput) this.get("select")).setButtonValue("Apply Protocol");
		((ActionInput) this.get("select")).setLabel("Apply Protocol");
		((ActionInput) this.get("select")).setTooltip("Apply Protocol");
	}
	
	public void addTextLineInput(Integer observedValueId, String value)
	{
		TextLineInput<String> valueInput = new TextLineInput<String>(observedValueId.toString());
		valueInput.setValue(value);
		this.add(valueInput);
	}
	
	public Input<?> showInput(String name)
	{
		return this.get(name);
	}
}

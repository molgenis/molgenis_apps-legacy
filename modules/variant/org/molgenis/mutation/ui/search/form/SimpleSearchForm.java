package org.molgenis.mutation.ui.search.form;

import java.util.Arrays;

import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.AutocompleteInput;
import org.molgenis.framework.ui.html.Container;
import org.molgenis.framework.ui.html.HiddenInput;
import org.molgenis.framework.ui.html.HtmlInputException;
import org.molgenis.framework.ui.html.RadioInput;
import org.molgenis.variant.Variant;

public class SimpleSearchForm extends Container
{

	private static final long serialVersionUID = -8848955017610414423L;

	public SimpleSearchForm()
	{
		this.add(new HiddenInput("__target", ""));
		this.add(new HiddenInput("select", ""));
		this.add(new HiddenInput("__action", "findMutationsByTerm"));
		AutocompleteInput<Variant> termInput = new AutocompleteInput<Variant>("term", "", "org.molgenis.variant.Variant", "nameCdna", "");
		termInput.setClazz("simple_search");
		termInput.setSize(50);
		this.add(termInput);
		String[] options = new String[] { "mutations", "patients" };
		String[] labels  = new String[] { "Show mutations", "Show patients" };
		try
		{
			this.add(new RadioInput("result", "", "", false, true, "", Arrays.asList(options), Arrays.asList(labels)));
		}
		catch (HtmlInputException e)
		{
			//Should never be thrown
		}
		this.add(new ActionInput("findMutationsByTerm"));
		((ActionInput) this.get("findMutationsByTerm")).setLabel("Search");
		((ActionInput) this.get("findMutationsByTerm")).setTooltip("Search");
		((ActionInput) this.get("findMutationsByTerm")).setButtonValue("Search");
	}
}

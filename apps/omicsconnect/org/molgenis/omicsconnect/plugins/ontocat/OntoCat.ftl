<#macro OntoCatTest screen>
<!-- normally you make one big form for the whole plugin-->
<form method="post" enctype="multipart/form-data" name="${screen.name}" action="">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}">
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action">
	<!--need to be set to "true" in order to force a download-->
	<input type="hidden" name="__show">
	
	
<!-- this shows a title and border -->
	<div class="formscreen">
		<div class="form_header" id="${screen.getName()}">
		${screen.label}
		</div>
		
		<#--optional: mechanism to show messages-->
		<#list screen.getMessages() as message>
			<#if message.success>
		<p class="successmessage">${message.text}</p>
			<#else>
		<p class="errormessage">${message.text}</p>
			</#if>
		</#list>
		
<#if screen.myModel?exists>
	<#assign modelExists = true>
	<#assign model = screen.myModel>
<#else>
	No model. An error has occurred.
	<#assign modelExists = false>
</#if>

<#--<input type="submit" value="Studies" id="loadStudies" onclick="document.forms.${screen.name}.__action.value = 'LoadStudies'; document.forms.${screen.name}.submit();"/>-->

<#--<input type="submit" value="Panels" id="loadPanels" onclick="document.forms.${screen.name}.__action.value = 'LoadPanels'; document.forms.${screen.name}.submit();"/>-->

<#if model.ontologies??>


<input type="submit" value="Query" id="loadonto" onclick="document.forms.${screen.name}.__action.value = 'query'; document.forms.${screen.name}.submit();"/>

<#list model.ontologies as ontology>

<input type="radio" name="ontology" value="${ontology.ontologyAccession}"/>${ontology.label}<br />

</#list>

<#else>

<p>No ontologies loaded<p>

</#if>



<#if model.results??>

<#list model.results as result>

${result.label}
<br>
</#list>

<#else>

<p>No results loaded<p>

</#if>

	</div>
</form>
</#macro>

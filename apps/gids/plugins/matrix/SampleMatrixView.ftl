<!-- normally you make one big form for the whole plugin-->
<form method="post" enctype="multipart/form-data" name="${model.name}" action="">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${model.name}">
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action">
	
<!-- this shows a title and border -->
	<div class="formscreen">
		<div class="form_header" id="${model.getName()}">
		${model.label}
		</div>
		<div id="protocols">
			<span><a></a>&nbsp;</span>
			<span class="${model.getSampleNavClass()}"><a href="molgenis.do?__target=${screen.name}&__action=setSample">Sample_info</a></span>
			<span class="${model.getDnaNavClass()}"><a href="molgenis.do?__target=${screen.name}&__action=setDNA">DNA</a></span>
			<span class="${model.getRnaNavClass()}"><a href="molgenis.do?__target=${screen.name}&__action=setRNA">RNA</a></span>
			<span class="${model.getSerumNavClass()}"><a href="molgenis.do?__target=${screen.name}&__action=setSerum">Serum</a></span>
			<span class="${model.getPlasmaNavClass()}"><a href="molgenis.do?__target=${screen.name}&__action=setPlasma">Plasma</a></span>
			<span class="${model.getBiopsiesNavClass()}"><a href="molgenis.do?__target=${screen.name}&__action=setBiopsies">Biopsies</a></span>
			<span class="${model.getHlaNavClass()}"><a href="molgenis.do?__target=${screen.name}&__action=setHLA">HLA_Typing</a></span>
			
		</div>
		<#--optional: mechanism to show messages-->
		<#list model.getMessages() as message>
			<#if message.success>
		<p class="successmessage">${message.text}</p>
			<#else>
		<p class="errormessage">${message.text}</p>
			</#if>
		</#list>
		
		<div class="screenbody">
			<div class="screenpadding">	
			
<#--begin your plugin-->	
<#if !model.error>
	${model.getMatrixViewerSample()}


	<input type="submit" name="setSelection" value='Get selected from matrix' onclick='__action.value="setSelection"' />

	<#if model.getSelection()??>
		<div id="protocols2">
			<span><a></a>&nbsp;</span>
			<span class="${model.getIndividualNavClass()}"><a href="molgenis.do?__target=${screen.name}&__action=Individual_info">Individual_info</a></span>
			<span class="${model.getPersonalNavClass()}"><a href="molgenis.do?__target=${screen.name}&__action=Personal_info">Personal_info</a></span>
			<span class="${model.getMedicalNavClass()}"><a href="molgenis.do?__target=${screen.name}&__action=Medical_info">Medical_info</a></span>
		</div>
	
		${model.getMatrixViewerIndv()}
		<p>You selected from the matrix: ${model.getSelection()}</p>
	</#if>
<#else>
	<p>There is a problem, probably there is no protocol given.</p>
	<a href="http://localhost:8080/molgenis_apps/molgenis.do?__target=mainmenu&select=protocol">Go to protocols </a>
	
</#if>
<#--end of your plugin-->	
			</div>
		</div>
	</div>
	
</form>

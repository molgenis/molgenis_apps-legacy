<#macro plugins_qtlfinder2_linkback_LinkBack screen>
<#if screen.myModel?exists>
	<#assign modelExists = true>
	<#assign model = screen.myModel>
<#else>
	No model. An error has occurred.
	<#assign modelExists = false>
</#if>

<!-- normally you make one big form for the whole plugin-->
<form method="post" enctype="multipart/form-data" name="${screen.name}" action="">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}">
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action">
	
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
		
		<div class="screenbody">
			<div class="screenpadding">	
<#--begin your plugin-->

<#if model.id??>
	<a href="molgenis.do?__target=QtlFinderPublic2&select=QtlFinderPublic2&__comebacktoscreen=${screen.getName()}&p=${model.id}" target="_blank"><img src="clusterdemo/icons/icon_plaintext_plot.png" alt=""/> Plot this trait in <b>Find QTLs</b></a>
<#else>
	Plot not available.
</#if>

<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>

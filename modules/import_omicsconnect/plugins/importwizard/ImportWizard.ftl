<#macro ImportWizard screen>
<#assign model = screen.myModel>
<!-- normally you make one big form for the whole plugin-->
<form method="post" enctype="multipart/form-data" name="${screen.name}" action="">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}">
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action">
	<!--need to be set to "true" in order to force a download-->
	<input type="hidden" name="__show">
			
	<#assign center = "align=\"center\"">
	<#assign courier = "<font face=\"Courier New, Courier, mono, serif\">">	
	<#assign endFont = "</font>">
	
	<#assign greenBg = "<font style=\"background-color: #52D017\">"> <#--successmess: 52D017-->
	<#assign orangeBg = "<font style=\"background-color: #FFA500\">">
	<#assign redBg = "<font style=\"background-color: red; color: white; font-weight:bold\">">
		
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

<#if model.page == 0>
<h1>Data import wizard</h1>
<input type="file" name="upload"/>
<a href="clusterdemo/ExampleExcel.xls"><img src="clusterdemo/excel.gif"/></a><label>Download example Excel file</label>
<@controls screen.name model />
<#elseif model.page == 1>
<h1>Validation Check</h1>
<h3>Data</h3>
<table class="listtable">
	<tr class="form_listrow0">
		<td>Name</td>
		<td>Importable?</td>
	</tr>
	
	<#list model.dataImportable?keys as name>
	<tr class="form_listrow1">
		<td>${name}</td>
		<td><#if model.dataImportable[name] == true><p class="successmessage">Yes</p><#else><p class="errormessage">No</p></#if></td>
	</tr>
	</#list>
</table>
<h3>Entities</h3>
<table class="listtable">
	<tr class="form_listrow0">
		<td>Name</td>
		<td>Importable?</td>
	</tr>
	<#list model.entitiesImportable?keys as entity>
	<tr class="form_listrow1">
		<td><#if model.entitiesImportable[entity] == true><a href="generated-doc/fileformat.html#${entity}_entity">${entity}</a><#else>${entity}</#if></td>
		<td><#if model.entitiesImportable[entity] == true><p class="successmessage">Yes</p><#else><p class="errormessage">No</p></#if></td>
	</tr>
	</#list>
</table>
<h3>Entity Fields</h3>
<table class="listtable">
	<tr class="form_listrow0">
		<td>Name</td>
		<td>Detected</td>
		<td>Required</td>
		<td>Available</td>
		<td>Unknown</td>
	</tr>
<#list model.entitiesImportable?keys as entity>
	<#if model.entitiesImportable[entity] == true>
	<tr class="form_listrow1">
		<td><a href="generated-doc/fileformat.html#${entity}_entity">${entity}</a></td>
		<td><#if model.fieldsDetected[entity]?size gt 0><#list model.fieldsDetected[entity] as field>${greenBg}${field}${endFont}<#if field_has_next>, </#if></#list><#else><p class="errormessage">No detected fields</p></#if></td>
		<td><#if model.fieldsRequired[entity]?size gt 0><#list model.fieldsRequired[entity] as field>${redBg}${field}${endFont}<#if field_has_next>, </#if></#list><#else><p class="successmessage">No missing fields</p></#if></td>
		<td><#if model.fieldsAvailable[entity]?size gt 0><#list model.fieldsAvailable[entity] as field>${orangeBg}${field}${endFont}<#if field_has_next>, </#if></#list><#else><p class="successmessage">No optional fields</p></#if></td>
		<td><#if model.fieldsUnknown[entity]?size gt 0><#list model.fieldsUnknown[entity] as field>${redBg}${field}${endFont}<#if field_has_next>, </#if></#list><#else><p class="successmessage">No unknown fields</p></#if></td>
	</tr>
	</#if>
</#list>
</table>
<#if model.importError>
<h3>Validation Error</h3>
An error occurred validating the input data. Please resolve the errors and try again.   
</#if>
<@controls screen.name model />
<#elseif model.page == 2>
<h1>Import options</h1>
<input type="radio" name="storage_option" value="add" checked>Add<br>
<input type="radio" name="storage_option" value="add_ignore">Add (ignore existing)<br>
<input type="radio" name="storage_option" value="add_update">Add (update existing)<br>
<input type="radio" name="storage_option" value="update">Update<br>
<input type="radio" name="storage_option" value="update_ignore">Update (ignore missing)<br>
<@controls screen.name model />
<#elseif model.page == 3>

<br>
<#if model.importError>
Your import failed. See the above message for details. Please go back to the first screen and upload a new file.
<#else>
<p class="successmessage">Your import was successful.</p>
</#if>
<@controls screen.name model />
</#if>
<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>
<#macro controls form model>
<div align="center">
<input type="submit" value="&#60; Back" onclick="document.forms.${form}.__action.value = 'screen${model.page - 1}'; document.forms.${form}.submit();"<#if model.disableBack> disabled</#if><#if model.firstPage> hidden</#if>/>
<input type="submit" value="Next &#62;" onclick="document.forms.${form}.__action.value = 'screen${model.page + 1}'; document.forms.${form}.submit();"<#if model.disableNext> disabled</#if><#if model.lastPage> hidden</#if>/>
<#if !model.firstPage && !model.lastPage>
<input type="submit" value="Cancel" onclick="document.forms.${form}.__action.value = 'screen0'; document.forms.${form}.submit();"/>
</#if>
<#if model.lastPage>
<input type="submit" value="Finish" onclick="document.forms.${form}.__action.value = 'screen0'; document.forms.${form}.submit();"/>
</#if>
</div>
</#macro>
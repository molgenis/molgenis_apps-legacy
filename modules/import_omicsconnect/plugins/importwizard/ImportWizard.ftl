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
<div style="width: 1140px; margin-left: auto; margin-right: auto;">
<#if model.page == 0>
<h2>Data Import Wizard - Step ${model.page + 1} of ${model.nrPages}</h2>
<p>Upload <a href="generated-doc/fileformat.html" target="_blank">OmicsConnect</a> data file</p>
<input type="file" name="upload" style="width: 100%;"/>
<@controls screen.name model />
<#elseif model.page == 1>
<h2>Validation Check - Step ${model.page + 1} of ${model.nrPages}</h2>
<#if model.dataImportable??>
<h3>Data</h3>
<table class="listtable" style="width: 25%;">
	<tr class="form_listrow0">
		<td>Name</td>
		<td style="width: 25%; text-align: center;">Importable</td>
	</tr>
	<#list model.dataImportable?keys as name>
	<tr class="form_listrow1">
		<td>${name}</td>
		<td><#if model.dataImportable[name] == true><p class="successmessage">Yes</p><#else><p class="errormessage">No</p></#if></td>
	</tr>
	</#list>
</table>
</#if>
<#if model.entitiesImportable??>
<h3>Entities</h3>
<table class="listtable" style="width: 25%;">
	<tr class="form_listrow0">
		<td>Name</td>
		<td style="width: 25%; text-align: center;">Importable</td>
	</tr>
	<#list model.entitiesImportable?keys as entity>
	<tr class="form_listrow1">
		<td><#if model.entitiesImportable[entity] == true><a href="generated-doc/fileformat.html#${entity}_entity" target="_blank">${entity}</a><#else>${entity}</#if></td>
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
</#if>
<#if model.validationError>
<h3>Validation Error</h3>
An error occurred validating the input data. Please resolve the errors and try again.   
</#if>
<@controls screen.name model false model.validationError/>
<#elseif model.page == 2>
<h2>Entity Import options - Step ${model.page + 1} of ${model.nrPages}</h2>
<table class="listtable" style="width: 75%;">
	<tr>
		<td style="width: 30%;"><input type="radio" name="entity_option" value="add" checked>Add entities</td>
		<td>Importer adds new entities or fails if entity exists<td>
	</tr>
	<tr>
		<td><input type="radio" name="entity_option" value="add_ignore">Add entities / ignore existing</td>
		<td>Importer adds new entities or skips if entity exists<td>
	</tr>
	<tr>
		<td><input type="radio" name="entity_option" value="add_update">Add entities / update existing</td>
		<td>Importer adds new entities or updates existing entities<td>
	</tr>
	<tr>
		<td><input type="radio" name="entity_option" value="update">Update Entities<br></td>
		<td>Importer updates existing entities or fails if entity does not exist</td>
	</tr>
	<tr>
		<td><input type="radio" name="entity_option" value="update_ignore">Update Entities / ignore existing<br></td>
		<td>Importer updates existing entities or skips if entity does not exist</td>
	</tr>
</table>
<@controls screen.name model />
<#elseif model.page == 3>
<h2>Data Import options - Step ${model.page + 1} of ${model.nrPages}</h2>
<h3>Target options</h3>
<table class="listtable" style="width: 75%;">
	<tr>
		<td style="width: 30%;"><input type="radio" name="data_target_option" value="add" checked>Add Targets</td>
		<td>Importer adds data for new targets in dataset or fails if target exists<td>
	</tr>
	<tr>
		<td><input type="radio" name="data_target_option" value="add_ignore">Add Targets / ignore existing</td>
		<td>Importer adds data for new targets in dataset or skips if target exists<td>
	</tr>
	<tr>
		<td><input type="radio" name="data_target_option" value="add_update">Add Targets / update existing</td>
		<td>Importer adds data for new targets in dataset or updates existing measurements<td>
	</tr>
	<tr>
		<td><input type="radio" name="data_target_option" value="update">Update Targets<br></td>
		<td>Importer updates existing targets in dataset or fails if target does not exist</td>
	</tr>
	<tr>
		<td><input type="radio" name="data_target_option" value="update_ignore">Update Targets / ignore existing<br></td>
		<td>Importer updates existing targets in dataset or skips if target does not exist</td>
	</tr>
</table>
<h3>Feature options</h3>
<table class="listtable" style="width: 75%;">
	<tr>
		<td style="width: 30%;"><input type="radio" name="data_feature_option" value="add" checked>Add Features</td>
		<td>Importer adds data for new features in dataset or fails if feature exists<td>
	</tr>
	<tr>
		<td><input type="radio" name="data_feature_option" value="add_ignore">Add Features / ignore existing</td>
		<td>Importer adds data for new features in dataset or skips if feature exists<td>
	</tr>
	<tr>
		<td><input type="radio" name="data_feature_option" value="add_update">Add Features / update existing</td>
		<td>Importer adds data for new features in dataset or updates existing features<td>
	</tr>
	<tr>
		<td><input type="radio" name="data_feature_option" value="update">Update Features<br></td>
		<td>Importer updates existing features in dataset or fails if feature does not exist</td>
	</tr>
	<tr>
		<td><input type="radio" name="data_feature_option" value="update_ignore">Update Features / ignore existing<br></td>
		<td>Importer updates existing features in dataset or skips if feature does not exist</td>
	</tr>
</table>
<@controls screen.name model />
<#elseif model.page == 4>
<h2>Import Summary - Step ${model.page + 1} of ${model.nrPages}</h2>
<#if model.importError>
<h3>Import Error</h3>
Your import failed. See the above message for details. Please go back to the first screen and upload a new file.
<#else>
<p class="successmessage">Your import was successful.</p>
</#if>
</div>
<@controls screen.name model model.importError==false model.importError==false />
</#if>
<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>
<#-- wizard button strip macro -->
<#macro controls form model disableBack=false disableNext=false>
<div align="center" style="padding: 10px;">
<input type="submit" value="&#60; Back" onclick="document.forms.${form}.__action.value = 'screen${model.page - 1}'; document.forms.${form}.submit();"<#if disableBack> disabled</#if><#if model.firstPage> hidden</#if>/>
<input type="submit" value="Next &#62;" onclick="document.forms.${form}.__action.value = 'screen${model.page + 1}'; document.forms.${form}.submit();"<#if disableNext> disabled</#if><#if model.lastPage> hidden</#if>/>
<#if !model.firstPage && !model.lastPage>
<input type="submit" value="Cancel" onclick="document.forms.${form}.__action.value = 'cancel'; document.forms.${form}.submit();"/>
</#if>
<#if model.lastPage>
<input type="submit" value="Finish" onclick="document.forms.${form}.__action.value = 'finish'; document.forms.${form}.submit();"/>
</#if>
</div>
</#macro>
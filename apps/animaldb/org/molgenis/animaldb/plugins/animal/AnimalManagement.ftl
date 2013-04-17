<#macro org_molgenis_animaldb_plugins_animal_AnimalManagement screen>
<!-- normally you make one big form for the whole plugin-->
<form method="post" enctype="multipart/form-data" name="${screen.name}">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}"" />
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action" />
	
<!-- this shows a title and border -->
	<div class="formscreen">
		<div class="form_header" id="${screen.getName()}">
		${screen.label}
		</div>
		
		<#--optional: mechanism to show messages-->
		<#list screen.getMessages() as message>
			<#if message.success>
				<script>$.ctNotify("${message.text}", {type: 'confirmation', delay: 5000});</script>
				<!-- <p class="successmessage">${message.text}</p> -->
			<#else>
				<script>$.ctNotify("${message.text}", {type: 'error', delay: 7000});</script>	        	
				<!-- <p class="errormessage">${message.text}</p> -->
			</#if>
		</#list>
		
		<div class="screenbody">
			<div class="screenpadding">	
			
<#--begin your plugin-->	

<label for="animalmatrix">Choose animal:</label>
${screen.animalMatrix}
<br />

<label for="usersList">List of users:</label>
	<select name="usersList" id="usersList" class="selectbox">
		<#if screen.listOfUsersString??>
			<#list screen.listOfUsersString as user>
				<option value="${user}" <#if user == user>selected="selected"</#if>>${user}</option>
			</#list>
		</#if>
	</select>
	<input type='submit' id='selectedAnimals' class='addbutton' value='Give rights' onclick="__action.value='Chosen Animals'" />

<#if screen.info??><p>${screen.info}</p></#if>
	
<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>

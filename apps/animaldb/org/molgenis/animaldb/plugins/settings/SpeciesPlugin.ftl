<#macro org_molgenis_animaldb_plugins_settings_SpeciesPlugin screen>
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
		<p class="successmessage">${message.text}</p>
			<#else>
		<p class="errormessage">${message.text}</p>
			</#if>
		</#list>
		
		<div class="screenbody">
			<div class="screenpadding">	
<#--begin your plugin-->

<#if screen.action == "Import">

	<p><a href="molgenis.do?__target=${screen.name}&__action=init">Back to overview</a></p>

	Import

<#elseif screen.action == "Add">

	<p><a href="molgenis.do?__target=${screen.name}&__action=init">Back to overview</a></p>

	<div class="row">
		<label for="speciesname">Species Name:</label>
		<input type="text" name="speciesname" id="speciesname" class="textbox" />
	</div>
	<div class="row">
		<label for="specieslatinname">Latin name:</label>
		<input type="text" name="specieslatinname" id="specieslatinname" class="textbox" />
	</div>
	<div class="row">
		<label for="speciesdutchname">Dutch name:</label>
		<input type="text" name="speciesdutchname" id="speciesdutchname" class="textbox" />
	</div>

	<div class="row">
		<label for="nvwacategory">NVWA species category:</label>
		<input type="text" name="nvwacategory" id="nvwacategory" class="textbox" />
	</div>
	<!--
	<div class="row">
		<label for="species">NVWA species category:</label>
		<select name="nvwacategory" id="nvwacategory" class="selectbox">
			<#if screen.nvwaCategoryList??>
				<#list screen.nvwaCategoryList as species>
					<option value="${nvwaCategory.name}">${nvwaCategory.name}</option>
				</#list>
			</#if>
		</select>
	</div>-->
	<div class="row">
		<label for="prefixstring">species prefix string:</label>
		<input type="text" name="nprefixstring" id="prefixstring" class="textbox" />
	</div>
	
	<div class='row'>
		<input type='submit' class='addbutton' value='Add' onclick="__action.value='addSpecies'" />
	</div>
	
	<div id='buttons_part' class='row'>
	<input type='submit' class='addbutton' value='Add' onclick="__action.value='addSpecies'" />
	</div>

<#else>

	<p>
		<a href="molgenis.do?__target=${screen.name}&__action=Add">Add new species</a>
	</p>

	<#if screen.speciesList?size gt 0>
		<table cellpadding="0" cellspacing="0" border="0" class="display" id="spectable">
			<thead>
				<tr>
					<th>Name</th>
					<th>Latin name</th>
					<th>Dutch name</th>
					<th>NVWA category</th>
				</tr>
			</thead>
			<tbody>
			<#list screen.speciesList as spec>
				<#assign specId = spec.getId()>
				<tr>
					<td>${spec.name}</td>
					<td>${screen.getLatinName(specId)}</td>
					<td>${screen.getDutchName(specId)}</td>
					<td>${screen.getVwaName(specId)}</td>
				</tr>
			</#list>
			</tbody>
		</table>
	<#else>
		<p>There are no species yet</p>
	</#if>
</#if>
	
<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>

<script>
	var oTable = jQuery('#spectable').dataTable(
	{ "bProcessing": true,
	  "bServerSide": false,
	  "sPaginationType": "full_numbers",
	  "bSaveState": true,
	  "bAutoWidth": false,
	  "bJQueryUI" : true }
	);
</script>

</#macro>

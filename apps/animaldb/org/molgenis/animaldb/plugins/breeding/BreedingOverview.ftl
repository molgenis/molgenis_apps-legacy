<#macro org_molgenis_animaldb_plugins_breeding_BreedingOverview screen>
	
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

	
	<form method="post" enctype="multipart/form-data" name="${screen.name}">
		<!--needed in every form: to redirect the request to the right screen-->
		<input type="hidden" name="__target" value="${screen.name}"" />
		<!--needed in every form: to define the action. This can be set by the submit button-->
		<input type="hidden" name="__action" />

	
		<div>
		<#if screen.lineList??>
			<#if screen.lineList?size gt 0>
				<table cellpadding="0" cellspacing="0" border="0" class="display" id="breedingOverviewTable">
					<thead>
						<tr>
							<th style="font-weight:bold;">Name</th>
							<th style="font-weight:bold;">Species</th>
							<th style="font-weight:bold;">&#9794;</th>
							<th style="font-weight:bold;">&#9792;</th>
							<th style="font-weight:bold;">&#9794;/&#9792 ?</th>
							<th style="font-weight:bold;">parent groups:</th>
							<th style="font-weight:bold;">litters</th>
							<th style="font-weight:bold;">unweaned</th>
							<th style="font-weight:bold;">Source</th>
							
						</tr>
					</thead>
					<tbody>
					<#list screen.lineList as line>
						<#assign lineName = line.getName()>
						<tr>
							<td>${lineName}</td>
							<td>${screen.getSpeciesName(lineName)}</td>
							<td>${screen.getCountPerSex(lineName, "Male")}</td>
							<td>${screen.getCountPerSex(lineName, "Female")}</td>
							<td>${screen.getCountPerSex(lineName, "UnknownSex")}</td>
							<td></td>
							<td></td>
							<td></td>
							<td>${screen.getSourceName(lineName)}</td>
							<!--<td><a href='molgenis.do?__target=${screen.name}&__action=Delete&id=${line.id?string.computer}'><img id="delete_breedingline" class="edit_button" title="delete current record" alt="Delete" src="generated-res/img/delete.png"></a></td-->
						</tr>
					</#list>
					</tbody>
				</table>
			<#else>
				<p>There are no breeding lines yet.</p>
			</#if>
		<#else>
			<p>There are no breeding lines yet.</p>
		</#if>
	</div>

<script>
	var oTable = jQuery('#breedingOverviewTable').dataTable(
	{ "bProcessing": true,
	  "bServerSide": false,
	  "bSort": true,
	  "bInfo": true,
	  "sPaginationType": "full_numbers",
	  "bLengthChange": true,
	  "bSaveState": true,
	  "bAutoWidth": true,
	  "bJQueryUI": true
	  }
	  
	);
	
	
</script>

<#--end of your plugin-->	

</#macro>
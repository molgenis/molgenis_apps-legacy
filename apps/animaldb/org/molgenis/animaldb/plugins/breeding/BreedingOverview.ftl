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

<#if screen.action == "init">

	<p><a href="molgenis.do?__target=${screen.name}&__action=init">Back to overview</a></p>
	
	<form method="post" enctype="multipart/form-data" name="${screen.name}">
		<!--needed in every form: to redirect the request to the right screen-->
		<input type="hidden" name="__target" value="${screen.name}"" />
		<!--needed in every form: to define the action. This can be set by the submit button-->
		<input type="hidden" name="__action" />
		
		<div>
		<p><h2>Existing breeding lines</h2></p>
		<#if screen.lineList??>
			<#if screen.lineList?size gt 0>
				<table cellpadding="0" cellspacing="0" border="0" class="display" id="breedingOverviewTable">
					<thead>
						<tr>
							<th>Name</th>
							<th>Full name</th>
							<th>Species</th>
							<th>Source</th>
							<th>Remarks</th>
							
						</tr>
					</thead>
					<tbody>
					<#list screen.lineList as line>
						<#assign lineName = line.getName()>
						<tr>
							<td>${lineName}</td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
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
</#if>

<script>
	var oTable = jQuery('#breedingOverviewTable').dataTable(
	{ "bProcessing": true,
	  "bServerSide": false,
	  "sPaginationType": "full_numbers",
	  "bSaveState": true,
	  "bAutoWidth": false,
	  "bJQueryUI": true,
	  "aoColumnDefs": [ 
      	{ "sWidth": "30px", "aTargets": [ 0 ] }
    	] 
	  }
	);
	
	
</script>

<#--end of your plugin-->	

</#macro>
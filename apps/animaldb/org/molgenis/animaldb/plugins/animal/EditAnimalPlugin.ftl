<#macro org_molgenis_animaldb_plugins_animal_EditAnimalPlugin screen>
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

<#if screen.action="editAnimals">
	${screen.editTable}
	<div id='animalActions' style='float:left; background-color: #D3D6FF;padding:5px;margin:5px;border-radius: 5px; border:1px solid #5B82A4'>
			<table cellpadding="0" cellspacing="0" border="0" class="display" id="litterActionsTable">
				<tbody>
					<tr style="text-align:center;">
						<td><input id="saveAnimals" type="image" title="Save your" onclick="__action.value='saveAnimals'" src="generated-res/img/save.png"  /></td>
						<td><input id="cancel" type="image" title="Cancel" onclick="__action.value='start'" src="generated-res/img/cancel.png"  /></td>
					</tr>
				</tbody>
			</table>
		</div>
<#elseif screen.action="nvwa4Animals">
	<div id="yearselect" class="row">
		<label for="year">Year:</label>
		<select name="year" id="year" class="selectbox">
			<#list screen.getLastYearsList() as y>
				<option value="${y?string.computer}" <#if screen.year??><#if screen.year==y>selected="selected"</#if></#if> >${y?string.computer}</option>
			</#list>
		</select>
	</div>
	<div id="formselect" class="row">
		<label for="form">Form:</label>
		<select name="form" id="form" class="selectbox">
			<option value="A" <#if screen.form??><#if screen.form=="A">selected="selected"</#if></#if> >A</option>
			<option value="B" <#if screen.form??><#if screen.form=="B">selected="selected"</#if></#if> >B</option>
			<option value="C" <#if screen.form??><#if screen.form=="C">selected="selected"</#if></#if> >C</option>
			<!--option value="5" <#if screen.form??><#if screen.form=="5">selected="selected"</#if></#if> >5</option--!>
		</select>
	</div>
	<div class='row'>
		<input type='submit' id='generate' class='addbutton' value='Generate' onclick="__action.value='generateNvwa4Report'" />
	</div>
	
<#elseif screen.action="nvwa4ShowReport">
${screen.report}
<p><a href="molgenis.do?__target=${screen.name}&__action=init">Back to overview</a></p>

<#else>
	
		${screen.animalMatrix}

	<div id='animalActions' style='float:left; background-color: #D3D6FF;padding:5px;margin:5px;border-radius: 5px; border:1px solid #5B82A4'>
			<table cellpadding="0" cellspacing="0" border="0" class="display" id="litterActionsTable">
				<thead>
					<tr style="text-align:center;">
						<th>Edit</th>
						<th>nVWA 4 report</th>
						<!--th>Delete</th-->
						<!--th>Print Cagelabels</th-->
					</tr>
				</thead>
				<tbody>
					<tr style="text-align:center;">
						<td><input id="editAnimals" type="image" title="Edit the selected animals." onclick="__action.value='editAnimals'" src="generated-res/img/editview.gif"  /></td>
						<td><input id="nvwa4Animals" type="image" title="make nvwa form 4 report on selection" onclick="__action.value='nvwa4Animals'" src="res/img/report-icon_32x32.png"  /></td>
						
						<!--td><input id="deleteAnimals" type="image" title="Delete the selected animals." onclick="__action.value='deleteAnimals'" src="generated-res/img/delete.png"  /></td-->
						<!--td><input id="printCagelabels" type="image" title="Print cage labels for the selected animals." onclick="__action.value='makeLabels'" src="res/img/print_32.png"  /></td-->
					</tr>
				</tbody>
			</table>
		</div>
		
</#if>	
	
<div id="pushdown" style="clear:both;" ></div>
	
	
<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>

<script>
	jQuery('#editAnimals').button()
	jQuery('#nvwa4Animals').button()
	jQuery('#deleteAnimals').button()
	jQuery('#printCagelabels').button()
	jQuery('#saveAnimals').button()
	jQuery('#cancel').button()
</script>
</#macro>

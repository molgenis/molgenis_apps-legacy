<#macro ProtocolViewer screen>
<#assign model = screen.myModel>
<#assign url_base = "molgenis.do?__target=${screen.name}">
<!-- normally you make one big form for the whole plugin-->
<form method="post" enctype="multipart/form-data" name="${screen.name}" action="">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}">
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action" id="test" value="">

	<div class="formscreen">
		
		<div class="form_header" id="${screen.name}">
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
				<table class="box" width="100%" cellpadding="0" cellspacing="0" style="border-right:1px solid lightgray">
					<tr>
						<td class="box-header" colspan="2">
							<div id="datasets">  
						        <label style='font-size:14px'>
						        	<#if (model.dataSets?size == 1)>Catalog:<#elseif (model.dataSets?size > 1)>Choose a Catalog:</#if>
									<#list model.dataSets as dataSet>
										<input type="button" value="${dataSet.name}" id="dataset${dataSet.id}">
									</#list>
								</label>
							</div>
 						</td>
		    		</tr>
		    		<tr>
		    			<td class="box-body" style="width:50%;">
							<select id="selectedField" name="selectedField" title="choose field" name="chooseField" style="display:none"> 
								<#assign searchFilters = ["All", "Protocols", "Variables"]>
								<#list searchFilters as searchFilter>
									<option value="${searchFilter}" <#if searchFilter_index == 0>selected</#if>Search ${searchFilter}		
								</#list>
							</select>
							<input title="fill in search term" type="text" name="InputToken" id="InputToken"
								onfocus="selectedField.style.display='inline'; selectedField.style.display='inline';" 
								onkeyup="checkSearchingStatus();" onkeypress="if(event.keyCode === 13){;return whetherReload();}">
								
							
							<input type="button" id="SearchCatalogueTree" class='addbutton ui-button ui-widget ui-state-default ui-corner-all' name="SearchCatalogueTree" 
							value="search" style="font-size:0.8em" onclick="whetherReload()"/>
							<input type="button" id="clearSearchingResult" class='addbutton ui-button ui-widget ui-state-default ui-corner-all' name="clearSearchingResult" 
							value="clear" style="font-size:0.8em"/>
				    	</td>
				    <td class="box-body" style="width: 50%"><div >Details:</div></td></tr>
				    <tr>
				    	<td class="box-body">
				    		<div id="treeHeader" style="display:none;background:#DDDDDD;height:30px;font-size:20px;text-align:center">View all the data items in the study</div>
							<div id="dataset-view">
							</div>
						</td>
					    <td class="box-body" id="showInformation"> 
					    	<table  style="height:500px;width:100% ">
						    	<tr>
							    	<td style="height:250px; padding:0px" >
								    	<div id="showDetailsHeader" style="display:none;background:#DDDDDD;height:30px;font-size:20px;text-align:center">View the variable details here</div>
								    	<div id="feature-details" style="height:250px;overflow:auto">
								    		
		      							</div>
  									</td>
								</tr>
								<tr>
							    	<td style="height:20px; border-top:1px solid lightgray;">
										<div id="selectionInformationHeader" style="display:none;background:#DDDDDD;height:30px;font-size:20px;text-align:center">View your selection here</div>
										<div id="selectionState" >Your selection:
											<div id="popUpDialogue" style="float:right;display:none">Click to see details</div>
											<div id="traceBack" style="float:right;display:none">Locate the variable in the tree</div>
											<div id="selectNotification" style="font-size:20px" ></br></br>Please select a variable in the tree</div>
										</div>
									</td>
								</tr>
								<tr>
						    		<td>
		  								<div id="selectionHeader" style="margin:0px"></div>
									</td>
								</tr>
								<tr>
						    		<td style="height:185px" >
		  								<div id="feature-selection" style="height:185px; overflow:auto; width:100%"></div>
									</td>
								</tr>
								<tr>
									<td style="height:25px; border-top:1px solid lightgray; margin:0px;padding:0px">
			  							<div id="selection" style="height:25px; margin:0px;padding:0px">
											<div style="float:right">
					 							<input class='addbutton ui-button ui-widget ui-state-default ui-corner-all' type="submit" id="downloadButton" name="downloadButton" value="Download as Excel" 
												 onclick="__action.value='downloadButton';"/>
			 								</div>
			 								<div style="float:right">
					 							<input type="submit" class='addbutton ui-button ui-widget ui-state-default ui-corner-all' id="downloadButtonEMeasure" name="downloadButton" value="Download as E-Measure" 
												 onclick="__action.value='downloadButtonEMeasure';"/>
			 								</div>
			 								<div style="float:right">
					 							<input class='addbutton ui-button ui-widget ui-state-default ui-corner-all' type="submit" id="viewButton" name="viewButton" value="View" 
												 onclick="__action.value='viewButton';"/>
			 								</div>
										</div>
									</td>
								</tr>
							</table>
					   </td>
					</tr>
					<tr>
						<td class="box-body">
						
						</td>
						<td class="box-body">
						<!--<label>Fill in selection name</label>
						<input title="fill in selection name" type="text" name="SelectionName" >
						<input class="saveSubmit" type="submit" id="SaveSelectionSubmit" name="SaveSelectionSubmit" value="Save selection" 
								onclick="__action.value='SaveSelectionSubmit';"/>-->
						</td>
					</tr>
				</table>
 				<!-- The detailed table bound to the branch is store in a click event. Therefore this table is not available
 							until the branch has been clicked. As checkbox is part of this branch therefore when the checkbox is ticked
 							the table shows up on the right as well. Another event is fired when the checkbox is checked which is
 							adding a new variable in the selection table and it happens before the detailed table pops up. But we want to
 							use the information (description) from the datailed table. Therefore we have to trigger the click event on branch
 							here first and create the detailed table!-->
 				<script type="text/javascript">					
					// assign ids to dataset inputs
					var dataSetIds = new Array(<#list model.dataSets as dataset>${dataset.id}<#if (dataset_has_next)>, </#if></#list>);
 					for(i in dataSetIds) {
 						var dataSetId = dataSetIds[i];
 						$('#dataset' + dataSetId).data('id', dataSetId);
	 					$('#dataset' + dataSetId).click(function() {
	 						var dataSetId = $(this).data('id');
							$.getJSON('${url_base}&__action=download_json_getdataset&datasetid=' + dataSetId, function(data) {
								updateDataSetView(data, dataSetId);
								$('#feature-details').empty();
								$('#feature-selection').empty();
							});
							return false;
						});
 					}
 					
 					// create event handlers
 					$('#downloadButton').click(function() {
 						window.location = getSelectedFeaturesURL('xls');
 						return false;
 					});
 					
 					$('#downloadButtonEMeasure').click(function() {
 						window.location = getSelectedFeaturesURL('emeasure');
 						return false;
 					});
 					
 					$('#viewButton').click(function() {
 						window.location = getSelectedFeaturesURL('viewer');
 						return false;
 					});
 					
					$(function() {
						$('#datasets').find('input:first-child').click();
					});
 				</script>
			</div>
		</div>
	</div>
</form>
</#macro>

<#macro plugins_protocolViewer_ProtocolViewerPlugin screen>

<!-- normally you make one big form for the whole plugin-->
<form method="post" enctype="multipart/form-data" id="plugins_catalogueTree_CatalogueTreePlugin" name="${screen.name}" action="">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}">
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action" id="test" value="">
	<!-- hidden input for measurementId -->
	<input type="hidden" name="clickedVariable" id="clickedVariable">
	
	<link rel="stylesheet" href="res/jquery-plugins/Treeview/jquery.treeview.css" type="text/css" media="screen" />
	<link rel="stylesheet" href="res/css/catalogue.css" type="text/css" media="screen" />
	<link rel="stylesheet" href="jquery/css/smoothness/jquery-ui-1.8.7.custom.css" type="text/css"/>
	<script type="text/javascript" src="jquery/development-bundle/ui/jquery-ui-1.8.7.custom.js"></script>
	<script type="text/javascript" src="res/jquery-plugins/Treeview/jquery.treeview.js"></script>
	<script type="text/javascript" src="res/jquery-plugins/splitter/splitter.js"></script>
	<script type="text/javascript" src="res/scripts/catalogue.js"></script>
	
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
				<table class="box" width="100%" cellpadding="0" cellspacing="0" style="border-right:1px solid lightgray">
					<tr>
						<td class="box-header" colspan="2">
							<div id="datasets">  
						        <label style='font-size:14px'>
						        <#if screen.dataSets??>
							        <#if (screen.dataSets?size > 1)>
										Choose a Catalog:
									<#elseif (screen.dataSets?size == 1)>
										Catalog:
									</#if>
									<#list screen.dataSets as dataset>
										<input type="button" value="${dataset.name}" id="dataset${dataset.id}">
									</#list>									
								</#if>
								</label>
							</div>
 						</td>
		    		</tr>
		    		<tr>
		    			<td class="box-body" style="width:50%;">
							<select id="selectedField" name="selectedField" title="choose field" name="chooseField" style="display:none"> 
								<#list screen.searchFields as field>
											<!--#assign FieldName = field.name-->
									<option value="${field}" <#if screen.selectedField??>
										<#if screen.selectedField == field>selected="selected"</#if></#if> >Search ${field}</option>			
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
							<div id="protocolTree">
							</div>
						</td>
					    <td class="box-body" id="showInformation"> 
					    	<table  style="height:500px;width:100% ">
						    	<tr>
							    	<td style="height:250px; padding:0px" >
								    	<div id="showDetailsHeader" style="display:none;background:#DDDDDD;height:30px;font-size:20px;text-align:center">View the variable details here</div>
								    	<div id="featureDetails" style="height:250px;overflow:auto">
								    		
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
		  								<div id="featureSelection" style="height:185px; overflow:auto; width:100%"></div>
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
					function updateProtocolTree(data, dataSetId) {
						$('#protocolTree').empty();
						$('#protocolTree').data('id', dataSetId);
						
						// recursively build tree for protocol (+ store data with elements)	
						function buildTree(protocol) {
							// add protocol
							var item = $('<li />');
							var input = $('<input type="checkbox" name="protocol" class="folder">').attr('id', 'protocol' + protocol.id);
							input.data({'id': protocol.id, 'name': protocol.name, 'description': protocol.description});
							var label = $('<label />').attr('for', 'protocol' + protocol.id).text(protocol.name);
							item.append(input).append(label);
							
							var list = $('<ul />');
							
							// add protocol: features
							if(protocol.features) {
								$.each(protocol.features, function(i, feature){
									var item = $('<li />');
									var input = $('<input type="checkbox" name="feature" class="point">').attr('id', 'feature' + feature.id);
									input.data({'id': feature.id, 'name': feature.name, 'description': feature.description, 'protocol_name': protocol.name});
									var label = $('<label />').attr('for', 'feature' + feature.id).text(feature.name);
									item.append(input).append(label).appendTo(list);
								});
							}
							
							// add protocol: subprotocols
							if(protocol.subProtocols) {
								$.each(protocol.subProtocols, function(i, subProtocol){
									list.append(buildTree(subProtocol));
								});
							}
							
							item.append(list);
							return item;
						};
						
						// append tree to DOM
						var tree =  $('<ul id="browser"/>').append(buildTree(data));
						$('#protocolTree').append(tree);
						
						// add protocol click handlers
						$('#browser .folder').change(function(){
							if($(this).is(":checked")) {
								var list = $(this).nextAll('ul');
								list.find('input:checkbox').attr('checked', true);
							} else {
								var list = $(this).nextAll('ul');
								list.find('input:checkbox').attr('checked', false);
							}
							updateFeatureSelection();
						});
						
						// add feature click handlers
						$('#browser .point').change(function(){
							if($(this).is(":checked")) {
								var featureId = $(this).data('id');
								$.getJSON('${screen.url}&__action=download_json_getfeature&featureid=' + featureId, function(data) {
									if(data != null)
										updateFeatureDetails(data);
								});
							}
							updateFeatureSelection();
						});
						
						// render tree
						$('#browser').treeview();
					}
					
					function updateFeatureDetails(data) {
						$('#featureDetails').empty();
						var table = $('<table />');
						table.append('<tr><td>' + "Current selection:" + '<tr><td>' + data.name + '<td></tr>');
						table.append('<tr><td>' + "Description:" + '<tr><td>' + data.description + '<td></tr>');
						table.append('<tr><td>' + "Data type:" + '<tr><td>' + data.dataType + '<td></tr>');
						$('#featureDetails').append(table);
						
						if(data.categories) {
							$('#details').append('<h3>Categories</h3>');
							var table = $('<table />');
							$.each(data.categories, function(i, category){
								var row = $('<tr />');
								$('<td />').text(category.code).appendTo(row);
								$('<td />').text(category.label).appendTo(row);
								$('<td />').text(category.description).appendTo(row);
								row.appendTo(table);		
							});
							$('#featureDetails').append(table);
						}
					}
					
					function updateFeatureSelection() {
						$('#featureSelection').empty();
						var table = $('<table />');
						$('<thead />').append('<tr><td>Variables</td><td>Description</td><td>Protocol</td><td></td></tr>').appendTo(table);
						$('#protocolTree input:checkbox[name=feature]:checked').each(function() {
							var name = $(this).data('name');
							var description = $(this).data('description');
							var protocol_name = $(this).data('protocol_name');
							
							var row = $('<tr />');
							$('<td />').text(name !== undefined ? name : "").appendTo(row);
							$('<td />').text(description !== undefined ? description : "").appendTo(row);
							$('<td />').text(protocol_name != undefined ? protocol_name : "").appendTo(row);
							
							var deleteButton = $('<input type="image" src="generated-res/img/cancel.png" alt="delete">');
							deleteButton.click($.proxy(function() {
								$('#feature' + $(this).data('id')).attr('checked', false);
								updateFeatureSelection();
								return false;
							}, this));
							$('<td />').append(deleteButton).appendTo(row);
							
							row.appendTo(table);
						});
						$('#featureSelection').append(table);
					}
					
					function getSelectedFeaturesURL(format) {
					 	var features = [];
 						$('#protocolTree input:checkbox[name=feature]:checked').each(function() {
 							features.push($(this).data('id'));	
 						});
 						var dataSetId = $('#protocolTree').data('id');
 						return '${screen.url}&__action=download_' + format + '&datasetid=' + dataSetId + '&features=' + features.join();
					}
					
					// assign ids to dataset inputs
					var dataSetIds = new Array(<#list screen.dataSets as dataset>${dataset.id}<#if (dataset_has_next)>, </#if></#list>);
 					for(i in dataSetIds) {
 						var dataSetId = dataSetIds[i];
 						$('#dataset' + dataSetId).data('id', dataSetId);
	 					$('#dataset' + dataSetId).click(function() {
	 						var dataSetId = $(this).data('id');
							$.getJSON('${screen.url}&__action=download_json_getdataset&datasetid=' + dataSetId, function(data) {
								updateProtocolTree(data, dataSetId);	
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

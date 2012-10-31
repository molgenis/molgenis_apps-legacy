<#macro plugins_catalogueTree_ProtocolViewerPlugin screen>

<!-- normally you make one big form for the whole plugin-->
<form method="post" enctype="multipart/form-data" id="plugins_catalogueTree_CatalogueTreePlugin" name="${screen.name}" action="">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}">
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action" id="test" value="">
	<!-- hidden input for measurementId -->
	<input type="hidden" name="clickedVariable" id="clickedVariable">
	
	<script type="text/javascript">
		
		function searchInTree(){
			
			var json = "";
			
			if($('#selectedField').val() == "All"){

				$.ajax({
					url:"${screen.getUrl()}&__action=download_json_searchAll",
      				async: false
      			}).done(function(data) {
					json = data;
				});
			
			}
			
			var inputToken = $('#InputToken').val();
			
			$('#leftSideTree li').hide();
			
			if($('#browser ul.visibleBeforeSearch').length == 0){
				
				$('#browser ul:visible').addClass('visibleBeforeSearch');
				
				$('#leftSideTree ul').each(function(){
					if($(this).css('display') != "none"){
						$(this).addClass('visibleBeforeSearch');
					}
				});		
			}
			if($('#selectedField').val() == "Measurements" || $('#selectedField').val() == "All"){
				
				$('#leftSideTree li').each(function(){
					
					if($(this).find('li').length == 0){
						
						var name = $(this).text().replace(/_/g,' ');
						
						var id = $(this).attr('id');
						
						inputToken = inputToken.replace(/_/g,' ');
						
						if(name.search(new RegExp(inputToken, "gi")) != -1){
							$(this).show();
							$('#leftSideTree li#' + id).parents().show();
							$('#leftSideTree li#' + id).parents('li').children('div').
								removeClass('lastExpandable-hitarea expandable-hitarea').
									addClass('collapsable-hitarea');
							$('#leftSideTree li#' + id).parents('li').removeClass('lastExpandable');
							$(this).addClass('matchedDisplayNode');
							$('#leftSideTree li#' + id).parents('li').addClass('matchedDisplayNode');
						}
					}
				});
			}
			
			if($('#selectedField').val() == "Protocols" || $('#selectedField').val() == "All"){
				
				$('#leftSideTree li').each(function(){
					
					if($(this).find('li').length > 0){
						
						var name = $(this).children('span').text().replace(/_/g,' ');
						
						var id = $(this).attr('id');
						
						inputToken = inputToken.replace(/_/g,' ');
						
						if(name.search(new RegExp(inputToken, "gi")) != -1){
						
							//remove the expandable Class for the element which was found by searching.
							$(this).show();
							$(this).removeClass('lastExpandable');
							$(this).children('div').removeClass('expandable-hitarea lastExpandable-hitarea').addClass('collapsable-hitarea');
							
							//Remove the last expanedable class from all its parents
							$('#leftSideTree li#' + id).parents().show();
							$('#leftSideTree li#' + id).parents('li').removeClass('lastExpandable');
							$('#leftSideTree li#' + id).parents('li').children('div').
								removeClass('expandable-hitarea lastExpandable-hitarea').
									addClass('collapsable-hitarea');
							
							
							//Remove the last expanedable class from all its children
							$('#leftSideTree li#' + id).find('ul').show();
							$('#leftSideTree li#' + id).find('li').show();
							$('#leftSideTree li#' + id).find('li').removeClass('lastExpandable');
							$('#leftSideTree li#' + id).find('div').removeClass('expandable-hitarea lastExpandable-hitarea').addClass('collapsable-hitarea');
							
							$(this).addClass('matchedDisplayNode');
							$('#leftSideTree li#' + id).parents('li').addClass('matchedDisplayNode');
							$('#leftSideTree li#' + id).find('li').addClass('matchedDisplayNode');
						}
					}
				});
				
			}
			if($('#selectedField').val() == "All"){
				
				//var json = eval(jsonString);
				
				$('#leftSideTree li').each(function(){
					
					if($(this).find('li').length == 0){
						
						var id = $(this).attr('id');
						
						var table = json[id];
						
						table = table.replace(/_/g,' ');
						
						inputToken = inputToken.replace(/_/g,' ');
						
						if(table.search(new RegExp(inputToken, "gi")) != -1){
							$(this).show();
							$('#leftSideTree li#' + id).parents().show();
							$('#leftSideTree li#' + id).parents('li').children('div').
								removeClass('expandable-hitarea lastExpandable-hitarea').
									addClass('collapsable-hitarea');
							$('#leftSideTree li#' + id).parents('li').removeClass('lastExpandable');
							$(this).addClass('matchedDisplayNode');
							$('#leftSideTree li#' + id).parents('li').addClass('matchedDisplayNode');
						}
					}
				});
			}			
			
			addVeritcalLine($('#browser >li').attr('id'));
			removeVerticalLine($('#browser >li').attr('id'));
		}		
		
		function removeVerticalLine(id){
			
			if($('#' + id).css('display') != 'none'){						
				
				//Nodes in the middle
				if($('#' + id).children('ul').children('li').length > 0){
					if($('#' + id).nextAll().length == 0 || $('#' + id).nextAll().length > 0 && 
						!$('#' + id).nextAll().is(':visible'))
					{
						$('#' + id).addClass('lastCollapsable');
						$('#' + id).children('div').addClass('lastCollapsable-hitarea');
					}		
					$('#' + id).children('ul').children('li').each(function(){					
						removeVerticalLine($(this).attr('id'));					
					});
				}else{//Last node
					if($('#' + id).nextAll().length > 0 && !$('#' + id).nextAll().is(':visible')){
						$('#' + id).addClass('last');
					}
				}
			}
		}
		
		function addVeritcalLine(id){
			if($('#' + id).children('ul').children('li').length > 0){
					if($('#' + id).nextAll().length > 0){
						$('#' + id).removeClass('lastCollapsable');
						$('#' + id).children('div').removeClass('lastCollapsable-hitarea');
					}
					$('#' + id).children('ul').children('li').each(function(){					
						addVeritcalLine($(this).attr('id'));	
					});					
			}else{
				if($('#' + id).nextAll().length > 0){
					$('#' + id).removeClass('last');
				}
			}
		}

		function checkSearchingStatus(){
			
			if($('#InputToken').val() === ""){
				addVeritcalLine($('#browser >li').attr('id'));
				$('#leftSideTree li').show();	
				revertBackToLastState("matchedDisplayNode");			
			}
		}
		
		function revertBackToLastState(selector){
			
			$('#browser li.' + selector).each(function(){
					
				if($(this).parent('ul').hasClass('visibleBeforeSearch')){
					//That means it was visible before searching, do nothing just show the element
					$(this).parent('ul').show();
				}else{
					//That means it was hidden before searching, hide the element and revert the hitarea
					$(this).parent('ul').hide();
					$(this).parent('ul').siblings('div').removeClass('collapsable-hitarea').addClass('expandable-hitarea');
					if($(this).parents('li:first').nextAll().length == 0){
						$(this).parents('li:first').removeClass('lastCollapsable').addClass('lastExpandable');
						$(this).parent('ul').siblings('div').removeClass('lastCollapsable-hitarea').addClass('lastExpandable-hitarea');
					}
				}
			});
			
			$('#browser li.' + selector).removeClass(selector);
			$('#browser ul.visibleBeforeSearch').removeClass('visibleBeforeSearch');
		}
		
		function whetherReload(){
			var value = $('#InputToken').val();
			if(value.search(new RegExp("\\w", "gi")) != -1){
				searchInTree();
			}
			return false;
		}
		
		function onClickRemoveTableRow() {

			if($('#selectedVariableTable').find('tr').length > 1){
				$(this).parent().parent().remove();
			}else{
				$('#selectedVariableHeader').remove();
				$('#selectedVariableTable').remove();
				$('#selectNotification').show();
			}

			var myCheckBoxId = $(this).attr('id').replace("_delete", "");
			$('#' + myCheckBoxId).find('input:checkbox').attr('checked',false);
			var currentNode = $('#' + myCheckBoxId);
			var uncheckProtocol = true;
			$(currentNode).siblings().each(function(){
				if($(this).find('input:checkbox').attr('checked') == "checked"){
					uncheckProtocol = false;
				}
			});
			if(uncheckProtocol == true){
				$(currentNode).parents('li:first').find('input:checkbox').attr('checked', false);
			}
			
			if($('#selectedVariableTable').find('tr').length == 0) {
				$('#selectedVariableHeader').remove();
				$('#selectedVariableTable');
			}
		}
		
		function traceBackSelection(tracedElementID){
			
			$('#' + tracedElementID + '>span').trigger('click');
			$('#' + tracedElementID).show();
			var id = $('#' + tracedElementID).attr('id');
			$('#leftSideTree li#' + id).parents().show();
			$('#leftSideTree li#' + id).parents('li').children('div').
				removeClass('lastExpandable-hitarea expandable-hitarea').
				addClass('collapsable-hitarea');
			$('#leftSideTree li#' + id).parents('li').removeClass('lastExpandable');
			
			var elementTop = $('#' + tracedElementID).position().top;
			var treeDivTop = $('#leftSideTree').position().top;
			var divHeight = $('#leftSideTree').height();
			var lastTop = $('#leftSideTree').scrollTop();
			$('#leftSideTree').scrollTop(lastTop + elementTop - divHeight/3 - treeDivTop);
			addVeritcalLine($('#' + tracedElementID).parents('li').eq(0).attr('id'));
			removeVerticalLine($('#' + tracedElementID).parents('li').eq(0).attr('id'));
		}
		function addSelection(listOfVariables){
			
			for(var i = 0; i < listOfVariables.length; i++){
				
				var variableID = listOfVariables[i];

				var uniqueID = $('#' + variableID).parents('li').eq(0).attr('id');
			
				$('#' + uniqueID + '>span').trigger('click');
				
				if($('#' + uniqueID + '_row').length == 0){
					
					var label = $('#' + variableID).parent().text();
					var checkBoxID = $('#' + variableID).attr('id');
					var protocolName = $('#' + variableID).parents('li').eq(1).children('span').text();
					var variableDescription = $('#' + uniqueID + '_description').find('td').eq(1).text();
					var descriptionShows = variableDescription.substr(0, 10);
					var deleteButton = '<img src=\"generated-res/img/cancel.png\" id=\"'+uniqueID+'_delete\" style=\"cursor:pointer;length:16px;width:16px\">';
					var content = '<tr id=\"'+uniqueID +'_row\" ><td style=\"width:30%; text-align:left; cursor:pointer\">' + label + '</td><td id=\"'+uniqueID +'_hover\" style=\"cursor:pointer;width:30%; text-align:left\">' + 
								descriptionShows + '...</td><td style=\"width:30%; text-align:left\">' + 
								protocolName + '</td><td style=\"text-align:center; width:10%; text-align:left\">' + 
								deleteButton + '</td></tr>';
					
					<!--We are going to check whether this  already existed-->
					if($('#selectedVariableTable').length == 0){
						
						$('#selectNotification').hide();
						var newTableHeader = '<table id=\"selectedVariableHeader\" style=\"width:100%\" class=\"listtable\">'+
						'<th style=\"width:30%; text-align:left\">Variables</th><th style=\"width:30%; text-align:left\">Description</th>'+
						'<th style=\"width:30%; text-align:left\">Sector/Protocol</th><th style=\"width:10%;text-align:center\">Delete</th></table>';
						var newTable = '<table id=\"selectedVariableTable\"  class=\"listtable\" style=\"width:100%; overflow:auto\">';
						newTable += content;
						newTable += "</table>";
						$('#selection').append(newTable);
						$('#selectionHeader').append(newTableHeader);
						$('#'+uniqueID+'_delete').click(onClickRemoveTableRow);
						
					}else{
						
						$('#selectedVariableTable').find('tr:last-child').after(content);
						
						if($('#selectedVariableTable tr').length%2 == 1){
							$('#'+uniqueID +'_row').addClass('form_listrow0');
						}else{
							$('#'+uniqueID +'_row').addClass('form_listrow1');
						}
						$('#'+uniqueID+'_delete').click(onClickRemoveTableRow);
					}
					
					$('#' + uniqueID +'_hover').click(function(){
						var clickedVariable = $(this).attr('id').replace("_hover", "");
						$('#' + clickedVariable + ' span').trigger('click');
					});
					$('#' + uniqueID +'_hover').mouseenter(function(){
						$('#popUpDialogue').show();
					});
					$('#' + uniqueID +'_hover').mouseout(function(){
						$('#popUpDialogue').hide();
					});
					$('#' + uniqueID +'_row >td').eq(0).mouseenter(function(){
						$('#traceBack').show();
					});
					$('#' + uniqueID +'_row >td').eq(0).mouseout(function(){
						$('#traceBack').hide();
					});
					
					$('#' + uniqueID + '_row >td').eq(0).click(function(){
						var tracedElementID = $(this).parent().attr('id').replace("_row", "");
						traceBackSelection(tracedElementID);
					});
				}
			}
		}
		
		$(document).ready(function(){	
			
			$("#browser").treeview({
				control:"#masstoggler",
			});	
			
			$('ul#browser li').show();
			
			var startingElement = $('#browser');
			
			while(true){
			
				if(startingElement.find('li ul:first').length > 0){
					startingElement.find('li span:first').trigger('click');
					startingElement = startingElement.find('li ul:first');
				}else{
					break;
				}
			}
			
      		$('#browser').click(function(){
	      		$('#showDetailsHeader').fadeOut();
	      		$('#selectionInformationHeader').fadeOut();
	      		$('#treeHeader').fadeOut();
	      		$('#browser').unbind('click');
	      	});
			
			$('input[name="cohortSelectSubmit"]').button();
			$('input[name="cohortSelectSubmit"]').css({
				'font-size':'1.2em',
				'color':'#123481'
			});
			$('input[name="cohortSelectSubmit"]').show();
			
			$('#viewButton').button();
			$('#viewButton').css({
				'font-size':'0.8em'
			});
			$('#viewButton').show();
			
			$('#downloadButton').button();
			$('#downloadButton').css({
				'font-size':'0.8em'
			});
			$('#downloadButton').show(); 
			
			$('#downloadButtonEMeasure').button();
			$('#downloadButtonEMeasure').css({
				'font-size':'0.8em'
			});
			$('#downloadButtonEMeasure').show();
		
			
			$('#clearSearchingResult').click(function(){
				$('#InputToken').val('');
				checkSearchingStatus();
			});
			$('#showDetailsHeader').show();
      		$('#selectionInformationHeader').show();
      		$('#treeHeader').show();
		});
	</script>

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
				<#if screen.isSelectedDataSet() == true>
					<table class="box" width="100%" cellpadding="0" cellspacing="0" style="border-right:1px solid lightgray">
						<tr>
							<td class="box-header" colspan="2">  
						        <label style='font-size:14px'>
						        <#if screen.getArrayDataSets()??>
							        <#if (screen.getArrayDataSets()?size > 1)>
										Choose a cohort:
										<#list screen.getArrayDataSets() as datasetsName>
												<input class="cohortSelect" type="submit" name="cohortSelectSubmit" value = "${datasetsName}"
													 style="display:none" onclick="__action.value='cohortSelect';"/>
										</#list>
									<#elseif (screen.getArrayDataSets()?size > 0)>
										Catalog: 
										<#assign invName = screen.getArrayDataSets()[0]> ${datasetsName}
									</#if>
								</#if>
								</label>

	 						</td>
			    		</tr>
			    		<tr>
			    			<td class="box-body" style="width:50%;">
								<select id="selectedField" name="selectedField" title="choose field" name="chooseField" style="display:none"> 
									<#list screen.arraySearchFields as field>
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
								<#list screen.getFilters() as filter>			
									<b>${filter}</b>
									<input type="image" src="generated-res/img/cancel.png" alt="Remove filter" 
													name="chooseInvestigation" style="vertical-align: middle;" 
													value="refresh tree" onclick="__action.value='chooseInvestigation';DownloadMeasurementsSubmit.style.display='inline'; 
													DownloadMeasurementsSubmit.style.display='inline';" title="load another study"	/>	
								<#if filter_has_next> and </#if>
								</#list>				    
					    	</td>
					    <td class="box-body" style="width: 50%"><div >Details:</div></td></tr>
					    <tr>
					    	<td class="box-body">
					    		<div id="treeHeader" style="display:none;background:#DDDDDD;height:30px;font-size:20px;text-align:center">View all the data items in the study</div>
								<div id="leftSideTree">
									${screen.getTreeView()}<br/>
									
									<script>
										
										$('#browser').find('li:first').find('span:first').trigger('click');
									</script>
								</div>
							</td>
						    <td class="box-body" id="showInformation"> 
						    	<table  style="height:500px;width:100% ">
							    	<tr>
								    	<td style="height:250px; padding:0px" >
									    	<div id="showDetailsHeader" style="display:none;background:#DDDDDD;height:30px;font-size:20px;text-align:center">View the variable details here</div>
									    	<div id="details" style="height:250px;overflow:auto">
									    		
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
			  								<div id="selection" style="height:185px; overflow:auto; width:100%"></div>
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
			   	</#if>	
 				<!-- The detailed table bound to the branch is store in a click event. Therefore this table is not available
 							until the branch has been clicked. As checkbox is part of this branch therefore when the checkbox is ticked
 							the table shows up on the right as well. Another event is fired when the checkbox is checked which is
 							adding a new variable in the selection table and it happens before the detailed table pops up. But we want to
 							use the information (description) from the datailed table. Therefore we have to trigger the click event on branch
 							here first and create the detailed table!-->
 				<script>
 				
					$('#browser').find('li').each(function(){
			      		
			      		if($(this).find('li').length == 0){
			      			
			      			var measurementID = $(this).attr('id');
	      					var clicked = $('#clickedVariable').val();
						
	      					$(this).children('span').click(function(){
			      				//Itself
			      				$(this).css({
			      					'color':'#778899',
			      					'font-size':15,
			      					'font-style':'italic',
			      					'font-weight':'bold'
			      				});
			      			
		      					var parent = $(this).parent().parent().siblings('span');
			      				//Parent
			      				$(parent).css({
									'color':'#778899',
			      					'font-size':15,
			      					'font-style':'italic',
			      					'font-weight':'bold'
									
								});
			      				
								var clickedVar = $('#clickedVariable').val();
								
								if(clickedVar != "" && clickedVar != measurementID){
								//Old variable
									$('#' + clickedVar + '>span').css({
										'color':'black',
										'font-size':13,
										'font-style':'normal',
										'font-weight':400
									});
								var parentOld = $('#' + clickedVar).parent().siblings('span');
							
			      					$(parentOld).css({
									'color':'black',
										'font-size':13,
										'font-style':'normal',
										'font-weight':400									
									});
		      						
								}   				
								$('#clickedVariable').val(measurementID);
								$('#details').empty();
								$.ajax({
									url:"${screen.getUrl()}&__action=download_json_showInformation&variableName=" + measurementID,
				      				async: false
				      			}).done(function(data) {
									$('#details').append(data["result"]);
								});
								$('#' + measurementID + '_itemName').click(function(){
									var uniqueID = $(this).attr('id').replace("_itemName","");
									traceBackSelection(uniqueID);
								});
							});
							$('#' + measurementID + '>span').mouseenter(function(){
								$('#popUpDialogue').show();
							});
							$('#' + measurementID + '>span').mouseout(function(){
								$('#popUpDialogue').hide();
							});												
						}
					});	
 					
 					$('div#leftSideTree input:checkbox').each(function(index){
 						$(this).click(function() {
							 if($(this).attr('checked')){
								 $(this).parent()
								.siblings('ul')
								.find('input:checkbox')
						     	.attr('checked',true);
							 }else{
							 	 $(this).parent()
								.siblings('ul')
								.find('input:checkbox')
						     	.attr('checked',false);
							 }
						});
 					});
 					$('#GenericDCM').find('li').each(function(){
 						if($(this).find('li').length == 0){
 							$(this).find(':checkbox').attr('disabled','true');
 						}
 					});
 					
 					$('#browser').find('input:checkbox').each(function(){
 						
 						$(this).attr('checked', false);
 						
 						$(this).click(function(){
 							
 							//The protocol is clicked
 							if($(this).parent().parent().find('input:checkbox').length > 1){
 								
 								if($(this).attr('checked') != 'checked'){
 									
 									var variableID = $(this).attr('id');
 										
 									$(this).parents('li:first').find('input:checkbox').each(function(){
 										if(variableID != $(this).attr('id')){
 											$('#' + $(this).parents('li:first').attr('id') + '_row').remove();
 										}
 									});
	 								if($('#selectedVariableTable').find('tr').length == 0){
	 									$('#selectedVariableTable').remove();
	 									$('#selectedVariableHeader').remove();
	 									$('#selectNotification').show();
	 								}

 									
 								}else{
 								
	 								//Get all the checkbox id's of the children
	 								var index = 0;
	 								var array = new Array();
	 								var listOfVariable = new Array();
	 								var variableCheckBoxID = $(this).attr('id');
	 								$(this).parent().parent().find('input:checkbox').each(function(){
	 									if($(this).attr('id') != variableCheckBoxID){
		 									array[index] = $(this).attr('id');
		 									listOfVariable[index] = $(this).parents('li:first').attr('id');
		 									index++;
	 									}
	 								});
	 								$.ajax({
										url:"${screen.getUrl()}&__action=download_json_showInformation&variableName=" + listOfVariable,
					      				async: false
					      			}).done(function(data) {
										
										$('#details').append("<div id=\"hiddenDetails\" style=\"display:none\">" + data["result"] + "</div>");
									});
	 								addSelection(array);
	 								
	 								$('#' + listOfVariable[listOfVariable.length - 1]).children('span').trigger('click');
	 								
	 								$('#hiddenDetails').remove();
	 							}
	 							
 							}else{
 							
 								//Add only one measurement to the selection 
 								$(this).parents('span:first').trigger('click');
 							
	 							if($(this).attr('checked') != 'checked'){
	 								
	 								if($('#selectedVariableTable').find('tr').length > 1){
	 									$('#' + $(this).parent().parent().attr('id') + '_row').remove();
	 								}else{
	 									$('#selectedVariableTable').remove();
	 									$('#selectedVariableHeader').remove()
	 									$('#selectNotification').show();
	 								}
	 								var uncheckProtocol = true;
	 								var currentNode = $(this).parents('li:first');
									$(currentNode).siblings().each(function(){
										if($(this).find('input:checkbox').attr('checked') == "checked"){
											uncheckProtocol = false;
										}
									});
									if(uncheckProtocol == true){
										$(currentNode).parents('li:first').find('input:checkbox').attr('checked', false);
									}
	 							}else{
	 								var array = new Array();
	 								array[0] = $(this).attr('id');
	 								addSelection(array);
	 							}
 							}
 						});	
 					});
 				</script>
			</div>
		</div>
	</div>
</form>
</#macro>

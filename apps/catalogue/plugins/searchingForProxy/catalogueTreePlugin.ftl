<#macro plugins_searchingForProxy_catalogueTreePlugin screen>

<!-- normally you make one big form for the whole plugin-->
<form method="post" enctype="multipart/form-data" id="plugins_catalogueTree_catalogueTreePlugin" name="${screen.name}" action="">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}">
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action" id="test" value="">
	<!-- hidden input for measurementId -->
	<input type="hidden" name="clickedVariable" id="clickedVariable">
	
	<script type="text/javascript">
		function searchInTree(){
			
			var inputToken = $('#InputToken').val();
			
			$('#leftSideTree li').hide();
					
			if($('#selectedField').val() == "Measurements" || $('#selectedField').val() == "All fields"){
				
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
						}
					}
				});
			}
			
			if($('#selectedField').val() == "Protocols" || $('#selectedField').val() == "All fields"){
				
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
						}
					}
				});
				
			}
			if($('#selectedField').val() == "Details" || $('#selectedField').val() == "All fields"){
				
				var json = eval(${screen.getInheritance()});
				
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
			}
		}
		
		function whetherReload(){
			var value = $('#InputToken').val();
			if(value.search(new RegExp("\\w", "gi")) != -1){
				searchInTree();
			}
			return false;
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
		
		
		$(document).ready(function(){	
			
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
				<#if screen.isSelectedInv() == true>
					<table id="layoutTable" class="box" width="100%" cellpadding="0" cellspacing="0" style="border-right:1px solid lightgray">
						<tr>
							<td class="box-header" colspan="2">  
						        <label style='font-size:14px'>Choose a cohort:
									
								<select name="cohortSelectSubmit" id="cohortSelectSubmit"> 
									<#list screen.arrayInvestigations as inv>
										<#assign invName = inv.name>
										<option value="${invName}" <#if screen.selectedInvestigation??><#if screen.selectedInvestigation == invName>selected="selected"</#if></#if> >${invName}</option>			
									</#list>
								</select>
							
								<script>$('#cohortSelectSubmit').chosen();</script>
								<input type="image" src="res/img/refresh.png" alt="Submit" 
										name="chooseInvestigation" style="vertical-align: middle;" 
										value="refresh tree" onclick="__action.value='chooseInvestigation';DownloadMeasurementsSubmit.style.display='inline'; 
										DownloadMeasurementsSubmit.style.display='inline';" title="load another study"	/>
							<!--	<div id="masstoggler"> 		
				 					<label style='font-size:14px'>Browse protocols and their variables '${screen.selectedInvestigation}':click to expand, collapse or show details</label>
				 					<a id="collapse" title="Collapse entire tree" href="#"><img src="res/img/toggle_collapse_tiny.png"  style="vertical-align: bottom;"></a> 
				 					<a id="expand" title="Expand entire tree" href="#"><img src="res/img/toggle_expand_tiny.png"  style="vertical-align: bottom;"></a>
	 							</div>-->
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
									onkeyup="checkSearchingStatus();" onkeypress="if(event.keyCode === 13){return whetherReload();}">
									
								
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
								<div id="leftSideTree">
									${screen.getTreeView()}<br/>
								</div>
							</td>
						    <td class="box-body" id="showInformation"> 
						    	
						    	<div id="tabs" style="display:none;width:100%">
									<ul>
										<li><a href="#viewVariable">Variable information</a></li>
										<li><a href="#advancedSearch">Advanced searching</a></li>
									</ul>
									<div id="advancedSearch" style="height:500px;width:100%">
										<p>test</p>
									</div>
									<div id="viewVariable" style="height:500px;width:100%">
										<div id="details" style="height:250px;overflow:auto"></div>
									</div>
								</div>
						    	<script>
						    		$( "#tabs" ).tabs();
						    		$( "#tabs" ).show();
						    	</script>
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
			    <label><#if screen.getStatus()?exists>${screen.getStatus()} </#if>  </label>
 				<!-- The detailed table bound to the branch is store in a click event. Therefore this table is not available
 							until the branch has been clicked. As checkbox is part of this branch therefore when the checkbox is ticked
 							the table shows up on the right as well. Another event is fired when the checkbox is checked which is
 							adding a new variable in the selection table and it happens before the detailed table pops up. But we want to
 							use the information (description) from the datailed table. Therefore we have to trigger the click event on branch
 							here first and create the detailed table!-->
 				<script>
					var json = eval(${screen.getInheritance()});
			      	
			      	$('#browser').find('li').each(function(){
			      		
			      		if($(this).find('li').length == 0){
			      			
			      			var measurementID = $(this).attr('id');
	      					
	      					$(this).children('span').click(function(){
			      				$(this).css({
			      					'color':'#778899',
			      					'font-size':15,
			      					'font-style':'italic',
			      					'font-weight':'bold'
			      				});
								
								if($('#clickedVariable').val() != "" && $('#clickedVariable').val() != measurementID){
									var clickedVariableID = $('#clickedVariable').val();
									$('#' + clickedVariableID + '>span').css({
										'color':'black',
										'font-size':13,
										'font-style':'normal',
										'font-weight':400
									});
								}			   				
								$('#clickedVariable').val(measurementID);
								$('#details').empty();
								$('#details').append(json[measurementID]);
								$('#' + measurementID + '_itemName').click(function(){
									var uniqueID = $(this).attr('id').replace("_itemName","");
									traceBackSelection(uniqueID);
								});
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
 				</script>
			</div>
		</div>
	</div>
</form>

</#macro>

<link type="text/css" href="jquery/css/smoothness/jquery-ui-1.8.7.custom.css" rel="Stylesheet"/>
<script src="jquery/development-bundle/ui/jquery-ui-1.8.7.custom.js" language="javascript"></script>
<script src="res/jquery-plugins/Treeview/jquery.treeview.js" language="javascript"></script>
<script src="res/scripts/bioshare_search.js" language="javascript"></script>
<script src="res/scripts/bioshare_harmonization.js" language="javascript"></script>
<script src="res/scripts/catalogue.js" language="javascript"></script>
<link rel="stylesheet" href="res/jquery-plugins/Treeview/jquery.treeview.css" type="text/css" media="screen" /> 
<link rel="stylesheet" href="res/css/catalogue.css" type="text/css" media="screen" />
<style>
	.predictorInput{
		display:block;
		float:right;
		width:200px;
		margin-right:15px;
	}
	td{
		vertical-align:middle;
		font-size:16px;
	}
	button >span {
 		font-size:12px;
 	}
</style>
<script>
	
	var URL = "${screen.getUrl()}";
	var NAME = "${screen.getName()}";
	var CLASSES = $.treeview.classes;
	var settings = {};
	var searchNode = new Array();

	$(document).ready(function()
	{	
		if("${screen.isRetrieveResult()}" == "true")
		{
			retrieveResult(URL);
		}
		//Styling for the dropDown box
		$('#selectPredictionModel').chosen().change(function(){
			
			selected = $('#selectPredictionModel').val();
			
			$('#selectedPrediction >span').empty().text(selected);
			
			showPredictors(selected, URL);
			//Fill out summary panel
			$('#summaryPanel').fadeIn().draggable();
		});
		
		if($('#selectPredictionModel option').length == 0){
			message = "There are no prediction models in database, add one first";
			showMessage(message, false);
		}else{
			selected = $('#selectPredictionModel').val();
			showPredictors(selected, URL);
			$('#selectedPrediction span').text(selected);
		}
		
		$('#dataTypeOfPredictor').change(function(){
			
			if($('#dataTypeOfPredictor').val() == "categorical"){
				$('#categoryOfPredictor').attr('disabled', false);
			}else{
				$('#categoryOfPredictor').attr('disabled', true);
			}
		});
		
		$('#defineFormulaPanel').dialog({
			autoOpen : false,
			title : "Formula",
			height: 300,
        	width: 400,
        	modal: true,
        	buttons: {
                Update: function() {
                	defineFormula(URL);
                },
                Cancel: function() {
                    $( this ).dialog( "close" );
                }
        	},
		});
		
		$('#confirmWindow').dialog({
			autoOpen : false,
			title : "Warning",
			height: 300,
        	width: 500,
        	modal: true,
        	buttons: {
                Confirm: function() {
                	removePredictionModel(URL);
                },
                Cancel: function() {
                    $( this ).dialog( "close" );
                }
        	},
		});
		
		$('#defineFormula').button().click(function(){
			$('#defineFormulaPanel').dialog('open');
		});
		
		$('#showCohortStudy').button().click(function(){
			$('#selectCohortStudyPanel').fadeIn();
		});
		
		$('#listOfCohortStudies').chosen();
		
		$('#validatePredictionModel').button().click(function(){
			validateStudy(NAME);
		});
		
		$('#showMatchedMapping').button().click(function(){
			$('#candidateMapping').hide();
			$('#matchedMapping').fadeIn();
		});
		
		$('#showCandidateMapping').button().click(function(){
			$('#matchedMapping').hide();
			$('#candidateMapping').fadeIn();
		});
		
		$('#addMappingFromTree').button().click(function(){
			addMappingFromTree(URL);
		});
		
		$('#startNewValidation').button().click(function(){
			$('#selectCohortStudyPanel').hide();
			$('#beforeMapping').show();
			$('#afterMapping').hide();
		});
		
		$('#saveMapping').button().click(function(){
			collectMappingFromCandidate(URL);
		});
		
		$('#openMapping').button().click(function(){
			
			if($('#mappingResult table').length > 0)
			{	
				$('#mappingResultDialog').append($('#mappingResult table:visible'));
				
				$('#mappingResultDialog').dialog({
				
					title : "Mapping result",
					height: 600,
	            	width: 700,
	            	modal: true,
	            	close: function() {
			        	$('#mappingResult').append($(this).find('table'));
			        },
	            	buttons: {
		                Save : function(){
		                	$( this ).dialog( "close" );
		                	collectMappingFromCandidate(URL);
		                },
		                Cancel : function() {
		                    $( this ).dialog( "close" );
		                },
	            	},
				});
			}
		});
		
		$('#cancelSelectCohortStudy').button().click(function(){
			$('#selectCohortStudyPanel').fadeOut();
		});
		
		$('#addPredictorButton').click(function(){
			$('#defineVariablePanel').fadeIn().draggable();
		});
		
		$('#addPredictor').button().click(function(){
			addPredictor(URL);
		});
		
		$('#cancelPredictor').button().click(function(){
			cancelAddPredictorPanel();
		});
		
		$('#closeSummary').click(function(){
			$('#summaryPanel').fadeOut();
		});
		
		$('#addShowSummary').click(function(){
			$('#summaryPanel').fadeIn().draggable();
		});
		
		//Add a new prediction model in the dropdown menu
		$('#addModelButton').click(function(){
			addNewPredictionModel(URL);
		});
		//Remove a prediction model in the dropdown menu
		$('#removeModelButton').click(function(){
			$('#confirmWindow').dialog('open');
		});
	});
</script>
<form method="post" enctype="multipart/form-data" name="${screen.name}" action="">
<!--needed in every form: to redirect the request to the right screen-->
<input type="hidden" name="__target" value="${screen.name}">
<!--needed in every form: to define the action. This can be set by the submit button-->
<input type="hidden" name="__action">
<!-- remember the clicked variable -->
<input type="hidden" id="clickedVariable"/>

	<#--optional: mechanism to show messages-->
	<div class="formscreen">
		<div class="form_header" id="${screen.getName()}">
			${screen.label}
		</div>
		<div class="screenbody">
			<div class="screenpadding">
				<div id="messagePanel"></div>
				<div id="afterMapping" style="display:none;height:800px;width:100%;">
					<div style="width:100%;height:140px;">
						<div class="ui-tabs-nav ui-corner-all ui-widget-content" style="width:60%;height:130px;margin:2px;float:left">
							<div class="ui-widget-header ui-corner-all" style="height:30px;">
								<div style="margin:3px;float:left;">Matching result</div>
								<input type="button" id="startNewValidation" value="Validate a new model" 
									style="font-size:10px;margin:3px;float:right;" class="ui-button ui-widget ui-state-default ui-corner-all"/>
							</div>
							<div style="margin:5px;">
								Prediction model: <span id="matchingPredictionModel" style="float:right;margin-right:20px;"></span>
							</div>
							<div style="margin:5px;">
								Validation study: <span id="matchingValidationStudy" style="float:right;margin-right:20px;"></span>
							</div>
							<div style="margin:5px;">
								Selected predictor: <span id="matchingSelectedPredictor" style="float:right;margin-right:20px;"></span>
							</div>
						</div>
					</div>
					<div style="height:40%;width:100%;float:left;">
						<div class="ui-corner-all ui-tabs-nav ui-widget-content" style="height:100%;width:30%;float:left;">
							<div class="ui-tabs-nav ui-widget-header ui-corner-all" style="float:left;width:100%;height:16%;">
								<span style="display:block;margin:12px;text-align:center;">Predictors</span>
							</div>
							<div style="float:left;width:100%;height:80%;">
								<select id="validatePredictors" multiple="multiple" style="margin-top:2px;font-size:16px;width:100%;height:95%;">
								</select>
								<div class="ui-tabs-nav ui-widget-header ui-corner-all" style="float:left;width:100%;height:8%;">
								</div>
							</div>
						</div>
						<div id="candidateMapping" class="ui-corner-all ui-tabs-nav ui-widget-content" style="height:100%;width:69%;float:left;">
							<div class="ui-tabs-nav ui-widget-header ui-corner-all" style="float:left;width:100%;height:16%;">
								<span style="display:block;margin:12px;float:left;">Candidate variables</span>
								<input type="button" id="showMatchedMapping" style="font-size:40%;float:right;margin-top:12px;margin-right:4px" 
									value="Matched variables" class="ui-button ui-widget ui-state-default ui-corner-all"/>
								<input type="button" id="saveMapping" value="Save the mappings" style="font-size:40%;margin-top:12px;margin-right:4px;float:right;" 
									class="ui-button ui-widget ui-state-default ui-corner-all"/>
								<input type="button" id="openMapping" value="open mappings" style="font-size:40%;margin-top:12px;margin-right:4px;float:right;" 
									class="ui-button ui-widget ui-state-default ui-corner-all"/>
							</div>
							<div style="float:left;width:100%;height:80%;">
								<div id="mappingResult" style="margin-top:2px;font-size:20px;width:100%;height:95%;overflow:auto;">
								</div>
								<div class="ui-tabs-nav ui-widget-header ui-corner-all" style="float:left;width:100%;height:8%;">
								</div>
								<div id="mappingResultDialog">
								</div>
							</div>
						</div>
						<div id="matchedMapping" class="ui-corner-all ui-tabs-nav ui-widget-content" style="display:none;height:100%;width:69%;float:left;">
							<div class="ui-tabs-nav ui-widget-header ui-corner-all" style="float:left;width:100%;height:16%;">
								<span style="display:block;margin:12px;float:left;">Matched variables</span>
								<input type="button" id="showCandidateMapping" style="font-size:40%;float:right;margin:12px;" 
									value="Candidate variables" class="ui-button ui-widget ui-state-default ui-corner-all"/>
								<input type="button" id="addMappingFromTree" style="font-size:40%;float:right;margin:12px;" 
									value="Add variable from tree" class="ui-button ui-widget ui-state-default ui-corner-all"/>
							</div>
							<div style="float:left;width:100%;height:80%;">
								<div id="existingMappings" style="margin-top:2px;font-size:20px;width:100%;height:95%;overflow:auto;">
								</div>
								<div class="ui-tabs-nav ui-widget-header ui-corner-all" style="float:left;width:100%;height:8%;">
								</div>
							</div>
						</div>
					</div>
					<div id="treePanel" style="float:left;width:99%;margin-top:10px;height:40%;" class="ui-corner-all ui-tabs-nav ui-widget-content">
						<div class="ui-tabs-nav ui-widget-header ui-corner-all" style="float:left;width:100%;height:14%;">
							<span style="display:block;float:left;margin:7px;text-align:center;">Search variables</span>
							<div id="treeSearchPanel" style="float:left;margin:7px;">
								<input type="text" id="searchField" style="font-size:12px;"
									onkeyup="checkSearchingStatus();" onkeypress="if(event.keyCode === 13){;return whetherReload('${screen.getUrl()}');}"/>
								<input type="button" id="search" style="font-size:12px;" value="search"/>
								<input type="button" id="clearButton" style="font-size:12px;" value="clear"/>
							</div>
						</div>
						<table style="width:100%">
							<tr>
								<td style="width:50%">
									<div id="treePanel">
										<div id="treeView" style="height:250px;overflow:auto;">
											<ul id="browser" class="pointtree">  
											</ul>
										</div>
									</div>
								</td>
								<td style="width:50%">
									<div id="details" style="height:250px;overflow:auto;">
									</div>
								</td>
							</tr>
						</table>
					</div>
				</div>
				<div id="beforeMapping" style="height:600px;width:100%;">
					<div style="padding-left:6px;background:#DDDDDD;border:1px solid #BBBBBB;font-size:14px;height:28%;width:60%;float:left;margin-right:30px;" class="ui-corner-all">
						<div style="font-style:italic;">
							<h3>Validate prediction models</h3>
						</div>							
						<hr/>
						<div id="selectedPrediction" style="height:30px;padding-left:10px;">
							Selected prediction model:
							<span style="font-size:25px;font-style:italic;"></span>
							<input type="button" id="defineFormula" value="formula" class="ui-button ui-widget ui-state-default ui-corner-all"
								style="font-size:12px;height:30px;width:70px;float:right;margin-top:4px;margin-right:20px;" />
							<input type="button" id="showCohortStudy" value="cohort study" class="ui-button ui-widget ui-state-default ui-corner-all"
								style="font-size:12px;height:30px;width:100px;float:right;margin-top:4px;" />
							<div id="defineFormulaPanel" style="display:none;">
								<textarea id="showFormula" style="width:90%;height:90%;font-size:12px;">
								</textarea>
							</div>
						</div>
						<hr/>
						<div style="float:left;width:43%;margin-left:10px;">
							<span style="display:block;margin-bottom:5px;">Select a prediction model:</span>
							<div style="float:left;margin-right:10px;">
								<select id="selectPredictionModel" name="selectPredictionModel" style="width:185px;" data-placeholder="Choose a prediction model">
									<#list screen.getPredictionModels() as predictionModel>
										<option>
											${predictionModel}
										</option>	
									</#list>
								</select>
							</div>
							<div id="removeModelButton" style="cursor:pointer;height:18px;width:18px;float:left" class="ui-state-default ui-corner-all" title="remove">
								<span class="ui-icon ui-icon-trash"></span>
							</div>
							<div id="confirmWindow" style="display:none;">
								<p>
									WARN: All data for this prediction model will be deleted! It is not repairable!
								</p>
							</div>
						</div>
						<div style="border-left:1px solid black;float:left;height:41%;top:-8px;position:relative;">
						</div>
						<div style="float:left;width:45%;margin-left:15px;">
							<span style="display:block;margin-bottom:5px;">Add a prediction model:</span>
							<input type="text" id="addPredictionModel" name="addPredictionModel" class="ui-corner-all" style="height:20px;float:left;margin-right:10px;"/>
							<div id="addModelButton" style="cursor:pointer;height:18px;width:18px;float:left" class="ui-state-default ui-corner-all" title="add a model">
								<span class="ui-icon ui-icon-plus"></span>
							</div>
						</div>
					</div>
					<div id="selectCohortStudyPanel" style="display:none;margin-left:-20px;width:300px;height:28%;position:relative;float:left;" class="ui-tabs-panel ui-widget-content ui-corner-bottom">
						<div class="ui-tabs-nav ui-widget-header ui-corner-all" style="height:14%;width:100%;">
							<span style="margin:10px;font-size:20px;font-style:italic;">Select a cohort study</span>
						</div>
						<div style="margin-top:20px;margin-left:10px;height:70%;">
							<select id="listOfCohortStudies" name = "listOfCohortStudies" style="width:185px;">
								<#if screen.getValidationStudies()??>
									<#list screen.getValidationStudies() as study>
										<option>${study}</option>
									</#list>
								</#if>
							</select>
							<input type="button" id="validatePredictionModel" value="validate" 
								style="font-size:10px;position:absolute;top:80%;right:25%;" class="ui-button ui-widget ui-state-default ui-corner-all">
							<input type="button" id="cancelSelectCohortStudy" value="cancel" 
								style="font-size:10px;position:absolute;top:80%;right:5%;" class="ui-button ui-widget ui-state-default ui-corner-all">
						</div>
					</div>
					<div class="ui-corner-all" style="margin-top:20px;width:100%;height:60%;float:left;">
						<div id="defineVariablePanel" class="ui-corner-all ui-widget-content" style="display:none;position:absolute;height:280px;width:400px;float:left;margin:5px;z-index:1500;">
							<div class="ui-tabs-nav ui-widget-header ui-corner-all" style="height:14%;width:100%;cursor:pointer;">
								<span style="margin:10px;font-size:28px;font-style:italic;">Define a predictor</span>
							</div>
							<table style="margin-top:10px;margin-left:2px;width:100%;">
								<tr>
									<td style="margin-right:10px;">
										<span style="display:block;font-size:12px;margin:5px;">Name: </span>
									</td>
									<td>
										<input id="nameOfPredictor" class="predictorInput" type="text"/>
									</td>
								</tr>
								<tr>
									<td style="margin-right:10px;">
										<span style="display:block;font-size:12px;margin:5px;">Description: </span>
									</td>
									<td>
										<input id="descriptionOfPredictor" class="predictorInput" type="text"/>
									</td>
								</tr>
								<tr>
									<td>
										<span style="display:block;font-size:12px;margin:5px;">Data type: </span>
									</td>
									<td>
										<select id="dataTypeOfPredictor" class="predictorInput" style="width:205px">
											<#list screen.getDataTypes() as dataType>
												<option>${dataType}</option>
											</#list>
										</select>
									</td>
								</tr>
								<tr>
									<td>
										<span style="display:block;font-size:12px;margin:5px;">Categories: </span>
									</td>
									<td>
										<input id="categoryOfPredictor" class="predictorInput" type="text" disabled="disabled"/>
									</td>
								</tr>
								<tr>
									<td>
										<span style="display:block;font-size:12px;margin:5px;">Unit: </span>
									</td>
									<td>
										<input id="unitOfPredictor" class="predictorInput" type="text"/>
									</td>
								</tr>
								<tr>
									<td>
										<span style="display:block;font-size:12px;margin:5px;">Building blocks: </span>
									</td>
									<td>
										<input id="buildingBlocks" class="predictorInput" type="text"/>
									</td>
								</tr>
							</table>
							<hr>
							<div style="margin:5px">
								<input id="cancelPredictor" type="button" value="cancel" style="float:right;font-size:12px"/>
								<input id="addPredictor" type="button" value="add" style="float:right;font-size:12px"/>
							</div>
						</div>
						<div id="summaryPanel" class="ui-corner-all ui-widget-content" style="display:none;position:absolute;left:20px;height:200px;width:40%;margin:5px;margin-top:120px;z-index:2000;">
							<div class="ui-tabs-nav ui-widget-header ui-corner-all" style="cursor:pointer;height:20%;width:100%;">
								<span style="margin:10px;font-size:28px;font-style:italic;">Summary</span>
								<div id="closeSummary" style="cursor:pointer;height:16px;width:16px;float:right;margin:10px;" class="ui-state-default ui-corner-all" title="add a new predictor">
									<span class="ui-icon ui-icon-circle-close"></span>
								</div>
							</div>
							<table id="summaryPredictorTable" style="margin-top:10px;margin-left:2px;width:100%;">
								<tr>
									<td style="margin-right:10px;">
										<span style="display:block;font-size:12px;margin:5px;">Selected prediction model</span>
									</td>
									<td>
										<input id="selectedPredictionModelName" type="text" disabled="disabled" style="display:block;" value="None"/>
									</td>
								</tr>
								<tr>
									<td style="margin-right:10px;">
										<span style="display:block;font-size:12px;margin:5px;">Number of predictors</span>
									</td>
									<td>
										<input id="numberOfPredictors" type="text" disabled="disabled" style="display:block;" value="0"/>
									</td>
								</tr>
								<tr>
									<td>
										<span style="display:block;font-size:12px;margin:5px;">Building blocks defined</span>
									</td>
									<td>
										<input id="buildingBlocksDefined" type="text" disabled="disabled" style="display:block;" value="0"/>
									</td>
								</tr>
								<tr>
									<td>
										<span style="display:block;font-size:12px;margin:5px;">Formula</span>
									</td>
									<td>
										<input id="formula" type="text" disabled="disabled" style="display:block;" value="No"/>
									</td>
								</tr>
							</table>
						</div>
						
						<div id="showPredictorPanel" style="height:100%;width:100%;float:right;">
							<div class="ui-tabs-nav ui-widget-header ui-corner-all" style="height:10%;width:100%;">
								<span style="margin:10px;font-size:24px;font-style:italic;">Predictor summary</span>
								<div id="addShowSummary" style="cursor:pointer;height:16px;width:16px;float:right;margin:10px;margin-left:2px;" class="ui-state-default ui-corner-all" title="show summary">
									<span class="ui-icon ui-icon-comment"></span>
								</div>
								<div id="addPredictorButton" style="cursor:pointer;height:16px;width:16px;float:right;margin:10px;margin-right:2px;" class="ui-state-default ui-corner-all" title="add a new predictor">
									<span class="ui-icon ui-icon-circle-plus"></span>
								</div>
							</div>
							<div class="ui-tabs-nav ui-widget-content ui-corner-all" style="height:100%;width:100%;overflow:auto;">
								<table class="ui-corner-all ui-widget-content" style="width:100%;margin-top:3%;border-bottom:1px solid #AAAAAA;">
									<tr style="width:100%;height:50px;font-size:14px;font-style:italic;">
										<th class="ui-tabs-nav ui-widget-header ui-corner-all" style="width:12%;">
											name
										</th>
										<th class="ui-tabs-nav ui-widget-header ui-corner-all" style="width:20%;">
											description
										</th>
										<th class="ui-tabs-nav ui-widget-header ui-corner-all" style="width:8%;">
											data type
										</th>
										<th class="ui-tabs-nav ui-widget-header ui-corner-all" style="width:10%;">
											unit
										</th>
										<th class="ui-tabs-nav ui-widget-header ui-corner-all" style="width:20%;">
											category
										</th>
										<th class="ui-tabs-nav ui-widget-header ui-corner-all" style="width:40%;">
											building blocks
										</th>
									</tr>
								</table>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</form>
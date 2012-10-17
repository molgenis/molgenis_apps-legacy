<#macro plugins_harmonization_Harmonization screen>
	<style>
		.predictorInput{
			display:block;
			float:right;
			width:200px;
			margin-right:15px;
		}
	</style>
	<script>
		$(document).ready(function(){
			
			//Styling for the dropDown box
			$('#selectPredictionModel').chosen().change(function(){
				
				selected = $('#selectPredictionModel').val();
				$('#selectedPrediction >span').empty().text(selected);
			});
			
			if($('#selectPredictionModel option').length == 0){
				//element = "<p style=\"color:red;\">There are no prediction models in database, add one first</p>";
				//$('#statusMessage >div').empty().append(element);
				//$('#statusMessage').effect('bounce').delay(2000).fadeOut();
				
				message = "There are no prediction models in database, add one first";
				
				showMessage(message, false);
			}
			
			$('#addPredictorButton').click(function(){
				$('#defineVariablePanel').fadeIn();
			});
			
			$('#cancelPredictor').button().click(function(){
				$('#defineVariablePanel').fadeOut();
				$('#defineVariablePanel input[type="text"]').val('');
				$('#dataType option:first-child').attr('selected','selected');
			});
			
			$('#addPredictor').button().click(function(){
				
			});
			
			//Add a new prediction model in the dropdown menu
			$('#addModelButton').click(function(){
				if($('#addPredictionModel').val() != ""){
					selected = $('#addPredictionModel').val();
					if($('#selectPredictionModel').find("option[name=\"" + selected +"\"]").length > 0){
						message = "The prediction model already existed!";
						showMessage(message, false);
					}else{
						element = "<option name=\"" + selected + "\" selected=\"selected\">" + selected + "</option>";
						$('#selectPredictionModel').append(element);
						$('#selectPredictionModel').trigger("liszt:updated");
						$('#addPredictionModel').val('');
						$('#selectedPrediction >span').empty().text(selected);
						message = "You successfully added a new prediction model</br>Please define the predictors";
						showMessage(message, true);
					}
				}
			});
			//Remove a prediction model in the dropdown menu
			$('#removeModelButton').click(function(){
				if($('#selectPredictionModel option').length > 0){
					$('#selectPredictionModel option').filter(':selected').remove();
					$('#selectPredictionModel').trigger("liszt:updated");
					selected = $('#selectPredictionModel').val();
					$('#selectedPrediction >span').empty().text(selected);
					message = "You successfully removed a prediction model!";
					showMessage(message, true);
				}
			});
		});
		
		function showMessage(message, success){
			
			element = "";
			
			if(success == true){
				element = "<p style=\"color:green;\">";
			}else{
				element = "<p style=\"color:red;\">";
			}
			element += message + "</p>";
			
			$('#statusMessage >div').empty().append(element);
			$('#statusMessage').effect('bounce').delay(2000).fadeOut();
		}
	</script>
	<form method="post" enctype="multipart/form-data" name="${screen.name}" action="">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}">
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action">
		<#--optional: mechanism to show messages-->
		<div class="formscreen">
			<div class="form_header" id="${screen.getName()}">
				${screen.label}
			</div>
			<div class="screenbody">
				<div class="screenpadding">
					<div style="height:600px;width:100%;">
						<div style="padding-left:6px;background:#DDDDDD;border:1px solid #BBBBBB;font-size:14px;height:28%;width:60%;float:left;margin-right:30px;" class="ui-corner-all">
							<span style="font-style:italic;">
								<h3>Validate prediction models</h3>
								<hr/>
							</span>
							<div id="selectedPrediction" style="height:30px;padding-left:10px;">
								Selected prediction model:
								<span style="font-size:25px;font-style:italic;"></span>
							</div>
							<hr/>
							<div style="float:left;width:43%;margin-left:10px;">
								<span style="display:block;margin-bottom:5px;">Select a prediction model:</span>
								<div style="float:left;margin-right:10px;">
									<select id="selectPredictionModel" name="selectPredictionModel" style="width:185px;" data-placeholder="Choose a prediction model">
										<#list screen.getListOfPredictionModels() as predictionModel>
											<option>
												${predictionModel}
											</option>	
										</#list>
									</select>
								</div>
								<div id="removeModelButton" style="cursor:pointer;height:18px;width:18px;float:left" class="ui-state-default ui-corner-all" title="remove">
									<span class="ui-icon ui-icon-trash"></span>
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
						<fieldset id="statusMessage" style="width:275px;height:140px;position:relative;top:-10px;" class="ui-tabs-panel ui-widget-content ui-corner-bottom">
							<legend style="font-style:italic;">
								Status
							</legend>
							<div align="justify" style="font-size:14px;padding:4px;">
								Please choose an existing prediction model or add a new prediction model 
							</div>
						</fieldset>
						<div class="ui-corner-all" style="margin-top:20px;width:100%;height:60%;float:left;background:#DDDDDD;border:1px solid #BBBBBB;">
							<div id="addPredictorButton" style="cursor:pointer;height:16px;width:16px;float:left;margin:10px;" class="ui-state-default ui-corner-all" title="add a new predictor">
								<span class="ui-icon ui-icon-circle-plus"></span>
							</div>
							<div id="defineVariablePanel" class="ui-corner-all ui-widget-content" style="display:none;position:absolute;height:280px;width:400px;float:left;margin:5px;">
								<div class="ui-tabs-nav ui-widget-header ui-corner-all" style="height:14%;width:100%;">
									<span style="margin:10px;font-size:28px;font-style:italic;">Define a predictor</span>
								</div>
								<table style="margin-top:10px;margin-left:2px;width:100%;">
									<tr>
										<td style="margin-right:10px;">
											<span style="display:block;font-size:12px;margin:5px;">Name: </span>
										</td>
										<td>
											<input name="nameOfPredictors" class="predictorInput" type="text"/>
										</td>
									</tr>
									<tr>
										<td style="margin-right:10px;">
											<span style="display:block;font-size:12px;margin:5px;">Description: </span>
										</td>
										<td>
											<input name="descriptionOfPredictors" class="predictorInput" type="text"/>
										</td>
									</tr>
									<tr>
										<td>
											<span style="display:block;font-size:12px;margin:5px;">Data type: </span>
										</td>
										<td>
											<select id="dataType" class="predictorInput" style="width:205px">
												<option>string</option>
												<option>category</option>
												<option>integer</option>
												<option>decimal</option>
											</select>
										</td>
									</tr>
									<tr>
										<td>
											<span style="display:block;font-size:12px;margin:5px;">Categories: </span>
										</td>
										<td>
											<input id="categoryOfPredictors" class="predictorInput" type="text"/>
										</td>
									</tr>
									<tr>
										<td>
											<span style="display:block;font-size:12px;margin:5px;">Unit: </span>
										</td>
										<td>
											<input id="unitOfPredictors" class="predictorInput" type="text"/>
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
							<div class="ui-corner-all ui-widget-content" style="position:absolute;right:20px;height:200px;width:40%;margin:5px;">
								<div class="ui-tabs-nav ui-widget-header ui-corner-all" style="height:20%;width:100%;">
									<span style="margin:10px;font-size:28px;font-style:italic;">Summary</span>
								</div>
								<table style="margin-top:10px;margin-left:2px;width:100%;">
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
						</div>
					</div>
				</div>
			</div>
		</div>
	</form>
</#macro>
<#macro plugins_harmonization_Harmonization screen>
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
		$(document).ready(function(){
			
			//Styling for the dropDown box
			$('#selectPredictionModel').chosen().change(function(){
				selected = $('#selectPredictionModel').val();
				$('#selectedPrediction >span').empty().text(selected);
				
				showPredictors(selected);
				//Fill out summary panel
				$('#summaryPanel').fadeIn().draggable();
			});
			
			if($('#selectPredictionModel option').length == 0){
				message = "There are no prediction models in database, add one first";
				showMessage(message, false);
			}else{
				selected = $('#selectPredictionModel').val();
				showPredictors(selected);
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
	                	defineFormula();
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
	                	removePredictionModel();
	                },
	                Cancel: function() {
	                    $( this ).dialog( "close" );
	                }
            	},
			});
			
			$('#defineFormula').button().click(function(){
				$('#defineFormulaPanel').dialog('open');
			});
			
			$('#addPredictorButton').click(function(){
				$('#defineVariablePanel').fadeIn();
			});
			
			$('#addPredictor').button().click(function(){
				addPredictor();
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
				addNewPredictionModel();
			});
			//Remove a prediction model in the dropdown menu
			$('#removeModelButton').click(function(){
				$('#confirmWindow').dialog('open');
			});
		});
		
		function insertNewRow(){
			
			message = {};
			message["message"] = "You successfully added a new predictor!";
			message["success"] = true;
			
			if($('#' + $('#nameOfPredictor').val()).length == 0){
			
				data = {};
				data["selected"] = $('#selectPredictionModel').val();
				data["name"] = $('#nameOfPredictor').val();
				data["description"] = $('#descriptionOfPredictor').val();
				data["dataType"] = $('#dataTypeOfPredictor').val();
				data["unit"] = $('#unitOfPredictor').val();
				data["category"] = uniqueElementToString($('#categoryOfPredictor').val().split(","));
				data["buildingBlocks"] = uniqueElementToString($('#buildingBlocks').val().split(","));
				
				//add the data to table
				identifier = data["name"].replace(/\s/g,"_");
				data["identifier"] = identifier;
				
				$.ajax({
					url : "${screen.getUrl()}&__action=download_json_addPredictor&data=" + JSON.stringify(data),
					async: false,
				}).done(function(status){
					message["message"] = status["message"];
					message["success"] = status["success"];
					message["identifier"] = data["identifier"];
				});
				
				if(message["success"] == true){
					
					populateRowInTable(data);
				}
				
			}else{
				message["message"] = "Predictor already existed!";
				message["success"] = false;
			}
			
			return message;
		}
		
		function populateRowInTable(data){
			
			name = data["name"];
			description = data["description"];
			dataType = data["dataType"];
			categories = data["category"];
			unit = data["unit"];
			buildingBlocks = data["buildingBlocks"];
			identifier = data["identifier"];
			
			newRow =  "<tr id=\"" + identifier + "\" name=\"" + identifier + "\" style=\"border-bottom:1px dotted #AAAAAA;\">";
			newRow += "<td name=\"name\" class=\"ui-corner-all\" style=\"border-right:1px dotted #AAAAAA;\"><span style=\"margin:10px;margin-right:2px;float:left;\">" + name + "</span>";
			newRow += "<div id=\"" + identifier + "_remove\" style=\"cursor:pointer;height:16px;width:16px;float:right;margin:10px;margin-left:3px;\" "
					+ "class=\"ui-state-default ui-corner-all\" title=\"remove this predictor\">"
					+ "<span class=\"ui-icon ui-icon-circle-close\"></span>"
					+ "</div></td>"
			newRow += "<td name=\"description\" class=\"ui-corner-all\" style=\"border-right:1px dotted #AAAAAA;\">" + description + "</td>";
			newRow += "<td name=\"dataType\" class=\"ui-corner-all\" style=\"border-right:1px dotted #AAAAAA;\">" + dataType + "</td>";
			newRow += "<td name=\"unit\" class=\"ui-corner-all\" style=\"border-right:1px dotted #AAAAAA;\">" + unit + "</td>";
			
			addedCategory = "";
			
			if(categories != "" && categories != null){
				blocks = categories.split(",");
				addedCategory = createMultipleSelect(blocks);
			}
			
			newRow += "<td name=\"category\" class=\"ui-corner-all\" style=\"border-right:1px dotted #AAAAAA;\">" + addedCategory + "</td>";
			
			selectBlocks = "";
			
			if(buildingBlocks != "" && buildingBlocks != null){
				blocks = buildingBlocks.split(",");
				selectBlocks = createMultipleSelect(blocks);
			}
			
			newRow += "<td name=\"buildingBlocks\" class=\"ui-corner-all\" style=\"border-right:1px dotted #AAAAAA;\">" + selectBlocks + "</td>";
			newRow += "</tr>";
			
			$('#showPredictorPanel table tr:first-child').after(newRow);
			
			$('td[name="buildingBlocks"] >select').chosen();
			
			$('td[name="category"] >select').chosen();
			
			$('#' + identifier + '_remove').click(function(){
				removePredictor($(this).attr('id'));
				$(this).parents('tr').eq(0).remove();
			});
			
			$('#showPredictorPanel table tr:last-child').css({
				'vertical-align':'middle',
				'text-align':'center',
				'font-size':'16px',
			});
			
			cancelAddPredictorPanel();
		}
		
		function addNewPredictionModel(){
			
			if($('#addPredictionModel').val() != ""){
				selected = $('#addPredictionModel').val();
				if($('#selectPredictionModel').find("option[name=\"" + selected +"\"]").length > 0){
					message = "The prediction model already existed!";
					showMessage(message, false);
				}else{
					message = "";
					success = "";
					$.ajax({
						url : "${screen.getUrl()}&__action=download_json_newPredictionModel&name=" + selected,
						async: false,
					}).done(function(status){
						message = status["message"];
						success= status["success"];
					});
					if(success == true){
						element = "<option name=\"" + selected + "\" selected=\"selected\">" + selected + "</option>";
						$('#selectPredictionModel').append(element);
						$('#selectPredictionModel').trigger("liszt:updated");
						$('#addPredictionModel').val('');
						$('#selectedPrediction >span').empty().text(selected);
						showPredictors(selected);
					}
					showMessage(message, success);
				}
			}
		}
		
		function removePredictionModel(){
			
			if($('#selectPredictionModel option').length > 0){
				selected = $('#selectPredictionModel').val();
				
				$.ajax({
					url : "${screen.getUrl()}&__action=download_json_removePredictionModel&name=" + selected,
					async: false,
				}).done(function(status){
					message = status["message"];
					success= status["success"];
				});
				if(success == true){
					$('#confirmWindow').dialog('close');
					$('#selectPredictionModel option').filter(':selected').remove();
					$('#selectPredictionModel').trigger("liszt:updated");
					selected = $('#selectPredictionModel').val();
					$('#selectedPrediction >span').empty().text(selected);
					showPredictors(selected);
				}
				showMessage(message, success);
			}
		}
		
		function addPredictor(){
		
			message = ""
			success = "";
			
			if( $('#selectPredictionModel option').length == 0){
				message = "Please define a prediction model first!";
				success = false;
			}else if($('#nameOfPredictor').val() == ""){
				message = "The name of predictor cannot be empty!";
				success = false;
			}else{
				result = insertNewRow();
				message = result["message"];
				success = result["success"];
				predictor = result["identifier"];
				if(success == true){
					summaryAddOne(predictor);
				}
			}
			showMessage(message, success);
		}
		
		function removePredictor(identifier){
			
			predictor = $('#' + identifier).parents('td').eq(0).text();
			selected = $('#selectPredictionModel').val();
			$.ajax({
				url : "${screen.getUrl()}&__action=download_json_removePredictors&name=" 
					+ predictor + "&predictionModel=" + selected,
				async: false,
			}).done(function(status){
				message = status["message"];
				success = status["success"];
				showMessage(message, success);
				if(success == true){
					summaryRemoveOne(identifier);
				}
			});
		}
		
		function showPredictors(predictionModelName){
			
			$.ajax({
				url : "${screen.getUrl()}&__action=download_json_showPredictors&name=" + predictionModelName,
				async: false,
			}).done(function(status){
				
				$('#selectedPredictionModelName').val(status["selected"]);
				$('#numberOfPredictors').val(status["numberOfPredictors"]);
				$('#buildingBlocksDefined').val(status["buildingBlocksDefined"]);
				$('#formula').val(status["formula"]);
				$('#showFormula').val(status["formula"]);
				delete status["selected"];
				delete status["numberOfPredictors"];
				delete status["buildingBlocksDefined"]; 
				delete status["formula"];  
				
				$('#showPredictorPanel table tr:gt(0)').remove();
				
				$.each(status, function(predictor, Info){
					populateRowInTable(Info);
				});
				
				$('#summaryPanel').fadeIn().draggable();
			});
		}
		
		function defineFormula(){
		
			selected = $('#selectPredictionModel').val();
			data = {};
			data["selected"] = selected;
			data["formula"] = $('#showFormula').val();
			$.ajax({
				url : "${screen.getUrl()}&__action=download_json_defineFormula&data=" + JSON.stringify(data),
				async: false,
			}).done(function(status){
				message = status["message"];
				success = status["success"];
				showMessage(message, success);
				if(success == true){
					$('#formula').val($('#showFormula').val());
					$('#defineFormulaPanel').dialog('close');
				}
			});
		}
		
		//Make the add predictor panel disappear
		function cancelAddPredictorPanel(){
			
			$('#defineVariablePanel').fadeOut();
			$('#defineVariablePanel input[type="text"]').val('');
			$('#dataTypeOfPredictor option:first-child').attr('selected',true);
			$('#categoryOfPredictor').attr('disabled', true);
		}
		
		//Create list with unique elements
		function uniqueElements(anArray){
			var result = [];
	       $.each(anArray, function(i,v){
	           if ($.inArray(v, result) == -1) result.push(v);
	       });
	       return result;
		}
		
		function uniqueElementToString(anArray){
			
			unique = uniqueElements(anArray);
			
			backToString = "";
			
			for(var i = 0; i < unique.length; i++){
				backToString += unique[i] + ",";
			}
			
			backToString = backToString.substring(0, backToString.length - 1);
			
			return backToString;
		}
		
		//Create a jquery chosen multiple select element
		function createMultipleSelect(listOfTerms){
			
			listOfTerms = uniqueElements(listOfTerms);
			
			selectBlocks = "<select multiple=\"true\" style=\"width:90%;\">";
			
			for(var i = 0; i < listOfTerms.length; i++){
				selectBlocks += "<option selected=\"selected\">" + listOfTerms[i] + "</option>";
			}
			selectBlocks += "</select>"
			
			return selectBlocks;
		}
		
		function summaryAddOne(identifier){
			
			numberOfPredcitors = $('#numberOfPredictors').val();
			buildingBlocksDefine = $('#buildingBlocksDefined').val();
			$('#numberOfPredictors').val(++numberOfPredcitors);
			if($('#' + identifier + ' td:last-child >select').length > 0){
				++buildingBlocksDefine
			}
			$('#buildingBlocksDefined').val(buildingBlocksDefine);
		}
		
		function summaryRemoveOne(identifier){
			numberOfPredcitors = $('#numberOfPredictors').val();
			buildingBlocksDefine = $('#buildingBlocksDefined').val();
			$('#numberOfPredictors').val(--numberOfPredcitors);
			if($('#' + identifier + ' td:last-child >select').length > 0){
				--buildingBlocksDefine;
			}
			$('#buildingBlocksDefined').val(buildingBlocksDefine);
		}
		
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
								<input type="button" id="defineFormula" value="formula" style="cursor:pointer;font-size:12px;height:25px;width:60px;float:right;margin-top:4px;margin-right:20px;" />
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
						<fieldset id="statusMessage" style="display:none;width:275px;height:140px;position:relative;top:-10px;" class="ui-tabs-panel ui-widget-content ui-corner-bottom">
							<legend style="font-style:italic;">
								Status
							</legend>
							<div align="justify" style="font-size:14px;padding:4px;">
								Please choose an existing prediction model or add a new prediction model 
							</div>
						</fieldset>
						<div class="ui-corner-all" style="margin-top:20px;width:100%;height:60%;float:left;">
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
							<div id="summaryPanel" class="ui-corner-all ui-widget-content" style="display:none;position:absolute;left:20px;height:200px;width:40%;margin:5px;margin-top:120px">
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
											<th class="ui-tabs-nav ui-widget-header ui-corner-all" style="width:20%;">
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
</#macro>
<#macro plugins_harmonization_Harmonization screen>
	<link type="text/css" href="jquery/css/smoothness/jquery-ui-1.8.7.custom.css" rel="Stylesheet"/>
	<script src="jquery/development-bundle/ui/jquery-ui-1.8.7.custom.js" language="javascript"></script>
	<script src="res/jquery-plugins/Treeview/jquery.treeview.js" language="javascript"></script>
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
		var CLASSES = $.treeview.classes;
		var settings = {};
		var searchNode = new Array();
	
		$(document).ready(function()
		{	
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
			
			$('#showCohortStudy').button().click(function(){
				$('#selectCohortStudyPanel').fadeIn();
			});
			
			$('#listOfCohortStudies').chosen();
			
			$('#validatePredictionModel').button().click(function(){
				validateStudy();
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
				addMappingFromTree();
			});
			
			$('#startNewValidation').button().click(function(){
				$('#selectCohortStudyPanel').hide();
				$('#beforeMapping').show();
				$('#afterMapping').hide();
			});
			
			$('#saveMapping').button().click(function(){
				collectMappingFromCandidate();
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
			                	collectMappingFromCandidate();
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
		
		function validateStudy()
		{
			if($('#listOfCohortStudies option').length == 0){
				
				showMessage("There are no cohort studies to validate the prediction model", false);
			
			}else if($('#selectPredictionModel option').length == 0){
			
				showMessage("There are no prediction models", false);
			
			}else{
				
				predictionModel = $('#selectPredictionModel').val();
				
				validationStudy = $('#listOfCohortStudies').val();
				
				$('#beforeMapping').hide();
				
				$('#details').empty();
				
				$('#browser').empty();
				
				$('#existingMappings').empty();
						
				$('#mappingResult').empty();
				
				$('#validatePredictors').empty();
				
				$('#matchingSelectedPredictor').text("");
						
				$('#afterMapping').show();
				
				createScreenMessage("Loading ontology...", "50%");
				
				$.ajax({
					url : "${screen.getUrl()}&__action=download_json_validateStudy&predictionModel=" + predictionModel 
						+ "&validationStudy=" + validationStudy,
					async: false,
					
				}).done(function(status){
					
					if(status["success"] == true)
					{
					
						destroyScreenMessage();
					
						$('#mappingResult').empty();
						
						$.each(status, function(key, Info)
						{
							if(key == "predictionModel")
							{
								$('#matchingPredictionModel').text(status["predictionModel"]);
							}
							else if(key == "validationStudy")
							{
								$('#matchingValidationStudy').text(status["validationStudy"]);
								
					 		}
						});
						
						createProgressBar("Loading predictor");
						
						loadMappingResult(validationStudy);
						
						destroyProgressBar();
						
						initializeTree();
						
						$('#validatePredictors option:first-child').attr('selected', true).click();
						$('#mappingResult table:first-child').show();
					}
				});
			}
		}
		
		function loadMappingResult(validationStudy)
		{	
			$.ajax({
				url : "${screen.getUrl()}&__action=download_json_loadMappingResult&validationStudy=" + validationStudy,
				async: false,
			}).done(function(status){
				
				label = status["label"];
				
				table = status["mappingResult"];
				
				existingMapping = status["existingMapping"];
				
				$('#validatePredictors').append("<option id=\"" + status["identifier"].replace(/\s/g,"_") 
					+ "\" style=\"cursor:pointer;font-family:Verdana,Arial,sans-serif;\">" + label + "</option>");
				
				$('#validatePredictors option').each(function(){
					$(this).hover(
						function(){
							$(this).css({
								"font-weight" : "bolder",
								"color" : "grey"
							});
						},function(){
							$(this).css({
								"font-weight" : "normal",
								"color" : "black"
							});
						}
					);
				});
				
				$('#mappingResult').append(table);
				
				$('#existingMappings').append(existingMapping);
				
				if(status["loadingProcess"] == status["total"])
				{
					$('#browser').empty().append(status["treeView"]);
					
					$('#mappingResult tr td:first-child').each(function(){
				
						identifier = $(this).parent().attr('id');
						identifier = identifier.replace("_row", "_details");
						$('#' + identifier).show();
						$('#' + identifier).click(function(){
							predictor = $(this).parents('table').eq(0).attr('id').replace("mapping_","");
							retrieveExpandedQueries(predictor, identifier.replace("_details", ""));
						});	
						
						$(this).hover(
							function () {
								$(this).css("font-weight", "bolder");
								identifier = $(this).parent().attr('id');
								identifier = identifier.replace("_row", "_details");
								$('#' + identifier).show();
							}, 
							function () {
								$(this).css("font-weight", "normal");
								//identifier = $(this).parent().attr('id');
								//identifier = identifier.replace("_row", "_details");
								//$('#' + identifier).hide();
							}
						);
						
						$(this).children('span').click(function(){
							
							measurementName = $(this).text();
							
							trackInTree(measurementName);
						});
					});
					
					$('#validatePredictors').click(function(){
						
						predictorName = $(this).find('option:selected').eq(0).text();
						
						$('#matchingSelectedPredictor').text(predictorName);
						
						$('#mappingResult table').hide();
						
						$('#existingMappings table').hide();
						
						id = $("#validatePredictors option:selected").attr('id');
						
						identifierForNew = "mapping_" + id;
						
						$('#' + identifierForNew).show();
						
						identifierForExisting = "matched_" + id;
						
						$('#' + identifierForExisting).show();
					});
					
					$('#existingMappings tr td:last-child >div').each(function()
					{
						$(this).click(function(){
							removeSingleMapping($(this));
						});
						
						span = $(this).parents('tr').eq(0).find('td >span');
						
						measurementName = $(span).text();
						
						$(span).click(function(){
							trackInTree($(this).text());
						});
					});
					
				}else{
					updateProgressBar(status["loadingProcess"]/status["total"]);
					loadMappingResult(validationStudy);
				}
			});
		}
		
		function addMappingFromTree()
		{	
			mappedVariableId = $('#clickedVariable').val();
			
			measurementName = new Array();
			
			measurementName[0] = $('#' + mappedVariableId).find('span').text();
			
			predictor = $('#validatePredictors option:selected').attr('id');
		
			mappings = {};
			
			mappings[predictor] = measurementName;
		
			saveMapping(mappings);
		}
		
		function removeSingleMapping(element)
		{
			data = {};
			
			data["measurementName"] = $(element).parents('tr').eq(0).find('td:first-child span').text();
			
			data["mappingIdentifier"] = $(element).attr('id').replace('_remove','');
			
			data["validationStudy"] = $('#matchingValidationStudy').text();
			
			data["predictionModel"] = $('#matchingPredictionModel').text();
			
			$.ajax({
				url : "${screen.getUrl()}&__action=download_json_removeMapping&predictor=" 
					+ $('#validatePredictors').val(),
				async : false,
				data : data,
			}).done(function(status){
				
				if(status["success"] == true){
					showMessage(status["message"], true);
					$(element).parents('tr').eq(0).remove();
				}else{
					showMessage(status["message"], false);
				}
			});
		}
		
		function trackInTree(measurementName)
		{	
			$.ajax({
				url : "${screen.getUrl()}&__action=download_json_getPosition&measurementName=" + measurementName,
				async: false,
			}).done(function(status){
				
				identifier = status["identifier"];
				
				$('#browser').empty().append(status["treeView"]).treeview(settings);
				$("#browser").find('li').each(function(){
					toggleNodeEvent($(this));
				});
				
				$('#' + identifier).children('span').trigger('click');
				
				window.scrollTo(0, 500);
				
				var elementTop = $('#' + identifier).position().top;
				var treeDivTop = $('#treeView').position().top;
				var divHeight = $('#treeView').height();
				var lastTop = $('#treeView').scrollTop();
				$('#treeView').scrollTop(lastTop + elementTop - divHeight/3 - treeDivTop);
				
			});
		}
		
		function collectMappingFromCandidate()
		{	
			mappings = {};
			
			$('#mappingResult input:checkbox').each(function(){
				
				if($(this).is(':checked')){
					
					predictor = $(this).parents('table').eq(0).attr('id').replace("mapping_", "");
					
					measurementName = $(this).parents('tr').eq(0).find('td>span').text();
					
					features = new Array();
					
					if(mappings[predictor] == null)
					{
						features[0] = measurementName;
					}else
					{
						features = mappings[predictor];
						index = features.length;
						features[index] = measurementName;
					}
					
					mappings[predictor] = features;	
				}
			});
			
			saveMapping(mappings);
		}
		
		function saveMapping(mappings)
		{	
			validationStudy = $('#matchingValidationStudy').text();
			
			predictionModel = $('#matchingPredictionModel').text();
			
			createScreenMessage("Save mappings!", "50%");
			
			$.ajax({
				url : "${screen.getUrl()}&__action=download_json_saveMapping&mappingResult=" 
					+ JSON.stringify(mappings) + "&validationStudy=" + validationStudy 
					+ "&predictionModel=" + predictionModel,
				async: false,
				
			}).done(function(status){
				
				if(status["success"] == true)
				{
					$.each(status, function(key, table)
					{
						if(key != "success" & key != "message")
						{
							$('#matched_' + key).remove();
							
							$('#existingMappings').append(table);
							
							$('#matched_' + key + " tr td:last-child >div").each(function(){
								$(this).click(function(){
									removeSingleMapping($(this));
								});
							});
							$('#' + key).click(function(){
								$('#existingMappings table').hide();
								$('#matched_' + key).show();
							});
							
							$('#' + key).trigger('click');
						}
					});
					$('#candidateMapping input:checkbox').attr('checked', false);
					showMessage(status["message"], true);
					
				}else{
					showMessage(status["message"], false);
				}
			});
			
			destroyScreenMessage();
		}
		
		function retrieveExpandedQueries(predictor, matchedVariable)
		{	
			$.ajax({
				url : "${screen.getUrl()}&__action=download_json_retrieveExpandedQuery&predictor="
					+ predictor + "&matchedVariable=" + matchedVariable,
				async: false,
				
			}).done(function(status){
				
				table = status["table"];
				
				$('#afterMapping').append(table);
				
				$('#' + matchedVariable).dialog({
					title : "Expanded queries",
					height: 300,
	            	width: 600,
	            	modal: true,
	            	buttons: {
		                Cancel: function() {
		                    $( this ).dialog( "close" );
		                }
	            	},
				});
				$('#' + matchedVariable).show();
			});
		}
		
		function insertNewRow()
		{	
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
				data["category"] = uniqueElementToString($('#categoryOfPredictor').val().split(","), ",");
				
				buildingBlockString = uniqueElementToString($('#buildingBlocks').val().split(";"), ";");
				
				data["buildingBlocks"] = uniqueElementToString(buildingBlockString.split(","), ",");
				
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
		
		function populateRowInTable(data)
		{
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
			
			if(categories != "" && categories != null)
			{
				blocks = categories.split(",");
				addedCategory = createMultipleSelect(blocks);
			}
			
			newRow += "<td name=\"category\" class=\"ui-corner-all\" style=\"border-right:1px dotted #AAAAAA;\">" + addedCategory + "</td>";
			
			selectBlocks = "";
			
			if(buildingBlocks != "" && buildingBlocks != null)
			{
				
				for( var i = 0 ; i < buildingBlocks.split(";").length ; i++)
				{
					eachDefinition = buildingBlocks.split(";")[i];
					blocks = eachDefinition.split(",");
					selectBlocks += createMultipleSelect(blocks);		
				}
			}
			
			newRow += "<td name=\"buildingBlocks\" class=\"ui-corner-all\" style=\"border-right:1px dotted #AAAAAA;\">" + selectBlocks + "</td>";
			newRow += "</tr>";
			
			$('#showPredictorPanel table tr:first-child').after(newRow);
			
			$('td[name="buildingBlocks"] >select').chosen();
			
			$('td[name="category"] >select').chosen();
			
			$('#' + identifier + '_remove').click(function(){
				removePredictor($(this));
			});
			
			$('#showPredictorPanel table tr:last-child').css({
				'vertical-align':'middle',
				'text-align':'center',
				'font-size':'16px',
			});
			
			cancelAddPredictorPanel();
		}
		
		function addNewPredictionModel()
		{	
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
		
		function removePredictionModel()
		{	
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
		
		function addPredictor()
		{
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
		
		function removePredictor(element)
		{	
			predictor = $(element).parents('td').eq(0).text();
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
					$(element).parents('tr').eq(0).remove();
				}
			});
		}
		
		function showPredictors(predictionModelName)
		{	
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
		
		function defineFormula()
		{
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
		function cancelAddPredictorPanel()
		{	
			$('#defineVariablePanel').fadeOut();
			$('#defineVariablePanel input[type="text"]').val('');
			$('#dataTypeOfPredictor option:first-child').attr('selected',true);
			$('#categoryOfPredictor').attr('disabled', true);
		}
		
		//Create list with unique elements
		function uniqueElements(anArray)
		{
			var result = [];
	    	$.each(anArray, function(i,v){
	        	if ($.inArray(v, result) == -1) result.push(v);
	    	});
	    	return result;
		}
		
		function uniqueElementToString(anArray, separator)
		{	
			unique = uniqueElements(anArray);
			
			backToString = "";
			
			for(var i = 0; i < unique.length; i++){
				if(separator != null){
					backToString += unique[i] + separator;
				}else{
					backToString += unique[i] + ",";
				}
			}
			
			backToString = backToString.substring(0, backToString.length - 1);
			
			return backToString;
		}
		
		//Create a jquery chosen multiple select element
		function createMultipleSelect(listOfTerms)
		{	
			listOfTerms = uniqueElements(listOfTerms);
			
			selectBlocks = "<select multiple=\"true\" style=\"width:90%;\">";
			
			for(var i = 0; i < listOfTerms.length; i++){
				selectBlocks += "<option selected=\"selected\">" + listOfTerms[i] + "</option>";
			}
			selectBlocks += "</select>"
			
			return selectBlocks;
		}
		
		function summaryAddOne(identifier)
		{	
			numberOfPredcitors = $('#numberOfPredictors').val();
			buildingBlocksDefine = $('#buildingBlocksDefined').val();
			$('#numberOfPredictors').val(++numberOfPredcitors);
			if($('#' + identifier + ' td:last-child >select').length > 0){
				++buildingBlocksDefine
			}
			$('#buildingBlocksDefined').val(buildingBlocksDefine);
		}
		
		function summaryRemoveOne(identifier)
		{
			numberOfPredcitors = $('#numberOfPredictors').val();
			buildingBlocksDefine = $('#buildingBlocksDefined').val();
			$('#numberOfPredictors').val(--numberOfPredcitors);
			if($('#' + identifier + ' td:last-child >select').length > 0){
				--buildingBlocksDefine;
			}
			$('#buildingBlocksDefined').val(buildingBlocksDefine);
		}
		
		function showMessage(message, success)
		{	
			messagePanel = "<fieldset id=\"statusMessage\" class=\"ui-tabs-panel ui-widget-content ui-corner-bottom\">"
						 + "<legend style=\"font-style:italic;\">Status</legend>"
						 + "<div align=\"justify\" style=\"font-size:14px;padding:4px;\">"
						 + "Please choose an existing prediction model or add a new prediction model </div></fieldset>"		
			
			$('#messagePanel').empty().append(messagePanel);
			
			$('#statusMessage').css({
				position : "absolute",
				width : "250px",
				height : "140px",
				top : 150,
				right : 30
			});
			
			element = "";
			
			if(success == true){
				element = "<p style=\"color:green;\">";
			}else{
				element = "<p style=\"color:red;\">";
			}
			
			element += message + "</p>";
			
			$('#statusMessage >div').empty().append(element);
			
			$('#statusMessage').effect('bounce').delay(2000).fadeOut().delay(2000);
		}
		
		function createScreenMessage(showMessage, height)
		{	
			if(height == null)
			{
				height = "50%";
			}
			
			showModal();
				
			$('body').append("<div id=\"progressbar\" class=\"middleBar\">" + showMessage + "</div>");
		
			$('.middleBar').css({
				'left' : '46%',
				'top' : height,
				'height' : 50,
				'width' : 300,
			    'position' : 'absolute',
			    'z-index' : 1501,
			});
		}
		
		function destroyScreenMessage()
		{			
			$('.modalWindow').remove();
			
			$('#progressbar').remove();
		}
		
		function createProgressBar(showMessage)
		{	
			showModal();
						
			$('body').append("<div id=\"progressbar\" class=\"middleBar\"></div>");
			$('body').append("<div id=\"LoadPredictor\" class=\"middleBar\" style=\"padding-left:60px;padding-top:15px;font-style:italic;\">" 
				+ showMessage + "</div>");
			
			$("#progressbar").progressbar({value: 0});
			
			$('.middleBar').css({
				'left' : '38%',
				'top' : '50%',
				'height' : 50,
				'width' : 300,
			    'position' : 'absolute',
			    'z-index' : 1501,
			});
		}
		
		function updateProgressBar(percentage)
		{
			$("#progressbar").progressbar({
				value: percentage * 100
			});
		}
		
		function destroyProgressBar()
		{	
			$('.middleBar').remove();
			$('.modalWindow').remove();
			$('#progressbar').remove();
		}
		
		function toggler() 
		{
			$(this)
				.parent()
				// swap classes for hitarea
				.find(">.hitarea")
					.swapClass( CLASSES.collapsableHitarea, CLASSES.expandableHitarea )
					.swapClass( CLASSES.lastCollapsableHitarea, CLASSES.lastExpandableHitarea )
				.end()
				// swap classes for parent li
				.swapClass( CLASSES.collapsable, CLASSES.expandable )
				.swapClass( CLASSES.lastCollapsable, CLASSES.lastExpandable )
				// find child lists
				.find( ">ul" )
				// toggle them
				.heightToggle( settings.animated, settings.toggle );
			if ( settings.unique ) {
				$(this).parent()
					.siblings()
					// swap classes for hitarea
					.find(">.hitarea")
						.replaceClass( CLASSES.collapsableHitarea, CLASSES.expandableHitarea )
						.replaceClass( CLASSES.lastCollapsableHitarea, CLASSES.lastExpandableHitarea )
					.end()
					.replaceClass( CLASSES.collapsable, CLASSES.expandable )
					.replaceClass( CLASSES.lastCollapsable, CLASSES.lastExpandable )
					.find( ">ul" )
					.heightHide( settings.animated, settings.toggle );
			}
		}
		
		function toggleNodeEvent(clickedNode)
		{	
			$(clickedNode).children('span').click(function(){
				
				var nodeName = $(this).parent().attr('id');
				
				if($('#' + nodeName).find('li').length > 0){
					
					$.ajax({
						url:"${screen.getUrl()}&__action=download_json_toggleNode&nodeIdentifier=" + nodeName,
						async: false,
					}).done();
					
				}else{
					
					$.ajax({
						url:"${screen.getUrl()}&__action=download_json_getChildren&nodeIdentifier=" + nodeName,
						async: false,
					}).done(function(result){
						var addedNodes = result["result"];
						var branch = "<li class=\"open\"><span class=\"folder\">test</span><ul><li class=\"open\"><span class=\"folder\">test2</li></ul></li>";
						$('#' + nodeName + ' >ul').prepend(addedNodes);
						var prepareBranches = $.fn.prepareBranches;
						var applyClasses = $.fn.applyClasses;
						
						var branches = $('#' + nodeName).find('li').prepareBranches(settings);
						
						branches.applyClasses(settings, toggler);
						
						$('#' + nodeName).find('li').each(function(){
							toggleNodeEvent($(this));
						});
					});
				}
				
				if($(this).parent().find('li').size() == 0){
					highlightClick($(this).parent());
					$('#details').empty();
					$.ajax({
						url:"${screen.getUrl()}&__action=download_json_showInformation&variableName=" + $(this).parent().attr('id'),
	      				async: false
	      			}).done(function(data) {
						$('#details').append(data["result"]);
					});
				}
			});
		}
		
		function searchTree(token)
		{	
			createScreenMessage("Searching...","70%");
			
			if(token != ""){
				array = {};
				
				$.ajax({
					url:"${screen.getUrl()}&__action=download_json_search&searchToken=" + token,
					async: false,
				}).done(function(result){
					array = result["result"];
				});
				
				$('#browser').empty().append(array);
				
				var branches = $('#browser li').prepareBranches(settings);
				
				branches.applyClasses(settings, toggler);
			}
			
			destroyScreenMessage();
			
			$('#browser li').each(function(){
				toggleNodeEvent($(this));
			});
		}
		
		function loadTree()
		{	
			status = {};
			
			$.ajax({
				url:"${screen.getUrl()}&__action=download_json_loadingTree",
				async: false,
			}).done(function(result){
				status = result;
			});
			
			updateProgressBar(status["result"]/5);
			
			if(status["status"] == false){
				loadTree();
			}
		}
		
		function showModal()
		{	
			$('body').append("<div class=\"modalWindow\"></div>");
			
			$('.modalWindow').css({
				'left' : 0,
				'top' : 0,
				'height' : $('.formscreen').height() + 200,
				'width' : $('body').width(),
			    'position' : 'absolute',
			    'z-index' : '1500',
			    'opacity' : '0.5',
			    background : 'grey'
			});
		}
		
		function highlightClick(clickedBranch)
		{	
			$(clickedBranch).children('span').css({
				'color':'#778899',
				'font-size':17,
				'font-style':'italic',
				'font-weight':'bold'
			});
			
			var measurementID = $(clickedBranch).attr('id');
			
			var clickedVar = $('#clickedVariable').val();
			
			if(clickedVar != "" && clickedVar != measurementID){
				$('#' + clickedVar + '>span').css({
					'color':'black',
					'font-size':15,
					'font-style':'normal',
					'font-weight':400
				});
				var parentOld = $('#' + clickedVar).parent().siblings('span');
		
				$(parentOld).css({
					'color':'black',
					'font-size':15,
					'font-style':'normal',
					'font-weight':400									
				});
			}
			$('#clickedVariable').val(measurementID);
		}
		
		function initializeTree()
		{	
			$('#search').button().click(function()
			{
				token = $('#searchField').val();
				searchTree(token);
			});
			
			$('#clearButton').button().click(function()
			{
				
				array = {};
				$.ajax({
					url:"${screen.getUrl()}&__action=download_json_clearSearch",
					async: false,
				}).done(function(result){
					array = result["result"];
				});
				
				$('#browser').empty().append(array).treeview(settings);
				
				$("#browser").find('li').each(function(){
					
					toggleNodeEvent($(this));
				});
				
				$('#searchField').val('');
			});
			
			$("#browser").treeview(settings).css('font-size', 16).find('li').show();	
			
			$("#browser").find('li').each(function(){
				
				toggleNodeEvent($(this));
			});	
			
			createProgressBar("Loading catalogue");
			
			loadTree();
			
			destroyProgressBar();
			
			$('#treePanel').show();
		}
		
		function checkSearchingStatus()
		{	
			if($('#searchField').val() === "")
			{
				$('#clearButton').trigger('click');
			}
		}
		
		function whetherReload()
		{	
			var value = $('#searchField').val();
			
			if(value.search(new RegExp("\\w", "gi")) != -1)
			{
				searchTree(value);
			}
			return false;
		}
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
										onkeyup="checkSearchingStatus();" onkeypress="if(event.keyCode === 13){;return whetherReload();}"/>
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
						<div id="selectCohortStudyPanel" style="display:none;margin-left:-20px;width:300px;height:28%;position:relative;float:left;" class="ui-tabs-panel ui-widget-content ui-corner-bottom">
							<div class="ui-tabs-nav ui-widget-header ui-corner-all" style="height:14%;width:100%;">
								<span style="margin:10px;font-size:20px;font-style:italic;">Select a cohort study</span>
							</div>
							<div style="margin-top:20px;margin-left:10px;height:70%;">
								<select id="listOfCohortStudies" style="width:185px;">
									<#if screen.getListOfCohortStudies()??>
										<#list screen.getListOfCohortStudies() as study>
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
</#macro>
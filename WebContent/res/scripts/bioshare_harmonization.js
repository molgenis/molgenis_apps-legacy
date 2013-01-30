function monitorJobs(url)
{
	$.ajax({
		url : url + "&__action=download_json_monitorJobs",
		async: false,
	}).done(function(status){

		$('#processedTime').text("");

		$('#estimatedTime').text("");

		percentage = 0;

		processedTime = (status["currentTime"] - status["startTime"])/1000;

		estimatedTimeString = "calculating...";

		if(status["totalQuery"] != 0 && status["finishedQuery"] != 0)
		{
			percentage = status["finishedQuery"] / status["totalQuery"];

			estimatedTime = processedTime / percentage * (1 - percentage);

			estimatedTimeString = (estimatedTime/60).toFixed(0) + " mins " + (estimatedTime%60).toFixed(0) + " secs";
		}

		$('#processedTime').text((processedTime/60).toFixed(0) + " mins " + (processedTime%60).toFixed(0) + " secs");

		$('#estimatedTime').text(estimatedTimeString);
		
		$('#jobTitle').text(status["jobTitle"]);

		$('#progressBarMessage').text(" " + (percentage * 100).toFixed(2) + "%");

		$('#progressBar').progressbar({value: percentage * 100});

		if(status["totalQuery"] == status["finishedQuery"])
		{
			clearInterval(timer);

			$('#resultPanel').show();
		}			
	});
}

function retrieveResult(url)
{
	$.ajax({
		url : url + "&__action=download_json_retrieveResult",
		async: false,
	}).done(function(status){

		$('#beforeMapping').hide();

		$('#details').empty();

		$('#mappingResult').empty();

		$('#validatePredictors').empty();

		$('#matchingSelectedPredictor').text("");

		$('#afterMapping').show();

		$('#matchingPredictionModel').text($('#selectPredictionModel').val());

		$('#matchingValidationStudy').text($('#listOfCohortStudies').val());

		$.each(status, function(key, Info)
		{
			if(key == "treeView")
			{
				$('#browser').empty().append(status["treeView"]);
				
			}else{
				
				label = Info["label"];

				table = Info["mappingResult"];

				existingMapping = Info["existingMapping"];

				$('#mappingResult').append(table);

				$('#existingMappings').append(existingMapping);

				$('#validatePredictors').append("<option id=\"" + Info["identifier"].replace(/\s/g,"_")
						+ "\" style=\"cursor:pointer;font-family:Verdana,Arial,sans-serif;\">" + label + "</option>");
			}
		});
		
		$('#validatePredictors option').each(function()
		{
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
		
		$('#mappingResult tr td:first-child').each(function(){

			identifier = $(this).parent().attr('id');
			
			identifier = identifier.replace("_row", "_details");
			
			$('#' + identifier).show();
			
			$('#' + identifier).click(function()
			{
				predictor = $(this).parents('table').eq(0).attr('id').replace("mapping_","");
				retrieveExpandedQueries(predictor, $(this).attr('id').replace("_details", ""), url);
			});	
		});

		$('#validatePredictors').click(function()
		{
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
				removeSingleMapping($(this), url);
			});

			span = $(this).parents('tr').eq(0).find('td >span');

			measurementName = $(span).text();

			$(span).click(function(){
				trackInTree($(this).text(), url);
			});
		});
		
//		initializeTree(url);

		$('#validatePredictors option:first-child').attr('selected', true).click();

		$('#mappingResult table:first-child').show();
	});
}

function showExistingMapping(url)
{	
	if($('#listOfCohortStudies option').length == 0){

		showMessage("There are no cohort studies to validate the prediction model", false);

	}else if($('#selectPredictionModel option').length == 0){

		showMessage("There are no prediction models", false);

	}else{
		
		predictionModel = $('#selectPredictionModel').val();
	
		validationStudy = $('#listOfCohortStudies').val();
		
		$.ajax({
			
			url : url + "&__action=download_json_existingMapping&selectPredictionModel=" 
				+ predictionModel + "&listOfCohortStudies=" + validationStudy,
			async: false,
			
		}).done(function(status){
			
			retrieveResult(url);
		});
	}
}

function validateStudy(formName)
{
	if($('#listOfCohortStudies option').length == 0){

		showMessage("There are no cohort studies to validate the prediction model", false);

	}else if($('#selectPredictionModel option').length == 0){

		showMessage("There are no prediction models", false);

	}else{

		predictionModel = $('#selectPredictionModel').val();

		validationStudy = $('#listOfCohortStudies').val();

		$('#details').empty();

		$('#browser').empty();

		$('#existingMappings').empty();

		$('#mappingResult').empty();

		$('#validatePredictors').empty();

		$('#matchingSelectedPredictor').text("");

		$("input[name=\"__action\"]").val("loadMapping");

		$("form[name=\"" + formName + "\"]").submit();
	}
}

function addMappingFromTree(url)
{	
	mappedVariableId = $('#clickedVariable').val();

	measurementName = new Array();

	measurementName[0] = $('#' + mappedVariableId).find('span').text();

	predictor = $('#validatePredictors option:selected').attr('id');

	mappings = {};

	mappings[predictor] = measurementName;

	saveMapping(mappings, url);
}

function removeSingleMapping(element, url)
{
	data = {};

	data["measurementName"] = $(element).parents('tr').eq(0).find('td:first-child span').text();

	data["mappingIdentifier"] = $(element).attr('id').replace('_remove','');

	data["validationStudy"] = $('#matchingValidationStudy').text();

	data["predictionModel"] = $('#matchingPredictionModel').text();

	$.ajax({
		url : url + "&__action=download_json_removeMapping&predictor=" 
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

function collectMappingFromCandidate(url)
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

	saveMapping(mappings, url);
}

function saveMapping(mappings, url)
{	
	validationStudy = $('#matchingValidationStudy').text();

	predictionModel = $('#matchingPredictionModel').text();

	createScreenMessage("Save mappings!", "50%");

	$.ajax({
		url : url + "&__action=download_json_saveMapping&mappingResult=" 
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
							removeSingleMapping($(this), url);
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

function retrieveExpandedQueries(predictor, matchedVariable, url)
{	
	$.ajax({
		url : url + "&__action=download_json_retrieveExpandedQuery&predictor="
		+ predictor + "&matchedVariable=" + matchedVariable,
		async: false,

	}).done(function(status){

		table = status["table"];

		$('#afterMapping').append(table);

//		$('#' + matchedVariable).dialog({
//			title : "Expanded queries",
//			height: 300,
//			width: 600,
//			modal: true,
//			buttons: {
//				Cancel: function() {
//					$( this ).dialog( "close" );
//				}
//			},
//		});
//		$('#' + matchedVariable).show();
		
		$('#' + matchedVariable).modal('show');
	});
}

function insertNewRow(url)
{	
	message = {};
	message["message"] = "You successfully added a new predictor!";
	message["success"] = true;

	if($('#' + $('#nameOfPredictor').val()).length == 0){

		data = {};
		
		data["selected"] = $('#selectPredictionModel').val();
		data["name"] = $('#nameOfPredictor').val() + "_" + data["selected"];
	
		if($('#labelOfPredictor').val() == "")
		{
			data["label"] = $('#nameOfPredictor').val();
		
		}else{
			data["label"] = $('#labelOfPredictor').val();
		}
		data["description"] = $('#descriptionOfPredictor').val();
		data["dataType"] = $('#dataTypeOfPredictor').val();
		data["unit_name"] = $('#unitOfPredictor').val();
		data["categories_name"] = uniqueElementToString($('#categoryOfPredictor').val().split(","), ",");

		buildingBlockString = uniqueElementToString($('#buildingBlocks').val().split(";"), ";");

		data["buildingBlocks"] = uniqueElementToString(buildingBlockString.split(","), ",");

		//add the data to table
		identifier = data["name"].replace(/\s/g,"_");
		
		data["identifier"] = identifier;

		$.ajax({
			url :  url + "&__action=download_json_addPredictor&data=" + JSON.stringify(data),
			async: false,
		}).done(function(status){
			message["message"] = status["message"];
			message["success"] = status["success"];
			data["identifier"] = message["identifier"];
		});

		if(message["success"] == true){

			populateRowInTable(data, url);
		}

	}else{
		message["message"] = "Predictor already existed!";
		message["success"] = false;
	}

	return message;
}

function populateRowInTable(data, url)
{
	name = data["name"];
	label = data["label"];
	description = data["description"];
	identifier = data["identifier"];

	newRow =  "<tr id=\"" + identifier + "\" name=\"" + identifier + "\" style=\"width:100%;\">";
	newRow += "<td name=\"variableName\" class=\"ui-corner-all\"><span style=\"float:left;cursor:pointer;\">" + name + "</span>";
	newRow += "<input id=\"" + name + "_name\" type=\"hidden\" value=\"" + name + "\"/>";
	newRow += "<div id=\"" + identifier + "_remove\" style=\"cursor:pointer;height:16px;width:16px;float:right;margin:10px;margin-left:2px;\" "
	+ "class=\"ui-state-default ui-corner-all\" title=\"remove this predictor\">"
	+ "<span class=\"ui-icon ui-icon-circle-close\"></span>"
	+ "</div></td>";
	newRow += "<td id=\"label\" name=\"label\" class=\"ui-corner-all\">" + label + "</td>";
	newRow += "<td id=\"description\" name=\"description\" class=\"ui-corner-all\">" + description + "</td></tr>";

	$('#overviewTable').append(newRow);

	$('#' + identifier + '_remove').click(function(){
		removePredictor($(this), url);
	});

	$('#showPredictorPanel table tr:last-child').css({
		'vertical-align':'middle',
		'text-align':'center',
		'font-size':'16px',
	});
	
	$('#' + identifier).data('dataObject', data);
	
	$('#' + identifier + ' td:first-child').click(function(){
		
		dataObject = $(this).parents('tr').eq(0).data("dataObject");
		
		$(document).data("selectedVariable", dataObject["name"]);
		
		identifier = dataObject["identifier"];
		label = dataObject["label"];
		name = dataObject["name"];
		description = dataObject["description"];
		dataType = data["dataType"];
		categories = data["category"];
		unit = data["unit"];
		buildingBlocks = data["buildingBlocks"];
		
		row = "<tr><th class=\"ui-corner-all\">ID:</td>";
		row += "<td class=\"ui-corner-all\">" + identifier + "<div style=\"cursor:pointer;height:16px;width:16px;" 
			+ "float:right;margin:10px;margin-left:2px;\" "
			+ "class=\"ui-state-default ui-corner-all\" title=\"remove this predictor\">"
			+ "<span class=\"ui-icon ui-icon-circle-close\"></span>"
			+ "</div></td></tr>";
		row += "<tr><th class=\"ui-corner-all\">Name:</td>";
		row += "<td class=\"ui-corner-all\"><span style=\"float:left;cursor:pointer;\">" + name + "</span></td></tr>";
		row += "<tr><th class=\"ui-corner-all\">Label:</td>";
		row += "<td class=\"ui-corner-all\">" + label + "</td></tr>";
		row += "<tr><th class=\"ui-corner-all\">Description:</td>";
		row += "<td id=\"variableDescription\" name=\"variableDescription\"  class=\"ui-corner-all\">" + description + "</td></tr>";

		if(buildingBlocks != "" && buildingBlocks != null)
		{
			selectBlocks = "";
			
			for( var i = 0 ; i < buildingBlocks.split(";").length ; i++)
			{
				eachDefinition = buildingBlocks.split(";")[i];
				blocks = eachDefinition.split(",");
				selectBlocks += createMultipleSelect(blocks);		
			}
			
			row += "<tr><th class=\"ui-corner-all\">Building blocks:</td>";
			row += "<td id=\"variableBuildingBlocks\" name=\"variableBuildingBlocks\" class=\"ui-corner-all\">" + selectBlocks + "</td></tr>";
		}
		
		row += "<tr><td></td><td><input type=\"button\" id=\"matchSelectedVariable\"  style=\"margin-left:250px;\"" 
			+  "class=\"btn btn-info btn-small\" value=\"Match selected variable\"></td><tr>";
		
		$('#variableDetail').empty().append(row);
		
		$('td[name="variableBuildingBlocks"] >select').chosen();

		$('td[name="category"] >select').chosen();
		
		$('#variableDetail tr:eq(0) div').click(function()
		{
			$('#variableDetail').empty();
			$('#overviewTable td').show();
			$('#overviewTable th').show();
			$('#overviewTable').parents('div').eq(0).width("100%");
			$("input[name=\"selectedVariableID\"]").val(null);
		});
		
		$('#matchSelectedVariable').click(function()
		{	
			formName = $(this).parents('form').eq(0).attr('name');
			
			selectedVariableID = $(document).data('selectedVariable');
			
			$("input[name=\"selectedVariableID\"]").val(selectedVariableID);
			
			$("input[name=\"__action\"]").val("loadMapping");
			
			$("form[name=\"" + formName + "\"]").submit();
		});
		
		$('#overviewTable tr').each(function(){
			
			var i = 0;
			
			$(this).children().each(function()
			{
				if(i > 0)
				{
					$(this).hide();
				}
				
				i++;
			});
		});
		
		$('#overviewTable').parents('div').eq(0).css('width', '35%');
	});

	cancelAddPredictorPanel();
}

function addNewPredictionModel(url)
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
				url : url + "&__action=download_json_newPredictionModel&name=" + selected,
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
				showPredictors(selected, url);
			}
			showMessage(message, success);
		}
	}
}

function removePredictionModel(url)
{	
	if($('#selectPredictionModel option').length > 0)
	{
		selected = $('#selectPredictionModel').val();

		$.ajax({
			url : url + "&__action=download_json_removePredictionModel&name=" + selected,
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
			showPredictors(selected, url);
		}
		showMessage(message, success);
	}
}

function addPredictor(url)
{
	message = "";
	success = "";

	if( $('#selectPredictionModel option').length == 0){
		message = "Please define a prediction model first!";
		success = false;
	}else if($('#nameOfPredictor').val() == ""){
		message = "The name of predictor cannot be empty!";
		success = false;
	}else{
		result = insertNewRow(url);
		message = result["message"];
		success = result["success"];
		predictor = result["identifier"];
	}
	
	showMessage(message, success);
	
	showPredictors($('#selectPredictionModel').val(), url);
}

function removePredictor(element, url)
{	
	predictorName = $(element).parents('td').children('input:hidden').eq(0).val();
	
	predictorID = $(element).parents('tr').eq(0).attr('id');
	
	selected = $('#selectPredictionModel').val();
	
	$.ajax({
		url : url + "&__action=download_json_removePredictors&name=" 
		+ predictorName + "&predictorID=" + predictorID + "&predictionModel=" + selected,
		async: false,
	}).done(function(status){
		
		message = status["message"];
		
		success = status["success"];
		
		showMessage(message, success);
		
		if(success == true)
		{
			$(element).parents('tr').eq(0).remove();
		}
	});
}

function showPredictors(predictionModelName, url)
{	
	$.ajax({
		url : url + "&__action=download_json_showPredictors&name=" + predictionModelName,
		async: false,
	}).done(function(status){
		
		$('#selectedPredictionModelName').val(status["name"]);
		
		$('#formula').val(status["formula"]);
		
		$('#showFormula').val(status["formula"]);

		$('#showPredictorPanel table tr:gt(0)').remove();
		
		listOfFeatures = status["predictorObjects"];
		
		if(listOfFeatures != null)
		{
			for(var i = 0; i < listOfFeatures.length; i++)
			{
				populateRowInTable(listOfFeatures[i], url);
			}
		}
		
//		$.each(predictionModelObject["predictorObjects"], function(predictor, Info){
//			populateRowInTable(Info, url);
//		});
	});
}

function defineFormula(url)
{
	selected = $('#selectPredictionModel').val();
	data = {};
	data["selected"] = selected;
	data["formula"] = $('#showFormula').val();
	$.ajax({
		url : url + "&__action=download_json_defineFormula&data=" + JSON.stringify(data),
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
	$('#defineVariablePanel').modal('hide');
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
	selectBlocks += "</select>";

	return selectBlocks;
}

function showMessage(message, success)
{	
	messagePanel = "<fieldset id=\"statusMessage\" class=\"ui-tabs-panel ui-widget-content ui-corner-bottom\">"
		+ "<legend style=\"font-style:italic;\">Status</legend>"
		+ "<div align=\"justify\" style=\"font-size:14px;padding:4px;\">"
		+ "Please choose an existing prediction model or add a new prediction model </div></fieldset>";		

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
	$('body').append("<div id=\"LoadPredictor\" class=\"middleBar\" style=\"font-size:25px;padding-left:50px;padding-top:15px;font-style:italic;\">" 
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

function showModal()
{	
	$('body').append("<div class=\"modalWindow\"></div>");

	$('.modalWindow').css({
		'left' : 0,
		'top' : 0,
		'height' : $('.formscreen').height() + 200,
		'width' : 2 * $('body').width(),
		'position' : 'absolute',
		'z-index' : '1500',
		'opacity' : '0.5',
		background : 'grey'
	});
}
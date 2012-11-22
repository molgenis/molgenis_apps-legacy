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

		$('#progressBarMessage').text(" " + (percentage * 100).toFixed(2) + "%");

		$('#progressBar').progressbar({value: percentage * 100});

		if(status["finishedJobs"] == status["totalJobs"])
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

		$('#browser').empty();

		$('#existingMappings').empty();

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
				retrieveExpandedQueries(predictor, identifier.replace("_details", ""), url);
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
				}
			);

			$(this).children('span').click(function()
			{
				measurementName = $(this).text();

				trackInTree(measurementName, url);
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
		
		initializeTree(url);

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

function validateStudy(name)
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

		$("form[name=\"" + name + "\"]").submit();
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

function insertNewRow(url)
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
			url :  url + "&__action=download_json_addPredictor&data=" + JSON.stringify(data),
			async: false,
		}).done(function(status){
			message["message"] = status["message"];
			message["success"] = status["success"];
			message["identifier"] = data["identifier"];
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
	description = data["description"];
	dataType = data["dataType"];
	categories = data["category"];
	unit = data["unit"];
	buildingBlocks = data["buildingBlocks"];
	identifier = data["identifier"];

	newRow =  "<tr id=\"" + identifier + "\" name=\"" + identifier + "\" style=\"width:100%;\">";
	newRow += "<td name=\"name\" class=\"ui-corner-all\"><span style=\"margin:10px;margin-right:2px;float:left;width:12%;\">" + name + "</span>";
	newRow += "<div id=\"" + identifier + "_remove\" style=\"cursor:pointer;height:16px;width:16px;float:right;margin:10px;margin-left:3px;\" "
	+ "class=\"ui-state-default ui-corner-all\" title=\"remove this predictor\">"
	+ "<span class=\"ui-icon ui-icon-circle-close\"></span>"
	+ "</div></td>";
	newRow += "<td name=\"description\" class=\"ui-corner-all\" style=\"width:20%;\">" + description + "</td>";
	newRow += "<td name=\"dataType\" class=\"ui-corner-all\" style=\"width:8%;\">" + dataType + "</td>";
	newRow += "<td name=\"unit\" class=\"ui-corner-all\" style=\"width:10%;\">" + unit + "</td>";

	addedCategory = "";

	if(categories != "" && categories != null)
	{
		blocks = categories.split(",");
		addedCategory = createMultipleSelect(blocks);
	}

	newRow += "<td name=\"category\" class=\"ui-corner-all\" style=\"width:20%;\">" + addedCategory + "</td>";

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

	newRow += "<td name=\"buildingBlocks\" class=\"ui-corner-all\" style=\"width:40%\">" + selectBlocks + "</td>";
	newRow += "</tr>";

	$('#overviewTable').append(newRow);

	$('td[name="buildingBlocks"] >select').chosen();

	$('td[name="category"] >select').chosen();

	$('#' + identifier + '_remove').click(function(){
		removePredictor($(this), url);
	});

	$('#showPredictorPanel table tr:last-child').css({
		'vertical-align':'middle',
		'text-align':'center',
		'font-size':'16px',
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
	if($('#selectPredictionModel option').length > 0){
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
		if(success == true){
			summaryAddOne(predictor);
		}
	}
	showMessage(message, success);
}

function removePredictor(element, url)
{	
	predictor = $(element).parents('td').eq(0).text();
	selected = $('#selectPredictionModel').val();
	$.ajax({
		url : url + "&__action=download_json_removePredictors&name=" 
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

function showPredictors(predictionModelName, url)
{	
	$.ajax({
		url : url + "&__action=download_json_showPredictors&name=" + predictionModelName,
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
			populateRowInTable(Info, url);
		});

		$('#summaryPanel').fadeIn().draggable();
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
	selectBlocks += "</select>";

	return selectBlocks;
}

function summaryAddOne(identifier)
{	
	numberOfPredcitors = $('#numberOfPredictors').val();
	buildingBlocksDefine = $('#buildingBlocksDefined').val();
	$('#numberOfPredictors').val(++numberOfPredcitors);
	if($('#' + identifier + ' td:last-child >select').length > 0){
		++buildingBlocksDefine;
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
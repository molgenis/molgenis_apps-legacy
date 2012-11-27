<link type="text/css" href="jquery/css/smoothness/jquery-ui-1.8.7.custom.css" rel="Stylesheet"/>
<script src="jquery/development-bundle/ui/jquery-ui-1.8.7.custom.js" language="javascript"></script>
<script src="res/jquery-plugins/Treeview/jquery.treeview.js" language="javascript"></script>
<script src="res/scripts/bioshare_search.js" language="javascript"></script>
<script src="res/scripts/bioshare_harmonization.js" language="javascript"></script>
<script src="res/scripts/catalogue.js" language="javascript"></script>
<link rel="stylesheet" href="res/jquery-plugins/Treeview/jquery.treeview.css" type="text/css" media="screen" /> 
<link rel="stylesheet" href="res/css/catalogue.css" type="text/css" media="screen" />
<script>
	var URL = "${screen.getUrl()}";
	var FORM_NAME = "${screen.name}";
	var timer;
	
	$(document).ready(function()
	{	
		$('#progressBar').progressbar({value:0});
		
		$('#viewResult').button().click(function(){
			$('input[name="__action"]').val("retrieveResult");
			$("form[name=\"" + FORM_NAME + "\"]").submit();
		});
		
		$('#startNewSession').button().click(function(){
			$('input[name="__action"]').val("startNewSession");
			$("form[name=\"" + FORM_NAME + "\"]").submit();
		});
		
		timer = setInterval(function(){monitorJobs(URL)}, 15000);
	});
	
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
				<div style="width:900px;height:300px">
					<div id="summaryPanel" style="width:500px;height:300px;float:left;" class="ui-tabs-nav ui-corner-all ui-widget-content">
						<div class="ui-widget-header ui-corner-all" style="height:30px;">
							<div style="margin:3px;float:left;">Summary of the job: <span id="jobTitle" style="float:right"></span></div>
						</div>
						<div id="summaryOfJobs" style="margin:10px;width:90%;">
							<div>Processed time: <span id="processedTime" style="float:right;">
								calculating...</span></div>
							<div>Estimated time: <span id="estimatedTime" style="float:right;">
								calculating...</span></div>
						</div>
						<hr>
						<div id="information" style="margin:10px;width:90%;">
							<div>Prediction model: <span id="predictionModel" style="float:right;">
								${screen.getSelectedPredictionModel()}</span></div>
							<div>Validation study: <span id="validationStudy" style="float:right;">
								${screen.getSelectedValidationStudy()}</span></div>
						</div>
						<hr>
						<div style="margin-left:10px;margin-top:20px;font-style:italic;">Finished:<span id="progressBarMessage"></span></div>
						<div id="progressBar" style="width:95%;margin-left:10px;margin-top:10px;"></div>
					</div>
					<div id="resultPanel" style="width:360px;height:300px;float:left;display:none;" class="ui-tabs-nav ui-corner-all ui-widget-content">
						<div class="ui-widget-header ui-corner-all" style="height:30px;">
							<div style="margin:3px;float:left;">Next action</div>
						</div>
						<div id="messageForAction">
							<p align="justify" style="font-size:15px;padding:10px;height:170px">
								The matching has been done, please click the <b>view result</b> button to retrieve the result.
								Otherwise please start a new matching job by clicking the <b>start a new mapping</b> button. 
							</p>
						</div>
						<div style="margin:10px;">
							<input type="button" id="viewResult" style="font-size:12px;" value="view result">
							<input type="button" id="startNewSession" style="font-size:12px;" value="start new mapping">
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</form>
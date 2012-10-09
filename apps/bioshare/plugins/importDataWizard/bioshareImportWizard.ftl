<#macro plugins_importDataWizard_bioshareImportWizard screen>
	<script type="text/javascript">
		
		$(document).ready(function(){
			
			//Initialize the buttonset in the accordion
			$('#menu').buttonset();
			
			//Initialize accordion
			var icons = {
	            header: "ui-icon-circle-arrow-e",
	            activeHeader: "ui-icon-circle-arrow-s"
	        };
	        $('#accordion').accordion({
	            icons : icons,
	            heightStyle : 'content'
	        });
	        
	        //Initialize the the study that is used to show the information
	        $('#panelHeader').append($('input[name="summary"]').filter(':checked').next().text().toLowerCase());
	       	
	       	//Bind click event to the advance button which allows us to choose different
	       	//prediction models or validation studies to view
	        $('#advancedOption').click(function(){
	        	
	        	//The hidden accordion tends to be quite small, therefore
	        	//check the height first, if the height is too small, change it
	        	if($('#accordion div').height(100) < 100){
	        		$('#accordion div').height(100);
	        	}
	        	if($('#accordion').is(':visible')){
	        		$('#advancedOption span').addClass('ui-icon-gear');
	        		$('#advancedOption span').removeClass('ui-icon-close');
	        		$('#accordion').fadeOut();
	        	}else{
	        		$('#advancedOption span').removeClass('ui-icon-gear');
	        		$('#advancedOption span').addClass('ui-icon-close');
	        		$('#accordion').fadeIn();
	        	}
	        });
	        
	        $('input[name="summary"]').click(function(){
	        	name = $('input[name="summary"]').filter(':checked').next().text().toLowerCase();
	        	$('#panelHeader').empty().append('Study summary: ' + name);
	        });
		});
		
	</script>
	<form method="post" enctype="multipart/form-data" name="${screen.name}" action="">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}">
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action">
	
	<div class="formscreen">
		<div class="form_header" id="${screen.getName()}">
			${screen.label}
		</div>
		<div id="summaryPanel" style="height:100%;width:100%">
			<div style="height:30px;vertical-align:middle;text-align:center" class="ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all">
				<span id="panelHeader" style="font-size:20px;color:grey">Study summary: </span>
			</div>
			<div style="height:470px" class="ui-tabs-panel ui-widget-content ui-corner-bottom">
				<div id="advancedOption" style="cursor:pointer;height:18px;width:18px" class="ui-state-default ui-corner-all" title="view other studies">
					<span class="ui-icon ui-icon-gear"></span>
				</div>
				<div id="accordion" style="display:none;position:absolute;float:left;width:200px;">
				    <h3 style="font-size:16px">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Prediction Model</h3>
				    <div id="selectMenu">
				        <span id="menu" style="position:absolute;left:0px;">
							<input type="radio" id="predictionModel" name="summary" checked="checked"/>
							<label style="font-size:12px;width:195px" for="predictionModel">Prediction model</label></br>
							<input type="radio" id="validationStudy" name="summary">
							<label style="font-size:12px;width:195px" for="validationStudy">Validation study</label>
						</span>
				    </div>
				    <h3 style="font-size:16px">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Validation study</h3>
				    <div>
				    	Hello world!
				    </div>
				    <h3 style="font-size:16px">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Study comparison</h3>
				    <div>
				    	Hello world!
				    </div>
				</div>
				
				<div id="studyDescription" style="height:100%">
					
					<div style="height:15%">
						<img src="res/img/BioshareHeader.png" style="position:relative;top:-13px;left:70%;height:80px;width:240px">
					</div>
					<div style="height:80%;width:100%;margin-left:auto;margin-right:auto" class="ui-tabs-panel ui-widget-content ui-corner-bottom">
						<div id="studyName" style="width:100%;height:40%;font-size:14px;">
							<div style="height:20%;font-size:24px;color:grey" class="ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all">
								Study name: <span id="studyName" name="studyName">Prevend</span>
							</div>
							<div id="studyDescription" name="studyDescription" align="justify" style="height:75%;overflow:auto">
								The PREVEND Study is a prospective, observational cohort study, 
								focussed to assess the impact of elevated urinary albumin loss in non-diabetic 
								subjects on future cardiovascular and renal disease. PREVEND is an acronym for Prevention 
								of REnal and Vascular ENd-stage Disease. This study started with a population survey on the 
								prevalence of micro-albuminuria and generation of a study cohort of the general population. 
								The goal is to monitor this cohort for the long-term development of cardiac-, renal- and 
								peripheral vascular end-stage disease. For that purpose the participants receive questionnaires 
								on events and are seen every three/four years for a survey on cardiac-, renal- and peripheral 
								vascular morbidity.
							</div>
						</div>
						<div id="generalInformation" style="float:left;width:40%;height:60%;font-size:14px;" class="ui-widget-content ui-corner-bottom">
							<div style="text-align:center" class="ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all">
								General information
							</div>
							Launched year: <span id="launchYear" name="launchYear">1990</span></br>
							Country of study: <span id="countryStudy" name="countryStudy">Netherlands</span></br>
							Study description: <span id="launchYear" name="launchYear"></span></br>
						</div>
						<div id="individualInformation" style="float:left;width:59.5%;height:60%;font-size:14px;" class="ui-widget-content ui-corner-bottom">
							<div style="text-align:center" class="ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all">
								Individual information
							</div>
							Number of participants
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</#macro>

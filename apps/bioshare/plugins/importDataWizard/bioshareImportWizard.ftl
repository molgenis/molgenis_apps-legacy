<#macro plugins_importDataWizard_bioshareImportWizard screen>
	
	<style>
		input.text,textarea { 
			width:95%; 
			padding: .4em; 
			margin-bottom:12px; 
			display:block;
		}
		legend {
			font-size:20px;
		}
		fieldset {
			margin-bottom:12px;
		}
	</style>
	<script type="text/javascript">
		
		var previousInformation = {};
		
		$(document).ready(function(){
			
			//Check if there are any studies or prediction models loaded
			if($('#menuForCorhortStudies div:first-child').children().length == 0){
				insertHTML = "<div style=\"font-size:12px;\">"
							+ "There is no cohort study in the database, an example PREVEND study has been shown. "
							+ "Do you want to load this example?</div>"
							+ "<div id=\"loadExample\" style=\"cursor:pointer;height:15px;width:15px;position:relative;top:-15px;left:65%\" "
							+ "class=\"ui-state-default ui-corner-all\">"
							+ "<span class=\"ui-icon ui-icon-check\"></span></div>"; 
				$('#statusMessage span').append(insertHTML);
				$('#statusMessage').effect('bounce');
				$('#loadExample').click(function(){
					array = {};
					$('#descriptionForStudy').find('span').each(function(){
						if($(this).attr('id')){
							array[$(this).attr('id') + "Input"] =$(this).text();
						} 
					});
					addNewStudyFunction(array);
				});
			}else{
				$('#menuForCorhortStudies input:first-child').attr('checked', true);
				$('#menuForCorhortStudies div:first-child').buttonset();
				
				refreshPanel($('#menuForCorhortStudies input:first-child'));
				
				//Bind refreshPanel function to each button
				$('#menuForCorhortStudies input[type="radio"]').click(function(){
					refreshPanel($(this));
				});
				
				//Detect any changes being edited by users
		        $('#descriptionForStudy input[type="text"]').change(function(){
		        	detectChanges();
		        });
		        $('#descriptionForStudy textarea').change(function(){
		        	detectChanges();
		        });
			}
			
			//Initialize accordion
			var icons = {
	            header: "ui-icon-circle-arrow-e",
	            activeHeader: "ui-icon-circle-arrow-s"
	        };
	        $('#accordion').accordion({
	            icons : icons,
	            heightStyle : 'content'
	        });
	        
	        //Initialize the addStudyDialog
	        $('#addStudyForm').dialog({
	        	autoOpen : false,
	        	modal : true,
	        	title : "New study",
	        	height : 400,
	        	width : 700,
	        	buttons : {
	        		"add" : function(){
	        			listOfInfo = collectStudyInfo($('#addStudyForm'));
	        			addNewStudyFunction(listOfInfo);
	        		},
	        		"cancel" : function(){
	        			$(this).dialog('close');
	        		}
	        	}
	        });
	        
	        //Initialize the study that is used to show the information
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
	        		$('#advancedOption span').eq(0).addClass('ui-icon-gear');
	        		$('#advancedOption span').eq(1).addClass('ui-icon-triangle-1-s');
	        		$('#advancedOption span').removeClass('ui-icon-close');
	        		$('#accordion').fadeOut();
	        	}else{
	        		$('#advancedOption span').eq(0).removeClass('ui-icon-gear');
	        		$('#advancedOption span').eq(1).removeClass('ui-icon-triangle-1-s');
	        		$('#advancedOption span').addClass('ui-icon-close');
	        		$('#accordion').fadeIn();
	        	}
	        });
	        
	        //Update the topic, validation study or prediction model, if changed
	        $('input[name="summary"]').click(function(){
	        	name = $('input[name="summary"]').filter(':checked').next().text().toLowerCase();
	        	$('#panelHeader').empty().append('Study summary: ' + name);
	        });
	        
	        //hover over the header, the edit button shows up
	        $('div.editable div:first-child').hover(
				function () {
					$(this).find('div[name="editStudy"]').show();
				}, 
				function () {
					$(this).find('div[name="editStudy"]').hide();
				}
			);
			
			//Click on the edit button, the input text should be edited
			$('div[name="editStudy"]').click(function(){
				//Edit the header
				$(this).parent().find('div').eq(0).children('span').hide();
				value = $(this).parent().find('div').eq(0).children('span').text();
				$(this).parent().find('div').eq(0).children('input').val(value).show().focus();
				$(this).siblings('[name="saveStudy"]').show();
				
				//Edit the content of the table
				$(this).parent().parent().children('div:gt(0)').each(function(){
					
					$(this).attr('align','baseline');
					
					//get the width of parent div
					width = $(this).width() * 0.98;
					
					text = $(this).children('span').hide();
					
					$(this).children('textarea').val(text.text().replace(/\t|\n/g,""));
					$(this).children('textarea').css({
						'width' : width
					}).show();
					
				});
				//Save the changes
				$(this).siblings('[name="saveStudy"]').click(function(){
					//Save changes for header
					$(this).hide();
					if($(this).parent().children('div:first-child').children('span').length > 0){
						value = $(this).parent().children('div').eq(0).children('input').val();
						$(this).parent().find('div').eq(0).children('span').text(value).show();
						$(this).parent().find('div').eq(0).children('input').hide();
					}
					//Save changes for content
					$(this).parent().parent().children('div:gt(0)').each(function(){
						$(this).attr('align','justify');
						text = $(this).children('textarea').hide().val();
						$(this).children('span').empty();
						$(this).children('span').text(text).show();
					});
				});
			});
			
			//Click add new study
			$('#addNewStudy').click(function(){
				$('#addStudyForm').dialog('open');			
			});
		});
		function addNewStudyFunction(listOfInfo){
			
			if(listOfInfo["studyNameInput"] != ""){
				$.ajax("${screen.getUrl()}&__action=download_json_addNewStudy&studyInfo="
					+ JSON.stringify(listOfInfo)).done(function(status){
					
					message = status['message'];
					
					$('#addStudyForm').dialog('close');
					$('#addStudyForm input[type="text"]').val('');
					
					if(status['status'] == "true"){
						
						//Success message in green
						info = "<span style=\"font-size:12px;color:green\">" + message + "</span>";	
						
						//Add the study to the dropdown menu!
						studyName = listOfInfo["studyNameInput"];
						newStudyButton = "<input type=\"radio\" id=\"" + studyName + "\" name=\"radio\"/>" + 
							"<label style=\"font-size:12px;width:195px\" for=\"" + studyName + "\">" + studyName + "</label></br>"
						$('#menuForCorhortStudies div:first-child').append(newStudyButton);
						$('#menuForCorhortStudies div:first-child').buttonset();
						
						$('#menuForCorhortStudies input[type="radio"]').click(function(){
							refreshPanel($(this));
						});
						
						//If size is equal to 1, that means there were no studies before this 
						//new study was added. Replace the example with the newly added study
						if($('#menuForCorhortStudies input[type="radio"]').size() == 1){
							refreshPanel($('#' + studyName));
						}
						//Detect any changes being edited by users
				        $('#descriptionForStudy input[type="text"]').change(function(){
				        	detectChanges();
				        });
				        $('#descriptionForStudy textarea').change(function(){
				        	detectChanges();
				        });
						
					}else{
						//failure message in red
						info = "<span style=\"font-size:12px;color:red\">" + message + "</span>";
					}
					$('#statusMessage span').empty().append(info);
					$('#statusMessage').effect('bounce').delay(2000).fadeOut();
				});
			}else{
				alert("the study name cannot be null!");
			}
		}
		function refreshPanel(clickedStudy){
			$.ajax("${screen.getUrl()}&__action=download_json_refreshStudy&studyName=" 
				+ $(clickedStudy).attr('id')).done(function(studyInfo){
				$('#descriptionForStudy >div').eq(1).find('span').text('');
				$.each(studyInfo, function(key, val) {
					$('#' + key).text(val);
				});
				$('#studyName').text($(clickedStudy).attr('id'));
			});
		}
		
		function collectStudyInfo(addStudyForm){
			array = {};
			$(addStudyForm).find('input').each(function(){
				array[$(this).attr('id')] =$(this).val(); 
			});
			return array;
		}
		
		function detectChanges(){
			hintMessage =  "<div style=\"font-size:12px;float:left;padding-right:3px\">There are changes! Do you want to update?</div>";
        	hintMessage += "<div id=\"updateChanges\" style=\"cursor:pointer;height:15px;width:15px;float:left\" class=\"ui-state-default ui-corner-all\">"
						+  "<span class=\"ui-icon ui-icon-check\"></span></div>"
						+  "<div style=\"font-size:12px;float:left;padding:3px\">Do you want to abandan the changes?</div>" 
						+  "<div id=\"abandonChanges\" style=\"cursor:pointer;height:15px;width:15px;float:left\" class=\"ui-state-default ui-corner-all\">"
						+  "<span class=\"ui-icon ui-icon-trash\"></span></div>"; 
						
        	$('#statusMessage span').empty().append(hintMessage);
			$('#statusMessage').effect('bounce');
		}
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
				<div id="controlButtons">
					<div id="advancedOption" style="cursor:pointer;height:18px;width:38px;float:left" class="ui-state-default ui-corner-all" title="view other studies">
						<span style="float:left" class="ui-icon ui-icon-gear"></span>
						<span style="float:left" class="ui-icon ui-icon-triangle-1-s"></span>
					</div>
					<div id="addNewStudy" style="cursor:pointer;height:18px;width:18px;float:left" class="ui-state-default ui-corner-all" title="add a new study">
						<span class="ui-icon ui-icon-plus"></span>
					</div>
					<!--
					<div id="editStudyAll" style="cursor:pointer;height:18px;width:18px;float:left" class="ui-state-default ui-corner-all" title="edit the study">
						<span class="ui-icon ui-icon-pencil"></span>
					</div>
					-->
				</div>
				</br>
				<div id="accordion" style="display:none;position:absolute;width:200px;">
				    <h3 style="font-size:16px">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Validation study</h3>
				    <div id="menuForCorhortStudies">
				        <span style="position:absolute;left:0px;">
							<div>
								<#if screen.getlistOfCohortStudies()??>
									<#list screen.getlistOfCohortStudies() as study>
										<input type="radio" id="${study}" name="radio"/>
										<label style="font-size:12px;width:195px" for="${study}">${study}</label></br>
									</#list>
								</#if>
							</div>
							<!--
							<input type="radio" id="predictionModel" name="summary" checked="checked"/>
							<label style="font-size:12px;width:195px" for="predictionModel">Prediction model</label></br>
							<input type="radio" id="validationStudy" name="summary">
							<label style="font-size:12px;width:195px" for="validationStudy">Validation study</label>
							-->
						</span>
				    </div>
				    <h3 style="font-size:16px">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Prediction Model</h3>
				    <div id="menuForPredictionModels">
				    	<span style="position:absolute;left:0px;">
				    	</span>
				    </div>
				    <h3 style="font-size:16px">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Study comparison</h3>
				    <div id="menuForStudyComparison">
				    	Hello world!
				    </div>
				</div>
				<div id="addStudyForm" style="display:none">
					<fieldset>
						<legend class="ui-widget-content ui-corner-all">Study information</legend>
				        <label for="studyNameInput">Study name</label>
				        <input type="text" name="studyNameInput" id="studyNameInput" class="text ui-widget-content ui-corner-all" />
				        <label for="studyDescriptionInput">Study description</label>
				        <textarea style="height:100px" name="studyDescriptionInput" id="studyDescriptionInput" class="text ui-widget-content ui-corner-all"></textarea>
				    </fieldset>
				    <fieldset>
						<legend class="ui-widget-content ui-corner-all">General information</legend>
				        <label for="launchYearInput">Launched year</label>
				        <input type="text" name="launchYearInput" id="launchYearInput" class="text ui-widget-content ui-corner-all" />
				        <label for="countryOfStudyInput">Country of study</label>
				        <input type="text" name="countryOfStudyInput" id="countryOfStudyInput" class="text ui-widget-content ui-corner-all" />
				        <label for="studyDesginInput">Study design</label>
				        <textarea name="studyDesginInput" id="studyDesginInput" class="text ui-widget-content ui-corner-all" ></textarea>
				    </fieldset>
				    <fieldset>
						<legend class="ui-widget-content ui-corner-all">Individual information</legend>
				        <label for="numberOfParticipantsInput">Number of participants</label>
				        <input type="text" name="numberOfParticipantsInput" id="numberOfParticipantsInput" class="text ui-widget-content ui-corner-all" />
				        <label for="ageGroupInput">Age group</label>
				        <input type="text" name="ageGroupInput" id="ageGroupInput" class="text ui-widget-content ui-corner-all" />
				        <label for="ethnicGroupInput">Ethnic group</label>
				        <input type="text" name="ethnicGroupInput" id="ethnicGroupInput" class="text ui-widget-content ui-corner-all" />
				    </fieldset>
				</div>
				<div id="descriptionForStudy" style="height:100%">
					<div style="height:15%;">
						<div style="height:60%;">
							<fieldset id="statusMessage"style="width:400px;display:none" class="ui-tabs-panel ui-widget-content ui-corner-bottom">
								<legend style="font-style:italic;">Message</legend>
								<span></span>
							</fieldset>
						</div>
						<img src="res/img/BioshareHeader.png" style="position:relative;top:-60px;left:70%;height:80px;width:240px">
					</div>
					<div style="height:80%;width:100%;margin-left:auto;margin-right:auto" class="ui-tabs-panel ui-widget-content ui-corner-bottom">
						<div id="studyNameInfo" style="width:100%;height:40%;font-size:14px;" class="editable">
							<div style="height:20%;font-size:24px;color:grey;font-style:italic" class="ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all">
								<div style="float:left">Study name: 
									<span id="studyName" name="studyName">Prevend</span>
									<input type="text" style="display:none" size="12" />
								</div>
								<div name="saveStudy" style="display:none;cursor:pointer;height:18px;width:18px;float:left" class="ui-state-default ui-corner-all" title="save changes">
									<span class="ui-icon ui-icon-disk"></span>
								</div>
								<div name="editStudy" style="display:none;cursor:pointer;height:18px;width:18px;float:left" class="ui-state-default ui-corner-all" title="edit the study">
									<span class="ui-icon ui-icon-pencil"></span>
								</div>
							</div>
							<div align="justify" style="float:left;padding:4px;height:75%;width:100%;overflow:auto">
								<span id="studyDescription" name="studyDescription" >
									The PREVEND Study is a prospective, observational cohort study, focussed to assess the impact of elevated urinary albumin loss in non-diabetic 
									subjects on future cardiovascular and renal disease. PREVEND is an acronym for Prevention 
									of REnal and Vascular ENd-stage Disease. This study started with a population survey on the 
									prevalence of micro-albuminuria and generation of a study cohort of the general population. 
									The goal is to monitor this cohort for the long-term development of cardiac-, renal- and 
									peripheral vascular end-stage disease. For that purpose the participants receive questionnaires 
									on events and are seen every three/four years for a survey on cardiac-, renal- and peripheral 
									vascular morbidity.
								</span>
								<textarea style="height:300px;display:none"></textarea>
							</div>
						</div>
						<div id="generalInformation" style="float:left;width:40%;height:58%;font-size:14px;overflow:auto" class="editable ui-widget-content ui-corner-bottom">
							<div style="text-align:center;height:20px" class="ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all">
								<div style="float:left;">General information</div>
								<div name="saveStudy" style="display:none;cursor:pointer;height:18px;width:18px;float:left" class="ui-state-default ui-corner-all" title="save changes">
									<span class="ui-icon ui-icon-disk"></span>
								</div>
								<div name="editStudy" style="display:none;cursor:pointer;height:18px;width:18px;float:left" class="ui-state-default ui-corner-all" title="edit the study">
									<span class="ui-icon ui-icon-pencil"></span>
								</div>
							</div>
							<div align="justify" style="padding:4px">Launched year: <span id="launchYear" name="launchYear">1990</span><textarea style="height:12px;display:none"></textarea></div>
							<div align="justify" style="padding:4px">Country of study: <span id="countryOfStudy" name="countryStudy">Netherlands</span><textarea style="height:12px;display:none"></textarea></div>
							<div align="justify" style="padding:4px;height:55%;overflow:auto">Study description: 
								<span id="studyDesign" name="studyDesign">
									Of the 85,421 subjects invited 40,856 responded (=48%) . We invited all 
									consenting subjects with a morning urinary albumin concentration (UAC) 
									of >10 mg/L (n=7,768) and an a-select sample of those with an UAC <10 mg/L 
									(n=3,395) for further studies . Of these invitees 8,592 subjects completed 
									the first screening, that was held in 1997/1998
								</span>
								<textarea style="height:150px;display:none"></textarea>
							</div>
						</div>
						<div id="individualInformation" style="float:left;width:59.5%;height:58%;font-size:14px;overflow:auto" class="editable ui-widget-content ui-corner-bottom">
							<div style="text-align:center;height:20px" class="ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all">
								<div style="float:left;">Individual information</div>
								<div name="saveStudy" style="display:none;cursor:pointer;height:18px;width:18px;float:left" class="ui-state-default ui-corner-all" title="save changes">
									<span class="ui-icon ui-icon-disk"></span>
								</div>
								<div name="editStudy" style="display:none;cursor:pointer;height:18px;width:18px;float:left" class="ui-state-default ui-corner-all" title="edit the study">
									<span class="ui-icon ui-icon-pencil"></span>
								</div>
							</div>
							<div style="padding:4px">Number of participants: <span id="numberOfParticipants" name="numberOfParticipants">8592</span><textarea style="height:12px;display:none"></textarea></div>
							<div style="padding:4px">Age: <span id="ageGroup" name="ageGroup">15 to 75</span><textarea style="height:12px;display:none"></textarea></div>
							<div style="padding:4px">Ethnic group: <span id="ethnicGroup" name="ethnicGroup">Caussian</span><textarea style="height:12px;display:none"></textarea></div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</#macro>

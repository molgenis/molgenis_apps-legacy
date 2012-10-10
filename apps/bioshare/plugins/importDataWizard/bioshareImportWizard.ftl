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
					width = $(this).children('span').width();
					if(width < 100){
						width = 100;
					}
					height = $(this).children('span').height();
					text = $(this).children('span').hide();
					$(this).children('textarea').val(text.text().replace(/\t|\n/g,""));
					$(this).children('textarea').css({
						'height' : height,
						'width' : width
					}).show();
					
				});
				//width = $(this).parent().next().children('span').width();
				//height = $(this).parent().next().children('span').height();
				//text = $(this).parent().next().children('span').hide();
				//$(this).parent().next().children('textarea').val(text.text().replace(/\t|\n/g,""));
				//$(this).parent().next().children('textarea').css({
				//	'height' : height,
				//	'width' : width
				//}).show();
				
				//Save the changes
				$(this).siblings('[name="saveStudy"]').click(function(){
					//Save changes for header
					$(this).hide();
					if($(this).parent().children('div').lengt > 0){
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
				<div id="controlButtons">
					<div id="advancedOption" style="cursor:pointer;height:18px;width:18px;float:left" class="ui-state-default ui-corner-all" title="view other studies">
						<span class="ui-icon ui-icon-gear"></span>
					</div>
					<div id="addNewStudy" style="cursor:pointer;height:18px;width:18px;float:left" class="ui-state-default ui-corner-all" title="add a new study">
						<span class="ui-icon ui-icon-plus"></span>
					</div>
					<div id="editStudyAll" style="cursor:pointer;height:18px;width:18px;float:left" class="ui-state-default ui-corner-all" title="edit the study">
						<span class="ui-icon ui-icon-pencil"></span>
					</div>
				</div>
				</br>
				<div id="accordion" style="display:none;position:absolute;width:200px;">
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
						<img src="res/img/BioshareHeader.png" style="position:relative;top:-16px;left:70%;height:80px;width:240px">
					</div>
					<div style="height:80%;width:100%;margin-left:auto;margin-right:auto" class="ui-tabs-panel ui-widget-content ui-corner-bottom">
						<div id="studyName" style="width:100%;height:40%;font-size:14px;" class="editable">
							<div style="height:20%;font-size:24px;color:grey" class="ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all">
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
							<div id="studyDescription" name="studyDescription" align="justify" style="float:left;padding:4px;height:75%;overflow:auto">
								<span>
									The PREVEND Study is a prospective, observational cohort study, focussed to assess the impact of elevated urinary albumin loss in non-diabetic 
									subjects on future cardiovascular and renal disease. PREVEND is an acronym for Prevention 
									of REnal and Vascular ENd-stage Disease. This study started with a population survey on the 
									prevalence of micro-albuminuria and generation of a study cohort of the general population. 
									The goal is to monitor this cohort for the long-term development of cardiac-, renal- and 
									peripheral vascular end-stage disease. For that purpose the participants receive questionnaires 
									on events and are seen every three/four years for a survey on cardiac-, renal- and peripheral 
									vascular morbidity.
								</span>
								<textarea style="display:none"></textarea>
							</div>
						</div>
						<div id="generalInformation" style="float:left;width:40%;height:58%;font-size:14px;" class="editable ui-widget-content ui-corner-bottom">
							<div style="text-align:center;height:20px" class="ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all">
								<div style="float:left;">General information</div>
								<div name="saveStudy" style="display:none;cursor:pointer;height:18px;width:18px;float:left" class="ui-state-default ui-corner-all" title="save changes">
									<span class="ui-icon ui-icon-disk"></span>
								</div>
								<div name="editStudy" style="display:none;cursor:pointer;height:18px;width:18px;float:left" class="ui-state-default ui-corner-all" title="edit the study">
									<span class="ui-icon ui-icon-pencil"></span>
								</div>
							</div>
							<div align="justify" style="padding:4px">Launched year: <span id="launchYear" name="launchYear">1990</span><textarea style="display:none"></textarea></div>
							<div align="justify" style="padding:4px">Country of study: <span id="countryStudy" name="countryStudy">Netherlands</span><textarea style="display:none"></textarea></div>
							<div align="justify" style="padding:4px;height:55%;overflow:auto">Study description: 
								<span id="description" name="description">
									Of the 85,421 subjects invited 40,856 responded (=48%) . We invited all 
									consenting subjects with a morning urinary albumin concentration (UAC) 
									of >10 mg/L (n=7,768) and an a-select sample of those with an UAC <10 mg/L 
									(n=3,395) for further studies . Of these invitees 8,592 subjects completed 
									the first screening, that was held in 1997/1998
								</span>
								<textarea style="display:none"></textarea>
							</div>
						</div>
						<div id="individualInformation" style="float:left;width:59.5%;height:58%;font-size:14px;" class="editable ui-widget-content ui-corner-bottom">
							<div style="text-align:center;height:20px" class="ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all">
								<div style="float:left;">Individual information</div>
								<div name="saveStudy" style="display:none;cursor:pointer;height:18px;width:18px;float:left" class="ui-state-default ui-corner-all" title="save changes">
									<span class="ui-icon ui-icon-disk"></span>
								</div>
								<div name="editStudy" style="display:none;cursor:pointer;height:18px;width:18px;float:left" class="ui-state-default ui-corner-all" title="edit the study">
									<span class="ui-icon ui-icon-pencil"></span>
								</div>
							</div>
							<div style="padding:4px">Number of participants: <span id="numberOfParticipants" name="numberOfParticipants">8592</span><textarea style="display:none"></textarea></div>
							<div style="padding:4px">Age: <span id="ageGround" name="ageGround">15 to 75</span><textarea style="display:none"></textarea></div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</#macro>

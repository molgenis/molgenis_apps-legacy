<#macro plugins_importDataWizard_bioshareImportWizard screen>
	<script type="text/javascript">
		
		$(document).ready(function(){
			
			$('#menu').buttonset();
			
			var icons = {
	            header: "ui-icon-circle-arrow-e",
	            activeHeader: "ui-icon-circle-arrow-s"
	        };
	        $('#accordion').accordion({
	            icons : icons,
	            heightStyle : 'content'
	        });
	        
	        //Initialize the the study that is used to show the information
	        $('#studySummary').append($('input[name="summary"]').filter(':checked').next().text().toLowerCase());
	       	
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
	        	$('#studySummary').empty().append('Study summary: ' + name);
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
		<div id="showSummary" style="height:100%;width:100%">
			<div style="height:30px;vertical-align:middle;text-align:center" class="ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all">
				<span id="studySummary" style="font-size:20px;color:grey">Study summary: </span>
			</div>
			<div style="height:470px" class="ui-tabs-panel ui-widget-content ui-corner-bottom">
				<div id="advancedOption" style="cursor:pointer;height:18px;width:18px" class="ui-state-default ui-corner-all" title="view other studies">
					<span class="ui-icon ui-icon-gear"></span>
				</div>
				
				<div id="accordion" style="display:none;float:left;width:200px;">
				    <h3 style="font-size:16px">&nbsp;&nbsp;&nbsp;&nbsp;Prediction Model</h3>
				    <div id="selectMenu">
				        <span id="menu" style="position:absolute;left:0px;">
							<input type="radio" id="predictionModel" name="summary" checked="checked"/>
							<label style="font-size:12px;width:195px" for="predictionModel">Prediction model</label></br>
							<input type="radio" id="validationStudy" name="summary">
							<label style="font-size:12px;width:195px" for="validationStudy">Validation study</label>
						</span>
				    </div>
				    <h3 style="font-size:16px">&nbsp;&nbsp;&nbsp;&nbsp;Validation study</h3>
				    <div>
				    	Hello world!
				    </div>
				</div>
			</div>
		</div>
	</div>
</#macro>

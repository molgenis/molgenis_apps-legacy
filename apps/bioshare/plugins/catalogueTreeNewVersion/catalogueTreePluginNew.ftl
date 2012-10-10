<#macro plugins_catalogueTreeNewVersion_catalogueTreePluginNew screen>

<!-- normally you make one big form for the whole plugin-->
<form method="post" enctype="multipart/form-data" name="${screen.name}" action="">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}">
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action" value="">
	
	<script src="res/jquery-plugins/Treeview/jquery.treeview.js" language="javascript"></script>
	<script src="res/scripts/catalogue.js" language="javascript"></script>
	<link rel="stylesheet" href="res/jquery-plugins/Treeview/jquery.treeview.css" type="text/css" media="screen" /> 
	<link rel="stylesheet" href="res/css/catalogue.css" type="text/css" media="screen" />
	<link type="text/css" href="jquery/css/smoothness/jquery-ui-1.8.7.custom.css" rel="Stylesheet"/>
	<script src="jquery/development-bundle/ui/jquery-ui-1.8.7.custom.js" language="javascript"></script>
	
	<script type="text/javascript">
		
		var CLASSES = $.treeview.classes;
		var settings = {};
		var searchNode = new Array();
		
		function toggler() {
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
		
		function toggleNodeEvent(clickedNode){
			
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
		
		function searchTree(){
			
			showModal();
				
			$('body').append("<div id=\"progressbar\" style=\"z-index:1501;height:50px;width:300px;position:absolute;left:46%;top:80%\">Searching..</div>");
			
			token = $('#searchField').val();
			
			if(token != ""){
				array = {};
				
				$.ajax({
					url:"${screen.getUrl()}&__action=download_json_search&searchToken=" + token,
					async: false,
				}).done(function(result){
					array = result["result"];
				});
				
				$('#browser').empty();
				
				$('#browser').append(array);
				
				var branches = $('#browser li').prepareBranches(settings);
				
				branches.applyClasses(settings, toggler);
			}
			
			$('.modalWindow').remove();
			
			$('#progressbar').remove();
			
			$('#browser li').each(function(){
				toggleNodeEvent($(this));
			});
		}
		
		function loadTree(){
			status = {};
			$.ajax({
				url:"${screen.getUrl()}&__action=download_json_loadingTree",
				async: false,
			}).done(function(result){
				status = result;
			});
			$("#progressbar").progressbar({
				value: status["result"]/5 * 100
			});
			if(status["status"] == false){
				loadTree();
			}
		}
		
		function showModal(){
			
			$("body").append("<div class=\"modalWindow\"></div>");
			
			treePanel = $('table:first-child').children().children('tr').eq(1);
			
			position = $(treePanel).offset();
			
			$('.modalWindow').css({
				'left' : position.left,
				'top' : position.top,
				'height' : $(treePanel).height(),
				'width' : $(treePanel).width(),
			    'position' : 'absolute',
			    'z-index' : '1500',
			    'opacity' : '0.5'
			});
		}
		
		function highlightClick(clickedBranch){
			
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
		
		function initialize(){

			$('#predictionModels').chosen();
			$('#refreshModel').button();
			
			$('#refreshModel').click(function(){
				
				selectedModel = $('#predictionModels').val();
				array = {};
				$.ajax({
					url:"${screen.getUrl()}&__action=download_json_refreshModel&selectedModel="+selectedModel,
					async: false,
				}).done(function(result){
					array = result["result"];
				});
				$('#predictor').empty();
				for(var i = 0; i < array.length; i++){
					$('#predictor').append("<option>" + array[i] + "</option>");
				}
				$('#predictionModelPanel').hide();
				$('#displayPredictor').fadeIn();
				$('#predictor').chosen();
			});
			
			$('#addPredictor').button();
			$('#addPredictor').click(function(){
				
				if($('#mappingTable').length == 0){
					table = "<table id=\"mappingTable\" style=\"width:100%\" ><tr style=\"width:100%;\">"
					      + "<td class=\"box-body-label\" style=\"height:35px;text-align:center; background: #DCE3F7;border-radius:8px 5px 5px 8px \">variable name</td>"
					      + "<td class=\"box-body-label\" style=\"text-align:center; background: #DCE3F7;border-radius:5px 5px 5px 5px \">define variable</td>"
					      + "<td class=\"box-body-label\" style=\"text-align:center; background: #DCE3F7;border-radius:5px 8px 12px 5px \">mapping</td></tr>";
					table += "<tr><td style=\"text-align:center;\" id=\"" + $('#predictor').val().replace(" ", "_") + "\">" + $('#predictor').val() + "</td><td></td></tr>"
					table += "</table>";
					$('#displayPredictor').append(table);
				}else{
					if($('#' + $('#predictor').val().replace(" ", "_")).length == 0){
						newRow = "<tr><td style=\"text-align:center;\">" + $('#predictor').val() + "</td><td></td></tr>";
						$('#mappingTable').append(newRow);
					}
				}
			});
			
			$('#search').button();
			$('#search').click(function(){
				searchTree();
			});
			
			$('#clearButton').button();
			$('#clearButton').click(function(){
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
			
			$('#fullScreen').button();
			
			$('#fullScreen').click(function(){
			
				firstElement = $('#layoutTable >tbody >tr:first-child');
				
				tableHeight = $('#layoutTable').height();
				
				if($(firstElement).is(":visible")){
					$(firstElement).hide()
					$('#layoutTable >tbody >tr').eq(1).animate({'height' : tableHeight}, 1500);
					$('hr').hide();
					$('#treeView').height(tableHeight);
				}else{
					$('#layoutTable >tbody >tr').eq(1).animate({'height' : tableHeight/2}, 1500);
					$(firstElement).show();
					$('hr').show();
					$('#treeView').height(250);
				}
			});
			
			$("#browser").treeview(settings).css('font-size', 16).find('li').show();	
			
			$("#browser").find('li').each(function(){
				
				toggleNodeEvent($(this));
			});	
			
			showModal();
			
			$('body').append("<div id=\"progressbar\"></div>");
			
			$("#progressbar").progressbar({value: 0});
			
			treePanel = $('table:first-child').children().children('tr').eq(1);
			
			position = $(treePanel).offset();
			
			elementLeft = position.left + $(treePanel).width()/2 - 150;
			
			elementTop = position.top + $(treePanel).height()/2;
			
			$('#progressbar').css({
				'left' : elementLeft,
				'top' : elementTop,
				'height' : 50,
				'width' : 300,
			    'position' : 'absolute',
			    'z-index' : 1501,
			});
			
			loadTree();
			
			$('.modalWindow').remove();
			$('#progressbar').remove();
			$('#treePanel').show();
		}
		
		$(document).ready(function(){	
			initialize();
		});
		
	</script>
	
	<div class="formscreen">
		<input type="hidden" id="clickedVariable"/>
		<div class="form_header" id="${screen.getName()}">
			${screen.label}
		</div>
		
		<#--optional: mechanism to show messages-->
		<#list screen.getMessages() as message>
			<#if message.success>
		<p class="successmessage">${message.text}</p>
			<#else>
		<p class="errormessage">${message.text}</p>
			</#if>
		</#list>
		
		<div class="screenbody">
			
			<table id="layoutTable" style="height:700px;width:100%">
				<tr style="height:50%" >
					<td>
						<div id="predictionModelPanel">
							<p style="font-style:italic;font-size:24px;color:#123481">
								Please select a prediction model
							</p>
							<select id="predictionModels">
								<#list screen.getListOfProtocols() as model>
									<option>${model}</option>
								</#list>
							</select>
							<input type="button" id="refreshModel" style="vertical-align:top" value="refresh"/>
						</div>
						<div id="displayPredictor" style="display:none">
							<p style="font-style:italic;font-size:24px;color:#123481">
								Please define the predictors
							</p>
							<select id="predictor" style="display:none;vertical-align:middle">
							</select>
							<input type="button" id="addPredictor" style="vertical-align:top" value="add"/></br></br></br>
						</div>
					</td>
				</tr>
				<tr style="height:50%">
					<td>
						<hr style="width:100%;">
						<table style="width:100%">
							<tr>
								<td>
									<input type="text" id="searchField" />
									<input type="button" id="search" value="search"/>
									<input type="button" id="clearButton" value="clear"/>
									<input type="button" id="fullScreen" value="full screen"/>
									</br>
								</td>
							</tr>
							<tr>
								<td style="width:50%">
									<div id="treePanel">
										<div id="treeView" style="height:250px;overflow:auto">
											<#if screen.getTreeView??>
												<ul id="browser" class="pointtree"> 
													${screen.getTreeView()} 
												</ul>
											</#if>
										</div>
									</div>
								</td>
								<td style="width:50%">
									<div id="details">
						
									</div>
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</div>
	</div>
	
</form>
</#macro>
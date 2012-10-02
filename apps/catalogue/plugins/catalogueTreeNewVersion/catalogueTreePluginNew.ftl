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
			});
		}
		
		$(document).ready(function(){	
			
			$('#search').button();
			
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
				
				//$('#browser').append(array);
				
				//$("#browser").treeview(settings);
				
				$("#browser").find('li').each(function(){
					toggleNodeEvent($(this));
				});
				$('#searchField').val('');
			});
			
			$('#search').click(function(){
				
				showModal();
				
				$('body').append("<div id=\"progressbar\" style=\"z-index:1501;height:50px;width:300px;position:absolute;left:46%;top:60%\">Searching..</div>");
				
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
			});
			
			$("#browser").treeview(settings).find('li').show();	
			
			$("#browser").find('li').each(function(){
				toggleNodeEvent($(this));
			});	
			showModal();
			$('body').append("<div id=\"progressbar\" style=\"z-index:1501;height:50px;width:300px;position:absolute;left:36%;top:60%\"></div>");
			$("#progressbar").progressbar({value: 0});
			loadTree();
			$('.modalWindow').remove();
			$('#progressbar').remove();
			$('#treePanel').show();
		});
		
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
			$('.modalWindow').css({
				'left' : '0px',
				'top' : '0px',
				'width' : '100%',
			    'height' : '100%',
			    'position' : 'absolute',
			    'z-index' : '1500',
			    'background' : 'grey',
			    'opacity' : '0.5'
			});
		}
		
	</script>
	
	<div class="formscreen">
		
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
			<div id="treePanel" style="display:none">
				<input type="text" id="searchField" />
				<input type="button" id="search" value="search"/>
				<input type="button" id="clearButton" value="clear"/>
				</br>
				<#if screen.getTreeView??>
					<ul id="browser" class="pointtree"> 
						${screen.getTreeView()} 
					</ul>
				</#if>
			</div>
		</div>
	</div>
	
</form>
</#macro>

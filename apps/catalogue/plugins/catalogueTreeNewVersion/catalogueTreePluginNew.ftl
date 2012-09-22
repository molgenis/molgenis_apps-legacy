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
					
					//$('#' + nodeName).find('li').remove();
					
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
			
			$('#search').click(function(){
				$.ajax({
					url:"${screen.getUrl()}&__action=download_json_search&searchToken=height",
					async: false,
				}).done();
			});
			
			$("#browser").treeview(settings).find('li').show();	
			
			$("#browser").find('li').each(function(){
				toggleNodeEvent($(this));
			});	
			showModal();
			$.ajax({
				url:"${screen.getUrl()}&__action=download_json_loadingTree",
				async: false,
			}).done();
			$('.modalWindow').remove();
		});
		
		function showModal(){
		  $("body").append('<div class="modalWindow"/>');
		}
		
	</script>
	
	<style>
    	.modalWindow{
			left:0px;
			top:0px;
			width: 100%;
		    height: 100%;
		    position: absolute;
		    z-index: 1500;
		    background: grey;
		    opacity: 0.5;
		}
    </style>
	
	
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
			</br>
			<input type="button" id="search" value="search"/>
			</br>
			<#if screen.getTreeView??>
				<ul id="browser" class="pointtree"> 
					${screen.getTreeView()} 
				</ul>
			</#if>
		</div>
	</div>
	
</form>
</#macro>

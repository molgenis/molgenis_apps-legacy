function toggler() 
{
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

function searchTree(token, url)
{	
	createScreenMessage("Searching...","70%");
	
	if(token != "")
	{
		array = {};

		$.ajax({
			url: url + "&__action=download_json_search&searchToken=" + token,
			async: false,
		}).done(function(result){
			array = result["result"];
		});

		$('#browser').empty().append(array);

		$('#browser li').prepareBranches(settings).applyClasses(settings, toggler);
		
		$("#browser").find('li').each(function(){
			toggleNodeEvent($(this), url);
		});
	}
	
	destroyScreenMessage(); 
}

function loadTree(url)
{	
	status = {};

	$.ajax({
		url: url + "&__action=download_json_loadingTree",
		async: false,
	}).done(function(result){
		status = result;
	});

	updateProgressBar(status["result"]/5);

	if(status["status"] == false){
		loadTree(url);
	}
}

function highlightClick(clickedBranch)
{	
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

function initializeTree(url)
{	
	$('#search').button().click(function()
	{
		token = $('#searchField').val();

		searchTree(token, url);
	});

	$('#clearButton').button().click(function()
	{
		array = {};
		$.ajax({
			url: url + "&__action=download_json_clearSearch",
			async: false,
		}).done(function(result){
			array = result["result"];
		});

		$('#browser').empty().append(array).treeview(settings);

		$('#browser li').prepareBranches(settings).applyClasses(settings, toggler);
		
		$("#browser").find('li').each(function(){

			toggleNodeEvent($(this), url);
		});

		$('#searchField').val('');
	});

	$("#browser").treeview(settings).css('font-size', 16).find('li').show();	

	$("#browser").find('li').each(function()
	{
		toggleNodeEvent($(this), url);
	});	

	createProgressBar("Loading catalogue");

	loadTree(url);

	destroyProgressBar();

	$('#treePanel').show();
}

function checkSearchingStatus()
{	
	if($('#searchField').val() === "")
	{
		$('#clearButton').trigger('click');
	}
}

function whetherReload(url)
{	
	var value = $('#searchField').val();

	if(value.search(new RegExp("\\w", "gi")) != -1)
	{
		searchTree(value, url);
	}
	return false;
}
function toggleNodeEvent(clickedNode, url)
{	
	$(clickedNode).children('span').click(function(){

		var nodeName = $(this).parent().attr('id');

//		$.ajax({
//			url: url + "&__action=download_json_toggleNode&nodeIdentifier=" + nodeName,
//			async: false,
//		}).done();
		
		$.ajax({
			url:url + "&__action=download_json_getChildren&nodeIdentifier=" + nodeName,
			async: false,
		}).done(function(result)
		{
			var addedNodes = result["result"];

			$('#' + nodeName + ' >ul').empty().prepend(addedNodes);

			var prepareBranches = $.fn.prepareBranches;

			var applyClasses = $.fn.applyClasses;

			var branches = $('#' + nodeName).find('li').prepareBranches(settings);

			branches.applyClasses(settings, toggler);

			$('#' + nodeName).find('li').each(function()
			{
				toggleNodeEvent($(this), url);
			});
		});
		
		if($(this).parent().find('li').size() == 0){
			
			highlightClick($(this).parent());
			
			$('#details').empty();
			$.ajax({
				url: url + "&__action=download_json_showInformation&variableName=" + $(this).parent().attr('id'),
				async: false
			}).done(function(data) {
				$('#details').append(data["result"]);
			});
		}
	});
}

function trackInTree(measurementName, url)
{	
	$.ajax({
		url : url + "&__action=download_json_getPosition&measurementName=" + measurementName,
		async: false,
	}).done(function(status){

		identifier = status["identifier"];

		$('#browser').empty().append(status["treeView"]).treeview(settings);
		$("#browser").find('li').each(function(){
			toggleNodeEvent($(this), url);
		});

		$('#' + identifier).children('span').trigger('click');

		window.scrollTo(0, 500);

		var elementTop = $('#' + identifier).position().top;
		var treeDivTop = $('#treeView').position().top;
		var divHeight = $('#treeView').height();
		var lastTop = $('#treeView').scrollTop();
		$('#treeView').scrollTop(lastTop + elementTop - divHeight/3 - treeDivTop);

	});
}
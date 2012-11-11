<#macro ProtocolViewer screen>
<#assign model = screen.myModel>
<#assign url_base = "molgenis.do?__target=${screen.name}">
<!-- normally you make one big form for the whole plugin-->
<form method="post" enctype="multipart/form-data" name="${screen.name}" action="">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}">
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action" id="test" value="">

	<div class="formscreen">
		
		<div class="form_header" id="${screen.name}">
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
			<div class="screenpadding">
				<div id="plugin-container">
					<div id="plugin-header">
					<#if (model.dataSets?size == 0)>
						<span>No available catalogs</span>
					<#else>
						<label>Choose a catalog:</label>
						<div id="datasets">
						<#list model.dataSets as dataSet>
							<input type="radio" id="dataset${dataSet.id}" name="radio"><label for="dataset${dataSet.id}">${dataSet.name}</label>
						</#list>
						</div>
						<#-- store dataset ids with dataset input elements -->
						<script type="text/javascript">
							var ids = [<#list model.dataSets as dataset>${dataset.id}<#if (dataset_has_next)>, </#if></#list>];
		 					for(i in ids)
		 						$('#dataset' + ids[i]).data('id', ids[i]);
						</script>
					</#if>
					</div>
					<div id="plugin-content">
						<div id="plugin-content-left">
							<p class="box-title">Browse variables</p>
							<div id="search-controls">
								<select id="search-filter">
									<option value="filter_all" selected>All</option>
									<option value="filter_protocols">Protocols</option>
									<option value="filter_variables">Variables</option>
								</select>
								<input type="text" title="Enter your search term" id="search-text">	
								<input type="button" value="Search" id="search-button">
								<input type="button" value="Clear" id="search-clear-button">
							</div>
							<div id="dataset-browser">
								<div id="dataset-view">
								</div>
							</div>
						</div>
						<div id="plugin-content-right">
							<div id="feature-information">
								<p class="box-title">Description</p>
								<div id="feature-details">
								</div>
							</div>
							<div id="feature-shopping">
								<p class="box-title">Your selection</p>
								<div id="feature-selection">
								</div>
								<div id="download-controls">
		 							<input type="button" value="Download as Excel" id="download-xls-button">
									<input type="button" value="Download as eMeasure" id="download-emeasure-button">
									<input type="button" value="View" id="view-features-button">
								</div>
							</div>
						</div>
					</div>
					<div id="plugin-footer">
					</div>
				</div>
 				<script type="text/javascript">
 					// render inputs
 					$('input[type=button]').button();
 					$('#datasets input[type=radio]').first().attr('checked', 'checked');
 					$('#datasets').buttonset();
 					
 					// create event handlers
 					$('#datasets input').click(function(e) {
 						e.preventDefault();
 						selectDataSet($(this).data('id'));
					});
 					
 					$("#search-text").keyup(function(e){
 						e.preventDefault();
					    if(e.keyCode == 13) // enter
					        $("#search-button").click();
					});
 					
 					$('#search-button').click(function(e) {
 						e.preventDefault();
 						processSearch($('#search-text').val());
 					});
 					
 					$('#search-clear-button').click(function(e) {
 						e.preventDefault();
 						console.log("todo: implement clear search");
 					});
 					
 					$('#download-xls-button').click(function(e) {
 						e.preventDefault();
 						window.location = getSelectedFeaturesURL('xls');
 					});
 					
 					$('#download-emeasure-button').click(function(e) {
 						e.preventDefault();
 						window.location = getSelectedFeaturesURL('emeasure');
 					});
 					
 					$('#view-features-button').click(function(e) {
 						e.preventDefault();
 						window.location = getSelectedFeaturesURL('viewer');
 					});
 					
 					// on ready
					$(function() {
						// select first dataset
						$('#datasets input').first().click();
					});
 				</script>
			</div>
		</div>
	</div>
</form>
</#macro>

<#macro OntoCatTest screen>
<!-- normally you make one big form for the whole plugin-->
<form method="post" enctype="multipart/form-data" name="${screen.name}" action="">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}">
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action">
	<!--need to be set to "true" in order to force a download-->
	<input type="hidden" name="__show">
	

	
<!-- this shows a title and border -->
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
		
		

		
<#if screen.myModel?exists>
	<#assign modelExists = true>
	<#assign model = screen.myModel>
	<p>my Model exists <p>
<#else>
	No model. An error has occurred.
	<#assign modelExists = false>
</#if>



<input type="submit" value="Query" id="loadonto" onclick="document.forms.${screen.name}.__action.value = 'query'; document.forms.${screen.name}.submit();"/>


<#if model.jsonarray??>


<script type="text/javascript">
 
$(document).ready(function(){
	var ac_config = {
		source: ${model.jsonstring},
		select: function(event, ui){
			$("#city").val(ui.item.label);
			$("#state").val(ui.item.label);
		},
		minLength:1
	};
	$("#city").autocomplete(ac_config);
});
</script>

<form action="#" method="post">
	 <p><label for="city">City</label><br />
		 <input type="text" name="city" id="city" value="" /></p>
	 <p><label for="state">State</label><br />
		 <input type="text" name="state" id="state" value="" /></p>
</form>

<script>
	$(function() {
		var json = ${model.jsonstring};
		$( "#tags" ).autocomplete({
			source: json
		});
	});
	</script>
	
	


<div class="demo">

<div class="ui-widget">
	<label for="tags">Select an Ontology</label>
	<input id="tags" name="Ontology"/>
	
</div>

</div><!-- End demo -->

Ontologies Loaded

<#else>

<p>No ontologies loaded<p>

</#if>

<#if model.ontoresults??>
<script type="text/javascript" charset="utf-8">
			$(document).ready(function() {
				$('#example').dataTable();
			} );
		</script>

<body id="dt_example">
		<div id="container">
			<div id="demo">
<table cellpadding="0" cellspacing="0" border="0" class="display" id="example" width="100%">
	<thead>
		<tr>
			<th>OntologyAccession</th>
			<th>OntologyName</th>
			<th>URI</th>
			<th>Label</th>
		</tr>
	</thead>
	<tbody>
	<#list model.ontoresults as results>
	
<tr class="test">
			<td>${results.label}</td>
			<td>${results.ontologyAccession}</td>
			<td>${results.accession}</td>
			<td>${results.URI}</td>
		</tr>

</#list>
		
		
	</tbody>
	<tfoot>
		<tr>
		<th>OntologyAccession</th>
			<th>OntologyName</th>
			<th>URI</th>
			<th>Label</th>
		</tr>
	</tfoot>
</table>
			</div>

		<div class="spacer"></div>




</#if>



	</div>
</form>
</#macro>

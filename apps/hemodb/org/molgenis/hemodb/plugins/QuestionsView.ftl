<!-- normally you make one big form for the whole plugin-->
<form method="post" enctype="multipart/form-data" name="${model.name}" action="">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${model.name}">
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action">
<!-- this shows a title and border -->
	<div class="formscreen">
		<div class="form_header" id="${model.getName()}">
		${model.label}
		</div>
		
		<#--optional: mechanism to show messages-->
		<#list model.getMessages() as message>
			<#if message.success>	
		<p class="successmessage">${message.text}</p>
			<#else>
		<p class="errormessage">${message.text}</p>
			</#if>
		</#list>
	
    
    
		<div class="screenbody">
			<div class="screenpadding">	
	
<#--begin your plugin-->	


<div id="geneExpression">
	Choose the type of gene expression:<br />

		<input type="radio" id="geneExpRaw" name="geneExp" value="raw" /> Raw expression<br />
		<input type="radio" id="geneExpLog" name="geneExp" value="quanLog" checked /> Quantile normalized & log2 transformed expression<br />

</div>
<div id="geneList">
		<br />Supply the gene(s) you want to select (one per line):<br />
		<textarea rows="10" cols="51" name="geneText"value="genes"></textarea>
</div>
<div id="groupSelection">
<br/>Here will be a dropdown thingy for group selection<br />
	<input type="text" name="dropdownThingy" value="dropdownThingy"/>
</div>

<div id="submit">
	<input type='submit' id='jetty' value='Submit' onclick="__action.value='verstuurJetty2'" />
</div>




<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>

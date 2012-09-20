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
			<div class="screenpadding">
<p>
Use this page to submit any unpublished data. Before you can submit any data you need to <a href="molgenis.do?__target=View&select=UserLogin">login</a>. If you do not have an account yet, please <a href="molgenis.do?__target=UserLogin&select=UserLogin&__action=Register">register</a> to create an account.
</p>
			</div>
		</div>
	</div>
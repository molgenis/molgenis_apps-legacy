<#macro org_molgenis_wormqtl_header_HomePage screen>
<!-- normally you make one big form for the whole plugin-->
<form method="post" enctype="multipart/form-data" name="${screen.name}" action="">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}">
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action">
	
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
		
<div align="center">
	<table width="700px">
		<tr>
			<td colspan="4">
				<div style="height: 10px">&nbsp;</div>
			</td>
		</tr>
		<tr>
			<td colspan="4" align="center">
				<div align="left">
				<!-- <font style='font-size:24px; font-weight:bold;'>xQTL workbench</font>-->
				<#if screen.userIsAdminAndDatabaseIsEmpty == true>
					<table bgcolor="white" border="3" bordercolor="red">
						<tr>
							<td>
								<br><i><font color="red">You are logged in as admin, and the database does not contain any investigations or other users. Automated setup is now possible. Database additions will disable this notice.</font></i><br><br>
								Enter your preferred file storage location, and press 'Load' to validate this path and load the example dataset here. Unvalidated paths are overwritten. In addition, the demo users and permissions are loaded.<br><br>
								The default shown is ./data - consider changing this before continuing. Be aware of permissions your OS grants you on this directory, depending on which user started up the application.<br><br>
								<#if screen.validpath?exists>
									<b>A valid path is present and cannot be overwritten here. To do so, use Settings -> File storage.</b><br><br>
									Path: <font style="font-size:medium; font-family: Courier, 'Courier New', monospace">${screen.validpath}</font>
								<#else>
									Path: <input type="text" size="30" style="border:2px solid black; color:blue; display:inline; font-size:medium; font-family: Courier, 'Courier New', monospace" id="inputBox" name="fileDirPath" value="./data" onkeypress="if(window.event.keyCode==13){document.forms.${screen.name}.__action.value = 'setPathAndLoad';}">
								</#if>
								<input type="submit" value="Load" id="loadExamples" onclick="document.forms.${screen.name}.__action.value = 'setPathAndLoad'; document.forms.${screen.name}.submit();"/>
								<br><br>
							</td>
						</tr>
					</table>
				</#if>
				</div>
			</td>
		</tr>
		<tr>
			<td colspan="4">
				<div style="height: 10px">&nbsp;</div>
			</td>
		</tr>
		<tr>
			<td colspan="4" align="center">
				<div align="left">
					<h2>WormQTL private<br><br> <a href="autohideloginswitch">Login</a></h2>
				</div>
			</td>
		</tr>
		<tr>
			<td colspan="4">
				<div style="height: 20px">&nbsp;</div>
			</td>
		</tr>		
	
	</table>
</div>


</div>
</form>
</#macro>

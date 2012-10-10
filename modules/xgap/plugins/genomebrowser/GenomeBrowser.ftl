<#macro GenomeBrowser screen>
<!-- normally you make one big form for the whole plugin-->
<form method="post" enctype="multipart/form-data" name="${screen.name}" action="">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}">
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action">
	<!--need to be set to "true" in order to force a download-->
	<input type="hidden" name="__show">
	
	<input type="hidden" name="__shopMeName">
	<input type="hidden" name="__shopMeId">
	
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
<#else>
	No model. An error has occurred.
	<#assign modelExists = false>
</#if>

<#if model.release??>
	<#assign release = model.release>
<#else>
	<#assign release = "ce10">
</#if>

<#assign ucscMirror = "http://genome.ucsc.edu/">						<#-- NOTE: original and probably most stressed server, avoid if possible -->
<#--assign ucscMirror = "http://genome-mirror.moma.ki.au.dk/"-->		<#-- NOTE, doesn't work: unresponsive to URL parameters -->
<#--assign ucscMirror = "http://genome-mirror.bscb.cornell.edu/"-->		<#-- NOTE: works, but incredibly slow -->
<#--assign ucscMirror = "http://genome.hmgc.mcw.edu/"-->				<#-- NOTE, doesn't work: server error "can not find custom track tmp load directory: '/data/tmp/ct'" -->
<#--assign ucscMirror = "http://genome-mirror.duhs.duke.edu/"-->		<#-- NOTE, doesn't work: error reading GFF "Unrecognized format line 8" -->
<#--assign ucscMirror = "http://genome.qfab.org/"-->					<#-- NOTE, doesn't work: server error "You don't have permission to access /cgi-bin/hgTracks on this server." -->

<h2>Genome browser plugin (UCSC)</h2>

<div align="middle">
	<div style="width: 850px" align="left">
		Using UCSC mirror: <a href="${ucscMirror}">${ucscMirror}</a>. More info: <a href="http://en.wikipedia.org/wiki/UCSC_Genome_Browser">Wikipedia</a>, <a href="http://genomewiki.ucsc.edu/">UCSC Wiki</a>, <a href="http://genome.ucsc.edu/FAQ/">UCSC FAQ</a>.<br>
		<br>
		Selected database release: <b>${release}</b>. Valid options include e.g. <b>hg19</b>, <b>mm10</b> or <b>rn4</b>.<br>
		Default is <b>ce10</b> for C. Elegans, build WS220. <a target="_blank" href="http://genome.ucsc.edu/FAQ/FAQreleases.html#release1">See the list of all available releases.</a><br>
		Switch UCSC database release to:<input type="text" name="__ucsc_release" class="searchBox" value="${release}"> <input type="submit" value="Switch" onclick="__action.value='__setRelease';return true;"/><br>
	</div>
</div>
<br>

<#if model.appUrl??>

	<#if model.appUrl?contains("localhost") || model.appUrl?contains("127.0.0.1")>
		<font color="red"><h>WARNING: your application was contacted via <b>localhost</b> or loopback (<b>127.0.0.1</b>). This means xQTL does not know what URL to provide form the UCSC browser. Please use a world-wide unique URL or IP address!</font>
	</#if>

	<#if model.filesAreVisible>
		<#if model.gffFiles?size == 0>
			There are currently no GFF files in your database!<br>
		<#else>
			<h3>Load GFF file tracks into browser</h3>
			<table border="1">
				<#list model.gffFiles as f>
				<tr>
					<td>
						<b>Add track: <a target="ucsc_iframe" href="${ucscMirror}cgi-bin/hgTracks?db=${release}&hgt.customText=${model.appUrl}/viewfile/${f.name}">${f.name}</a></b>
					</td>
					<td>
						<#if f.description??>${f.description}</#if>
					</td>
				</tr>
				</#list>
			</table>
			<br>
		</#if>
	<#else>
		<br><br>ERROR: Your GFF files are not made visible to the outside world. To enable this, tell your admin to give <b>read</b> permissions for <b>anonymous</b> on <b>org.molgenis.core.MolgenisFile</b> or <b>org.molgenis.xgap.InvestigationFile</b>.<br><br>
	</#if>

	<h3>Embedded genome browser - <a target="_blank" href="${ucscMirror}cgi-bin/hgTracks?db=${release}">open it in a new window</a></h3>
	<div align="middle">
		<iframe name="ucsc_iframe" height="500px" width="850px" src="${ucscMirror}cgi-bin/hgTracks?db=${release}"></iframe>
		<div style="width: 850px" align="left">
			<br>
			To reset the viewer, click on <b>Session</b> and then <i>Click here to reset</i> in the <b>Session Management</b> section. You start from scratch and must add any custom tracks again.
			<br><br>
			PLEASE NOTE: Even if you see no errors here, the UCSC browser might still prompt an error such as <b>Unrecognized format line 1 [...] </b>, or display no results in the viewer. This means you are probably behind a router which has a valid outgoing IP, but prevents incoming requests from reaching your computer within the local network. Ask a technical person to arrange a solution, and/or install xQTL on a server that is reachable via a world-wide unique URL or IP address.
			<br><br>
		</div>
	</div>
	
<#else>

	<br><br>
	<table>
		<tr>
			<td width="20" height="50">
				&nbsp;
			</td>
			<td width="20" height="50">
				<input type="submit" value="Continue..." onclick="__action.value='__continue_to_genomebrowser';return true;"/>
			</td>
		</tr>
	</table>
	
</#if>

	</div>
</form>
</#macro>

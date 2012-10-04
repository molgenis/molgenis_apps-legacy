
<#include "Layout.ftl"/>
<#-- start with the 'main' screen, which is called 'application'-->

<#assign title=screen.label/>

<#--rendering starts here -->
<@molgenis_header screen/>
<div id="container">
	
<#if screen.target?exists && screen.show=="popup">
	<@layout screen.target/>
<#else>	

<#list screen.children as subscreen>
	<@layout subscreen />
</#list>

</#if>

</div>
<div id="footer" style="text-align:center;margin:0 auto;">
	<div style="height:95px;width:900px;margin:0 auto;padding-top:20px">
		<div style="float:left;"><img height="75" src="clusterdemo/wormqtl/eulogo.gif"></div>
		<div style="float:left;"><img height="75" src="clusterdemo/wormqtl/seventh-framework-programme.png"></div>
		<div style="float:left; text-align:center; padding-left:30px; width:640px;"><h3>The research has received funding from the European Community's Health Seventh Framework Programme (FP7/2007-2013) under grant agreement PANACEA (nr 222936)</h3></div>
	</div>
	<i>This database was generated using the open source <a href="http://www.molgenis.org">MOLGENIS database generator</a> version ${screen.getVersion()}.
	<br>Please cite <a href="http://www.ncbi.nlm.nih.gov/pubmed/21210979">Swertz et al (2010)</a> and <a href="http://dx.doi.org/10.1093/bioinformatics/bts049" target="_blank">Arends & van der Velde et al (2012)</a> on use.</i>
</div>
<@molgenis_footer />

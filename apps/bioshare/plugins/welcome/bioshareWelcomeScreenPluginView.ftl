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
<div style="background: white">


<table>
<tr><td colspan="2">
<img src="res/img/BioshareHeader.png">
<h1>Welcome at BioShare: prediction model validation portal</h1>
<p>We provide an ontology based methodology to fast validate the existing prediction models</p>

</td></tr>
<tr><td>
<h2>About Bioshare</h2>

<p align="justify">BioSHaRE is a consortium of leading biobanks and international researchers from all domains 
of biobanking science. The overall aim of the project is to build upon tools and methods available
to achieve solutions for researchers to use pooled data from different cohort and biobank studies. 
This, in order to obtain the very large sample sizes needed to investigate current questions in 
multifactorial diseases, notably on gene-environment interactions. This aim will be achieved through the development
 of harmonization and standardization tools, implementation of these tools and demonstration of their applicability.</p>
<p align="justfiy">
The mission of BioSHaRE is to ensure the development of harmonized measures and standardized computing infrastructures enabling the 
effective pooling of data and key measures of life-style, social circumstances and environment, as well as
 critical sub-components of the phenotypes associated with common complex diseases.</br><a href="http://www.bioshare.eu/">For more information</a></p>

<h2>About validation of prediction models</h2>
<p align="justfiy">
In order to ensure the quality of newly developed prediction models, the external validations need to be carried out.
Therefore we are aiming to develop a software suite in which data management, data harmonization and data analysis are
provided, researchers could spend more time focusing on interpreting the performance of prediction models. 
</p>

<h2>About this software</h2>
<p>
This catalogue software developed jointly by the <a href="http://wiki.gcc.rug.nl">Genomics</a> 
and the <a href="http://www.trailcoordinationcenter.nl">Trial</a> Coordination Centers of the <a href="http://www.umcg.nl">UMC Groningen</a>.
</p>
<p>
This work is part of the larger collaboration on catalogue harmonisation in <a href="http://www.bbmri.nl/">BBMRI-NL bioinformatics rainbow project</a>, 
the <a href="http://www.nbic.nl">NBIC/biobanking task force</a>, 
 <a href="http://p3gobservatory.org">P3G/Obiba</a> and <a href="http://www.bioshare.eu">EU-BioSHArE</a>. 
 The catalogue is structured compatible to the international <a href="http://www.observ-om.org">Observ' data standard for life science observation data</a>, 
 implemented using the <a href="http://www.molgenis.org">MOLGENIS</a> open source software platform and hosted by the <a href="target">Target</a> infrastructure project as part of [BEZWERINGSFORMULE INVULLEN]. 
 For more information on the software, please contact: <a href="mailto:m.a.swertz@rug.nl">Morris Swertz</a>.
</p>
</td>
</tr></table>

</div>

	
<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>

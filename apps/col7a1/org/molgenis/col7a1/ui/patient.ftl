<#include "/org/molgenis/mutation/ui/search/header.ftl">

<#assign patientSummaryVO = model.patientSummaryVO>
<#list model.individualDTO.protocolList as protocolDTO>
<#assign protocolKey = "Protocol" + protocolDTO.protocolId>
<#if model.individualDTO.observedValues?keys?seq_contains(protocolKey)>
<#list model.individualDTO.observedValues[protocolKey]?keys as paKey>
<#assign observedValueDTOValList = model.individualDTO.observedValues[protocolKey][paKey]>
<#assign observedValueDTO     = observedValueDTOValList?first>
<#if observedValueDTO.featureDTO.featureName == 'LH7:2 Amount of type VII collagen'>
  <#assign if_collagen = observedValueDTO.value>
<#elseif observedValueDTO.featureDTO.featureName == 'Anchoring fibrils Number'>
  <#assign em_fibrils = observedValueDTO.value>
<#elseif observedValueDTO.featureDTO.featureName == 'Material stored?'>
  <#assign patient_material = observedValueDTO.value>
</#if>
</#list>
</#if>
</#list>

<table class="listtable">
<tr class="form_listrow0"><th>Patient ID</th><td>${patientSummaryVO.patientIdentifier}</td></tr>
<tr class="form_listrow1"><th>Genotype</th><td>
<#assign i = 0>
<#list patientSummaryVO.variantDTOList as variantSummaryVO>
<#if i &gt; 0>/</#if>
${variantSummaryVO.cdnaNotation}<#if variantSummaryVO.aaNotation??> (${variantSummaryVO.aaNotation})</#if>
<#assign i = i + 1>
</#list>
</td></tr>
<tr class="form_listrow0"><th>Phenotype</th><td>${patientSummaryVO.getPhenotypeMajor()}<#if patientSummaryVO.getPhenotypeSub() != "">, ${patientSummaryVO.getPhenotypeSub()}</#if><#if patientSummaryVO.patientConsent != "no"> [<a href="molgenis.do?__target=${screen.name}&__action=showPhenotypeDetails&pid=${patientSummaryVO.patientIdentifier}#phenotype">Details</a>]</#if></td></tr>

<tr class="form_listrow1"><th>Immunofluorescence: type VII collagen</th><td>${if_collagen}<#if patientSummaryVO.patientConsent != "no">  [<a href="molgenis.do?__target=${screen.name}&__action=showPhenotypeDetails&pid=${patientSummaryVO.patientIdentifier}#phenotype">Details</a>]</#if></td></tr>
<tr class="form_listrow0"><th>Electron Microscopy: anchoring fibrils</th><td>${em_fibrils}<#if patientSummaryVO.patientConsent != "no">  [<a href="molgenis.do?__target=${screen.name}&__action=showPhenotypeDetails&pid=${patientSummaryVO.patientIdentifier}#phenotype">Details</a>]</#if></td></tr>
<tr class="form_listrow1"><th>Patient material available?</th><td>${patient_material}</td></tr>

<tr class="form_listrow0"><th>Local patient no</th><td>${patientSummaryVO.patientLocalId}</td></tr>
<tr class="form_listrow1"><th>Reference</th><td>
<#if patientSummaryVO.publicationDTOList?? && patientSummaryVO.publicationDTOList?size &gt; 0>
<#list patientSummaryVO.publicationDTOList as publicationDTO>
<a href="${patientSummaryVO.pubmedURL}${publicationDTO.pubmedId}" title="${publicationDTO.title}" target="_new">${publicationDTO.firstAuthor} (${publicationDTO.year}) ${publicationDTO.journal}</a><br/>
</#list>
<#if patientSummaryVO.submitterDepartment??>
First submitted as unpublished case by
${patientSummaryVO.submitterDepartment}, ${patientSummaryVO.submitterInstitute}, ${patientSummaryVO.submitterCity}, ${patientSummaryVO.submitterCountry}
</#if>
<#elseif patientSummaryVO.submitter??>
Unpublished<br/>
${patientSummaryVO.submitterDepartment}, ${patientSummaryVO.submitterInstitute}, ${patientSummaryVO.submitterCity}, ${patientSummaryVO.submitterCountry}
</#if></td></tr>
</table>

<p>
[<a href="javascript:window.history.back();" onclick="javascript:window.history.back();">Back to results</a>]
</p>
<p>
[<a href="#">Back to top</a>]
</p>
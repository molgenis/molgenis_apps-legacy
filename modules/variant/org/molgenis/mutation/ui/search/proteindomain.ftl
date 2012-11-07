<#include "header.ftl">

<#assign exonDTOList = model.proteinDomainDTO.exonDTOList>
<#if exonDTOList?size &gt; 0>
<h4>Browse the ${model.geneDTO.name} gene: ${model.proteinDomainDTO.domainName}</h4>
<#--
${model.mbrowse.createProteinDomainPanel()}
-->
<br/>
${model.getMBrowsePanel().toHtml()}
<br/>
</#if>

${vo.rawOutput}

<#include "footer.ftl">
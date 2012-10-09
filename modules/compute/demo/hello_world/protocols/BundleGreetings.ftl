#FOREACH planetType
<#list planet as p>
	cat ${p}.txt >> ${planetType}.txt
</#list>
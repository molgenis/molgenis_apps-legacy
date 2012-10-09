#FOREACH planetType
<#list planet as p>
	cat ${p}.txt 2>&1 | tee -a ${planetType}.txt
</#list>
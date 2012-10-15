#MOLGENIS walltime=96:00:00 nodes=1 cores=1 mem=4

#FOREACH project,chr

getFile 

#Retrieve number of chunks (list)
s=${chunk?size}

cp ${imputationResultDir}/chunk1-chr${chr}.imputed.dose ${imputationResultDir}/chunk1-chr${chr}.imputed.dose.tmp

for c in `seq 2 $s`
do
	cut -f3- ${imputationResultDir}/chunk$c-chr${chr}.imputed.dose \
	> ${imputationResultDir}/chunk$c-chr${chr}.imputed.dose.tmp
done

paste \
<#list chunk as chnk>
${imputationResultDir}/chunk${chnk}-chr${chr}.imputed.dose.tmp \
</#list>
> ${imputationResultDir}/chr${chr}.imputed.merged.dose
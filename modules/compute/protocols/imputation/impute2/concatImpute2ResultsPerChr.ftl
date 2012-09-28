#MOLGENIS walltime=48:00:00 nodes=1 cores=1 mem=4

#FOREACH project,chr

<#list impute2ResultChrBinInfoFile as infoFile>
	getFile ${infoFile}
</#list>
<#list impute2ResultChrBinGenFile as genFile>
	getFile ${genFile}
</#list>
putFile ${imputationResultDir}/chr_${chr}
putFile ${imputationResultDir}/chr_${chr}_info


inputs "${ssvQuoted(impute2ResultChrBinInfoFile)}"
inputs "${ssvQuoted(impute2ResultChrBinGenFile)}"
alloutputsexist "${imputationResultDir}/chr_${chr}"
alloutputsexist "${imputationResultDir}/chr_${chr}_info"

#Concat the bins with compute for each

cat ${ssvQuoted(impute2ResultChrBinInfoFile)} > ${imputationResultDir}/chr_${chr}_info

cat ${ssvQuoted(impute2ResultChrBinGenFile)} > ${imputationResultDir}/chr_${chr}

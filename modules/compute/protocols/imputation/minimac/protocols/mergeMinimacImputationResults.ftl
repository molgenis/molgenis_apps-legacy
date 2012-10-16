#MOLGENIS walltime=96:00:00 nodes=1 cores=1 mem=4

#FOREACH project,chr

<#list chunk as chnk>


getFile ${studyChunkChrDir}/chunk${chnk}-chr${chr}.imputed.dose
getFile ${studyChunkChrDir}/chunk${chnk}-chr${chr}.imputed.info
getFile ${studyChunkChrDir}/chunk${chnk}-chr${chr}.imputed.erate

inputs "${studyChunkChrDir}/chunk${chnk}-chr${chr}.imputed.dose"
inputs "${studyChunkChrDir}/chunk${chnk}-chr${chr}.imputed.info"
inputs "${studyChunkChrDir}/chunk${chnk}-chr${chr}.imputed.erate"

</#list>
getFile ${studyPedMapChr}.ped
inputs "${studyPedMapChr}.ped"

alloutputsexist \
"${imputationResultDir}/chr${chr}.imputed.dose" \
"${imputationResultDir}/chr${chr}.imputed.info" \
"${imputationResultDir}/chr${chr}.imputed.erate" \
"${imputationResultDir}/chr${chr}.fam"

#Retrieve number of chunks (list)
s=${chunk?size}


##Paste resulting chunk *.impuated.dose files together
cp ${studyChunkChrDir}/chunk1-chr${chr}.imputed.dose ${studyChunkChrDir}/chunk1-chr${chr}.imputed.dose.tmp

<#assign x=chunk?size+1>

<#if x gt 1>
for c in `seq 2 $s`
do
	cut -f3- ${studyChunkChrDir}/chunk$c-chr${chr}.imputed.dose \
	> ${studyChunkChrDir}/chunk$c-chr${chr}.imputed.dose.tmp
done
</#if>

paste \
	<#list chunk as chnk>
	${studyChunkChrDir}/chunk${chnk}-chr${chr}.imputed.dose.tmp \
	</#list>
	> ${imputationResultDir}/~chr${chr}.imputed.dose

#Get return code from last program call
returnCode=$?


if [ $returnCode -eq 0 ]
then

	mv ${imputationResultDir}/~chr${chr}.imputed.dose ${imputationResultDir}/chr${chr}.imputed.dose

	putFile ${imputationResultDir}/chr${chr}.imputed.dose
	
else
  
	echo -e "\nNon zero return code not making files final. Existing temp files are kept for debuging purposes\n\n"
	#Return non zero return code
	exit 1

fi


#Concat *.rec and *.info chunk files

<#assign x=chunk?size+1>

<#if x gt 1>
	( cat ${studyChunkChrDir}/chunk1-chr${chr}.imputed.info <#list 2..x as chnk><#if chnk_has_next>; tail -n +2 ${studyChunkChrDir}/chunk${chnk}-chr${chr}.imputed.info </#if></#list>) \
	>> ${imputationResultDir}/~chr${chr}.imputed.info
</#if>

#Get return code from last program call
returnCode=$?


if [ $returnCode -eq 0 ]
then

	mv ${imputationResultDir}/~chr${chr}.imputed.info ${imputationResultDir}/chr${chr}.imputed.info

	putFile ${imputationResultDir}/chr${chr}.imputed.info
	
else
  
	echo -e "\nNon zero return code not making files final. Existing temp files are kept for debuging purposes\n\n"
	#Return non zero return code
	exit 1

fi

<#if x gt 1>
	( cat ${studyChunkChrDir}/chunk1-chr${chr}.imputed.erate <#list 2..x as chnk><#if chnk_has_next>; tail -n +2 ${studyChunkChrDir}/chunk${chnk}-chr${chr}.imputed.erate </#if></#list>) \
	>> ${studyChunkChrDir}/~chr${chr}.imputed.erate
</#if>

#Get return code from last program call
returnCode=$?


if [ $returnCode -eq 0 ]
then

	mv ${studyChunkChrDir}/~chr${chr}.imputed.erate ${imputationResultDir}/chr${chr}.imputed.erate

	putFile ${imputationResultDir}/chr${chr}.imputed.erate
	
else
  
	echo -e "\nNon zero return code not making files final. Existing temp files are kept for debuging purposes\n\n"
	#Return non zero return code
	exit 1

fi

#Create *.fam file from *.imputed.dose and original ped file

awk '{print $1}' ${studyChunkChrDir}/chunk1-chr${chr}.imputed.dose \
	| awk '{print $1,$2}' FS="->" OFS="_" > ${studyChunkChrDir}/chr${chr}_fam_sample.txt

awk '{$7=$1"_"$2;print $7,$1,$2,$3,$4,$5,$6}' \
	${studyPedMapChr}.ped \
	> ${studyChunkChrDir}/chr${chr}.tmp.ped

awk ' FILENAME=="${studyChunkChrDir}/chr${chr}.tmp.ped" \
	{arr[$1]=$0; next} FILENAME=="${studyChunkChrDir}/chr${chr}_fam_sample.txt"  \
	{print arr[$1]} ' ${studyChunkChrDir}/chr${chr}.tmp.ped \
	${studyChunkChrDir}/chr${chr}_fam_sample.txt \
	| awk '{print $1,$2,$3,$4,$5,$6,$7}' \
	| awk '{
		if ($0 ~ /^[ ]*$/) { \
			print "ERROR: FamilyID_SampleID combination not found in original PED file! Exiting now!" > "/dev/stderr"; exit 1; \
		} \
		else { \
			print $2,$3,$4,$5,$6,$7} \
		}' \
	> ${imputationResultDir}/~chr${chr}.fam

#Get return code from last program call
returnCode=$?


if [ $returnCode -eq 0 ]
then

	mv ${imputationResultDir}/~chr${chr}.fam ${imputationResultDir}/chr${chr}.fam

	putFile ${imputationResultDir}/chr${chr}.fam
	
else
  
	echo -e "\nNon zero return code not making files final. Existing temp files are kept for debuging purposes\n\n"
	#Return non zero return code
	exit 1

fi


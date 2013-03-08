#MOLGENIS walltime=48:00:00 nodes=1 cores=1 mem=4

#FOREACH project,chr



declare -a impute2ChunkOutputs=(${ssvQuoted(impute2ChunkOutput)})
declare -a impute2ChunkOutputInfos=(${ssvQuoted(impute2ChunkOutputInfo)})

inputs ${ssvQuoted(impute2ChunkOutput)}
inputs ${ssvQuoted(impute2ChunkOutputInfo)}

outputFolder="${outputFolder}"

<#noparse>

alloutputsexist "${outputFolder}/chr_${chr}" "${outputFolder}/chr_${chr}_info"


rm -f ${outputFolder}/~chr_${chr}
rm -f ${outputFolder}/chr_${chr}_info

#Concat the actual imputation results
cat ${impute2ChunkOutputs[@]} >> ${outputFolder}/~chr_${chr}

#Need not capture the header of the first non empty file
headerSet = false
for chunkInfoFile in "${array[@]}"
do
	
	#Skip empty files
	lineCount=`wc -l out.log | awk '{print $1}'`
	if [ "$lineCount" -eq "0" ]
	then
		continue
	fi

	#Print header if not yet done needed 
	if [ "$headerSet" == "false" ]
	then
		head -n 1 $chunkInfoFile >> ${outputFolder}/chr_${chr}_info
		$headerSet = "true"
	fi
	
	#Cat without header
	cat -n +2 < $chunkInfoFile >> ${outputFolder}/chr_${chr}_info
	
done


</#noparse>

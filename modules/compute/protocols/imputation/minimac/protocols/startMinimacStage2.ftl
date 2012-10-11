#MOLGENIS walltime=05:00:00 nodes=1 cores=1 mem=4


getFile ${concatWorksheetsJar}

<#list finalChunkChrWorkSheet as chunkFile>
	getFile ${chunkFile}
</#list>


inputs "${ssvQuoted(finalChunkChrWorkSheet)}"
alloutputsexist \
"${projectPhasingJobsDir}/check_for_submission.txt" \
"${concattedChunkWorksheet}" \
"${projectPhasingJobsDirTarGz}"


module load jdk/${javaversion}

java -jar ${concatWorksheetsJar} \
${tmpConcattedChunkWorksheet} \
${ssvQuoted(finalChunkChrWorkSheet)}

#Get return code from last program call
returnCode=$?

if [ $returnCode -eq 0 ]
then
	
	echo -e "\nMoving temp files to final files\n\n"

	mv ${tmpConcattedChunkWorksheet} ${concattedChunkWorksheet}

	putFile ${concattedChunkWorksheet}
	
else
  
	echo -e "\nNon zero return code not making files final. Existing temp files are kept for debuging purposes\n\n"
	#Return non zero return code
	exit 1

fi


<#if autostart == "TRUE">

#Call compute to generate phasing jobs

mkdir -p ${projectPhasingJobsDir}

# Execute MOLGENIS/compute to create job scripts.
sh ${McDir}/molgenis_compute.sh \
-worksheet=${concattedChunkWorksheet} \
-parameters=${McParameters} \
-workflow=${McProtocols}/../workflowMinimacStage2.csv \
-protocols=${McProtocols}/ \
-templates=${McTemplates}/ \
-scripts=${projectPhasingJobsDir}/ \
-id=${McId}


#Get return code from last program call
returnCode=$?


if [ $returnCode -eq 0 ]
then
	
	echo -e "\nJob generation succesful!\n\n"

	cd ${projectPhasingJobsDir}
	sh submit.sh
	
	touch ${projectPhasingJobsDir}/check_for_submission.txt

	tar czf ${projectPhasingJobsDirTarGz} ${projectPhasingJobsDir}
	putFile ${projectPhasingJobsDirTarGz}
	
else
	
	echo -e "\nNon zero return code not making files final. Existing temp files are kept for debuging purposes\n\n"
	#Return non zero return code
	exit 1

fi

<#elseif autostart == "FALSE">

	echo "No autostart selected"
	
	echo "You can run Molgenis Compute executing the following command:"
	
	
	
</#if>


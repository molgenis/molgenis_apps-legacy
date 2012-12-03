#MOLGENIS walltime=06:00:00 nodes=1 cores=1 mem=1

inputs ${chrVcfReferenceIntermediateFile}
alloutputsexist ${chrVcfReferenceFile}

mkdir -p ${vcfReferenceFolder}

perl ${convertVcfIdsScript} -inputvcf ${chrVcfReferenceIntermediateFile} -outputvcf ${chrVcfReferenceFileTmp} -delimiter ${convertVcfIdsScriptDelimiter}

#Get return code from last program call
returnCode=$?

if [ $returnCode -eq 0 ]
then
	
	echo -e "\nMoving temp files to final files\n\n"
	mv ${chrVcfReferenceFileTmp} ${chrVcfReferenceFile}
	
else
  
	echo -e "\nNon zero return code not making files final. Existing temp files are kept for debuging purposes\n\n"
	#Return non zero return code
	exit 1

fi
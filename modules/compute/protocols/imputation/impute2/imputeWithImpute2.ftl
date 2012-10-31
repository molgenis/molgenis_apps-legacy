#MOLGENIS walltime=96:00:00 nodes=1 cores=1 mem=4



getFile ${referenceImpute2HapFile}
getFile ${referenceImpute2LegendFile}
getFile ${referenceImpute2MapFile}
getFile ${preparedStudyDir}/chr${chr}.gen
putFile ${impute2ResultChrBin}
putFile ${impute2ResultChrBin}_info
putFile ${impute2ResultChrBin}_info_by_sample
putFile ${impute2ResultChrBin}_summary
putFile ${impute2ResultChrBin}_warnings


inputs "${referenceImpute2HapFile}"
inputs "${referenceImpute2LegendFile}"
inputs "${referenceImpute2MapFile}"
inputs "${preparedStudyDir}/chr${chr}.gen"
alloutputsexist "${impute2ResultChrBin}" \
"${impute2ResultChrBin}_info" \
"${impute2ResultChrBin}_info_by_sample" \
"${impute2ResultChrBin}_summary" \
"${impute2ResultChrBin}_warnings"


module load ${impute}/${impute2Binversion}

mkdir -p ${impute2ResultDir}/${chr}/

${impute2Bin} \
-h ${referenceImpute2HapFile} \
-l ${referenceImpute2LegendFile} \
-m ${referenceImpute2MapFile} \
-g ${preparedStudyDir}/chr${chr}.gen \
-int ${fromChrPos} ${toChrPos} \
-o ${impute2ResultChrBinTemp} \
2>&1 | tee -a ${impute2ResultChrBinLog}


#Get return code from last program call
returnCode=$?

if [ $returnCode -eq 0 ]
then
	
	echo -e "\nMoving temp files to final files\n\n"

	for tempFile in ${impute2ResultChrBinTemp}* ; do
		finalFile=`echo $tempFile | sed -e "s/~//g"`
		mv $tempFile $finalFile
	done
	
else
  
	echo -e "\nNon zero return code not making files final. Existing temp files are kept for debuging purposes\n\n"
	#Return non zero return code
	exit 1

fi

#Grep the log file, if there are no SNPs in this interval, three additional empty files will be created
#This to prevent the pipeline from crashing, since Impute2 doesn't produce these files when no SNPs available


if [ ! -f ${impute2ResultDir}/${chr}/chr_${chr}_from_${fromChrPos}_to_${toChrPos}_info ]
then

	echo "Touching file: ${impute2ResultDir}/${chr}/chr_${chr}_from_${fromChrPos}_to_${toChrPos}"
	echo "Touching file: ${impute2ResultDir}/${chr}/chr_${chr}_from_${fromChrPos}_to_${toChrPos}_info"
	echo "Touching file: ${impute2ResultDir}/${chr}/chr_${chr}_from_${fromChrPos}_to_${toChrPos}_info_by_sample"

	touch ${impute2ResultDir}/${chr}/chr_${chr}_from_${fromChrPos}_to_${toChrPos}
	touch ${impute2ResultDir}/${chr}/chr_${chr}_from_${fromChrPos}_to_${toChrPos}_info
	touch ${impute2ResultDir}/${chr}/chr_${chr}_from_${fromChrPos}_to_${toChrPos}_info_by_sample
	
fi


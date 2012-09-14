#MOLGENIS walltime=48:00:00 nodes=1 cores=1 mem=4

#EXES gtoolBin
#LOGS log

#FOREACH project,chr

getFile "${imputationResultDir}/chr_${chr}.gen"
getFile "${preparedStudyDir}/chr${chr}.sample"
putFile "${imputationResultDir}/chr_${chr}.ped"
putFile "${imputationResultDir}/chr_${chr}.map"


inputs "${imputationResultDir}/chr_${chr}.gen"
inputs "${preparedStudyDir}/chr${chr}.sample"
alloutputsexist "${imputationResultDir}/chr_${chr}.ped"
alloutputsexist "${imputationResultDir}/chr_${chr}.map"


#module load ${gtoolBin}/${gtoolBinversion}

${gtoolBin} -G --g ${imputationResultDir}/chr_${chr}.gen --s ${preparedStudyDir}/chr${chr}.sample --ped ${imputationResultDir}/~chr_${chr}.ped --map ${imputationResultDir}/~chr_${chr}.map



if [ $returnCode -eq 0 ]
then
	
	echo -e "\nMoving temp files to final files\n\n"

	mv ${imputationResultDir}/~chr_${chr}.ped ${imputationResultDir}/chr_${chr}.ped
	mv ${imputationResultDir}/~chr_${chr}.map ${imputationResultDir}/chr_${chr}.map

	
else
  
	echo -e "\nNon zero return code not making files final. Existing temp files are kept for debuging purposes\n\n"
	#Return non zero return code
	exit 1

fi
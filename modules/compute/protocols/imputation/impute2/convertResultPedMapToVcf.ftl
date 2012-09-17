#MOLGENIS walltime=48:00:00 nodes=1 cores=1 mem=4

#FOREACH project,chr

getFile ${imputationResultDir}/chr_${chr}.ped
getFile ${imputationResultDir}/chr_${chr}.map
putFile ${imputationResultDir}/chr_${chr}.vcf

inputs "${imputationResultDir}/chr_${chr}.ped"
inputs "${imputationResultDir}/chr_${chr}.map"
alloutputsexist "${imputationResultDir}/chr_${chr}.vcf"


#module load ${plinkseqBin}/${plinkseqversion}


${plinkseqBin} \
--noweb \
--recode-vcf \
--ped ${imputationResultDir}/chr_${chr}.ped \
--map ${imputationResultDir}/chr_${chr}.map \
--out ${imputationResultDir}/~chr_${chr}


#Get return code from last program call
returnCode=$?

if [ $returnCode -eq 0 ]
then
	
	echo -e "\nMoving temp files to final files\n\n"

	mv ${imputationResultDir}/~chr_${chr}.vcf ${imputationResultDir}/chr_${chr}.vcf

	
else
  
	echo -e "\nNon zero return code not making files final. Existing temp files are kept for debuging purposes\n\n"
	#Return non zero return code
	exit 1

fi
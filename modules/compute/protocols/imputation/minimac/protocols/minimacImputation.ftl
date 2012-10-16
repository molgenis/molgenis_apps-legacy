#MOLGENIS walltime=96:00:00 nodes=1 cores=1 mem=4

#FOREACH project,chr,chunk

getFile ${minimacBin}

getFile ${referenceChrVcf}
getFile ${studyChunkChrDir}/chunk${chunk}-chr${chr}.gz
getFile ${studyMerlinChrDir}/chunk${chunk}-chr${chr}.dat.snps

inputs "${referenceChrVcf}" 
inputs "${studyChunkChrDir}/chunk${chunk}-chr${chr}.gz" 
inputs "${studyMerlinChrDir}/chunk${chunk}-chr${chr}.dat.snps"

${minimacBin} \
	--refHaps ${referenceChrVcf} \
	--vcfReference \
	--haps ${studyChunkChrDir}/chunk${chunk}-chr${chr}.gz \
	--snps ${studyMerlinChrDir}/chunk${chunk}-chr${chr}.dat.snps \
	--prefix ${studyChunkChrDir}/chunk${chunk}-chr${chr}.imputed \
	--autoClip ${studyMerlinChrDir}/autoChunk-chr20.dat \
	2>&1 | tee ${studyChunkChrDir}/chunk${chunk}-chr${chr}.imputed-minimac.log

#########################################
##############FIX THIS LATER#############
#########################################


#Get return code from last program call
returnCode=$?


if [ $returnCode -eq 0 ]
then
	
	echo -e "\nMoving temp files to final files\n\n"

	#mv ${tmpFinalChunkChrWorksheet} ${finalChunkChrWorksheet}

	#putFile ${finalChunkChrWorksheet}
	
	
else
  
	echo -e "\nNon zero return code not making files final. Existing temp files are kept for debuging purposes\n\n"
	#Return non zero return code
	exit 1

fi

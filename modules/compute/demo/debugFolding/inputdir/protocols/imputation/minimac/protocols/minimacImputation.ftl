#MOLGENIS walltime=96:00:00 nodes=1 cores=1 mem=4

#FOREACH project,chr,chunk

getFile ${minimacBin}

getFile ${referenceChrVcf}
getFile ${studyChunkChrDir}/chunk${chunk}-chr${chr}.gz
getFile ${studyMerlinChrDir}/chunk${chunk}-chr${chr}.dat.snps

inputs "${referenceChrVcf}" 
inputs "${studyChunkChrDir}/chunk${chunk}-chr${chr}.gz" 
inputs "${studyMerlinChrDir}/chunk${chunk}-chr${chr}.dat.snps"

alloutputsexist \
${studyChunkChrDir}/chunk${chunk}-chr${chr}.imputed.dose \
${studyChunkChrDir}/chunk${chunk}-chr${chr}.imputed.erate \
${studyChunkChrDir}/chunk${chunk}-chr${chr}.imputed.info \
${studyChunkChrDir}/chunk${chunk}-chr${chr}.imputed.info.draft \
${studyChunkChrDir}/chunk${chunk}-chr${chr}.imputed.rec \
${studyChunkChrDir}/chunk${chunk}-chr${chr}.imputed-minimac.log


${minimacBin} \
	--refHaps ${referenceChrVcf} \
	--vcfReference \
	--haps ${studyChunkChrDir}/chunk${chunk}-chr${chr}.gz \
	--snps ${studyMerlinChrDir}/chunk${chunk}-chr${chr}.dat.snps \
	--prefix ${studyChunkChrDir}/~chunk${chunk}-chr${chr}.imputed \
	--autoClip ${studyMerlinChrDir}/autoChunk-chr${chr}.dat \
	2>&1 | tee ${studyChunkChrDir}/~chunk${chunk}-chr${chr}.imputed-minimac.log


#Get return code from last program call
returnCode=$?


if [ $returnCode -eq 0 ]
then

	mv ${studyChunkChrDir}/~chunk${chunk}-chr${chr}.imputed.dose ${studyChunkChrDir}/chunk${chunk}-chr${chr}.imputed.dose
	mv ${studyChunkChrDir}/~chunk${chunk}-chr${chr}.imputed.erate ${studyChunkChrDir}/chunk${chunk}-chr${chr}.imputed.erate
	mv ${studyChunkChrDir}/~chunk${chunk}-chr${chr}.imputed.info ${studyChunkChrDir}/chunk${chunk}-chr${chr}.imputed.info
	mv ${studyChunkChrDir}/~chunk${chunk}-chr${chr}.imputed.info.draft ${studyChunkChrDir}/chunk${chunk}-chr${chr}.imputed.info.draft
	mv ${studyChunkChrDir}/~chunk${chunk}-chr${chr}.imputed.rec ${studyChunkChrDir}/chunk${chunk}-chr${chr}.imputed.rec
	mv ${studyChunkChrDir}/~chunk${chunk}-chr${chr}.imputed-minimac.log ${studyChunkChrDir}/chunk${chunk}-chr${chr}.imputed-minimac.log

	putFile ${studyChunkChrDir}/chunk${chunk}-chr${chr}.imputed.dose
	putFile ${studyChunkChrDir}/chunk${chunk}-chr${chr}.imputed.erate
	putFile ${studyChunkChrDir}/chunk${chunk}-chr${chr}.imputed.info
	putFile ${studyChunkChrDir}/chunk${chunk}-chr${chr}.imputed.info.draft
	putFile ${studyChunkChrDir}/chunk${chunk}-chr${chr}.imputed.rec
	putFile ${studyChunkChrDir}/chunk${chunk}-chr${chr}.imputed-minimac.log
	
else
  
	echo -e "\nNon zero return code not making files final. Existing temp files are kept for debuging purposes\n\n"
	#Return non zero return code
	exit 1

fi

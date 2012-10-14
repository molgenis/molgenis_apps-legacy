#MOLGENIS walltime=96:00:00 nodes=1 cores=1 mem=4

#FOREACH project,chr,chunk

getFile ${machBin}

getFile ${studyMerlinChrDir}/chunk${chunk}-chr${chr}.dat
getFile ${studyMerlinChrPed}

inputs "${studyMerlinChrDir}/chunk${chunk}-chr${chr}.dat"
inputs "${studyMerlinChrPed}"

alloutputsexist \
	"${studyChunkChrDir}/chunk${chunk}-chr${chr}.erate" \
	"${studyChunkChrDir}/chunk${chunk}-chr${chr}.gz" \
	"${studyChunkChrDir}/chunk${chunk}-chr${chr}.rec" \
	"${studyChunkChrDir}/chunk${chunk}-chr${chr}-mach.log"


mkdir -p ${studyChunkChrDir}

${machBin} \
	-d ${studyMerlinChrDir}/chunk${chunk}-chr${chr}.dat \
	-p ${studyMerlinChrPed} \
	--prefix ${studyChunkChrDir}/~chunk${chunk}-chr${chr} \
	--rounds ${phasingRounds} \
	--states ${phasingStates} \
	--phase \
	2>&1 | tee -a ${studyChunkChrDir}/chunk${chunk}-chr${chr}-mach.log

#Get return code from last program call
returnCode=$?

if [ $returnCode -eq 0 ]
then
	
	echo -e "\nMoving temp files to final files\n\n"

	mv ${studyChunkChrDir}/~chunk${chunk}-chr${chr}.erate ${studyChunkChrDir}/chunk${chunk}-chr${chr}.erate
	mv ${studyChunkChrDir}/~chunk${chunk}-chr${chr}.gz ${studyChunkChrDir}/chunk${chunk}-chr${chr}.gz
	mv ${studyChunkChrDir}/~chunk${chunk}-chr${chr}.rec ${studyChunkChrDir}/chunk${chunk}-chr${chr}.rec
	mv ${studyChunkChrDir}/~chunk${chunk}-chr${chr}-mach.log ${studyChunkChrDir}/chunk${chunk}-chr${chr}-mach.log

	putFile ${studyChunkChrDir}/chunk${chunk}-chr${chr}.erate
	putFile ${studyChunkChrDir}/chunk${chunk}-chr${chr}.gz
	putFile ${studyChunkChrDir}/chunk${chunk}-chr${chr}.rec
	putFile ${studyChunkChrDir}/chunk${chunk}-chr${chr}-mach.log	
	
else
  
	echo -e "\nNon zero return code not making files final. Existing temp files are kept for debuging purposes\n\n"
	#Return non zero return code
	exit 1

fi


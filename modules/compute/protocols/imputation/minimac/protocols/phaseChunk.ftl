#MOLGENIS walltime=96:00:00 nodes=1 cores=1 mem=4

#FOREACH project,chr,chunk

getFile ${machBin}

getFile ${studyMerlinChrDir}/chunk${chunk}-chr${chr}.dat.snps
getFile ${studyMerlinChrPed}


${machBin} \
	-d ${studyMerlinChrDir}/chunk${chunk}-chr${chr}.dat \
	-p ${studyMerlinChrPed} \
	--prefix ${studyChunkChrDir}/chunk${chunk}-chr${chr} \
	--rounds ${phasingRounds} \
	--states ${phasingStates} \
	--phase \
	--sample ${phasingHaplotypeSampling} \
	2>&1 | tee -a ${studyChunkChrDir}/chunk${chunk}-chr${chr}-mach.log

if [ $returnCode -eq 0 ]
then
	
	echo -e "\nMoving temp files to final files\n\n"


	#putFile ${studyChunkChrDir}/chunk${chunk}-chr${chr} ..????
	putFile ${studyChunkChrDir}/chunk${chunk}-chr${chr}-mach.log
	
	
else
  
	echo -e "\nNon zero return code not making files final. Existing temp files are kept for debuging purposes\n\n"
	#Return non zero return code
	exit 1

fi

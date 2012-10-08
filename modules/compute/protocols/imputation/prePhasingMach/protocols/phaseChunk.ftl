#MOLGENIS walltime=96:00:00 nodes=1 cores=1 mem=4

#FOREACH project,chr,chunk

getFile ${machBin}

getFile ${studyMerlinChrDir}/chunk$chunk-chr${chr}.dat.snps
getFile ${studyMerlinChrPed}



${machBin} \
-d ${studyMerlinChrDir}/chunk$chunk-chr${chr}.dat.snps \
-p ${studyMerlinChrPed} \
--prefix ${studyChunkChrDir}/chunk$chunk-chr${chr} \
--rounds ${phasingRounds} \
--states ${phasingStates} \
--phase \
--sample ${phasingHaplotypeSampling} \
2>&1 | tee -a ${studyChunkChrDir}/chunk$chunk-chr${chr}-mach.log

#Add the if returnState thingy later#


putFile ${studyChunkChrDir}/chunk$chunk-chr${chr} ..????
putFile ${studyChunkChrDir}/chunk$chunk-chr${chr}-mach.log
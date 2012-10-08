#MOLGENIS walltime=48:00:00 nodes=1 cores=4 mem=12

#FOREACH project,chr,chunk


#NEED TO GET FILES

${minimacOmpBin} \
	--cpus 4 \
	--refHaps ${referenceChrVcf} \
	--vcfReference \
	--haps  \
	--snps  \
	--prefix ${studyChunkChrDir}/chunk${chunk}-chr${chr}
	


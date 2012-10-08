#MOLGENIS walltime=05:00:00 nodes=1 cores=1 mem=4

for chr in {$startChr..$endChr}
do
	getFile ${studyMerlinDir}/$chr/chr$chr.map
	getFile ${studyMerlinDir}/$chr/chr$chr.dat
	getFile ${studyMerlinDir}/$chr/chr$chr.ped
	
	inputs ${studyMerlinDir}/$chr/chr$chr.map
	inputs ${studyMerlinDir}/$chr/chr$chr.dat
	inputs ${studyMerlinDir}/$chr/chr$chr.ped
done

echo "chr,chunk" > ${chunkWorksheet}

for chr in {$startChr..$endChr}
do

	mkdir -p ${studyMerlinDir}/chr$chr
	
	ChunkChromosome -d ${studyMerlinDir}/$chr/chr$chr.dat -n ${chunkSize} -o ${chunkOverlap}
	
	chunks=(chunk*-chr$chr.dat.snps)

	#Get number of chunks
	s=${#chunks[*]}
	
	for c in `seq 1 $s`
	do
		echo "{$chr},{$c}" >> ${chunkWorksheet}
	done
	
done
	
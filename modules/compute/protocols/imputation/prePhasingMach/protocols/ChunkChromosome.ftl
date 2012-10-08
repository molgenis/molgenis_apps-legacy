#MOLGENIS walltime=48:00:00 nodes=1 cores=1 mem=4

#FOREACH project

getFile ${chunkChromosomeBin}
getFile ${expandWorksheetJar}

getFile ${McWorksheet}
getFile ${studyMerlinDir}/$chr/chr$chr.dat

#Chunk chromosomes into pieces containing ~2500 markers

${chunkChromosomeBin} \
-d ${studyMerlinDir}/$chr/chr$chr.dat \
-n ${chunkSize} \
-o ${chunkOverlap}

#Create .csv file to be merged with original worksheet
echo "chr,chunk" > ${chunkWorkSheet}
for chr in { 1..22 }
do

	chunks=(chunk*-chr$chr.dat.snps)

	s=${#chunks[*]}

	for c in `seq 1 $s`
	do
		echo $chr,chunk$c-chr$chr.dat.snps >> ${chunkWorkSheet}
	done

done

#Merge worksheets
module load jdk/${javaversion}

#Run Jar to create full worksheet
java -jar ${expandWorksheetJar} ${McWorksheet} ${finalChunksWorksheet} ${chunkWorkSheet} project ${project}

putFile ${finalChunksWorksheet}
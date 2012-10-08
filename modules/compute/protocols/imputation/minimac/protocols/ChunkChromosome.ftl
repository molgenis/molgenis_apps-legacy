#MOLGENIS walltime=48:00:00 nodes=1 cores=1 mem=4

#FOREACH project,chr

getFile ${chunkChromosomeBin}
getFile ${expandWorksheetJar}

getFile ${McWorksheet}
getFile ${studyMerlinChrDat}

#Chunk chromosomes into pieces containing ~2500 markers

cd ${studyMerlinChrDir}

${chunkChromosomeBin} \
-d ${studyMerlinChrDat} \
-n ${chunkSize} \
-o ${chunkOverlap}

returnCode=$?

if [ $returnCode -ne 0 ]
then

	echo -e "\nNon zero return code. Something went wrong creating the chunks\n\n"
	#Return non zero return code
	exit 1

fi

#Create .csv file to be merged with original worksheet
echo "chunk" > ${chunkChrWorkSheet}


chunks=(${studyMerlinChrDir}/chunk*-chr${chr}.dat.snps)

s=${r"${#chunks[*]}"}

for c in `seq 1 $s`
do
	echo $c >> ${chunkChrWorkSheet}
	putFile ${studyMerlinChrDir}/chunk$c-chr${chr}.dat.snps
	putFile ${studyMerlinChrDir}/chunk$c-chr${chr}.dat
done


#Merge worksheets
module load jdk/${javaversion}

#Run Jar to create full worksheet


java -jar ${expandWorksheetJar} ${McWorksheet} ${tmpFinalChunkChrWorksheet} ${chunkChrWorkSheet} project ${project}



if [ $returnCode -eq 0 ]
then
	
	echo -e "\nMoving temp files to final files\n\n"

	mv ${tmpFinalChunkChrWorksheet} ${finalChunkChrWorksheet}

	putFile ${finalChunkChrWorksheet}
	
	
else
  
	echo -e "\nNon zero return code not making files final. Existing temp files are kept for debuging purposes\n\n"
	#Return non zero return code
	exit 1

fi

#Call compute to generate phasing jobs
module load jdk/${javaversion}

mkdir -p ${projectChrPhasingJobsDir}

# Execute MOLGENIS/compute to create job scripts.
sh ${McDir}/molgenis_compute.sh \
-worksheet=${finalChunkChrWorksheet} \
-parameters=${McParameters} \
-workflow=${McProtocols}/../workflowMinimacStage2.csv \
-protocols=${McProtocols}/ \
-templates=${McTemplates}/ \
-scripts=${projectChrPhasingJobsDir}/ \
-id=${McId}

cd ${projectChrPhasingJobsDir}
sh submit.sh

tar czf ${projectChrPhasingJobsDirTarGz} ${projectChrPhasingJobsDir}
putFile ${projectChrPhasingJobsDirTarGz}
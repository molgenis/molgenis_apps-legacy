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
--prefix ${studyChunkChrDir}/chunk${chunk}-chr${chr} \
--rounds ${phasingRounds} \
--states ${phasingStates} \
--phase \
2>&1 | tee -a ${studyChunkChrDir}/chunk${chunk}-chr${chr}-mach.log

###REMOVED --sample ${phasingHaplotypeSampling} from command
###If used files with extension *.sample1, *.sample6, *.sample11, *.sample16 etc are created

if [ $returnCode -eq 0 ]
then
	
	echo -e "\nMoving temp files to final files\n\n"


	putFile ${studyChunkChrDir}/chunk${chunk}-chr${chr}.erate
	putFile ${studyChunkChrDir}/chunk${chunk}-chr${chr}.gz
	putFile ${studyChunkChrDir}/chunk${chunk}-chr${chr}.rec
	putFile ${studyChunkChrDir}/chunk${chunk}-chr${chr}-mach.log
	
	
else
  
	echo -e "\nNon zero return code not making files final. Existing temp files are kept for debuging purposes\n\n"
	#Return non zero return code
	exit 1

fi

<#if autostart == "TRUE">

#Call compute to generate phasing jobs
module load jdk/${javaversion}

mkdir -p ${projectChrImputationJobsDir}

# Execute MOLGENIS/compute to create job scripts.
sh ${McDir}/molgenis_compute.sh \
-worksheet=${finalChunkChrWorksheet} \
-parameters=${McParameters} \
-workflow=${McProtocols}/../workflowMinimacStage3.csv \
-protocols=${McProtocols}/ \
-templates=${McTemplates}/ \
-scripts=${projectChrImputationJobsDir}/ \
-id=${McId}

cd ${projectChrImputationJobsDir}
sh submit.sh

tar czf ${projectChrImputationJobsDirTarGz} ${projectChrImputationJobsDir}
putFile ${projectChrImputationJobsDirTarGz}

</#if>


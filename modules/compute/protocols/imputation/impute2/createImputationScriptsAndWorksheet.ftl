#MOLGENIS walltime=48:00:00 nodes=1 cores=1 mem=4

#FOREACH project

getFile ${expandWorksheetJar}

getFile ${McWorksheet}
getFile ${chrBinsFile}
#putFile ${projectComputeDir}/${project}.chr${chr}.worksheet.csv

inputs "${McWorksheet}"
inputs "${chrBinsFile}"
#alloutputsexist "${projectComputeDir}/${project}.chr${chr}.worksheet.csv"


mkdir -p ${projectTempDir}
mkdir -p ${projectJobsDir}
mkdir -p ${projectChrJobsDir}

module load jdk/${javaversion}

#Run Jar to create full worksheet

<#if imputationPipeline == "impute2">


	java -jar ${expandWorksheetJar} ${McWorksheet} ${projectComputeDir}/${project}.chr${chr}.worksheet.csv ${chrBinsFile} project ${project} 
	
	
	
	# Execute MOLGENIS/compute to create job scripts to analyse this project.
	#
	sh ${McDir}/molgenis_compute.sh \
	-worksheet=${projectComputeDir}/${project}.chr${chr}.worksheet.csv \
	-parameters=${McParameters} \
	-workflow=${McProtocols}/workflowImpute.csv \
	-protocols=${McProtocols}/ \
	-templates=${McTemplates}/ \
	-scripts=${projectChrJobsDir}/ \
	-id=${McId}
	
<#else>

	echo "imputationPipeline ${imputationPipeline} not supported"
	return 1

</#if>
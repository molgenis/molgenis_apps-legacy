#MOLGENIS walltime=48:00:00 nodes=1 cores=1 mem=4

#FOREACH project

getFile ${expandWorksheetJar}

getFile ${McWorksheet}
getFile ${chrBinsFile}
#putFile ${projectComputeDir}/${project}.worksheet.csv

inputs "${McWorksheet}"
inputs "${chrBinsFile}"
#alloutputsexist "${expandedWorksheet}"


mkdir -p ${projectTempDir}
mkdir -p ${projectJobsDir}

module load jdk/${javaversion}

#Run Jar to create full worksheet

<#if imputationPipeline == "impute2">


	java -jar ${expandWorksheetJar} ${McWorksheet} ${expandedWorksheet} ${chrBinsFile} project ${project} 
	
	# Execute MOLGENIS/compute to create job scripts.
	sh ${McDir}/molgenis_compute.sh \
	-worksheet=${expandedWorksheet} \
	-parameters=${McParameters} \
	-workflow=${McProtocols}/workflowImpute.csv \
	-protocols=${McProtocols}/ \
	-templates=${McTemplates}/ \
	-scripts=${projectJobsDir}/ \
	-id=${McId}
	
<#else>

	echo "imputationPipeline ${imputationPipeline} not supported"
	return 1

</#if>
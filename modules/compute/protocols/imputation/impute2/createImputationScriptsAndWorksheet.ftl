#MOLGENIS walltime=48:00:00 nodes=1 cores=1 mem=4


#FOREACH project

####TEMPLATE TO BE FINISHED SOON####

mkdir -p ${projectTempDir}
mkdir -p ${projectJobsDir}

dos2unix ${McWorksheet}

${scriptdir}/extract_samples_from_GAF_list.pl --i ${McWorksheet} --o ${projectTempDir}/${project}.csv --c project --q ${project}



#Run Jar to create full worksheet

<#if imputationPipeline == "impute2">


	java -jar ${expandWorksheetJar} ${projectTempDir}/${project}.csv ${projectComputeDir}/${project}.worksheet.csv ${chrBinsFile} 
	
	
	
	# Execute MOLGENIS/compute to create job scripts to analyse this project.
	#
	sh ${McDir}/molgenis_compute.sh \
	-worksheet=${projectComputeDir}/${project}.worksheet.csv \
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
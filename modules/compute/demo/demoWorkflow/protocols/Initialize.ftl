#FOREACH project

#
##
### Initialization of demoWorkflow
##
#

#
# Remove tmp dir
#
rm -rf ${tempprojectdir}
rm -rf ${projectdir}
#
# Create project dirs.
#
mkdir -p ${tempprojectdir}
mkdir -p ${projectrawarraydatadir}
mkdir -p ${projectrawdatadir}
mkdir -p ${projectJobsDir}
mkdir -p ${projectLogsDir}
mkdir -p ${intermediatedir}
mkdir -p ${projectResultsDir}
mkdir -p ${qcdir}

# Fake concordance check and just paste the faked result in the corresponding directory
cp ../source/externalSampleID.concordance.ngsVSarray.txt ${sampleconcordancefile[0]}

# Copy a db snp file
cp ../source/dbSNP135.tabdelim.table ../root/resources/hg19/dbsnp/

#
# (Added to demoWorkflow) in case the big rawdata dir did not exist yet in 'our' root
# Also create subdirectory for this run
#
mkdir -p ${allRawNgsDataDir}/${runPrefix[0]}

#
# Create symlinks to the raw data required to analyse this project
#
# For each sequence file (could be multiple per sample):
#
<#list internalSampleID as sample>
	
	<#if seqType[sample_index] == "SR">
		
		<#if barcode[sample_index] == "None">
			ln -s ${allRawNgsDataDir}/${runPrefix[sample_index]}/${compressedFastqFilenameSR[sample_index]} ${projectrawdatadir}/${compressedFastqFilenameNoBarcodeSR[sample_index]}
			ln -s ${allRawNgsDataDir}/${runPrefix[sample_index]}/${fastqChecksumFilenameSR[sample_index]} ${projectrawdatadir}/${fastqChecksumFilenameNoBarcodeSR[sample_index]}
			
			# Also add a symlink for the alignment step:
			# ln -s ${allRawNgsDataDir}/${runPrefix[sample_index]}/${compressedFastqFilenamePE1[sample_index]} ${projectrawdatadir}/${compressedFastqFilenameNoBarcodePE1[sample_index]}
		<#else>
			ln -s ${allRawNgsDataDir}/${runPrefix[sample_index]}/${compressedDemultiplexedSampleFastqFilenameSR[sample_index]} ${projectrawdatadir}/
			ln -s ${allRawNgsDataDir}/${runPrefix[sample_index]}/${demultiplexedSampleFastqChecksumFilenameSR} ${projectrawdatadir}/
		</#if>
		
	<#elseif seqType[sample_index] == "PE">
		<#if barcode[sample_index] == "None">
			ln -s ${allRawNgsDataDir}/${runPrefix[sample_index]}/${compressedFastqFilenamePE1[sample_index]} ${projectrawdatadir}/${compressedFastqFilenameNoBarcodePE1[sample_index]}
			ln -s ${allRawNgsDataDir}/${runPrefix[sample_index]}/${compressedFastqFilenamePE2[sample_index]} ${projectrawdatadir}/${compressedFastqFilenameNoBarcodePE2[sample_index]}
			ln -s ${allRawNgsDataDir}/${runPrefix[sample_index]}/${fastqChecksumFilenamePE1[sample_index]} ${projectrawdatadir}/${fastqChecksumFilenameNoBarcodePE1[sample_index]}
			ln -s ${allRawNgsDataDir}/${runPrefix[sample_index]}/${fastqChecksumFilenamePE2[sample_index]} ${projectrawdatadir}/${fastqChecksumFilenameNoBarcodePE2[sample_index]}
		<#else>
			ln -s ${allRawNgsDataDir}/${runPrefix[sample_index]}/${compressedDemultiplexedSampleFastqFilenamePE1[sample_index]} ${projectrawdatadir}/
			ln -s ${allRawNgsDataDir}/${runPrefix[sample_index]}/${compressedDemultiplexedSampleFastqFilenamePE2[sample_index]} ${projectrawdatadir}/
			ln -s ${allRawNgsDataDir}/${runPrefix[sample_index]}/${demultiplexedSampleFastqChecksumFilenamePE1[sample_index]} ${projectrawdatadir}/
			ln -s ${allRawNgsDataDir}/${runPrefix[sample_index]}/${demultiplexedSampleFastqChecksumFilenamePE2[sample_index]} ${projectrawdatadir}/
		</#if>
		
	</#if>
</#list>
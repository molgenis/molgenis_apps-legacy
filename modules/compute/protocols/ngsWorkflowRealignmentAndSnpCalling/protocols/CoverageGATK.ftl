#
# =====================================================
# $Id$
# $URL$
# $LastChangedDate$
# $LastChangedRevision$
# $LastChangedBy$
# =====================================================
#

#MOLGENIS walltime=66:00:00 nodes=1 cores=1 mem=12
#FOREACH externalSampleID

getFile ${mergedbam}
getFile ${mergedbamindex}
getFile ${indexfile}
getFile ${indexfile}.amb
getFile ${indexfile}.ann
getFile ${indexfile}.bwt
getFile ${indexfile}.fai
getFile ${indexfile}.pac
getFile ${indexfile}.rbwt
getFile ${indexfile}.rpac
getFile ${indexfile}.rsa
getFile ${indexfile}.sa

getFile ${cumcoveragescriptgatk}

<#if capturingKit != "None">getFile ${targetintervals}</#if>

module load GATK/${gatkVersion}

#export R_LIBS=${R_LIBS}

java -Djava.io.tmpdir=${tempdir} -Xmx12g -jar \
${genomeAnalysisTKjar} \
-T DepthOfCoverage \
-R ${indexfile} \
-I ${mergedbam} \
-o ${coveragegatk} \
-ct 1 -ct 2 -ct 5 -ct 10 -ct 15 -ct 20 -ct 30 -ct 40 -ct 50<#if capturingKit != "None"> \
-L ${targetintervals}</#if>

#Create coverage graphs for sample
${rscript} ${cumcoveragescriptgatk} \
--in ${coveragegatk}.sample_cumulative_coverage_proportions \
--out ${coveragegatk}.cumulative_coverage.pdf \
--max-depth 100 \
--title "Cumulative coverage ${externalSampleID}"

putFile ${coveragegatk}
putFile ${coveragegatk}.sample_cumulative_coverage_counts
putFile ${coveragegatk}.sample_cumulative_coverage_proportions
putFile ${coveragegatk}.sample_interval_statistics
putFile ${coveragegatk}.sample_interval_summary
putFile ${coveragegatk}.sample_statistics
putFile ${coveragegatk}.sample_summary
putFile ${coveragegatk}.cumulative_coverage.pdf
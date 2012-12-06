#
# =====================================================
# $Id$
# $URL$
# $LastChangedDate$
# $LastChangedRevision$
# $LastChangedBy$
# =====================================================
#

#MOLGENIS walltime=65:59:00 mem=12 cores=1
#FOREACH externalSampleID

##EXPORT R_LIBS AGAIN? #SAME QUESTION AS IN ANALYZE_COVARIATES.FTL

getFile ${mergedbam}
getFile ${mergedbamindex}
getFile ${targetintervals}

getFile ${coveragescript}

module load ${rBin}/${rVersion}

export R_LIBS=${R_LIBS}

Rscript ${coveragescript} \
--bam ${mergedbam} \
--chromosome 1 \
--interval_list ${targetintervals} \
--csv ${sample}.coverage.csv \
--pdf ${samplecoverageplotpdf} \
--Rcovlist ${sample}.coverage.Rdata

putFile ${sample}.coverage.csv
putFile ${samplecoverageplotpdf}
putFile ${sample}.coverage.Rdata
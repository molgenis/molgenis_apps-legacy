#
# =====================================================
# $Id$
# $URL$
# $LastChangedDate$
# $LastChangedRevision$
# $LastChangedBy$
# =====================================================
#

#FOREACH

getFile ${matefixedbam}
getFile ${matefixedbamindex}
getFile ${indexfile}
getFile ${dbsnprod}
getFile ${dbsnprod}.idx

java -jar -Xmx4g \
${genomeAnalysisTKjar} -l INFO \
-T CountCovariates \
-U ALLOW_UNINDEXED_BAM \
-R ${indexfile} \
--DBSNP ${dbsnprod} \
-I ${matefixedbam} \
-cov ReadGroupcovariate \
-cov QualityScoreCovariate \
-cov CycleCovariate \
-cov DinucCovariate \
-recalFile ${matefixedcovariatecsv}

putFile ${matefixedcovariatecsv}
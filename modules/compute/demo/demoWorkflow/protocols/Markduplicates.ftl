#
# =====================================================
# $Id$
# $URL$
# $LastChangedDate$
# $LastChangedRevision$
# $LastChangedBy$
# =====================================================
#

#MOLGENIS walltime=35:59:00 mem=4
#FOREACH

#<#noparse>module load ${picardBin}/${picardVersion}</#noparse>
#module load picard-tools/${picardVersion}
#
##
### Module system may not work locally, so just set path to tool
##
#
PICARD_HOME=${tooldir}/picard-tools-${picardVersion}

getFile ${sortedbam}
getFile ${sortedbamindex}

java -Xmx4g -jar ${markduplicatesjar} \
INPUT=${sortedbam} \
OUTPUT=${dedupbam} \
METRICS_FILE=${dedupmetrics} \
REMOVE_DUPLICATES=false \
ASSUME_SORTED=true \
VALIDATION_STRINGENCY=LENIENT \
TMP_DIR=${tempdir}

java -jar -Xmx3g ${buildbamindexjar} \
INPUT=${dedupbam} \
OUTPUT=${dedupbamindex} \
VALIDATION_STRINGENCY=LENIENT \
MAX_RECORDS_IN_RAM=1000000 \
TMP_DIR=${tempdir}

putFile ${dedupbam}
putFile ${dedupbamindex}
putFile ${dedupmetrics}
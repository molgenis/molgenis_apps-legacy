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

module load picard-tools/${picardVersion}

getFile ${bamfile}

java -jar -Xmx3g SortSam.jar \
INPUT=${bamfile} \
OUTPUT=${sortedbam} \
SORT_ORDER=coordinate \
VALIDATION_STRINGENCY=LENIENT \
MAX_RECORDS_IN_RAM=1000000 \
TMP_DIR=${tempdir}

java -jar -Xmx3g BuildBamIndex.jar \
INPUT=${sortedbam} \
OUTPUT=${sortedbamindex} \
VALIDATION_STRINGENCY=LENIENT \
MAX_RECORDS_IN_RAM=1000000 \
TMP_DIR=${tempdir}

putFile ${sortedbam}
putFile ${sortedbamindex}
#
# =====================================================
# $Id$
# $URL$
# $LastChangedDate$
# $LastChangedRevision$
# $LastChangedBy$
# =====================================================
#

#MOLGENIS walltime=35:59:00 mem=6
#FOREACH

getFile ${realignedbam}

#<#noparse>module load ${picardBin}/${picardVersion}</#noparse>
#
##
### Module system may not work locally, so just set path to tool
##
#
PICARD_HOME=${tooldir}/picard-tools-${picardVersion}

java -jar -Xmx6g \
${fixmateinformationjar} \
INPUT=${realignedbam} \
OUTPUT=${matefixedbam} \
SORT_ORDER=coordinate \
VALIDATION_STRINGENCY=SILENT \
TMP_DIR=${tempdir}

java -jar -Xmx3g ${buildbamindexjar} \
INPUT=${matefixedbam} \
OUTPUT=${matefixedbamindex} \
VALIDATION_STRINGENCY=LENIENT \
MAX_RECORDS_IN_RAM=1000000 \
TMP_DIR=${tempdir}

putFile ${matefixedbam}
putFile ${matefixedbamindex}
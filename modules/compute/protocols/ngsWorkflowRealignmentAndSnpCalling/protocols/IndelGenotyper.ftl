#
# =====================================================
# $Id$
# $URL$
# $LastChangedDate$
# $LastChangedRevision$
# $LastChangedBy$
# =====================================================
#

#MOLGENIS walltime=33:00:00 mem=8
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

java -Xmx8g -jar ${genomeAnalysisTKjar} \
-l INFO \
-T IndelGenotyperV2 \
-I ${mergedbam} \
-o ${indelsvcf} \
--bedOutput ${indelsbed} \
-R ${indexfile} \
-verbose ${indelsverboseoutput} \
--window_size 300

putFile ${indelsvcf}
putFile ${indelsbed}
putFile ${indelsverboseoutput}
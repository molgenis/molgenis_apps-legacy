#
# =====================================================
# $Id$
# $URL$
# $LastChangedDate$
# $LastChangedRevision$
# $LastChangedBy$
# =====================================================
#

#MOLGENIS walltime=46:00:00 mem=8 cores=5
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
getFile ${dbsnprod}
getFile ${dbsnprod}.idx

java -Xmx8g -Djava.io.tmpdir=${tempdir} -XX:+UseParallelGC -XX:ParallelGCThreads=1 -jar \
${genomeAnalysisTKjar} \
-l INFO \
-T UnifiedGenotyper \
-I ${mergedbam} \
--out ${snpsvcf} \
-R ${indexfile} \
-D ${dbsnprod} \
-stand_call_conf 30.0 \
-stand_emit_conf 10.0 \
-nt 4 \
--metrics_file ${snpsvcf}.metrics

putFile ${snpsvcf}
putFile ${snpsvcf}.metrics
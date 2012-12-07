#
# =====================================================
# $Id$
# $URL$
# $LastChangedDate$
# $LastChangedRevision$
# $LastChangedBy$
# =====================================================
#

<#if seqType == "PE">
#MOLGENIS walltime=15:00:00 nodes=1 cores=4 mem=6
#FOREACH

module load bwa/${bwaVersion}

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
getFile ${rightbarcodefqgz}

mkdir -p "${intermediatedir}"

bwa aln \
${indexfile} \
${rightbarcodefqgz} \
-t ${bwaaligncores} \
-f ${rightbwaout}

putFile ${rightbwaout}

</#if>
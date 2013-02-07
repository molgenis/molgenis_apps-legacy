#
# =====================================================
# $Id$
# $URL$
# $LastChangedDate$
# $LastChangedRevision$
# $LastChangedBy$
# =====================================================
#

#MOLGENIS walltime=23:59:00
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
getFile ${leftbwaout}
getFile ${rightbwaout}
getFile ${leftbarcodefqgz}
getFile ${rightbarcodefqgz}

<#if seqType == "PE">bwa sampe -P \<#else>bwa samse \</#if>
-p illumina \
-i ${lane} \
-m ${externalSampleID} \
-l ${library} \
${indexfile} \
${leftbwaout} \
<#if seqType == "PE">${rightbwaout} \
</#if>${leftbarcodefqgz} \
<#if seqType == "PE">${rightbarcodefqgz} \
</#if>-f ${samfile}

putFile ${samfile}
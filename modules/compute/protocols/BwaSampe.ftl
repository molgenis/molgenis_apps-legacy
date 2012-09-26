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
#TARGETS

#EXES bwasampejar

getFile ${indexfile}
getFile ${leftbwaout}
getFile ${rightbwaout}
getFile ${leftbarcodefqgz}
getFile ${rightbarcodefqgz}
alloutputsexist "${samfile}"

<#if seqType == "PE">${bwasampejar} sampe -P \<#else>${bwasampejar} samse \</#if>
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
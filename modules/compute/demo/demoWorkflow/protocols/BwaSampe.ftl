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

#module load bwa/${bwaVersion}
#
##
### Module system may not work locally, so just set path to tool
##
#
PATH=${tooldir}/bwa-${bwaVersion}:$PATH

getFile ${indexfile}
getFile ${leftbwaout}
getFile ${rightbwaout}
getFile ${leftbarcodefqgz}
getFile ${rightbarcodefqgz}
alloutputsexist "${samfile}"

<#if seqType == "PE">bwa sampe -P \<#else>bwa samse \</#if>
-p illumina \
-i ${lane} \
-m ${externalSampleID} \
-l ${library} \
-f ${samfile} \
${indexfile} \
${leftbwaout} <#if seqType == "PE">\</#if>
<#if seqType == "PE">${rightbwaout} \
</#if>${leftbarcodefqgz} \
<#if seqType == "PE">${rightbarcodefqgz}
</#if>

putFile ${samfile}
#
# =====================================================
# $Id$
# $URL$
# $LastChangedDate$
# $LastChangedRevision$
# $LastChangedBy$
# =====================================================
#

#MOLGENIS walltime=08:00:00 nodes=1 cores=1 mem=1

<#if seqType == "SR">
#INPUTS srbarcodefqgz,
#OUTPUTS leftfastqczip
#LOGS log
#EXES fastqcjar
#TARGETS

	inputs "${srbarcodefqgz}"
	alloutputsexist \
	 "${leftfastqczip}" \

<#else>
#INPUTS leftbarcodefqgz,rightbarcodefqgz
#OUTPUTS leftfastqczip,rightfastqczip
#LOGS log
#EXES fastqcjar
#TARGETS

	inputs "${leftbarcodefqgz}"
	inputs "${rightbarcodefqgz}"
	
	alloutputsexist \
	 "${leftfastqczip}" \
	 "${rightfastqczip}" \
</#if>

# first make logdir...
mkdir -p "${intermediatedir}"

# pair1
${fastqcjar} ${leftbarcodefqgz} \
-Djava.io.tmpdir=${tempdir} \
-Dfastqc.output_dir=${intermediatedir} \
-Dfastqc.unzip=false

<#if seqType == "PE">
# pair2
${fastqcjar} ${rightbarcodefqgz} \
-Djava.io.tmpdir=${tempdir} \
-Dfastqc.output_dir=${intermediatedir} \
-Dfastqc.unzip=false
</#if>
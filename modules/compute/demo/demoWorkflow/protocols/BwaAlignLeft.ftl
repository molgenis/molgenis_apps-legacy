#
# =====================================================
# $Id$
# $URL$
# $LastChangedDate$
# $LastChangedRevision$
# $LastChangedBy$
# =====================================================
#

#MOLGENIS walltime=15:00:00 nodes=1 cores=4 mem=6
#TARGETS

#module load bwa/${bwaVersion}
#
##
### Module system may not work locally, so just set path to tool
##
#
PATH=${tooldir}/bwa-${bwaVersion}:$PATH

getFile ${indexfile}
getFile ${leftbarcodefqgz}
alloutputsexist "${leftbwaout}"

mkdir -p "${intermediatedir}"

bwa aln \
-f ${leftbwaout} \
${indexfile} \
${leftbarcodefqgz} \
-t ${bwaaligncores}


putFile ${leftbwaout}
#
# =====================================================
# $Id$
# $URL$
# $LastChangedDate$
# $LastChangedRevision$
# $LastChangedBy$
# =====================================================
#

#MOLGENIS walltime=40:00:00
#FOREACH externalSampleID

getFile ${indelsfilteredbed}

getFile ${makeIndelMaskpyton}

module load ${pythonBin}/${pythonVersion}

python ${makeIndelMaskpyton} \
${indelsfilteredbed} \
10 \
${indelsmaskbed}

putFile ${indelsmaskbed}
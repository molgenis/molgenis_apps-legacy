#
# =====================================================
# $Id$
# $URL$
# $LastChangedDate 28 Jun 2012 $
# $LastChangedRevision$
# $LastChangedBy WBKoetsier$
# =====================================================
#

#MOLGENIS walltime=47:59:00 mem=2 cores=1
#FOREACH run

#Source GCC bash
. ${root}/gcc.bashrc

umask 0007

${gafscripts}/copy_fq_to_rawdatadir.pl \
-rawdatadir ${runIntermediateDir} \
-run ${run} \
-samplecsv ${McWorksheet}
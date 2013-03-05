# Configures the GCC bash environment
. /scratch1/gcc.bashrc

##### BEFORE #####
touch $PBS_O_WORKDIR/s00_RunGerald_0482.out
source /scratch1/tools/scripts/import.sh
before="$(date +%s)"
echo "Begin job s00_RunGerald_0482 at $(date)" >> $PBS_O_WORKDIR/RUNTIME.log

echo Running on node: `hostname`

sleep 60
###### MAIN ######
# This script is processing samples:
# 2048
# 2049
# 2050
# 2051
# 2055
# Serum_0102
# CSF_473
# H2O
# 2047
# 2052
# 2053
# 2054
# 2056
# Serum_0103
# Granulocytes
# H2O
# T07_16098
# T07_17332
# T08_0617
# T06_16822
# T06_0601
# T03_62818
# T02_104821
# T97_13615
# T10_18095
# T09_20721
# T09_04104
# T09_15160
# T08_15966
# T05_15373
# T07_17380
# T09_15289
# T06_10848
# T02_01220
# n_CD4
# Granulocytes
# ctr2_tRNA_0h
# ctr4_RPF_0h
# uc1_tRNA_24h
# ctr2_tRNA_24h
# ctr4_tRNA_2h
# ctr5_tRNA_24h
# UC2_tRNA_24h

#
# =====================================================
# $Id$
# $URL$
# $LastChangedDate$
# $LastChangedRevision$
# $LastChangedBy$
# =====================================================
#

#MOLGENIS walltime=45:59:00 mem=12 cores=8
#FOREACH run

#Source GAF bash
. /scratch1/gaf.bashrc


perl /scratch1/tools/scripts/run_GERALD.pl \
-run 0482 \
-samplecsv /Users/mdijkstra/Documents/work/git/molgenis_apps/modules/compute/demo/run0482/run0482.csv

###### AFTER ######
after="$(date +%s)"
elapsed_seconds="$(expr $after - $before)"
echo Completed s00_RunGerald_0482 at $(date) in $elapsed_seconds seconds >> $PBS_O_WORKDIR/RUNTIME.log
touch $PBS_O_WORKDIR/s00_RunGerald_0482.finished
######## END ########


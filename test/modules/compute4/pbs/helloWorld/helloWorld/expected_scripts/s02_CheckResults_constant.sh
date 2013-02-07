#!/bin/bash
#PBS -N s02_CheckResults_constant
#PBS -q gaf
#PBS -l nodes=1:ppn=1
#PBS -l walltime=20:00:00
#PBS -l mem=4gbgb
#PBS -e s02_CheckResults_constant.err
#PBS -o s02_CheckResults_constant.out
#PBS -W umask=0007



# Configures the GCC bash environment
. /target/gpfs2/gcc/gcc.bashrc

##### BEFORE #####
touch $PBS_O_WORKDIR/s02_CheckResults_constant.out
source /target/gpfs2/gcc/tools/scripts/import.sh
before="$(date +%s)"
echo "Begin job s02_CheckResults_constant at $(date)" >> $PBS_O_WORKDIR/RUNTIME.log

#echo Running on node: `hostname`

sleep 0
###### MAIN ######
#FOREACH constant

d=$(diff -rq ~/test/compute/helloWorld/results ~/test/compute/helloWorld/expected_results)
if [ -z $d ]
then
	touch ~/test/compute/helloWorld/SUCCESS
else
	touch ~/test/compute/helloWorld/FAILURE
fi

# Empty footer

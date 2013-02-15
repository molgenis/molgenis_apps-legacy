#!/bin/bash
#PBS -N s01_OrganizerInvitationStep_child
#PBS -q gaf
#PBS -l nodes=1:ppn=1
#PBS -l walltime=20:00:00
#PBS -l mem=4gbgb
#PBS -e s01_OrganizerInvitationStep_child.err
#PBS -o s01_OrganizerInvitationStep_child.out
#PBS -W umask=0007



# Configures the GCC bash environment
. /target/gpfs2/gcc/gcc.bashrc

##### BEFORE #####
touch $PBS_O_WORKDIR/s01_OrganizerInvitationStep_child.out
source /target/gpfs2/gcc/tools/scripts/import.sh
before="$(date +%s)"
echo "Begin job s01_OrganizerInvitationStep_child at $(date)" >> $PBS_O_WORKDIR/RUNTIME.log

#echo Running on node: `hostname`

sleep 0
###### MAIN ######
#FOREACH group

mkdir -p ~/test/compute/helloWorld/results

echo "Dear Oscar," > ~/test/compute/helloWorld/results/child.txt
echo "Please organize activities for the child group." >> ~/test/compute/helloWorld/results/child.txt
echo "List of guests:" >> ~/test/compute/helloWorld/results/child.txt
	echo "Charly" >> ~/test/compute/helloWorld/results/child.txt
	echo "Cindy" >> ~/test/compute/helloWorld/results/child.txt


# Empty footer

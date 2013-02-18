#!/bin/bash
#PBS -N s01_OrganizerInvitationStep_adult
#PBS -q gaf
#PBS -l nodes=1:ppn=1
#PBS -l walltime=20:00:00
#PBS -l mem=4gbgb
#PBS -e s01_OrganizerInvitationStep_adult.err
#PBS -o s01_OrganizerInvitationStep_adult.out
#PBS -W umask=0007



# Configures the GCC bash environment
. /target/gpfs2/gcc/gcc.bashrc

##### BEFORE #####
touch $PBS_O_WORKDIR/s01_OrganizerInvitationStep_adult.out
source /target/gpfs2/gcc/tools/scripts/import.sh
before="$(date +%s)"
echo "Begin job s01_OrganizerInvitationStep_adult at $(date)" >> $PBS_O_WORKDIR/RUNTIME.log

#echo Running on node: `hostname`

sleep 0
###### MAIN ######
#FOREACH group

mkdir -p ~/test/compute/helloWorld/results

echo "Dear Otto," > ~/test/compute/helloWorld/results/adult.txt
echo "Please organize activities for the adult group." >> ~/test/compute/helloWorld/results/adult.txt
echo "List of guests:" >> ~/test/compute/helloWorld/results/adult.txt
	echo "Abel" >> ~/test/compute/helloWorld/results/adult.txt
	echo "Adam" >> ~/test/compute/helloWorld/results/adult.txt
	echo "Adri" >> ~/test/compute/helloWorld/results/adult.txt


# Empty footer

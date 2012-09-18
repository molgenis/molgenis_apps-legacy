#MOLGENIS walltime=48:00:00 nodes=1 cores=1 mem=4

#INPUTS /target/gpfs2/gcc/tmp/processing/demo/proj001//preparedStudy//chr20.ped,preparedStudyDir/chr20.dat
#OUTPUTS /target/gpfs2/gcc/tmp/processing/demo/proj001//preparedStudy//chr20.gz
#EXES /target/gpfs2/gcc/tools/mach/executables/mach1
#LOGS log
#TARGETS project,chr

#FOREACH project,chr

inputs "/target/gpfs2/gcc/tmp/processing/demo/proj001//preparedStudy//chr20.ped"
inputs "/target/gpfs2/gcc/tmp/processing/demo/proj001//preparedStudy//chr20.dat"
alloutputsexist "/target/gpfs2/gcc/tmp/processing/demo/proj001//preparedStudy//chr20.gz"


#Use mach to phase the study panel. The command should be like
/target/gpfs2/gcc/tools/mach/executables/mach1 -d /target/gpfs2/gcc/tmp/processing/demo/proj001//preparedStudy//chr20.dat -p /target/gpfs2/gcc/tmp/processing/demo/proj001//preparedStudy//chr20.ped --rounds 20 --states 20 --phase --interim 5 --compact --prefix /target/gpfs2/gcc/tmp/processing/demo/proj001//preparedStudy//~chr20

#Example
# ./executables/mach1 -d examples/sample.dat -p examples/sample.ped --rounds 20 --states 20 --phase --interim 5 --compact

#Remove intermediate preliminary data if the previous step was correct
rm /target/gpfs2/gcc/tmp/processing/demo/proj001//preparedStudy//~chr20.prelim*




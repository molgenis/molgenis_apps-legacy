#MOLGENIS walltime=48:00:00 nodes=1 cores=1 mem=4

#INPUTS ${preparedStudyDir}/chr${chr}.dat,${preparedStudyDir}/chr${chr}.ped
#OUTPUTS ${preparedStudyDir}/chr${chr}.bgl
#EXES ${linkage2beagle}
#LOGS log
#TARGETS project,chr

#FOREACH project,chr

inputs "${preparedStudyDir}/chr${chr}.dat"
inputs "${preparedStudyDir}/chr${chr}.ped"
alloutputsexist "${preparedStudyDir}/chr${chr}.bgl"

java -jar ${linkage2beagle} ${preparedStudyDir}/chr${chr}.dat ${preparedStudyDir}/chr${chr}.ped > ${preparedStudyDir}/chr${chr}.bgl

sleep 30


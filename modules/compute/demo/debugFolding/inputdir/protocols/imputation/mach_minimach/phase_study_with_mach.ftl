#MOLGENIS walltime=48:00:00 nodes=1 cores=1 mem=4

#INPUTS preparedStudyDir/chr${chr}.ped,preparedStudyDir/chr${chr}.dat
#OUTPUTS preparedStudyDir/chr${chr}.gz
#EXES machBin
#LOGS log
#TARGETS project,chr

#FOREACH project,chr

inputs "${preparedStudyDir}/chr${chr}.ped"
inputs "${preparedStudyDir}/chr${chr}.dat"
alloutputsexist "${preparedStudyDir}/chr${chr}.gz"


#Use mach to phase the study panel. The command should be like
#TODO: We have to define machBin in parameters.csv
${machBin} -d ${preparedStudyDir}/chr${chr}.dat -p ${preparedStudyDir}/chr${chr}.ped --rounds 20 --states 20 --phase --interim 5 --compact --prefix ${preparedStudyDir}/~chr${chr}

#Example
# ./executables/mach1 -d examples/sample.dat -p examples/sample.ped --rounds 20 --states 20 --phase --interim 5 --compact

#Remove intermediate preliminary data if the previous step was correct
rm ${preparedStudyDir}/~chr${chr}.prelim*

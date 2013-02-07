#MOLGENIS walltime=48:00:00 nodes=1 cores=1 mem=4
#INPUTS ${referencePanel},${preparedStudyDir}/chr${chr}.dat,${preparedStudyDir}/chr${chr}.gz,${preparedStudyDir}/chr${chr}.snps
#OUTPUTS ${preparedStudyDir}/chr${chr}.results
#EXES ${minimacBin}
#LOGS log
#TARGETS project,chr

#FOREACH project,chr

inputs "${referenceVCFFile}"
inputs "${preparedStudyDir}/chr${chr}.gz"
inputs "${preparedStudyDir}/chr${chr}.dat"
alloutputsexist "${preparedStudyDir}/chr${chr}.results"

#Transpose output probabilities file
mkdir -p ${projectTempDir}/transpose_temp
cat ${preparedStudyDir}/chr${chr}.results.prob | java -Djava.io.tmp.dir=${projectTempDir}/transpose_temp -jar ${transpose} > ${preparedStudyDir}/chr${chr}.results.prob.transposed

#Compute Beagle's Allelic R2
python ${calculateBeagleR2ForIMinimacResultsPythonScript} ${preparedStudyDir}/chr${chr}.results.prob.transposed ${preparedStudyDir}/chr${chr}.results.info ${preparedStudyDir}/chr${chr}.results.beagleR2

#MOLGENIS walltime=48:00:00 nodes=1 cores=1 mem=4

#EXES calculateBeagleR2ForIMpute2ResultsPythonScript
#LOGS log

#FOREACH project,chr


for file in $(ls ${imputationResultDir}/chr_${chr})
do
	getFile $file;
done
putFile "${imputationResultDir}/chr_${chr}.beagleR2"

getFile ${calculateBeagleR2ForIMpute2ResultsPythonScript}

inputs "${imputationResultDir}/chr_${chr}"
alloutputsexist "${imputationResultDir}/chr_${chr}.beagleR2"


python ${calculateBeagleR2ForIMpute2ResultsPythonScript} ${imputationResultDir}/chr_${chr} ${imputationResultDir}/chr_${chr}.beagleR2
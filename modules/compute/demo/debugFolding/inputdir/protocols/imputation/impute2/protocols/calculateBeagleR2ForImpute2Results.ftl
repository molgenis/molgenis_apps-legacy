#MOLGENIS walltime=48:00:00 nodes=1 cores=1 mem=4

#FOREACH project,chr

getFile ${calculateBeagleR2ForIMpute2ResultsPythonScript}
getFile ${imputationResultDir}/chr_${chr}

inputs "${imputationResultDir}/chr_${chr}"
alloutputsexist "${imputationResultDir}/chr_${chr}.beagleR2"

module load Python/${pythonversion}

python ${calculateBeagleR2ForIMpute2ResultsPythonScript} ${imputationResultDir}/chr_${chr} ${imputationResultDir}/chr_${chr}.beagleR2

putFile ${imputationResultDir}/chr_${chr}.beagleR2

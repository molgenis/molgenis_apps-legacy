
#MOLGENIS walltime=48:00:00 nodes=1 cores=1 mem=4
#INPUTS imputationResult/chr_${chr}
#OUTPUTS imputationResult/chr_${chr}.beagleR2
#EXES calculateBeagleR2ForIMpute2ResultsPythonScript
#LOGS log
#TARGETS project,chr
#FOREACH project,chr


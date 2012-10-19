#MOLGENIS walltime=48:00:00 nodes=1 cores=1 mem=4

getFile ${impute2ResultsBinsLocation}

inputs "${impute2ResultsBinsLocation}"

python ${tooldir}/python_scripts/AssemblyImpute2GprobsBins.py ${impute2ResultsBinsLocation} 500 ${resultsDir}/OUTPUT.gprobs

putFile ${resultsDir}/OUTPUT.gprobs


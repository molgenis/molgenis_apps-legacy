#MOLGENIS walltime=48:00:00 nodes=1 cores=1 mem=4

getFile ${impute2ResultsBinsLocation}
getFile ${tooldir}/python_scripts/AssemblyImpute2GprobsBins.py

inputs "${impute2ResultsBinsLocation}"

mkdir -p ${resultsDir}
python ${tooldir}/python_scripts/AssemblyImpute2GprobsBins.py ${impute2ResultsBinsLocation} 500 ${chr} ${resultsDir}/OUTPUT.gprobs

putFile ${resultsDir}/OUTPUT.gprobs


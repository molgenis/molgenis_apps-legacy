#MOLGENIS walltime=48:00:00 nodes=1 cores=1 mem=4

getFile ${resultsDir}/OUTPUT.gprobs
getFile ${tooldir}/python_scripts/AssemblyImpute2GprobsBins.py

module load Python/2.7.3
python -V

python ${tooldir}/python_scripts/AssemblyImpute2GprobsBins.py ${gprobsBinsDir} 500 10 ${chr} ${resultsDir}/OUTPUT.gprobs

putFile ${resultsDir}/OUTPUT.tped
putFile ${resultsDir}/OUTPUT.tfam

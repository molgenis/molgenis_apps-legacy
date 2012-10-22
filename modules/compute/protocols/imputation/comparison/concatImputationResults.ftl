#MOLGENIS walltime=48:00:00 nodes=1 cores=1 mem=4

getFile ${impute2ResultsBinsLocation}
getFile ${tooldir}/python_scripts/AssemblyImpute2GprobsBins.py

getFile ${gprobsBinsDir}/chr1_0_499
getFile ${gprobsBinsDir}/chr1_500_999
getFile ${gprobsBinsDir}/chr1_1000_1499
getFile ${gprobsBinsDir}/chr1_1500_1999
getFile ${gprobsBinsDir}/chr1_2000_2499
getFile ${gprobsBinsDir}/chr1_2500_2999
getFile ${gprobsBinsDir}/chr1_3000_3499
getFile ${gprobsBinsDir}/chr1_3500_3999
getFile ${gprobsBinsDir}/chr1_4000_4499
getFile ${gprobsBinsDir}/chr1_4500_4999

inputs "${impute2ResultsBinsLocation}"

module load Python/2.7.3
python -V

mkdir -p ${resultsDir}
python ${tooldir}/python_scripts/AssemblyImpute2GprobsBins.py ${gprobsBinsDir} 500 10 ${chr} ${resultsDir}/OUTPUT.gprobs


putFile ${resultsDir}/OUTPUT.gprobs


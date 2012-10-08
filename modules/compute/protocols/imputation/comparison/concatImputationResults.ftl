#MOLGENIS walltime=48:00:00 nodes=1 cores=1 mem=4



getFile ${referenceImpute2HapFile}
getFile ${referenceImpute2LegendFile}
getFile ${referenceImpute2MapFile}
getFile ${preparedStudyDir}/chr${chr}.gen
putFile ${impute2ResultChrBin}
putFile ${impute2ResultChrBin}_info
putFile ${impute2ResultChrBin}_info_by_sample
putFile ${impute2ResultChrBin}_summary
putFile ${impute2ResultChrBin}_warnings


inputs "${referenceImpute2HapFile}"
inputs "${referenceImpute2LegendFile}"
inputs "${referenceImpute2MapFile}"
inputs "${preparedStudyDir}/chr${chr}.gen"
inputs "${impute2ResultChrBin}"
inputs "${impute2ResultChrBin}_info"
inputs "${impute2ResultChrBin}_info_by_sample"
inputs "${impute2ResultChrBin}_summary"
inputs "${impute2ResultChrBin}_warnings"

module load ${impute}/${impute2Binversion}

python -c 'names = {x[1] : x[2] for x in [line.split() for line in open("all.txt")]}; files = str.join(" ", [names["chr1_1_5000000_samples_" + str(i) + "_" + str(i+500-1)] + " 500" for i in range(0,5000,500)]); print "python AssemblyImpute2GprobsBins.py " + files + " OUTPUT.gprobs"'
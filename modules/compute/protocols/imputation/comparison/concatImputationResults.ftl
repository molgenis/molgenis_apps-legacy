#MOLGENIS walltime=48:00:00 nodes=1 cores=1 mem=4

getFile ${impute2ResultsBinsLocation}

putFile OUTPUT.gprobs


inputs "${impute2ResultsBinsLocation}"

`python -c 'names = {x[1] : x[2] for x in [line.split() for line in open("${impute2ResultsBinsLocation}")]}; files = str.join(" ", [names["chr${chr}_1_5000000_samples_" + str(i) + "_" + str(i+500-1)] + " 500" for i in range(0,5000,500)]); print "python AssemblyImpute2GprobsBins.py " + files + " OUTPUT.gprobs"'`

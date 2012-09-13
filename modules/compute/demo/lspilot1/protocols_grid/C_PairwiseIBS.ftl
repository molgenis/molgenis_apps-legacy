#MOLGENIS walltime=00:45:00

inputs "${filehandlePairwiseString}"

${plink} --noweb --silent --bfile ${filehandleUpdateSexString} --extract ${filehandlePairwiseString}.prune.in --genome --out ${filehandleGenomeString}
 
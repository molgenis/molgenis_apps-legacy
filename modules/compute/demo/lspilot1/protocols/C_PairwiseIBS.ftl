#MOLGENIS walltime=00:45:00

module load plink

getFile ${filehandleUpdateSexString}.bed
getFile ${filehandleUpdateSexString}.sexcheck
getFile ${filehandleUpdateSexString}.nof
getFile ${filehandleUpdateSexString}.hh
getFile ${filehandleUpdateSexString}.fam
getFile ${filehandleUpdateSexString}.bim
getFile ${filehandleUpdateSexString}.nosex
getFile ${filehandlePairwiseString}.prune.in

${plink} --noweb --silent --bfile ${filehandleUpdateSexString} --extract ${filehandlePairwiseString}.prune.in --genome --out ${filehandleGenomeString}

putFile ${filehandleGenomeString}.genome
putFile ${filehandleGenomeString}.hh
putFile ${filehandleGenomeString}.log
putFile ${filehandleGenomeString}.nosex 
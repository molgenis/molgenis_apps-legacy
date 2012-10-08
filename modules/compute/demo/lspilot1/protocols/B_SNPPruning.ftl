#MOLGENIS walltime=00:45:00

module load plink

getFile ${filehandleUpdateSexString}.bed
getFile ${filehandleUpdateSexString}.bim
getFile ${filehandleUpdateSexString}.fam
getFile ${filehandleUpdateSexString}.hh
getFile ${filehandleUpdateSexString}.nof
getFile ${filehandleUpdateSexString}.nosex
getFile ${filehandleUpdateSexString}.sexcheck

${plink} --noweb --silent --bfile ${filehandleUpdateSexString} --indep-pairwise ${snpWindowN} ${snpIntervalM} ${rSquaredThreshold} --out ${filehandlePairwiseString}
 
putFile ${filehandlePairwiseString}.hh
putFile ${filehandlePairwiseString}.nof
putFile ${filehandlePairwiseString}.prune.in
putFile ${filehandlePairwiseString}.log
putFile ${filehandlePairwiseString}.nosex
putFile ${filehandlePairwiseString}.prune.out
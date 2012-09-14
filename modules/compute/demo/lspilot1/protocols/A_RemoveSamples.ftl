#MOLGENIS walltime=00:45:00

module load plink

getFile ${filehandleUpdateSexString}.bed
getFile ${filehandleUpdateSexString}.bim
getFile ${filehandleUpdateSexString}.fam
getFile ${filehandleUpdateSexString}.hh
getFile ${filehandleUpdateSexString}.nof
getFile ${filehandleUpdateSexString}.nosex
getFile ${filehandleUpdateSexString}.sexcheck
getFile ${filehandleFilterString}

${plink} --noweb --silent --bfile ${filehandleUpdateSexString} --remove ${filehandleFilterString}  --out ${filehandleRemoveSampleString}  
 
putFile ${filehandleRemoveSampleString}.hh
putFile ${filehandleRemoveSampleString}.log
putFile ${filehandleRemoveSampleString}.nof
putFile ${filehandleRemoveSampleString}.nosex
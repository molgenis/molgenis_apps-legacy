#MOLGENIS walltime=00:45:00

module load plink

getFile ${filehandleUpdateSexString}.bed
getFile ${filehandleUpdateSexString}.sexcheck
getFile ${filehandleUpdateSexString}.nof
getFile ${filehandleUpdateSexString}.hh
getFile ${filehandleUpdateSexString}.fam
getFile ${filehandleUpdateSexString}.bim
getFile ${filehandleUpdateSexString}.nosex

${plink} --noweb --bfile ${filehandleUpdateSexString} --missing --out ${filehandleMissingString}  

putFile ${filehandleMissingString}.hh
putFile ${filehandleMissingString}.imiss
putFile ${filehandleMissingString}.lmiss
putFile ${filehandleMissingString}.nof
putFile ${filehandleMissingString}.nosex
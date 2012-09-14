#MOLGENIS walltime=00:45:00

module load plink

getFile ${filehandleUpdateSexString}.bed
getFile ${filehandleUpdateSexString}.sexcheck
getFile ${filehandleUpdateSexString}.nof
getFile ${filehandleUpdateSexString}.hh
getFile ${filehandleUpdateSexString}.fam
getFile ${filehandleUpdateSexString}.bim
getFile ${filehandleUpdateSexString}.nosex

${plink} --noweb --bfile ${filehandleUpdateSexString} --het --out ${filehandleHeterozygosityString}

putFile ${filehandleHeterozygosityString}.het
putFile ${filehandleHeterozygosityString}.nof
putFile ${filehandleHeterozygosityString}.hh
putFile ${filehandleHeterozygosityString}.nosex
#MOLGENIS walltime=00:45:00

module load plink

getFile ${filehandleUpdateSexString}.bed
getFile ${filehandleUpdateSexString}.sexcheck
getFile ${filehandleUpdateSexString}.nof
getFile ${filehandleUpdateSexString}.hh
getFile ${filehandleUpdateSexString}.fam
getFile ${filehandleUpdateSexString}.bim
getFile ${filehandleUpdateSexString}.nosex

${plink} --noweb --silent --bfile ${filehandleUpdateSexString} --me ${familyError} ${snpError} --make-bed --out ${filehandleMendelString}

putFile ${filehandleMendelString}.bed
putFile ${filehandleMendelString}.bim
putFile ${filehandleMendelString}.fam
putFile ${filehandleMendelString}.hh
putFile ${filehandleMendelString}.log
putFile ${filehandleMendelString}.nof
putFile ${filehandleMendelString}.nosex
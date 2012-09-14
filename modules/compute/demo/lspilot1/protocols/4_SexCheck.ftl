#MOLGENIS walltime=00:45:00

module load plink

getFile ${filehandleUpdateParentsString}.bed
getFile ${filehandleUpdateParentsString}.nof
getFile ${filehandleUpdateParentsString}.hh
getFile ${filehandleUpdateParentsString}.fam
getFile ${filehandleUpdateParentsString}.bim
getFile ${filehandleUpdateParentsString}.nosex

${plink} --noweb --bfile ${filehandleUpdateParentsString} --check-sex --make-bed --out ${filehandleSexCheckString} 

putFile ${filehandleSexCheckString}.bed
putFile ${filehandleSexCheckString}.sexcheck
putFile ${filehandleSexCheckString}.nof
putFile ${filehandleSexCheckString}.hh
putFile ${filehandleSexCheckString}.fam
putFile ${filehandleSexCheckString}.bim
putFile ${filehandleSexCheckString}.nosex

 
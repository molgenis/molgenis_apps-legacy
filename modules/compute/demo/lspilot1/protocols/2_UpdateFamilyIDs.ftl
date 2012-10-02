#MOLGENIS walltime=00:45:00

module load plink

getFile ${filehandleRemoveString}.bed
getFile ${filehandleRemoveString}.nof
getFile ${filehandleRemoveString}.hh
getFile ${filehandleRemoveString}.fam
getFile ${filehandleRemoveString}.bim
getFile ${filehandleRemoveString}.nosex
getFile ${familyUpdateFile}

${plink} --noweb --bfile ${filehandleRemoveString} --update-ids ${familyUpdateFile} --make-bed --out ${filehandleUpdateIdsString}  

putFile ${filehandleUpdateIdsString}.bed
putFile ${filehandleUpdateIdsString}.nof
putFile ${filehandleUpdateIdsString}.hh
putFile ${filehandleUpdateIdsString}.fam
putFile ${filehandleUpdateIdsString}.bim
putFile ${filehandleUpdateIdsString}.nosex

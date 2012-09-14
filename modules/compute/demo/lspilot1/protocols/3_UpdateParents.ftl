#MOLGENIS walltime=00:45:00

module load plink

getFile ${parentsUpdateFile}
getFile ${filehandleUpdateIdsString}.bed
getFile ${filehandleUpdateIdsString}.nof
getFile ${filehandleUpdateIdsString}.hh
getFile ${filehandleUpdateIdsString}.fam
getFile ${filehandleUpdateIdsString}.bim
getFile ${filehandleUpdateIdsString}.nosex

${plink} --noweb --bfile ${filehandleUpdateIdsString} --update-parents ${parentsUpdateFile} --make-bed --out ${filehandleUpdateParentsString}  

putFile ${filehandleUpdateParentsString}.bed
putFile ${filehandleUpdateParentsString}.nof
putFile ${filehandleUpdateParentsString}.hh
putFile ${filehandleUpdateParentsString}.fam
putFile ${filehandleUpdateParentsString}.bim
putFile ${filehandleUpdateParentsString}.nosex 
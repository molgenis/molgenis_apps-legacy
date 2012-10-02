#MOLGENIS walltime=00:45:00

module load plink

getFile ${bedFile} 
getFile ${bimFile}
getFile ${famFile}
getFile ${badSampleFile}

${plink} --noweb --bed ${bedFile} --bim ${bimFile} --fam ${famFile} --remove ${badSampleFile} --make-bed --out ${filehandleRemoveString}

putFile ${filehandleRemoveString}.bed
putFile ${filehandleRemoveString}.nof
putFile ${filehandleRemoveString}.hh
putFile ${filehandleRemoveString}.fam
putFile ${filehandleRemoveString}.bim
putFile ${filehandleRemoveString}.nosex
#MOLGENIS walltime=00:45:00 mem=2GB nodes=1 cores=2

source ${bashAPI}

login ${token}

getFile ${pedFile}
getFile ${mapFile}
getFile ${phenoInputFile}

#run Plink 
${plink} --noweb --file ${geno} --pheno ${phenoInputFile} --assoc --maf 0.05 --hwe -0.001 --1 --allow-no-sex --out ${phenoResultFile}

putFile ${phenoResultFile}
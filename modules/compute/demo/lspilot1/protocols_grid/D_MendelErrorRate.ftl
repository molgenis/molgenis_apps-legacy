#MOLGENIS walltime=00:45:00

inputs "${filehandleGenomeString}"

${plink} --noweb --silent --bfile ${filehandleUpdateSexString} --me ${familyError} ${snpError} --make-bed --out ${filehandleMendelString}
 
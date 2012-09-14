#MOLGENIS walltime=00:45:00

#module load plink

#This step should automatically correct the sexes of the samples.
#Officially we'd run the following command, however, the UpdateSexFile is missing. Therefore, this step (5) fails.
#${plink} --noweb --silent --bfile ${filehandleUpdateParentsString} --update-sex ${filehandleSexCheckString} --make-bed --out ${filehandleUpdateSexString}

#To fix this, one can combine steps 4 and 5 by the following command:
#/target/gpfs2/gcc/tools/plink-1.07-x86_64/plink --noweb --silent --bfile out3  --impute-sex --make-bed --out out5

#However, the sexes are predicted wrongly in most cases (checked manually). Therefore, we skip this step.   
 
# Empty header#MOLGENIS walltime=00:45:00 mem=2GB nodes=1 cores=2

source xqtl.org/api/bash

login theToken

getFile $HOME/workdir/llrp_geno.ped
getFile $HOME/workdir/llrp_geno.map
getFile $HOME/workdir/llrp_pheno_Health19.txt

#run Plink 
plink --noweb --file $HOME/workdir/llrp_geno --pheno $HOME/workdir/llrp_pheno_Health19.txt --assoc --maf 0.05 --hwe -0.001 --1 --allow-no-sex --out $HOME/workdir/results_for_Health19

putFile $HOME/workdir/results_for_Health19

# Empty footer

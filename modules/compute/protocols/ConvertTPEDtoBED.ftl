#MOLGENIS walltime=48:00:00 nodes=1 cores=1 mem=4


module load plink/1.07-x86_64

getFile {plinkInput}.tped
getFile {plinkInput}.tfam

plink --tfile {plinkInput} --recode --make-bed --noweb --out {plinkOutput}

putFile {plinkOutput}.bed
putFile {plinkOutput}.bim
putFile {plinkOutput}.fam


#MOLGENIS walltime=48:00:00 nodes=1 cores=1 mem=4

#FOREACH project,chr

getFile ${imputationResultDir}/chr_${chr}.map
getFile ${hg19ToHg18chainfile}
getFile ${imputationResultDir}/chr_${chr}.ped
putFile ${convertB36ChrHg19Bed}
putFile ${convertB36ChrHg18Bed}
putFile ${convertB36unmapped}
putFile ${b36conversionPedMapResultDir}/chr_${chr}.map
putFile ${b36conversionPedMapResultDir}/chr_${chr}.ped
putFile ${convertB36unmappedSNPs}

inputs "${imputationResultDir}/chr_${chr}.map"
inputs "${hg19ToHg18chainfile}"
inputs "${imputationResultDir}/chr_${chr}.ped"
alloutputsexist "${convertB36ChrHg19Bed}"
alloutputsexist "${convertB36ChrHg18Bed}"
alloutputsexist "${convertB36unmapped}"
alloutputsexist "${b36conversionPedMapResultDir}/chr_${chr}.map"
alloutputsexist "${b36conversionPedMapResultDir}/chr_${chr}.ped"
alloutputsexist "${convertB36unmappedSNPs}"

#module load ${liftOverUcscBin}/${liftOverUcscBinversion}
#module load ${plinkseqBin}/${plinkseqversion}

mkdir -p ${convertB36ChrTempDir}

#Create hg19 bed file from map file
awk '{$5=$2;$2=$4;$3=$4+1;$1="chr"$1;print $1,$2,$3,$5}' OFS="\t" ${imputationResultDir}/chr_${chr}.map \
> ${convertB36ChrHg19Bed}

#Lift over hg19 bed to hg18 bed
${liftOverUcscBin} \
-bedPlus=4 \
${convertB36ChrHg19Bed} \
${hg19ToHg18chainfile} \
${convertB36ChrHg18Bed} \
${convertB36unmapped}

#Select SNP ID from non-lifted over SNPs
awk '/^[^#]/ {print $4}' \
${convertB36unmapped} \
> ${convertB36unmappedSNPs}

mkdir -p ${b36conversionPedMapResultDir}

#Generate new Ped/Map file excluding SNPs which were NOT lifted over
${plinkseqBin} \
--noweb \
--recode \
--ped ${imputationResultDir}/chr_${chr}.ped \
--map ${imputationResultDir}/chr_${chr}.map \
--out ${b36conversionPedMapResultDir}/chr_${chr} \
--exclude ${convertB36unmappedSNPs} \
--missing-genotype N

#Create new map file from hg18 bed
awk '{sub("chr","",$1);print $1,$4,0,$2}' OFS="\t" \
${convertB36ChrHg18Bed} \
> ${b36conversionPedMapResultDir}/chr_${chr}.map

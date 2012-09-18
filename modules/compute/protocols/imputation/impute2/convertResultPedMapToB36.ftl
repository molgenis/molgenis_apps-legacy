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

inputs "${imputationResultDir}/chr_${chr}.map"
inputs "${hg19ToHg18chainfile}"
inputs "${imputationResultDir}/chr_${chr}.ped"
alloutputsexist "${convertB36ChrHg19Bed}"
alloutputsexist "${convertB36ChrHg18Bed}"
alloutputsexist "${convertB36unmapped}"
alloutputsexist "${b36conversionPedMapResultDir}/chr_${chr}.map"
alloutputsexist "${b36conversionPedMapResultDir}/chr_${chr}.ped"

#module load ${liftOverUcscBin}/${liftOverUcscBinversion}

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

mkdir -p ${b36conversionPedMapResultDir}

#Create new map file from hg18 bed
awk '{sub("chr","",$1);print $1,$4,0,$2}' OFS="\t" ${convertB36ChrHg18Bed} \
> ${b36conversionPedMapResultDir}/chr_${chr}.map

#Create symlink from hg19 ped to "new" hg18 ped
ln -s ${imputationResultDir}/chr_${chr}.ped \
${b36conversionPedMapResultDir}/chr_${chr}.ped
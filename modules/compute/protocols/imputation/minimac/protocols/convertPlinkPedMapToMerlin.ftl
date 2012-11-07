#MOLGENIS walltime=05:00:00 nodes=1 cores=1 mem=4

#FOREACH project,chr

getFile ${preparedStudyDir}/chr${chr}.map
getFile ${preparedStudyDir}/chr${chr}.ped
getFile ${studyPedMapChr}.ped

inputs ${preparedStudyDir}/chr${chr}.map
inputs ${preparedStudyDir}/chr${chr}.ped
inputs ${studyPedMapChr}.ped


alloutputsexist \
"${studyMerlinChrMap}" \
"${studyMerlinChrDat}" \
"${studyMerlinChrPed}"

mkdir -p ${studyMerlinChrDir}

#Convert SNP ID to chr_pos and remove 3e column to adhere to merlin
gawk '
	BEGIN {$1="CHROMOSOME";$2="MARKER";$3="POSITION";print $0}
	{$2=$1":"$4;print $1,$2,$4}
' OFS="\t" ${preparedStudyDir}/chr${chr}.map > ${studyMerlinChrMap}

gawk 'BEGIN {print "T","pheno";}{print "M",$1":"$4}' ${preparedStudyDir}/chr${chr}.map > ${studyMerlinChrDat}

set +o posix

#Create merlin ped from genotypes outputed by imputation tool but use fam id, sample id and phenodata from original pedmap
paste <(awk '{print $1,$2,$3,$4,$5,$6}' ${studyPedMapChr}.ped) <(awk '{for(i=7;i<NF;i++) $(i-6) = $i;print $0}' ${preparedStudyDir}/chr${chr}.ped) -d ' ' > ${studyMerlinChrPed}

putFile ${studyMerlinChrMap}
putFile ${studyMerlinChrDat}
putFile ${studyMerlinChrPed}



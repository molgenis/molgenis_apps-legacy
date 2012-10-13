#MOLGENIS walltime=05:00:00 nodes=1 cores=1 mem=4

#FOREACH project,chr

getFile ${preparedStudyDir}/chr${chr}.map
getFile ${preparedStudyDir}/chr${chr}.ped

inputs ${preparedStudyDir}/chr${chr}.map
inputs ${preparedStudyDir}/chr${chr}.ped


alloutputsexist \
"${studyMerlinChrMap}" \
"${studyMerlinChrDat}" \
"${studyMerlinChrPed}"

mkdir -p ${studyMerlinChrDir}

#Conver SNP ID to chr_pos and remove 3e column to adhere to merlin
gawk '
	BEGIN {$1="CHROMOSOME";$2="MARKER";$3="POSITION";print $0}
	{$2=$1":"$4;print $1,$2,$4}
' OFS="\t" ${preparedStudyDir}/chr${chr}.map > ${studyMerlinChrMap}

gawk 'BEGIN {print "T","pheno";}{print "M",$1":"$4}' ${preparedStudyDir}/chr${chr}.map > ${studyMerlinChrDat}

#Copy ped file
cp  ${preparedStudyDir}/chr${chr}.ped ${studyMerlinChrPed}



putFile ${studyMerlinChrMap}
putFile ${studyMerlinChrDat}
putFile ${studyMerlinChrPed}



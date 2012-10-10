#MOLGENIS walltime=05:00:00 nodes=1 cores=1 mem=4

#FOREACH project,chr

getFile ${studyPedMapChr}.map
getFile ${studyPedMapChr}.ped

inputs ${studyPedMapChr}.map
inputs ${studyPedMapChr}.ped


alloutputsexist \
"${studyMerlinChrMap}" \
"${studyMerlinChrDat}" \
"${studyMerlinChrPed}"

mkdir -p ${studyMerlinChrDir}

#Conver SNP ID to chr_pos and remove 3e column to adhere to merlin
gawk '
	BEGIN {$1="CHROMOSOME";$2="MARKER";$3="POSITION";print $0}
	{$2=$1":"$4;print $1,$2,$4}
' OFS="\t" ${studyPedMapChr}.map > ${studyMerlinChrMap}

gawk 'BEGIN {print "T","pheno";}{print "M",$1":"$4}' ${studyPedMapChr}.map > ${studyMerlinChrDat}

#Copy ped file
cp  ${studyPedMapChr}.ped ${studyMerlinChrPed}



putFile ${studyMerlinChrMap}
putFile ${studyMerlinChrDat}
putFile ${studyMerlinChrPed}



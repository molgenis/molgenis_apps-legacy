#MOLGENIS walltime=05:00:00 nodes=1 cores=1 mem=4GB

#FOREACH project,chr

getFile ${studyInputPedMapChr}.map
getFile ${studyInputPedMapChr}.ped

inputs "${studyInputPedMapChr}.map"
inputs "${studyInputPedMapChr}.ped"
alloutputsexist \
	"${studyPedMapChr}.map" \
	"${studyPedMapChr}.ped"

mkdir -p ${studyPedMapChrDir}

#Convert SNPids to chr_pos
awk '{$2=$1"_"$4; print $0}' ${studyInputPedMapChr}.map > ${studyPedMapChr}.map

#Copy ped file
cp ${studyInputPedMapChr}.ped ${studyPedMapChr}.ped

putFile ${studyPedMapChr}.map
putFile ${studyPedMapChr}.ped

#MOLGENIS walltime=24:00:00 nodes=1 cores=1 mem=4

known_haps_g=${known_haps_g}
m=${m}
h=${h}
l=${l}
additonalImpute2Param=${additonalImpute2Param}
chr=${chr}
fromChrPos=${fromChrPos}
toChrPos=${toChrPos}
imputationIntermediatesFolder=${imputationIntermediatesFolder}
impute2Bin=${impute2Bin}

<#noparse>

tmpOuput="${imputationIntermediatesFolder}/~chr${chr}_${fromChrPos}-${toChrPos}"
finalOutput="${imputationIntermediatesFolder}/chr${chr}_${fromChrPos}-${toChrPos}"

inputs $m
inputs $h
inputs $l

alloutputsexist \
	"${finalOutput}" \
	"${finalOutput}_info" \
	"${finalOutput}_info_by_sample" \
	"${finalOutput}_summary" \
	"${finalOutput}_warnings"

mkdir -p $imputationIntermediatesFolder

$impute2Bin \
	-known_haps_g $known_haps_g \
	-m $m \
	-h $h \
	-l $l \
	-k_hap $k_hap \
	-int $fromChrPos $toChrPos \
	-o $tmpOuput \
	$additonalImpute2Param
		
#Get return code from last program call
returnCode=$?

if [ $returnCode -eq 0 ]
then

	#If there are no SNPs in this bin we will create empty files 
	if [ ! -f ${tmpOuput}_info ]
	then
	
		echo "Touching file: ${tmpOuput}"
		echo "Touching file: ${tmpOuput}_info"
		echo "Touching file: ${tmpOuput}_info_by_sample"
	
		touch ${tmpOuput}
		touch ${tmpOuput}_info
		touch ${tmpOuput}_info_by_sample
	
	fi
	
		
	
	echo -e "\nMoving temp files to final files\n\n"

	for tempFile in ${tmpOuput}* ; do
		finalFile=`echo $tempFile | sed -e "s/~//g"`
		mv $tempFile $finalFile
	done
	

else
  
	echo -e "\nNon zero return code not making files final. Existing temp files are kept for debugging purposes\n\n"
	#Return non zero return code
	exit 1

fi


</#noparse>
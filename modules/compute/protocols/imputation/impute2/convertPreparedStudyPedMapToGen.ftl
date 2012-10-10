#MOLGENIS walltime=48:00:00 nodes=1 cores=1 mem=4

#FOREACH project,chr

getFile ${preparedStudyDir}/chr${chr}.ped
getFile ${preparedStudyDir}/chr${chr}.map
putFile ${preparedStudyDir}/chr${chr}.gen
putFile ${preparedStudyDir}/chr${chr}.sample



inputs "${preparedStudyDir}/chr${chr}.ped"
inputs "${preparedStudyDir}/chr${chr}.map"
alloutputsexist "${preparedStudyDir}/chr${chr}.gen" \
"${preparedStudyDir}/chr${chr}.sample"


module load ${gtoolBin}/${gtoolBinversion}

${gtoolBin} -P \
--ped ${preparedStudyDir}/chr${chr}.ped \
--map ${preparedStudyDir}/chr${chr}.map \
--og ${preparedStudyDir}/~chr${chr}.gen

awk '
    BEGIN { print "ID_1 ID_2 missing sex phenotype"; print "0 0 0 D P"}    
    {print $2,$1,"0",$5,$6}
' OFS=" " ${preparedStudyDir}/chr${chr}.ped \
> ${preparedStudyDir}/~chr${chr}.sample


#Get return code from last program call
returnCode=$?

if [ $returnCode -eq 0 ]
then
	
	echo -e "\nMoving temp files to final files\n\n"

	mv ${preparedStudyDir}/~chr${chr}.gen ${preparedStudyDir}/chr${chr}.gen
	mv ${preparedStudyDir}/~chr${chr}.sample ${preparedStudyDir}/chr${chr}.sample

	
else
  
	echo -e "\nNon zero return code not making files final. Existing temp files are kept for debuging purposes\n\n"
	#Return non zero return code
	exit 1

fi
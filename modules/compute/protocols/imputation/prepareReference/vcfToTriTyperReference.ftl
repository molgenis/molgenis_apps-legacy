





#MOLGENIS walltime=01:00:00 nodes=1 cores=1 mem=1

inputs ${chrVcfInputFile}
alloutputsexist ${chrVcfReferenceFile}

mkdir -p ${vcfReferenceFolder}


java -jar ~/Programs/ConvertVcfToTriTyper.jar ./vcf/ ./TriTyper/


if [ $returnCode -eq 0 ]
then
	
	echo -e "\nMoving temp files to final files\n\n"
	mv ${chrVcfReferenceFileTemp} ${chrVcfReferenceFile}
	
else
  
	echo -e "\nNon zero return code not making files final. Existing temp files are kept for debuging purposes\n\n"
	#Return non zero return code
	exit 1

fi
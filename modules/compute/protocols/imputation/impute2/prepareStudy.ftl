#MOLGENIS walltime=48:00:00 nodes=1 cores=1 mem=8

#INPUTS studyTriTyperChrDir,referenceTriTyperDir
#OUTPUTS preparedStudyDir
#EXES imputationToolJar
#LOGS log

#FOREACH project,chr


getFile ${studyTriTyperChrDir}/GenotypeMatrix.dat
getFile ${studyTriTyperChrDir}/Individuals.txt
getFile ${studyTriTyperChrDir}/PhenotypeInformation.txt
getFile ${studyTriTyperChrDir}/SNPMappings.txt
getFile ${studyTriTyperChrDir}/SNPs.txt
getFile ${referenceTriTyperDir}/GenotypeMatrix.dat
getFile ${referenceTriTyperDir}/Individuals.txt
getFile ${referenceTriTyperDir}/PhenotypeInformation.txt
getFile ${referenceTriTyperDir}/SNPMappings.txt
getFile ${referenceTriTyperDir}/SNPs.txt

for file in $(ls ${preparedStudyTempDir})
do
putFile $file;
done


inputs ${studyTriTyperChrDir}/GenotypeMatrix.dat
inputs ${studyTriTyperChrDir}/Individuals.txt
inputs ${studyTriTyperChrDir}/PhenotypeInformation.txt
inputs ${studyTriTyperChrDir}/SNPMappings.txt
inputs ${studyTriTyperChrDir}/SNPs.txt
inputs ${referenceTriTyperDir}/GenotypeMatrix.dat
inputs ${referenceTriTyperDir}/Individuals.txt
inputs ${referenceTriTyperDir}/PhenotypeInformation.txt
inputs ${referenceTriTyperDir}/SNPMappings.txt
inputs ${referenceTriTyperDir}/SNPs.txt


#module load ${imputationToolJar}/${imputationToolJarversion}

mkdir ${preparedStudyTempDir}


java -Xmx16g -jar ${imputationToolJar} \
--mode ttpmh \
--in ${studyTriTyperChrDir} \
--hap ${referenceTriTyperDir} \
--out ${preparedStudyTempDir}


#Get return code from last program call
returnCode=$?

if [ $returnCode -eq 0 ]
then
	
	echo -e "\nMoving temp files to final files\n\n"

	mv ${preparedStudyTempDir} ${preparedStudyDir}

	
else
  
	echo -e "\nNon zero return code not making files final. Existing temp files are kept for debuging purposes\n\n"
	#Return non zero return code
	exit 1

fi
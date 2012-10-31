#MOLGENIS walltime=05:00:00 nodes=1 cores=1 mem=4

#FOREACH project,chr

getFile "${imputationToolJar}"
getFile "${imputationToolJsciCoreJar}"
getFile "${imputationToolGeneticaLibrariesJar}"

getFile "${studyPedMapChr}.map"
getFile "${studyPedMapChr}.ped"
putFile "${studyTriTyperChrDir}/GenotypeMatrix.dat"
putFile "${studyTriTyperChrDir}/Individuals.txt"
putFile "${studyTriTyperChrDir}/PhenotypeInformation.txt"
putFile "${studyTriTyperChrDir}/SNPMappings.txt"
putFile "${studyTriTyperChrDir}/SNPsHash.txt"
putFile "${studyTriTyperChrDir}/SNPs.txt"


inputs "${studyPedMapChr}.map"
inputs "${studyPedMapChr}.ped"
alloutputsexist "${studyTriTyperChrDir}/GenotypeMatrix.dat" \
"${studyTriTyperChrDir}/Individuals.txt" \
"${studyTriTyperChrDir}/PhenotypeInformation.txt" \
"${studyTriTyperChrDir}/SNPMappings.txt" \
"${studyTriTyperChrDir}/SNPsHash.txt" \
"${studyTriTyperChrDir}/SNPs.txt"


if [ -d ${studyTriTyperChrDir} ]
then
	rm -r ${studyTriTyperChrDir}
fi


module load jdk/${javaversion}

mkdir -p ${studyTriTyperChrTempDir}

java -jar ${imputationToolJar} \
--mode pmtt \
--in ${studyPedMapChrDir} \
--out ${studyTriTyperChrTempDir}


#Get return code from last program call
returnCode=$?

if [ $returnCode -eq 0 ]
then
	
	echo -e "\nMoving temp files to final files\n\n"

	mv ${studyTriTyperChrTempDir} ${studyTriTyperChrDir}

	
else
  
	echo -e "\nNon zero return code not making files final. Existing temp files are kept for debuging purposes\n\n"
	#Return non zero return code
	exit 1

fi
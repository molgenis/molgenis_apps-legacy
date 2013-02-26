

#MOLGENIS walltime=96:00:00 nodes=1 cores=1 mem=4

#FOREACH project,chr

getFile ${resultDir}/qc_1/chr${chr}.ped
getFile ${resultDir}/qc_1/chr${chr}.map

mkdir -p ${resultDir}/pca

echo "
genotypename:    ${resultDir}/qc_1/chr${chr}.ped
snpname:         ${resultDir}/qc_1/chr${chr}.map
indivname:       ${resultDir}/qc_1/chr${chr}.ped
outputformat:    EIGENSTRAT
genotypeoutname: combined.eigenstratgeno
snpoutname:      combined.snp
indivoutname:    combined.ind
familynames:     NO
"

${convertf} -p param.txt

alloutputsexist \
  ${resultDir}/qc_1/chr${chr}.ped \
  ${resultDir}/qc_1/chr${chr}.map

# For explenation of these parameters see: http://pngu.mgh.harvard.edu/~purcell/plink/thresh.shtml
${plink} --file ${studyInputDir}/chr${chr} \
	--mind ${plink_mind} \
	--geno ${plink_geno} \
	--maf ${plink_maf} \
	--hwe ${plink_hwe} \
	--me ${plink_me1} ${plink_me2} \
	--recode --noweb \
	--out ${resultDir}/~qc_1/chr${chr}

#Get return code from last program call
returnCode=$?

if [ $returnCode -eq 0 ]
then
	mv ${resultDir}/~qc_1/chr${chr}.ped ${resultDir}/qc_1/chr${chr}.ped
	mv ${resultDir}/~qc_1/chr${chr}.map ${resultDir}/qc_1/chr${chr}.map

	putFile ${resultDir}/qc_1/chr${chr}.ped
	putFile ${resultDir}/qc_1/chr${chr}.map
	
else
  
	echo -e "\nNon zero return code not making files final. Existing temp files are kept for debuging purposes\n\n"
	#Return non zero return code
	exit 1

fi

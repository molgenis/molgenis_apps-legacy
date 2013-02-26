
#MOLGENIS walltime=96:00:00 nodes=1 cores=1 mem=4

#FOREACH project,chr

getFile ${resultDir}/qc_1/qc.ped
getFile ${resultDir}/qc_1/qc.map

mkdir -p ${resultDir}/pca

#Creating parameters file
echo "
genotypename:    ${resultDir}/qc_1/qc.ped
snpname:         ${resultDir}/qc_1/qc.map
indivname:       ${resultDir}/qc_1/qc.ped
outputformat:    EIGENSTRAT
genotypeoutname: ${resultDir}/pca/combined.eigenstratgeno
snpoutname:      ${resultDir}/pca/combined.snp
indivoutname:    ${resultDir}/pca/combined.ind
familynames:     NO
" > ${resultDir}/pca/param.txt

#Convert from ped / map to eigen
${convertf} -p ${resultDir}/pca/param.txt

#Do the PCA
${smartpca_perl} \
    -i ${resultDir}/pca/combined.eigenstratgeno \
    -a ${resultDir}/pca/combined.snp \
    -b ${resultDir}/pca/combined.ind \
    -k 10 \
    -o ${resultDir}/pca/combinedPca.pca \
    -p ${resultDir}/pca/combinedPca.plot \
    -e ${resultDir}/pca/combinedPca.eval \
    -l ${resultDir}/pca/combinedPca.log \
    -m 0 \
    -t 10 \
    -s 6 \
    -w ${resultDir}/pca/1000gGonlPopulation.txt

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

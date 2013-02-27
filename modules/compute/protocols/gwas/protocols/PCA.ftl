#MOLGENIS walltime=96:00:00 nodes=1 cores=1 mem=4

#FOREACH project

getFile ${resultDir}/prunning/merged.ped
getFile ${resultDir}/prunning/merged.map

#PATH=$PATH:${tooldir}/EIG/

mkdir -p ${resultDir}/pca

#Creating parameters file
echo "
genotypename:    ${resultDir}/prunning/merged.ped
snpname:         ${resultDir}/prunning/merged.map
indivname:       ${resultDir}/prunning/merged.ped
outputformat:    EIGENSTRAT
genotypeoutname: ${resultDir}/pca/~combined.eigenstratgeno
snpoutname:      ${resultDir}/pca/~combined.snp
indivoutname:    ${resultDir}/pca/~combined.ind
familynames:     NO
" > ${resultDir}/pca/param.txt

#Convert from ped / map to eigen
${convertf} -p ${resultDir}/pca/param.txt

alloutputsexist \
  ${resultDir}/pca/combinedPca.pca \
  ${resultDir}/pca/combinedPca.plot \
  ${resultDir}/pca/combinedPca.eval \
  ${resultDir}/pca/combinedPca.log


#Do the PCA
${smartpca_perl} \
    -i ${resultDir}/pca/~combined.eigenstratgeno \
    -a ${resultDir}/pca/~combined.snp \
    -b ${resultDir}/pca/~combined.ind \
    -k 10 \
    -o ${resultDir}/pca/~combinedPca.pca \
    -p ${resultDir}/pca/~combinedPca.plot \
    -e ${resultDir}/pca/~combinedPca.eval \
    -l ${resultDir}/pca/~combinedPca.log \
    -m 0 \
    -t 10 \
    -s 6 

#Get return code from last program call
returnCode=$?

if [ $returnCode -eq 0 ]
then
	mv ${resultDir}/pca/~combined.eigenstratgeno ${resultDir}/pca/combined.eigenstratgeno
	mv ${resultDir}/pca/~combined.snp ${resultDir}/pca/combined.snp
	mv ${resultDir}/pca/~combined.ind ${resultDir}/pca/combined.ind
	mv ${resultDir}/pca/~combinedPca.pca ${resultDir}/pca/combinedPca.pca
	mv ${resultDir}/pca/~combinedPca.plot ${resultDir}/pca/combinedPca.plot
	mv ${resultDir}/pca/~combinedPca.eval ${resultDir}/pca/combinedPca.eval
	mv ${resultDir}/pca/~combinedPca.log ${resultDir}/pca/combinedPca.log

	putFile ${resultDir}/pca/combined.eigenstratgeno
	putFile ${resultDir}/pca/combined.snp
	putFile ${resultDir}/pca/combined.ind
	putFile ${resultDir}/pca/combinedPca.pca
	putFile ${resultDir}/pca/combinedPca.plot
	putFile ${resultDir}/pca/combinedPca.eval
	putFile ${resultDir}/pca/combinedPca.log
	
else
  
	echo -e "\nNon zero return code not making files final. Existing temp files are kept for debuging purposes\n\n"
	#Return non zero return code
	exit 1

fi

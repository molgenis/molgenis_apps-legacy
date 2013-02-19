#MOLGENIS walltime=96:00:00 nodes=1 cores=1 mem=4

#FOREACH project,chr,chrChunk

#Construct getFile script
ls -1 ${imputationResultDir}/~chunk${chrChunk}-chr${chr}_sampleChunk*.imputed.prob.transposed | python -c 'import sys; print str.join(" ", ["getFile " + x for x in sorted(sys.stdin.readlines(), key=lambda y:int(y[y.find("sampleChunk") + 11:y.find(".imputed.prob")]))])' > ${imputationResultDir}/~fetch_trasposed_chunk${chrChunk}-chr${chr}.sh

#Execute script and fetch transposed files
sh ${imputationResultDir}/~fetch_trasposed_chunk${chrChunk}-chr${chr}.sh

#Construct script to compute quality metrics

ls -1 /target/gpfs2/gcc/groups/gonl/projects/imputationBenchmarking/imputationResult/lifelines_MinimacV2_refGoNLv4/chunk9-chr2_sampleChunk*.imputed.prob | python -c 'import sys; print str.join(" ", [x.replace("\n", "") for x in sorted(sys.stdin.readlines(), key=lambda y:int(y[y.find("sampleChunk") + 11:y.find(".imputed.prob")]))])'


getFile ${imputationResultDir}/~chunk${chrChunk}-chr${chr}_sampleChunk${sampleChunk}.imputed.prob


#Transpose the probs file
{python_exec} ${transpose_script} ${imputationResultDir}/~chunk${chrChunk}-chr${chr}_sampleChunk${sampleChunk}.imputed.prob ${imputationResultDir}/~chunk${chrChunk}-chr${chr}_sampleChunk${sampleChunk}.imputed.prob.transposed

#Get return code from last program call
returnCode=$?

if [ $returnCode -eq 0 ]
then
    mv ${imputationResultDir}/~chunk${chrChunk}-chr${chr}_sampleChunk${sampleChunk}.imputed.prob.transposed ${imputationResultDir}/chunk${chrChunk}-chr${chr}_sampleChunk${sampleChunk}.imputed.prob.transposed

    putFile ${imputationResultDir}/chunk${chrChunk}-chr${chr}_sampleChunk${sampleChunk}.imputed.prob.transposed
else

  echo -e "\nNon zero return code not making files final. Existing temp files are kept for debuging purposes\n\n"
  #Return non zero return code
	exit 1

fi

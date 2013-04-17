#MOLGENIS walltime=18:00:00 nodes=1 cores=4 mem=40
#FOREACH sampleID

#module load abyss/<#noparse>${abyssVersion}</#noparse>

export PATH=/target/gpfs2/gcc/tools/ABySS/bin/:<#noparse>${PATH}</#noparse>

<#noparse>getFile "${leftbarcodefqgz}"
getFile "${rightbarcodefqgz}"</#noparse>
alloutputsexist "${abyssContigs}"

# first make logdir...
#mkdir -p "${intermediatedir}"
mkdir -p "${assemblyResultDir}"


cd ${assemblyResultDir}; \
abyss-pe \
np=4 \
k=${kmer} \
name=${assemblyResultDir}/ABySS_k$k \
lib='${ssvQuoted(library)}' \
mp='${ssvQuoted(library)}' \
<#list library as lib>
${lib}="<#list lane as L>${sequencingStartDate}_${sequencer}_${run}_${flowcell}_L${L}_${barcode}_1.fq.gz ${sequencingStartDate}_${sequencer}_${run}_${flowcell}_L${L}_${barcode}_2.fa \
</#list>"
</#list>
2>&1 | tee -a ${assemblyResultDir}/ABySS_k$k_runtime.log; \


# TODO: use putFile to move all output dir contents.
#putFile ${abyss_results}


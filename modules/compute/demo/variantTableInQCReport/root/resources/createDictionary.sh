java -jar /target/gpfs2/gcc/tools/picard-tools-1.61/CreateSequenceDictionary.jar R=simulatedReferenceGenome.fa O=simulatedReferenceGenome.dict
perl -pi -e 's/unsorted/coordinate/g' simulatedReferenceGenome.dict

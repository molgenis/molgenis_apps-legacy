# Source functions for data transfer to cluster
source dataTransfer.sh

source /Users/mdijkstra/Documents/work/git/molgenis_apps/modules/compute/demo/demoWorkflow/root/tools/scripts/import.sh
#
##
### Index the reference genome, create dictionary, bed files and interval lists, and create fasta index
##
#

getFile /Users/mdijkstra/Documents/work/git/molgenis_apps/modules/compute/demo/demoWorkflow/root/resources/hg19/indices/simulatedReferenceGenome.fa

# Remove files that will be created (append is used)
rm -f /Users/mdijkstra/Documents/work/git/molgenis_apps/modules/compute/demo/demoWorkflow/root/resources/hg19/intervals/simulated_targets_hg19_simulatedReferenceGenome.bed
rm -f /Users/mdijkstra/Documents/work/git/molgenis_apps/modules/compute/demo/demoWorkflow/root/resources/hg19/intervals/simulated_baits_hg19_simulatedReferenceGenome.bed

# Set paths
PATH=/Users/mdijkstra/Documents/work/git/molgenis_apps/modules/compute/demo/demoWorkflow/root/tools/bwa-0.5.8c_patched:$PATH
PICARD_HOME=/Users/mdijkstra/Documents/work/git/molgenis_apps/modules/compute/demo/demoWorkflow/root/tools/picard-tools-1.61

# Index reference genome
bwa index -a is /Users/mdijkstra/Documents/work/git/molgenis_apps/modules/compute/demo/demoWorkflow/root/resources/hg19/indices/simulatedReferenceGenome.fa

# Create dictionary
java -jar $PICARD_HOME/CreateSequenceDictionary.jar R=/Users/mdijkstra/Documents/work/git/molgenis_apps/modules/compute/demo/demoWorkflow/root/resources/hg19/indices/simulatedReferenceGenome.fa O=/Users/mdijkstra/Documents/work/git/molgenis_apps/modules/compute/demo/demoWorkflow/root/resources/hg19/indices/simulatedReferenceGenome.dict
perl -pi -e 's/unsorted/coordinate/g' /Users/mdijkstra/Documents/work/git/molgenis_apps/modules/compute/demo/demoWorkflow/root/resources/hg19/indices/simulatedReferenceGenome.dict

# Create bed files
echo "1\t1\t50\t+\ttarget1" >> /Users/mdijkstra/Documents/work/git/molgenis_apps/modules/compute/demo/demoWorkflow/root/resources/hg19/intervals/simulated_targets_hg19_simulatedReferenceGenome.bed
echo "2\t1\t50\t+\ttarget2" >> /Users/mdijkstra/Documents/work/git/molgenis_apps/modules/compute/demo/demoWorkflow/root/resources/hg19/intervals/simulated_targets_hg19_simulatedReferenceGenome.bed
echo "1\t1\t50\t+\tbait1" >> /Users/mdijkstra/Documents/work/git/molgenis_apps/modules/compute/demo/demoWorkflow/root/resources/hg19/intervals/simulated_baits_hg19_simulatedReferenceGenome.bed
echo "2\t1\t50\t+\tbait2" >> /Users/mdijkstra/Documents/work/git/molgenis_apps/modules/compute/demo/demoWorkflow/root/resources/hg19/intervals/simulated_baits_hg19_simulatedReferenceGenome.bed

# Create interval files
cat /Users/mdijkstra/Documents/work/git/molgenis_apps/modules/compute/demo/demoWorkflow/root/resources/hg19/indices/simulatedReferenceGenome.dict /Users/mdijkstra/Documents/work/git/molgenis_apps/modules/compute/demo/demoWorkflow/root/resources/hg19/intervals/simulated_targets_hg19_simulatedReferenceGenome.bed > /Users/mdijkstra/Documents/work/git/molgenis_apps/modules/compute/demo/demoWorkflow/root/resources/hg19/intervals/simulated_targets.interval_list
cat /Users/mdijkstra/Documents/work/git/molgenis_apps/modules/compute/demo/demoWorkflow/root/resources/hg19/indices/simulatedReferenceGenome.dict /Users/mdijkstra/Documents/work/git/molgenis_apps/modules/compute/demo/demoWorkflow/root/resources/hg19/intervals/simulated_baits_hg19_simulatedReferenceGenome.bed > /Users/mdijkstra/Documents/work/git/molgenis_apps/modules/compute/demo/demoWorkflow/root/resources/hg19/intervals/simulated_baits.interval_list

# Create fasta index
/Users/mdijkstra/Documents/work/git/molgenis_apps/modules/compute/demo/demoWorkflow/root/tools/samtools-0.1.18/samtools faidx /Users/mdijkstra/Documents/work/git/molgenis_apps/modules/compute/demo/demoWorkflow/root/resources/hg19/indices/simulatedReferenceGenome.fa


# Empty footer


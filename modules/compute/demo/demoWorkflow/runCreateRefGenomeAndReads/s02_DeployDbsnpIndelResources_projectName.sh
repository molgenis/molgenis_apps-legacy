# Source functions for data transfer to cluster
source dataTransfer.sh

source /Users/mdijkstra/Documents/work/git/molgenis_apps/modules/compute/demo/demoWorkflow/root/tools/scripts/import.sh
#FOREACH project


#
##
### Deploy resources demoWorkflow
##
#

# Create empty dbsnp / rod file 
rm -rf /Users/mdijkstra/Documents/work/git/molgenis_apps/modules/compute/demo/demoWorkflow/root/resources/hg19/dbsnp/
mkdir -p /Users/mdijkstra/Documents/work/git/molgenis_apps/modules/compute/demo/demoWorkflow/root/resources/hg19/dbsnp/
touch /Users/mdijkstra/Documents/work/git/molgenis_apps/modules/compute/demo/demoWorkflow/root/resources/hg19/dbsnp/dbsnp_129_b37_human_g1k_v37.rod

# Same for indels
rm -rf /Users/mdijkstra/Documents/work/git/molgenis_apps/modules/compute/demo/demoWorkflow/root/resources/hg19/indels/
mkdir /Users/mdijkstra/Documents/work/git/molgenis_apps/modules/compute/demo/demoWorkflow/root/resources/hg19/indels/
cp ../source/1kg_pilot_release_merged_indels_sites_hg19_human_g1k_v37.vcf_header /Users/mdijkstra/Documents/work/git/molgenis_apps/modules/compute/demo/demoWorkflow/root/resources/hg19/indels/1kg_pilot_release_merged_indels_sites_hg19_human_g1k_v37.vcf


# Empty footer


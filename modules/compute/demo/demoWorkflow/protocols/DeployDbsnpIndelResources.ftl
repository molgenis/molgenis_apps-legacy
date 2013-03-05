#FOREACH project


#
##
### Deploy resources demoWorkflow
##
#

# Create empty dbsnp / rod file 
rm -rf ${resdir}/${genome}/dbsnp/
mkdir -p ${resdir}/${genome}/dbsnp/
touch ${dbsnprod}

# Same for indels
rm -rf ${resdir}/${genome}/indels/
mkdir ${resdir}/${genome}/indels/
cp ../source/1kg_pilot_release_merged_indels_sites_hg19_human_g1k_v37.vcf_header ${pilot1KgVcf}

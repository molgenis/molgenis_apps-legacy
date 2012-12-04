#                                                                                                                       
# A script to make testing of MOLGENIS/COMPUTE easier
#                                                                                                                       
# =====================================================                                                                 
# $Id$                                            
# $URL: http://www.bbmriwiki.nl/svn/ngs_scripts/trunk/extract_samples_from_GAF_list.pl $                                
# $LastChangedDate$                                                      
# $LastChangedRevision$                                                                                           
# $LastChangedBy: mdijkstra $                                                                                           
# =====================================================                                                                 
#

sh $(dirname -- "$0")/molgenis_compute-f6e477a/molgenis_compute.sh \
-worksheet=/Users/mdijkstra/Documents/work/git/molgenis_apps/modules/compute/demo/run0482/run0482.csv \
-parameters=/Users/mdijkstra/Documents/work/git/molgenis_apps/modules/compute/workflows/in-house_parameters_pipeline.target.rug.nl.csv \
-workflow=/Users/mdijkstra/Documents/work/git/molgenis_apps/modules/compute/workflows/in-house_workflow_data_archiving.csv \
-protocols=/Users/mdijkstra/Documents/work/git/molgenis_apps/modules/compute/protocols \
-scripts=$(dirname -- "$0")/generatedscripts \
-templates=/Users/mdijkstra/Documents/work/git/molgenis_apps/modules/compute/protocols \
-id=run01
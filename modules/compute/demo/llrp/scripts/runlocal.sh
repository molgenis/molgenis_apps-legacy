DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
touch $DIR/llrp_workflow_csv.started
export PBS_O_WORKDIR=${DIR}
echo Starting with s00_1_PlinkPhenotypeAnalysis_1...
sh s00_1_PlinkPhenotypeAnalysis_1.sh
#Dependencies: 


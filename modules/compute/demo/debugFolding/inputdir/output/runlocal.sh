DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

touch $DIR/workflow_csv.started
export PBS_O_WORKDIR=${DIR}
echo Starting with s00_QCReport_SCA_B_lane6_testcompute4...
sh s00_QCReport_SCA_B_lane6_testcompute4.sh
#Dependencies: 


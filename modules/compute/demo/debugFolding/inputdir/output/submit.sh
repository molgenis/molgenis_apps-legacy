DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
touch $DIR/workflow_csv.started

#s00_QCReport_SCA_B_lane6_testcompute4
s00_QCReport_SCA_B_lane6_testcompute4=$(qsub -N s00_QCReport_SCA_B_lane6_testcompute4 s00_QCReport_SCA_B_lane6_testcompute4.sh)
echo $s00_QCReport_SCA_B_lane6_testcompute4
sleep 0

touch $DIR/workflow_csv.finished
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
touch $DIR/llrp_workflow_csv.started

#s00_1_PlinkPhenotypeAnalysis_1
s00_1_PlinkPhenotypeAnalysis_1=$(qsub -N s00_1_PlinkPhenotypeAnalysis_1 s00_1_PlinkPhenotypeAnalysis_1.sh)
echo $s00_1_PlinkPhenotypeAnalysis_1
sleep 8

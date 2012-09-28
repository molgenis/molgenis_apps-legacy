DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
touch $DIR/workflow_csv.started
#s00_impute2_s1_1
s00_impute2_s1_1=$(qsub -N s00_impute2_s1_1  s00_impute2_s1_1.sh)
echo $s00_impute2_s1_1
sleep 8
#s01_impute2_s2_1
s01_impute2_s2_1=$(qsub -N s01_impute2_s2_1 -W depend=afterok:$s00_impute2_s1_proj001_20 s01_impute2_s2_1.sh)
echo $s01_impute2_s2_1
sleep 8

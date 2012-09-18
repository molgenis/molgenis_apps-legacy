DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
touch $DIR/workflow_csv.started
export PBS_O_WORKDIR=${DIR}
echo Starting with s00_impute2_s1_1...
sh s00_impute2_s1_1.sh
#Dependencies: 

echo Starting with s01_impute2_s2_1...
sh s01_impute2_s2_1.sh
#Dependencies: -W depend=afterok:$s00_impute2_s1_proj001_20


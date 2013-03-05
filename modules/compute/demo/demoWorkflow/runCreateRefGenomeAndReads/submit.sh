DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
touch $DIR/workflow_CreateAndDeployResources_csv.started

#s00_GenerateReference_1
s00_GenerateReference_1=$(qsub -N s00_GenerateReference_1 s00_GenerateReference_1.sh)
echo $s00_GenerateReference_1
sleep 0
#s01_IndexReference_1
s01_IndexReference_1=$(qsub -N s01_IndexReference_1 -W depend=afterok:$s00_GenerateReference_1 s01_IndexReference_1.sh)
echo $s01_IndexReference_1
sleep 0
#s02_DeployDbsnpIndelResources_projectName
s02_DeployDbsnpIndelResources_projectName=$(qsub -N s02_DeployDbsnpIndelResources_projectName s02_DeployDbsnpIndelResources_projectName.sh)
echo $s02_DeployDbsnpIndelResources_projectName
sleep 0

touch $DIR/workflow_CreateAndDeployResources_csv.finished
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

touch $DIR/workflow_CreateAndDeployResources_csv.started
export PBS_O_WORKDIR=${DIR}
echo Starting with s00_GenerateReference_1...
sh s00_GenerateReference_1.sh
#Dependencies: 

echo Starting with s01_IndexReference_1...
sh s01_IndexReference_1.sh
#Dependencies: 

echo Starting with s02_DeployDbsnpIndelResources_projectName...
sh s02_DeployDbsnpIndelResources_projectName.sh
#Dependencies: 


DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
touch $DIR/in-house_workflow_data_archiving_csv.started
export PBS_O_WORKDIR=${DIR}
echo Starting with s00_RunGerald_0482...
sh s00_RunGerald_0482.sh
#Dependencies: 

echo Starting with s01_CopyFqToRawdatadir_0482...
sh s01_CopyFqToRawdatadir_0482.sh
#Dependencies: -W depend=afterok:$s00_RunGerald_0482

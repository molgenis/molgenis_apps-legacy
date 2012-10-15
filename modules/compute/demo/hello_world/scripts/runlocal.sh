DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
touch $DIR/workflow_csv.started
export PBS_O_WORKDIR=${DIR}
echo Starting with s00_HelloWorld_1...
sh s00_HelloWorld_1.sh
#Dependencies: 

echo Starting with s00_HelloWorld_2...
sh s00_HelloWorld_2.sh
#Dependencies: 

echo Starting with s00_HelloWorld_3...
sh s00_HelloWorld_3.sh
#Dependencies: 

echo Starting with s00_HelloWorld_4...
sh s00_HelloWorld_4.sh
#Dependencies: 

echo Starting with s00_HelloWorld_5...
sh s00_HelloWorld_5.sh
#Dependencies: 

echo Starting with s00_HelloWorld_6...
sh s00_HelloWorld_6.sh
#Dependencies: 

echo Starting with s00_HelloWorld_7...
sh s00_HelloWorld_7.sh
#Dependencies: 

echo Starting with s00_HelloWorld_8...
sh s00_HelloWorld_8.sh
#Dependencies: 

echo Starting with s01_BundleGreetings_1...
sh s01_BundleGreetings_1.sh
#Dependencies: -W depend=afterok:$s00_HelloWorld_4:$s00_HelloWorld_3:$s00_HelloWorld_1:$s00_HelloWorld_2

echo Starting with s01_BundleGreetings_2...
sh s01_BundleGreetings_2.sh
#Dependencies: -W depend=afterok:$s00_HelloWorld_8:$s00_HelloWorld_7:$s00_HelloWorld_6:$s00_HelloWorld_5


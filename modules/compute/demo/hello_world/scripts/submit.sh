DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
touch $DIR/workflow_csv.started
#s00_HelloWorld_1
s00_HelloWorld_1=$(qsub -N s00_HelloWorld_1  s00_HelloWorld_1.sh)
echo $s00_HelloWorld_1
sleep 8
#s00_HelloWorld_2
s00_HelloWorld_2=$(qsub -N s00_HelloWorld_2  s00_HelloWorld_2.sh)
echo $s00_HelloWorld_2
sleep 8
#s00_HelloWorld_3
s00_HelloWorld_3=$(qsub -N s00_HelloWorld_3  s00_HelloWorld_3.sh)
echo $s00_HelloWorld_3
sleep 8
#s00_HelloWorld_4
s00_HelloWorld_4=$(qsub -N s00_HelloWorld_4  s00_HelloWorld_4.sh)
echo $s00_HelloWorld_4
sleep 8
#s00_HelloWorld_5
s00_HelloWorld_5=$(qsub -N s00_HelloWorld_5  s00_HelloWorld_5.sh)
echo $s00_HelloWorld_5
sleep 8
#s00_HelloWorld_6
s00_HelloWorld_6=$(qsub -N s00_HelloWorld_6  s00_HelloWorld_6.sh)
echo $s00_HelloWorld_6
sleep 8
#s00_HelloWorld_7
s00_HelloWorld_7=$(qsub -N s00_HelloWorld_7  s00_HelloWorld_7.sh)
echo $s00_HelloWorld_7
sleep 8
#s00_HelloWorld_8
s00_HelloWorld_8=$(qsub -N s00_HelloWorld_8  s00_HelloWorld_8.sh)
echo $s00_HelloWorld_8
sleep 8
#s01_BundleGreetings_1
s01_BundleGreetings_1=$(qsub -N s01_BundleGreetings_1 -W depend=afterok:$s00_HelloWorld_4:$s00_HelloWorld_3:$s00_HelloWorld_1:$s00_HelloWorld_2 s01_BundleGreetings_1.sh)
echo $s01_BundleGreetings_1
sleep 8
#s01_BundleGreetings_2
s01_BundleGreetings_2=$(qsub -N s01_BundleGreetings_2 -W depend=afterok:$s00_HelloWorld_8:$s00_HelloWorld_7:$s00_HelloWorld_6:$s00_HelloWorld_5 s01_BundleGreetings_2.sh)
echo $s01_BundleGreetings_2
sleep 8

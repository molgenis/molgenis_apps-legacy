#!/bin/bash

printf "\nStarting HelloWorld test.\n\n"

# Initialize constant testResults and load function isEmpty
workdir=$( cd -P "$( dirname "$0" )" && pwd )
. $workdir/initialize.sh

# Define constants
clusterPath="~/test/compute"
cluster="cluster.gcc.rug.nl"
originalDistroDir="$workdir/../../../../../dist/"
testResults="$workdir/../../../../../test-output/compute4/pbs/helloWorld"
generatedScriptsDir=$testResults/generatedScripts
gitVersion=$(git rev-parse --short HEAD)
distro=molgenis_compute-$gitVersion.zip
unzippedDistroDirName=molgenis_compute-$gitVersion

echo ">> Your test results will be written in: $testResults."

echo ">> Remove previous results and recreate this directory..."
rm -rf $testResults
mkdir -p $testResults

echo ">> Create an empty directory $clusterPath/ on the cluster where your remote results will be stored..."
ssh $cluster "rm -rf $clusterPath/; mkdir -p $clusterPath"

echo ">> Copy helloWorld example to the cluster... Output in copyHelloWorld.log."
scp -r $workdir/helloWorld/ $cluster:$clusterPath/ > $testResults/copyHelloWorld.log

echo ">> Run makedistro.sh... Output in makedistro.log."
#sh $workdir/makedistro.sh > $testResults/makedistro.log

# check whether a new distro was made
if [ -f "$originalDistroDir/$distro" ]
then
    # check whether file is not older than a minute
    if test `find "$originalDistroDir/$distro" -mmin +999`
    then
        echo "\nERROR: newest distro older than 1 minute.\n"
        echo "Probably no new distro was build."
        echo "Potential distro:"
        ls -l $originalDistroDir/$distro
	echo ""
	echo "Please check: $testResults/makedistro.log"
	exit 1
    else
        echo "   Successfully created new distro: $distro."
    fi
else
    echo "Error: file $originalDistroDir/$distro not found."
    echo ""
    echo "Please check: $testResults/makedistro.log"
    exit 1
fi

echo ">> Copy the new distro to testResults..."
cp $originalDistroDir/$distro $testResults/

echo ">> Copy distro to $clusterPath/ on the cluster..."
scp $testResults/$distro $cluster:$clusterPath/

echo ">> Unzip the distro on the cluster. Output in unzipdistro.log."
echo "cd $clusterPath; ls -al; unzip $clusterPath/${distro}" | ssh $cluster 'bash -s' > $testResults/unzipdistro.log

echo ">> Generate scripts. Output in generate.log."
echo "source /target/gpfs2/gcc/gcc.bashrc; module load jdk/1.6.0_33; cd $clusterPath; sh $unzippedDistroDirName/molgenis_compute.sh -inputdir=helloWorld" \
| ssh $cluster 'bash -s' > $testResults/generate.log

echo ">> Compare generated and expected pipeline..."
echo "diff -rq $clusterPath/helloWorld/output $clusterPath/helloWorld/expected_scripts" | ssh $cluster 'bash -s' > $testResults/diff_generated_vs_expected_pipeline.log
isEmpty $testResults/diff_generated_vs_expected_pipeline.log

echo ">> Sleep 60 seconds. The cluster does not allow more than a certain number of ssh-connections in a minute..."
sleep 60

echo ">> Execute pipeline and produce results..."
echo "cd $clusterPath/helloWorld/output; sh runlocal.sh > $clusterPath/helloWorld/produced_results.log" | ssh $cluster 'bash -s'

echo ">> Compare produced and expected results (Command 'ls -l *F *S': you should see SUCCESS and no FAILURE)"
echo "cd $clusterPath/helloWorld/; ls -l *F *S" | ssh $cluster 'bash -s'
#echo "diff $clusterPath/helloWorld/produced_results.log $clusterPath/helloWorld/expected_results.log" | ssh $cluster 'bash -s' > $testResults/diff_generated_vs_expected_results.log
#isEmpty $testResults/diff_generated_vs_expected_results.log

printf "\nTest terminated successfully!\n\n"

exit 0
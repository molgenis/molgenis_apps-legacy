#!/bin/bash

# Define constants
root="~/test/compute"
cluster="cluster.gcc.rug.nl"

# Define function that fails if file (first argument) does not exist or is non-empty.
function isEmpty(){
    # check whether file exists
    if [ -f "$1" ]
    then
        # check whether file is empty
        if [ -s "$1" ]
        then
            echo "   Error: comparison failed. Please check file '$1'."
            exit 1
        else
            echo "   Successful; they are equal."
        fi
    else
        echo "Error: file $1 not found."
	exit 1
    fi
}

echo "\nStarting HelloWorld test.\n"
echo ">> Remove previous results..."
sh removeResults.sh

echo ">> Run makedistro.sh and copy distro to $testdir directory... Output in makedistro.log."
sh makedistro.sh > makedistro.log

echo ">> Check whether we successfully made a new distro..."
newdistro=`find ../../../../../dist/ -type f -exec stat -f "%m %N" {} \; | sort -n | tail -1 | cut -f2- -d" "`

if test `find "$newdistro" -mmin +2`
then
    echo "\nERROR: newest distro older than 2 minutes.\n"
    echo "Probably no new distro was build."
    echo "Potential distro:"
    ls -l $newdistro
    exit 1
else
    echo "   Successfully created new distro: $newdistro."
fi

echo ">> Copy the new distro to here..."
cp $newdistro .

# assign current file to $newdistro
newdistro=`find . -type f -exec stat -f "%m %N" {} \; | sort -n | tail -1 | cut -f2- -d" "`
distrodirname=${newdistro:2:${#newdistro}-6}

echo ">> Create an empty directory $root/ on the cluster..."
ssh $cluster "mkdir -p $root/; rm -r $root/; mkdir $root"

echo ">> Copy distro to $root/ on the cluster..."
scp $newdistro $cluster:$root/

echo ">> Unzip the distro on the cluster. Output in unzipdistro.log."
echo "cd $root; ls -al; unzip ${newdistro}" | ssh $cluster 'bash -s' > unzipdistro.log

echo ">> Copy helloWorld example to the cluster. Output in copyHelloWorld.log."
scp -r helloWorld/ $cluster:$root/ > copyHelloWorld.log

echo ">> Generate scripts. Output in generate.log."
echo "source /target/gpfs2/gcc/gcc.bashrc; module load jdk/1.6.0_33; cd $root; sh $distrodirname/molgenis_compute.sh -inputdir=helloWorld" \
| ssh $cluster 'bash -s' > generate.log

echo ">> Compare generated and expected pipeline..."
echo "diff -rq $root/helloWorld/output $root/helloWorld/expected_scripts" | ssh $cluster 'bash -s' > diff_generated_vs_expected_pipeline.log
isEmpty diff_generated_vs_expected_pipeline.log

echo ">> Sleep 60 seconds. The cluster does not allow more than a certain number of ssh-connections in a minute..."
sleep 60

echo ">> Execute pipeline and produce results..."
echo "cd $root/helloWorld/output; sh runlocal.sh > $root/helloWorld/produced_results.log" | ssh $cluster 'bash -s'

echo ">> Compare produced and expected results..."
echo "diff $root/helloWorld/produced_results.log $root/helloWorld/expected_results.log" | ssh $cluster 'bash -s' > diff_generated_vs_expected_results.log
isEmpty diff_generated_vs_expected_results.log

echo "\nTest terminated successfully!\n"
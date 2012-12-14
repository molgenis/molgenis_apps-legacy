#!/bin/bash

printf "\nStarting HelloWorld test.\n\n"

# Initialize constant testResults and load function isEmpty
workdir=$( cd -P "$( dirname "$0" )" && pwd )
. $workdir/initialize.sh

echo "1. Remove previous results..."
sh $workdir/removeResults.sh

echo "2. Create directory 'testResults' for the results of this test..."
mkdir -p $testResults

echo "2. Generate new result..."
sh $workdir/generate_HelloWorld_test.sh > $testResults/generate.log

echo "3. Compare generated and expected pipeline..."
diff -rq $generatedScriptsDir $workdir/helloWorld/expected_scripts > $testResults/diff_generated_vs_expected_pipeline.log
isEmpty $testResults/diff_generated_vs_expected_pipeline.log

echo "4. Execute pipeline and produce results..."
cd $generatedScriptsDir
sh runlocal.sh > $testResults/current_results.log

echo "5. Compare produced and expected results..."
diff $testResults/current_results.log $workdir/helloWorld/expected_results.log > $testResults/diff_generated_vs_expected_results.log
isEmpty $testResults/diff_generated_vs_expected_results.log

printf "\nTest terminated successfully!\n\n"
exit 0
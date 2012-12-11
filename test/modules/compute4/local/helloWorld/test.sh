#!/bin/bash

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
echo "1. Remove previous results..."
sh removeResults.sh

echo "2. Create directory 'testResults' for the results of this test..."
mkdir testResults

echo "2. Generate new result..."
sh generate_HelloWorld_test.sh > testResults/generate.log

echo "3. Compare generated and expected pipeline..."
diff -rq helloWorld/output helloWorld/expected_scripts > testResults/diff_generated_vs_expected_pipeline.log
isEmpty testResults/diff_generated_vs_expected_pipeline.log

echo "4. Execute pipeline and produce results..."
cd helloWorld/output
sh runlocal.sh > ../../testResults/current_results.log

echo "5. Compare produced and expected results..."
cd ../..
diff testResults/current_results.log helloWorld/expected_results.log > testResults/diff_generated_vs_expected_results.log
isEmpty testResults/diff_generated_vs_expected_results.log

echo "\nTest terminated successfully!\n"
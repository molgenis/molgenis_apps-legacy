# Define constants  
workdir=$( cd -P "$( dirname "$0" )" && pwd )
echo "WORKDIR = $workdir"
testResults="$workdir/../../../../../test-output/compute4/local/helloWorld"
generatedScriptsDir=$testResults/generatedScripts

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
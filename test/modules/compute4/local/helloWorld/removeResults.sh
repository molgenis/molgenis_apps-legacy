workdir=$( cd -P "$( dirname "$0" )" && pwd )
. $workdir/initialize.sh

rm -rf $testResults      # our log files and comparison files
rm -rf helloWorld/output # generated files
rm -f  logger.out        # output of generator
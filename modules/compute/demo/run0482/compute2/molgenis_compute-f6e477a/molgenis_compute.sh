#!/bin/bash
#
# This script runs the MOLGENIS/compute commandline with only the jars needed added to the CLASSPATH.
# To get relative path to this script use $(dirname -- "$0").
#

MCDIR=$( cd -P "$( dirname "$0" )" && pwd )
			
java -cp \
$(dirname -- "$0")/lib/molgenis-b6e0274.jar:\
$(dirname -- "$0")/lib/molgenis-compute-f6e477a.jar:\
$(dirname -- "$0")/lib/commons-io-2.4.jar:\
$(dirname -- "$0")/lib/freemarker.jar:\
$(dirname -- "$0")/lib/log4j-1.2.15.jar \
org.molgenis.compute.commandline.ComputeCommandLine \
-mcdir=${MCDIR} \
$*
		
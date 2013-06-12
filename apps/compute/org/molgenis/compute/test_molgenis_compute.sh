#!/usr/bin/env bash

#
# A script to make testing of MOLGENIS/COMPUTE easier.
#
sh $(dirname -- "$0")/molgenis_compute.sh \
-inputdir=$(dirname -- "$0")/pipelines/demo/helloWorld/ \
-templates=$(dirname -- "$0")/templates/ \
-outputdir=$(dirname -- "$0")/generatedscripts/ \
-id=testRun \

#MOLGENIS walltime=00:45:00

# This step will be solved in the R-script of step 9.
<#noparse>#join -1 "FID" -2 "FID" ${filehandleMissingString} ${filehandleHeterozygosityString} > ${filehandleJoinString}</#noparse>
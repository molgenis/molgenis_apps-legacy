#MOLGENIS walltime=00:05:00
#FOREACH project
#DOCUMENTATION Documentation of QCReport.ftl, ${getStatisticsScript}

<#include "Helpers.ftl"/>

# We need some parameters folded per sample:
Parameters:
${parameters}

<#assign folded = foldParameters(parameters,"project,externalSampleID") />
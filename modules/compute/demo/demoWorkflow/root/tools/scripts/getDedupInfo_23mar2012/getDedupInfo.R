#!/usr/bin/env Rscript
# author: mdijkstra
# version: 23mar12
debug = F

# TO DO: get rid of this local variable!
current.path = if (debug) "./" else "/Users/mdijkstra/Documents/work/git/molgenis_apps/modules/compute/demo/demoWorkflow/root/tools/scripts/getDedupInfo_23mar2012/"

library(stringr)
source(str_c(current.path,'readCommandLineArgs.R'))
source(str_c(current.path,'createDedupTableFunctions.R'))

# if paired end, the divide duplicates by two
ddmat = getDedupMatrix(paired)

ddmat = cbind(flowcell, lane, sample, ddmat)
colnames(ddmat)[1:3] = c("Flowcell", "Lane", "Sample")

# save output
write(mat2Latex(addHeader(ddmat), "Duplication statistics"), qcdedupmetricsout)
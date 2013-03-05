#!/usr/bin/env Rscript
# author: mdijkstra
# version: 19mar12
debug = F

# TO DO: get rid of this local variable!
current.path = if (debug) "./" else "/target/gpfs2/gcc/tools/scripts/createSNPTable_19mar2012/"

library(stringr)
source(str_c(current.path,'readCommandLineArgs.R'))
source(str_c(current.path,'createSNPTableFunctions.R'))

# type table
write(mat2Latex(addHeader(readTables(type), sample),'Functional type'), typetableout)

# class table
write(mat2Latex(addHeader(readTables(class), sample),'Functional class'), classtableout) 

# impact table
write(mat2Latex(addHeader(readTables(impact), sample),'Functional impact'), impacttableout)
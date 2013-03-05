if (!debug) {
	#Read script arguments
	cargs <- commandArgs(TRUE)
	args=NULL
	if(length(cargs)>0){
		flags = grep("^--.*",cargs)
		values = (1:length(cargs))[-flags]
		args[values-1] = cargs[values]
		if(length(args)<tail(flags,1)){
			args[tail(flags,1)] = NA
		}
	    names(args)[flags]=cargs[flags]
    }
	
	## validate command line arguments
	arglist = c("--dedupmetrics", "--flowcell", "--lane", "--sample", "--paired", "--qcdedupmetricsout", NA)

	wrong.flags = length(args) == 0
	if (!wrong.flags) wrong.flags = !all(names(args) %in% arglist)

	if(wrong.flags)
	{
		if (!all(names(args) %in% arglist)) cat("\n =====\n ERROR: YOUR FLAG", names(args)[!(names(args) %in% arglist)], "WAS NOT FOUND!\n", "=====\n\n")
		cat("Example usage (TO DO: improve this help): \n
		${getDedupInfoScript} \\
		--dedupmetrics ${csvQuoted(dedupmetrics)} \\
		--flowcell ${csvQuoted(flowcell)} \\
		--lane ${csvQuoted(lane)} \\
		--sample ${csvQuoted(externalSampleID)} \\
		--paired TRUE \\
		--qcdedupmetricsout ${qcdedupmetricsout}"
		)
	}
	else
	{
		getList = function(param) strsplit(args[paste('--', param, sep='')], ',')[[1]]
	
		## parse command line arguments:
		dedupmetrics		= getList('dedupmetrics')
		flowcell			= getList('flowcell')
		lane				= getList('lane')
		sample				= getList('sample')
		paired				= args['--paired']
		qcdedupmetricsout	= args['--qcdedupmetricsout']
	}	
} else {
	dedupmetrics		= c("testdata/110303_SN163_0393_A80MP0ABXX_L4_AGAGAT.human_g1k_v37.dedup.metrics","testdata/110303_SN163_0393_A80MP0ABXX_L4_TAATTT.human_g1k_v37.dedup.metrics","testdata/111018_SN163_0434_AD0AP0ACXX_L5_AGAGAT.human_g1k_v37.dedup.metrics","testdata/111018_SN163_0434_AD0AP0ACXX_L5_TAATTT.human_g1k_v37.dedup.metrics")
	flowcell			= c("A80MP0ABXX","A80MP0ABXX","AD0AP0ACXX","AD0AP0ACXX")
	lane				= c("4","4","5","5")
	sample				= c( "96_322","97_1574","96_322","97_1574")
	paired				= TRUE
	qcdedupmetricsout	= "out.tex"
}
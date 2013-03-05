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
	arglist = c("--runtimelog", "--hsmetrics", "--alignment", "--insertmetrics", "--dedupmetrics", "--concordance", "--sample", "--colnames", "--csvout", "--tableout", "--descriptionout", "--baitsetout", "--qcdedupmetricsout", NA)

	wrong.flags = length(args) == 0
	if (!wrong.flags) wrong.flags = !all(names(args) %in% arglist)

	if(wrong.flags)
	{
		if (!all(names(args) %in% arglist)) cat("\n =====\n ERROR: YOUR FLAG", names(args)[!(names(args) %in% arglist)], "WAS NOT FOUND!\n", "=====\n\n")
		cat("Example usage: \n
		getStatistics.sh \\
		--runtimelog ${runtimelog} \\
		--hsmetrics ${csvQuoted(hsmetrics)} \\
		--alignment ${csvQuoted(alignmentmetrics)} \\
		--insertmetrics ${csvQuoted(recalinsertsizemetrics)} \\
		--dedupmetrics ${csvQuoted(dedupmetrics)} \\
		--concordance ${csvQuoted(concordancefile)} \\
		--sample ${csvQuoted(externalSampleID)} \\
		--csvout ${qcstatisticscsv} \\
		--tableout ${qcstatisticstex} \\
                --descriptionout ${qcstatisticsdescription} \\
		--baitsetout ${qcbaitset} \\
                --qcdedupmetricsout  ${qcdedupmetricsout}"
		)
	}
	else
	{
	
		## parse command line arguments:
		hsmetrics.files		= strsplit(args['--hsmetrics'], ',')[[1]]
		almetrics.files		= strsplit(args['--alignment'], ',')[[1]]
		insertmetrics.files	= strsplit(args['--insertmetrics'], ',')[[1]]
		dedupmetrics.files	= strsplit(args['--dedupmetrics'], ',')[[1]]
		concordance.files	= strsplit(args['--concordance'], ',')[[1]]
		samples				= strsplit(args['--sample'], ',')[[1]]
		cnames				= args['--colnames']
		csvout				= args['--csvout']
		tableout			= args['--tableout']
		descriptionout			= args['--descriptionout']
		baitsetout			= args['--baitsetout']
		qcdedupmetricsout		= args['--qcdedupmetricsout']
	}	
} else {
	nsamples = 6
	setwd('/Users/mdijkstra/Dropbox/Documents/ngs/statistics')
	hsmetrics.files = rep('111202_SN163_0448_C0A5UACXX_L1_AGAGAT.human_g1k_v37.hsmetrics', nsamples)
	almetrics.files = rep('111202_SN163_0448_C0A5UACXX_L1_AGAGAT.human_g1k_v37.alignmentmetrics', nsamples)
	insertmetrics.files = rep('111202_SN163_0448_C0A5UACXX_L1_TGACTT.human_g1k_v37.recal.insertsizemetrics', nsamples)
	dedupmetrics.files = rep('111202_SN163_0448_C0A5UACXX_L1_TGACTT.human_g1k_v37.dedup.metrics', nsamples)
	concordance.files = rep('111202_SN163_0448_C0A5UACXX_L1_AGAGAT.human_g1k_v37.concordance.ngsVSarray.txt', nsamples)
	samples	= str_c('s', 1:nsamples)
	cnames = 'NiceColumnNames.csv'
	csvout = 'test.csv'
	tableout = 'qctable.tex'
	descriptionout = 'description.tex'
}
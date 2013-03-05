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
	arglist = c("--sample", "--type", "--class", "--impact", "--typetableout", "--classtableout", "--impacttableout", NA)

	wrong.flags = length(args) == 0
	if (!wrong.flags) wrong.flags = !all(names(args) %in% arglist)

	if(wrong.flags)
	{
		if (!all(names(args) %in% arglist)) cat("\n =====\n ERROR: YOUR FLAG", names(args)[!(names(args) %in% arglist)], "WAS NOT FOUND!\n", "=====\n\n")
		cat("Example usage (TO DO: improve this help): \n
		createSNPTable.sh \\
		--sample ${csvQuoted(externalSampleIDfolded)} \\
		--type ${csvQuoted(snpsfinalvcftabletypefolded)} \\
		--class ${csvQuoted(snpsfinalvcftableclassfolded)} \\
		--impact ${csvQuoted(snpsfinalvcftableimpactfolded)} \\
		--typetableout ${typetableout} \\
		--classtableout ${classtableout} \\
		--impacttableout ${impacttableout}"
		)
	}
	else
	{
		getList = function(param) strsplit(args[paste('--', param, sep='')], ',')[[1]]
	
		## parse command line arguments:
		sample				= getList('sample')
		type				= getList('type')
		class				= getList('class')
		impact				= getList('impact')
		typetableout		= args['--typetableout']
		classtableout		= args['--classtableout']
		impacttableout		= args['--impacttableout']
	}	
} else {
	sample				= c("96_322","97_1574")
	type				= c("testdata/96_322.snps.final.type.txt","testdata/97_1574.snps.final.type.txt")
	class				= c("testdata/96_322.snps.final.class.txt","testdata/97_1574.snps.final.class.txt")
	impact				= c("testdata/96_322.snps.final.impact.txt","testdata/97_1574.snps.final.impact.txt")
	typetableout		= "sca19.snps.final.type.tex"
	classtableout		= "sca19.snps.final.class.tex"
	impacttableout		= "sca19.snps.final.impact.tex"
}
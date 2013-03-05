#!/usr/bin/env Rscript
# author: mdijkstra
# version: 20120719

debug = F
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
		            
		## parse command line arguments:
		insertSizeMetrics = args['--insertSizeMetrics']
		pdfFile = args['--pdf']
		
} else {
	insertSizeMetrics = "33029.insertsizemetrics"
	pdfFile = paste(insertSizeMetrics, '.pdf', sep='')
}

# If metrics file does not exist, then (assume Single Read and) plot "N/A" so that QC-report is still generated in the same way.
if (!file.exists(insertSizeMetrics)) {
	pdf(pdfFile)
		par(mai=c(0,0,0,0))
		plot(1, type="n", xlab="", ylab="", axes=F)
		text(1,1,"N/A",cex=15, col="gray")
	dev.off()
} else {

# Read insert size distribution file
mat = read.table(insertSizeMetrics, sep='\t', skip=10, header=T)

# Scale y-axis by dividing by 10^6
mat[,2] = mat[,2] / 10^6

if (!all(colnames(mat) == c("insert_size", "All_Reads.fr_count")))
{
	pdf(pdfFile)
		plot(1,1, type="n", axes=F, xlab="", ylab="")
		text(1, 1, "Wrong input", cex=5)
	dev.off()
} else {
	pdf(pdfFile)
		par(mai=c(1.2,1.5,0,0.5))
		plot(mat, axes=F, lwd = 7, t='l', line=-1, xlab="", ylab="")
		axis(1, cex.axis = 2, lwd=3, padj=.5)
		axis(2, cex.axis =2, lwd=3, las = 2)
		mtext("Insert size (base)", 1,cex=3, line=4.5)
		mtext("Counts * 10^6 (a.u.)", 2,cex=3, line=5)

		# calculate median
		# NB Assume length(unique(diff(mat[,1]))) == 1
		# We do it this way because its quick
		cum.sum = cumsum(mat[,2])
		i = which.min(cum.sum <= tail(cum.sum, 1) / 2)
		arrows(mat[i,1], max(mat[,2])/4, mat[i,1], 0, lwd = 3.5, col="darkred")
		legend("topright", lty=NULL, c("Median", paste(mat[i,1], "bp")), bty="n", cex=2, text.col="darkred")
	dev.off()
}
}
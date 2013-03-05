retrieve.table = function(filename, skip.n.lines)
{
	thismat = NULL
	
	f = file(filename, "r")
	readLines(f, skip.n.lines)
	header 	= str_split(readLines(f, 1), '\t')[[1]]
	val		= str_split(readLines(f, 1), '\t')[[1]]
	close(f)
	
	thismat = rbind(NULL, val)
	colnames(thismat) = header
	
	return(thismat)
}

getDedupMatrix = function(is.paired.end)
{
	ddmat = NULL
	for (fn in dedupmetrics)
	{
	        ddmat = rbind(ddmat, retrieve.table(fn, 6))
	}
	if (is.paired.end) ddmat[, 'PERCENT_DUPLICATION'] = as.numeric(ddmat[, 'PERCENT_DUPLICATION']) / 2
	dedup.col.selection = c('READ_PAIR_DUPLICATES', 'PERCENT_DUPLICATION')
	ddmat = ddmat[, dedup.col.selection]

	# hack if ddmat just became a vector
	if (is.vector(ddmat))ddmat = t(as.matrix(ddmat))

	colnames(ddmat) = c('Duplicate read pairs', 'Fraction')
	ddmat[,2] = round(as.numeric(ddmat[,2]), 3)
	ddmat
}

addHeader = function(mat)
{
	rbind(str_c("\\textbf{", colnames(mat), "}"), mat)
}

mat2Latex = function(mat, title)
{
	nc = ncol(mat)
	
	latexString = str_c("\\begin{table}[h!]\n \\caption{", title, "}\n \\begin{narrow}{-1in}{-1in}\n \\centering\n")
	latexString = str_c(latexString, "\\begin{tabular}{", paste(str_c(rep('l', nc - 2), collapse=' '), 'r', 'r'), "} \n  \\hline \n")
	for (i in 1:nrow(mat))
	{
		for (j in 1 : nc)
		{
			latexString = str_c(latexString, mat[i, j])
			latexString = str_c(latexString, if (j < nc) ' & ' else ' \\\\ \n')
		}
	}

	latexString = str_c(latexString, "\\hline \n\\end{tabular}")
	latexString = str_c(latexString, "\\end{narrow}\n \\end{table}\n")

	latexString = str_replace_all(latexString, "_", "\\\\textunderscore ")
	latexString = str_replace_all(latexString, ">=", "$\\\\ge$")
		
	latexString
}

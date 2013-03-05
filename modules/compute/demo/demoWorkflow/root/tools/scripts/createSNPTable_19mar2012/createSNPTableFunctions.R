readTables = function(args) {
	table = NULL
	for (i in 1:length(args))
	{
		i.table = read.csv(args[i], sep='\t', header=F, stringsAsFactors=F)
		
		if (i == 1) {
			table = i.table
		}
		else
		{
			table = cbind(table, i.table[,2])
		}
	}

	table
}

addHeader = function(mat, header)
{
	rbind(c("Sample", str_c("\\textbf{", header, "}")), mat)
}

mat2Latex = function(mat, title)
{
	nc = ncol(mat)
	
	latexString = str_c("\\begin{table}[h!]\n \\caption{", title, "}\n \\begin{narrow}{-1in}{-1in}\n \\centering\n")
	latexString = str_c(latexString, "\\begin{tabular}{", str_c(rep('l', nc + 1), collapse=' '), "} \n  \\hline \n")
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

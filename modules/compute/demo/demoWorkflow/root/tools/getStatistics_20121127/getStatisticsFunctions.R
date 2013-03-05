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


add.col.after = function(mat, left.col.name, col.name, values)
{
	index.col.left = which(colnames(mat) == left.col.name)
	mat = cbind(mat[1:index.col.left], values, mat[(index.col.left + 1):ncol(mat)])
	colnames(mat)[index.col.left + 1] = col.name
	mat
}

# Substitute Nice column names
substitute.col.names = function(mat)
{
	niceNames = read.csv(cnames, as.is = T, header = T)
	
	description = NULL
	for (i in 1:ncol(mat))
	{
		cn = colnames(mat)[i]
		index = which(cn == niceNames[, 1])
		
		if (0 < length(index))
		{
			colnames(mat)[i] = niceNames[index, 2]
			description[i] = niceNames[index, 3]
		} else {
			description[i] = ""
		}
		i = i + 1
	}
	
	rbind(mat, description)
}

mat2Latex = function(mat)
{
	nc = ncol(mat) - 1
	nsuper = 1
	description = ""
	
	latexString = str_c("\\begin{table}[h!]\n \\caption{Overview statistics}\n \\begin{narrow}{-1in}{-1in}\n \\centering\n")
	latexString = str_c(latexString, "\\begin{tabular}{", str_c(rep('l', nc + 1), collapse=' '), "} \n  \\hline \n")
	for (i in 1:nrow(mat))
	{
		latexString = str_c(latexString, '  ', rownames(mat)[i])

		if (mat[i, nc + 1] != "")
		{
			# add superscript only if description present
			superscript = str_c("$^{(", nsuper, ")}$")
			latexString = str_c(latexString, superscript)
			description = str_c(description, superscript, ' & ', mat[i, nc + 1], "\\\\ \n ")
			
			nsuper = nsuper + 1
		}
		
		latexString = str_c(latexString, " & ")

		for (j in 1 : nc)
		{
			latexString = str_c(latexString, mat[i, j])
			latexString = str_c(latexString, if (j < nc) ' & ' else ' \\\\ \n')
		}
	}
	
	latexDescription <<- str_replace_all(description, ">=", "$\\\\ge$")
	
	latexString = str_c(latexString, "\\hline \n\\end{tabular}")
	latexString = str_c(latexString, "\\end{narrow}\n \\end{table}\n")

	latexString = str_replace_all(latexString, "_", "\\\\textunderscore ")
	latexString = str_replace_all(latexString, ">=", "$\\\\ge$")
		
	latexString
}

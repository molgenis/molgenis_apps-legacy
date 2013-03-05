# Source functions for data transfer to cluster
source dataTransfer.sh

source /Users/mdijkstra/Documents/work/git/molgenis_apps/modules/compute/demo/demoWorkflow/root/tools/scripts/import.sh
#
##
### Create Reference Genome and reads. Next gzip the reads.
##
#

# Remove files
rm -rf /Users/mdijkstra/Documents/work/git/molgenis_apps/modules/compute/demo/demoWorkflow/root/resources/hg19/indices/
rm -f /Users/mdijkstra/Documents/work/git/molgenis_apps/modules/compute/demo/demoWorkflow/root/groups/gaf/rawdata/ngs//20130129_simMachine_0001_FLOWCELL/20130129_simMachine_0001_FLOWCELL_L1_1.fq
rm -f /Users/mdijkstra/Documents/work/git/molgenis_apps/modules/compute/demo/demoWorkflow/root/groups/gaf/rawdata/ngs//20130129_simMachine_0001_FLOWCELL/20130129_simMachine_0001_FLOWCELL_L1_2.fq
rm -f /Users/mdijkstra/Documents/work/git/molgenis_apps/modules/compute/demo/demoWorkflow/root/resources/hg19/intervals/simulated_baits_hg19_simulatedReferenceGenome.bed
rm -f /Users/mdijkstra/Documents/work/git/molgenis_apps/modules/compute/demo/demoWorkflow/root/resources/hg19/intervals/simulated_targets_hg19_simulatedReferenceGenome.bed

# Create directory to store files
mkdir -p /Users/mdijkstra/Documents/work/git/molgenis_apps/modules/compute/demo/demoWorkflow/root/resources/hg19/indices/
mkdir -p /Users/mdijkstra/Documents/work/git/molgenis_apps/modules/compute/demo/demoWorkflow/root/resources/hg19/intervals

/usr/bin/R --vanilla -q <<RSCRIPT

# Reference genome
refGen = refGenSNP	= list()	# Ref. genome with and without SNP
chrom				= NULL		# Ref. genome per chromosome
chromosomes 		= 1:2
n.chrom				= length(chromosomes)
line.length 		= 25
n.lines.per.chrom 	= 2

# SNP
SNP.chrom		= 1
SNP.location	= 5
SNP.reference	= 'A'	# The nucleotide in the reference genome
SNP.mutation	= 'T'	# The nucleotide in the measured genome

# Reading error
error.read		= 1					# The number of the read with the reading error
error.location	= SNP.location + 1
error.reference	= 'A'				# The correct nucleotide as present in the measured genome
error.mutation	= 'T'				# The reading error in the respective read

# Reads
reads.left = reads.right = list()				# A list with your reads including the SNP
read.quality	= 2								# Constant quality for each nucleotide read
n.chrom.reads	= 5								# Number of reads per chrom
n.reads			= n.chrom.reads * n.chrom		# Total number of reads
read.chrom		= c(rep(1, n.chrom.reads), rep(2, n.chrom.reads))
read.length		= rep(10, n.reads)
insert.length	= rep(read.length, n.reads)
read.start		= c(1:n.chrom.reads, 1:n.chrom.reads + 1)

# Duplicate read
duplicate.read.index.origin = n.reads - 1
duplicate.read.index.target = n.reads

#
##
### Generate Reference genome
##
#
for (i.chrom in chromosomes)
{
	chromSeq = list()
	refGen[[i.chrom]] = sample(c("A", "T", "C", "G"), line.length * n.lines.per.chrom, replace=T)
	cat(i.chrom, ' ', refGen[[i.chrom]], '\n', sep='')
}

# Generate SNP
refGen[[SNP.chrom]][SNP.location] = SNP.reference
# Prepare reading error and define the 'correct' nucleotide in RefGenome
refGen[[read.chrom[error.read]]][error.location] = error.reference
refGenSNP = refGen
refGenSNP[[SNP.chrom]][SNP.location] = SNP.mutation

# Generate reads
for (i in 1:n.reads)
{
	index.start	= read.start[i]
	index.stop	= read.start[i] + read.length[i] - 1
	read.left	= refGenSNP[[read.chrom[i]]][index.start : index.stop]

	index.start	= index.start + read.length[i] + insert.length[i]
	index.stop	= index.stop  + read.length[i] + insert.length[i]
	read.right	= refGenSNP[[read.chrom[i]]][index.start : index.stop]

	reads.left[[length(reads.left) + 1]] = read.left
	reads.right[[length(reads.right) + 1]] = read.right
}

# Duplicate read
reads.left[[duplicate.read.index.target]] = reads.left[[duplicate.read.index.origin]]
reads.right[[duplicate.read.index.target]] = reads.right[[duplicate.read.index.origin]]
read.start[duplicate.read.index.target] = read.start[duplicate.read.index.origin]

# Generate reading error
reads.left[[error.read]][error.location] = error.mutation

#
##
### Save data in files
##
#

# Save reference genome
for (i.chrom in chromosomes)
{
	cat('>', i.chrom, '\n', file = '/Users/mdijkstra/Documents/work/git/molgenis_apps/modules/compute/demo/demoWorkflow/root/resources/hg19/indices/simulatedReferenceGenome.fa', append = T, sep='')
	for (i.line in 1:n.lines.per.chrom)
	{
		index.start = (i.line - 1) * line.length
		cat(refGen[[i.chrom]][(index.start + 1) : (index.start + line.length)], '\n', sep='', file = '/Users/mdijkstra/Documents/work/git/molgenis_apps/modules/compute/demo/demoWorkflow/root/resources/hg19/indices/simulatedReferenceGenome.fa', append = T)
	}
}

# Save reads
for (i in 1:n.reads)
{
	# left
	read.id = paste(read.chrom[i], '_read_', i, '_0/1\n', sep='')
	cat('@', read.id, sep='', file = '/Users/mdijkstra/Documents/work/git/molgenis_apps/modules/compute/demo/demoWorkflow/root/groups/gaf/rawdata/ngs//20130129_simMachine_0001_FLOWCELL/20130129_simMachine_0001_FLOWCELL_L1_1.fq', append = T)
	cat(reads.left[[i]], '\n', sep='', file = '/Users/mdijkstra/Documents/work/git/molgenis_apps/modules/compute/demo/demoWorkflow/root/groups/gaf/rawdata/ngs//20130129_simMachine_0001_FLOWCELL/20130129_simMachine_0001_FLOWCELL_L1_1.fq', append = T)
	cat('+', read.id, sep='', file = '/Users/mdijkstra/Documents/work/git/molgenis_apps/modules/compute/demo/demoWorkflow/root/groups/gaf/rawdata/ngs//20130129_simMachine_0001_FLOWCELL/20130129_simMachine_0001_FLOWCELL_L1_1.fq', append = T)
	cat(rep(2, read.length[i]), '\n', sep='', file = '/Users/mdijkstra/Documents/work/git/molgenis_apps/modules/compute/demo/demoWorkflow/root/groups/gaf/rawdata/ngs//20130129_simMachine_0001_FLOWCELL/20130129_simMachine_0001_FLOWCELL_L1_1.fq', append = T)

	# right
	read.id = paste(read.chrom[i], '_read_', i, '_0/2\n', sep='')
	cat('@', read.id, sep='', file = '/Users/mdijkstra/Documents/work/git/molgenis_apps/modules/compute/demo/demoWorkflow/root/groups/gaf/rawdata/ngs//20130129_simMachine_0001_FLOWCELL/20130129_simMachine_0001_FLOWCELL_L1_2.fq', append = T)
	cat(reads.right[[i]], '\n', sep='', file = '/Users/mdijkstra/Documents/work/git/molgenis_apps/modules/compute/demo/demoWorkflow/root/groups/gaf/rawdata/ngs//20130129_simMachine_0001_FLOWCELL/20130129_simMachine_0001_FLOWCELL_L1_2.fq', append = T)
	cat('+', read.id, sep='', file = '/Users/mdijkstra/Documents/work/git/molgenis_apps/modules/compute/demo/demoWorkflow/root/groups/gaf/rawdata/ngs//20130129_simMachine_0001_FLOWCELL/20130129_simMachine_0001_FLOWCELL_L1_2.fq', append = T)
	cat(rep(2, read.length[i]), '\n', sep='', file = '/Users/mdijkstra/Documents/work/git/molgenis_apps/modules/compute/demo/demoWorkflow/root/groups/gaf/rawdata/ngs//20130129_simMachine_0001_FLOWCELL/20130129_simMachine_0001_FLOWCELL_L1_2.fq', append = T)
}

# Visualize Reference Genome and reads
for (i.chrom in chromosomes)
{
	cat('> Chromosome', i.chrom, '\n', file = '/Users/mdijkstra/Documents/work/git/molgenis_apps/modules/compute/demo/demoWorkflow/root/resources/hg19/indices/visualization.txt', append = T)
	if (SNP.chrom == i.chrom) cat(rep(' ', min(SNP.location) - 1), '| SNP\n', sep='', file = '/Users/mdijkstra/Documents/work/git/molgenis_apps/modules/compute/demo/demoWorkflow/root/resources/hg19/indices/visualization.txt', append = T)
	if (read.chrom[error.read] == i.chrom) cat(rep(' ', min(error.location) - 1), '| Error\n', sep='', file = '/Users/mdijkstra/Documents/work/git/molgenis_apps/modules/compute/demo/demoWorkflow/root/resources/hg19/indices/visualization.txt', append = T)	
	cat(refGen[[i.chrom]], '\n', sep='', file = '/Users/mdijkstra/Documents/work/git/molgenis_apps/modules/compute/demo/demoWorkflow/root/resources/hg19/indices/visualization.txt', append = T)
	
	# Visualize reads
	for (i in 1:n.reads)
	{
		if (read.chrom[i] == i.chrom)
		{
			read.vis = c(rep(' ', read.start[i] - 1), reads.left[[i]], rep(' ', insert.length[i]), reads.right[[i]])
			cat(read.vis, '\n', sep='', file = '/Users/mdijkstra/Documents/work/git/molgenis_apps/modules/compute/demo/demoWorkflow/root/resources/hg19/indices/visualization.txt', append = T)
		}
	}
	cat('\n', file = '/Users/mdijkstra/Documents/work/git/molgenis_apps/modules/compute/demo/demoWorkflow/root/resources/hg19/indices/visualization.txt', append = T)
}

RSCRIPT

gzip -f /Users/mdijkstra/Documents/work/git/molgenis_apps/modules/compute/demo/demoWorkflow/root/groups/gaf/rawdata/ngs//20130129_simMachine_0001_FLOWCELL/20130129_simMachine_0001_FLOWCELL_L1_1.fq
gzip -f /Users/mdijkstra/Documents/work/git/molgenis_apps/modules/compute/demo/demoWorkflow/root/groups/gaf/rawdata/ngs//20130129_simMachine_0001_FLOWCELL/20130129_simMachine_0001_FLOWCELL_L1_2.fq


# Empty footer


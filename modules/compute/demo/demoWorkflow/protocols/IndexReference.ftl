#
##
### Index the reference genome, create dictionary, bed files and interval lists, and create fasta index
##
#

getFile ${refGenFile}

# Remove files that will be created (append is used)
rm -f ${targetsbed}
rm -f ${baitsbed}

# Set paths
PATH=${tooldir}/bwa-${bwaVersion}:$PATH
PICARD_HOME=${tooldir}/picard-tools-${picardVersion}

# Index reference genome
bwa index -a is ${refGenFile}

# Create dictionary
java -jar $PICARD_HOME/CreateSequenceDictionary.jar R=${refGenFile} O=${refGenDict}
perl -pi -e 's/unsorted/coordinate/g' ${refGenDict}

# Create bed files
echo "1\t1\t50\t+\ttarget1" >> ${targetsbed}
echo "2\t1\t50\t+\ttarget2" >> ${targetsbed}
echo "1\t1\t50\t+\tbait1" >> ${baitsbed}
echo "2\t1\t50\t+\tbait2" >> ${baitsbed}

# Create interval files
cat ${refGenDict} ${targetsbed} > ${targetintervals}
cat ${refGenDict} ${baitsbed} > ${baitintervals}

# Create fasta index
${samtools} faidx ${refGenFile}
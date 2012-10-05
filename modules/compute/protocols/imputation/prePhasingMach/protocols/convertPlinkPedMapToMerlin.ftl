#MOLGENIS walltime=05:00:00 nodes=1 cores=1 mem=4

for chr in {$startChr..$endChr}
do
	getFile ${studyInputDir}/chr$chr.map 
	getFile ${studyInputDir}/chr$chr.ped
	
	inputs ${studyInputDir}/chr$chr.map 
	inputs ${studyInputDir}/chr$chr.ped
done




for chr in {$startChr..$endChr}
do

	mkdir -p ${studyMerlinDir}/$chr/

	#Conver SNP ID to chr_pos and remove 3e column to adhere to merlin
	gawk '
		BEGIN {$1="CHROMOSOME";$2=MARKER;$3=POSITION;print $0}
		{$2=$1"_"$4;print $1,$2,$4}
	' OFS="\t" ${studyInputDir}/chr$chr.map > ${studyMerlinDir}/$chr/chr$chr.map
	
	gawk 'BEGIN {print "T","pheno";}{print "M",$2}' ${studyMerlinDir}/$chr/chr$chr.map > ${studyMerlinDir}/$chr/chr$chr.dat
	
	#Copy ped file
	cp  ${studyInputDir}/chr$chr.ped ${studyMerlinDir}/$chr/chr$chr.ped

done

for chr in {$startChr..$endChr}
do
	putFile ${studyMerlinDir}/$chr/chr$chr.map
	putFile ${studyMerlinDir}/$chr/chr$chr.dat
	putFile ${studyMerlinDir}/$chr/chr$chr.ped
done


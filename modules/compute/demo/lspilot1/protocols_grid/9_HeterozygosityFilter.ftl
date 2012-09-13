#MOLGENIS walltime=00:45:00

inputs "${filehandleMissingString}.imiss"
inputs "${filehandleHeterozygosityString}.het"

#join -1 "FID" -2 "FID" ${filehandleMissingString} ${filehandleHeterozygosityString} > ${filehandleJoinString}

#Run R
${R} --slave --vanilla <<RSCRIPT

het <- read.table("${filehandleHeterozygosityString}", head=TRUE);
mis <- read.table("${filehandleMissingString}", head=TRUE);
het$"HET_RATE" = (het$"N.NM." - het$"O.HOM.")/het$"N.NM.";
plot(mis$"F_MISS",het$"HET_RATE");

het_fail = subset(het, (het$"HET_RATE" < mean(het$"HET_RATE")-3*sd(het$"HET_RATE")) | (het$"HET_RATE" > mean(het$"HET_RATE")+3*sd(het$"HET_RATE")));
het_fail$"HET_DST" = (het_fail$"HET_RATE"-mean(het$"HET_RATE"))/sd(het$"HET_RATE");

write.table(het_fail, "${filehandleFilterString}", row.names=FALSE);
 
RSCRIPT
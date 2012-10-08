#MOLGENIS walltime=96:00:00 nodes=1 cores=1 mem=4

#FOREACH project,chr,chunk




mach \
-d $chunk \
-p chr$chr.ped \
--prefix ${chunk:r} \
--rounds 20 \
--states 200 \
--phase \
--sample 5 \
2>&1 | tee -a ${chunk:r}-mach.log
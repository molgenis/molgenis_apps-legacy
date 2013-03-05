./getDedupInfo.sh \
--dedupmetrics "testdata/110303_SN163_0393_A80MP0ABXX_L4_AGAGAT.human_g1k_v37.dedup.metrics","testdata/110303_SN163_0393_A80MP0ABXX_L4_TAATTT.human_g1k_v37.dedup.metrics","testdata/111018_SN163_0434_AD0AP0ACXX_L5_AGAGAT.human_g1k_v37.dedup.metrics","testdata/111018_SN163_0434_AD0AP0ACXX_L5_TAATTT.human_g1k_v37.dedup.metrics" \
--flowcell "A80MP0ABXX","A80MP0ABXX","AD0AP0ACXX","AD0AP0ACXX" \
--lane "4","4","5","5" \
--sample "96_322","97_1574","96_322","97_1574" \
--paired TRUE \
--qcdedupmetricsout "out.tex"

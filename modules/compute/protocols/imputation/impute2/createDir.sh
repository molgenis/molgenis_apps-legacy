srmmkdir srm://srm.grid.sara.nl/pnfs/grid.sara.nl/data/bbmri.nl/byelas
srmmkdir srm://srm.grid.sara.nl/pnfs/grid.sara.nl/data/bbmri.nl/byelas/tools
srmmkdir srm://srm.grid.sara.nl/pnfs/grid.sara.nl/data/bbmri.nl/byelas/tools/ImputationTool-20120912
srmmkdir srm://srm.grid.sara.nl/pnfs/grid.sara.nl/data/bbmri.nl/byelas/tools/ImputationTool-20120912/lib
srmmkdir srm://srm.grid.sara.nl/pnfs/grid.sara.nl/data/bbmri.nl/byelas/resources
srmmkdir srm://srm.grid.sara.nl/pnfs/grid.sara.nl/data/bbmri.nl/byelas/resources/liftover
srmmkdir srm://srm.grid.sara.nl/pnfs/grid.sara.nl/data/bbmri.nl/byelas/resources/imputationReference
srmmkdir srm://srm.grid.sara.nl/pnfs/grid.sara.nl/data/bbmri.nl/byelas/resources/imputationReference/gonl_release3.1
srmmkdir srm://srm.grid.sara.nl/pnfs/grid.sara.nl/data/bbmri.nl/byelas/resources/imputationReference/gonl_release3.1/Impute2
srmmkdir srm://srm.grid.sara.nl/pnfs/grid.sara.nl/data/bbmri.nl/byelas/resources/imputationReference/gonl_release3.1/TriTyper
srmmkdir srm://srm.grid.sara.nl/pnfs/grid.sara.nl/data/bbmri.nl/byelas/resources/imputationReference/gonl_release3.1/TriTyper/Chr20
srmmkdir srm://srm.grid.sara.nl/pnfs/grid.sara.nl/data/bbmri.nl/byelas/groups
srmmkdir srm://srm.grid.sara.nl/pnfs/grid.sara.nl/data/bbmri.nl/byelas/groups/gonl
srmmkdir srm://srm.grid.sara.nl/pnfs/grid.sara.nl/data/bbmri.nl/byelas/groups/gonl/projects
srmmkdir srm://srm.grid.sara.nl/pnfs/grid.sara.nl/data/bbmri.nl/byelas/groups/gonl/projects/imputationBenchmarking
srmmkdir srm://srm.grid.sara.nl/pnfs/grid.sara.nl/data/bbmri.nl/byelas/groups/gonl/projects/imputationBenchmarking/eQtl
srmmkdir srm://srm.grid.sara.nl/pnfs/grid.sara.nl/data/bbmri.nl/byelas/groups/gonl/projects/imputationBenchmarking/eQtl/hapmap2r24ceu
srmmkdir srm://srm.grid.sara.nl/pnfs/grid.sara.nl/data/bbmri.nl/byelas/groups/gonl/projects/imputationBenchmarking/imputationResult
srmmkdir srm://srm.grid.sara.nl/pnfs/grid.sara.nl/data/bbmri.nl/byelas/groups/gonl/projects/imputationBenchmarking/imputationResult/hapmap2r24ceu_Impute2_refGoNL_allChrs
srmmkdir srm://srm.grid.sara.nl/pnfs/grid.sara.nl/data/bbmri.nl/byelas/groups/gonl/projects/imputationBenchmarking/imputationResult/hapmap2r24ceu_Impute2_refGoNL_allChrs/b36PedMap
srmmkdir srm://srm.grid.sara.nl/pnfs/grid.sara.nl/data/bbmri.nl/byelas/groups/gonl/projects/imputationBenchmarking/imputationResult/hapmap2r24ceu_Impute2_refGoNL_allChrs/tmp/
srmmkdir srm://srm.grid.sara.nl/pnfs/grid.sara.nl/data/bbmri.nl/byelas/groups/gonl/projects/imputationBenchmarking/imputationResult/hapmap2r24ceu_Impute2_refGoNL_allChrs/tmp/b36conversion
srmmkdir srm://srm.grid.sara.nl/pnfs/grid.sara.nl/data/bbmri.nl/byelas/groups/gonl/projects/imputationBenchmarking/imputationResult/hapmap2r24ceu_Impute2_refGoNL_allChrs/tmp/Impute2Result
srmmkdir srm://srm.grid.sara.nl/pnfs/grid.sara.nl/data/bbmri.nl/byelas/groups/gonl/projects/imputationBenchmarking/imputationResult/hapmap2r24ceu_Impute2_refGoNL_allChrs/tmp/Impute2Result/20
srmmkdir srm://srm.grid.sara.nl/pnfs/grid.sara.nl/data/bbmri.nl/byelas/groups/gonl/projects/imputationBenchmarking/imputationResult/hapmap2r24ceu_Impute2_refGoNL_allChrs/tmp/preparedStudy
srmmkdir srm://srm.grid.sara.nl/pnfs/grid.sara.nl/data/bbmri.nl/byelas/groups/gonl/projects/imputationBenchmarking/imputationResult/hapmap2r24ceu_Impute2_refGoNL_allChrs/tmp/preparedStudy/20
srmmkdir srm://srm.grid.sara.nl/pnfs/grid.sara.nl/data/bbmri.nl/byelas/groups/gonl/projects/imputationBenchmarking/imputationResult/hapmap2r24ceu_Impute2_refGoNL_allChrs/tmp/StudyTriTyper
srmmkdir srm://srm.grid.sara.nl/pnfs/grid.sara.nl/data/bbmri.nl/byelas/groups/gonl/projects/imputationBenchmarking/imputationResult/hapmap2r24ceu_Impute2_refGoNL_allChrs/tmp/StudyTriTyper/chr20
srmmkdir srm://srm.grid.sara.nl/pnfs/grid.sara.nl/data/bbmri.nl/byelas/groups/gonl/projects/imputationBenchmarking/imputationResult/hapmap2r24ceu_Impute2_refGoNL_allChrs/tmp/PedMap
srmmkdir srm://srm.grid.sara.nl/pnfs/grid.sara.nl/data/bbmri.nl/byelas/groups/gonl/projects/imputationBenchmarking/imputationResult/hapmap2r24ceu_Impute2_refGoNL_allChrs/tmp/PedMap/chr20

srmcp -server_mode=passive file:///$HOME/impute2-test-run/gonl_release3.1/Impute2/20.impute.hap \
srm://srm.grid.sara.nl:8443/pnfs/grid.sara.nl/data/bbmri.nl/byelas/resources/imputationReference/gonl_release3.1/Impute2/20.impute.hap

srmcp -server_mode=passive file:///$HOME/impute2-test-run/gonl_release3.1/Impute2/20.impute.hap.indv \
srm://srm.grid.sara.nl:8443/pnfs/grid.sara.nl/data/bbmri.nl/byelas/resources/imputationReference/gonl_release3.1/Impute2/20.impute.hap.indv

srmcp -server_mode=passive file:///$HOME/impute2-test-run/gonl_release3.1/Impute2/20.impute.legend \
srm://srm.grid.sara.nl:8443/pnfs/grid.sara.nl/data/bbmri.nl/byelas/resources/imputationReference/gonl_release3.1/Impute2/20.impute.legend

srmcp -server_mode=passive file:///$HOME/impute2-test-run/gonl_release3.1/Impute2/genetic_map_chr20_combined_b37.txt \
srm://srm.grid.sara.nl:8443/pnfs/grid.sara.nl/data/bbmri.nl/byelas/resources/imputationReference/gonl_release3.1/Impute2/genetic_map_chr20_combined_b37.txt

srmcp -server_mode=passive file:///$HOME/impute2-test-run/gonl_release3.1/TriTyper/Chr20/GenotypeMatrix.dat \
srm://srm.grid.sara.nl:8443/pnfs/grid.sara.nl/data/bbmri.nl/byelas/resources/imputationReference/gonl_release3.1/TriTyper/Chr20/GenotypeMatrix.dat

srmcp -server_mode=passive file:///$HOME/impute2-test-run/gonl_release3.1/TriTyper/Chr20/Individuals.txt \
srm://srm.grid.sara.nl:8443/pnfs/grid.sara.nl/data/bbmri.nl/byelas/resources/imputationReference/gonl_release3.1/TriTyper/Chr20/Individuals.txt

srmcp -server_mode=passive file:///$HOME/impute2-test-run/gonl_release3.1/TriTyper/Chr20/PhenotypeInformation.txt \
srm://srm.grid.sara.nl:8443/pnfs/grid.sara.nl/data/bbmri.nl/byelas/resources/imputationReference/gonl_release3.1/TriTyper/Chr20/PhenotypeInformation.txt

srmcp -server_mode=passive file:///$HOME/impute2-test-run/gonl_release3.1/TriTyper/Chr20/SNPMappings.txt \
srm://srm.grid.sara.nl:8443/pnfs/grid.sara.nl/data/bbmri.nl/byelas/resources/imputationReference/gonl_release3.1/TriTyper/Chr20/SNPMappings.txt

srmcp -server_mode=passive file:///$HOME/impute2-test-run/gonl_release3.1/TriTyper/Chr20/SNPs.txt \
srm://srm.grid.sara.nl:8443/pnfs/grid.sara.nl/data/bbmri.nl/byelas/resources/imputationReference/gonl_release3.1/TriTyper/Chr20/SNPs.txt

srmcp -server_mode=passive file:///$HOME/impute2-test-run/hapmap2r24ceu/chr20.map \
srm://srm.grid.sara.nl:8443/pnfs/grid.sara.nl/data/bbmri.nl/byelas/groups/gonl/projects/imputationBenchmarking/eQtl/hapmap2r24ceu/chr20.map

srmcp -server_mode=passive file:///$HOME/impute2-test-run/hapmap2r24ceu/chr20.ped \
srm://srm.grid.sara.nl:8443/pnfs/grid.sara.nl/data/bbmri.nl/byelas/groups/gonl/projects/imputationBenchmarking/eQtl/hapmap2r24ceu/chr20.ped

srmcp -server_mode=passive file:///$HOME/impute2-test-run/resources/liftover/hg18ToHg19.over.chain \
srm://srm.grid.sara.nl:8443/pnfs/grid.sara.nl/data/bbmri.nl/byelas/resources/liftover/hg18ToHg19.over.chain

srmcp -server_mode=passive file:///$HOME/impute2-test-run/resources/liftover/hg19ToHg18.over.chain \
srm://srm.grid.sara.nl:8443/pnfs/grid.sara.nl/data/bbmri.nl/byelas/resources/liftover/hg19ToHg18.over.chain

srmcp -server_mode=passive file:///$HOME/impute2-test-run/tools/ImputationTool-20120912/lib/GeneticaLibraries.jar \
srm://srm.grid.sara.nl:8443/pnfs/grid.sara.nl/data/bbmri.nl/byelas/tools/ImputationTool-20120912/lib/GeneticaLibraries.jar

srmcp -server_mode=passive file:///$HOME/impute2-test-run/tools/ImputationTool-20120912/lib/jsci-core-1.1.jar \
srm://srm.grid.sara.nl:8443/pnfs/grid.sara.nl/data/bbmri.nl/byelas/tools/ImputationTool-20120912/lib/jsci-core-1.1.jar

srmcp -server_mode=passive file:///$HOME/impute2-test-run/tools/ImputationTool-20120912/ImputationTool.jar \
srm://srm.grid.sara.nl:8443/pnfs/grid.sara.nl/data/bbmri.nl/byelas/tools/ImputationTool-20120912/ImputationTool.jar

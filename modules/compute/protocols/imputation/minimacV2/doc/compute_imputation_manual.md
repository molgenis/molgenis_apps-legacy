Imputation using Molgenis Compute
=================================
  


Content
=======

1.	Introduction  
2.	General  
3.	Preparing the study data  
4.	Preparing the reference dataset  
5.	Imputation using minimacV2 pipeline  
6.	Imputation on the grid  
7.	Appendix  

  
  
###1. Introduction  
This manual explains how one can do imputation using the minimac[^3] analysis pipeline in the [Molgenis Compute] framework. The second chapter describes the content of the [Molgenis Compute] binary to be used with the minimac imputation pipeline. The sequential three chapters explain how to create the study and reference data for imputation followed by the actual execution of the imputation pipeline using one of the following five backends; PBS, SGE, BSUB, grid or your local computer. To run the analysis efficient it's needed to devide the analysis in three steps:  
  
* *Preparing and QCing the study data:* here bad SNPs are removed from the study data, afterwards the data is split per number of samples and chromosome chunk  
* *Phasing:* here the data chunks are phased  
* *Imputation:* this last step performs the imputation. This step is separate from the first two steps allowing the use of a new reference panel without having to recalculate the first two steps.  
  
Using the above explained method imputation is parallelized and ready to use on your cluster. For additional compute resources one can use the national Computing infrastructure for life Sciences, [eBioGrid]. How to setup [Molgenis Compute] for this grid infrastructure is explained in chapter six.  
  
  
###2. General
  
  
This chapter describes the content of the [Molgenis Compute] binary. Before starting we recommend to read the [Molgenis Compute manual] and the [Molgenis Compute imputation installation manual].  
  
  
####2.1 Imputation overview  
All imputation protocols are stored in the *protocols/imputation/* directory. This
directory contains multiple sub-directories:
  
* minimac  
* prepareReference  
  
Each of these directories contain the following files to be used as input for Molgenis
Compute:  
  
* parameters.csv
* workflow.csv
* worksheet.csv
* protocols (*.ftl files)
  
  
###3.	Preparing the study data  
  
  
To speed up parallel processing it is required to have your study data split per chromosome in the plink PED/MAP format[^4]. To split up your study per chromosome (1 up to 22) the following plink[^5] command can be used:  
  
>for $chrNumber in {1..22}  
>do  
>plink \\  
>--noweb \\  
>--ped your_study.ped \\  
>--map your_study.map \\  
>--recode \\  
>--chr chrNumber \\  
>--out chr$chrNumber  
>done   
  
Afterwards the created PED and MAP files can be used as input for your analysis. The directory containing the PED/MAP files should be specified as `studyInputDir` in the sampleWorksheet.csv which is explained later in this manual.  
  
####3.1 Test data
In case you don't have study data, a test dataset containing 60 samples from HapMap revision22 on build37 (chr20 & 21) of the human genome is available for download. This set can be used in combination with the included `workflow.csv`.  
  
Download here: https://github.com/downloads/freerkvandijk/files/hapmapCEUr22b37.zip    
  
  
###4. Preparing the reference dataset
  
  
To start imputation on a cohort one first has to prepare the reference data. The
*protocols/imputation/prepareReference/* directory contains several protocols to prepare this data. For this version 3 tools need to be installed:  
* VCFTools  
* convert_snpIDs  
* ConvertVcfToTriTyper  
  
We recommend to install `VCFTools` and `ConvertVcfToTrityper` in a directory named *tools/*. convert_snpIDs should be installed in a directory named *tools/scripts/*. The download links for these tools can be found in the appendix.  
  
####4.1 Generating a reference data set
This workflow requires a VCF file containing phased genotypes as input. The workflow requires the VCF files to be split per chromosome in a folder named *vcf*, see 4.2 for an overview of the datastructure. When executing this workflow two folders containing the data in Impute2 and TriTyper format per chromosome are generated.  
  
The `workflow.csv` contains the following five columns:  
  
| referenceName | chrVcfInputFile | chr | samplesToIncludeFile | vcfFilterOptions |  
| :----: | :----: | :----: | :----: | :----: |  
| reference name | VCF file | chromosome | file containing list of samples | filter options |  
  
The columns explained:  
* referenceName: the name of the reference, later used to specify your reference panel to impute with. Type: string  
* chrVcfInputFile: the VCF file to convert. Type: string  
* chr: chromosome number to convert. Type: integer  
* samplesToIncludeFile: a file containing a list of sampleIDs to keep. Type: string  
* vcfFilterOptions: a space delimited list of parameters to filter on, as used in [VCFTools]. Type: string  
  
An example test reference set can be created using the following command:  
  
>sh molgenis_compute.sh \\  
>-worksheet=protocols/imputation/prepareReference/prepareReferenceWorksheetSample.csv \\  
>-parameters=protocols/imputation/prepareReference/parameters.csv \\  
>-workflow=protocols/imputation/prepareReference/prepareReferenceWorkflow.csv \\  
>-protocols=protocols/imputation/prepareReference/ \\  
>-templates=protocols/imputation/prepareReference/ \\  
>-scripts=/your/output/directory/here/ \\  
>-id=runXX 
  
  
####4.2 Output example  
Executing the above mentioned commands will result in a directory with the following structure in the selected execution environment and after the completion of the jobs:
  


      -referenceName
			|
			+Impute2
			|	<Number>.impute.hap
			|	<Number>.impute.hap.indv
			|	<Number>.impute.legend
			+TriTyper
			|	|
			|	+Chr<Number>
			|		GenotypeMatrix.dat
			|		Individuals.txt
			|		PhenotypeInformation.txt
			|		SNPMappings.txt
			|		SNPs.txt
			|
			+vcf
				chr<Number>.vcf
				chr<Number>.vcf.vcfidx  

	Note:
	1 The vcf directory contains the input VCF files split per chromosome. The *.vcf.vcfidx file is generated in this workflow.
	2 When using Impute2 genetic recombination maps for each chromosome should be added to the Impute2 directory manually.  
	Afterwards the filename convention should be specified in the parameters.csv file of the impute2 workflow.  
  
  
When all the above files are present the reference dataset is ready to be used.  
  
  
###5. Imputation using minimacV2 pipeline  
  
  
All protocols and files to run an imputation using Minimac[^3] can be found in the *protocols/imputation/minimacV2/* directory. The pipeline consists of three steps; preparing the data, phasing and imputation. Furthermore the pipeline has prerequisits which are listed in chapter 5.1.
  
  
####5.1 Tools
To run this pipeline the following tools, scripts and datasets are required:
  
* study data in PED/MAP format (prepared as described in chapter 3)
* reference dataset in VCF,  format (prepared as described in chapter 4)
* java
* python
* ChunkChromosome (v. 2012-08-28)
* minimac (v. beta-2012.10.3) [^3]
* mach (v. 1.0.18) [^6]
* plink (v. 1.07)
* plink1.08 (v. 1.08)
* imputationTool (v. 20120912)
* ConcatWorksheets (v. V1.0)
* expandWorksheet (v. V1.1)  
**Note1: Version numbers are tested**  
**Note2: Download links can be found in chapter 9 "Appendix""**
  
We recommend to install all tools in one directory in a structure of *tools/<toolname>/*, this way only the `"$tooldir"` variable in the parameters.csv needs to be changed.
  
  
####5.2 The sample worksheet for pre-phasing  
To start an analysis one needs to create a so called "worksheet". This worksheet should contain six columns and follow the format specified below:
  
| project | studyInputDir | prePhasingResultDir | imputationPipeline | genomeBuild | chr | autostart |  
| :----: | :----: | :----: | :----: | :----: | :----: | :----: |   
| projectname | directory | directory | beagle/mach/impute2 | b36/b37 | chromosome number | TRUE/FALSE |  
  
The columns explained:  
* project: the project name of your analysis. Type: string  
* studyInputDir: the directory containing the study data split per chrosome in the PED/MAP format as explained in chapter 3. Type: string  
* prePhasingResultDir: the output directory for the pre-phasing result. Type: string  
* imputationPipeline: the pipeline to use, this can be one of the three described in this document. Type: beagle/minimac/impute2  
* genomeBuild: the genome build to use. Type: boolean, b36/b37 **Please make sure your study and referencedata are on the same genome build**  
* chr: the chromosome to run the analysis on. Type: integer  
* autostart: the value in this column specifies if the subsequent analysis steps in the minimac pipeline should be started/submitted automatically. Type: boolean, TRUE/FALSE **Note: This only works if in your cluster setup submission from nodes is allowed.**  
  
  
####5.3 Running an analysis  
The minimacV2 pipeline consists of three parts. The first one aligns all alleles to the reference genome and performs quality control using ImputationTool [^7], chunks the study data in a user specified number of samples and splits the chromosome in chunks by splitting on a specified number of SNPs. This extensive chunking is needed to parallelize the analysis, leading to a total analysis time of approximately 10 hours per chunk of 2000 SNPs and 500 samples. The second step phases the data using MaCH [^6]. The phasing only has to be done once for a specific study. The last step consist of imputing the phased data and concatenates the results per chromosome. Since the phasing is independant of the reference panel one only has to run the third step again when imputing with a different reference panel.  
  
#####Step 1: preparing & QCing the study data  
After preparing the study PED/MAP files as described in chapter 3 and preparing the reference data as described in chapter 4 one needs to change the following parameter values in the `parametersMinimac.csv`:  
  
| name | defaultValue |  
| :----: | :----: |  
| scheduler | PBS/SGE/BSUB/GRID, depending on the backend |  
| stage | The command to load a module eg "module load" |  
| root | The root path were all tools, resources etc. are |  
  
Optionally one can change parameters such as `$pythonversion` and `$javaversion` to accomodate own wishes.  
  
To start the study data preparation & QCing one can run the following command:  
  
>sh molgenis_compute.sh \\  
>-inputdir=protocols/imputation/minimacV2/protocols/ \\  
>-worksheet=protocols/imputation/minimacV2/examplePrePhasingWorksheet.csv \\  
>-parameters=protocols/imputation/minimacV2/parametersMinimac.csv \\  
>-workflow=protocols/imputation/minimacV2/workflowMinimacStage1.csv \\  
>-protocols=protocols/imputation/minimacV2/protocols/ \\  
>-outputdir=/your/output/directory/here/ \\  
>-id=runXX  
  
All folders and jobs can be found in the output directory specified by the user. The second step of the preparation consists of executing the generated shell scripts. This can be done by typing the following command:
  
`cd /your/output/directory/here/`  
`sh submit.sh`  
**Note: Alternatively the generated s00_\*.sh scripts can be executed locally.**  
  
During this preparation the study data chunks and chromosome chunks are automatically added to the existing worksheet leading to a new worksheet named `concattedChunkWorksheet.csv`. This worksheet has to be used during step 2 of the pipeline.  
  
#####Step 2: phasing the study data  
During this step the phasing takes place. If in the `worksheet.csv` the value `TRUE` was set in the `autostart` column the jobs to start this analysis are automatically submitted. If the value is set to `FALSE` one can generate and submit the jobs by executing the following commands:  
  
>sh molgenis_compute.sh \\  
>-inputdir=. \\  
>-worksheet=/your/output/directory/here/../concattedChunkWorksheet.csv \\  
>-parameters=protocols/imputation/minimacV2/parametersMinimac.csv \\  
>-workflow=protocols/imputation/minimacV2/protocols/../workflowMinimacStage2.csv \\  
>-protocols=protocols/imputation/minimacV2/protocols/ \\  
>-outputdir=/your/output/directory/here/../phasing/ \\  
>-id=runXX  
  
`cd /your/output/directory/here/../phasing`  
`sh submit.sh`  
  
**Note: Alternatively one can copy/paste the generated molgenis_compute execution command from the generated script named s01_prepare_s01_FALSE.sh.**  
  
#####Step 3: imputing the phased data  
Since the imputation part is independent from the phasing this step needs to additional parameters/values, `imputationResultDir` and `referencePanel`, in the worksheet. To add these two parameters to the worksheet one can run the included `add_variable.sh` shell script using the following command:  
  
>sh protocols/imputation/minimacV2/add_variable.sh \\  
>-w /your/output/directory/here/../concattedChunkWorksheet.csv \\  
>-v imputationResultDir \\  
>-p /your/imputation/result/directory/here/ \\  
>-o /your/output/directory/here/../tmpImputationWorksheet.csv  
  
>sh protocols/imputation/minimacV2/add_variable.sh \\  
>-w /your/output/directory/here/../tmpImputationWorksheet.csv \\  
>-v referencePanel \\  
>-p nameOfImputationReference \\  
>-o /your/output/directory/here/../mputationWorksheet.csv  
  
**Note1: nameOfImputationReference should be changed to the name of your reference panel, for example `giant1000gv3.20101123`**  
**Note2: an example `imputationWorksheet.csv` can be found here: `protocols/imputation/minimacV2/imputationWorksheet.csv`.**  
  
When finished one can generate and execute the imputation jobs by executing the following commands:  
  
>sh molgenis_compute.sh \\  
>-inputdir=. \\  
>-worksheet=/your/output/directory/here/../mputationWorksheet.csv \\  
>-parameters=protocols/imputation/minimacV2/parametersMinimac.csv \\  
>-workflow=protocols/imputation/minimacV2/protocols/../workflowMinimacStage3.csv \\  
>-protocols=protocols/imputation/minimacV2/protocols/ \\  
>-outputdir=/your/output/directory/here/../imputation/ \\  
>-id=runXX  
  
`cd /your/output/directory/here/../phasing`  
`sh submit.sh`  
  
The output is now ready for further analysis.  
  
  
####5.4 Output  
The pipeline produces several files which can be used for downstream analysis. The following files are produced: 
EXTEND THIS WHEN QUICKTEST IS IMPLEMENTED?  
  
  
###6 Imputation on the grid  
  
  
To run Molgenis Compute on the grid one needs to prepare a webserver with the following requierements:  
* java 1.6.0 or higher  
* git 1.7.1 or higher  
* ant 1.7.1 or higher  
* mysql 5.1.54 or higher  
  
The whole installation and running process can be done in seven steps.  
  
1. Create database  
  >Login as root to mysql.  
  >CREATE USER ’molgenis’ IDENTIFIED BY ’molgenis’;  
  >CREATE DATABASE compute;  
  >GRANT ALL PRIVILEGES ON compute.* TO ’molgenis’@’%’ WITH GRANT OPTION;  
  >FLUSH PRIVILEGES;  
  >Logout.  
  
2. Checkout from git repository and build compute  
  >git clone https://github.com/molgenis/molgenis.git  
  >git clone https://github.com/molgenis/molgenis_apps.git  
  >cd molgenis_apps  
  >ant -f build_compute.xml clean-generate-compile  

  Alternatively one can download the [clone_build.sh] shell script and execute it:  
  >sh clone_build.sh  
  
3. Start webserver  
  >kill -9 \`lsof -i :<your port> -t`  
  >cd molgenis_apps;  
  >nohup ant -f build_compute.xml runOn -Dport=<your port> &  
  
4. Setup environment on the grid  
  Copy `maverick.sh`, `maverick.jdl` and `dataTransferSRM.sh` from [pilot directory] to your `$HOME/maverick` directory on the grid ui-node by executing the following command:  
  >scp maverick.sh maverick.jdl dataTransferSRM.sh \<username>@ui.grid.sara.nl  
  
  Edit `maverick.sh`, specify your ip and port of your webserver, which is started on step 3:  
  >export WORKDIR=$TMPDIR  
  >source dataTransferSRM.sh  
  >curl  -F status=started http://<ip>:<port>/compute/api/pilot > script.sh  
  >sh script.sh 2>&1 | tee -a log.log  
  >curl -F status=done -F log_file=@log.log http://<ip>:<port>/compute/api/pilot  
  
5. Import the first workflow into database by running the `importWorkflow.sh` from [deployment directory]. Files to be imported can be found here:  
  >sh importWorkflow.sh \\  
  >molgenis_apps/modules/compute/protocols/imputation/minimacV2/parametersMinimac.csv \\  
  >molgenis_apps/modules/compute/protocols/imputation/minimacV2/workflowMinimacStage1.csv \\  
  >molgenis_apps/modules/compute/protocols/imputation/minimacV2/protocols/  
  
6. Generate imputation jobs in the database with the `importWorksheet.sh` from [deployment directory] and example worksheet:  
  >sh importWorksheet.sh \\  
  >molgenis_apps/modules/compute/protocols/imputation/minimacV2/workflowMinimacStage1.csv \\  
  >ui.grid.sara.nl \\  
  >molgenis_apps/modules/compute/protocols/imputation/minimacV2/worksheet.csv \\  
  >step01  
  
7. Execute imputation with user credentials using pilot job system: 
  >sh runPilots.sh \\  
  >ui.grid.sara.nl \\  
  >\<username> \\  
  >\<password> \\  
  >grid  
  
After completion of the above generated jobs one needs to start step 2 of the pipeline. For this it is requiered to copy the generated worksheet from the grid back to your local computer and import it into the database. The worksheet can be copied back by executing:  
>scp \<username>@ui.grid.sara.nl:~srm:something? .  
  
The new worksheet can now be imported in the database again, using above mentioned commands.  
  
  
For further information read the [Molgenis Compute manual].  
  
  
###7 Appendix  
  
  
Overview of the tools needed for the minimacV2 pipeline.  
  
| Tool | Downloadlink |  
| :----: | :----: |  
| ChunkChromosome | http://www.bbmriwiki.nl/svn/ebiogrid/scripts/ChunkChromosome-2012-08-28.zip |  
| minimac | http://www.bbmriwiki.nl/svn/ebiogrid/modules/minimac/beta-2012.10.3/minimac.beta-2012.10.3.tgz |  
| mach | http://www.bbmriwiki.nl/svn/ebiogrid/modules/mach/1.0.18/mach.1.0.18.Linux.tgz |  
| plink | http://www.bbmriwiki.nl/svn/ebiogrid/modules/plink/1.07-x86_64/plink-1.07-x86_64.tgz |  
| plink1.08 | http://www.bbmriwiki.nl/svn/ebiogrid/modules/plink/1.08/plink-1.08.tgz |  
| ImputationTool | http://www.bbmriwiki.nl/svn/ebiogrid/scripts/ImputationTool-20120912.zip |  
| ConcatWorksheets | http://www.bbmriwiki.nl/svn/ebiogrid/scripts/ConcatWorksheetsV1.0.zip |  
| ExpandWorksheet | http://www.bbmriwiki.nl/svn/ebiogrid/scripts/ExpandWorksheetWithMergeWorksheetV1.1.zip |  
| VCFTools | http://sourceforge.net/projects/vcftools/files/ |  
| ConvertVcfToTrityper | http://www.bbmriwiki.nl/svn/ebiogrid/scripts/ConvertVcfToTriTyperV1.zip |  
| convert_snpIDs | http://www.bbmriwiki.nl/svn/ebiogrid/scripts/convert_snpIDsV2.pl |  
    
  
[^1]: See http://freemarker.org/ for a manual.
[^2]: http://mathgen.stats.ox.ac.uk/impute/impute_v2.html
[^3]: http://genome.sph.umich.edu/wiki/Minimac
[^4]: http://pngu.mgh.harvard.edu/~purcell/plink/data.shtml#ped
[^5]: http://pngu.mgh.harvard.edu/~purcell/plink/
[^6]: http://www.sph.umich.edu/csg/abecasis/MACH/tour/imputation.html
[^7]: http://www.bbmriwiki.nl/wiki/ImputationTool
[^8]: Link_to_shell_script  
[Molgenis Compute]: http://www.molgenis.org/wiki/ComputeStart (Molgenis Compute)  
[Molgenis Compute manual]: https://github.com/molgenis/molgenis_apps/blob/testing/modules/compute/doc/UserManual.pdf
[VCFTools]: http://vcftools.sourceforge.net/
[clone_build.sh]: https://github.com/molgenis/molgenis_apps/blob/testing/modules/compute4/deployment/clone_build.sh  
[deployment directory]: https://github.com/molgenis/molgenis_apps/tree/testing/modules/compute4/deployment  
[pilot directory]: https://github.com/molgenis/molgenis_apps/tree/testing/modules/compute/pilots/grid
[eBioGrid]: http://www.ebiogrid.nl/  
[Molgenis Compute imputation installation manual]: https://github.com/molgenis/molgenis_apps/blob/testing/modules/compute/protocols/imputation/minimacV2/doc/compute_imputation_installation_manual.md  


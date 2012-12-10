Imputation using Molgenis Compute
============================
  


Content
=======

1.	Introduction
2.	Imputation
3.	Preparing the study data
4.	Preparing the reference dataset
5.	Imputation using Impute2
6.	Imputation using Beagle
7.	Imputation using Minimac
8.	Imputation using minimacV2 pipeline
9.	Appendix  

  
  
###1. Introduction
Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo, fringilla vel, aliquet nec, vulputate eget, arcu. In enim justo, rhoncus ut, imperdiet a, venenatis vitae, justo. Nullam dictum felis eu pede mollis pretium. Integer tincidunt. Cras dapibus. Vivamus elementum semper nisi. Aenean vulputate eleifend tellus. Aenean leo ligula, porttitor eu, consequat vitae, eleifend ac, enim. Aliquam lorem ante, dapibus in, viverra quis, feugiat a, tellus. Phasellus viverra nulla ut metus varius laoreet. Quisque rutrum. Aenean imperdiet. Etiam ultricies nisi vel augue. Curabitur ullamcorper ultricies nisi. Nam eget dui.
  
  
###2. Imputation
  
  
This chapter shortly describes the contents of the Molgenis Compute distro. EXTEND THIS!!  
  
####2.1 Imputation overview  
All imputation protocols are stored in the *protocols/imputation/* directory. This
directory contains multiple sub-directories:
  
* beagle
* impute2
* minimac
* prepareReference
  
Each of these directories contain the following files to be used as input for Molgenis
Compute:
  
* parameters.csv
* workflow.csv
* worksheet.csv
* protocols (*.ftl files)
  
  
####2.2 General compute settings for imputation  
To setup Compute several default parameters in the `"parameters.csv"` file should be changed to your specific system/cluster environment. Changing these settings is obliged to execute any of the imputation pipelines. After changing these parameters the parameters file is ready to use. Changing the following environment parameters is obliged:  
  
* scheduler: this parameter specifies which header for a specific scheduling system should be generated. The following scheduling systems are supported BSUB (BSUB), Portable Batch System (PBS) and Sun Grid Engine (SGE). To generate jobs without headers for Grid usage the value GRID should be specified.  
* root: this is the "root" parameter shared by all other parameters. To ease the setup we recommend to install all tools in a *tools* directory and all resources in a *resources* directory in the "root".    
  
  
###3.	Preparing the study data  
  
  
To speed up parallel processing it is required to have your study data split per chromosome in the pLINK PED/MAP format[^4]. To split up your study per chromosome (1 up to 22) the following pLINK[^5] command can be used:  
  
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
  
Afterwards the created PED and MAP can be used as input for your analysis. The directory containing the PED/MAP files should be specified as `studyInputDir` in the sampleWorksheet.csv which is explained later in this manual.  
  
  
###4. Preparing the reference dataset
  
  
To start imputation on a cohort one first has to prepare the reference data. The
*protocols/imputation/prepareReference/* directory contains several protocols to prepare this data. 
  
####4.1 Generating a reference data set
This workflow requires a VCF file containing phased genotypes as input. The workflow requires the VCF files to be split per chromosome in a folder named *vcf*. When executing this workflow two folders containing the data in Impute2 and TriTyper format per chromosome are generated. When imputing using the minimac pipeline it is sufficient to have the reference data only in VCF format.
  
  
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
Executing the above mentioned commands will result in a directory with the following structure:
  


      -referenceName
			|
			+Impute2
			|	chrNumber.impute.hap
			|	chrNumber.impute.hap.indv
			|	chrNumber.impute.legend
			+TriTyper
			|	|
			|	+ChrchrNumber
			|		GenotypeMatrix.dat
			|		Individuals.txt
			|		PhenotypeInformation.txt
			|		SNPMappings.txt
			|		SNPs.txt
			|
			+vcf
				chrNumber.vcf
				chrNumber.vcf.vcfidx  

	Note:
	1 The vcf directory contains the input VCF files split per chromosome. The *.vcf.vcfidx file is generated in this workflow.
	2 When using Impute 2 genetic recombination maps for each chromosome should be added to the Impute2 directory manually.  
	Afterwards the filename convention should be specified in the parameters.csv file of the impute2 workflow.

  
**HOW CAN NOTE 2 BE EXPLAINED BETTER?**
  
When all these files are present the reference dataset is ready to be used.
  
  
  
###5. Imputation using Impute2  
  
  
All protocols and files to run an imputation using Impute2[^2] can be found in the *protocols/imputation/impute2/* directory. Before running an analysis all required tools need to be installed. 
  
  
####5.1 Tools
To run this pipeline the following tools, scripts and datasets are required:
  
* reference dataset (prepared using the workflow described earlier)
* java
* python
* gtool (v. 0.7.5)
* plink (v. 1.07)
* plink (v. 1.08)
* imputationTool (v. 20120912)
* impute2 (v. 2.2.2)[^2]
* calculateBeagleR2ForImpute2Results.py
* ExpandWorksheetWithMergeWorksheetV1.0
* liftOverUcsc  
**Note: Version numbers are tested**  
  
We recommend to install all tools in one directory, this way only the `"$tooldir"` variable needs to be changed. Furthermore it is required to have your study data divided by chromosome in the PED/MAP format as specified by pLINK.
  
  
####5.2 The sample worksheet
To start an analysis one needs to create a so called "worksheet". This worksheet should contain six columns and follow the format specified below:
  
| project | studyInputDir | referencePanel | imputationResulsDir | imputationPipeline | genomeBuild |  
| :----: | :----: | :----: | :----: | :----: | :----: |  
| projectname | directory | reference data | directory | beagle/mach/impute2 | b36/b37 |
  
  
An example worksheet using Impute2 and b37 is distributed in the Compute binary and can be found here:  
*protocols/imputation/impute2/sampleWorkflow.csv*
  
  
####5.3 Running an analysis using Molgenis Compute
The complete Impute2 pipeline consists of two steps. The first one (specified in createWorkflowImpute.csv) creates the complete filestructure and all Compute jobs in the project folder specified in the sampleWorksheet.csv. The pipeline can be executed using the following command:
  
  
>sh molgenis_compute.sh \\  
>-worksheet=protocols/imputation/impute2/sampleWorksheet.csv \\  
>-parameters=protocols/imputation/impute2/parameters.csv \\  
>-workflow=protocols/imputation/impute2/createWorkflowImpute.csv \\  
>-protocols=protocols/imputation/impute2/ \\  
>-templates=protocols/imputation/impute2/ \\  
>-scripts=/your/output/directory/here/ \\  
>-id=runXX
  
  
All folders and jobs can be found in the scripts directory specified by the user. The second step of the analysis pipeline consists of executing the generated shell scripts. This can be done by typing the following command:
  
`cd /your/output/directory/here/`  
`sh submit.sh`  
**Note: Alternatively the generated s00_\*.sh scripts can be executed by hand**
  
  
All Compute jobs can now be found in the directory */compute/jobs/chrNumber/* in the *imputationResultDir* specified in the sampleWorksheet.csv.
  
  
###6. Imputation using Beagle
  
Still to come!
  
  
###7. Imputation using Minimac
  
All protocols and files to run an imputation using Minimac[^3] can be found in the *protocols/imputation/minimac/* directory. Before running an analysis all required tools need to be installed. 
  
  
####7.1 Tools
To run this pipeline the following tools, scripts and datasets are required:
  
* reference dataset in Ped/Map format (prepared using the workflow described in chapter 5.2)
* java
* python
* ChunkChromosome (v. 2012-08-28)
* minimac (v. beta-2012.10.3) [^3]
* mach (v. 1.0.18)
* plink (v. 1.07)
* plink1.08 (v. 1.08)
* imputationTool (v. 20120912)
* ConcatWorksheets (v. V1.0)
* expandWorksheet (v. V1.1)  
**Note: Version numbers are tested**  
  
We recommend to install all tools in one directory, this way only the `"$tooldir"` variable needs to be changed.
  
  
####7.2 The sample worksheet
To start an analysis one needs to create a so called "worksheet". This worksheet should contain six columns and follow the format specified below:
  
| project | studyInputDir | referencePanel | imputationResultsDir | imputationPipeline | genomeBuild | chr | autostart |  
| :----: | :----: | :----: | :----: | :----: | :----: | | :----: | | :----: |  
| projectname | directory | reference data | directory | beagle/mach/impute2 | b36/b37 | integer | TRUE/FALSE |
  
    
The columns explained:  
* project: the project name of your analysis  
* studyInputDir: the directory containing the study data split per chrosome in the PED/MAP format as explained in chapter 3  
* referencePanel: the directory containing the reference data generated as explained in chapter 4  
* imputationResultsDir: the output directory for all results  
* imputationPipeline: the pipeline to use, this can be one of the three described in this document  
* genomeBuild: the genome build to use. **Please make sure your study and referencedata are on the same genome build**  
* chr: the chromosome to run the analysis on  
* autostart: the value in this column specifies if the subsequent analysis steps in the minimac pipeline should be started/submitted automatically.  
  
An example worksheet using minimac and b37 is distributed in the Compute binary and can be found here:  
*protocols/imputation/minimac/exampleWorksheet.csv*
  
  
####7.3 Running an analysis using Molgenis Compute
The complete minimac pipeline consists of three steps. The first one (specified in workflowMinimacStage1.csv) creates the complete filestructure and all Compute jobs for the prephasing. The first three steps of this workflow consist of filtering the study data using ImputationTool, one might choose to remove these steps from the workflow if the study data is already filtered and qced. The second step takes care of the phasing of the data, this process is executed per chunk/region of a user specified number of markers/SNPs (default is 2000). The steps described in workflowMinimacStage3.csv perform the actual imputation and merge the chunk-based results back per chromosome. The pipeline can be executed using the following command:
  
  
>sh molgenis_compute.sh \\  
>-worksheet=protocols/imputation/minimac/exampleWorksheet.csv \\  
>-parameters=protocols/imputation/minimac/parametersMinimac.csv \\  
>-workflow=protocols/imputation/minimac/workflowMinimacStage1.csv \\  
>-protocols=protocols/imputation/minimac/protocols/ \\  
>-templates=protocols/imputation/minimac/protocols/ \\  
>-scripts=/your/output/directory/here/ \\  
>-id=runXX
  
  
All Compute jobs can now be found in the directory */compute/jobs/prepare/* in the *imputationResultDir* specified in the sampleWorksheet.csv. One now only has to submit the generated scripts. This can be done by typing the following command:
  
`cd /your/output/directory/here/`  
`sh submit.sh`  
**Note: Alternatively the generated s00_\*.sh scripts can be executed by hand**
  
  
###8. Imputation using minimacV2 pipeline
All protocols and files to run an imputation using Minimac[^3] can be found in the *protocols/imputation/minimacV2/* directory. The version 2 pipeline consists of three steps; preparing the data, phasing and imputation. Furthermore the pipeline has prerequisits which are listed in chapter 8.1.
  
  
####8.1 Tools
To run this pipeline the following tools, scripts and datasets are required:
  
* study data
* reference dataset in Ped/Map format (prepared using the workflow described in chapter 5.2)
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
  
  
####8.2 The sample worksheet for pre-phasing  
To start an analysis one needs to create a so called "worksheet". This worksheet should contain six columns and follow the format specified below:
  
| project | studyInputDir | prePhasingResultDir | imputationPipeline | genomeBuild | chr | autostart |  
| :----: | :----: | :----: | :----: | :----: | :----: | :----: |   
| projectname | directory | directory | beagle/mach/impute2 | b36/b37 | chromosome number | TRUE/FALSE |  
  
The columns explained:  
* project: the project name of your analysis  
* studyInputDir: the directory containing the study data split per chrosome in the PED/MAP format as explained in chapter 3  
* prePhasingResultDir: the output directory for the pre-phasing result  
* imputationPipeline: the pipeline to use, this can be one of the three described in this document  
* genomeBuild: the genome build to use. **Please make sure your study and referencedata are on the same genome build**  
* chr: the chromosome to run the analysis on  
* autostart: the value in this column specifies if the subsequent analysis steps in the minimac pipeline should be started/submitted automatically. **Note: This only works if in your cluster setup submission from nodes is allowed.**  
  
  
####8.3 Running an analysis  
The minimacV2 pipeline consists of three parts. The first one aligns all alleles to the reference genome using ImputationTool [^7], chunks the study data in a user specified number of samples and splits the chromosome in chunks by splitting on a specified number of SNPs. The second step phases the data using MaCH [^6]. The phasing only has to be done once for a specific study. The last step consist of imputing the phased data and concatenate the results into output files per chromosome. Since the phasing is independant of the referencepanel one only has to run the third step again when imputing with a different referencepanel.  
  
#####Step 1: preparing the study data  
After preparing the study PED/MAP files as described in chapter 3 and preparing the reference data as described in chapter 4 one needs to change the following parameter values in the `parametersMinimac.csv`:  
  
| name | defaultValue |  
| :----: | :----: |  
| scheduler | PBS/SGE/BSUB/GRID, depending on the backend |  
| stage | The command to load a module eg "module load" |  
| root | The root path were all tools, resources etc are |  
  
Optionally one can change parameters such as `$pythonversion` and `$javaversion` to accomodate own wishes.  
  
To start the study data preparation one can run the following command:  
  
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
**Note: Alternatively the generated s00_\*.sh scripts can be executed by hand.**  
  
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
>-o /your/output/directory/here/../ImputationWorksheet.csv  
  
**Note: nameOfImputationReference should be changed to the name of your reference panel, for example `giant1000gv3.20101123`.**  
  
When finished one can generate and execute the imputation jobs by executing the following commands:  
  
>sh molgenis_compute.sh \\  
>-inputdir=. \\  
>-worksheet=/your/output/directory/here/../ImputationWorksheet.csv \\  
>-parameters=protocols/imputation/minimacV2/parametersMinimac.csv \\  
>-workflow=protocols/imputation/minimacV2/protocols/../workflowMinimacStage3.csv \\  
>-protocols=protocols/imputation/minimacV2/protocols/ \\  
>-outputdir=/your/output/directory/here/../imputation/ \\  
>-id=runXX  
  
`cd /your/output/directory/here/../phasing`  
`sh submit.sh`  
  
The output is now ready for further analysis.  
  
  
####8.4 Output  
The pipeline produces several files which can be used for downstream analysis. The following files are produced: 
EXTEND THIS WHEN QUICKTEST IS IMPLEMENTED?  
  
  
  
####9 Appendix  
  
| Tool | Downloadlink |  
| :----: | :----: |  
| ChunkChromosome | http://www.bbmriwiki.nl/svn/ebiogrid/scripts/ChunkChromosome-2012-08-28/ |  
| minimac | http://www.bbmriwiki.nl/svn/ebiogrid/modules/minimac/beta-2012.10.3/minimac.beta-2012.10.3.tgz |  
| mach | http://www.bbmriwiki.nl/svn/ebiogrid/modules/mach/1.0.18/mach.1.0.18.Linux.tgz |  
| plink | http://www.bbmriwiki.nl/svn/ebiogrid/modules/plink/1.07-x86_64/plink-1.07-x86_64.tgz |  
| plink1.08 | http://www.bbmriwiki.nl/svn/ebiogrid/modules/plink/1.08/plink-1.08.tgz |  
| ImputationTool | Link Here |  
| ConcatWorksheets | http://www.bbmriwiki.nl/svn/ebiogrid/scripts/ConcatWorksheetsV1.0/ |  
| ExpandWorksheet | http://www.bbmriwiki.nl/svn/ebiogrid/scripts/ExpandWorksheetWithMergeWorksheetV1.1/ |  
  
Text














  
  
[^1]: See http://freemarker.org/ for a manual.
[^2]: http://mathgen.stats.ox.ac.uk/impute/impute_v2.html
[^3]: http://genome.sph.umich.edu/wiki/Minimac
[^4]: http://pngu.mgh.harvard.edu/~purcell/plink/data.shtml#ped
[^5]: http://pngu.mgh.harvard.edu/~purcell/plink/
[^6]: http://www.sph.umich.edu/csg/abecasis/MACH/tour/imputation.html
[^7]: http://www.bbmriwiki.nl/wiki/ImputationTool


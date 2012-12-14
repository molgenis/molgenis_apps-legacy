Next-generation sequencing data analysis using Molgenis Compute  
===============================================================
  
  
  
Content  
=======
  
  
1. Introduction  
2. The Molgenis Compute binary  
3. Analysis using Compute  
4. Analysis on the grid  
  
  
###1. Introduction  
  
  
Next-generation sequencing methods produce a growing volume of data, leading to increasing difficulties in analysing this data. This manual describes how one can simplify, parallelize and distribute such analysis across high performance compute architecture by using a standardized pipeline and the [Molgenis Compute] framework. The first chapter gives a short introduction on the [Molgenis Compute] framework followed by an overview of the content of the Compute binary. The third chapter describes how one can do NGS data analysis using the pipeline in [Molgenis Compute]. The pipeline is comprised of best-practice open-source software packages used in multiple institutions leading to 23 analysis steps. The four main parts of the pipeline are:  
  
* *Alignment:* here alignment is performed using Burrows-Wheeler Aligner [BWA]. The produced [SAM] file is converted to a binary format using [Picard] and sorted afterwards.  
* *Realignment:* in this part of the pipeline duplicate reads are marked using [Picard]. Afterwards realignment around known insertions and deletions (indels) from the Mills-Devine[^1] dataset using the Genome Analysis ToolKit [GATK] is performed. If reads are re-aligned, the fix-mates step will update the coordinates of the reads mate.  
* *Quality score recalibration:* Here the base quality scores of a read a recalibrated using covariate information. This method takes several covariates like cycle, dinucleotides, readgroup etc. into account and recalculates the quality scores, leading to reads being re-written with better empirical quality scores.  
* *Variant calling:* the last part of the pipeline performs indel and SNP calling using the [GATK]. The output of the pipeline are two VCF [reference] files, one with indels and one containing SNPs, ready for downstream analysis.  
  
Chapter three describes how one can execute the analysis pipeline on a cluster, supported backends are Portable Batch System (PBS), Sun Grid Engine (SGE), BSUB or a local machine. In the last chapter the manual describes how one can run the pipeline on the national Computing infrastructure for life Sciences, [eBioGrid].  
  
  
###2. The Molgenis Compute binary  
  
  
This chapter describes the content of the [Molgenis Compute] binary. Before starting we recommend to read the [Molgenis Compute manual].  
  
  
####2.1 Overview  
All next-generation sequencing protocols are stored in the *protocols/ngsWorkflowRealignmentAndSnpCalling/* directory. This directory contains multiple sub-directories:
  
* doc: this directory contains documentation for futher reference 
* protocols: in here are all the protocols needed for the analysis
* testdata: this directory contains a synthetic test dataset, containing paired-end reads which can be aligned on the first 700k bases on chr1 of the human reference genome build 37 at 40X coverage. This data was generated using [ART].  
  
The following files can also be found in this directory:  
  
* parameters.csv  
* workflow.csv  
* worksheet.csv  
  
  
###3 Analysis using compute  
  
  
This chapter describes how one can do the NGS data analysis via the commandline version. For this, one needs to setup tools and scripts and prepare reference data as described in the [Molgenis Compute NGS installation manual]. Afterwards one needs to prepare the `worksheet.csv` with the proper sample information after which the analysis can be started.  
  
  
####3.1 Preparing the worksheet  
Before starting an analysis one has to specify the samples to analyse. For this the `"worksheet.csv"` is used. An example worksheet including the obligatory fieds is depicted below:  
  
| internalSampleID | externalSampleID | project | sequencingStartDate | sequencer | run | flowcell | lane | seqType | capturingKit | barcode | barcodeType |  
| :----: | :----: | :----: | :----: | :----: | :----: | :----: | :----: | :----: | :----: | :----: | :----: |  
| autoincrement | sample ID | projectname | date of flowcell | sequencer ID | run ID | flowcell name | lane number | type of sequencing | capturing kit ID | barcode used | type of barcode |  
  
The columns explained:  
* internalSampleID: unique id which is an autoincrement. Type: integer  
* externalSampleID: sample ID. Type: string  
* project: projectname. Type: string  
* sequencingStartDate: date used in the flowcell name in the format of YYYYMMDD. Type: string  
* sequencer: sequencer ID. Type: string  
* run: run number as specified in flowcell name. Type: string  
* flowcell: flowcell name. Type: string  
* lane: lane number, this can be a value between 1-8. Type: int  
* seqType: type of sequencing, either Single Read (SR) or Paired-End (PE). Type: string  
* capturingKit: name of the enrichment kit used, eg. SureSelect_All_Exon_50MB. Type: string  
* barcode: barcode used, eg. CAACCT. Type: string  
* barcodeType: type of barcode used in-house (GAF) or Illumina (RPI). Type: string  
    
An example worksheet can be found here: *protocols/ngsWorkflowRealignmentAndSnpCalling/demoWorksheet.csv*  
  
  
####3.2 Starting an analysis  
All protocols to run the analysis can be found in the *protocols/ngsWorkflowRealignmentAndSnpCalling/* directory. All scripts/jobs for the four main steps described earlier are generated by one command.  
  
To generate all jobs for the analysis one can execute the following command:  
  
>sh molgenis_compute.sh \\  
>-inputdir=protocols/ngsWorkflowRealignmentAndSnpCalling/protocols/ \\  
>-worksheet=protocols/ngsWorkflowRealignmentAndSnpCalling/demoWorksheet.csv \\  
>-parameters=protocols/ngsWorkflowRealignmentAndSnpCalling/parameters.csv \\  
>-workflow=protocols/ngsWorkflowRealignmentAndSnpCalling/workflow.csv \\  
>-protocols=protocols/ngsWorkflowRealignmentAndSnpCalling/protocols/ \\  
>-outputdir=/your/output/directory/here/ \\  
>-id=run01  
  
All folders and jobs can be found in the output directory specified by the user. The last procedure of the preparation consists of executing the generated shell scripts. This can be done by typing the following command:
  
`cd /your/output/directory/here/`  
`sh submit.sh`  
**Note: Alternatively the generated s00_\*.sh scripts can be executed locally.**  
  
Analysis with Compute commandline is now started. A detailed description for execution on the grid can be found in the next chapter.  
  
  
###4. Analysis on the grid  
  
  
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
  >molgenis_apps/modules/compute/protocols/ngsWorkflowRealignmentAndSnpCalling/parameters.csv \\  
  >molgenis_apps/modules/compute/protocols/ngsWorkflowRealignmentAndSnpCalling/workflow.csv \\  
  >molgenis_apps/modules/compute/protocols/ngsWorkflowRealignmentAndSnpCalling/protocols/  
  
6. Generate imputation jobs in the database with the `importWorksheet.sh` from [deployment directory] and example worksheet:  
  >sh importWorksheet.sh \\  
  >molgenis_apps/modules/compute/protocols/ngsWorkflowRealignmentAndSnpCalling/workflow.csv \\  
  >molgenis_apps/modules/compute/protocols/ngsWorkflowRealignmentAndSnpCalling/demoWorksheet.csv \\  
  >run01  
  
7. Execute imputation with user credentials using pilot job system: 
  >sh runPilots.sh \\  
  >ui.grid.sara.nl \\  
  >\<username> \\  
  >\<password> \\  
  >grid  




[Molgenis Compute]: http://www.molgenis.org/wiki/ComputeStart (Molgenis Compute)  
[Molgenis Compute Manual]: https://github.com/molgenis/molgenis_apps/blob/testing/modules/compute/doc/UserManual.pdf  
[eBioGrid]: http://www.ebiogrid.nl/  
[clone_build.sh]: https://github.com/molgenis/molgenis_apps/blob/testing/modules/compute4/deployment/clone_build.sh  
[deployment directory]: https://github.com/molgenis/molgenis_apps/tree/testing/modules/compute4/deployment  
[pilot directory]: https://github.com/molgenis/molgenis_apps/tree/testing/modules/compute/pilots/grid  
[BWA]: http://bio-bwa.sourceforge.net/  
[SAM]: http://samtools.sourceforge.net/SAM1.pdf  
[Picard]: http://picard.sourceforge.net/  
[^1]: http://genome.cshlp.org/content/21/6/830.abstract  
[GATK]: http://www.broadinstitute.org/gatk/  
[Molgenis Compute NGS installation manual]: https://github.com/molgenis/molgenis_apps/blob/testing/modules/compute/protocols/ngsWorkflowRealignmentAndSnpCalling/doc/compute_ngs_installation_manual.md  




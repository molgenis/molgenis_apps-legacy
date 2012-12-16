#Next-generation sequencing pipeline
Next-generation sequencing methods produce a growing volume of data, leading to increasing difficulties in analysing this data. This manual describes how one can simplify, parallelize and distribute such analysis across high performance compute architecture by using a standardized pipeline and the [Molgenis Compute] framework. 

The pipeline is comprised of best-practice open-source software packages used in multiple institutions leading to 23 analysis steps. The four main parts of the pipeline are:  
  
* *Alignment:* here alignment is performed using Burrows-Wheeler Aligner [BWA]. The produced [SAM] file is converted to a binary format using [Picard] and sorted afterwards.  
* *Realignment:* in this part of the pipeline duplicate reads are marked using [Picard]. Afterwards realignment around known insertions and deletions (indels) from the Mills-Devine[^1] dataset using the Genome Analysis ToolKit [GATK] is performed. If reads are re-aligned, the fix-mates step will update the coordinates of the reads mate.  
* *Quality score recalibration:* Here the base quality scores of a read a recalibrated using covariate information. This method takes several covariates like cycle, dinucleotides, readgroup etc. into account and recalculates the quality scores, leading to reads being re-written with better empirical quality scores.  
* *Variant calling:* the last part of the pipeline performs indel and SNP calling using the [GATK]. The output of the pipeline are two VCF [reference] files, one with indels and one containing SNPs, ready for downstream analysis.  

Expected runtime is about 2 days, depending on whether you are doing exome or whole genome sequencing.
  
Below is first described how to prepare the 'worksheet' describing your analysis. Then is described how one can execute the analysis pipeline on a cluster (supported backends are Portable Batch System (PBS), Sun Grid Engine (SGE), BSUB or a local machine) and on the national BigGrid Computing infrastructure, [eBioGrid].
  
  
##Getting started

If you didn't do so, please download compute:

>create mycompute #place where you want to work
>wget <path to compute>
>unzip molgenis-compute-<version>.zip
>move molgenis-compute-<version>/* .
    
All next-generation sequencing protocols are stored in the `workflows/ngs/alignmentAndSnpCalling` directory. This folder has standard structure of *Compute* such as `parameters.csv`, `workflow.csv`, directory `protocols` and an example `worksheet.csv`.

Example data is available in `testdata/ngs`.
  
##Preparing the NGS worksheet  
Before starting an analysis one has to specify the samples to analyse. For this the `worksheet.csv` is used. An example worksheet including the obligatory fieds is depicted below:  
  
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
* barcodeType: type of barcode used. Type: string  
  
%what are settings for whole genome?
  
An example worksheet can be found here: `workflows/ngs/alignmentAndSnpCalling/worksheet.csv` 
  
  
##Run NGS workflow on cluster or local server
Running the pipeline on cluster or local server can be done from the commandline. It consists of the following steps:

* generate ngs analysis scripts
* submit ngs analysis scripts
* monitor

We assume that your are already on the cluster. Otherwise, please move there.

###One time setup of your environement
To setup Compute several default parameters in the `"parameters.csv"` file should be changed to your specific system/cluster environment. Changing these settings is necessary to execute the ngs pipeline. After changing these parameters the parameters file is ready. Changing the following environment parameters is obliged:  
  
* scheduler: Every scheduler has different job specification syntax, this parameter specifies which header for a specific scheduling system should be generated. The following scheduling systems are supported BSUB (BSUB), Portable Batch System (PBS) and Sun Grid Engine (SGE). To generate jobs for Grid usage the value GRID should be specified.  
* root: this is the "root" parameter shared by all other parameters. To ease the setup we recommend to install all tools in a *tools* directory and all resources in a *resources* directory in the "root".
  

###Generate NGS analysis scripts
To generate all jobs for the analysis one can execute the following command (change path to match your worksheet):  
  
>sh molgenis_compute.sh \\  
>-workflow=workflows/ngs/alignemntAndSnpCalling/protocols/ \\  
>-worksheet=workflows/ngs/alignemntAndSnpCalling/worksheet.csv \\  
>-outputdir=run01 \\  
>-id=run01  
  
All folders and jobs can be found in the `outputdir` directory (if ommitted this will be named identical to `id`). 

###Submit NGS analysis scripts
The last procedure of the preparation consists of executing the generated shell scripts. For this first copy your analysis to the cluster (if you are not there yet). This can be done by typing the following command (change "run01" to match your outputdir):
  
>cd run01    
>sh submit.sh  

Alternatively, the scripts can be run locally  

>sh runlocal.sh
  
Analysis with Compute commandline is now started. A detailed description for execution on the grid can be found in the next chapter.    

###Monitor NGS analysis scripts

%todo watch the .finished tags
  
##Run NGS workflow on BigGrid  
Running the pipeline on the grid requires use of MOLGENIS 'pilot' database. This database keeps track of all your jobs (as the grid is distributed and jobs sometimes get lost). Running ngs analysis on the grid consists of the following steps:

###One time setup of your environment
If you use BBMRI-NL VO you don't need to install all software binaries. Instead you can use default sofware available via the module system.

% is this needed???

However, you need to setup the path to your data. In `parameters.csv` change:
* root: this is the "root" parameter shared by all other parameters. To ease the setup we recommend to install all tools in a *tools* directory and all resources in a *resources* directory in the "root".
   
###Move input data to the grid  
Move your data to your `$HOME` space on the grid and upload it with the "srmcp" command to the "srm" storage. An example command is below (using example data):

upload data to ui node (change path to match your data) 
>scp testdata/*.fq.gz myname@ui.grid.sara.nl:.  

log into ui node (change myname) 
>ssh myname@ui.grid.sara.nl  

put data into srm (change myname and myproject)
>mysrm=srm://srm.grid.sara.nl:8443/pnfs/grid.sara.nl/data/bbmri.nl/myname/myproject  
>srmcp -server_mode=passive file:///$HOME/120308_SN163_0457_BD0E5CACXX_L4_CAACCT_1.fq.gz \\     
>$mysrm/120308_SN163_0457_BD0E5CACXX_L4_CAACCT_1.fq.gz  
>srmcp -server_mode=passive file:///$HOME/120308_SN163_0457_BD0E5CACXX_L4_CAACCT_2.fq.gz \\     
>$mysrm/120308_SN163_0457_BD0E5CACXX_L4_CAACCT_2.fq.gz 

verify
>srmls $mysrm

exit
>exit

###Start pilot database and load analysis

Start webserver on your own pc (requires external IP, change 8080 if needed)  

>kill -9 \`lsof -i :8080 -t`  
>cd molgenis_apps;  
>nohup ant -f build_compute.xml runOn -Dport=8080 &  
    
Import the first workflow into database by running the `importWorkflow.sh` from [deployment directory]. Files to be imported can be found here:  

>sh importWorkflow.sh \\  
>workflows/ngs/alignmentAndSnpCalling/parameters.csv \\  
>workflows/ngs/alignmentAndSnpCalling/workflow.csv \\  
>workflows/ngs/alignmentAndSnpCalling/protocols/  
  
Generate imputation jobs in the database with the `importWorksheet.sh` from [deployment directory] and example worksheet:  

>sh importWorksheet.sh \\  
>workflow.csv \\  
>ui.grid.sara.nl \\  
>workflows/ngs/alignmentAndSnpCalling/worksheet.csv \\  
>run01  

###Start pilot jobs on the grid and monitor 

Execute ngs analysis with user credentials using pilot job system: 

>sh runPilots.sh \\  
>ui.grid.sara.nl \\  
>\<username> \\  
>\<password> \\  
>grid  

When you monitor the database at http://localhost:8080 you can see if all your jobs are done (change localhost to remote host if you are running elsewhere). 

###Download aligned BAMs & VCF  

If so, download your result, when analysis is finished with the same "srmcp" command to UI node  

>srmcp -server_mode=passive srm://srm.grid.sara.nl:8443/pnfs/grid.sara.nl/  \\
>data/bbmri.nl/RP2/demo/120308_SN163_0457_BD0E5CACXX_L4_CAACCT.human_g1k_v37.recal.sorted.bam \\  
>file:///$HOME/120308_SN163_0457_BD0E5CACXX_L4_CAACCT.human_g1k_v37.recal.sorted.bam  

%can we have an easy script for this???

[Molgenis Compute]: http://www.molgenis.org/wiki/ComputeStart (Molgenis Compute)  
[Molgenis Compute manual]: https://github.com/molgenis/molgenis_apps/blob/testing/modules/compute/doc/UserManual.pdf  
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




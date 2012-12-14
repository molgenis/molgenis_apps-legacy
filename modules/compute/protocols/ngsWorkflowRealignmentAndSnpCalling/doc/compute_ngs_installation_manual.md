Installation of the Next-generation sequencing data analysis pipeline  
===================================================
  
Content  
=======
  
1. Installation and setup for commandline usage  
2. Installation of tools  
3. Installation of Molgenis Compute on the grid  
  
  
###1. Installation and setup for commandline usage  
  
  
This chapter describes the content of the [Molgenis Compute] binary and how it should be installed. Before starting we recommend to read the [Molgenis Compute manual].  
  
  
####1.1 Installation of Molgenis Compute
To install [Molgenis Compute] one should download and unpack the binary using the following command:  
  
>unzip molgenis_compute-\<version\>.zip  
>cd molgenis_compute-\<version\>  
  
####1.2 Overview  
All next-generation sequencing protocols are stored in the *protocols/ngsWorkflowRealignmentAndSnpCalling/* directory. This directory contains multiple sub-directories:
  
* doc: this directory contains documentation for futher reference 
* protocols: in here are all the protocols needed for the analysis
* testdata: this directory contains a synthetic test dataset, containing paired-end reads which can be aligned on the first 700k bases on chr1 of the human reference genome build 37 at 40X coverage. This data was generated using [ART].  
  
The following files can also be found in this directory:  
  
* parameters.csv  
* workflow.csv  
* worksheet.csv  
  
####1.3 General setup  
To setup Compute several default parameters in the `"parameters.csv"` file should be changed to your specific system/cluster environment. Changing these settings is necessary to execute the ngs pipeline. After changing these parameters the parameters file is ready. Changing the following environment parameters is obliged:  
  
* scheduler: Every scheduler has different job specification syntax, this parameter specifies which header for a specific scheduling system should be generated. The following scheduling systems are supported BSUB (BSUB), Portable Batch System (PBS) and Sun Grid Engine (SGE). To generate jobs for Grid usage the value GRID should be specified.  
* root: this is the "root" parameter shared by all other parameters. To ease the setup we recommend to install all tools in a *tools* directory and all resources in a *resources* directory in the "root".
  
  
###2 Installation of tools  
  
  
This chapter describes how to install the tools and reference data for the NGS pipeline.  
  
  
####2.1 Tools used in analysis  
To run the pipeline one should first install all needed tools. We recommend to setup a directory `$root` directory which contains the following folders; *tools/* and *resources*. The following tools should be installed in the *tool/* directory:  
  
| Tools | Downloadlink |  
| :----: | :----: |  
| GATK | http://www.bbmriwiki.nl/svn/ebiogrid/modules/GATK/1.0.5069/GATK-1.0.5069.tgz |  
| BWA | http://www.bbmriwiki.nl/svn/ebiogrid/modules/bwa/0.5.8c_patched/bwa-0.5.8c_patched.tgz |  
| fastQC | http://www.bbmriwiki.nl/svn/ebiogrid/modules/fastqc/v0.10.1/fastqc-v0.10.1.tgz |  
| picard-tools | http://www.bbmriwiki.nl/svn/ebiogrid/modules/picard-tools/1.61/picard-tools-1.61.tgz |  
  
  
Scripts should be downloaded in installed in *tools/scripts/*. All scripts can be downloaded here:  
  
| script | Downloadlink |  
| :----: | :----: |  
| coverage.R | http://www.bbmriwiki.nl/svn/ebiogrid/scripts/coverage.R |  
| createInsertSizePlot | http://www.bbmriwiki.nl/svn/ebiogrid/scripts/createInsertSizePlot.zip |  
| filterSingleSampleCalls.pl | http://www.bbmriwiki.nl/svn/ebiogrid/scripts/filterSingleSampleCalls.pl |  
| plot_cumulative_coverage | http://www.bbmriwiki.nl/svn/ebiogrid/scripts/plot_cumulative_coverage-1.1.R |  
  
  
All resources can be downloaded here: insert_link  
After extracting the resources one can just move the *resources/* directory onder the `$root` since they are packed in the correct folder structure.  
  
  
After installing all tools and resources one should have the following directory structure:  
  
  
        -root  
          |
          +tools
          |   |
          |   +scripts
          |   |   <analysis scripts>
          |   |
          |   +<several tools, one tool per folder> SEE: Note 1
          |  
          +resources
               |
               +hg19
                  |
                  +dbsnp
                  |   <dbsnp files>
                  |
                  +indels
                  |   <indel files (VCF)>
                  |
                  +indices
                  |   <human_g1k_v37.fa files>
                  |
                  +intervals
                      <interval_list files>  
                      
        Note:  
        1 These tools are the ones described above  
        2 When using the grid as described in chapter 4 all tools are installed and usable as `module`.   
  
  
###3 Installation of Molgenis Compute on the grid
  
  
To run Molgenis Compute on the grid one needs to prepare a webserver with the following requierements:  
* java 1.6.0 or higher  
* git 1.7.1 or higher  
* ant 1.7.1 or higher  
* mysql 5.1.54 or higher  
  
The whole installation can be done in three steps.  
  
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
  
3. Setup environment on the grid  
  Copy `maverick.sh`, `maverick.jdl` and `dataTransferSRM.sh` from [pilot directory] to your `$HOME/maverick` directory on the grid ui-node by executing the following command:  
  >scp maverick.sh maverick.jdl dataTransferSRM.sh \<username>@ui.grid.sara.nl  
  
  Edit `maverick.sh`, specify your ip and port of your webserver, which is started on step 3:  
  >export WORKDIR=$TMPDIR  
  >source dataTransferSRM.sh  
  >curl  -F status=started http://<ip>:<port>/compute/api/pilot > script.sh  
  >sh script.sh 2>&1 | tee -a log.log  
  >curl -F status=done -F log_file=@log.log http://<ip>:<port>/compute/api/pilot  
  
Your environment is ready for usage.  
  
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
[GATK]: http://www.broadinstitute.org/gatk  

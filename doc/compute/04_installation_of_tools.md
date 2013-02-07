
#Installation of tools  

This chapter describes how to install the tools and reference data.  
  
##Tools for imputation
On BBMRI-NL VO of BigGrid and the cluster.gcc.rug.nl cluster all binaries needed are installed.
To run the imputation pipeline on another server the following tools and scripts are required:
  
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

  
##Tools for NGS  
On BBMRI-NL VO of BigGrid and the cluster.gcc.rug.nl cluster all binaries needed are installed.

To run the NGS pipeline elsehwhere one should first install all needed tools. We recommend to setup a directory `$root` directory which contains the following folders; *tools/* and *resources*. The following tools should be installed in the *tool/* directory:  
  
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
  
  

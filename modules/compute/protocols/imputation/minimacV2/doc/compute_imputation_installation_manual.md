Installation manual of the Molgenis Compute imputation pipeline
=================================
  


Content
=======

  
1. General installation  
2. Installation of tools  
  

###1. General installation
  
  
This chapter describes the content of the [Molgenis Compute] binary and how it should be installed. Before starting we recommend to read the [Molgenis Compute manual].  
  
  
####1.1 Installation of Molgenis Compute
The first thing to do is to unpack the Molgenis Compute distro. To unpack the distro execute the following command:  
  
>unzip molgenis_compute-\<version\>.zip  
>cd molgenis_compute-\<version\>  
  
  
####1.2 Imputation overview  
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
  
  
####1.3 General compute settings for imputation  
To setup Compute several default parameters in the `"parameters.csv"` file should be changed to your specific system/cluster environment. Changing these settings is necessary to execute the imputation pipeline. After changing these parameters the parameters file is ready. Changing the following environment parameters is obliged:  
  
* scheduler: Every scheduler has different job specification syntax, this parameter specifies which header for a specific scheduling system should be generated. The following scheduling systems are supported BSUB (BSUB), Portable Batch System (PBS) and Sun Grid Engine (SGE). To generate jobs for Grid usage the value GRID should be specified.  
* root: this is the "root" parameter shared by all other parameters. To ease the setup we recommend to install all tools in a *tools* directory and all resources in a *resources* directory in the "root".    
  
  
###2. Installation of tools  
  
  
This chapter shortly describes how one should install the tools needed for the imputation pipeline.  
  
  
####2.1 Tools
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
  
  
###Appendix  
  
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
[Molgenis Compute Manual]: https://github.com/molgenis/molgenis_apps/blob/testing/modules/compute/doc/UserManual.pdf
[VCFTools]: http://vcftools.sourceforge.net/
[clone_build.sh]: https://github.com/molgenis/molgenis_apps/blob/testing/modules/compute4/deployment/clone_build.sh  
[deployment directory]: https://github.com/molgenis/molgenis_apps/tree/testing/modules/compute4/deployment  
[pilot directory]: https://github.com/molgenis/molgenis_apps/tree/testing/modules/compute/pilots/grid
[eBioGrid]: http://www.ebiogrid.nl/

Imputation using Molgenis Compute
============================

Blaat blaat blaat etc.

Content
=======

1.	Introduction
2.	Running compute
3.	Worksheet
4.	Parameters
5.	Protocols
6.	Workflows
7.	Imputation


Introduction
============

Parameters
==========

Worksheet
=========

Folding the worksheet
---------------------

[sec:folding] É

Protocol
========

A protocol is a template in the Freemarker[^1] language that describes
the work to be done. A protocol may therefore contain one or more
parameters, like `${myFile}`, that are specified in your parameter list
or worksheet. A protocol consists of the following four parts as
explained below.

Header
------

The header of a protocol may contain the following line in which you
specify the hardware requirements for your analysis.

`#MOLGENIS walltime=hh:mm:ss mem=m nodes=n cores=c`

where walltime is the maximum execution time, $m$ is the memory (*e.g.*
512MB or 4GB), $n$ is the number of nodes and $c$ is the number of cores
that you request for the execution of this analysis.

Usually, a protocol is be applied to every row in the worksheet.
However, some protocols may apply to specific targets (*i.e.* parameters
that are specified in the worksheet); see Section [sec:folding] for more
detail. The targets (say $target1$ and $target2$) are specified with the
following statement.

`#FOREACH target1, target2`

Download your data
------------------

A protocol may be executed in a distributed environment. As a result,
the data may not be available where the execution takes place.
Therefore, one should first download the data to the execution node. For
every file that you want to use in the analysis protocol, you need to
include the following statement in the protocol before using it.

`getFile "$myInputFile"`

Where `"myInputFile"` is a parameter in your parameter list that refers
to the file. If this file is an executable, you may want to make it
executable by adding the following statement to your protocol.

`chmod +x "$myInputFile"`

Analysis template
-----------------

After downloading the data you can specify the analysis you want to run.

Upload your data
----------------

After finishing the analysis, you may save the files you want to keep by
including the following statement at the end of your protocol.

`putFile "$myOutputFile"`

Where `"myOutputFile"` again is a parameter in your parameter list that
refers to the respective file.

Workflow
========



Imputation
==========

All imputation protocols are stored in the *protocols/imputation/* directory. This
directory contains multiple sub-directories:
  
* beagle
* impute2
* mach_minimach
* prepareReference
  
Each of these directories contain the following files to be used as input for Molgenis
Compute:

* parameters.csv
* workflow.csv
* worksheet.csv
* protocols (*.ftl files)
  
  
Preparing the reference dataset
-------------------------------
  
  
To start imputation on a cohort one first has to prepare the reference data. The
*prepareReference* directory contains several protocols to prepare this data. 
  
###Generating a reference data set
This workflow requires a VCF file containing phased genotypes as input. The workflow requires the VCF files to be split per chromosome in a folder named *vcf*. When executing this workflow two folders containing the data in Impute2 and TriTyper format per chromosome are generated. 
  
  
An example test reference set can be created using the following command:
>sh molgenis_compute.sh \\  
>*-*worksheet=protocols/imputation/prepareReference/prepareReferenceWorksheetSample.csv \\  
>*-*parameters=protocols/imputation/prepareReference/parameters.csv \\  
>*-*workflow=protocols/imputation/prepareReference/prepareReferenceWorkflow.csv \\  
>*-*protocols=protocols/imputation/prepareReference/ \\  
>*-*templates=protocols/imputation/prepareReference/ \\  
>*-*scripts=/your/output/directory/here/ \\  
>*-*id=runXX 
  
###Output example  
Executing the above mentioned command will result in a directory with the following structure:
  


       -referenceName
			-Impute2
				chrNumber.impute.hap
				chrNumber.impute.hap.indv
				chrNumber.impute.legend
			-TriTyper
				-ChrchrNumber
					GenotypeMatrix.dat
					Individuals.txt
					PhenotypeInformation.txt
					SNPMappings.txt
					SNPs.txt
			-vcf
				chrNumber.vcf
				chrNumber.vcf.vcfidx  

	Note: 
	- The vcf directory contains the input VCF files split per chromosome. The *.vcf.vcfidx file is 	generated in this workflow.
	- Genetic recombination maps for each chromosome should be added to the Impute2 directory manually.  
	Afterwards the filename convention should be specified in the parameters.csv file of the impute2 workflow.

  
**HOW CAN NOTE 2 BE EXPLAINED BETTER?**

When all these files are present the reference dataset is ready to be used.
  
  
Imputation using Impute2
------------------------
  
  
All protocols and files to run an imputation using Impute2 can be found in the *protocols/imputation/impute2/* directory.






Imputation using Beagle
-----------------------



Imputation using Mach
---------------------




[^1]: See http://freemarker.org/ for a manual.

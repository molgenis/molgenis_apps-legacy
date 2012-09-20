Compute using Impute2 manual
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

All imputation protocols are stored in the protocols/imputation/ directory. This
directory contains multiple sub-directories:

* beagle
* impute2
* mach_minimach
* prepareReference


Imputation using Impute2
------------------------








[^1]: See http://freemarker.org/ for a manual.

# Configures the GCC bash environment
. /target/gpfs2/gcc/gcc.bashrc

##### BEFORE #####
touch $PBS_O_WORKDIR/s00_QCReport_SCA_B_lane6_testcompute4.out
source /target/gpfs2/gcc/tools/scripts/import.sh
before="$(date +%s)"
echo "Begin job s00_QCReport_SCA_B_lane6_testcompute4 at $(date)" >> $PBS_O_WORKDIR/RUNTIME.log

echo Running on node: `hostname`

sleep 60
###### MAIN ######
# This script is processing samples:
# 1_D01_1683_RF4
# 2_D01_1640_RF4
# 3_D96_0268_RF14
# 4_D96_0264_RF14

#
# =====================================================
# $Id$
# $URL$
# $LastChangedDate$
# $LastChangedRevision$
# $LastChangedBy$
# =====================================================
#

#MOLGENIS walltime=00:05:00
#FOREACH project
#DOCUMENTATION Documentation of QCReport.ftl, /target/gpfs2/gcc/tools/getStatistics_23jan2012/getStatistics.R


# We need some parameters folded per sample:

# parameters in *.tex template:

inputs "/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//1_D01_1683_RF4.hsmetrics" "/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//2_D01_1640_RF4.hsmetrics" "/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//3_D96_0268_RF14.hsmetrics" "/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//4_D96_0264_RF14.hsmetrics"
inputs "/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//1_D01_1683_RF4.alignmentmetrics" "/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//2_D01_1640_RF4.alignmentmetrics" "/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//3_D96_0268_RF14.alignmentmetrics" "/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//4_D96_0264_RF14.alignmentmetrics"
inputs "/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//1_D01_1683_RF4.insertsizemetrics" "/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//2_D01_1640_RF4.insertsizemetrics" "/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//3_D96_0268_RF14.insertsizemetrics" "/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//4_D96_0264_RF14.insertsizemetrics"
inputs "/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//121010_SN163_0477_AD1C6EACXX_L6_AGAGAT.human_g1k_v37.dedup.metrics" "/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//121010_SN163_0477_AD1C6EACXX_L6_TCAGTT.human_g1k_v37.dedup.metrics" "/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//121010_SN163_0477_AD1C6EACXX_L6_CAACCT.human_g1k_v37.dedup.metrics" "/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//121010_SN163_0477_AD1C6EACXX_L6_GGAACT.human_g1k_v37.dedup.metrics"
inputs "/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//1_D01_1683_RF4.concordance.ngsVSarray.txt" "/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//2_D01_1640_RF4.concordance.ngsVSarray.txt" "/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//3_D96_0268_RF14.concordance.ngsVSarray.txt" "/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//4_D96_0264_RF14.concordance.ngsVSarray.txt"
inputs /target/gpfs2/gcc/tools/getStatistics_23jan2012/NiceColumnNames.csv

export PATH=/target/gpfs2/gcc/tools/R//bin:${PATH}
export R_LIBS=/target/gpfs2/gcc/tools/GATK-1.3-24-gc8b1c92/gsalib/

# get general sample statistics
Rscript /target/gpfs2/gcc/tools/getStatistics_23jan2012/getStatistics.R \
--hsmetrics "/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//1_D01_1683_RF4.hsmetrics","/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//2_D01_1640_RF4.hsmetrics","/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//3_D96_0268_RF14.hsmetrics","/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//4_D96_0264_RF14.hsmetrics" \
--alignment "/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//1_D01_1683_RF4.alignmentmetrics","/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//2_D01_1640_RF4.alignmentmetrics","/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//3_D96_0268_RF14.alignmentmetrics","/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//4_D96_0264_RF14.alignmentmetrics" \
--insertmetrics "/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//1_D01_1683_RF4.insertsizemetrics","/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//2_D01_1640_RF4.insertsizemetrics","/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//3_D96_0268_RF14.insertsizemetrics","/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//4_D96_0264_RF14.insertsizemetrics" \
--dedupmetrics "/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//121010_SN163_0477_AD1C6EACXX_L6_AGAGAT.human_g1k_v37.dedup.metrics","/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//121010_SN163_0477_AD1C6EACXX_L6_TCAGTT.human_g1k_v37.dedup.metrics","/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//121010_SN163_0477_AD1C6EACXX_L6_CAACCT.human_g1k_v37.dedup.metrics","/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//121010_SN163_0477_AD1C6EACXX_L6_GGAACT.human_g1k_v37.dedup.metrics" \
--concordance "/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//1_D01_1683_RF4.concordance.ngsVSarray.txt","/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//2_D01_1640_RF4.concordance.ngsVSarray.txt","/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//3_D96_0268_RF14.concordance.ngsVSarray.txt","/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//4_D96_0264_RF14.concordance.ngsVSarray.txt" \
--sample "1_D01_1683_RF4","2_D01_1640_RF4","3_D96_0268_RF14","4_D96_0264_RF14" \
--colnames /target/gpfs2/gcc/tools/getStatistics_23jan2012/NiceColumnNames.csv \
--csvout /target/gpfs2/gcc/groups/in-house/projects/SCA_B_lane6_testcompute4/output/qc/SCA_B_lane6_testcompute4_QCStatistics.csv \
--tableout /target/gpfs2/gcc/groups/in-house/projects/SCA_B_lane6_testcompute4/output/qc/SCA_B_lane6_testcompute4_qcstatisticstable.tex \
--descriptionout /target/gpfs2/gcc/groups/in-house/projects/SCA_B_lane6_testcompute4/output/qc/SCA_B_lane6_testcompute4_qcstatisticsdescription.tex \
--baitsetout /target/gpfs2/gcc/groups/in-house/projects/SCA_B_lane6_testcompute4/output/qc/projectbaitset.txt \
--qcdedupmetricsout /target/gpfs2/gcc/groups/in-house/projects/SCA_B_lane6_testcompute4/output/qc/dedupmetrics.txt

# get dedup info per flowcell-lane-barcode/sample
Rscript /target/gpfs2/gcc/tools/scripts/getDedupInfo_23mar2012/getDedupInfo.R \
--dedupmetrics "/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//121010_SN163_0477_AD1C6EACXX_L6_AGAGAT.human_g1k_v37.dedup.metrics","/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//121010_SN163_0477_AD1C6EACXX_L6_TCAGTT.human_g1k_v37.dedup.metrics","/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//121010_SN163_0477_AD1C6EACXX_L6_CAACCT.human_g1k_v37.dedup.metrics","/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//121010_SN163_0477_AD1C6EACXX_L6_GGAACT.human_g1k_v37.dedup.metrics" \
--flowcell "AD1C6EACXX","AD1C6EACXX","AD1C6EACXX","AD1C6EACXX" \
--lane "6","6","6","6" \
--sample "1_D01_1683_RF4","2_D01_1640_RF4","3_D96_0268_RF14","4_D96_0264_RF14" \
--paired TRUE \
--qcdedupmetricsout "/target/gpfs2/gcc/groups/in-house/projects/SCA_B_lane6_testcompute4/output/qc/dedupmetrics.txt"

# get snp stats per sample
Rscript /target/gpfs2/gcc/tools/scripts/createSNPTable_19mar2012/createSNPTable.R \
--sample "1_D01_1683_RF4","2_D01_1640_RF4","3_D96_0268_RF14","4_D96_0264_RF14" \
--type "/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//1_D01_1683_RF4.snps.final.type.txt","/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//2_D01_1640_RF4.snps.final.type.txt","/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//3_D96_0268_RF14.snps.final.type.txt","/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//4_D96_0264_RF14.snps.final.type.txt" \
--class "/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//1_D01_1683_RF4.snps.final.class.txt","/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//2_D01_1640_RF4.snps.final.class.txt","/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//3_D96_0268_RF14.snps.final.class.txt","/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//4_D96_0264_RF14.snps.final.class.txt" \
--impact "/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//1_D01_1683_RF4.snps.final.impact.txt","/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//2_D01_1640_RF4.snps.final.impact.txt","/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//3_D96_0268_RF14.snps.final.impact.txt","/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//4_D96_0264_RF14.snps.final.impact.txt" \
--typetableout "/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//SCA_B_lane6_testcompute4.snps.final.type.tex" \
--classtableout "/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//SCA_B_lane6_testcompute4.snps.final.class.tex" \
--impacttableout "/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//SCA_B_lane6_testcompute4.snps.final.impact.tex"


# create workflow figure
echo "digraph G {QCReport; }" | /target/gpfs2/gcc/tools/graphviz-2.28.0/bin/dot -Tpng > /target/gpfs2/gcc/groups/in-house/projects/SCA_B_lane6_testcompute4/output/qc/SCA_B_lane6_testcompute4_workflow.png

# save latex template in file
echo "\documentclass[a4paper,12pt]{article}
\usepackage{nameref}
\usepackage{grffile}
\usepackage{graphicx}
\usepackage[strings]{underscore}
\usepackage{verbatim}
\usepackage{wrapfig}
\usepackage{lastpage}

\begin{comment}
#
# =====================================================
# $Id$
# $URL$
# $LastChangedDate$
# $LastChangedRevision$
# $LastChangedBy$
# =====================================================
#
\end{comment}

\newenvironment{narrow}[2]{
  \begin{list}{}{
    \setlength{\leftmargin}{#1}
    \setlength{\rightmargin}{#2}
    \setlength{\listparindent}{\parindent}
    \setlength{\itemindent}{\parindent}
    \setlength{\parsep}{\parskip}
  }
  \item[]
}{\end{list}}

\title{Next Generation Sequencing report}
\author{\small Genome Analysis Facility (GAF), Genomics Coordination Centre (GCC)\\\\
\small University Medical Centre Groningen}

\begin{document}
\maketitle
\thispagestyle{empty}
\vspace{40mm}

\begin{table}[h]
	\centering
	\begin{tabular}{l l}
		\hline
		\multicolumn{2}{l}{\textbf{Report}} \\\\
		Created on & \today \\\\
		Number of pages & \\pageref{LastPage} \\\\ \\\\
		Generated by & MOLGENIS Compute \\\\
		\\\\
		\multicolumn{2}{l}{\textbf{Project}} \\\\
		Project name & SCA_B_lane6_testcompute4 \\\\
		Number of samples & 4 \\\\
		\\\\
		\multicolumn{2}{l}{\textbf{Customer}} \\\\
		Principal investigator & Dineke Verbeek / d.s.verbeek@umcg.nl \\\\
		\\\\
		\multicolumn{2}{l}{\textbf{Contact}} \\\\
		Name & Cleo C. van Diemen \\\\
		E-mail & c.c.van.diemen@umcg.nl \\\\
		\hline
	\end{tabular}
\end{table}

\clearpage
\tableofcontents

\clearpage
\section*{Introduction}
\addcontentsline{toc}{section}{Introduction}
This report describes a series of statistics about your sequencing data. Together with this report you'll receive a SNP-list. If you, in addition, also want the raw data, then please notify us via e-mail. In any case we'll delete the raw data, three months after \today.

\clearpage
\section*{Project analysis results}
\addcontentsline{toc}{section}{Project analysis results}

\subsection*{Overview statistics}
\addcontentsline{toc}{subsection}{Overview statistics}
\label{subsect:overviewstatistics}
% statistics table
\input{/target/gpfs2/gcc/groups/in-house/projects/SCA_B_lane6_testcompute4/output/qc/SCA_B_lane6_testcompute4_qcstatisticstable.tex}

\begin{minipage}{\textwidth}
	Name of the bait set(s) used in the hybrid selection for this project:\\\\
	\textbf{\input{/target/gpfs2/gcc/groups/in-house/projects/SCA_B_lane6_testcompute4/output/qc/projectbaitset.txt}}
\end{minipage}

\clearpage
\subsection*{Description statistics table}
\addcontentsline{toc}{subsection}{Description statistics table}
\begin{table}[h!]
	\centering
	\begin{tabular}{r p{12cm}}
		\input{/target/gpfs2/gcc/groups/in-house/projects/SCA_B_lane6_testcompute4/output/qc/SCA_B_lane6_testcompute4_qcstatisticsdescription.tex}
	\end{tabular}
\end{table}

\clearpage
\subsection*{Capturing}
\addcontentsline{toc}{subsection}{Capturing}
The following figures show the cumulative depth distribution in the target regions that are located on \emph{chromosome 1}. The fractions of bases that is covered with at least 10x, 20x and 30x are marked with a dot. Please see section \"\\nameref{subsect:overviewstatistics}\" for the full coverage statistics per sample; \emph{i.e.}, in the target regions on \emph{all chromosomes}.
\begin{figure}[ht]\begin{minipage}{0.5\linewidth}\caption{sample \textbf{1_D01_1683_RF4}}\centering\includegraphics[width=\textwidth]{/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//1_D01_1683_RF4.coverageplot.pdf}\end{minipage}\hspace{1cm}\begin{minipage}{0.5\linewidth}\caption{sample \textbf{2_D01_1640_RF4}}\centering\includegraphics[width=\textwidth]{/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//2_D01_1640_RF4.coverageplot.pdf}\end{minipage}\hspace{1cm}\begin{minipage}{0.5\linewidth}\caption{sample \textbf{3_D96_0268_RF14}}\centering\includegraphics[width=\textwidth]{/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//3_D96_0268_RF14.coverageplot.pdf}\end{minipage}\hspace{1cm}\begin{minipage}{0.5\linewidth}\caption{sample \textbf{4_D96_0264_RF14}}\centering\includegraphics[width=\textwidth]{/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//4_D96_0264_RF14.coverageplot.pdf}\end{minipage}\hspace{1cm}\end{figure}

\clearpage
\subsection*{Insert size distribution}
\addcontentsline{toc}{subsection}{Insert size distribution}
The following figures show the insert size distribution per sample. Insert refers to the base pairs that are ligated between the adapters.
\begin{figure}[ht]\begin{minipage}{0.5\linewidth}\caption{sample \textbf{1_D01_1683_RF4}}\centering\includegraphics[width=\textwidth]{/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//1_D01_1683_RF4.insertsizemetrics.pdf}\end{minipage}\hspace{1cm}\begin{minipage}{0.5\linewidth}\caption{sample \textbf{2_D01_1640_RF4}}\centering\includegraphics[width=\textwidth]{/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//2_D01_1640_RF4.insertsizemetrics.pdf}\end{minipage}\hspace{1cm}\begin{minipage}{0.5\linewidth}\caption{sample \textbf{3_D96_0268_RF14}}\centering\includegraphics[width=\textwidth]{/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//3_D96_0268_RF14.insertsizemetrics.pdf}\end{minipage}\hspace{1cm}\begin{minipage}{0.5\linewidth}\caption{sample \textbf{4_D96_0264_RF14}}\centering\includegraphics[width=\textwidth]{/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//4_D96_0264_RF14.insertsizemetrics.pdf}\end{minipage}\hspace{1cm}\end{figure}

%\clearpage
%\subsection*{Demultiplex statistics}
%\addcontentsline{toc}{subsection}{Demultiplex statistics}
%Under construction...
%displaystats(demultiplexstats)

%\clearpage
%\subsection*{GC metrics}
%\addcontentsline{toc}{subsection}{GC metrics}
%The following figures show the GC-content distribution per sample.
%\begin{figure}[ht]\begin{minipage}{0.5\linewidth}\caption{sample \textbf{1_D01_1683_RF4}}\centering\includegraphics[width=\textwidth]{/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//1_D01_1683_RF4.gcbiasmetrics.pdf}\end{minipage}\hspace{1cm}\begin{minipage}{0.5\linewidth}\caption{sample \textbf{2_D01_1640_RF4}}\centering\includegraphics[width=\textwidth]{/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//2_D01_1640_RF4.gcbiasmetrics.pdf}\end{minipage}\hspace{1cm}\begin{minipage}{0.5\linewidth}\caption{sample \textbf{3_D96_0268_RF14}}\centering\includegraphics[width=\textwidth]{/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//3_D96_0268_RF14.gcbiasmetrics.pdf}\end{minipage}\hspace{1cm}\begin{minipage}{0.5\linewidth}\caption{sample \textbf{4_D96_0264_RF14}}\centering\includegraphics[width=\textwidth]{/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//4_D96_0264_RF14.gcbiasmetrics.pdf}\end{minipage}\hspace{1cm}\end{figure}

%\clearpage
%\subsection*{SNP statistics}
%\addcontentsline{toc}{subsection}{SNP statistics}
%The tables with caption 'Functional type', 'Functional class' and 'Functional impact', classify the SNPs, based on Ensembl, build 37.64.
%\input{/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//SCA_B_lane6_testcompute4.snps.final.type.tex}
%\input{/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//SCA_B_lane6_testcompute4.snps.final.class.tex}
%\input{/target/gpfs2/gcc/tmp/SCA_B_lane6_testcompute4/output//SCA_B_lane6_testcompute4.snps.final.impact.tex}

\clearpage
\subsection*{Duplication rates}
\addcontentsline{toc}{subsection}{Duplication rates}
\input{/target/gpfs2/gcc/groups/in-house/projects/SCA_B_lane6_testcompute4/output/qc/dedupmetrics.txt}

\clearpage
\section*{Appendix 1: Genome Analysis Facility Pipeline}
\addcontentsline{toc}{section}{Appendix 1: Genome Analysis Facility Pipeline}
\subsection*{Exome sequencing}
\addcontentsline{toc}{subsection}{Exome sequencing}
\begin{wrapfigure}{r}{0.5\textwidth}
	\begin{center}
		\includegraphics[width=.5\textwidth]{/target/gpfs2/gcc/tools/getStatistics_23jan2012/GAFpipeline.png}
	\end{center}
	\caption{Workflow in the lab}
	\label{fig:wet}
\end{wrapfigure}
Figure \ref{fig:wet} illustrated the basic experimental process of exome capture sequencing. The Genomic DNA sample was randomly fragmented using Nebulisation. Then barcoded adapters were ligated to both ends of the resulting fragments, according the standard New England Biolabs protocol. Fragments with an insert size of 220 bp on average were excised using the Caliper XT gel system and the extracted DNA was amplified with PCR.

The quality of the product was verified on the BioRad Experion instrument. If the quality of the product meets the criteria, the product is multiplexed in an equimolar pool of 4 simular products. This pool is hybridized to the Agilent SureSelect All exon V2, according the provided protocol. After amplification of the enriched products with PCR the quality of the products is verified on the BioRad Experion instrument and Paired End sequenced on the HiSeq2000 with 100 bp reads. Image Files were processed using standard Illumina basecalling software and the generated reads are ready for downstream processing after demultiplexing.

\clearpage
\section*{Appendix 2: Bioinformatics pipeline}
\addcontentsline{toc}{section}{Appendix 2: Bioinformatics pipeline}
Your samples have been anlayzed with bioinformatics pipeline shown in Figure \ref{fig:dry}.
\begin{figure}[h]
	\caption{Bioinformatics pipeline. The ovals describe the steps in the pipeline. The arrows indicate the work flow of data between the steps.}
	\begin{center}
		\includegraphics[width=.9\textwidth]{/target/gpfs2/gcc/groups/in-house/projects/SCA_B_lane6_testcompute4/output/qc/SCA_B_lane6_testcompute4_workflow.png}
	\end{center}
	\label{fig:dry}
\end{figure}
\end{document}" > /target/gpfs2/gcc/groups/in-house/projects/SCA_B_lane6_testcompute4/output/qc/SCA_B_lane6_testcompute4_QCReport.tex

pdflatex -output-directory=/target/gpfs2/gcc/groups/in-house/projects/SCA_B_lane6_testcompute4/output/qc /target/gpfs2/gcc/groups/in-house/projects/SCA_B_lane6_testcompute4/output/qc/SCA_B_lane6_testcompute4_QCReport.tex
pdflatex -output-directory=/target/gpfs2/gcc/groups/in-house/projects/SCA_B_lane6_testcompute4/output/qc /target/gpfs2/gcc/groups/in-house/projects/SCA_B_lane6_testcompute4/output/qc/SCA_B_lane6_testcompute4_QCReport.tex 

###### AFTER ######
after="$(date +%s)"
elapsed_seconds="$(expr $after - $before)"
echo Completed s00_QCReport_SCA_B_lane6_testcompute4 at $(date) in $elapsed_seconds seconds >> $PBS_O_WORKDIR/RUNTIME.log
touch $PBS_O_WORKDIR/s00_QCReport_SCA_B_lane6_testcompute4.finished
######## END ########


#
# =====================================================
# $Id$
# $URL$
# $LastChangedDate$
# $LastChangedRevision$
# $LastChangedBy$
# =====================================================
#

#MOLGENIS walltime=00:45:00
#FOREACH

#WHERE DOES RSCRIPT LIVE ON THE GRID?


getFile ${matefixedcovariatecsv}
getFile ${sortedrecalcovariatecsv}

#export PATH=${R_HOME}/bin:<#noparse>${PATH}</#noparse>
export R_LIBS=${R_LIBS} 

java -jar -Xmx4g ${analyzecovariatesjar} -l INFO \
-resources ${indexfile} \
--recal_file ${matefixedcovariatecsv} \
-outputDir ${recalstatsbeforedir} \
-Rscript ${rscript} \
-ignoreQ 5

java -jar -Xmx4g ${analyzecovariatesjar} -l INFO \
-resources ${indexfile} \
--recal_file ${sortedrecalcovariatecsv} \
-outputDir ${recalstatsafterdir} \
-Rscript ${rscript} \
-ignoreQ 5

putFile ${cyclecovariatebefore}
putFile ${cyclecovariateafter}

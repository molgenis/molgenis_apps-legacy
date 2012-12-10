#
# =====================================================
# $Id$
# $URL$
# $LastChangedDate$
# $LastChangedRevision$
# $LastChangedBy$
# =====================================================
#

#MOLGENIS walltime=45:00:00 mem=10
#FOREACH externalSampleID

getFile ${snpeffjar}
getFile ${snpeffconfig} 
getFile ${snpsgenomicannotatedvcf}
getFile ${mergedbam}
getFile ${mergedbamindex}
getFile ${dbsnpvcf}
getFile ${dbsnpvcf}.idx
getFile ${indexfile}
getFile ${indexfile}.amb
getFile ${indexfile}.ann
getFile ${indexfile}.bwt
getFile ${indexfile}.fai
getFile ${indexfile}.pac
getFile ${indexfile}.rbwt
getFile ${indexfile}.rpac
getFile ${indexfile}.rsa
getFile ${indexfile}.sa
<#if capturingKit != "None">getFile ${baitsbed}</#if>

####Create snpEFF annotations on original input file####
java -Xmx4g -jar ${snpeffjar} \
eff \
-v \
-c ${snpeffconfig} \
-i vcf \
-o vcf \
GRCh37.64 \
-onlyCoding true \
-stats ${snpeffsummaryhtml} \
${snpsgenomicannotatedvcf} \
> ${snpeffintermediate}

####Annotate SNPs with snpEff information####
java -jar -Xmx4g ${genomeAnalysisTKjar1411} \
-T VariantAnnotator \
--useAllAnnotations \
--excludeAnnotation MVLikelihoodRatio \
--excludeAnnotation TechnologyComposition \
-I ${mergedbam} \
--snpEffFile ${snpeffintermediate} \
-D ${dbsnpvcf} \
-R ${indexfile} \
--variant ${snpsgenomicannotatedvcf} \<#if capturingKit != "None">
-L ${baitsbed} \</#if>
-o ${snpsfinalvcf}

putFile ${snpeffsummaryhtml}
putFile ${snpeffintermediate}
putFile ${snpsfinalvcf}
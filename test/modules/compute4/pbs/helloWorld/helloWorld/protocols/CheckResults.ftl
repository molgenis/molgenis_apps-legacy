#FOREACH constant

d=$(diff -rq ${resultsdir} ${expectedresultsdir})
if [ -z $d ]
then
	touch ${clusterbasedir}/SUCCESS
	wget http://www.molgenis.org/hudson/job/molgenis_compute_pbs_result_helloworld/buildWithParameters?token=molgenis&RESULT=SUCCESS
else
	touch ${clusterbasedir}/FAILURE
	wget http://www.molgenis.org/hudson/job/molgenis_compute_pbs_result_helloworld/buildWithParameters?token=molgenis&RESULT=FAIL
fi

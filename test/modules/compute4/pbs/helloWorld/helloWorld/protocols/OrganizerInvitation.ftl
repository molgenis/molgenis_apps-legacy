#FOREACH group

mkdir -p ${resultsdir}

echo "Dear ${organizer}," > ${groupfile}
echo "Please organize activities for the ${group} group." >> ${groupfile}
echo "List of guests:" >> ${groupfile}
<#list guest as g>
	echo "${g}" >> ${groupfile}
</#list>


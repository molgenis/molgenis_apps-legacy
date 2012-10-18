#FOREACH group, kolom

echo "Dear ${organizer},"
echo "Please organize activities for the ${group} group."
echo "List of guests:"
<#list guest as g>
	echo "${g}"
</#list>


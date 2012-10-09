#FOREACH organizer

echo "Dear ${organizer},"
echo "Please organize activities for the ${age} group."
echo "List of guests:"
<#list guest as g>
	echo "${g}"
</#list>


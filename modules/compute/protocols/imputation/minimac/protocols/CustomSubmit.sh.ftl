DIR="$( cd "$( dirname "<#noparse>${BASH_SOURCE[0]}</#noparse>" )" && pwd )"
touch $DIR/${workflowfilename}.started

<#foreach j in jobs>	
#${j.name}
bsub < "${j.name}.sh"<#if j.prevSteps_Name?size &gt; 0> -w 'done("<#foreach d in j.prevSteps_Name>${d}")<#if d_has_next> && done("</#if></#foreach></#if>'
echo $${j.name}
sleep 0
</#foreach>
# Configures the GCC bash environment
. ${root}/gcc.bashrc

<#include "Macros.ftl"/>
<@begin/>
<#include "NGSHeader.ftl"/>
<#if defaultInterpreter = "R"><@Rbegin/></#if>
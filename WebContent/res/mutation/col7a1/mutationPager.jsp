<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<display:table name="mutationSummaryDTOList" pagesize="10" export="true" sort="list" class="listtable" id="current" decorator="org.molgenis.col7a1.ui.MutationPagerDecorator">
<display:setProperty name="paging.banner.full"><span class="pagelinks"><a href="{1}">First</a> <a href="{2}">Prev</a> {0} <a href="{3}">Next</a> <a href="{4}">Last</a></span></display:setProperty>  
<display:setProperty name="paging.banner.first"><span class="pagelinks">{0} <a href="{3}">Next</a> <a href="{4}">Last</a> </span></display:setProperty>
<display:setProperty name="paging.banner.last"><span class="pagelinks"><a href="{1}">First</a> <a href="{2}">Prev</a> {0}</span></display:setProperty>
<display:setProperty name="paging.banner.onepage"><span class="pagelinks"></span></display:setProperty>
<display:setProperty name="paging.banner.item_name" value="patient"/>
<display:setProperty name="paging.banner.items_name" value="patients"/>
<display:setProperty name="paging.banner.page.separator" value=" "/>
<display:setProperty name="paging.banner.placement" value="both"/>
<display:setProperty name="export.banner"><div class="exportlinks">Export: {0}</div></display:setProperty>
<display:setProperty name="export.csv.filename" value="mutations.csv"/>
<display:setProperty name="export.excel.filename" value="mutations.xls"/>
<display:setProperty name="export.pdf.filename" value="mutations.pdf"/>
<display:setProperty name="export.pdf" value="true"/>
<display:setProperty name="export.types" value="csv excel pdf"/>
<display:setProperty name="export.xml" value="false"/>

<display:column media="html" property="mutationId" title="Mutation ID"/>
<display:column media="html" property="nameCdna" title="cDNA change"/>
<display:column media="html" property="nameAa" title="Protein change"/>
<display:column media="html" property="exonIntron" title="Exon/Intron"/>
<display:column media="html" property="consequence" title="Consequence"/>
<display:column media="html" property="inheritance" title="Inheritance"/>
<display:column media="html" property="patientId" title="Patient ID"/>
<display:column media="html" property="phenotype" title="Phenotype"/>

<display:column media="csv excel pdf" title="Patient ID">
	<c:forEach var="patientDTO" items="${current.patientSummaryDTOList}"><c:out value="${patientDTO.patientIdentifier}"/> </c:forEach>
</display:column>
<display:column media="csv excel pdf" title="Reference">
	<c:choose>
	<c:when test="${fn:length(current.publicationDTOList) > 0}">
	<c:forEach var="publicationDTO" items="${current.publicationDTOList}">
	<c:out value="${publicationDTO.title}"/>
	</c:forEach>
	</c:when>
	<c:otherwise>
	<c:out value="Unpublished"/>
	</c:otherwise>
	</c:choose>
</display:column>

</display:table>  

<p>
[<a href="#">Back to top</a>]
</p>
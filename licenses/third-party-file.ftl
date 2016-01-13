<#function licenseFormat licenses>
    <#assign result = ""/>
    <#list licenses as license>
    	<#if license.licenseUrl?? >
			<#assign result = result + " ( <a href=\"" + license.licenseUrl + "\" > " + license.licenseName + "</a> )" />	
		<#else>
			<#assign result = result + " ( " + license.licenseName + " )" />
		</#if>
		<#if license.licenseLocalPath??>
			<#assign result = result + "<sup> <a href=\"" + license.licenseLocalPath + "\" >local</a></sup>" />
		<#else>
			<#assign result = result + "<sup></sup>" />
		</#if>
    </#list>
    <#return "<td>" + result + "</td>">
</#function>

<#function artifactFormat p>
	<#assign column0= "<td>_libraries/" + p.artifactId + "-" + p.version + ".jar</td>" />
	<#if p.url??>
		<#assign column1= "<td> <a href=\"" + p.url + "\"> " + p.name + "</a></td>" />
	<#else>
		<#assign column1= "<td>" + p.name + "</td>" />
	</#if>
	<#assign column2 = "<td>" + p.groupId + ":" + p.artifactId + ":" + p.version + "</td>" />
	<#return column0 + column1 + column2>
</#function>

<#function formatRepositories rep>
	<#assign retval= "" />
	<#list repositories as rep>
		<#assign retval = retval + "<a href=\"" + rep.url + "\">" + rep.name + "</a>&nbsp" />
	</#list>
	<#return retval>
</#function>

<html>
	<body>
	<h1>Libraries</h1>
	<p>
		This file contains the libraries which are used by FaLC and its add-ons. 
		No libraries were modified and they were taken from this maven repositories: 
		${formatRepositories(repositories)}
	</p>
		<table>
			<tr><th>File</th><th>Name</th><th>groupId:artifactId:version</th><th>License</th></tr>
			<#list dependencyMap as e>
				<#assign project = e.getKey()/>
			    <#assign licenses = e.getValue()/>
				<#if project.artifactId?index_of('falc-sim-') &lt;= -1>
					<tr>
						${artifactFormat(project)} ${licenseFormat(licenses)}
					</tr>
				</#if> 
			</#list>
		</table>
	</body>
</html>
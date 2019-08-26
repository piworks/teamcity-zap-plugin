<%@ page import="jetbrains.teamcity.EchoRunnerConstants" %>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<c:set var="messageId" value="<%=EchoRunnerConstants.MESSAGE_KEY%>"/>
<c:set var="zapPath" value="<%=EchoRunnerConstants.ZAP_PATH%>"/>
<c:set var="failOpt" value="<%=EchoRunnerConstants.FAIL_OPT%>"/>

<div class="parameter">
    URL: <props:displayValue name="${messageId}" emptyValue=""/>
    Zap Path: <props:displayValue name="${zapPath}" emptyValue=""/>
    Fail On Risk Level: <props:displayValue name="${failOpt}" emptyValue=""/>
</div>
<%@ page import="jetbrains.teamcity.EchoRunnerConstants" %>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="l" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean"/>
<c:set var="messageId" value="<%=EchoRunnerConstants.MESSAGE_KEY%>"/>
<c:set var="zapPath" value="<%=EchoRunnerConstants.ZAP_PATH%>"/>

<l:settingsGroup title="My Runner settings">
    <tr>
        <th><label for="${messageId}">Url to scan: <l:star/></label></th>
        <td>
            <div class="posRel">
                <props:textProperty name="${messageId}" size="56" maxlength="3000"/>
                <span class="error" id="error_${messageId}"></span>
            </div>
        </td>
     </tr>
     <tr>
        <th><label for="${zapPath}">ZAP PATH : <l:star/></label></th>
            <td>
                <div class="posRel">
                    <props:textProperty name="${zapPath}" size="56" maxlength="3000"/>
                    <span class="smallNote">Example Path: C:\Program Files\OWASP\Zed Attack Proxy\</span>
                    <span class="error" id="error_${zapPath}"></span>
                </div>
            </td>
    </tr>
</l:settingsGroup>
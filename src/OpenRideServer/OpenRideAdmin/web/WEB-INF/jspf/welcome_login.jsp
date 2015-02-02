<%--
    OpenRide -- Car Sharing 2.0
    Copyright (C) 2010  Fraunhofer Institute for Open Communication Systems (FOKUS)

    Fraunhofer FOKUS
    Kaiserin-Augusta-Allee 31
    10589 Berlin
    Tel: +49 30 3463-7000
    info@fokus.fraunhofer.de

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License Version 3 as
    published by the Free Software Foundation.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

    <fieldset style="min-height: 150px;">
        <legend>You are an administrator?</legend>

        <p>Log in register in order to access the admin area.</p>

        <form id="loginForm" action="j_security_check" method="post">
            <table border="0" width="100%">
                <tbody>
                    <tr>
                        <td class="column33"><label for="j_username" class="requiredField">Login:</label> <span class="requiredField">(*)</span></td>
                        <td class="column66"><input id="j_username" type="text" name="j_username" class="wide" /></td>
                    </tr>
                    <tr>
                        <td class="column33"><label for="j_password" class="requiredField">password:</label> <span class="requiredField">(*)</span></td>
                        <td class="column66"><input id="j_password" type="password" name="j_password" value="" class="wide" />
                        <c:if test="${param.login_error=='true'}"><span class="error">Invalid credentials.</span></c:if></td>
                    </tr>
                    <tr>
                        <td class="column33">&nbsp;</td>
                        <td class="column66"><input type="submit" name="submit" value="Einloggen" /></td>
                    </tr>
                </tbody>
            </table>
        </form>

        <%--h:form id="loginForm">
            <h:panelGrid width="100%" columns="2" columnClasses="column33,column66" border="0">

                <h:panelGroup>
                    <h:outputLabel value="User name:" for="username" styleClass="requiredField" />
                    <span class="requiredField">(*)</span>
                </h:panelGroup>
                <h:panelGroup>
                    <h:inputText required="true" id="username" binding="#{Welcome_Backing.username}" styleClass="wide" />
                    <h:message for="username" styleClass="error" />
                </h:panelGroup>

                <h:panelGroup>
                    <h:outputLabel value="Password:" for="password" styleClass="requiredField" />
                    <span class="requiredField">(*)</span>
                </h:panelGroup>
                <h:panelGroup>
                    <h:inputSecret required="true" id="password" binding="#{Welcome_Backing.password}" styleClass="wide" />
                    <h:message for="password" styleClass="error" />
                    <h:messages globalOnly="true" infoClass="error" />
                </h:panelGroup>

                <f:verbatim>&nbsp;</f:verbatim>
                <h:commandButton value="Log In" action="#{Welcome_Backing.login}" />

            </h:panelGrid>
        </h:form--%>

        <p>Passwort vergessen? <br />Wenden Sie sich an <a href="mailto:support-zu@open-ride.com">support-zu@open-ride.com</a>.</p>

    </fieldset>
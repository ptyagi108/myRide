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

<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>

<f:view>

    <jsp:include page="/WEB-INF/jspf/header.jsp"><jsp:param name="section" value="monitoring" /></jsp:include>

    <h:form id="logoutForm">
        <div style="float: right; margin-top: 4px;"><h:commandLink action="#{Welcome_Backing.logout}" immediate="true"><h:outputText value="log out" /></h:commandLink></div>
        <h1><h1>Monitoring</h1></h1>
    </h:form>

    <h:form id="form">

        <f:verbatim><fieldset><legend>user</legend></f:verbatim>
        <div style="padding: 10px 15px;">
            <h:panelGrid columns="2" columnClasses="monitoringCol1,monitoringCol2">
                <h:outputText value="Registered users:" />
                <h:outputText value="#{Monitoring.users}" />
                <h:outputText value="Today registered user:" />
                <h:outputText value="#{Monitoring.registrationsToday}" />
                <h:outputText value="Most Active driver" />
                <h:outputText value="not implemented yet" />
            </h:panelGrid>
        </div>
        <f:verbatim></fieldset></f:verbatim>

        <h:panelGrid columns="2">
            <h:panelGroup>
                <f:verbatim><fieldset><legend>Requests</legend></f:verbatim>
                <div style="padding: 10px 15px;">
                    <h:panelGrid columns="2" columnClasses="monitoringCol1,monitoringCol2">
                        <h:outputText value="All requests:" />
                        <h:outputText value="#{Monitoring.searchesAll}" />
                        <h:outputText value="Requests today:" />
                        <h:outputText value="#{Monitoring.searchesToday}" />
                    </h:panelGrid>
                </div>
                <f:verbatim></fieldset></f:verbatim>
            </h:panelGroup>

            <h:panelGroup>
                <f:verbatim><fieldset><legend>offers</legend></f:verbatim>
                <div style="padding: 10px 15px;">
                    <h:panelGrid columns="2" columnClasses="monitoringCol1,monitoringCol2">
                        <h:outputText value="All deals" />
                        <h:outputText value="#{Monitoring.offersAll}" />
                        <h:outputText value="Offers today:" />
                        <h:outputText value="#{Monitoring.offersToday}" />
                    </h:panelGrid>
                </div>
                <f:verbatim></fieldset></f:verbatim>
            </h:panelGroup>

            <h:panelGroup>
                <f:verbatim><fieldset><legend>Reservations</legend></f:verbatim>
                <div style="padding: 10px 15px;">
                    <h:panelGrid columns="2" columnClasses="monitoringCol1,monitoringCol2">
                        <h:outputText value="All bookings" />
                        <h:outputText value="#{Monitoring.bookingsAll}" />
                        <h:outputText value="Known today:" />
                        <h:outputText value="#{Monitoring.bookingsToday}"/>
                    </h:panelGrid>
                </div>
                <f:verbatim></fieldset></f:verbatim>
            </h:panelGroup>

            <h:panelGroup>
                <f:verbatim><fieldset><legend>Matches</legend></f:verbatim>
                <div style="padding: 10px 15px;">
                    <h:panelGrid columns="2" columnClasses="monitoringCol1,monitoringCol2">
                        <h:outputText value="Alle Matches:" />
                        <h:outputText value="#{Monitoring.matchesAll}" />
                        <h:outputText value="matches today:" />
                        <h:outputText value="#{Monitoring.matchesToday}" />
                    </h:panelGrid>
                </div>
                <f:verbatim></fieldset></f:verbatim>
            </h:panelGroup>

        </h:panelGrid>



    </h:form>

    <jsp:include page="/WEB-INF/jspf/footer.jsp"></jsp:include>
</f:view>

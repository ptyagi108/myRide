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
<%@taglib prefix="t" uri="http://myfaces.apache.org/tomahawk" %>

<f:view>

    <jsp:include page="/WEB-INF/jspf/header.jsp"><jsp:param name="section" value="home" /></jsp:include>

    <h1>Register</h1>

    <!--p> You want to join the Open Ride-pilot for the students and staff of the Zeppelin University? That would be us! Please take a few moments to register. </ p>
     <p> Please enter your actual and not fictitious data. Drivers and passengers know to whom they are dealing with and can have any problems (eg delay) communicate faster. You can always arrange for deletion / alteration of your user data. Oh by the way: The use of open Ride is free of charge </ p -->.

    <p><strong>In the context of software testing no real rides take place. All data are fictitious. Authentic personal data for registration is not required for software testing.</strong></p>

    <p>The password for your first login you receive immediately after registration.</p>

    <fieldset>
        <legend>Create your account in Open Ride:</legend>

        <div style="width: 80%;">

            <h:form id="registrationForm">

                <h:messages globalOnly="true" styleClass="error" />

                <h:panelGrid width="100%" columns="2" columnClasses="column33,column66" border="0">

                    <h:panelGroup>
                        <h:outputLabel for="username" value="user name:" styleClass="requiredField" />
                        <span class="requiredField">(*)</span>
                    </h:panelGroup>
                    <h:panelGroup>
                        <t:inputText id="username" binding="#{Register_Backing.username}" validator="#{Register_Backing.validateUsername}" required="true" styleClass="wide">
                            <t:validateRegExpr pattern="\w{3,18}" message="User names can only consist of letters AZ, numbers, and underscores (length: 3-18 characters)." />
                        </t:inputText>
                        <h:message for="username" styleClass="error"/>
                    </h:panelGroup>

                    <%--h:panelGroup>
                        <h:outputLabel for="password" value="Password:" styleClass="requiredField" />
                        <span class="requiredField">(*)</span>
                    </h:panelGroup>
                    <h:panelGroup>
                        <h:inputSecret id="password" binding="#{Register_Backing.password}" required="true" styleClass="wide" />
                        <h:message for="password" styleClass="error"/>
                    </h:panelGroup>

                    <h:panelGroup>
                        <h:outputLabel for="passwordCheck" value="Repeat password):" styleClass="requiredField" />
                        <span class="requiredField">(*)</span>
                    </h:panelGroup>
                    <h:panelGroup>
                        <h:inputSecret id="passwordCheck" binding="#{Register_Backing.passwordCheck}" required="true" validator="#{Register_Backing.validatePassword}" styleClass="wide" />
                        <h:message for="passwordCheck" styleClass="error"/>
                    </h:panelGroup--%>

                    <h:panelGroup>
                        <h:outputLabel for="firstName" value="first name:" styleClass="requiredField" />
                        <span class="requiredField">(*)</span>
                    </h:panelGroup>
                    <h:panelGroup>
                        <h:inputText id="firstName" binding="#{Register_Backing.firstName}" required="true" styleClass="wide" />
                        <h:message for="firstName" styleClass="error"/>
                    </h:panelGroup>

                    <h:panelGroup>
                        <h:outputLabel for="lastName" value="Last Name:" styleClass="requiredField" />
                        <span class="requiredField">(*)</span>
                    </h:panelGroup>
                    <h:panelGroup>
                        <h:inputText id="lastName" binding="#{Register_Backing.lastName}" required="true" styleClass="wide" />
                        <h:message for="lastName" styleClass="error"/>
                    </h:panelGroup>

                    <h:panelGroup>
                        <h:outputLabel for="gender" value="Gender:" styleClass="requiredField" />
                        <span class="requiredField">(*)</span>
                    </h:panelGroup>
                    <h:panelGroup>
                        <h:selectOneRadio id="gender" binding="#{Register_Backing.gender}" required="true">
                            <f:selectItem itemLabel="male" itemValue="m" />
                            <f:selectItem itemLabel="female" itemValue="f" />
                        </h:selectOneRadio>
                        <h:message for="gender" styleClass="error"/>
                    </h:panelGroup>

                    <h:panelGroup>
                        <h:outputLabel for="email" value="E-Mail-address:" styleClass="requiredField" />
                        <span class="requiredField">(*)</span>
                    </h:panelGroup>
                    <h:panelGroup>
                        <h:inputText id="email" binding="#{Register_Backing.email}" required="true" validator="#{Register_Backing.validateEmail}" styleClass="wide">
                            <t:validateEmail summaryMessage="invalid E-Mail-address."/>                            
                        </h:inputText>
                        <h:message for="email" styleClass="error" />
                    </h:panelGroup>

                    <h:panelGroup>
                        <h:outputLabel for="mobilePhoneNumber" value="Mobile Number:" styleClass="requiredField" />
                        <span class="requiredField">(*)</span>
                    </h:panelGroup>
                    <h:panelGroup>
                        <h:inputText id="mobilePhoneNumber" binding="#{Register_Backing.mobilePhoneNumber}" required="true" styleClass="wide">
                            <t:validateRegExpr pattern="^[(]?0[ ]?1[5-7][0-9][ ]?[-/)]?[ ]?[1-9][-0-9 ]{6,16}$" message ="Please enter a valid mobile number." />
                        </h:inputText>
                        <h:message for="mobilePhoneNumber" styleClass="error"/>
                    </h:panelGroup>
                        
                    <f:verbatim>&nbsp;</f:verbatim>
                    <h:panelGroup>
                        <h:selectBooleanCheckbox id="acceptedTerms" binding="#{Register_Backing.acceptedTerms}" required="true" validator="#{Register_Backing.validateAcceptedTerms}" style="float: left;" />
                        <div style="padding-top: 1px;">I have <a href="terms.jsf" target="_blank">read the Disclaimer and the Privacy Statement and agree to this code. <span class="requiredField">(*)</span></div>
                        <h:message for="acceptedTerms" styleClass="error" />
                    </h:panelGroup>

                    <f:verbatim>&nbsp;</f:verbatim>
                    <h:panelGroup>
                        <div style="float: right; margin-top: 2px; color: #999;"><span class="requiredField">(*)</span> = required</div>
                        <h:commandButton value="further" action="#{Register_Backing.register}" />
                    </h:panelGroup>

                </h:panelGrid>


            </h:form>

        </div>

    </fieldset>

    <jsp:include page="/WEB-INF/jspf/footer.jsp"></jsp:include>
</f:view>

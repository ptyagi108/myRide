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

    <jsp:include page="/WEB-INF/jspf/header.jsp"><jsp:param name="section" value="partners" /></jsp:include>

    <h1>Partner</h1>

    <p>OpenRide wird unterstützt durch:</p>

    <br>
    <a href="http://www.fokus.fraunhofer.de"><img src="img/FraunhoferLogo_team-seite.png" height="47" width="154" alt="Fraunhofer FOKUS Logo"></a>
    <p>
        Das <a href="http://www.fokus.fraunhofer.de/de/fokus/index.html">Fraunhofer Institute for Open Communication Systems FOKUS</a> researched and developed for more than 20 years in Berlin communications and integration solutions for partners in industry, research grants, and public administration in the telecommunications, automotive, eGovernment and software development. 
    </p>
    <br>


    <a href="http://www.bmwi.de/BMWi/Navigation/root.html"><img src="img/bmwi.png" height="92" width="169" alt="BMWI Logo"></a>  <a href="http://www.exist.de/"><img align="top" src="img/exist.png" height="47" width="103" alt="exist Logo"></a>
    <p>Open Ride is sponsored by the Federal Republic of Germany under the <a href="http://www.exist.de/">EXIST funding</a>; Funded by:
        <a href="http://www.bmwi.de/BMWi/Navigation/root.html">Federal Ministry of Economics and Technology </a>code due to a
         Decision of the German Bundestag.
    </p>
    <p>
        The EXIST program supports outstanding research-based start-up projects,
         associated with expensive and risky development work.
    </p>



    <jsp:include page="/WEB-INF/jspf/footer.jsp"></jsp:include>
</f:view>

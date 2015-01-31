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

    <jsp:include page="/WEB-INF/jspf/header.jsp"><jsp:param name="section" value="about" /></jsp:include>

    <h1>About Open Ride</h1>

    <h2>Open infrastructure for ad - hoc arranging car-sharing</h2>
   <p> Increasing mobility costs and increasing environmental awareness currently allow the
        Need for effective and flexible alternatives to grow strongly established mobility services .
    </p>
    <p>
        Open Ride is being developed at the Fraunhofer Institute for Open Communication Systems and
        a complete solution for arranging car-sharing . Drivers and passengers can
        Setting the go or search Open Ride from their mobile phone spontaneously carpool .
    </p>
    <p>
        In contrast to established solutions for carpool Open Ride trips instantly tells
        on the road and even for short distances . This Open Ride opens up the market for the untapped
        Potential of free transport capacities in private .
    </p>

    <p>
        A special feature is the openness of the Open Ride infrastructure that existing
        Car-sharing communities and allows to link themselves in a simple way to open and thereby Ride
        to open up the market for mobile ad hoc carpool .
    </p>

    <h2> How Open Ride: </h2>
    <p>
        Please click on the graphic for the introduction of video in a new window.
    </p>
    <p align="center">
        <a href="http://www.open-ride.com/video/flashfull.html" onclick="return PopUp(760,630,this.href)"><img src="http://www.open-ride.com/img/AnimationTeaser.jpg" widht="300" height="225"></a>.
    </p>


    <!--             <object id="csSWF" classid="clsid:d27cdb6e-ae6d-11cf-96b8-444553540000" width="485" height="405" codebase="http://active.macromedia.com/flash7/cabs/swflash.cab#version=8">
                    <param name="src" value="OpenRideFlash_controller.swf"/>
                    <param name="bgcolor" value="FFFFFF"/>
                    <param name="quality" value="best"/>
                    <param name="allowScriptAccess" value="always"/>
                    <param name="flashVars" value="csConfigFile=OpenRideFlash_config.xml&csColor=FFFFFF"/>
                    <param name="autoplay" value="false"/>
                    <embed name="csSWF" src="OpenRideFlash_controller.swf" width="485" height="405" bgcolor="FFFFFF" quality="best" allowScriptAccess="always" flashVars="csConfigFile=OpenRideFlash_config.xml&csColor=FFFFFF" pluginspage="http://www.macromedia.com/shockwave/download/index.cgi?P1_Prod_Version=ShockwaveFlash"></embed>
                 </object>
    -->
    <h1>Open Ride advantages at a glance:</h1>
    <p>
    <table width="100%" height="178" border="0">
        <tr>
            <td width="109" height="30">&nbsp;</td>
            <th width="70"><div align="center">Flexibility&auml;t</div></th>
            <th width="70"><div align="center">Expense</div></th>

            <th width="70"><div align="center">Costs</div></th>
            <th width="70"><div align="center">Time </div></th>
            <th width="70"><div align="center">Komfort</div></th>
        </tr>
        <tr>
            <th height="26" bgcolor="#EFEFEF"><span class="style6">Auto</span></th>
            <td bgcolor="#EFEFEF"><div align="center"><strong>++</strong></div></td>

            <td bgcolor="#EFEFEF"><div align="center"><strong>+</strong></div></td>
            <td bgcolor="#EFEFEF"><div align="center"><strong>--</strong></div></td>
            <td bgcolor="#EFEFEF"><div align="center"><strong>++</strong></div></td>
            <td bgcolor="#EFEFEF"><div align="center"><strong>++</strong></div></td>
        </tr>
        <tr>
            <th height="27">&Ouml;PNV</th>

            <td><div align="center"><strong>-</strong></div></td>
            <td><div align="center"><strong>o</strong></div></td>
            <td><div align="center"><strong>o</strong></div></td>
            <td><div align="center"><strong>+</strong></div></td>
            <td><div align="center"><strong>o</strong></div></td>
        </tr>

        <tr>
            <th height="27" bgcolor="#EFEFEF"><div align="center">lift</div></th>
            <td bgcolor="#EFEFEF"><div align="center"><strong>--</strong></div></td>
            <td bgcolor="#EFEFEF"><div align="center"><strong>--</strong></div></td>
            <td bgcolor="#EFEFEF"><div align="center"><strong>+</strong></div></td>
            <td bgcolor="#EFEFEF"><div align="center"><strong>++</strong></div></td>

            <td bgcolor="#EFEFEF"><div align="center"><strong>+</strong></div></td>
        </tr>
        <tr>
            <th height="26"><div align="center">bike</div></th>
            <td><div align="center"><strong>++</strong></div></td>
            <td><div align="center"><strong>+</strong></div></td>
            <td><div align="center"><strong>++</strong></div></td>

            <td><div align="center"><strong>--</strong></div></td>
            <td><div align="center"><strong>--</strong></div></td>
        </tr>
        <tr>
            <th height="26" bgcolor="#B8DF2F"><div align="center">OpenRide</div></th>
            <td bordercolor="#000000" bgcolor="#B8DF2F"><div align="center"><strong>++</strong></div></td>
            <td bordercolor="#000000" bgcolor="#B8DF2F"><div align="center"><strong>+</strong></div></td>

            <td bordercolor="#000000" bgcolor="#B8DF2F"><div align="center"><strong>+</strong></div></td>
            <td bordercolor="#000000" bgcolor="#B8DF2F"><div align="center"><strong>++</strong></div></td>
            <td bordercolor="#000000" bgcolor="#B8DF2F"><div align="center"><strong>+</strong></div></td>
        </tr>
    </table>
</p>
<p>
    Legende: <strong>++</strong> = sehr gut; <strong>o</strong> = durchschnittlich; <strong>--</strong> = sehr schlecht
</p>


<jsp:include page="/WEB-INF/jspf/footer.jsp"></jsp:include>
</f:view>

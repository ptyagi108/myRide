/*
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
*/

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.fhg.fokus.openride.rides.driver;

import de.fhg.fokus.openride.rides.rider.RiderUndertakesRideEntity;
import de.fhg.fokus.openride.customerprofile.CustomerEntity;
import de.fhg.fokus.openride.helperclasses.converter.PointConverter;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.Converter;
import org.postgis.Point;

/**
 *
 * @author pab
 */
@Entity
@Table(name = "driverundertakesride")
@NamedQueries({
    @NamedQuery(name = "DriverUndertakesRideEntity.findAll", query = "SELECT d FROM DriverUndertakesRideEntity d"),
    @NamedQuery(name = "DriverUndertakesRideEntity.findByRideId", query = "SELECT d FROM DriverUndertakesRideEntity d WHERE d.rideId = :rideId"),
    @NamedQuery(name = "DriverUndertakesRideEntity.findByRideSeriesId", query = "SELECT d FROM DriverUndertakesRideEntity d WHERE d.rideSeriesId = :rideSeriesId"),
    @NamedQuery(name = "DriverUndertakesRideEntity.findByRideStarttime", query = "SELECT d FROM DriverUndertakesRideEntity d WHERE d.rideStarttime = :rideStarttime"),
    @NamedQuery(name = "DriverUndertakesRideEntity.findByRideOfferedseatsNo", query = "SELECT d FROM DriverUndertakesRideEntity d WHERE d.rideOfferedseatsNo = :rideOfferedseatsNo"),
    @NamedQuery(name = "DriverUndertakesRideEntity.findByRideComment", query = "SELECT d FROM DriverUndertakesRideEntity d WHERE d.rideComment = :rideComment"),
    @NamedQuery(name = "DriverUndertakesRideEntity.findByRideWeekdays", query = "SELECT d FROM DriverUndertakesRideEntity d WHERE d.rideWeekdays = :rideWeekdays"),
    @NamedQuery(name = "DriverUndertakesRideEntity.findByRideAcceptableDetourInMin", query = "SELECT d FROM DriverUndertakesRideEntity d WHERE d.rideAcceptableDetourInMin = :rideAcceptableDetourInMin"),
    @NamedQuery(name = "DriverUndertakesRideEntity.findByCustId", query = "SELECT d FROM DriverUndertakesRideEntity d WHERE d.custId = :custId"),
    @NamedQuery(name = "DriverUndertakesRideEntity.findCustomerDrivesAfterTime", query = "SELECT d FROM DriverUndertakesRideEntity d WHERE d.custId = :custId AND d.rideStarttime >= :time ORDER BY d.rideStarttime"),
    @NamedQuery(name = "DriverUndertakesRideEntity.findCustomerDrivesBeforeTime", query = "SELECT d FROM DriverUndertakesRideEntity d WHERE d.custId = :custId AND d.rideStarttime <= :time ORDER BY d.rideStarttime DESC"),
    @NamedQuery(name = "DriverUndertakesRideEntity.countTotalNoDrives", query = "SELECT COUNT(d.rideId) FROM DriverUndertakesRideEntity d"),
    @NamedQuery(name = "DriverUndertakesRideEntity.countTotalNoDrivesAfterDate", query = "SELECT COUNT(d.rideId) FROM DriverUndertakesRideEntity d WHERE d.rideStarttime >= :date"),
    @NamedQuery(name = "DriverUndertakesRideEntity.countTotalNoDrivesBetweenDates", query = "SELECT COUNT(d.rideId) FROM DriverUndertakesRideEntity d WHERE d.rideStarttime BETWEEN :startdate AND :enddate")
})
        @Converter(name="convert", converterClass=PointConverter.class)
public class DriverUndertakesRideEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name = "ride_id")
    private Integer rideId;
    @Column(name = "ride_weekdays")
    private String rideWeekdays;
    @Column(name = "ride_series_id")
    private Integer rideSeriesId;
    @Lob
    @Column(name = "ride_startpt")
    @Convert("convert")
    private Point rideStartpt;
    @Column(name = "ride_starttime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date rideStarttime;
    @Lob
    @Column(name = "ride_endpt")
    @Convert("convert")
    private Point rideEndpt;
    @Column(name = "ride_comment")
    private String rideComment;
    @Lob
    @Column(name = "ride_currpos")
    @Convert("convert")
    private Point rideCurrpos;
    @Column(name = "ride_acceptable_detour_in_min")
    private Integer rideAcceptableDetourInMin;
    @Column(name = "ride_acceptable_detour_in_km")
    private Integer rideAcceptableDetourInKm;
    @Column(name = "ride_acceptable_detour_in_percent")
    private Integer rideAcceptableDetourInPercent;
    @Column(name = "ride_offeredseats_no")
    private Integer rideOfferedseatsNo;
    @OneToMany(mappedBy = "rideId")
    private Collection<RiderUndertakesRideEntity> riderUndertakesRideEntityCollection;
    @JoinColumn(name = "cust_id", referencedColumnName = "cust_id")
    @ManyToOne
    private CustomerEntity custId;
    @Column(name = "ride_route_point_distance_meters")
    private Double rideRoutePointDistanceMeters;


    /* ADDRESSES */

    @Column(name = "startpt_addr")
    private String startptAddressStreet;
    @Column(name = "endpt_addr")
    private String endptAddressStreet;

    public DriverUndertakesRideEntity() {}

    public DriverUndertakesRideEntity(Date rideStarttime,
            Point rideStartpt, Point rideEndpt,
            int rideOfferedseatsNo, Integer rideAcceptableDetourInMin,
            Integer rideAcceptableDetourInKm, Integer rideAcceptableDetourInPercent, Double rideRoutePointDistanceMeters) {
        this.rideStarttime = rideStarttime;
        this.rideStartpt = rideStartpt;
        this.rideEndpt = rideEndpt;
        this.rideOfferedseatsNo = rideOfferedseatsNo;
        this.rideAcceptableDetourInMin = rideAcceptableDetourInMin;
        this.rideAcceptableDetourInKm = rideAcceptableDetourInKm;
        this.rideAcceptableDetourInPercent = rideAcceptableDetourInPercent;
        this.rideRoutePointDistanceMeters = rideRoutePointDistanceMeters;
    }

    public String getEndptAddress() {
        return endptAddressStreet;
    }

    public void setEndptAddress(String endptAddressStreet) {
        this.endptAddressStreet = endptAddressStreet;
    }

    public String getStartptAddress() {
        return startptAddressStreet;
    }

    public void setStartptAddress(String startptAddressStreet) {
        this.startptAddressStreet = startptAddressStreet;
    }

    public Integer getRideId() {
        return rideId;
    }

    public void setRideId(Integer rideId) {
        this.rideId = rideId;
    }

    public String getRideWeekdays() {
        return rideWeekdays;
    }

    public void setRideWeekdays(String rideWeekdays) {
        this.rideWeekdays = rideWeekdays;
    }

    public Integer getRideSeriesId() {
        return rideSeriesId;
    }

    public void setRideSeriesId(Integer rideSeriesId) {
        this.rideSeriesId = rideSeriesId;
    }

    public Point getRideStartpt() {
        return rideStartpt;
    }

    public void setRideStartpt(Point rideStartpt) {
        this.rideStartpt = rideStartpt;
    }

    public Date getRideStarttime() {
        return rideStarttime;
    }

    public void setRideStarttime(Date rideStarttime) {
        this.rideStarttime = rideStarttime;
    }

    public Point getRideEndpt() {
        return rideEndpt;
    }

    public void setRideEndpt(Point rideEndpt) {
        this.rideEndpt = rideEndpt;
    }

    public String getRideComment() {
        return rideComment;
    }

    public void setRideComment(String rideComment) {
        this.rideComment = rideComment;
    }

    public Point getRideCurrpos() {
        return rideCurrpos;
    }

    public void setRideCurrpos(Point rideCurrpos) {
        this.rideCurrpos = rideCurrpos;
    }

    public Integer getRideAcceptableDetourInMin() {
        return rideAcceptableDetourInMin;
    }

    public void setRideAcceptableDetourInMin(Integer rideAcceptableDetourInMin) {
        this.rideAcceptableDetourInMin = rideAcceptableDetourInMin;
    }

    public Integer getRideOfferedseatsNo() {
        return rideOfferedseatsNo;
    }

    public void setRideOfferedseatsNo(Integer rideOfferedseatsNo) {
        this.rideOfferedseatsNo = rideOfferedseatsNo;
    }

    public Collection<RiderUndertakesRideEntity> getRiderUndertakesRideEntityCollection() {
        return riderUndertakesRideEntityCollection;
    }

    public void setRiderUndertakesRideEntityCollection(Collection<RiderUndertakesRideEntity> riderUndertakesRideEntityCollection) {
        this.riderUndertakesRideEntityCollection = riderUndertakesRideEntityCollection;
    }

    public CustomerEntity getCustId() {
        return custId;
    }

    public void setCustId(CustomerEntity custId) {
        this.custId = custId;
    }

    public Integer getRideAcceptableDetourInKm() {
        return rideAcceptableDetourInKm;
    }

    public void setRideAcceptableDetourInKm(Integer rideAcceptableDetourInKm) {
        this.rideAcceptableDetourInKm = rideAcceptableDetourInKm;
    }

    public Integer getRideAcceptableDetourInPercent() {
        return rideAcceptableDetourInPercent;
    }

    public void setRideAcceptableDetourInPercent(Integer rideAcceptableDetourInPercent) {
        this.rideAcceptableDetourInPercent = rideAcceptableDetourInPercent;
    }

    public Double getRideRoutePointDistanceMeters() {
        return rideRoutePointDistanceMeters;
    }

    public void setRideRoutePointDistanceMeters(Double rideRoutePointDistanceMeters) {
        this.rideRoutePointDistanceMeters = rideRoutePointDistanceMeters;
    }



    @Override
    public int hashCode() {
        int hash = 0;
        hash += (rideId != null ? rideId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DriverUndertakesRideEntity)) {
            return false;
        }
        DriverUndertakesRideEntity other = (DriverUndertakesRideEntity) object;
        if ((this.rideId == null && other.rideId != null) || (this.rideId != null && !this.rideId.equals(other.rideId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "de.fhg.fokus.openride.rides.driver.DriverUndertakesRideEntity[rideId=" + rideId + "]";
    }

}

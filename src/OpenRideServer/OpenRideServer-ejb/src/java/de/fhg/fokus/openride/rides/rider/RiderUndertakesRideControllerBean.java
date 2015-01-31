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

package de.fhg.fokus.openride.rides.rider;

import de.fhg.fokus.openride.customerprofile.CustomerControllerLocal;
import de.fhg.fokus.openride.customerprofile.CustomerEntity;
import de.fhg.fokus.openride.helperclasses.ControllerBean;
import de.fhg.fokus.openride.matching.MatchEntity;
import de.fhg.fokus.openride.matching.RouteMatchingBeanLocal;
import de.fhg.fokus.openride.rides.driver.DriveRoutepointEntity;
import de.fhg.fokus.openride.rides.driver.DriverUndertakesRideControllerBean;
import de.fhg.fokus.openride.rides.driver.DriverUndertakesRideControllerLocal;
import de.fhg.fokus.openride.rides.driver.DriverUndertakesRideEntity;
import de.fhg.fokus.openride.rides.driver.RoutePointEntity;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.UserTransaction;
import org.postgis.Point;

/**
 *
 * @author pab
 */
@Stateless
public class RiderUndertakesRideControllerBean extends ControllerBean implements RiderUndertakesRideControllerLocal {

    @EJB
    private RouteMatchingBeanLocal routeMatchingBean;
    @EJB
    private DriverUndertakesRideControllerLocal driverUndertakesRideControllerBean;
    @EJB
    private CustomerControllerLocal customerControllerBean;
    @PersistenceContext
    private EntityManager em;
    private UserTransaction u;

    /**
     * This method returns the <code>startpoint</code> of a ride.
     * @param riderrouteId
     * @return null, if no Point was found; the startpoint of the ride.
     */
    public Point getStartPoint(int rideId) {
        init();
        Point returnValue = null;
        List<RiderUndertakesRideEntity> c = (List<RiderUndertakesRideEntity>) em.createNamedQuery("RiderUndertakesRideEntity.findByRideId").setParameter("rideId", rideId).getResultList();
        if (c.size() == 1) {
            returnValue = (Point) c.get(0).getStartpt();
        }
        finish();
        return returnValue;
    }

    public Point getEndPoint(int rideId) {
        init();
        Point returnValue = null;
        List<RiderUndertakesRideEntity> c = (List<RiderUndertakesRideEntity>) em.createNamedQuery("RiderUndertakesRideEntity.findByRideId").setParameter("rideId", rideId).getResultList();
        if (c.size() == 1) {
            returnValue = (Point) c.get(0).getEndpt();
        }
        finish();
        return returnValue;
    }

    public Timestamp getStartTime(int rideId) {
        init();
        Timestamp returnValue = null;
        List<RiderUndertakesRideEntity> c = (List<RiderUndertakesRideEntity>) em.createNamedQuery("RiderUndertakesRideEntity.findByRideId").setParameter("rideId", rideId).getResultList();
        if (c.size() == 1) {
            returnValue = new Timestamp(c.get(0).getStarttimeEarliest().getTime());
        }
        finish();
        return returnValue;
    }

    //TODO: What type shall be returned? Postgres specific?
    public Object getTolerance(int rideId) {
        init();
        finish();
        return null;
    }

    public void persist(Object object) {
        init();
        finish();
        em.persist(object);
    }

    /**
     * TODO: update counts for available seats for each routepoint -> needs information about lift and drop index
     *  FIXME: Swap fake rideId to real rideId
     * @param riderRouteId FIXME: this is the fake <code>rideID</code>
     * @param riderrouteId FIXME: this is the real rideId
     * @return 1 if rider was added -1 if not
     */
    public int addRiderToRide(int riderRouteId, int rideId) {
        init();
        RiderUndertakesRideEntity ride = getRideByRiderRouteId(riderRouteId);
        em.lock(ride, LockModeType.PESSIMISTIC_WRITE);

        //TODO: decrease number of free places
        System.out.println("addRiderToRide 0");
        DriverUndertakesRideEntity drive = driverUndertakesRideControllerBean.getDriveByDriveId(rideId);

        int seatsAvailable = drive.getRideOfferedseatsNo();
        for (RiderUndertakesRideEntity r : drive.getRiderUndertakesRideEntityCollection()) {
            seatsAvailable -= r.getNoPassengers();
        }
        if (seatsAvailable - ride.getNoPassengers() < 0) {
            // there can not be added any more rider to this drive!

            MatchEntity match = getMatch(rideId, riderRouteId);
            match.setDriverState(MatchEntity.NO_MORE_AVAILABLE);
            match.setDriverChange(new Date());
            em.merge(match);
            em.lock(ride, LockModeType.NONE);
            return -1;
        }

        // compute the new driver route
        LinkedList<RoutePointEntity> routePoints = new LinkedList<RoutePointEntity>();
        LinkedList<DriveRoutepointEntity> driveRoutePoints = new LinkedList<DriveRoutepointEntity>();
        double sharedDistance = routeMatchingBean.computeAdaptedRoute(rideId, riderRouteId, driveRoutePoints, routePoints);

        if (sharedDistance != -1 && drive != null && ride != null
                && ride.getRideId() == null) {
            // the ride is not yet booked
            driverUndertakesRideControllerBean.setRoutePoints(rideId, routePoints);
            driverUndertakesRideControllerBean.setDriveRoutePoints(rideId, driveRoutePoints);
            ride.setTimestampbooked(new Date());
            ride.setRideId(drive);
            em.merge(drive);
            MatchEntity match = getMatch(rideId, riderRouteId);
            match.setRiderChange(new Date());
            em.merge(match);
        } else {
            System.out.println("Ride was not added, no more available");
            if (ride.getRideId() != null) {
                // the rider could not be added to the ride!
                MatchEntity match = getMatch(rideId, riderRouteId);
                match.setRiderState(MatchEntity.NO_MORE_AVAILABLE);
                match.setRiderChange(new Date());
                em.merge(match);
            }
            em.lock(ride, LockModeType.NONE);
            return -1;
        }

        List<MatchEntity> list1 = (List<MatchEntity>) em.createNamedQuery("MatchEntity.findByRiderrouteId").setParameter("riderrouteId", riderRouteId).getResultList();
        // If the Rider accepts the ride. All other drivers must be informed about that.
        for (MatchEntity matchEntity : list1) {
            if (matchEntity != null && (matchEntity.getMatchEntityPK().getRideId() != rideId)) {
                // it is not the momentaneouse booked match
                matchEntity.setRiderState(MatchEntity.NO_MORE_AVAILABLE);
                matchEntity.setRiderChange(new Date());
                em.merge(matchEntity);
            }
        }

        finish();
        //return drive.getRideOfferedseatsNo()-passengers.size()-1;
        // clean up database
        List<MatchEntity> matches = driverUndertakesRideControllerBean.getMatches(rideId, true);
        List<MatchEntity> potentiallyOutdatedMatches = new ArrayList<MatchEntity>();
        for (MatchEntity match : matches) {
            if (match.getDriverState() != null && match.getRiderState() != null && match.getDriverState().equals(MatchEntity.ACCEPTED) && match.getRiderState().equals(MatchEntity.ACCEPTED)) {
                // this match has not to be deleted
            /*TODO:
                } else if(match.getDriverState() != null&& match.getRiderState() != null &&
                (match.getRiderState() != MatchEntity.ACCEPTED || match.getDriverState() != MatchEntity.ACCEPTED) ){
                 */
            } else {
                // this match might be invalid due to changed driverroute!
                //(tku 01/09/10): don't remove yet, will check if it is outdated in next step:
                potentiallyOutdatedMatches.add(match);
                //em.remove(match);
            }
        }
        // search for new Matches!
        List<MatchEntity> newMatches = routeMatchingBean.searchForRiders(rideId);

        // remove outdated (e.g. due to changed driverroute) matches:
        boolean isOutdated;
        for (MatchEntity poM : potentiallyOutdatedMatches) {
            isOutdated = true;
            for (MatchEntity newM : newMatches) {
                if (poM.getMatchEntityPK().getRideId() == newM.getMatchEntityPK().getRideId() && poM.getMatchEntityPK().getRiderrouteId() == newM.getMatchEntityPK().getRiderrouteId()) {
                    // Don't remove if rideId and riderRouteId match those of the new match (new match will be filtered in next step)
                    isOutdated = false;
                    break;
                }
            }
            if (isOutdated) {
                // this match is no longer valid
                //TODO: set state to NO_MORE_AVAILABLE instead of deleting if riderState was ACCEPTED... but need to discuss/think about other possible states.
                em.remove(poM);
            }
        }

        newMatches = filter(newMatches);
        for (MatchEntity m : newMatches) {
            // persist match, so it can be found later on!
            em.persist(m);
        }
        em.lock(ride, LockModeType.NONE);
        return 1;
    }

    /**
     * This method is called, when a new search or ride is persisted. It updates the
     * Matches table.
     */
    public void callAlgorithm(int riderrouteId, boolean setRiderAccess) {
        // there are still free places
        List<MatchEntity> matches = routeMatchingBean.searchForDrivers(riderrouteId);
        matches = filter(matches);
        for (MatchEntity m : matches) {
            // persist match, so it can be found later on!
            em.persist(m);
        }
    }

    public void setReceivedRating(int riderRouteId, int rating, String ratingComment) {
        RiderUndertakesRideEntity r = em.find(RiderUndertakesRideEntity.class, riderRouteId);
        if (r != null) {
            r.setReceivedrating(rating);
            r.setReceivedratingComment(ratingComment);
            r.setReceivedratingDate(new Date());
            em.persist(r);
        }
    }

    public void setGivenRating(int riderRouteId, int rating, String ratingComment) {
        RiderUndertakesRideEntity r = em.find(RiderUndertakesRideEntity.class, riderRouteId);
        if (r != null) {
            r.setGivenrating(rating);
            r.setGivenratingComment(ratingComment);
            r.setGivenratingDate(new Date());
            em.persist(r);
        }
    }

    public void addPaymentReference(int rideId) {
        init();
        finish();
    }

    /**
     * This method creates a new riderequest of a customer.
     * @param custId
     * @param starttime
     * @param noPassengers
     * @param startpt
     * @param endpt
     * @param price
     * @return -1, if the Entity could not be persisted; the rideId to indentify the Ride later on.
     */
    public int addRideRequest(int cust_id, Date starttime_earliest, Date starttimeLatest, int noPassengers, Point startpt, Point endpt, double price, String comment) {
        init();

        CustomerEntity customer = customerControllerBean.getCustomer(cust_id);

        //TODO: could get Problems if different users simultanously add a RideRequest;Transaction? perhaps locks to much?
        RiderUndertakesRideEntity r = null;
        logger.info("---------------------------addRideRequest, customer is :" + customer + "----------------------");
//        List<AccountHistoryEntity> a = (List<AccountHistoryEntity>)em.createNamedQuery("AccountHistoryEntity.findByCustId").setParameter("custId", customer.getCustId()).getResultList();
        if (customer != null) {
            /* new change: primary key */
            //r = new RiderUndertakesRideEntity(index, starttime,noPassengers,startpt,endpt,price,a.get(0).getAccountHistoryEntityPK().getAccountTimestamp());

            // FIXME: (pab) This index is only valid if a rideId with the same value exists!
            //r = new RiderUndertakesRideEntity(index, starttimeLatest, startpt, endpt, price, noPassengers, starttime_earliest, customer);
            r = new RiderUndertakesRideEntity(customer, starttime_earliest, starttimeLatest, noPassengers, startpt, endpt, price);
            r.setCustId(customer);
            r.setComment(comment);
            logger.info("---------------------------addRideRequest 1: " + r.getCustId().getCustId());
            
            em.persist(r);
            logger.log(Level.INFO, "riderundertakesride added ");
        } else {

            logger.log(Level.WARNING, "No Customer with id: " + cust_id);
        }
        finish();
        if (r == null) {
            return -1;
        } else {
            callAlgorithm(r.getRiderrouteId(), false);
            return r.getRiderrouteId();
        }
    }

    /**
     * This method searches for all the rides a user has had.
     * @param nickname The nickname of the user.
     * @return null, if no entries were found; a List containing all <code>RiderUndertakesRideEntity</code>'s refering the user.
     */
    public List<RiderUndertakesRideEntity> getRidesByCustId(String custId) {
        List<RiderUndertakesRideEntity> returnList = null;

        // Use the Id of the user to get his rides.
        returnList = em.createNamedQuery("RiderUndertakesRideEntity.findByCustId").setParameter("custId", custId).getResultList();
        return returnList;
    }

    /**
     * This method searches for all the rides a user has had.
     * @param nickname The nickname of the user.
     * @return null, if no entries were found; a List containing all <code>RiderUndertakesRideEntity</code>'s refering the user.
     */
    public List<RiderUndertakesRideEntity> getRides(String nickname) {
        List<RiderUndertakesRideEntity> returnList = null;
        List<RiderUndertakesRideEntity> past = new ArrayList<RiderUndertakesRideEntity>();
        List<RiderUndertakesRideEntity> future = new ArrayList<RiderUndertakesRideEntity>();

        // Get the CustomerEntity by <code>nickname</code>
        List<CustomerEntity> c = (List<CustomerEntity>) em.createNamedQuery("CustomerEntity.findByCustNickname").setParameter("custNickname", nickname).getResultList();

        if (c.size() == 1) {
            // Get the Entity. It should be only one, since nicknames are unique by constraint.
            CustomerEntity e = c.get(0);
            System.out.println("Cust_Id" + e.getCustId());
            // Use the Id of the user to get his rides.
            returnList = em.createNamedQuery("RiderUndertakesRideEntity.findByCustId").setParameter("custId", e).getResultList();
        }
        if (returnList == null) {
            returnList = new LinkedList<RiderUndertakesRideEntity>();
        } else {
            java.util.Date now = new java.util.Date();
            for (RiderUndertakesRideEntity d : returnList) {
                if (d.getStarttimeEarliest().before(now)) {
                    past.add(d);
                } else {
                    future.add(d);
                }
            }

            Collections.sort(future, new Comparator() {

                public int compare(Object o1, Object o2) {
                    RiderUndertakesRideEntity ent1 = (RiderUndertakesRideEntity) o1;
                    RiderUndertakesRideEntity ent2 = (RiderUndertakesRideEntity) o2;
                    java.util.Date now = new java.util.Date();
                    // both rides are future rides
                    if (ent1 == null || ent2 == null) {
                        return 0;
                    } else if (ent1.getStarttimeEarliest().before(ent2.getStarttimeEarliest())) {
                        return -1;
                    } else if (ent1.getStarttimeEarliest().after(ent2.getStarttimeEarliest())) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            });
        }
        returnList = future;
        return returnList;
    }

    public List<RiderUndertakesRideEntity> getInactiveRideRequests(String nickname) {
        List<RiderUndertakesRideEntity> returnList = null;
        List<RiderUndertakesRideEntity> past = new ArrayList<RiderUndertakesRideEntity>();
        List<RiderUndertakesRideEntity> future = new ArrayList<RiderUndertakesRideEntity>();

        //TODO: some errorhandling

        // Get the CustomerEntity by <code>nickname</code>
        CustomerEntity c = (CustomerEntity) em.createNamedQuery("CustomerEntity.findByCustNickname").setParameter("custNickname", nickname).getSingleResult();

        if (c != null) {
            // Get the Entity. It should be only one, since nicknames are unique by constraint.

            returnList = em.createNamedQuery("RiderUndertakesRideEntity.findByCustId").setParameter("custId", c).getResultList();
            // Use the Id of the user to get his rides.
            //TODO: Use Franks tables which contain the active rides to find the ones of the user.
        }

        if (returnList == null) {
            returnList = new LinkedList<RiderUndertakesRideEntity>();
        } else {
            java.util.Date now = new java.util.Date();
            now.setTime(now.getTime() - DriverUndertakesRideControllerBean.ACTIVE_DELAY_TIME);
            for (RiderUndertakesRideEntity d : returnList) {
                if (d.getStarttimeLatest().before(now)) {
                    past.add(d);
                }
            }

            Collections.sort(past, new Comparator() {

                public int compare(Object o1, Object o2) {
                    RiderUndertakesRideEntity ent1 = (RiderUndertakesRideEntity) o1;
                    RiderUndertakesRideEntity ent2 = (RiderUndertakesRideEntity) o2;
                    java.util.Date now = new java.util.Date();
                    // both rides are future rides
                    if (ent1 == null || ent2 == null) {
                        return 0;
                    } else if (ent1.getStarttimeEarliest().before(ent2.getStarttimeEarliest())) {
                        return 1;
                    } else if (ent1.getStarttimeEarliest().after(ent2.getStarttimeEarliest())) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            });
        }
        future.addAll(past);
        returnList = future;
        return returnList;
    }

    /**
     * This method can be used to find active rides of a user with <code>nickname</code>.
     * @param nickname The nickname of the user.
     * @return null, if no user with <code>nickname</code> was found; active rides of user <code>nickname</code>
     */
    public List<RiderUndertakesRideEntity> getActiveRideRequests(String nickname) {
        List<RiderUndertakesRideEntity> returnList = null;
        List<RiderUndertakesRideEntity> past = new ArrayList<RiderUndertakesRideEntity>();
        List<RiderUndertakesRideEntity> future = new ArrayList<RiderUndertakesRideEntity>();

        //TODO: some errorhandling

        // Get the CustomerEntity by <code>nickname</code>
        CustomerEntity c = (CustomerEntity) em.createNamedQuery("CustomerEntity.findByCustNickname").setParameter("custNickname", nickname).getSingleResult();

        if (c != null) {
            // Get the Entity. It should be only one, since nicknames are unique by constraint.

            returnList = em.createNamedQuery("RiderUndertakesRideEntity.findByCustId").setParameter("custId", c).getResultList();
            // Use the Id of the user to get his rides.
            //TODO: Use Franks tables which contain the active rides to find the ones of the user.
        }

        if (returnList == null) {
            returnList = new LinkedList<RiderUndertakesRideEntity>();
        } else {
            java.util.Date now = new java.util.Date();
            now.setTime(now.getTime() + DriverUndertakesRideControllerBean.ACTIVE_DELAY_TIME);
            java.util.Date earlier = new java.util.Date();
            earlier.setTime(earlier.getTime() - DriverUndertakesRideControllerBean.ACTIVE_DELAY_TIME);
            for (RiderUndertakesRideEntity d : returnList) {
                if (d.getStarttimeLatest().after(earlier)) {
                    future.add(d);
                }
            }

            Collections.sort(future, new Comparator() {

                public int compare(Object o1, Object o2) {
                    RiderUndertakesRideEntity ent1 = (RiderUndertakesRideEntity) o1;
                    RiderUndertakesRideEntity ent2 = (RiderUndertakesRideEntity) o2;
                    java.util.Date now = new java.util.Date();
                    // both rides are future rides
                    if (ent1 == null || ent2 == null) {
                        return 0;
                    } else if (ent1.getStarttimeEarliest().before(ent2.getStarttimeEarliest())) {
                        return -1;
                    } else if (ent1.getStarttimeEarliest().after(ent2.getStarttimeEarliest())) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            });

        }
        returnList = future;
        return returnList;
        //throw new NotImplementedException();
    }

    private List<MatchEntity> getActiveMatches(int riderrouteId) {
        List<MatchEntity> entities = em.createNamedQuery("MatchEntity.findByRiderrouteId").setParameter("riderrouteId", riderrouteId).getResultList();
        /*java.util.Date now = new java.util.Date();
        for(MatchEntity m : entities){
        if(m.getDriverUndertakesRideEntity().getRideStarttime().before(now)){
        // this is not active anymore
        entities.remove(m);
        }
        }*/
        return entities;
    }

    public RiderUndertakesRideEntity getRideByRiderRouteId(int riderRouteId) {
        RiderUndertakesRideEntity entity = null;
        try {
            Query q = em.createNamedQuery("RiderUndertakesRideEntity.findByRiderrouteId");
            q = q.setParameter("riderrouteId", riderRouteId);
            entity = (RiderUndertakesRideEntity) q.getSingleResult();
        } catch (NoResultException e) {
        }
        return entity;
    }

    public LinkedList<RiderUndertakesRideEntity> getAllRides() {
        init();

        List<RiderUndertakesRideEntity> l = em.createNamedQuery("RiderUndertakesRideEntity.findAll").getResultList();
        LinkedList<RiderUndertakesRideEntity> ll = new LinkedList<RiderUndertakesRideEntity>(l);

        finish();
        return ll;
    }

    public List<RiderUndertakesRideEntity> getActiveRideRequestsByCustId(String custId) {
        List<CustomerEntity> e = (List<CustomerEntity>) em.createNamedQuery("CustomerEntity.findByCustId").setParameter("custId", Integer.parseInt(custId)).getResultList();
        logger.info("getActiveRideRequestsByCustId: " + custId);
        if (e.size() > 0) {
            logger.info("getActiveRideRequestsByCustId: 1");
            CustomerEntity en = e.get(0);
            List<RiderUndertakesRideEntity> c = (List<RiderUndertakesRideEntity>) em.createNamedQuery("RiderUndertakesRideEntity.findByCustId").setParameter("custId", en).getResultList();
            return c;
        } else {
            logger.info("getActiveRideRequestsByCustId: 2");
            return null;
        }
    }

    /**
     *
     * @param rider
     * @return ArrayList of IDs of the rides undertaken by a specific customer as a rider that have not yet been rated by both parties.
     */
    public List<RiderUndertakesRideEntity> getRidesWithoutRatingByRider(CustomerEntity rider) {
        try {
            List<RiderUndertakesRideEntity> entities = em.createNamedQuery("RiderUndertakesRideEntity.findRidesWithoutRatingByRider").setParameter("custId", rider).getResultList();
            return entities;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     *
     * @param rider
     * @return ArrayList of IDs of the rides undertaken by a specific customer as a rider that have not yet been rated by this rider.
     */
    public List<RiderUndertakesRideEntity> getRidesWithoutGivenRatingByRider(CustomerEntity rider) {
        try {
            List<RiderUndertakesRideEntity> entities = em.createNamedQuery("RiderUndertakesRideEntity.findRidesWithoutGivenRatingByRider").setParameter("custId", rider).getResultList();
            return entities;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     *
     * @param rider
     * @return ArrayList of IDs of the rides undertaken by a specific customer as a rider that have not yet been rated by the driver.
     */
    public List<RiderUndertakesRideEntity> getRidesWithoutReceivedRatingByRider(CustomerEntity rider) {
        try {
            List<RiderUndertakesRideEntity> entities = em.createNamedQuery("RiderUndertakesRideEntity.findRidesWithoutReceivedRatingByRider").setParameter("custId", rider).getResultList();
            return entities;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     *
     * @param driver
     * @return ArrayList of IDs of the rides undertaken by a specific customer as a driver that have not yet been rated by all parties.
     */
    public List<RiderUndertakesRideEntity> getRidesWithoutRatingByDriver(CustomerEntity driver) {
        try {
            List<RiderUndertakesRideEntity> entities = em.createNamedQuery("RiderUndertakesRideEntity.findRidesWithoutRatingByDriver").setParameter("custId", driver).getResultList();
            return entities;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     *
     * @param driver
     * @return ArrayList of IDs of the rides undertaken by a specific customer as a driver that have not yet been rated by this customer.
     */
    public List<RiderUndertakesRideEntity> getRidesWithoutGivenRatingByDriver(CustomerEntity driver) {
        try {
            List<RiderUndertakesRideEntity> entities = em.createNamedQuery("RiderUndertakesRideEntity.findRidesWithoutGivenRatingByDriver").setParameter("custId", driver).getResultList();
            return entities;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     *
     * @param driver
     * @return ArrayList of IDs of the rides undertaken by a specific customer as a driver that have not yet been rated by (all of) the rider(s).
     */
    public List<RiderUndertakesRideEntity> getRidesWithoutReceivedRatingByDriver(CustomerEntity driver) {
        try {
            List<RiderUndertakesRideEntity> entities = em.createNamedQuery("RiderUndertakesRideEntity.findRidesWithoutReceivedRatingByDriver").setParameter("custId", driver).getResultList();
            return entities;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     *
     * @param rider
     * @return ArrayList of IDs of the rides undertaken by a specific customer as a rider that have been rated by the driver.
     */
    public List<RiderUndertakesRideEntity> getRatedRidesByRider(CustomerEntity rider) {
        try {
            List<RiderUndertakesRideEntity> entities = em.createNamedQuery("RiderUndertakesRideEntity.findRatedRidesByRider").setParameter("custId", rider).getResultList();
            return entities;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     *
     * @param driver
     * @return ArrayList of IDs of the rides undertaken by a specific customer as a driver that have been rated by the rider.
     */
    public List<RiderUndertakesRideEntity> getRatedRidesByDriver(CustomerEntity driver) {
        try {
            List<RiderUndertakesRideEntity> entities = em.createNamedQuery("RiderUndertakesRideEntity.findRatedRidesByDriver").setParameter("custId", driver).getResultList();
            return entities;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getRatingsTotalByCustomer(CustomerEntity customer) {
        int ratingsTotal = 0;
        try {
            Object ratingsAsRider = em.createNamedQuery("RiderUndertakesRideEntity.sumUpRatingsAsRider").setParameter("custId", customer).getSingleResult();
            if (ratingsAsRider != null) {
                ratingsTotal += Integer.parseInt(ratingsAsRider.toString());
            }
            Object ratingsAsDriver = em.createNamedQuery("RiderUndertakesRideEntity.sumUpRatingsAsDriver").setParameter("custId", customer).getSingleResult();
            if (ratingsAsDriver != null) {
                ratingsTotal += Integer.parseInt(ratingsAsDriver.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println("ratingsTotal for customer "+ customer.getCustNickname() +": "+ratingsTotal);
        return ratingsTotal;
    }

    public float getRatingsRatioByCustomerAndDate(CustomerEntity customer, Date fromDate) {
        float ratingsRatio = 0;
        try {
            int positiveRatings = 0, neutralRatings = 0, negativeRatings = 0;
            if (fromDate == null) {
                // By default, calculate date - 12 months back
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                cal.add(Calendar.YEAR, -1);
                fromDate = cal.getTime();
            }
            positiveRatings = Integer.parseInt(em.createNamedQuery("RiderUndertakesRideEntity.countPositiveRatingsAsRider").setParameter("custId", customer).setParameter("fromDate", fromDate).getSingleResult().toString());
            positiveRatings += Integer.parseInt(em.createNamedQuery("RiderUndertakesRideEntity.countPositiveRatingsAsDriver").setParameter("custId", customer).setParameter("fromDate", fromDate).getSingleResult().toString());
            //neutralRatings = Integer.parseInt(em.createNamedQuery("RiderUndertakesRideEntity.countNeutralRatingsAsRider").setParameter("custId", customer).setParameter("fromDate", fromDate).getSingleResult().toString());
            //neutralRatings += Integer.parseInt(em.createNamedQuery("RiderUndertakesRideEntity.countNeutralRatingsAsDriver").setParameter("custId", customer).setParameter("fromDate", fromDate).getSingleResult().toString());
            negativeRatings = Integer.parseInt(em.createNamedQuery("RiderUndertakesRideEntity.countNegativeRatingsAsRider").setParameter("custId", customer).setParameter("fromDate", fromDate).getSingleResult().toString());
            negativeRatings += Integer.parseInt(em.createNamedQuery("RiderUndertakesRideEntity.countNegativeRatingsAsDriver").setParameter("custId", customer).setParameter("fromDate", fromDate).getSingleResult().toString());
            if (positiveRatings == 0) {
                return 0;
            } else {
                ratingsRatio = (float) positiveRatings / (float) (positiveRatings + /*neutralRatings + */ negativeRatings);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println("ratingsRatio for customer "+ customer.getCustNickname() +": "+ratingsRatio);
        return ratingsRatio;
    }

    public float getRatingsRatioByCustomer(CustomerEntity customer) {
        return getRatingsRatioByCustomerAndDate(customer, null);
    }

    public int getPositiveRatingsTotalByCustomerAndDate(CustomerEntity customer, Date fromDate) {
        int positiveRatings = 0;
        try {
            if (fromDate == null) {
                // By default, calculate date - 12 months back
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                cal.add(Calendar.YEAR, -1);
                fromDate = cal.getTime();
            }
            positiveRatings = Integer.parseInt(em.createNamedQuery("RiderUndertakesRideEntity.countPositiveRatingsAsRider").setParameter("custId", customer).setParameter("fromDate", fromDate).getSingleResult().toString());
            positiveRatings += Integer.parseInt(em.createNamedQuery("RiderUndertakesRideEntity.countPositiveRatingsAsDriver").setParameter("custId", customer).setParameter("fromDate", fromDate).getSingleResult().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return positiveRatings;
    }

    public int getPositiveRatingsTotalByCustomer(CustomerEntity customer) {
        return getPositiveRatingsTotalByCustomerAndDate(customer, null);
    }

    public int getNeutralRatingsTotalByCustomerAndDate(CustomerEntity customer, Date fromDate) {
        int neutralRatings = 0;
        try {
            if (fromDate == null) {
                // By default, calculate date - 12 months back
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                cal.add(Calendar.YEAR, -1);
                fromDate = cal.getTime();
            }
            neutralRatings = Integer.parseInt(em.createNamedQuery("RiderUndertakesRideEntity.countNeutralRatingsAsRider").setParameter("custId", customer).setParameter("fromDate", fromDate).getSingleResult().toString());
            neutralRatings += Integer.parseInt(em.createNamedQuery("RiderUndertakesRideEntity.countNeutralRatingsAsDriver").setParameter("custId", customer).setParameter("fromDate", fromDate).getSingleResult().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return neutralRatings;
    }

    public int getNeutralRatingsTotalByCustomer(CustomerEntity customer) {
        return getNeutralRatingsTotalByCustomerAndDate(customer, null);
    }

    public int getNegativeRatingsTotalByCustomerAndDate(CustomerEntity customer, Date fromDate) {
        int negativeRatings = 0;
        try {
            if (fromDate == null) {
                // By default, calculate date - 12 months back
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                cal.add(Calendar.YEAR, -1);
                fromDate = cal.getTime();
            }
            negativeRatings = Integer.parseInt(em.createNamedQuery("RiderUndertakesRideEntity.countNegativeRatingsAsRider").setParameter("custId", customer).setParameter("fromDate", fromDate).getSingleResult().toString());
            negativeRatings += Integer.parseInt(em.createNamedQuery("RiderUndertakesRideEntity.countNegativeRatingsAsDriver").setParameter("custId", customer).setParameter("fromDate", fromDate).getSingleResult().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return negativeRatings;
    }

    public int getNegativeRatingsTotalByCustomer(CustomerEntity customer) {
        return getNegativeRatingsTotalByCustomerAndDate(customer, null);
    }

    public boolean removeRide(int riderrouteId) {
        System.out.println("remove ride");

        init();
        List<MatchEntity> states = (List<MatchEntity>) em.createNamedQuery("MatchEntity.findByRiderrouteId").setParameter("riderrouteId", riderrouteId).getResultList();
        boolean deletable = true;
        if (states.size() > 0) {
            // state already exists
            for (MatchEntity entity : states) {
                if (entity.getDriverState() != null || entity.getRiderState() != null) {
                    deletable = false;
                }
            }
        }
        if (deletable) {
            // entity can be chang  ed

            //TODO (03/09/10): Matches & Ride need to be removed in one transaction....!

            for (Iterator<MatchEntity> it = states.iterator(); it.hasNext();) {
                em.remove(it.next());
//                it.remove();
            }

            List<RiderUndertakesRideEntity> entity = em.createNamedQuery("RiderUndertakesRideEntity.findByRiderrouteId").setParameter("riderrouteId", riderrouteId).getResultList();
            System.out.println("size entity: " + entity.size());
            for (RiderUndertakesRideEntity ente : entity) {
                em.remove(ente);
            }

            System.out.println("entity removed: " + riderrouteId);
        } else {
            // entities have to be adapted
            for (Iterator<MatchEntity> it = states.iterator(); it.hasNext();) {
                MatchEntity matchEntity = it.next();
                // mark matches as countermanded by rider
                matchEntity.setRiderState(MatchEntity.COUNTERMANDED);
                matchEntity.setRiderChange(new Date());
                em.merge(matchEntity);
            }
        }
        finish();
        return deletable;

    }

    public int updateRide(
            int riderrouteId,
            Date starttime_earliest,
            Date starttimeLatest,
            int noPassengers,
            Point startpt,
            Point endpt,
            double price,
            String comment,
            String startptAddress,
            String endptAddress) {
        List<RiderUndertakesRideEntity> entities = em.createNamedQuery("RiderUndertakesRideEntity.findByRiderroute_id").setParameter("riderrouteId", riderrouteId).getResultList();
        // TODO: only do that if there is no Match existant yet.
        RiderUndertakesRideEntity cust = getRideByRiderRouteId(riderrouteId);
        int custId = cust.getCustId().getCustId();

        if (removeRide(riderrouteId)) {
            // ride is removed

            return addRideRequest(custId, starttime_earliest, starttimeLatest, noPassengers, startpt, endpt, price, comment, startptAddress, endptAddress);
        } else {
            // ride could not be removed
            return -1;
        }
    }

    @Override
    public int addRideRequest(int cust_id, Date starttime_earliest, Date starttimeLatest, int noPassengers, Point startpt, Point endpt, double price, String comment, String startptAddress, String endptAddress) {
        init();

        CustomerEntity customer = customerControllerBean.getCustomer(cust_id);

        //TODO: could get Problems if different users simultanously add a RideRequest;Transaction? perhaps locks to much?
        RiderUndertakesRideEntity r = null;
        logger.info("---------------------------addRideRequest, customer is :" + customer + "----------------------");
        if (customer != null) {
            // FIXME: (pab) This index is only valid if a rideId with the same value exists!
            //r = new RiderUndertakesRideEntity(index, starttimeLatest, startpt, endpt, price, noPassengers, starttime_earliest, customer);
            r = new RiderUndertakesRideEntity(customer, starttime_earliest, starttimeLatest, noPassengers, startpt, endpt, price);
            r.setCustId(customer);
            r.setComment(comment);
            r.setStartptAddress(startptAddress);
            r.setEndptAddress(endptAddress);
            logger.info("---------------------------addRideRequest 1: " + r.getCustId().getCustId());
            em.persist(r);
            logger.log(Level.INFO, "riderundertakesride added ");
        } else {

            logger.log(Level.WARNING, "No Customer with id: " + cust_id);
        }
        finish();
        if (r == null) {
            return -1;
        } else {
            callAlgorithm(r.getRiderrouteId(), false);
            return r.getRiderrouteId();
        }
    }

    /**
     * This method can be used to remove a rider from a Ride, that has
     * been already accepted by both parties.
     * @param rideid
     */
    public void removeRiderFromRide(int riderrouteid, int rideid) {
        // remove recent ride from riderundertakesrideentity
        RiderUndertakesRideEntity riderundertakesride = getRideByRiderRouteId(riderrouteid);

        if (riderundertakesride.getRideId() != null) {
            // FIXME: just a test!
            List<RiderUndertakesRideEntity> rider = driverUndertakesRideControllerBean.getRidersForDrive(rideid);
            int numOfRiders = rider.size();

            //mark match as countermanded
            List<MatchEntity> states = (List<MatchEntity>) em.createNamedQuery("MatchEntity.findByRideIdRiderrouteId").setParameter("riderrouteId", riderrouteid).setParameter("rideId", rideid).getResultList();

            for (Iterator<MatchEntity> it = states.iterator(); it.hasNext();) {
                MatchEntity matchEntity = it.next();
                // mark matches as countermanded by rider
                matchEntity.setRiderState(MatchEntity.COUNTERMANDED);
                matchEntity.setRiderChange(new Date());
                em.merge(matchEntity);
            }

            // a ride was already set.
            riderundertakesride.setRideId(null);
            em.persist(riderundertakesride);

            System.out.println(numOfRiders + " die neue Size: " + driverUndertakesRideControllerBean.getRidersForDrive(rideid).size());
        } else {
            //TODO: may return something or throw exception!
            System.out.println("no drive was set!");
        }
    }

    /**
     * This method can be used to get a list of offers that have been matched by the algorithm.
     * @param riderrouteId
     * @return
     */
    public List<MatchEntity> getMatches(int riderrouteId, boolean setRiderAccess) {
        List<MatchEntity> newMatches = new ArrayList<MatchEntity>();
        List<MatchEntity> rejectedMatches = new ArrayList<MatchEntity>();
        ArrayList<MatchEntity> list = new ArrayList<MatchEntity>();
        int rideid;
        //int seats;



        List<MatchEntity> list1 = (List<MatchEntity>) em.createNamedQuery("MatchEntity.findByRiderrouteId").setParameter("riderrouteId", riderrouteId).getResultList();
        RiderUndertakesRideEntity ride = getRideByRiderRouteId(riderrouteId);
        List<RiderUndertakesRideEntity> drives = getInactiveRideRequests(ride.getCustId().getCustNickname());
        if (drives.contains(ride)) {
            // The ride is already inactive so just return such matches which are already completed
            List<MatchEntity> entities = (List<MatchEntity>) em.createNamedQuery("MatchEntity.findByRiderrouteIdSuccessful").setParameter("riderrouteId", riderrouteId).getResultList();
            newMatches = entities;

            if (setRiderAccess) {
                for (MatchEntity m : newMatches) {
                    m.setRiderAccess(new Date());
                    em.merge(m);
                }
            }

        } else if (list1 != null) {
            for (MatchEntity m : list1) {
                //List<DriverUndertakesRideEntity> driveList = new ArrayList<DriverUndertakesRideEntity>();
                if ((m.getDriverState() != null && m.getRiderState() != null) && (m.getDriverState().equals(MatchEntity.ACCEPTED) && m.getRiderState().equals(MatchEntity.ACCEPTED))) {
                    // If there already exists one Match that has been finished only return this one.
                    list.add(m);
                    /*if (setRiderAccess) {
                    for (MatchEntity match : list) {
                    match.setRiderAccess(new Date());
                    em.merge(match);
                    }
                    }*/
                } else if ((m.getDriverState() != null && m.getDriverState().equals(MatchEntity.REJECTED)) || (m.getRiderState() != null && m.getRiderState().equals(MatchEntity.REJECTED))) {
                    // at least one of both candidates has rejected the other
                    rejectedMatches.add(m);
                } else {
                    // either state is both null or one has already accepted
                    rideid = m.getMatchEntityPK().getRideId();
                    //seats = driverUndertakesRideControllerBean.getDriveByDriveId(rideid).getRideOfferedseatsNo();
                    //DriverUndertakesRideEntity ride = driverUndertakesRideControllerBean.getDriveByDriveId(rideid);
                    //List<RiderUndertakesRideEntity> drives = (List<RiderUndertakesRideEntity>) em.createNamedQuery("RiderUndertakesRideEntity.findByRideId").setParameter("rideId", ride).getResultList();
                    //if (drives.size() >= seats) {
                    // this match cannot be added, because the drive is already complete
                    //} else {
                    // FIXME: this could be a problem if bookings are made while a user is sending his acceptance! How can we find out whether a booking is still ok?
                    newMatches.add(m);
                    //}
                }

                if (setRiderAccess) {
                    // Mark all possible matches as accessed (otherwise, status update would be displayed indefinitely)
                    m.setRiderAccess(new Date());
                    em.merge(m);
                }

            }
        }



        /*List<MatchEntity> matches = routeMatchingBean.searchForDrivers(riderrouteId);
        matches = filter(matches);

        for (MatchEntity m : matches) {
        // persist match, so it can be found later on!
        em.persist(m);
        }

        //persist open matchings
        newMatches.addAll(matches);*/
        newMatches.addAll(rejectedMatches);


        if (list.size() > 0) { // tku: Can there ever be more than 1 match in "list"???
            Collections.sort(list, new Comparator() {

                public int compare(Object o1, Object o2) {
                    MatchEntity ent1 = (MatchEntity) o1;
                    MatchEntity ent2 = (MatchEntity) o2;
                    // both rides are future rides
                    if (o1 == null || o2 == null) {
                        return 0;
                    } else {
                        return new Integer(ent1.getMatchEntityPK().getRideId()).compareTo(ent2.getMatchEntityPK().getRideId());
                    }

                }
            });

            /*if (setRiderAccess) {
            for (MatchEntity m : list) {
            m.setRiderAccess(new Date());
            em.merge(m);
            }
            }*/

            return list;
        } else {
            Collections.sort(newMatches, new Comparator() {

                public int compare(Object o1, Object o2) {
                    MatchEntity ent1 = (MatchEntity) o1;
                    MatchEntity ent2 = (MatchEntity) o2;
                    // both rides are future rides
                    if (o1 == null || o2 == null) {
                        return 0;
                    } else {
                        return new Integer(ent1.getMatchEntityPK().getRideId()).compareTo(ent2.getMatchEntityPK().getRideId());
                    }

                }
            });

            // Limit number of unrejected matches to return to `matchCountLimit` (tku 23/08/10)
            int matchCountLimit = 3;

            if (newMatches.size() > matchCountLimit) {
                List<MatchEntity> removedMatches = new ArrayList<MatchEntity>();
                int prevSize = newMatches.size();
                int unrejectedCount = 0;
                for (MatchEntity m : newMatches) {
                    // Generally don't remove matches with states 0, 2, 3 (rejected, countermanded, no more available),
                    // or matches accepted by both parties,
                    // or matches accepted by the other party only
                    if (!(m.getDriverState() == MatchEntity.ACCEPTED || m.getRiderState() == MatchEntity.REJECTED || m.getRiderState() == MatchEntity.COUNTERMANDED || m.getRiderState() == MatchEntity.NO_MORE_AVAILABLE || m.getDriverState() == MatchEntity.REJECTED || m.getDriverState() == MatchEntity.COUNTERMANDED || m.getDriverState() == MatchEntity.NO_MORE_AVAILABLE) && !(m.getRiderState() == MatchEntity.ACCEPTED && m.getDriverState() == MatchEntity.ACCEPTED)) {
                        if (unrejectedCount < matchCountLimit) {
                            // Keep this match in list
                            unrejectedCount++;
                        } else {
                            // Remove this match from list
                            removedMatches.add(m);
                        }
                    }
                }
                newMatches.removeAll(removedMatches);
                logger.log(Level.INFO, "RURC.getMatches: " + (prevSize - newMatches.size()) + " of " + prevSize + " matches dropped (limiting unrejected machtes to 3).");
            }

            return newMatches;
        }
    }

    /**
     * This method is comparing all the matches found by the algorithm to these which are
     * already persisted in the db and adds each unmatched.
     * @param newMatches
     * @param matches
     * @return
     */
    private List<MatchEntity> filter(List<MatchEntity> matches) {
        System.out.println("filter");
        if (matches != null) {
            for (Iterator<MatchEntity> it = matches.iterator(); it.hasNext();) {
                MatchEntity matchEntity = it.next();
                // for every match found by the algorithm
                if (em.createNamedQuery("MatchEntity.findByRideIdRiderrouteId").setParameter("rideId", matchEntity.getMatchEntityPK().getRideId()).setParameter("riderrouteId", matchEntity.getMatchEntityPK().getRiderrouteId()).getResultList().size() > 0) {
                    //TODO: if match has state...
                    // a similar match is already contained in the db
                    it.remove();
                }
            }
        }
        return matches;
    }

    public boolean isRideUpdated(int riderrouteId) {
        if (em.createNamedQuery("MatchEntity.findChangesSinceAccessByRiderByRiderrouteId").setParameter("riderrouteId", riderrouteId).getResultList().size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * This method returns such rides that have not yet been booked and which are nontheless in an active state.
     * @param nickname
     * @return
     */
    public List<RiderUndertakesRideEntity> getActiveOpenRides(String nickname) {
        List<RiderUndertakesRideEntity> returnList = null;
        List<RiderUndertakesRideEntity> past = new ArrayList<RiderUndertakesRideEntity>();
        List<RiderUndertakesRideEntity> future = new ArrayList<RiderUndertakesRideEntity>();

        //TODO: some errorhandling

        // Get the CustomerEntity by <code>nickname</code>
        CustomerEntity c = (CustomerEntity) em.createNamedQuery("CustomerEntity.findByCustNickname").setParameter("custNickname", nickname).getSingleResult();

        if (c != null) {
            // Get the Entity. It should be only one, since nicknames are unique by constraint.

            returnList = em.createNamedQuery("RiderUndertakesRideEntity.findByCustId").setParameter("custId", c).getResultList();
            // Use the Id of the user to get his rides.
            //TODO: Use Franks tables which contain the active rides to find the ones of the user.
        }

        if (returnList == null) {
            returnList = new LinkedList<RiderUndertakesRideEntity>();
        } else {
            java.util.Date now = new java.util.Date();
            for (RiderUndertakesRideEntity d : returnList) {
                if (d.getStarttimeLatest().after(now)) {
                    if (d.getRideId() == null) {
                        // the ride is pending!
                        future.add(d);
                    }
                } else {
                }
            }

            Collections.sort(future, new Comparator() {

                public int compare(Object o1, Object o2) {
                    RiderUndertakesRideEntity ent1 = (RiderUndertakesRideEntity) o1;
                    RiderUndertakesRideEntity ent2 = (RiderUndertakesRideEntity) o2;
                    java.util.Date now = new java.util.Date();
                    // both rides are future rides
                    if (ent1 == null || ent2 == null) {
                        return 0;
                    } else if (ent1.getStarttimeEarliest().before(ent2.getStarttimeEarliest())) {
                        return -1;
                    } else if (ent1.getStarttimeEarliest().after(ent2.getStarttimeEarliest())) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            });

        }
        returnList = future;
        return returnList;
    }

    public void setMatchCountermand(Integer rideId, Integer riderrouteId) {
        List<MatchEntity> match = em.createNamedQuery("MatchEntity.findByRideIdRiderrouteId").setParameter("rideId", rideId).setParameter("riderrouteId", riderrouteId).getResultList();
        if (match.size() > 0) {
            match.get(0).setRiderState(MatchEntity.COUNTERMANDED);
            match.get(0).setRiderChange(new Date());
        }
    }

    public MatchEntity getMatch(Integer rideId, Integer riderrouteId) {
        List<MatchEntity> match = em.createNamedQuery("MatchEntity.findByRideIdRiderrouteId").setParameter("rideId", rideId).setParameter("riderrouteId", riderrouteId).getResultList();
        if (match.size() > 0) {
            return match.get(0);
        }
        return null;
    }
}

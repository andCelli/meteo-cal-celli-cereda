/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.cellicereda.meteocal.businesslogic;

import static com.sun.corba.se.impl.util.Utility.printStackTrace;
import it.polimi.cellicereda.meteocal.entities.Event;
import it.polimi.cellicereda.meteocal.entities.Forecast;
import it.polimi.cellicereda.meteocal.entities.Place;
import it.polimi.cellicereda.meteocal.entities.User;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.primefaces.model.ScheduleEvent;

/**
 *
 * @author stefano
 */
@Stateless
public class CalendarManager {

    @PersistenceContext
    EntityManager em;

    @EJB
    NotificationManager nm;

    @EJB
    ForecastManager fm;

    /**
     * Save into the DB the given event and try to get a forecast for it
     *
     * @param e the event to be saved
     */
    public void save(Event e) {
        em.persist(e);
        fm.saveNewForecastForecastForEvent(e);
    }

    /**
     * Delete from the DB the given event (should not be used)
     *
     * @param e The event to remove
     */
    @Deprecated
    public void delete(Event e) {
        em.remove(e);
    }

    /**
     * Create and persist a new Event
     *
     * @return The created Event
     */
    public Event createNewEvent() {
        Event e = new Event();
        save(e);
        return e;
    }

    /**
     * Search an event by the id
     *
     * @param id The event id to serach for
     * @return The event that has the given id
     */
    public Event getByID(String id) {
        return em.find(Event.class, id);
    }

    /**
     * Get all the stored events
     *
     * @return All the events in the DB
     */
    public List<? extends ScheduleEvent> getAllEvents() {
        return em.createNamedQuery("Event.findAll").getResultList();
    }

    /**
     * Get all the stored events as a list of event and not as a list of wtf
     */
    public List<Event> getAllEventsAsEvents() {
        return em.createNamedQuery("Event.findAll").getResultList();
    }

    /**
     * Search events by creator
     *
     * @param creator A user
     * @return The list of all the events created by the user
     */
    public List<? extends ScheduleEvent> getEventsByCreator(User creator) {

        return em.createNamedQuery("Event.findByCreator").
                setParameter("creator", creator).getResultList();
    }

    /**
     * Search events by participant
     *
     * @param participant A user
     * @return The list of all the events where the user participates (not as a
     * creator)
     */
    public List<? extends ScheduleEvent> getEventsByParticipant(User participant) {
        return em.createNamedQuery("User.getParticipationList").
                setParameter("participant", participant).getResultList();
    }

    /**
     * Get the list of all the participants to an event
     *
     * @param e An event
     * @return The list of all the participants to the given event
     */
    public List<User> getEventParticipant(Event e) {
        try {
            List<User> users = em.createNamedQuery("User.getEventParticipants").
                    setParameter("event", e).getResultList();
            return users;
        } catch (Exception ex) {
            System.err.println("errore nella query");
            printStackTrace();
        }
        return null;
    }

    /**
     * This method adds the user to the list of participants of the event.
     * Usually this method is used by the notification manager when an user
     * answer to an event invite.
     *
     * @param newParticipant The user to add to the list of participants
     * @param event The event where the user will participate
     */
    public void addAnUserToAnEventParticipants(User newParticipant, Event event) {
        newParticipant = em.find(User.class, newParticipant.getEmail());
        event = em.find(Event.class, event.getId());

        newParticipant.addEvent(event);
    }

    /**
     * Change the title of the event and generate the consequent notifications
     */
    public void changeEventTitle(Event event, String newTitle) {
        event = em.find(Event.class, event.getId());

        event.setTitle(newTitle);
        nm.generateEventChangedNotifications(event);
    }

    /**
     * Change the description of the event and generate the consequent
     * notifications
     */
    public void changeEventDescription(Event event, String newDesc) {
        event = em.find(Event.class, event.getId());

        event.setDescription(newDesc);
        nm.generateEventChangedNotifications(event);
    }

    /**
     * Change the starting/ending date of the event and generate the consequent
     * notifications. You have to provide both the date even if only one changes
     */
    public void changeEventTiming(Event event, Date newStarting, Date newEnding) {
        event = em.find(Event.class, event.getId());

        event.setStartDate(newStarting);
        event.setEndDate(newEnding);

        fm.saveNewForecastForecastForEvent(event);

        nm.generateEventChangedNotifications(event);

    }

    /**
     * Change the event locationt and generate the consequent notifications
     */
    public void changeEventLocation(Event event, Place newPlace) {
        event = em.find(Event.class, event.getId());

        if (newPlace != null) {
            newPlace = em.find(Place.class, newPlace.getId());
        }

        event.setEventLocation(newPlace);

        fm.saveNewForecastForecastForEvent(event);

        nm.generateEventChangedNotifications(event);
    }

    /**
     * Change the privacy of the given event
     */
    public void changeEventPrivacy(Event event, boolean newPrivacy) {
        event = em.find(Event.class, event.getId());

        event.setPublicEvent(newPrivacy);
    }

    /**
     * Change the allDay attribute of the given event
     */
    public void changeEventAllDay(Event event, boolean isAllDay) {
        event = em.find(Event.class, event.getId());

        event.setIsAllDay(isAllDay);
    }

    /**
     * Check if the given event will start tomorrow
     */
    public boolean isTomorrow(Event e) {
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.setTime(new Date());
        tomorrow.add(Calendar.DATE, 1);

        Calendar event = Calendar.getInstance();
        event.setTime(e.getStartDate());

        return isSameDay(tomorrow, event);
    }

    /**
     * Check if the given event will start in the given number of days
     */
    public boolean isInNDays(Event e, int nDays) {
        Calendar now = Calendar.getInstance();
        now.setTime(new Date());

        Calendar days = Calendar.getInstance();
        days.setTime(new Date());
        days.add(Calendar.DATE, nDays);

        Calendar event = Calendar.getInstance();
        event.setTime(e.getStartDate());

        return isInDays(now, event, days);
    }

    public boolean isInThreeDays(Event e) {
        return isInNDays(e, 3);
    }

    public boolean isInFiveDays(Event e) {
        return isInNDays(e, 5);
    }

    private boolean isSameDay(Calendar c1, Calendar c2) {
        return (c1.get(Calendar.ERA) == c2.get(Calendar.ERA)
                && c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR));
    }

    private boolean isInDays(Calendar c1, Calendar c2, Calendar c3) {
        c1.set(Calendar.HOUR_OF_DAY, 0);
        c1.set(Calendar.MINUTE, 0);
        c1.set(Calendar.SECOND, 0);
        c1.set(Calendar.MILLISECOND, 0);

        c3.set(Calendar.HOUR_OF_DAY, 23);
        c3.set(Calendar.MINUTE, 59);
        c3.set(Calendar.SECOND, 59);
        c3.set(Calendar.MILLISECOND, 999);

        return c2.after(c1) && c2.before(c3);
    }

    public void changeEventForecast(Event e, Forecast f) {
        e = em.find(Event.class, e.getId());
        e.setForecast(f);
    }
}

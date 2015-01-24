/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.cellicereda.meteocal.businesslogic;

import static com.sun.corba.se.impl.util.Utility.printStackTrace;
import it.polimi.cellicereda.meteocal.entities.Event;
import it.polimi.cellicereda.meteocal.entities.Forecast;
import it.polimi.cellicereda.meteocal.entities.Notification;
import it.polimi.cellicereda.meteocal.entities.NotificationState;
import it.polimi.cellicereda.meteocal.entities.NotificationType;
import it.polimi.cellicereda.meteocal.entities.Place;
import it.polimi.cellicereda.meteocal.entities.User;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
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

    public List<Event> getPastEvents() {
        return em.createNamedQuery("Event.findPreviousToDate").setParameter("date", new Date()).getResultList();
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

        Event old = event.clone();
        event.setTitle(newTitle);

        if (eventIsChanged(old, event)) {
            nm.generateEventChangedNotifications(event);
        }
    }

    /**
     * Change the description of the event and generate the consequent
     * notifications
     */
    public void changeEventDescription(Event event, String newDesc) {
        event = em.find(Event.class, event.getId());

        Event old = event.clone();
        event.setDescription(newDesc);
        if (eventIsChanged(old, event)) {
            nm.generateEventChangedNotifications(event);
        }
    }

    private boolean eventIsChanged(Event old, Event event) {
        return (isChanged(old.getDescription(), event.getDescription())
                || isChanged(old.getEndDate(), event.getEndDate())
                || isChanged(old.getEventLocation(), event.getEventLocation())
                || isChanged(old.getStartDate(), event.getStartDate())
                || isChanged(old.getTitle(), event.getTitle()));
    }

    private boolean isChanged(Object o1, Object o2) {
        if (o1 == null && o2 != null) {
            return true;
        }
        if (o1 == null && o2 == null) {
            return false;
        }
        return o1.equals(o2);
    }

    /**
     * Change the starting/ending date of the event and generate the consequent
     * notifications. You have to provide both the date even if only one changes
     */
    public void changeEventTiming(Event event, Date newStarting, Date newEnding) {
        event = em.find(Event.class, event.getId());

        Event old = event.clone();
        event.setStartDate(newStarting);
        event.setEndDate(newEnding);

        if (eventIsChanged(old, event)) {
            fm.saveNewForecastForecastForEvent(event);
            nm.generateEventChangedNotifications(event);
        }
    }

    /**
     * Change the event locationt and generate the consequent notifications
     */
    public void changeEventLocation(Event event, Place newPlace) {
        event = em.find(Event.class, event.getId());

        if (newPlace != null) {
            newPlace = em.find(Place.class, newPlace.getId());
        }

        Event old = event.clone();

        event.setEventLocation(newPlace);

        if (eventIsChanged(old, event)) {
            fm.saveNewForecastForecastForEvent(event);
            nm.generateEventChangedNotifications(event);
        }
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

    /**
     * Call this method to usubscribe from an event. If the given user is a
     * simple participant he's only removed from the participants list, if the
     * user is the event creator the event is deleted.
     *
     * @param e The event
     * @param u A user, either the event creator or a participant
     */
    public void unSubscribeFromEvent(Event e, User u) {
        e = em.find(Event.class, e.getId());
        u = em.find(User.class, u.getEmail());

        if (e.getCreator()
                .equals(u)) {
            creatorCancelEvent(e);
        } else if (getEventParticipant(e)
                .contains(u)) {
            cancelParticipant(e, u);
        } else {
            throw new IllegalArgumentException("The given user is not the event creator nor an event participant");
        }
    }

    private void creatorCancelEvent(Event e) {
        //remove the participation
        for (User participant : getEventParticipant(e)) {
            participant.removeEvent(e);
        }

        //destroy the forecast
        Forecast f = e.getForecast();
        if (f != null) {
            e.setForecast(null);
            em.remove(f);
        }

        //destroy the notifications
        for (Notification n : nm.getNotificationForEvent(e)) {
            em.remove(n);
        }

        //destroy the event
        em.remove(e);
    }

    /**
     * Delete an invited user. If the user received an invite notification but
     * the notification is still pending the invite is deleted. Otherwise (if
     * the user saw the invite and accepted it) the user is removed from the
     * partecipation list and the event disappears from his calendar
     *
     * @param e The event
     * @param u The user
     */
    public void removeInvitation(Event e, User u) {
        e = em.find(Event.class, e.getId());
        u = em.find(User.class, u.getEmail());

        //If the user received an invite but still have to see it simply delete the notification
        for (Notification n
                : nm.getPendingNotificationForUser(u)) {
            if (n.getNotificationType() == NotificationType.EVENT_INVITE && n.getReferredEvent().equals(e)) {
                em.remove(n);
                return;
            }
        }

        //otherwise check if the user participates in the event and remove the partecipation
        if (u.getEvents()
                .contains(e)) {
            cancelParticipant(e, u);
        }
    }

    /**
     * Remove an user from the event participants and delete all the
     * notifications that have the user as recipient and the event as referred
     * event
     */
    private void cancelParticipant(Event e, User u) {
        //remove all the notifications
        for (Notification n : nm.getNotificationForUser(u)) {
            if (n.getReferredEvent().equals(e)) {
                em.remove(n);
            }
        }

        //cancel the partecipation
        u.removeEvent(e);
    }

    /**
     * Search the user that have been invited to an event but still have not
     * answered to the invite
     *
     * @param e The event
     * @return A list of user invited to the event with a non answered
     * notification
     */
    public List<User> getInvitedUserNotAnsweredInvite(Event e) {
        List<User> toReturn = new LinkedList();

        for (Notification n : nm.getNotificationForEvent(e)) {
            if (!n.getNotificationState().equals(NotificationState.READED) && n.getNotificationType().equals(NotificationType.EVENT_INVITE)) {
                toReturn.add(n.getRecipient());
            }
        }

        return toReturn;
    }
}

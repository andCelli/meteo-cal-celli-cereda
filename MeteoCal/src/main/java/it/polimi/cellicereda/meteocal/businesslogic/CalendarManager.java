/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.cellicereda.meteocal.businesslogic;

import it.polimi.cellicereda.meteocal.entities.Event;
import it.polimi.cellicereda.meteocal.entities.Notification;
import it.polimi.cellicereda.meteocal.entities.NotificationType;
import it.polimi.cellicereda.meteocal.entities.User;
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

    /**
     * Save into the DB the given event
     *
     * @param e the event to be saved
     */
    public void save(Event e) {
        em.persist(e);
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
     * Search an event by the id
     *
     * @param id The event id to serach for
     * @return The event that has the given id
     */
    public Event getByID(Long id) {
        return em.find(Event.class, id);
    }

    /**
     * Get all the stored events
     *
     * @return All the events in the DB
     */
    public List<? extends ScheduleEvent> getAllEvents() {
        return em.createNamedQuery("event.findAll").getResultList();
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
        return em.createNamedQuery("User.getEventParticipants").
                setParameter("event", e).getResultList();
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
        newParticipant.addEvent(event);
    }

    /**
     * Generate an EventChangedNotification for all the users that participates
     * in the given event (excluding the event's creator). If the user already
     * have a pending EventChanged notification for the same event we skip the
     * creation of a new one.
     *
     * @param event The event that just changed
     */
    private void generateEventChangedNotifications(Event event) {
        for (User u : getEventParticipant(event)) {
            //check if we need a new notification
            Boolean needed = true;

            for (Notification n : nm.getPendingNotificationForUser(u)) {
                if (n.getNotificationType() == NotificationType.EVENT_CHANGED
                        && n.getReferredEvent() == event) {
                    needed = false;
                }
            }

            if (needed) {
                Notification n = new Notification(NotificationType.EVENT_CHANGED, u, event);
                em.persist(n);
            }

        }
    }

    /**
     * Change the title of the event and generate the consequent notifications
     */
    public void changeEventTitle(Event event, String newTitle) {
        event.setTitle(newTitle);
        generateEventChangedNotifications(event);
    }

    /**
     * Change the description of the event and generate the consequent
     * notifications
     */
    public void changeEventDescription(Event event, String newDesc) {
        event.setDescription(newDesc);
        generateEventChangedNotifications(event);
    }

    /**
     * Change the starting/ending date of the event and generate the consequent
     * notifications. You have to provide both the date even if only one changes
     */
    public void changeEventTiming(Event event, Date newStarting, Date newEnding) {
        event.setStartingDate(newStarting);
        event.setEndingDate(newEnding);
        generateEventChangedNotifications(event);

    }

    /**
     * Change the event locationt and generate the consequent notifications
     */
    public void changeEventLocation(Event event, Date newEnding) {
        event.setEndingDate(newEnding);
        generateEventChangedNotifications(event);

    }
}

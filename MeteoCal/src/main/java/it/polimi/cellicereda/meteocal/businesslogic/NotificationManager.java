/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.cellicereda.meteocal.businesslogic;

import it.polimi.cellicereda.meteocal.entities.Event;
import it.polimi.cellicereda.meteocal.entities.Notification;
import it.polimi.cellicereda.meteocal.entities.NotificationState;
import it.polimi.cellicereda.meteocal.entities.NotificationType;
import it.polimi.cellicereda.meteocal.entities.User;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author stefano
 */
@Stateless
public class NotificationManager {

    @PersistenceContext
    private EntityManager em;

    @EJB
    private CalendarManager cm;

    /**
     * Persist the given notification
     *
     * @param n The notification to persist
     */
    public void save(Notification n) {
        em.persist(n);
    }

    /**
     * Delete the given notification from the db (this method shouldn't be used)
     *
     * @param n The notification to delete
     */
    @Deprecated
    public void delete(Notification n) {
        em.remove(n);
    }

    /**
     * Get the notifications that a user still have to see.
     *
     * @param recipient The user that is the recipient of the search
     * notifications
     * @return The notifications that have the given user as recipient and
     * pending as state
     */
    public List<Notification> getPendingNotificationForUser(User recipient) {
        recipient = em.find(User.class, recipient.getEmail());
        return em.createNamedQuery("Notification.findForUserAndState").
                setParameter("recipient", recipient).
                setParameter("state", NotificationState.PENDING).getResultList();
    }

    /**
     * Get the pending notifications related to events that still have to take
     * place
     *
     * @param recipient The user that is the recipient of the search
     * notifications
     * @return The notifications that have the given user as recipient and
     * pending as state and the related event still have to start
     */
    public List<Notification> getPendingFutureNotificationForUser(User recipient) {
        recipient = em.find(User.class, recipient.getEmail());

        List<Notification> nonFiltered = getPendingNotificationForUser(recipient);
        List<Notification> filtered = new LinkedList<>();

        Date now = new Date();

        for (Notification n : nonFiltered) {
            if (n.getReferredEvent().getStartDate().after(now)) {
                filtered.add(n);
            }
        }

        return filtered;
    }

    /**
     * Answer to an event invite, set the new state for the notification and
     * manage the calendar according to the answer
     *
     * @param invite The event invite notification
     * @param answer The answer to the invite
     */
    public void answerToAnInvite(Notification invite, Boolean answer) {
        invite = em.find(Notification.class, invite.getId());

        if ((invite.getNotificationType() != NotificationType.EVENT_INVITE)
                || (invite.getNotificationState() != NotificationState.PENDING)) {
            throw new IllegalArgumentException();
        }

        invite.setNotificationAnswer(answer);
        invite.setNotificationState(NotificationState.ANSWERED);

        if (answer) {
            cm.addAnUserToAnEventParticipants(invite.getRecipient(), invite.getReferredEvent());
        }
    }

    /**
     * Answer to a SunnyDayProposal notification, set the new state for the
     * notification and change the event details, the calendar manager will take
     * care of generate the right EventChanged notifications. If the user
     * accepts the proposal you have to provide in the paarmeters the new
     * starting date chosen by the user, the ending date will be set
     * automagically so taht the event duration remains the same.
     *
     * @param proposal The answered notification
     * @param answer True if the user accepts the proposal
     * @param newStartingDate The new starting date (null if the user didn't
     * accept the proposal)
     */
    public void answerToASunnyDayProposal(Notification proposal, Boolean answer, Date newStartingDate) {
        proposal = em.find(Notification.class, proposal.getId());

        if ((proposal.getNotificationType() != NotificationType.SUNNY_DAY_PROPOSAL)
                || (proposal.getNotificationState() != NotificationState.PENDING)) {
            throw new IllegalArgumentException();
        }

        proposal.setNotificationAnswer(answer);
        proposal.setNotificationState(NotificationState.ANSWERED);

        if (answer) {
            Event e = proposal.getReferredEvent();

            //new ending = oldEnding + (newstart - oldstart)
            Date newEnding = new Date();
            newEnding.setTime(e.getEndDate().getTime() + newStartingDate.getTime() - e.getStartDate().getTime());

            cm.changeEventTiming(e, newStartingDate, newEnding);
        }
    }

    /**
     * Create an event invite notification
     *
     * @param event The event that you are inviting for
     * @param invited The user you want to invite
     */
    public void sendAnInvite(Event event, User invited) {
        event = em.find(Event.class, event.getId());
        invited = em.find(User.class, invited.getEmail());

        Notification n = new Notification(NotificationType.EVENT_INVITE, invited, event);
        em.persist(n);
    }

    /**
     * Read a notification, use this method when you want to set the
     * notification state to readed
     */
    public void readNotification(Notification n) {
        n = em.find(Notification.class, n.getId());
        n.setNotificationState(NotificationState.READED);
    }

    /**
     * Send a bad weather alert to all the event participant and to the event
     * creator
     *
     * @param e The event with a bad weather
     */
    public void sendBadWeatherAlert(Event e) {
        e = em.find(Event.class, e.getId());

        //an alert for all the participants
        for (User u : cm.getEventParticipant(e)) {
            Notification n = new Notification(NotificationType.BAD_WEATHER_ALERT, u, e);
            em.persist(n);
        }

        //and one for the crator
        Notification n = new Notification(NotificationType.BAD_WEATHER_ALERT, e.getCreator(), e);
        em.persist(n);
    }

    /**
     * Send a sunny day proposal to the event creator
     *
     * @param e The event witha bad weather
     */
    public void sendSunnyDayProposal(Event e) {
        e = em.find(Event.class, e.getId());

        Notification n = new Notification(NotificationType.SUNNY_DAY_PROPOSAL, e.getCreator(), e);
        em.persist(n);
    }

    /**
     * Generate an EventChangedNotification for all the users that participates
     * in the given event (excluding the event's creator). If the user already
     * have a pending EventChanged notification for the same event we skip the
     * creation of a new one.
     *
     * @param event The event that just changed
     */
    public void generateEventChangedNotifications(Event event) {
        event = em.find(Event.class, event.getId());

        for (User u : cm.getEventParticipant(event)) {
            //check if we need a new notification
            Boolean needed = true;

            for (Notification n : getPendingNotificationForUser(u)) {
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
}

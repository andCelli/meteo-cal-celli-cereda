/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.cellicereda.meteocal.businesslogic;

import it.polimi.cellicereda.meteocal.entities.Notification;
import it.polimi.cellicereda.meteocal.entities.NotificationState;
import it.polimi.cellicereda.meteocal.entities.NotificationType;
import it.polimi.cellicereda.meteocal.entities.User;
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
     * Get the notifications that a user still have to see
     *
     * @param recipient The user that is the recipient of the search
     * notifications
     * @return The notifications that have the given user as recipient and
     * pending as state
     */
    public List<Notification> getPendingNotificationForUser(User recipient) {
        return em.createNamedQuery("Notification.findPendingForUser").
                setParameter("recipient", recipient).
                setParameter("state", NotificationState.PENDING).getResultList();
    }

    /**
     * Answer to an event invite, set the new state for the notification and
     * manage the calendar according to the answer
     *
     * @param invite The event invite notification
     * @param answer The answer to the invite
     */
    public void answerToAnInvite(Notification invite, Boolean answer) {
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
     * care of generate the right EventChanged notifications
     *
     * @param proposal The answered notification
     * @param answer True if the user accepts the proposal
     */
    public void answerToASunnyDayProposal(Notification proposal, Boolean answer) {
        if ((proposal.getNotificationType() != NotificationType.SUNNY_DAY_PROPOSAL)
                || (proposal.getNotificationState() != NotificationState.PENDING)) {
            throw new IllegalArgumentException();
        }

        proposal.setNotificationAnswer(answer);
        proposal.setNotificationState(NotificationState.ANSWERED);

        if (answer){
            cm.TODO
        }
    }
}

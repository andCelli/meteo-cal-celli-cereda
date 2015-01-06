/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.cellicereda.meteocal.businesslogic;

import it.polimi.cellicereda.meteocal.entities.Notification;
import it.polimi.cellicereda.meteocal.entities.NotificationState;
import it.polimi.cellicereda.meteocal.entities.User;
import java.util.List;
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
    EntityManager em;

    /**
     * Persist the given notification
     *
     * @param n The notification to persist
     */
    public void save(Notification n) {
        em.persist(n);
    }

    /**
     * Delete the given notification from the db (this method shouldn't be used
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
}

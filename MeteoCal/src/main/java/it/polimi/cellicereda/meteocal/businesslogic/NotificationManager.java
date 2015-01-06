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

    public void save(Notification n) {
        em.persist(n);
    }

    public void delete(Notification n) {
        em.remove(n);
    }

    public List<Notification> getPendingNotificationForUser(User recipient) {
        return em.createNamedQuery("Notification.findPendingForUser").
                setParameter("recipient", recipient).getResultList();
    }
}

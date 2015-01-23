/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.cellicereda.meteocal.businesslogic;

import it.polimi.cellicereda.meteocal.entities.Event;
import it.polimi.cellicereda.meteocal.entities.Forecast;
import it.polimi.cellicereda.meteocal.entities.Notification;
import it.polimi.cellicereda.meteocal.entities.NotificationState;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * This is a class that takes care of keeping the db clean
 *
 * @author stefano
 */
@Singleton
public class Cleanupper {

    @EJB
    private ForecastManager fm;

    @EJB
    private NotificationManager nm;

    @EJB
    private CalendarManager cm;

    @PersistenceContext
    private EntityManager em;

    @Schedule(hour = "*", minute = "*")
    private void cleanup() {
        cleanupUselessForecast();
        cleanupUselessNotification();
        cleanupOldEvents();
    }

    private void cleanupUselessForecast() {
        //delete the forecast without a related event (they should not exist but they could have been created by a mistake)
        for (Forecast f : fm.findForecastWithoutRelatedEvent()) {
            em.remove(f);
        }
    }

    private void cleanupUselessNotification() {
        //delete the notification without a related event
        for (Notification n : nm.findNotificationWithoutEvent()) {
            em.remove(n);
        }
    }

    private void cleanupOldEvents() {
        //for all the past events delete the related forecast and the readed notification (we don't delete pending notifications in order to avoid casualties between girls because "THAT FUCKING BITCH DIDN'T INVITE ME")
        for (Event e : cm.getPastEvents()) {
            //destroy the forecast
            Forecast f = e.getForecast();
            if (f != null) {
                em.remove(f);
                e.setForecast(null);
            }

            //destroy the notification
            for (Notification n : nm.getNotificationForEvent(e)) {
                if (!(n.getNotificationState() == NotificationState.PENDING)) {
                    em.remove(n);
                }
            }
        }
    }
}

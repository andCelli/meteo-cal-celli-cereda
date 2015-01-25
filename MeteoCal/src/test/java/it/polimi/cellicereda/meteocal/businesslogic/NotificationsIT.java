/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.cellicereda.meteocal.businesslogic;

import it.polimi.cellicereda.meteocal.entities.Event;
import it.polimi.cellicereda.meteocal.entities.Notification;
import it.polimi.cellicereda.meteocal.entities.NotificationType;
import it.polimi.cellicereda.meteocal.entities.Place;
import it.polimi.cellicereda.meteocal.entities.User;
import it.polimi.cellicereda.meteocal.gui.DetailsEventBean;
import it.polimi.cellicereda.meteocal.gui.ModifyEventBean;
import it.polimi.cellicereda.meteocal.gui.ScheduleBean;
import it.polimi.cellicereda.meteocal.gui.Utility;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author Andrea
 */
@RunWith(Arquillian.class)
public class NotificationsIT {

    @Inject
    private ModifyEventBean modify;
    @Inject
    private ScheduleBean schedule;
    @EJB
    private CalendarManager calManager;
    @EJB
    private UserProfileManager upm;
    @EJB
    private LocationManager lm;
    @EJB
    private Utility utility;
    @EJB
    private NotificationManager notification;
    @Inject
    private DetailsEventBean det;
    @Inject
    private ForecastManager forecast;

    @PersistenceContext
    EntityManager em;

    @Deployment
    public static WebArchive createArchiveAndDeploy() {
        return ShrinkWrap.create(WebArchive.class)
                .addClass(ModifyEventBean.class)
                .addClass(ScheduleBean.class)
                .addClass(CalendarManager.class)
                .addClass(UserProfileManager.class)
                .addClass(LocationManager.class)
                .addClass(Utility.class)
                .addClass(NotificationManager.class)
                .addClass(DetailsEventBean.class)
                .addClass(ForecastManager.class)
                .addPackage(User.class.getPackage())
                .addPackage(Event.class.getPackage())
                .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Test
    public void sendInvite() {

        //set current user
        User u1 = new User();
        u1.setUsername("a");
        u1.setName("andrea");
        u1.setSurname("celli");
        u1.setPassword("a");
        u1.setPublicCalendar(true);
        u1.setEmail("a@a.com");
        upm.save(u1);
        modify.setCurrentUser(u1);

        //create the invited user
        User invited = new User();
        invited.setUsername("b");
        invited.setName("b");
        invited.setSurname("b");
        invited.setPassword("b");
        invited.setPublicCalendar(true);
        invited.setEmail("b@b.com");
        upm.save(invited);

        //create the event with a partecipant
        Event e1 = new Event();
        modify.setEvent(e1);
        modify.setTitle("send invite");
        modify.getInvitedUsers().add(invited);
        modify.saveEvent();

        //check if the notification has been sent
        List<Notification> invite = notification.getNotificationForUser(invited);
        assertNotNull(invite);
        assertEquals(invite.size(), 1);
        assertEquals(invite.get(0).getNotificationType(), NotificationType.EVENT_INVITE);
        assertEquals(invite.get(0).getReferredEvent().getId(), e1.getId());

    }

    @Test
    public void eventChangedNotification() {
        //set current user
        User u1 = new User();
        u1.setUsername("a");
        u1.setName("andrea");
        u1.setSurname("celli");
        u1.setPassword("a");
        u1.setPublicCalendar(true);
        u1.setEmail("new@a.com");
        upm.save(u1);
        modify.setCurrentUser(u1);

        //create the invited user
        User invited = new User();
        invited.setUsername("b");
        invited.setName("b");
        invited.setSurname("b");
        invited.setPassword("b");
        invited.setPublicCalendar(true);
        invited.setEmail("new@b.com");
        upm.save(invited);

        //create and save new event
        Event e1 = new Event();
        e1.setCreator(u1);
        e1.setTitle("e1");
        e1.setStartDate(new Date());
        e1.setEndDate(new Date());
        calManager.save(e1);
        calManager.addAnUserToAnEventParticipants(invited, e1);

        //modify the event e1
        det.setEvent(em.find(Event.class, e1.getId()));
        det.modify();
        modify.setTitle("new Title");
        modify.saveEvent();

        //check for the notification
        List<Notification> n = notification.getNotificationForUser(invited);
        assertNotNull(n);
        assertEquals(n.size(), 1);
        assertEquals(n.get(0).getReferredEvent().getId(), e1.getId());
        assertEquals(n.get(0).getNotificationType(), NotificationType.EVENT_CHANGED);
    }

    @Test
    public void noNotificationsIfEventNotChanged() {
        //set current user
        User u1 = new User();
        u1.setUsername("a");
        u1.setName("andrea");
        u1.setSurname("celli");
        u1.setPassword("a");
        u1.setPublicCalendar(true);
        u1.setEmail("otherMail@a.com");
        upm.save(u1);
        modify.setCurrentUser(u1);

        //create the invited user
        User invited = new User();
        invited.setUsername("b");
        invited.setName("b");
        invited.setSurname("b");
        invited.setPassword("b");
        invited.setPublicCalendar(true);
        invited.setEmail("otherMail@b.com");
        upm.save(invited);

        //create and save new event
        Event e1 = new Event();
        e1.setCreator(u1);
        e1.setTitle("e1");
        e1.setStartDate(new Date());
        e1.setEndDate(new Date());
        calManager.save(e1);
        calManager.addAnUserToAnEventParticipants(invited, e1);

        //save without making any change
        det.setEvent(em.find(Event.class, e1.getId()));
        det.modify();
        modify.saveEvent();

        //no event changed notifications referring to e1
        List<Notification> n = notification.getNotificationForUser(invited);
        for (Notification notif : n) {
            assertTrue(!(notif.getReferredEvent().equals(e1)
                    && notif.getNotificationType().equals(NotificationType.EVENT_CHANGED)));
        }
    }

    @Test
    public void movingBackAnEventRegeneratesNotification() {
        //an user creates an event for tomorrow
        User u1 = new User();
        u1.setUsername(" ");
        u1.setName("giovanni");
        u1.setSurname("nonso");
        u1.setPassword("facile");
        u1.setPublicCalendar(true);
        u1.setEmail("li.cazzi@tua.no");
        upm.save(u1);
        modify.setCurrentUser(u1);

        Place p = em.find(Place.class, (long) 5557293);
        if (p == null) {
            p = new Place();
            p.setId((long) 5557293);
            p.setName("Sitka");
            p.setCountry("US");
            p.setLongitude(-135.330002);
            p.setLatitude(57.053059);
            em.persist(p);
        }

        Event e = new Event();
        e.setCreator(u1);
        e.setDescription(null);
        e.setTitle("prova");
        e.setStartDate(new Date((new Date()).getTime() + 24 * 60 * 60 * 100));
        e.setEndDate(new Date(e.getStartDate().getTime() + 1 * 60 * 60 * 1000));
        e.setEventLocation(lm.getPlaceByID(p.getId()));
        calManager.save(e);

        //if the forecast is bad we should have a bad weather notification
        if (!forecast.isGoodWeather(e.getForecast().getWeatherId())) {
            boolean ok = false;
            for (Notification n : notification.getPendingNotificationForUser(u1)) {
                if (n.getReferredEvent().equals(e) && n.getNotificationType() == NotificationType.BAD_WEATHER_ALERT) {
                    ok = true;
                }
            }
            assertTrue(ok);
        }

        calManager.changeEventTiming(e, new Date(e.getStartDate().getTime() + 24 * 60 * 60 * 1000), new Date(e.getEndDate().getTime() + 24 * 60 * 60 * 1000));

        //if the forecast is bad we should have a sunny day proposal and no bad weather alert
        if (!forecast.isGoodWeather(e.getForecast().getWeatherId())) {
            boolean ok1 = true;
            boolean ok2 = false;

            for (Notification n : notification.getPendingNotificationForUser(u1)) {
                if (n.getReferredEvent().equals(e) && n.getNotificationType() == NotificationType.BAD_WEATHER_ALERT) {
                    ok1 = false;
                }
                if (n.getReferredEvent().equals(e) && n.getNotificationType() == NotificationType.SUNNY_DAY_PROPOSAL) {
                    ok2 = true;
                }
            }
            assertTrue(!ok1 && ok2);
        }
    }
}

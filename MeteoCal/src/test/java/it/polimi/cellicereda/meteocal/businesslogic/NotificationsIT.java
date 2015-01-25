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
    public void sendInvite(){
        
        //set current user
        User u1=new User();
        u1.setUsername("a");
        u1.setName("andrea");
        u1.setSurname("celli");
        u1.setPassword("a");
        u1.setPublicCalendar(true);
        u1.setEmail("a@a.com");
        upm.save(u1);
        modify.setCurrentUser(u1);
        
        //create the invited user
        User invited=new User();
        invited.setUsername("b");
        invited.setName("b");
        invited.setSurname("b");
        invited.setPassword("b");
        invited.setPublicCalendar(true);
        invited.setEmail("b@b.com");
        upm.save(invited);
        
        //create the event with a partecipant
        Event e1=new Event();
        modify.setEvent(e1);
        modify.setTitle("send invite");
        modify.getInvitedUsers().add(invited);
        modify.saveEvent();
        
        //check if the notification has been sent
        List<Notification> invite=notification.getNotificationForUser(invited);
        assertNotNull(invite);
        assertEquals(invite.size(),1);
        assertEquals(invite.get(0).getNotificationType(),NotificationType.EVENT_INVITE);
        assertEquals(invite.get(0).getReferredEvent().getId(),e1.getId());
        
        
    }
    
   @Test
    public void eventChangedNotification(){
        //set current user
        User u1=new User();
        u1.setUsername("a");
        u1.setName("andrea");
        u1.setSurname("celli");
        u1.setPassword("a");
        u1.setPublicCalendar(true);
        u1.setEmail("new@a.com");
        upm.save(u1);
        modify.setCurrentUser(u1);
        
        //create the invited user
        User invited=new User();
        invited.setUsername("b");
        invited.setName("b");
        invited.setSurname("b");
        invited.setPassword("b");
        invited.setPublicCalendar(true);
        invited.setEmail("new@b.com");
        upm.save(invited);
        
        //create and save new event
        Event e1=new Event();
        e1.setCreator(u1);
        e1.setTitle("e1");
        e1.setStartDate(new Date());
        e1.setEndDate(new Date());
        calManager.save(e1);
        calManager.addAnUserToAnEventParticipants(invited, e1);
        
        //modify the event e1
        det.setEvent(em.find(Event.class,e1.getId()));
        det.modify();
        modify.setTitle("new Title");
        modify.saveEvent();
        
        //check for the notification
        List<Notification> n=notification.getNotificationForUser(invited);
        assertNotNull(n);
        assertEquals(n.size(),1);
        assertEquals(n.get(0).getReferredEvent().getId(), e1.getId());
        assertEquals(n.get(0).getNotificationType(),NotificationType.EVENT_CHANGED);
    }
}

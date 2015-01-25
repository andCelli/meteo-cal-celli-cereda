/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.cellicereda.meteocal.businesslogic;

import it.polimi.cellicereda.meteocal.entities.Event;
import it.polimi.cellicereda.meteocal.entities.User;
import java.util.List;
import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author Arquillian
 */
@RunWith(Arquillian.class)
public class CalendarManagerIT {

    @EJB
    private CalendarManager cm;
    @EJB
    private NotificationManager nm;
    @EJB
    private ForecastManager fm;
    @EJB
    private LocationManager lm;
    @EJB
    private UserProfileManager upm;

    @PersistenceContext
    EntityManager em;

    @Deployment
    public static WebArchive createArchiveAndDeploy() {
        return ShrinkWrap.create(WebArchive.class)
                .addClass(CalendarManager.class)
                .addClass(NotificationManager.class)
                .addClass(LocationManager.class)
                .addClass(ForecastManager.class)
                .addClass(UserProfileManager.class)
                .addPackage(User.class.getPackage())
                .addPackage(Event.class.getPackage())
                .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Test
    public void removeInvitationTest() {
        User u1 = new User();
        u1.setUsername("a");
        u1.setName("andrea");
        u1.setSurname("celli");
        u1.setPassword("a");
        u1.setPublicCalendar(true);
        u1.setEmail("removeInvitationTest@a.com");
        upm.save(u1);

        User u2 = new User();
        u2.setUsername("a");
        u2.setName("andrea");
        u2.setSurname("celli");
        u2.setPassword("a");
        u2.setPublicCalendar(true);
        u2.setEmail("removeInvitationTest2@a.com");
        upm.save(u2);

        Event e1 = new Event();
        e1.setCreator(u1);
        cm.save(e1);

        Event e2 = new Event();
        e1.setCreator(u2);
        cm.save(e2);
        cm.addAnUserToAnEventParticipants(u1, e2);

        Event e3 = new Event();
        e1.setCreator(u2);
        cm.save(e3);
        nm.sendAnInvite(e3, u1);

        cm.removeInvitation(e2, u1);
        List<User> users = cm.getEventParticipant(e2);
        assertTrue(!(users.contains(u1)));
    }
}

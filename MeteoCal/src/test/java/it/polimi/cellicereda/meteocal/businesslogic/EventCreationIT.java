/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.cellicereda.meteocal.businesslogic;

import it.polimi.cellicereda.meteocal.entities.Event;
import it.polimi.cellicereda.meteocal.entities.User;
import it.polimi.cellicereda.meteocal.gui.DetailsEventBean;
import it.polimi.cellicereda.meteocal.gui.LoggerProducer;
import it.polimi.cellicereda.meteocal.gui.ModifyEventBean;
import it.polimi.cellicereda.meteocal.gui.RegistrationBean;
import it.polimi.cellicereda.meteocal.gui.ScheduleBean;
import it.polimi.cellicereda.meteocal.gui.Utility;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author Andrea
 */
@RunWith(Arquillian.class)
public class EventCreationIT {
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
    public void ModifyEventBeanShouldBeInjected() {
        assertNotNull(modify);
    }
    
    @Test
    public void EntityManagerShouldBeInjected() {
        assertNotNull(em);
    }
    
    @Test
    public void ScheduleBeanShouldBeInjected() {
        assertNotNull(schedule);
    }
    
    @Test
    public void DetailsEventBeanShouldBeInjected() {
        assertNotNull(det);
    }
    
    @Test
    public void createNewEvent(){
        //set current user
        User u1=new User();
        u1.setUsername("a");
        u1.setName("andrea");
        u1.setSurname("celli");
        u1.setPassword("a");
        u1.setPublicCalendar(true);
        u1.setEmail("a1@a.com");
        upm.save(u1);
        modify.setCurrentUser(u1);
        
        modify.setTitle("prova");
        modify.saveEvent();
        
        assertEquals(calManager.getEventsByCreator(u1).size(),1);
        assertEquals(calManager.getEventsByCreator(u1).get(0).getTitle(),"prova");
    }
}


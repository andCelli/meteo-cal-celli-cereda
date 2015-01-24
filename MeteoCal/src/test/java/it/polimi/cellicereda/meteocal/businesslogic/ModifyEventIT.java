/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.cellicereda.meteocal.businesslogic;

import it.polimi.cellicereda.meteocal.entities.Event;
import it.polimi.cellicereda.meteocal.entities.User;
import it.polimi.cellicereda.meteocal.gui.DetailsEventBean;
import it.polimi.cellicereda.meteocal.gui.ModifyEventBean;
import it.polimi.cellicereda.meteocal.gui.ScheduleBean;
import it.polimi.cellicereda.meteocal.gui.SettingsBean;
import it.polimi.cellicereda.meteocal.gui.UtilityMethods;
import java.nio.file.Paths;
import java.util.Date;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolationException;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author Andrea
 */
@RunWith(Arquillian.class)
public class ModifyEventIT {
    
   @Inject
   private SettingsBean sb;

   @EJB
   private UserProfileManager upm; 
   
    @PersistenceContext
    private EntityManager em;
    
    @Deployment
    public static WebArchive createArchiveAndDeploy(){
        return ShrinkWrap.create(WebArchive.class)
                .addClass(SettingsBean.class)
                .addClass(UserProfileManager.class)
                .addPackage(User.class.getPackage())
                .addAsResource("test-persistence.xml","META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");     
    }
    
  @Test
    public void SettingsInjected(){
        assertNotNull(sb);
    }
    
  @Test
    public void EntityManagerShouldBeInjected() {
        assertNotNull(em);
    }
    
    /**
     * no events saved with start date>end date
     *
    @Test
    public void noEmptyFieldsInSettings() {
        User u=new User();
        u.setEmail("a@a.com");
        u.setUsername("a");
        u.setName("a");
        u.setSurname("a");
        u.setPassword("a");
        u.setPublicCalendar(true);
        em.persist(u);
        sb.setCurrentUser(u);
        sb.setName(new String());
        try {
            sb.save();
        } catch (Exception e) {
            assertTrue(e.getCause() instanceof ConstraintViolationException);
        }
    }*/
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.cellicereda.meteocal.businesslogic;

import it.polimi.cellicereda.meteocal.entities.Event;
import it.polimi.cellicereda.meteocal.entities.User;
import it.polimi.cellicereda.meteocal.gui.SettingsBean;
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
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
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
public class SettingsIT {
    
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
     * no empty fields in settings
     */
    @Test
    public void noEmptyFieldsInSettings() {
        User u=new User();
        u.setEmail("a3@a.com");
        u.setUsername("a");
        u.setName("a");
        u.setSurname("a");
        u.setPassword("a");
        u.setPublicCalendar(true);
        upm.save(u);
        sb.setCurrentUser(u);
        sb.getCurrentUser().setName(null);
        sb.getCurrentUser().setSurname("new");
        try {
            sb.save();
        } catch (Exception e){}
        //name and surname have not been changed
        User updatedUser=em.find(User.class,u.getEmail());
        assertEquals(updatedUser.getName(),"a");
        assertEquals(updatedUser.getSurname(),"a");
    }
    
    /**
     * if new data are fine user info are updated
     */
    @Test
    public void updateData(){
        User u=new User();
        u.setEmail("b3@a.com");
        u.setUsername("a");
        u.setName("a");
        u.setSurname("a");
        u.setPassword("a");
        u.setPublicCalendar(false);
        upm.save(u);
        
        sb.setCurrentUser(u);
        sb.getCurrentUser().setName("new");
        sb.getCurrentUser().setSurname("new");
        sb.getCurrentUser().setUsername("new");
        sb.getCurrentUser().setPublicCalendar(false);
        sb.save();
        
        User updatedUser=em.find(User.class,u.getEmail());
        assertNotNull(updatedUser);
        assertEquals(updatedUser.getUsername(),"new");
        assertEquals(updatedUser.getName(),"new");
        assertEquals(updatedUser.getSurname(),"new");
        assertEquals(updatedUser.getPublicCalendar(),false);    
    }
}

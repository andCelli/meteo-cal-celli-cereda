/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.cellicereda.meteocal.businesslogic;

import it.polimi.cellicereda.meteocal.entities.User;
import it.polimi.cellicereda.meteocal.gui.LoggerProducer;
import it.polimi.cellicereda.meteocal.gui.RegistrationBean;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolationException;
import static org.hamcrest.CoreMatchers.is;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author Andrea
 */
@RunWith(Arquillian.class)
public class RegistrationIT {
    @Inject
    private RegistrationBean reg;
    @EJB
    private UserProfileManager upm;
    @PersistenceContext
    EntityManager em;
    @Inject 
    Logger logger;
    
    
   @Deployment
    public static WebArchive createArchiveAndDeploy() {
        return ShrinkWrap.create(WebArchive.class)
                .addClass(RegistrationBean.class)
                .addClass(UserProfileManager.class)
                .addClass(LoggerProducer.class)
                .addPackage(User.class.getPackage())
                .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }
    
    @Test
    public void RegistrationBeanShouldBeInjected() {
        assertNotNull(reg);
    }
    
    @Test
    public void EntityManagerShouldBeInjected() {
        assertNotNull(em);
    }
    
    @Test
    public void newUserShouldBeComplete(){
        User newUser=new User();
        newUser.setEmail("mail@mail.com");
        reg.setUser(newUser);
        try{
        reg.register();
        }catch(NullPointerException e){}
        assertNull(em.find(User.class,"mail@mail.com"));     
        }
    
    @Test
    public void invalidEmail(){
        User u1=new User();
        u1.setUsername("a");
        u1.setName("andrea");
        u1.setSurname("celli");
        u1.setPassword("a");
        u1.setPublicCalendar(true);
        u1.setEmail("a");
        reg.setUser(u1);
        try{
            reg.register();
        }catch(Exception e){}
        assertNull(em.find(User.class,"a"));       
    }
    
    @Test
    public void validRegistration(){
        User u1=new User();
        u1.setUsername("a");
        u1.setName("andrea");
        u1.setSurname("celli");
        u1.setPassword("a");
        u1.setPublicCalendar(true);
        u1.setEmail("a5@a.com");
        reg.setUser(u1);
        
        reg.register();
        
        User saved=em.find(User.class,"a5@a.com");
        assertNotNull(saved);
        assertEquals(saved.getUsername(),"a");
        assertEquals(saved.getName(),"andrea");
        assertEquals(saved.getSurname(),"celli");
        assertEquals(saved.getPublicCalendar(),true);
        assertThat(saved.getPassword(),is(PasswordEncrypter.encryptPassword("a")));
    }
}

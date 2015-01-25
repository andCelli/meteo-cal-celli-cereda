/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.cellicereda.meteocal.businesslogic;

import it.polimi.cellicereda.meteocal.entities.User;
import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author Andrea
 */
@RunWith(Arquillian.class)
public class UserProfileManagerIT {
    @EJB
   private UserProfileManager upm; 
   
    @PersistenceContext
    private EntityManager em;
    
    @Deployment
    public static WebArchive createArchiveAndDeploy(){
        return ShrinkWrap.create(WebArchive.class)
                .addClass(UserProfileManager.class)
                .addPackage(User.class.getPackage())
                .addAsResource("test-persistence.xml","META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");     
    }
    
    @Test
    public void saveTest(){
        User u1=new User();
        u1.setUsername("a");
        u1.setName("a");
        u1.setSurname("a");
        u1.setPassword("a");
        u1.setPublicCalendar(true);
        u1.setEmail("a@a.com");
        upm.save(u1);
        
        User savedUser=em.find(User.class,u1.getEmail());
        assertEquals(u1,savedUser);
    }
}

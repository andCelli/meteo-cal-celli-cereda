/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.cellicereda.meteocal.businesslogic;

import it.polimi.cellicereda.meteocal.entities.User;
import it.polimi.cellicereda.meteocal.gui.SearchBean;
import it.polimi.cellicereda.meteocal.gui.SettingsBean;
import java.security.Principal;
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
public class SearchIT {
    
   @Inject
   private SearchBean search;

   @EJB
   private UserProfileManager upm; 
   
   @Inject
   private Principal p;
   
    @PersistenceContext
    private EntityManager em;
    
    @Deployment
    public static WebArchive createArchiveAndDeploy(){
        return ShrinkWrap.create(WebArchive.class)
                .addClass(SearchBean.class)
                .addClass(UserProfileManager.class)
                .addClass(Principal.class)
                .addPackage(User.class.getPackage())
                .addAsResource("test-persistence.xml","META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");     
    }
    
    @Test
    public void searchUser(){
        User current=new User();
        current.setUsername("c");
        current.setName("c");
        current.setSurname("c");
        current.setPassword("c");
        current.setPublicCalendar(true);
        current.setEmail("c4@a.com");
        upm.save(current);
        search.setCurrentUser(current);
        
        User u1=new User();
        u1.setUsername("andre");
        u1.setName("andrea");
        u1.setSurname("celli");
        u1.setPassword("a");
        u1.setPublicCalendar(true);
        u1.setEmail("a4@a.com");
        upm.save(u1);
        
        User u2=new User();
        u2.setUsername("b");
        u2.setName("b");
        u2.setSurname("b");
        u2.setPassword("b");
        u2.setPublicCalendar(true);
        u2.setEmail("b4@a.com");
        upm.save(u2);
        
        //username
        search.setSearchKey("andre");
        List<User> results=search.getSearchResults();
        assertNotNull(results);
        for(User u:results){
        assertEquals(u.getUsername(),"andre");
        }
        //name
        search.setSearchKey("andrea");
        results=search.getSearchResults();
        assertNotNull(results);
        for(User u:results){
        assertEquals(u.getName(),"andrea");
        }
        //surname
        search.setSearchKey("celli");
        results=search.getSearchResults();
        assertNotNull(results);
        for(User u:results){
        assertEquals(u.getSurname(),"celli");
        }
        //no current user search himself
        search.setSearchKey("c");
        results=search.getSearchResults();
        assertNotNull(results);
        assertEquals(results.size(),0);
    }
}
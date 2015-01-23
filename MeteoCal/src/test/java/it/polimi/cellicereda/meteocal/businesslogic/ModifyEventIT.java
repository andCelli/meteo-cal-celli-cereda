/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.cellicereda.meteocal.businesslogic;

import it.polimi.cellicereda.meteocal.entities.Event;
import it.polimi.cellicereda.meteocal.gui.ModifyEventBean;
import java.util.Date;
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
    ModifyEventBean meb;
    
    @PersistenceContext
    EntityManager em;
    
    @Deployment
    public static WebArchive createArchiveAndDeploy(){
        return ShrinkWrap.create(WebArchive.class)
                .addClass(ModifyEventBean.class)
                .addPackage(Event.class.getPackage())
                .addAsResource("test-persistence.xml","META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE,"beans.xml");      
    }
    
    @Test
    public void ModifyInjected(){
        assertNotNull(meb);
    }
    
    @Test
    public void EntityManagerShouldBeInjected() {
        assertNotNull(em);
    }
    
    /**
     * no events saved with start date>end date
     */
    @Test
    public void newEventShouldBeValid() {
        Event newEvent = new Event();
        meb.setEndingDate(new Date(2015,1,20));
        meb.setStartingDate(new Date());
        meb.setEvent(newEvent);
        try {
            meb.saveEvent();
        } catch (Exception e) {
            assertTrue(e.getCause() instanceof ConstraintViolationException);
        }
    }
}

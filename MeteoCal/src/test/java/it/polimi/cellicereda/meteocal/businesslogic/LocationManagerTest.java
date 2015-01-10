/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.cellicereda.meteocal.businesslogic;

import javax.persistence.EntityManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

/**
 *
 * @author stefano
 */
public class LocationManagerTest {

    private LocationManager lm;

    @Before
    public void setUp() {
        lm = new LocationManager();
        lm.em = mock(EntityManager.class);
    }

    @After
    public void tearDown() {

    }

    @Test
    public void initializePlaceList() {
        lm.initializePlaceList();
        verify(lm.em, times(74071)).persist(any());
    }

}

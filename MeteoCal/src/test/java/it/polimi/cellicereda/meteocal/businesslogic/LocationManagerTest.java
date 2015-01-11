/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.cellicereda.meteocal.businesslogic;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;

/**
 *
 * @author stefano
 */
public class LocationManagerTest {

    private LocationManager lm;

    @Mock
    private Query query;

    @Before
    public void setUp() {
        lm = new LocationManager();
        lm.em = mock(EntityManager.class);
        query = mock(Query.class);

        Mockito.when(lm.em.createNamedQuery("Place.deleteAll")).thenReturn(query);
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.cellicereda.meteocal.businesslogic;

import it.polimi.cellicereda.meteocal.entities.Place;
import javax.persistence.EntityManager;
import org.junit.After;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Matchers.any;
import org.mockito.Mockito;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 *
 * @author stefano
 */
public class ForecastManagerTest {

    private ForecastManager fm;

    @Before
    public void setUp() {
        fm = new ForecastManager();
        fm.em = mock(EntityManager.class);
        fm.lm = mock(LocationManager.class);

        //    forecast.setPlace(lm.getPlaceByID(cityID));
        //create a place entity
        Place moscow = new Place();
        moscow.setId((long) 524901);
        moscow.setName("Moscow");
        moscow.setLongitude(37.615555);
        moscow.setLatitude(55.75222);

        //and return it when the forecast manager asks it to the location manager
        Mockito.when(fm.lm.getPlaceByID((long) 524901)).thenReturn(moscow);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void getNewForecastsByCityID() {
        fm.persistNewForecastsByCityID((long) 524901);
        verify(fm.em, atLeast(10)).persist(any());
    }

    @Test
    public void isGoodWeather() {
        assertTrue(fm.isGoodWeather(800));
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.cellicereda.meteocal.businesslogic;

import it.polimi.cellicereda.meteocal.entities.Event;
import it.polimi.cellicereda.meteocal.entities.Place;
import java.util.Date;
import javax.persistence.EntityManager;
import org.junit.After;
import org.junit.Assert;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;

/**
 *
 * @author stefano
 */
public class ForecastManagerTest {

    private ForecastManager fm;
    private Event event = new Event();

    @Before
    public void setUp() {
        fm = new ForecastManager();
        fm.em = mock(EntityManager.class);
        fm.lm = mock(LocationManager.class);
        fm.cm = new CalendarManager();

        //create a place entity
        Place honolulu = new Place();
        honolulu.setId((long) 5856195);
        honolulu.setName("Honolulu");
        honolulu.setLongitude(21.306940);
        honolulu.setLatitude(-157.858337);
        honolulu.setCountry("US");

        //and a related event
        event.setStartDate(new Date());
        event.setEndDate(new Date(new Date().getTime() + 1 * 60 * 60 * 1000));
        event.setEventLocation(honolulu);

        //and return it when the forecast manager asks it to the location manager
        Mockito.when(fm.lm.getPlaceByID((long) 5856195)).thenReturn(honolulu);

        Mockito.when(fm.em.find(Event.class, event)).thenReturn(event);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void DownloadNewForecastForEvent() {
        assertNotNull(fm.downloadNewForecastForEvent(event));

        //now try with an event in 6 days (16 days forecast related test, but not all weather has 16 days data - Honolulu only has seven days)
        Date newStart = event.getStartDate();
        newStart.setTime(newStart.getTime() + 6 * 24 * 60 * 60 * 1000);
        Date newEnd = new Date(newStart.getTime() + 10);

        event.setStartDate(newStart);
        event.setEndDate(newEnd);
        assertNotNull(fm.downloadNewForecastForEvent(event));
    }

    @Test
    public void searchGoodWeatherForEvent() {
        //this test could fail, but what the fuck we are in honolulu
        assertTrue(fm.searchGoodWeatherForEvent(event).size() > 0);
    }

    @Test
    public void isGoodWeather() {
        assertTrue(fm.isGoodWeather(800));
        assertTrue(fm.isGoodWeather(801));
        assertTrue(fm.isGoodWeather(802));
        assertTrue(fm.isGoodWeather(803));
        assertTrue(fm.isGoodWeather(804));

        assertFalse(fm.isGoodWeather(200));
        assertFalse(fm.isGoodWeather(232));
        assertFalse(fm.isGoodWeather(300));
        assertFalse(fm.isGoodWeather(321));
        assertFalse(fm.isGoodWeather(500));
        assertFalse(fm.isGoodWeather(511));
        assertFalse(fm.isGoodWeather(520));
        assertFalse(fm.isGoodWeather(531));
        assertFalse(fm.isGoodWeather(600));
        assertFalse(fm.isGoodWeather(622));
        assertFalse(fm.isGoodWeather(701));
        assertFalse(fm.isGoodWeather(781));
    }

    @Test
    public void getUrlOfWeatherIcon() {
        assertTrue(fm.getUrlOfWeatherIcon(800).contentEquals("http://openweathermap.org/img/w/01d.png"));
        assertTrue(fm.getUrlOfWeatherIcon(802).contentEquals("http://openweathermap.org/img/w/03d.png"));
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.cellicereda.meteocal.businesslogic;

import it.polimi.cellicereda.meteocal.entities.Event;
import it.polimi.cellicereda.meteocal.entities.Place;
import it.polimi.cellicereda.meteocal.entities.User;
import java.util.Date;
import javax.persistence.EntityManager;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 * @author stefano
 */
public class CalendarManagerTest {

    private CalendarManager cm = new CalendarManager();

    private Event event = new Event();
    private User creator = new User();
    private Date start = new Date();
    private Date end = new Date(start.getTime());
    private Place location = new Place();

    @Before
    public void setup() {
        location.setId((long) 1);
        event.setCreator(creator);
        event.setDescription("desc");
        event.setEndDate(end);
        event.setEventLocation(location);
        event.setId("id");
        event.setStartDate(start);
        event.setTitle("title");

        cm.nm = mock(NotificationManager.class);
        cm.em = mock(EntityManager.class);
        cm.fm = mock(ForecastManager.class);

        when(cm.em.find(Event.class, event.getId())).thenReturn(event);
        when(cm.em.find(Place.class, location.getId())).thenReturn(location);
    }

    @Test
    public void isTomorrow() {
        Event fakeEvent = new Event();

        fakeEvent.setStartDate(new Date());
        assertFalse(cm.isTomorrow(fakeEvent));

        Date tomorrow = new Date(new Date().getTime() + 24 * 60 * 60 * 1000);
        fakeEvent.setStartDate(tomorrow);
        assertTrue(cm.isTomorrow(fakeEvent));

        //try with tomorrow + 1 millisecond to get sure that we don't look for perfect time matching, please don't run this test at 12.59.59.999 p.m.
        tomorrow.setTime(tomorrow.getTime() + 1);
        fakeEvent.setStartDate(tomorrow);
        assertTrue(cm.isTomorrow(fakeEvent));

        Date twoDays = new Date(tomorrow.getTime() + 24 * 60 * 60 * 1000);
        fakeEvent.setStartDate(twoDays);
        assertFalse(cm.isTomorrow(fakeEvent));
    }

    @Test
    public void eventChange() {
        cm.changeEventAllDay(event, true);
        verify(cm.nm, times(0)).generateEventChangedNotifications(any());

        cm.changeEventDescription(event, "desc");
        verify(cm.nm, times(0)).generateEventChangedNotifications(any());
        cm.changeEventDescription(event, "description");
        verify(cm.nm, times(1)).generateEventChangedNotifications(any());

        cm.changeEventLocation(event, location);
        verify(cm.nm, times(1)).generateEventChangedNotifications(any());
        cm.changeEventLocation(event, new Place());
        verify(cm.nm, times(2)).generateEventChangedNotifications(any());
        cm.changeEventLocation(event, location);
        verify(cm.nm, times(3)).generateEventChangedNotifications(any());

        cm.changeEventTiming(event, start, end);
        verify(cm.nm, times(3)).generateEventChangedNotifications(any());
        Date d1 = new Date();
        cm.changeEventTiming(event, d1, end);
        verify(cm.nm, times(4)).generateEventChangedNotifications(any());
        cm.changeEventTiming(event, d1, new Date());
        verify(cm.nm, times(5)).generateEventChangedNotifications(any());

        cm.changeEventTitle(event, "title");
        verify(cm.nm, times(5)).generateEventChangedNotifications(any());
        cm.changeEventTitle(event, "newtitle");
        verify(cm.nm, times(6)).generateEventChangedNotifications(any());

    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.cellicereda.meteocal.businesslogic;

import it.polimi.cellicereda.meteocal.entities.Event;
import java.util.Date;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author stefano
 */
public class CalendarManagerTest {

    private CalendarManager cm = new CalendarManager();

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

}

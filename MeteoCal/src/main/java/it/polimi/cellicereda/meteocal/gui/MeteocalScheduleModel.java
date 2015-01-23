/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.cellicereda.meteocal.gui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

/**
 * This schedule model is equal to primafaces's default schedule model. Except
 * that it works
 *
 * @author stefano
 */
public class MeteocalScheduleModel implements ScheduleModel, Serializable {

    private List<ScheduleEvent> events;

    public MeteocalScheduleModel() {
        events = new ArrayList<>();
    }

    public MeteocalScheduleModel(List<ScheduleEvent> events) {
        this.events = events;
    }

    public void addEvent(ScheduleEvent event) {
        events.add(event);
    }

    public boolean deleteEvent(ScheduleEvent event) {
        return events.remove(event);
    }

    public List<ScheduleEvent> getEvents() {
        return events;
    }

    public ScheduleEvent getEvent(String id) {
        for (ScheduleEvent event : events) {
            if (event.getId().equals(id)) {
                return event;
            }
        }
        return null;
    }

    public void updateEvent(ScheduleEvent event) {
        int index = -1;

        for (int i = 0; i < events.size(); i++) {
            if (events.get(i).getId().equals(event.getId())) {
                index = i;

                break;
            }
        }

        if (index >= 0) {
            events.set(index, event);
        }
    }

    public int getEventCount() {
        return events.size();
    }

    public void clear() {
        events = new ArrayList<ScheduleEvent>();
    }

}

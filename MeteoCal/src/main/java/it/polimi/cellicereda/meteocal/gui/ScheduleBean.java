/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.cellicereda.meteocal.gui;

import it.polimi.cellicereda.meteocal.businesslogic.CalendarManager;
import it.polimi.cellicereda.meteocal.businesslogic.UserProfileManager;
import it.polimi.cellicereda.meteocal.entities.Event;
import it.polimi.cellicereda.meteocal.entities.User;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

/**
 * Backing bean for the schedule holding user events.
 * @author Andrea
 */
@ManagedBean
@ViewScoped
public class ScheduleBean implements Serializable{
    
    @EJB
    CalendarManager calendarManager;
    @EJB
    UserProfileManager userProfileManager;
   
    //this will contain the list of events to be displayed
    private ScheduleModel model;
    private User currentUser;
    private Event event=new Event();
    
    @PostConstruct
    public void init(){
        currentUser=userProfileManager.getLoggedUser();
        //find all the events in which the user will partecipate 
        model=new DefaultScheduleModel((List<ScheduleEvent>) calendarManager.getEventsByParticipant(currentUser));
    }        
    
    public ScheduleModel getModel() {
        return model; 
    }
    
    public Event getEvent(){
        return event;
    }
    
    public void setEvent(Event event){
        this.event = event; 
    }
    
    public void addEvent() {
        if(event.getId() == null)
             model.addEvent(event);
       else
             model.updateEvent(event);
       //reset the dialog form
       event = new Event();
    }
       
    public void onEventSelect(SelectEvent e) {
       event = (Event) (ScheduleEvent) e.getObject();
    }
    
     public void onDateSelect(SelectEvent e) {
             Date date = (Date) e.getObject();
             event = new Event();
             event.setStartingDate(date);
    }
}

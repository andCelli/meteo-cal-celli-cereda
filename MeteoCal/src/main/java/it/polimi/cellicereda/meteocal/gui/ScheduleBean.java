/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.cellicereda.meteocal.gui;

import static com.sun.corba.se.impl.util.Utility.printStackTrace;
import it.polimi.cellicereda.meteocal.businesslogic.CalendarManager;
import it.polimi.cellicereda.meteocal.businesslogic.UserProfileManager;
import it.polimi.cellicereda.meteocal.entities.Event;
import it.polimi.cellicereda.meteocal.entities.Place;
import it.polimi.cellicereda.meteocal.entities.User;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
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
@Named
public class ScheduleBean implements Serializable{
    
    @EJB
    CalendarManager calendarManager;
    @EJB
    UserProfileManager userProfileManager;
 
    //this will contain the list of events to be displayed
    private ScheduleModel model;
    private User currentUser;
    private Event event=new Event();
  
    /*
    states whether the user is the creator or a partecipant 
    the buttons in the detail dialog will be rendered accordingly to this flag
    (a partecipant can't modify the event)
    normally is true, the modification of an event makes it false
    */
    private boolean isCreator;
    private boolean newEvent;
    
    @PostConstruct
    public void init(){
        try{
          currentUser=userProfileManager.getLoggedUser();
          //find all the events in which the user will partecipate 
          model=new DefaultScheduleModel((List<ScheduleEvent>) calendarManager.getEventsByParticipant(currentUser));
          model=new DefaultScheduleModel((List<ScheduleEvent>) calendarManager.getEventsByCreator(currentUser));
        }catch(Exception e){
           printStackTrace();
           System.err.println("Problems durig init of scheduleBean"); 
        }
    }        
    
    public ScheduleModel getModel() {
        return model; 
    }
    public void setModel(ScheduleModel model){
        this.model=model;
    }
    public Event getEvent(){
        return event;
    }
    public void setEvent(Event event){
        this.event = event; 
    }
    private void addMessage(FacesMessage message) {
        FacesContext.getCurrentInstance().addMessage(null, message);
    } 
    
    /*
    This methods gets the selected event 
    */
    public void onEventSelect(SelectEvent e) {
       setEvent((Event) (ScheduleEvent) e.getObject());
    } 
    /*
    This method deletes the selected event
    */
    public void delete(){
        model.deleteEvent(getEvent());
        calendarManager.delete(getEvent());
        setEvent(new Event());
    }
    
    /**
     * This method set the newEvent to false when the user decides to modify
     * an existing event. 
     * 
     * Called by the modify button
     */
    public void modify(){
        setNewEvent(false);
    }

    /**
     * @return the newEvent
     */
    public boolean isNewEvent() {
        return newEvent;
    }

    /**
     * @param newEvent the newEvent to set
     */
    public void setNewEvent(boolean newEvent) {
        this.newEvent = newEvent;
    }

    
}

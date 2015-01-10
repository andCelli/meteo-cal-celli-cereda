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
    these are the attributes that are used to decouple the gui from the entities
    we need only the attributes to be displayed and set by the user
    */
    private String title;
    private String description;
    private Date startingDate;
    private Date endingDate;
    private String location;
    private boolean isPublic;
    private boolean allDay;
    
    //states whether a user is creating a new event or modifying an existing one
    private boolean newEvent;
    /*
    states whether the user is the creator or a partecipant 
    the buttons in the detail dialog will be rendered accordingly to this flag
    (a partecipant can't modify the event)
    normally is true, the modification of an event makes it false
    */
    private boolean isCreator;
    
    @PostConstruct
    public void init(){
        setNewEvent(true);
        setTitle(new String());
        setDescription(new String());
        setStartingDate(new Date());
        setEndingDate(new Date());
        setLocation(new String());
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
    This method checks whether the user is modifying an existing event or he's creating
    a new one. In the first case the method updates event info. In the second it creates
    a new event instance in the db.
    */
    public void saveEvent(){
        //check
        System.out.println("the newEvent flag is: "+newEvent);
        if(newEvent){
           try{
              //set info (va bene farlo prima che venga aggiunto??)
              calendarManager.changeEventTitle(event, getTitle());
              System.out.println("the title is: "+getTitle());
              calendarManager.changeEventDescription(event, getDescription());
              calendarManager.changeEventTiming(event, getStartingDate(), getEndingDate());
              System.out.println("the start date is: "+ getStartingDate().toString());
              //correggere
              calendarManager.changeEventLocation(event, null);
              event.setPublicEvent(isIsPublic());
              event.setIsAllDay(isAllDay());
              event.setCreator(currentUser);
           }catch(Exception e){
                printStackTrace();
                System.err.println("Problems while updating the event");
           }
            try{
                calendarManager.save(event);
            }catch(Exception e){
                printStackTrace();
                System.err.println("Problems while saving the event");
            }
        }else{
            //@TODO refactor
            calendarManager.changeEventTitle(event, getTitle());
            calendarManager.changeEventDescription(event, getDescription());
            calendarManager.changeEventTiming(event, getStartingDate(), getEndingDate());
            //correggere
            calendarManager.changeEventLocation(event, new Place());
            event.setPublicEvent(isIsPublic());
            event.setIsAllDay(isAllDay());
        }
        setNewEvent(true);
        model.addEvent(event);
        event=new Event();
    }
    
    /*
    This methods gets the selected event 
    */
    public void onEventSelect(SelectEvent e) {
       event = (Event) (ScheduleEvent) e.getObject();
    } 
    /*
    This method deletes the selected event
    */
    public void delete(){
        model.deleteEvent(event);
        calendarManager.delete(event);
        event=new Event();
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

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the startingDate
     */
    public Date getStartingDate() {
        return startingDate;
    }

    /**
     * @param startingDate the startingDate to set
     */
    public void setStartingDate(Date startingDate) {
        this.startingDate = startingDate;
    }

    /**
     * @return the endingDate
     */
    public Date getEndingDate() {
        return endingDate;
    }

    /**
     * @param endingDate the endingDate to set
     */
    public void setEndingDate(Date endingDate) {
        this.endingDate = endingDate;
    }

    /**
     * @return the location
     */
    public String getLocation() {
        return location;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * @return the isPublic
     */
    public boolean isIsPublic() {
        return isPublic;
    }

    /**
     * @param isPublic the isPublic to set
     */
    public void setIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    /**
     * @return the allDay
     */
    public boolean isAllDay() {
        return allDay;
    }

    /**
     * @param allDay the allDay to set
     */
    public void setAllDay(boolean allDay) {
        this.allDay = allDay;
    }
}

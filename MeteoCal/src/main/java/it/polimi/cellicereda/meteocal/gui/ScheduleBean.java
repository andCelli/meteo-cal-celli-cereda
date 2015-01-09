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
    
    //this flag determines whether the modification form is displayed or not
    private boolean modifyForm;
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
    
    
    @PostConstruct
    public void init(){   
        modifyForm=false;
        currentUser=userProfileManager.getLoggedUser();
        //find all the events in which the user will partecipate 
        model=new DefaultScheduleModel((List<ScheduleEvent>) calendarManager.getEventsByParticipant(currentUser));
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
        if(event.getTitle().equals(null)){
            //set info (va bene farlo prima che venga aggiunto??)
            calendarManager.changeEventTitle(event, title);
            calendarManager.changeEventDescription(event, description);
            calendarManager.changeEventTiming(event, startingDate, endingDate);
            //correggere
            calendarManager.changeEventLocation(event, startingDate);
            event.setPublicEvent(isPublic);
            event.setIsAllDay(allDay);
            event.setCreator(currentUser);
            calendarManager.save(event);
        }else{
            //@TODO refactor
            calendarManager.changeEventTitle(event, title);
            calendarManager.changeEventDescription(event, description);
            calendarManager.changeEventTiming(event, startingDate, endingDate);
            //correggere
            calendarManager.changeEventLocation(event, startingDate);
            event.setPublicEvent(isPublic);
            event.setIsAllDay(allDay);
        }
        model.addEvent(event);
        modifyForm=false;
        event=new Event();
    }
    
    
    
    
    
    
    
    
    public String getTitle(){
        return title;
    }
    public void setTitle(String title){
        this.title=title;
    }
    public String getDescription(){
        return description;
    }
    public void setDescription(String description){
        this.description=description;
    }public Date getStartingDate(){
        return startingDate;
    }
    public void setStartingDate(Date startingDate){
        this.startingDate=startingDate;
    }public Date getEndingDate(){
        return endingDate;
    }
    public void setEndingDate(Date endingDate){
        this.endingDate=endingDate;
    }public String getLocation(){
        return location;
    }
    public void setLocation(String location){
        this.location=location;
    }public boolean getIsPublic(){
        return isPublic;
    }
    public void setIsPublic(boolean isPublic){
        this.isPublic=isPublic;
    }
    public boolean getAllDay(){
        return allDay;
    }
    public void setAllDay(boolean allDay){
        this.allDay=allDay;
    }
    public String getModifyForm(){
        if(modifyForm)
            return "true";
        return "false";
    }
    //set the reder of the form to true
    public void setRenderModifyForm(){
        modifyForm=true;
    }
    /*public void addEvent(){
        //check if the model already contains the event
        //if(modelContains(event)){
          //   model.addEvent(event);
        //}
       //else
         //    model.updateEvent(event);
       event.setCreator(currentUser);
       System.out.println(event.toString());
       model.addEvent(event);
       calendarManager.save(event);
       //reset the dialog form   
       event = new Event();
    }
     
    //check if the model already contains the event
    private boolean modelContains(Event e1){
        for (ScheduleEvent e2: this.model.getEvents()){
            if(e1.equals(e2)){
                return true;
            }
        }
        return false;
    }
    
    public void onEventSelect(SelectEvent e) {
       event = (Event) (ScheduleEvent) e.getObject();
    }
    
     public void onDateSelect(SelectEvent e) {
             Date date = (Date) e.getObject();
             event = new Event();
             //event.setStartingDate(date);
    }
    
    /* //aggiungere l'aggiornamento dell'evento nel db
     public void onEventMove(ScheduleEntryMoveEvent event) {  
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event moved", 
                "Day delta: " + event.getDayDelta() + ", Minute delta: " + event.getMinuteDelta());
         
        addMessage(message);
    }
     //aggiungere l'aggiornamento dell'evento nel db
    public void onEventResize(ScheduleEntryResizeEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Event resized", "Day delta:" + event.getDayDelta() + ", Minute delta:" + event.getMinuteDelta());
         
        addMessage(message);
    } */
}

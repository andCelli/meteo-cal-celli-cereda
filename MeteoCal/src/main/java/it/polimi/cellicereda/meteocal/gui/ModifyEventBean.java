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
import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * This bean manages the modification of an event 
 * @author Andrea
 */

@SessionScoped
@Named
public class ModifyEventBean implements Serializable{
    
    @EJB
    CalendarManager calendarManager;
    
    @EJB
    UserProfileManager userProfileManager;
    
    @Inject
    ScheduleBean scheduleBean;
    
    private User currentUser;
    
    private Event event=new Event();
    
    private FacesContext context;
    
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
    
    @PostConstruct
    public void init(){
        setNewEvent(true);
        setTitle(new String());
        setDescription(new String());
        setStartingDate(new Date());
        setEndingDate(new Date());
        setLocation(new String());
        currentUser=userProfileManager.getLoggedUser();
        System.out.println("Parto: ModifyEvent");
        try{
        scheduleBean.getDetailsEventBean().setModifyEventBean(this);
            System.out.println("ho settato modifyBean in DetailsBean");
        }catch(Exception e){
            printStackTrace();
            System.err.println("error while retrieving detailsEventBean in the init of ModifyEventBean");
        }
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
              calendarManager.changeEventTitle(getEvent(), getTitle());
              System.out.println("the title is: "+getTitle());
              calendarManager.changeEventDescription(getEvent(), getDescription());
              calendarManager.changeEventTiming(getEvent(), getStartingDate(), getEndingDate());
              System.out.println("the start date is: "+ getStartingDate().toString());
              //correggere
              calendarManager.changeEventLocation(getEvent(), null);
                getEvent().setPublicEvent(isIsPublic());
                getEvent().setIsAllDay(isAllDay());
                getEvent().setCreator(userProfileManager.getByEmail(currentUser.getEmail()));
           }catch(Exception e){
                printStackTrace();
                System.err.println("Problems while updating the event");
           }
            try{
                System.out.println("user id: "+getEvent().getCreator().getEmail());
                calendarManager.save(getEvent());
            }catch(Exception e){
                printStackTrace();
                System.err.println("Problems while saving the event");
            }
        }else{
            //@TODO refactor
            calendarManager.changeEventTitle(getEvent(), getTitle());
            calendarManager.changeEventDescription(getEvent(), getDescription());
            calendarManager.changeEventTiming(getEvent(), getStartingDate(), getEndingDate());
            //correggere
            calendarManager.changeEventLocation(getEvent(), new Place());
            getEvent().setPublicEvent(isIsPublic());
            getEvent().setIsAllDay(isAllDay());
        }
        setNewEvent(true);
        try{
            context=FacesContext.getCurrentInstance();           
            //scheduleBean=(ScheduleBean) context.getApplication().evaluateExpressionGet(context, "#{scheduleBean}", ScheduleBean.class);
            scheduleBean.getModel().addEvent(getEvent());
        }catch(Exception e){
            System.err.println("Error while trying to retrieve the schedule bean in the modifyEventBean");
        }
        setEvent(new Event());
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

    /**
     * @return the event
     */
    public Event getEvent() {
        return event;
    }

    /**
     * @param event the event to set
     */
    public void setEvent(Event event) {
        this.event = event;
    }
}

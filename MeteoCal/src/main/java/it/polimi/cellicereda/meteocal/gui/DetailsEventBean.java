/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.cellicereda.meteocal.gui;

import it.polimi.cellicereda.meteocal.businesslogic.CalendarManager;
import it.polimi.cellicereda.meteocal.businesslogic.UserProfileManager;
import it.polimi.cellicereda.meteocal.entities.Event;
import java.io.Serializable;
import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.view.facelets.FaceletContext;
import javax.inject.Named;

/**
 * This bean manages the vizualization of an event's details
 * @author Andrea
 */
@ManagedBean
@SessionScoped
@Named
public class DetailsEventBean implements Serializable {
    
    //this flag states if the user is the creator of the selected event
    //it's set in the onEventSelect method in the ScheduleBean
    //Used to choose which buttons to be rendered
    private boolean isCreator;
    private Event event;
    private ScheduleBean scheduleBean;
    private FacesContext context;
    private ModifyEventBean modifyEventBean;
    
    @EJB
    private CalendarManager calendarManager;
    @EJB
    private UserProfileManager userProfileManager;
    
    @PostConstruct
    public void init(){
        event=new Event();
        System.out.println("Parto: DetailsEventBean");
        try{
             context=FacesContext.getCurrentInstance();           
             scheduleBean=(ScheduleBean) context.getApplication().evaluateExpressionGet(context, "#{scheduleBean}", ScheduleBean.class);
             modifyEventBean=(ModifyEventBean)context.getApplication().evaluateExpressionGet(context, "#{modifyEventBean}", ModifyEventBean.class);
        }catch(Exception e){
            System.err.println("error in the DetailsEventBean init while retrieving the ScheduleBean and ModifyEventBean");
        }
    }

    /**
     * remove from the list of displayed event (in scheduleBean)
     * remove from db
     */
    public void delete(){
        scheduleBean.getModel().deleteEvent(event);
        calendarManager.delete(event);
    }
    
    /**
     * the user removes himself from the partecipants
     * @TODO
     */
    public void removeFromPartecipants(){
       scheduleBean.getModel().deleteEvent(event);
       /**
        * @TODO rimuovere dalla lista dell'utente
        */
    }
    
    /**
     * This method is called when the user decides to modify an event
     * it calls the ModifySettingBean setting the event and the modification flag
     * (the flag states that the event is not a new one)
     */
    public void modify(){
        modifyEventBean.setEvent(event);
        modifyEventBean.setNewEvent(false);
        //set the ModifyEventBean vars
        modifyEventBean.setTitle(event.getTitle());
        modifyEventBean.setDescription(event.getDescription());
        modifyEventBean.setStartingDate(event.getStartDate());
        modifyEventBean.setEndingDate(event.getEndDate());
        modifyEventBean.setLocation(event.getEventLocation().toString());
        modifyEventBean.setIsPublic(event.getPublicEvent());
        modifyEventBean.setAllDay(event.isAllDay());
    }
    
    /**
     * @return the isCreator
     */
    public boolean isIsCreator() {
        return isCreator;
    }

    /**
     * @param isCreator the isCreator to set
     */
    public void setIsCreator(boolean isCreator) {
        this.isCreator = isCreator;
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

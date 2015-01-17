/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.cellicereda.meteocal.gui;

import static com.sun.corba.se.impl.util.Utility.printStackTrace;
import it.polimi.cellicereda.meteocal.businesslogic.CalendarManager;
import it.polimi.cellicereda.meteocal.businesslogic.ForecastManager;
import it.polimi.cellicereda.meteocal.businesslogic.UserProfileManager;
import it.polimi.cellicereda.meteocal.entities.Event;
import it.polimi.cellicereda.meteocal.entities.User;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 * This bean manages the vizualization of an event's details
 * @author Andrea
 */

@SessionScoped
@Named
public class DetailsEventBean implements Serializable {
    
    //this flag states if the user is the creator of the selected event
    //it's set in the onEventSelect method in the ScheduleBean
    //Used to choose which buttons to be rendered
    private boolean isCreator;
    private Event event;
    
    private ScheduleBean scheduleBean;
    
    private ModifyEventBean modifyEventBean;
    
    private List<User> partecipants;
    
    @EJB
    private CalendarManager calendarManager;
    @EJB
    private UserProfileManager userProfileManager;
    @EJB
    private ForecastManager fm;
    
    @PostConstruct
    public void init(){
        event=new Event();
        System.out.println("Parto: DetailsEventBean");
    }

    /**
     * remove from the list of displayed event (in scheduleBean)
     * remove from db
     */
    public void delete(){
        getScheduleBean().getModel().deleteEvent(event);
        calendarManager.delete(event);
    }
    
    /**
     * the user removes himself from the partecipants
     * @TODO
     */
    public void removeFromPartecipants(){
        getScheduleBean().getModel().deleteEvent(event);
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
        try{
            System.out.println("sono modify()");
            getModifyEventBean().setEvent(event);         
            getModifyEventBean().setNewEvent(false); 
        //set the ModifyEventBean vars
            getModifyEventBean().setTitle(event.getTitle());          
            getModifyEventBean().setDescription(event.getDescription());
            getModifyEventBean().setStartingDate(event.getStartDate());
            getModifyEventBean().setEndingDate(event.getEndDate());
            getModifyEventBean().setLocationKey(null);
            getModifyEventBean().setIsPublic(event.getPublicEvent());
            getModifyEventBean().setAllDay(event.isAllDay());
        //set the partecipants list    
            getModifyEventBean().setInvitedUsers(partecipants);
        }catch(Exception e){
            e.printStackTrace();
            System.err.println("Error in modify (DetailsEventBean)");
        }
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

    /**
     * @return the scheduleBean
     */
    public ScheduleBean getScheduleBean() {
        return scheduleBean;
    }

    /**
     * @param scheduleBean the scheduleBean to set
     */
    public void setScheduleBean(ScheduleBean scheduleBean) {
        this.scheduleBean = scheduleBean;
    }

    /**
     * @return the modifyEventBean
     */
    public ModifyEventBean getModifyEventBean() {
        return modifyEventBean;
    }

    /**
     * @param modifyEventBean the modifyEventBean to set
     */
    public void setModifyEventBean(ModifyEventBean modifyEventBean) {
        this.modifyEventBean = modifyEventBean;
    }

    /**
     * @return the partecipants
     */
    public List<User> getPartecipants() {
        return partecipants;
    }

    /**
     * @param partecipants the partecipants to set
     */
    public void setPartecipants(List<User> partecipants) {
        this.partecipants = partecipants;
    }
    
    /**
     * get the weather icon related to the selected event forecast
     * @return the url of the weather icon
     */
    public String getForecastIcon(){
        try{
         return fm.getUrlOfWeatherIcon(fm.getWeatherForEvent(event)); 
        }catch(Exception e){
            System.err.println("error while retrieving the icon");
            printStackTrace();
        }
        return null;
    }
    /**
     * 
     * @return true if the event has a forecast 
     */
    public boolean hasPrediction(){
        try{
      if(event.getEventLocation()!=null){
        if(fm.getWeatherForEvent(event)!=0)
            return true;
            }
        }catch(Exception e){
            System.err.println("error while checking if the event has a prediction");
            printStackTrace();
        }
        return false;
    }
}

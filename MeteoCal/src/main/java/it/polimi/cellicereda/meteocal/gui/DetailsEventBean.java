/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.cellicereda.meteocal.gui;

import it.polimi.cellicereda.meteocal.entities.Event;
import java.io.Serializable;
import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
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
    
    @PostConstruct
    public void init(){
        event=new Event();
    }

    /**
     * 
     */
    public void delete(){
        
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

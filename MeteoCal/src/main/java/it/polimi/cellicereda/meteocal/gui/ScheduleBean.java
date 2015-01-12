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
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
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

@SessionScoped
@Named
public class ScheduleBean implements Serializable{
    
    @EJB
    CalendarManager calendarManager;
    @EJB
    UserProfileManager userProfileManager;
    @Inject
    private DetailsEventBean detailsEventBean;

    //this will contain the list of events to be displayed
    private ScheduleModel model;
    private User currentUser;
  
  
    /*
    states whether the user is the creator or a partecipant 
    the buttons in the detail dialog will be rendered accordingly to this flag
    (a partecipant can't modify the event)
    normally is true, the modification of an event makes it false
    */
    private boolean isCreator;
   
    private FacesContext context;
    
    @PostConstruct
    public void init(){
        System.out.println("Parto: ScheduleBean");
        try{
          currentUser=userProfileManager.getLoggedUser();
          //find all the events in which the user will partecipate 
          //created events
          model=new DefaultScheduleModel((List<ScheduleEvent>) calendarManager.getEventsByCreator(currentUser));
          //partecipating events
          for(Event e:(List<Event>)calendarManager.getEventsByParticipant(currentUser))
              model.addEvent(e);
          
          
          detailsEventBean.setScheduleBean(this);
          
          
        }catch(Exception e){
           printStackTrace();
           System.err.println("Problems durig init of scheduleBean"); 
        }
        try{
             context=FacesContext.getCurrentInstance();           
             //detailsEventBean=(DetailsEventBean) context.getApplication().evaluateExpressionGet(context, "#{detailsEventBean}", DetailsEventBean.class);
        }catch(Exception e){
            System.err.println("error in the ScheduleBean init while retrieving the DetailsEventBean");
        }
    }        
    
    public ScheduleModel getModel() {
        return model; 
    }
    public void setModel(ScheduleModel model){
        this.model=model;
    }
   
    private void addMessage(FacesMessage message) {
        FacesContext.getCurrentInstance().addMessage(null, message);
    } 
    
    /**
     * This method get the selected event, sets the creator flag in the details bean
     * and set to the selected event the details 
     * @param SelectEvent
     */
    public void onEventSelect(SelectEvent e) {
       Event event=(Event)e.getObject();
        getDetailsEventBean().setEvent(event);
        getDetailsEventBean().setIsCreator(event.getCreator().equals(currentUser));
       } 

    /**
     * @return the detailsEventBean
     */
    public DetailsEventBean getDetailsEventBean() {
        return detailsEventBean;
    }
}

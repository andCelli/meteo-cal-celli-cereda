/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.cellicereda.meteocal.gui;

import static com.sun.corba.se.impl.util.Utility.printStackTrace;
import com.sun.media.jai.opimage.ConvolveRIF;
import it.polimi.cellicereda.meteocal.businesslogic.CalendarManager;
import it.polimi.cellicereda.meteocal.businesslogic.UserProfileManager;
import it.polimi.cellicereda.meteocal.entities.Event;
import it.polimi.cellicereda.meteocal.entities.User;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIViewParameter;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewMetadata;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

/**
 * This bean manages the visualization of other user's calendars according to their
 * own privacy settings
 * 
 * @author Andrea
 */
@SessionScoped
@Named

public class ExternalCalendarBean implements Serializable{
    @EJB
    private CalendarManager cm;
    @EJB
    private UserProfileManager upm;
    
    private User user;
    private ScheduleModel model;
    private Event selectedEvent;
    private FacesContext context;
    
    @PostConstruct
    public void init(){
        try{
        context=FacesContext.getCurrentInstance();           
        ResultsBean rb=(ResultsBean) context.getApplication().evaluateExpressionGet(context, "#{resultsBean}", ResultsBean.class);
        user=rb.getSelectedUser();
        
        /*Map<String,String> requestParams = context.getExternalContext().getRequestParameterMap();
        user=upm.getByEmail(requestParams.get("email"));*/
        
        /*HttpServletRequest servletRequest = (HttpServletRequest) context.getCurrentInstance().getExternalContext().getRequest();    
        user=upm.getByEmail(servletRequest.getParameter("email"));*/
        
        }catch(Exception e){
            System.err.println("error while retrieving the mail");
            printStackTrace();
        }
                
        try{
           model=new DefaultScheduleModel((List<ScheduleEvent>) cm.getEventsByCreator(user));
           if(cm.getEventsByParticipant(user).size()>0){
           for(Event e:(List<Event>)cm.getEventsByParticipant(user)){
              model.addEvent(e);
          }
         }
        }catch(Exception e){
            System.err.println("error while adding to the model. The user is "+user.toString());
            printStackTrace();
        }
    }

    /**
     * @return the user
     */
    public User getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
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
        setSelectedEvent((Event)e.getObject()); 
       }

    /**
     * @return the selectedEvent
     */
    public Event getSelectedEvent() {
        return selectedEvent;
    }

    /**
     * @param selectedEvent the selectedEvent to set
     */
    public void setSelectedEvent(Event selectedEvent) {
        this.selectedEvent = selectedEvent;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.cellicereda.meteocal.gui;

import it.polimi.cellicereda.meteocal.businesslogic.CalendarManager;
import it.polimi.cellicereda.meteocal.businesslogic.UserProfileManager;
import it.polimi.cellicereda.meteocal.entities.User;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ViewScoped;
import javax.inject.Named;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

/**
 * Backing bean for the schedule holding user events.
 * @author Andrea
 */
@Named
//quale conviene mettere?
@ViewScoped
public class ScheduleBean {
    
    @EJB
    CalendarManager calendarManager;
    @EJB
    UserProfileManager userProfileManager;
    
    //this will contain the list of events to be displayed
    private ScheduleModel model;
    private User currentUser;
    
    public ScheduleBean(){
        currentUser=userProfileManager.getLoggedUser();

        model=new DefaultScheduleModel((List<ScheduleEvent>) calendarManager.getEventsByParticipant(currentUser));
    }        
     
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.cellicereda.meteocal.gui;

import static com.sun.corba.se.impl.util.Utility.printStackTrace;
import it.polimi.cellicereda.meteocal.businesslogic.NotificationManager;
import it.polimi.cellicereda.meteocal.businesslogic.UserProfileManager;
import it.polimi.cellicereda.meteocal.entities.Notification;
import static it.polimi.cellicereda.meteocal.entities.NotificationType.EVENT_CHANGED;
import static it.polimi.cellicereda.meteocal.entities.NotificationType.EVENT_INVITE;
import static it.polimi.cellicereda.meteocal.entities.NotificationType.SUNNY_DAY_PROPOSAL;
import it.polimi.cellicereda.meteocal.entities.User;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 * This bean manages the visualization of notifications and answers to them
 * @author Andrea
 */
@SessionScoped
@Named
public class NotificationBean implements Serializable{
    
    @EJB
    private UserProfileManager upm;
    @EJB
    private NotificationManager nm;
    @EJB
    private Utility util;
    
    private User user;
    
    private List<Notification> notifications;
    
    private Notification selectedNotification;
    
    //strings used to display dates 
    private String start;
    private String end;
    
    @PostConstruct
    public void init(){
        System.out.println("notification");
       setUser(upm.getLoggedUser());
        try{
        setNotifications(nm.getPendingFutureNotificationForUser(getUser()));
        }catch(Exception e){
            System.err.println("error while retrieving the notification list. User: "+user.getEmail());
            printStackTrace();
        }
    }
    
    /**
     * This method it's called for the update of the displayed notifications
     */
    public void update(){
       try{
           List<Notification> prova=nm.getPendingFutureNotificationForUser(user);
            for(Notification n:nm.getPendingNotificationForUser(user)){
               if(!notifications.contains(n)){
                   notifications.add(n);
               }
           }
            
       }catch(Exception e){
            System.err.println("Error while updating notification for user: "+user.getEmail());
            printStackTrace();
        }
    }

    /**
     * positive answer/OK
     */
    public void positiveAnswer(){
        if(selectedNotification.getNotificationType().equals(SUNNY_DAY_PROPOSAL)){
            //@TODO
        }else{
            if(selectedNotification.getNotificationType().equals(EVENT_INVITE)){
                nm.answerToAnInvite(selectedNotification, Boolean.TRUE);
                notifications.remove(selectedNotification);
            }            
        }
        nm.readNotification(selectedNotification);
        notifications.remove(selectedNotification);
    }
    
    /**
     * negative answer 
     */
    public void negativeAnswer(){
        if(selectedNotification.getNotificationType().equals(SUNNY_DAY_PROPOSAL)){
            //@TODO
        }else{
            if(selectedNotification.getNotificationType().equals(EVENT_INVITE)){
                nm.answerToAnInvite(selectedNotification, Boolean.FALSE);
                notifications.remove(selectedNotification);
            }            
        }
        nm.readNotification(selectedNotification);
        notifications.remove(selectedNotification);
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

    /**
     * @return the notifications
     */
    public List<Notification> getNotifications() {
        return notifications;
    }

    /**
     * @param notifications the notifications to set
     */
    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    /**
     * @return the selectedNotification
     */
    public Notification getSelectedNotification() {
        return selectedNotification;
    }

    /**
     * @param selectedNotification the selectedNotification to set
     */
    public void setSelectedNotification(Notification selectedNotification) {
        this.selectedNotification = selectedNotification;
    }

    /**
     * @return the start
     */
    public String getStart() {
        return start;
    }

    /**
     * @param start the start to set
     */
    public void setStart(String start) {
        this.start = start;
    }

    /**
     * @return the end
     */
    public String getEnd() {
        return end;
    }

    /**
     * @param end the end to set
     */
    public void setEnd(String end) {
        this.end = end;
    }
    
    /**
     * sets the strings that display start and end of the event reletate to the selectedNotification
     */
    public void setStartAndEnd(){
        setStart(util.getFormattedDate(selectedNotification.getReferredEvent().getStartDate()));
        setEnd(util.getFormattedDate(selectedNotification.getReferredEvent().getEndDate()));
    }
}

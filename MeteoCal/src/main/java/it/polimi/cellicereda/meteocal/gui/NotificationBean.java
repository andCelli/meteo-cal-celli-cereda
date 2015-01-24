/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.cellicereda.meteocal.gui;

import static com.sun.corba.se.impl.util.Utility.printStackTrace;
import it.polimi.cellicereda.meteocal.businesslogic.ForecastManager;
import it.polimi.cellicereda.meteocal.businesslogic.NotificationManager;
import it.polimi.cellicereda.meteocal.businesslogic.UserProfileManager;
import it.polimi.cellicereda.meteocal.entities.Forecast;
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
    @EJB
    private ForecastManager fm;
    
    private User user;
    
    private List<Notification> notifications;
    
    private Notification selectedNotification;
    
    private boolean hasGoodDayFlag;
    private List<Forecast> goodDates;
    
    //strings used to display dates 
    private String start;
    private String end;
    
    @PostConstruct
    public void init(){
       System.out.println("notification");
       setUser(upm.getLoggedUser());
        setHasGoodDayFlag(false);
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
            nm.answerToASunnyDayProposal(selectedNotification, Boolean.TRUE,goodDates.get(0).getStartingValidity());
            setHasGoodDayFlag(false);
        }else{
            if(selectedNotification.getNotificationType().equals(EVENT_INVITE)){
                nm.answerToAnInvite(selectedNotification, Boolean.TRUE);  
            }            
        }
        readed();
    }
    
    /**
     * negative answer 
     */
    public void negativeAnswer(){
        if(selectedNotification.getNotificationType().equals(SUNNY_DAY_PROPOSAL)){
            nm.answerToASunnyDayProposal(selectedNotification, Boolean.FALSE, null);
        }else{
            if(selectedNotification.getNotificationType().equals(EVENT_INVITE)){
                nm.answerToAnInvite(selectedNotification, Boolean.FALSE);
            }            
        }
        readed();
    }
    
    /**
     * the user has read the notification (changed event and bad weather alert)
     */
     public void readed(){
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
    
    /**
     * @return the string representing the next available day with good weather (or tells that
     * no one was found)
     * 
     * this method also sets the hasGoodDay flag used to decide what to render in the notification's dialog
     */
    public String nextGoodDay(){
       try{
       goodDates=fm.searchGoodWeatherForEvent(selectedNotification.getReferredEvent());
       }catch(Exception e){
           System.err.println("error while searching good days");
       }
       if(!goodDates.isEmpty()){
           //there's a day with good weather
            setHasGoodDayFlag(true);
           return util.getFormattedDate(goodDates.get(0).getStartingValidity());
       }
       return "No day with good weather :(";
    }

    /**
     * @return the hasGoodDayFlag
     */
    public boolean isHasGoodDayFlag() {
        return hasGoodDayFlag;
    }

    /**
     * @param hasGoodDayFlag the hasGoodDayFlag to set
     */
    public void setHasGoodDayFlag(boolean hasGoodDayFlag) {
        this.hasGoodDayFlag = hasGoodDayFlag;
    }
    
    /**
     * check if there are notifications that are displayed but not present in the db (it happens when an event is deleted).
     * If so it removes them from the list of displayed notifications
     */
    public void checkForDeletedNotifications(){
        for(Notification n:notifications){
            if(!nm.getPendingFutureNotificationForUser(user).contains(n)){
                notifications.remove(n);
            }
        }
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.cellicereda.meteocal.gui;

import static com.sun.corba.se.impl.util.Utility.printStackTrace;
import it.polimi.cellicereda.meteocal.businesslogic.CalendarManager;
import it.polimi.cellicereda.meteocal.businesslogic.LocationManager;
import it.polimi.cellicereda.meteocal.businesslogic.NotificationManager;
import it.polimi.cellicereda.meteocal.businesslogic.UserProfileManager;
import it.polimi.cellicereda.meteocal.entities.Event;
import it.polimi.cellicereda.meteocal.entities.Place;
import it.polimi.cellicereda.meteocal.entities.User;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolationException;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.ScheduleEvent;

/**
 * This bean manages the modification of an event
 *
 * @author Andrea
 */
@SessionScoped
@Named
public class ModifyEventBean implements Serializable {

    @EJB
    CalendarManager calendarManager;

    @EJB
    UserProfileManager userProfileManager;

    @EJB
    private LocationManager lm;
    
    @EJB
    private UtilityMethods utility;

    @Inject
    ScheduleBean scheduleBean;
   
    @EJB
    private NotificationManager notificationManager;

    private User currentUser;

    private Event event = new Event();
    
    private User removedFromParticipants;

    /*
     these are the attributes that are used to decouple the gui from the entities
     we need only the attributes to be displayed and set by the user
     */
    private String title;
    private String description;
    private Date startingDate;
    private Date endingDate;
    private String locationKey;
    private Place place;
    private boolean isPublic;
    private boolean allDay;
    private List<User> invitedUsers;
    private List<String> places;

    //states whether a user is creating a new event or modifying an existing one
    private boolean newEvent;

    @PostConstruct
    public void init() {
        resetUtilityVariables();
        currentUser = userProfileManager.getLoggedUser();     
        try {
            scheduleBean.getDetailsEventBean().setModifyEventBean(this);
        } catch (Exception e) {
            Logger.getLogger(ModifyEventBean.class.getName()).
                    log(Level.SEVERE, "Error while retrieving detailsEventBean in the init of ModifyEventBean", e);
        }
    }
    /*
     This method checks whether the user is modifying an existing event or he's creating
     a new one. In the first case the method updates event info. In the second it creates
     a new event instance in the db.
     */

    public void saveEvent() {    
        if (newEvent) {
            //makes sure that the end date is not before the start date (in case the end date has not been specified
            if(endingDate.before(startingDate)){
                endingDate=startingDate;
            }
            //save the new values into the event and persist it
            event.setTitle(title);
            event.setDescription(description);
            event.setStartDate(startingDate);
            event.setEndDate(endingDate);
            event.setEventLocation(place);
            event.setIsAllDay(allDay);
            event.setPublicEvent(isPublic);
            event.setCreator(currentUser);

            calendarManager.save(event);

            //add the event to the schedule model
            String id = event.getId();
            scheduleBean.getModel().addEvent(event);
            event.setId(id);

        } else {
            if(endingDate.before(startingDate)){
                endingDate=utility.findNewDateWithInterval(event.getStartDate(), event.getEndDate(), startingDate);
            }
            //update the event in the database
            calendarManager.changeEventTitle(event, title);
            calendarManager.changeEventDescription(event, description);
            calendarManager.changeEventTiming(event, startingDate, endingDate);
            if (place != null) {
                calendarManager.changeEventLocation(event, place);
            }
            calendarManager.changeEventPrivacy(event, isPublic);
            calendarManager.changeEventAllDay(event, allDay);
            //and update the schedule model
            scheduleBean.getModel().deleteEvent(event);
            event = calendarManager.getByID(event.getId());
            
            String id = event.getId();
            scheduleBean.getModel().addEvent(event);
            event.setId(id);
        }
      
        //add invited user
        if (!invitedUsers.isEmpty()) {
            for (User u : invitedUsers) {
                //non invita un utente che è già tra i partecipanti
                if (!calendarManager.getEventParticipant(event).contains(u)) {
                    notificationManager.sendAnInvite(event, u);
                }
            }
        }

        //be ready for the next update
        event = new Event();
        resetUtilityVariables();
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
    public String getLocationKey() {
        return locationKey;
    }

    /**
     * @param location the location to set
     */
    public void setLocationKey(String location) {
        this.locationKey = location;
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

    //set all the utility variables to the default value (null)
    public void resetUtilityVariables() {
        newEvent = true;
        title = "New event";
        description = new String();
        startingDate = new Date();
        endingDate = new Date();
        locationKey = new String();
        invitedUsers = new ArrayList<>();
        place = null;
    }

    /**
     * @return the invitedUsers
     */
    public List<User> getInvitedUsers() {
        return invitedUsers;
    }

    /**
     * @param invitedUsers the invitedUsers to set
     */
    public void setInvitedUsers(List<User> invitedUsers) {
        this.invitedUsers = invitedUsers;
    }

    /**
     * @return the place
     */
    public Place getPlace() {
        return place;
    }

    /**
     * @param place the place to set
     */
    public void setPlace(Place place) {
        this.place = place;
    }

    /**
     * method used to retrieve the places shown in the autocomplete
     */
    public List<String> findPLaces(String locationKey) {
        setLocationKey(locationKey);
        places = new ArrayList<String>();
        for (Place p : lm.getPlaceByName(locationKey)) {
            places.add(p.toString());
        }
        return places;
    }

    /**
     * method used to select a place has to get the name from the string "name
     * (country)"
     */
    public void onPlaceSelect(SelectEvent selectedPlace) {

        String completeString = selectedPlace.getObject().toString();

        String[] parts = completeString.split("\\s\\(");
        parts[1] = parts[1].replaceAll("[()]", "");
        
        System.out.println("part 0: "+parts[0]);
        System.out.println("part 1: "+parts[1]);
        
        setPlace(lm.getPlaceByNameAndCountry(parts[0], parts[1]));

        System.out.println(place.toString());
    }

    /**
     * @return the places
     */
    public List<String> getPlaces() {
        return places;
    }

    /**
     * @param places the places to set
     */
    public void setPlaces(List<String> places) {
        this.places = places;
    }
    
    /**
     * This method removes a user from an event to he/she have been invited
     */
    public void removeFromPartecipant(){
        System.out.println("user: "+removedFromParticipants.toString());
        //user has already accepted the invite
        if(calendarManager.getEventParticipant(event).contains(removedFromParticipants)){
            calendarManager.unSubscribeFromEvent(event, removedFromParticipants);
            invitedUsers.remove(removedFromParticipants);
        }else{
            //the invite has been sent but not accepted yet
            //@TODO
        }
    }

    /**
     * @return the removedFromPartecipants
     */
    public User getRemovedFromParticipants() {
        return removedFromParticipants;
    }

    /**
     * @param removedFromPartecipants the removedFromPartecipants to set
     */
    public void setRemovedFromParticipants(User removedFromParticipants) {
        this.removedFromParticipants = removedFromParticipants;
    }
}

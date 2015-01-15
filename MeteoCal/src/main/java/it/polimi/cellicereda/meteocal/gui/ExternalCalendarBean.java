/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.cellicereda.meteocal.gui;

import it.polimi.cellicereda.meteocal.entities.User;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 * This bean manages the visualization of other user's calendars according to their
 * own privacy settings
 * 
 * @author Andrea
 */
@RequestScoped
@Named

public class ExternalCalendarBean {
    
    private User user;

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
    
}

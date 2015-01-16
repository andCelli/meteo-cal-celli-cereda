/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.cellicereda.meteocal.gui;

import it.polimi.cellicereda.meteocal.businesslogic.UserProfileManager;
import it.polimi.cellicereda.meteocal.entities.User;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

/**
 * This bean manages the updates of user personal information
 * @author Andrea
 */
@RequestScoped
@Named
public class SettingsBean {
    
    @EJB
    private UserProfileManager upm;
    
    private User currentUser;
    
    //variable that determines if the save button is rendered or not
    private boolean isChanged=false;
    
    @PostConstruct
    public void init(){
        setCurrentUser(upm.getLoggedUser());
    }

    /**
     * @return the currentUser
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * @param currentUser the currentUser to set
     */
    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    /**
     * @return the isChanged
     */
    public boolean isIsChanged() {
        return isChanged;
    }

    /**
     * @param isChanged the isChanged to set
     */
    public void setIsChanged(boolean isChanged) {
        this.isChanged = isChanged;
    }
    
    public String save(){
        System.out.println("bottone");
        return "/logged/home?faces-redirect=true";
    }
}

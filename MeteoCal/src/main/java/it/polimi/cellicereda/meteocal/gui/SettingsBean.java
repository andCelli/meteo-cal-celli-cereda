/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.cellicereda.meteocal.gui;

import it.polimi.cellicereda.meteocal.businesslogic.UserProfileManager;
import it.polimi.cellicereda.meteocal.entities.User;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

/**
 * This bean manages the updates of user personal information
 * @author Andrea
 */
@SessionScoped
@Named
public class SettingsBean implements Serializable{
    
    @EJB
    private UserProfileManager upm;
    
    private User currentUser;
    
    
    private String password;
    
    
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

    public String save(){
        upm.changeUsername(currentUser, currentUser.getUsername());
        upm.changeName(currentUser, currentUser.getName());
        upm.changeSurname(currentUser, currentUser.getSurname());
        if(getPassword()!=null && !password.isEmpty()){
              upm.changePassword(currentUser, getPassword());
        }
        upm.changePublicCalendar(currentUser, currentUser.getPublicCalendar());
        return "/logged/home?faces-redirect=true";
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

}

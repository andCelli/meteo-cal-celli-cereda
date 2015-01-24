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
    
    private String username;
    private String name;
    private String surname;
    private String password;
    private boolean privacy;
    
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
        if(password!=null && !password.isEmpty()){
              upm.changePassword(currentUser,password);
        }
        upm.changePublicCalendar(currentUser, currentUser.getPublicCalendar());
        return "/logged/home?faces-redirect=true";
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the surname
     */
    public String getSurname() {
        return surname;
    }

    /**
     * @param surname the surname to set
     */
    public void setSurname(String surname) {
        this.surname = surname;
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

    /**
     * @return the privacy
     */
    public boolean isPrivacy() {
        return privacy;
    }

    /**
     * @param privacy the privacy to set
     */
    public void setPrivacy(boolean privacy) {
        this.privacy = privacy;
    }
}

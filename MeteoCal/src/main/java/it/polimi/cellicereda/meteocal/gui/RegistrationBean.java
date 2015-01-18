/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.cellicereda.meteocal.gui;

import it.polimi.cellicereda.meteocal.businesslogic.UserProfileManager;
import it.polimi.cellicereda.meteocal.entities.User;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * This bean manages the registration of a new user.
 * @author Andrea
 */
@Named
@RequestScoped
public class RegistrationBean{
    
    @EJB 
    private UserProfileManager userManager;
    @Inject
    private Logger logger;
    
    private User user;
    
    public RegistrationBean(){
    }
    
    public User getUser(){
        if(user==null){
            user=new User();
        }
        return user;
    }
    
    public void setUser(User user){
        this.user=user;
    }
    
    public String register(){
        FacesContext context = FacesContext.getCurrentInstance();
        try{
        userManager.save(user);
         return "logged/home?faces-redirect=true";
        }catch(Exception e){
            //@TODO specificare l'eccezione
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Registration Failed", "Registration Failed"));
            logger.log(Level.SEVERE,"Registration Failed, email already in use");
            return null;
        }
        
    }
}

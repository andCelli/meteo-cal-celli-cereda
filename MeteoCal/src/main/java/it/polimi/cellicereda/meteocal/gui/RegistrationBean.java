/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.cellicereda.meteocal.gui;

import it.polimi.cellicereda.meteocal.businesslogic.UserManager;
import it.polimi.cellicereda.meteocal.entities.User;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 * This Servlet manages the registration of a new user.
 * @author Andrea
 */
@Named
@RequestScoped
public class RegistrationBean {
    
    @EJB 
    private UserManager userManager;
    
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
        userManager.save(user);
        //?????
        return "user/home?faces-redirect=true";
    }
}

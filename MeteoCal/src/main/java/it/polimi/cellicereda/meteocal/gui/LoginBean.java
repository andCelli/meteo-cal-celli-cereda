/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.cellicereda.meteocal.gui;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

/**
 * This Servlet manages the login functionality of the Login page. 
 * @author Andrea
 */
@Named
@RequestScoped
public class LoginBean {
    
    private String username;
    private String password;
    
    public LoginBean(){
    }
    
    public String getUsername(){
        return this.username;
    }
    
    public void setUsername(String username){
        this.username=username;
    }
    
    public String getPassword(){
        return this.password;
    }
    
    public void setPassword(String password){
        this.password=password;
    }
    
    /*
    * The following method is called when the user clicks the Login button 
    * in the login page. 
    * The method checks whether the login has been successful or not. 
    * It returns a String that represent the "next step" (ex. the next page)
    */
    public String login(){
       
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        
        try{
            request.login(this.username,this.password);
        }catch(ServletException e){
            context.addMessage(null, new FacesMessage("Login failed."));
            return "login";
        }
        //login OK
        return "/logged/home";
    }
    
    /*
    * The logout() method interrupts the current HttpSession and redirect 
    * to the login page.
    */
    public String logout() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        //interrupt the current session
        request.getSession().invalidate();
        //??????????
        return "/login?faces-redirect=true";
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.cellicereda.meteocal.gui;

import it.polimi.cellicereda.meteocal.businesslogic.UserProfileManager;
import it.polimi.cellicereda.meteocal.entities.User;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 * This bean manages the user searches for other users
 * @author Andrea
 */
@ManagedBean
@SessionScoped
@Named
public class SearchBean implements Serializable {
    
    @EJB
    UserProfileManager userManager;
    
    private User currentUser;
    
    //the searched string
    private String searchKey;
    
    @PostConstruct
    public void init(){
        currentUser=userManager.getLoggedUser();
    }
    
    /*
    This method is called when a search is performed
    */
    public String search(){
         return "/logged/search?faces-redirect=true";
    }
    
    /**
     * This method return the list of results
     * At the end the searchKey is resetted
     * 
     * criteria of search: 1) username 2) surname and name 3) surname 4) name
     * 
     * @return List<User> 
     */
    public List<User> getSearchResults(){
        List<User> results=new ArrayList<User>();
        
        //search for username
        //user can't search for his calendar
        if(!currentUser.getUsername().equals(searchKey)){
            results.add((User) userManager.getByUsername(searchKey));
        }
        //search for (name + surmane) (and check that the user is not already in the list
        for(User u:userManager.getBySurname(searchKey))
            /**
             * CHECK (se metto più di uno spazio?)
             */
            if(!results.contains(u) && searchKey.equals(u.getName()+" "+u.getSurname()))
                results.add(u);
       //search for surname
        for(User u:userManager.getBySurname(searchKey))
            if(!results.contains(u))
                results.add(u);
       //search for name
        for(User u:userManager.getByName(searchKey))
            if(!results.contains(u))
                results.add(u);
        
        return results;
    }
    
    public String getSearchKey(){
        return searchKey;
    }
    public void setSearchKey(String searchKey){
        this.searchKey=searchKey;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.cellicereda.meteocal.gui;

import static com.sun.corba.se.impl.util.Utility.printStackTrace;
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
    private UserProfileManager userManager;
    
    private User currentUser;
    
    //the searched string
    private String searchKey;
    
    @PostConstruct
    public void init(){
        setSearchKey(new String());
        currentUser=userManager.getLoggedUser();
    }
    
    /*
    This method is called when a search is performed
    */
    public String search(){
        try{
        if( getSearchKey().length()==0){
            return "/logged/home?faces-redirect=true";
        }
        return "/logged/search?faces-redirect=true";
        }catch(Exception e){
            printStackTrace();
            System.err.println("problem in search() Search key: "+getSearchKey());
        }
        return "/logged/home?faces-redirect=true";
    }
    
    /**
     * This method return the list of results
     * At the end the searchKey is resetted
     * 
     * @return List<User> 
     */
    public List<User> getSearchResults(){
        List<User> results=new ArrayList<User>();
        
        try{ 
        //search for username
        //user can't search for his calendar
        if(!currentUser.getUsername().equals(searchKey)){
            results.addAll(userManager.getByUsername(getSearchKey()));
        }
        //search for (name + surmane) (and check that the user is not already in the list
       /* for(User u:userManager.getBySurname(getSearchKey()))
             //
             // CHECK (se metto più di uno spazio?)
             //
            if(!results.contains(u) && getSearchKey().equals(u.getName()+" "+u.getSurname()))
                results.add(u);*/
       //search for surname
        for(User u:userManager.getBySurname(getSearchKey()))
            if(!results.contains(u))
                results.add(u);
       //search for name
        for(User u:userManager.getByName(getSearchKey()))
            if(!results.contains(u))
                results.add(u);
            System.out.println("Search key: "+getSearchKey());
        }catch(Exception e){
            printStackTrace();
            System.err.println("Problems in the user search. Search key: "+getSearchKey());
        }
        
        return results;
    }

    /**
     * @return the searchKey
     */
    public String getSearchKey() {
        return searchKey;
    }

    /**
     * @param searchKey the searchKey to set
     */
    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }
    
   
}

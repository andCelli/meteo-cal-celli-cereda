/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.cellicereda.meteocal.gui;


import it.polimi.cellicereda.meteocal.businesslogic.NotificationManager;
import it.polimi.cellicereda.meteocal.businesslogic.UserProfileManager;
import it.polimi.cellicereda.meteocal.entities.Event;
import it.polimi.cellicereda.meteocal.entities.User;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.event.SelectEvent;

/**
 * Backing bean for the autocomplete component to select user for event invitations
 * @author Andrea
 */
@Named
@RequestScoped
public class UserInviteBean {
    
    @Inject
    private SearchBean searchBean;
   
    @EJB
    private UserProfileManager upm;
    
    private ModifyEventBean meb;
    
    private List<String> searchResults;
    
    private String searchKey;
    
    private Event currentEvent;
    
    private FacesContext context;
    
    @PostConstruct
    public void init(){
        try{
        context=FacesContext.getCurrentInstance();           
        meb=(ModifyEventBean) context.getApplication().evaluateExpressionGet(context, "#{modifyEventBean}", ModifyEventBean.class);
        }catch(Exception e){
            e.printStackTrace();
            System.err.println("error while retrieving the ModifyEventBean in the UserInviteBean");
        }
    }
    /**
     * find the list of emails to be displayed as the search result
     * 
     * @TODO non mostrare partecipanti/persone già invitate
     * 
     * @param searchKey
     * @return List<String>
     */
    public List<String> autoCompleteSearch(String searchKey){
        searchBean.setSearchKey(searchKey);
        setSearchResults(new ArrayList<String>());
        for(User u:searchBean.getSearchResults())
            getSearchResults().add(u.getEmail());
        return getSearchResults();  
    }
    
    //@TODO
    public void onUserSelect(SelectEvent selectedUser){
     User selected=upm.getByEmail(selectedUser.getObject().toString());
     List<User> invitedUsers=meb.getInvitedUsers();
     
     if(!invitedUsers.contains(selected)){
      invitedUsers.add(selected);
     }
    }

    /**
     * @return the searchResults
     */
    public List<String> getSearchResults() {
        return searchResults;
    }

    /**
     * @param searchResults the searchResults to set
     */
    public void setSearchResults(List<String> searchResults) {
        this.searchResults = searchResults;
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

    /**
     * @return the currentEvent
     */
    public Event getCurrentEvent() {
        return currentEvent;
    }

    /**
     * @param currentEvent the currentEvent to set
     */
    public void setCurrentEvent(Event currentEvent) {
        this.currentEvent = currentEvent;
    }
    
    
}

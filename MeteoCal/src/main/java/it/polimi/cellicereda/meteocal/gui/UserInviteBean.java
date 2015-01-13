/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.cellicereda.meteocal.gui;

import it.polimi.cellicereda.meteocal.entities.User;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.RequestScoped;
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
    SearchBean searchBean;
    
    private List<String> searchResults;
    
    private String searchKey;
    
    /**
     * find the list of usernames to be displayed as the search result
     * 
     * @param searchKey
     * @return List<String>
     */
    public List<String> autoCompleteSearch(String searchKey){
        searchBean.setSearchKey(searchKey);
        setSearchResults(new ArrayList<String>());
        for(User u:searchBean.getSearchResults())
            getSearchResults().add(u.getUsername());
        return getSearchResults();  
    }
    
    //@TODO
    public void onUserSelect(SelectEvent selectedUser){
        System.out.println(selectedUser.getObject().toString());
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
    
    
}

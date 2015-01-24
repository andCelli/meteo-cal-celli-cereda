/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.cellicereda.meteocal.gui;

import static com.sun.corba.se.impl.util.Utility.printStackTrace;
import it.polimi.cellicereda.meteocal.entities.User;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

/**
 * This bean shows search results
 * @author Andrea
 */

@SessionScoped
@Named
public class ResultsBean implements Serializable{
    
    
    FacesContext context;
    
    private SearchBean searchBean;
  
    private List<User> users;
    
    private User selectedUser;
   
    //the message shown in the header
    private String header;
    
    @PostConstruct
    public void init(){
        header="Search Results";
        context=FacesContext.getCurrentInstance();           
        searchBean=(SearchBean) context.getApplication().evaluateExpressionGet(context, "#{searchBean}", SearchBean.class);
    }
    
    public void updateResults(){
        users=searchBean.getSearchResults();
        searchBean.setSearchKey(null);
        //userNotFound
        if(users.isEmpty()){
            header="No results found";
        }
    }
    
    public List<User> getUsers(){
        return users;
    }
    
    public String getHeader(){
        return header;
    }

    /**
     * @return the selectedUser
     */
    public User getSelectedUser() {
        return selectedUser;
    }

    /**
     * @param selectedUser the selectedUser to set
     */
    public void setSelectedUser(User selectedUser) {
        this.selectedUser = selectedUser;
    }
    
    public String goToUserCalendar(){
        System.out.println("selezionato "+selectedUser.toString());
        return "/logged/externalCal?faces-redirect=true;email="+selectedUser.getEmail();
    }
}

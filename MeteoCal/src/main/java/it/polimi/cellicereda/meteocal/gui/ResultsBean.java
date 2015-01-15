/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.cellicereda.meteocal.gui;

import static com.sun.corba.se.impl.util.Utility.printStackTrace;
import static com.sun.corba.se.spi.presentation.rmi.StubAdapter.request;
import it.polimi.cellicereda.meteocal.entities.User;
import java.util.List;
import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

/**
 * This bean shows search results
 * @author Andrea
 */

@ViewScoped
@Named
public class ResultsBean {
    
    
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
        users=searchBean.getSearchResults();
        //userNotFound
        if(users.size()==0){
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
    
}

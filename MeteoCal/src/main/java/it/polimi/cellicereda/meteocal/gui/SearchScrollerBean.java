/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.cellicereda.meteocal.gui;

import it.polimi.cellicereda.meteocal.entities.User;
import java.util.List;
import javax.annotation.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Named;

/**
 * This bean shows search results
 * @author Andrea
 */
@ManagedBean
@ViewScoped
@Named
public class SearchScrollerBean {
    
    private List<User> users;
    
}

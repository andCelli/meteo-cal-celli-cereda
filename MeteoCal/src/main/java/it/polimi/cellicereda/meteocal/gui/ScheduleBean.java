/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.cellicereda.meteocal.gui;

import javax.faces.bean.ViewScoped;
import javax.inject.Named;
import org.primefaces.model.ScheduleModel;

/**
 * Backing bean for the schedule holding user events.
 * @author Andrea
 */
@Named
//quale conviene mettere?
@ViewScoped
public class ScheduleBean {
    
    private ScheduleModel model;
            
    public ScheduleBean(){
        
    }        
     
    
}

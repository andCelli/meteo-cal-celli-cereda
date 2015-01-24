/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.cellicereda.meteocal.gui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.ejb.Stateless;
import javax.inject.Named;

/**
 * This class contains some utilities 
 * @author Andrea
 */
@Stateless
@Named
public class Utility {
    
    private DateFormat df=new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    
    public String getFormattedDate(Date d){
        return df.format(d);
    }
    
    /**
     * This methods gets as inputs the start and end in time of an event and a new start date.
     * It returns a new end date such that the time interval between the new end date and the new start is equal to the 
     * one before the change. 
     * 
     * @param oldStartDate
     * @param oldEndDate
     * @param newStartDate
     * @return the new end date
     * @throws IllegalArgumentException
     */
    public Date findNewDateWithInterval(Date oldStartDate, Date oldEndDate, Date newStartDate) throws IllegalArgumentException{
        
        Date newEndDate=new Date();
        long interval;
        
        if(oldEndDate.before(oldStartDate)||newStartDate.before(oldStartDate)){
            throw new IllegalArgumentException("Dates are not consistent");
        }
        
        interval=oldEndDate.getTime()-oldStartDate.getTime();
        newEndDate.setTime(newStartDate.getTime()+interval);
        
        return newEndDate;
        
    }
}

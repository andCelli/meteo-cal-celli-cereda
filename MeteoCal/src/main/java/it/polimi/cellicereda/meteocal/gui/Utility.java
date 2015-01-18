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
}

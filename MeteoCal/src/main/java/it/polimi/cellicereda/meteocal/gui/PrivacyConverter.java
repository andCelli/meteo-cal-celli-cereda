/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.cellicereda.meteocal.gui;

import java.lang.annotation.Annotation;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.FacesConverter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

/**
 * This is a converter that allows to convert the value of privacy buttons to the 
 * value of the privacy flag in the entity User.
 * 
 * @author Andrea
 */
@FacesConverter ("privacyConverter")
public class PrivacyConverter implements Converter{

    //from view to model
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) throws ConverterException{
      Boolean publicCalendar=null; 
      
      if(value=="Public"){
          publicCalendar=true;
      }else{
          if(value=="Private"){
              publicCalendar=false;
          }else 
              throw new ConverterException("invalid privacy type");
      }
      
      return publicCalendar;
    }

    //from model to view
    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

   
    
}

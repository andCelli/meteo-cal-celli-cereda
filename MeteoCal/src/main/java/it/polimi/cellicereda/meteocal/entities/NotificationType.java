/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.cellicereda.meteocal.entities;

import java.io.Serializable;

/**
 *
 * @author stefano
 */
public enum NotificationType{
    EVENT_CHANGED, BAD_WEATHER_ALERT, SUNNY_DAY_PROPOSAL, EVENT_INVITE;
    
    @Override
    public String toString(){
        if(this==EVENT_CHANGED)
            return "EVENT_CHANGED";
        else
            if(this==BAD_WEATHER_ALERT)
                return "BAD_WEATHER-ALERT";
        else
                if(this==SUNNY_DAY_PROPOSAL)
                    return "SUNNY_DAY_PROPOSAL";
                return "EVENT_INVITE";
    }
    
    public String displayableString(){
         if(this==EVENT_CHANGED)
            return "Event details changed";
        else
            if(this==BAD_WEATHER_ALERT)
                return "Bad weather warning";
        else
                if(this==SUNNY_DAY_PROPOSAL)
                    return "Sunny day proposal";
                return "Invite";
    }
}

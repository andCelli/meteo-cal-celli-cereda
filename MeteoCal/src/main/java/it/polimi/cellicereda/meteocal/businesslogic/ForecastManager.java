/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.cellicereda.meteocal.businesslogic;

import it.polimi.cellicereda.meteocal.entities.Forecast;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;

/**
 *
 * @author stefano
 */
public class ForecastManager {

    @PersistenceContext
    EntityManager em;
    
    private String downloadForecastsByCityID(int cityID) {
        try {
            String url = "http://api.openweathermap.org/data/2.5/forecast?id=" + cityID;
            return URLConnectionReader.getText(url);
        } catch (Exception e) {
            Logger.getLogger(ForecastManager.class.getName()).log(Level.SEVERE, "Unable to download forecasts", e);
            return null;
        }
    }
    
    public List<Forecast> getNewForecastsByCityID(int cityID) {
        try {
            JSONObject entireForecast = new JSONObject(downloadForecastsByCityID(cityID));
            JSONArray forecastList = entireForecast.getJSONArray("list");

            //TODO SET THE MAKING TIME ("dt":1420869600)
            for (int i = 0; i < forecastList.length(); i++) {
                JSONObject forecastJson = forecastList.getJSONObject(i);
                
                Forecast forecast = new Forecast();

                //TODO SET THE PLACE
                
                //SET THE STARTING/ENDING VALIDITY TIME 
                try {
                    String starting = forecastJson.getString("dt_txt");
                    DateFormat format = new SimpleDateFormat("YYYY-MM-DD HH:MM:SS");
                    Date startingValidity = format.parse(starting);

                    //the forecasts have a time coverage of three hours
                    Date endingValidity = new Date(startingValidity.getTime() + (3 * 60 * 60 * 1000));
                    
                    forecast.setStartingValidity(startingValidity);
                    forecast.setEndingValidity(endingValidity);
                } catch (ParseException ex) {
                    Logger.getLogger(ForecastManager.class.getName()).log(Level.SEVERE, "Unable to parse starting validity time", ex);
                    
                }

                //TODO SAVE THE MAKING TIME (COMPUTED BEFORE)
                
                //SET THE WEATHER ID
                forecast.setWeatherId(forecastJson.getJSONObject("weather").getInt("id"));

                //PERSIST THE FORECAST
                em.persist(forecast);
            }
            
        } catch (JSONException ex) {
            Logger.getLogger(ForecastManager.class.getName()).log(Level.SEVERE, "Unable to download forecasts", ex);
            return null;
        }
        
        return null;
    }
}

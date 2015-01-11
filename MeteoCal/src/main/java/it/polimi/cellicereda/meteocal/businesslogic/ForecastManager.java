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
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
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

    @EJB
    LocationManager lm;

    /**
     * Download from openweathermap the forecasts for the given city, the
     * forecast have a time span of 5 days with data every 3 hours
     */
    private String downloadForecastsByCityID(Long cityID) {
        try {
            String url = "http://api.openweathermap.org/data/2.5/forecast?id=" + cityID;
            return URLConnectionReader.getText(url);
        } catch (Exception e) {
            Logger.getLogger(ForecastManager.class.getName()).log(Level.SEVERE, "Unable to download forecasts", e);
            return null;
        }
    }

    /**
     * Given a cityID this method queries openweathermap to obtain the updated
     * forecast, which are then returned as a list
     */
    public List<Forecast> getNewForecastsByCityID(Long cityID) {
        try {
            JSONObject entireForecast = new JSONObject(downloadForecastsByCityID(cityID));
            JSONArray forecastList = entireForecast.getJSONArray("list");

            //compute the making time
            Date now = new Date();

            for (int i = 0; i < forecastList.length(); i++) {
                JSONObject forecastJson = forecastList.getJSONObject(i);

                Forecast forecast = new Forecast();

                //SET THE PLACE
                forecast.setPlace(lm.getPlaceByID(cityID));

                //TODO:SET THE STARTING/ENDING VALIDITY TIME (time coverage = 3 hours)
                Date starting = new Date(forecastJson.getLong("dt") * 1000);
                Date ending = new Date(starting.getTime() + (3 * 60 * 60 * 1000));
                forecast.setStartingValidity(starting);
                forecast.setEndingValidity(ending);

                //SAVE THE MAKING TIME (computed before and equals for all the forecasts)
                forecast.setMakingTime(now);

                //SET THE WEATHER ID
                forecast.setWeatherId(forecastJson.getJSONArray("weather").getJSONObject(0).getInt("id"));

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

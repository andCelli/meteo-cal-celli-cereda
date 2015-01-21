/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.cellicereda.meteocal.businesslogic;

import it.polimi.cellicereda.meteocal.entities.Event;
import it.polimi.cellicereda.meteocal.entities.Forecast;
import it.polimi.cellicereda.meteocal.entities.Place;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONObject;

/**
 *
 * @author stefano
 */
@Stateless
public class ForecastManager {

    @PersistenceContext
    EntityManager em;

    @EJB
    LocationManager lm;

    @EJB
    CalendarManager cm;

    @EJB
    NotificationManager nm;

    /**
     * Download from openweathermap the forecasts for the given city, the
     * forecast have a time span of 5 days with data every 3 hours
     */
    private String downloadForecastsByCityID(Long cityID) throws Exception {
        String url = "http://api.openweathermap.org/data/2.5/forecast?id=" + cityID;
        return URLConnectionReader.getText(url);
    }

    /**
     * Given an event this method queries openweathermap to obtain the updated
     * forecast, which is returned. If no forecast is available the method
     * returns null. Note that the forecast is not persisted into the database.
     * The forecast has to be referred to the same place of the event and has to
     * have a time coverage that includes the starting time of the event
     *
     * @param event The event for which you want to download a new forecast
     * @return The forecasts obtained for the given event
     */
    public Forecast downloadNewForecastForEvent(Event event) {
        try {
            Long cityID = event.getEventLocation().getId();
            Date wantedTime = event.getStartDate();

            JSONObject entireForecast = new JSONObject(downloadForecastsByCityID(cityID));
            JSONArray forecastList = entireForecast.getJSONArray("list");

            //compute the making time
            Date now = new Date();

            for (int i = 0; i < forecastList.length(); i++) {
                JSONObject forecastJson = forecastList.getJSONObject(i);

                //SET THE STARTING/ENDING VALIDITY TIME (time coverage = 3 hours)
                Date starting = new Date(forecastJson.getLong("dt") * 1000);
                Date ending = new Date(starting.getTime() + (3 * 60 * 60 * 1000));

                if (starting.after(wantedTime) || ending.before(wantedTime)) {
                    continue;
                }

                Forecast forecast = new Forecast();

                forecast.setStartingValidity(starting);
                forecast.setEndingValidity(ending);

                //SET THE PLACE
                forecast.setPlace(lm.getPlaceByID(cityID));

                //SAVE THE MAKING TIME (computed before and equals for all the forecasts)
                forecast.setMakingTime(now);

                //SET THE WEATHER ID
                forecast.setWeatherId(forecastJson.getJSONArray("weather").getJSONObject(0).getInt("id"));

                //RETURN THE FORECAST
                return forecast;
            }

        } catch (Exception ex) {
            Logger.getLogger(ForecastManager.class
                    .getName()).log(Level.SEVERE, "Unable to download forecasts", ex);
        }
        return null;
    }

    /**
     * Given an event search for a forecast that is related to the same place of
     * the event and has a temporal validity that includes the beginning of the
     * given event. The value returned is the weather id of the obtained
     * forecast. For a list of the weather id see
     * http://openweathermap.org/weather-conditions If no forecast is available
     * the reruned value is 0
     *
     * @param e The event to search a forecast for
     * @return The weather id or 0 if no forecast is available
     */
    public int getWeatherForEvent(Event e) {
        Place location = e.getEventLocation();
        Date time = e.getStartDate();

        try {
            Forecast f = (Forecast) em.createNamedQuery("Forecast.findByPlaceAndTime").
                    setParameter("id", location.getId()).
                    setParameter("time", time).getSingleResult();
            return f.getWeatherId();

        } catch (javax.persistence.NoResultException ex) {
            return 0;
        }
    }

    /**
     * Given a weather id search for the icon that represents the weather
     * according to http://openweathermap.org/weather-conditions
     *
     * @param weatherID An openweathermap weather id
     * @return The path of the icon representing the weather
     */
    public String getUrlOfWeatherIcon(int weatherID) {
        String url = "http://openweathermap.org/img/w/";

        if (isBetween(weatherID, 200, 232)) {
            url += "11d.png";
        } else if (isBetween(weatherID, 300, 321)) {
            url += "09d.png";
        } else if (isBetween(weatherID, 500, 504)) {
            url += "10d.png";
        } else if (weatherID == 511) {
            url += "13d.png";
        } else if (isBetween(weatherID, 520, 531)) {
            url += "09d.png";
        } else if (isBetween(weatherID, 600, 622)) {
            url += "13d.png";
        } else if (isBetween(weatherID, 700, 781)) {
            url += "50d.png";
        } else if (isBetween(weatherID, 800, 804)) {
            Integer num = (weatherID % 10) + 1;
            if (num == 5) {
                num = 4;
            }
            url += "0" + num + "d.png";
        } else {
            url += "04d.png";
        }

        return url;
    }

    private boolean isBetween(int num, int min, int max) {
        return (min <= num && num <= max);
    }

    /**
     * Given a weather id check if it represents a "good" weather. The check is
     * done according to the weather icon
     * (http://openweathermap.org/weather-conditions) icon from 1 to 4 are
     * considered "good weather", the other "bad weather"
     */
    public boolean isGoodWeather(int weatherID) {
        String icon = getUrlOfWeatherIcon(weatherID);
        //remove "d.png" and go to the first number
        int beginning = icon.length() - 7;
        int num = Integer.parseInt(icon.substring(beginning, beginning + 2));

        return num <= 4;
    }

    public void updateForecastsForAllTheEvents() {
        List<Event> events = cm.getAllEventsAsEvents();

        for (Event e : events) {
            //check if the event is in the future and if it has a valid location
            if (e.getEventLocation() != null && e.getStartDate().after(new Date())) {

                //if the event is in the future by more than 5 days don't even try to download a new forecast as we won't get it
                if (!cm.isInFiveDays(e)) {
                    continue;
                }

                //if the event already have a valid forecast save it
                Forecast oldForecast = e.getForecast();
                boolean wasGood = true;
                if (oldForecast != null) {
                    wasGood = isGoodWeather(oldForecast.getWeatherId());
                }

                //download the new forecast
                Forecast newForecast = downloadNewForecastForEvent(e);

                //and update the old one (so taht the id does not change in the db)
                oldForecast.setMakingTime(newForecast.getMakingTime());
                oldForecast.setWeatherId(newForecast.getWeatherId());

                //now it's time to generate the notifications
                //if the event is tomorrow and the weather is bad generate a bad weather alert
                if (cm.isTomorrow(e) && !isGoodWeather(newForecast.getWeatherId())) {
                    nm.sendBadWeatherAlert(e);
                }

                //if the event starts in three days and the weather just turned bad send a sunny day proposal
                if (cm.isInThreeDays(e) && wasGood && !isGoodWeather(newForecast.getWeatherId())) {
                    nm.sendSunnyDayProposal(e);
                }
            }
        }
    }
}

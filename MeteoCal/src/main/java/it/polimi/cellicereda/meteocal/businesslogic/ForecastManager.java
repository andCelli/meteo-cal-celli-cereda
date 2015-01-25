/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.cellicereda.meteocal.businesslogic;

import it.polimi.cellicereda.meteocal.entities.Event;
import it.polimi.cellicereda.meteocal.entities.Forecast;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONException;
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

    public List<Forecast> findForecastWithoutRelatedEvent() {
        return em.createNamedQuery("Forecast.findForecastWithouEvent").getResultList();
    }

    /**
     * Download from openweathermap the forecasts for the given city, the
     * forecast have a time span of 5 days with data every 3 hours
     */
    private String downloadForecastsByCityID(Long cityID) throws UnableToDownloadException {
        String toReturn = null;

        try {
            String url = "http://api.openweathermap.org/data/2.5/forecast?id=" + cityID;
            toReturn = URLConnectionReader.getText(url);
        } catch (Exception ex) {
            throw new UnableToDownloadException(ex);
        }
        return toReturn;
    }

    /**
     * Download from openweathermap the forecasts for the given city, the
     * forecast have a time span of 16 days with data every 1 day
     */
    private String download16DayForecastsByCityID(Long cityID) throws UnableToDownloadException {
        String toReturn = null;

        try {
            String url = "http://api.openweathermap.org/data/2.5/forecast/daily?id=" + cityID + "&cnt=16";
            toReturn = URLConnectionReader.getText(url);
        } catch (Exception ex) {
            throw new UnableToDownloadException(ex);
        }
        return toReturn;
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
    Forecast downloadNewForecastForEvent(Event event) {
        if (event.getEventLocation() == null || event.getStartDate() == null) {
            return null;
        }

        //If it's not possible to download a new forecast keep the old one
        Forecast oldForecast = event.getForecast();
        Forecast forecast = null;

        try {
            Long cityID = event.getEventLocation().getId();
            Date wantedTime = event.getStartDate();

            //try with 5 days
            if (cm.isInFiveDays(event)) {
                String jsonString = downloadForecastsByCityID(cityID);
                JSONObject jsonForecast = new JSONObject(jsonString);
                JSONArray forecastList = jsonForecast.getJSONArray("list");
                int coveredHours = 3;

                forecast = downloadForecast(forecastList, coveredHours, wantedTime, cityID);
            }

            //try with 16 days
            if (forecast == null && cm.isInNDays(event, 16)) {
                String jsonString = download16DayForecastsByCityID(cityID);
                JSONObject jsonForecast = new JSONObject(jsonString);
                JSONArray forecastList = jsonForecast.getJSONArray("list");
                int coveredHours = 24;

                forecast = downloadForecast(forecastList, coveredHours, wantedTime, cityID);
            }
        } catch (UnableToDownloadException ex) {
            Logger.getLogger(ForecastManager.class
                    .getName()).log(Level.INFO, "Unable to download forecast.", ex);
        } catch (JSONException ex) {
            Logger.getLogger(ForecastManager.class.
                    getName()).log(Level.SEVERE, "Error while parsing the forecast", ex);
        }

        if (forecast == null) {
            return oldForecast;
        }
        return forecast;
    }

    /**
     * Given the JSON array and the time coverage wanted this method parse the
     * json and returns the wanted forecast
     *
     * @param forecastList A JSONArray containing the data to be parsed
     * @param coveredHours The time coverage of the given forecast (3 hours /
     * daily)
     * @param wantedTime The time that must be covered by the returned forecast
     * @param cityID The cityID for wich is the given forecast
     * @return The Forecast containing the wanted forecast (can be null)
     * @throws JSONException If a parse error occurs
     */
    private Forecast downloadForecast(JSONArray forecastList, int coveredHours, Date wantedTime, Long cityID) throws JSONException {
        //compute the making time
        Date now = new Date();

        for (int i = 0; i < forecastList.length(); i++) {
            JSONObject forecastJson = forecastList.getJSONObject(i);

            //SET THE STARTING/ENDING VALIDITY TIME
            Date starting = new Date(forecastJson.getLong("dt") * 1000);
            Date ending = new Date(starting.getTime() + (coveredHours * 60 * 60 * 1000));

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
        return null;
    }

    /**
     * Download a new forecast for the given event, save it in the database and
     * attach it in the event. If the event already has a forecast it is removed
     * from the db. This method also generates the
     * sunnyDayProposal/BadWeatherAlert notifications if needed
     */
    public void saveNewForecastForecastForEvent(Event e) {
        e = em.find(Event.class, e.getId());

        Forecast f = downloadNewForecastForEvent(e);

        if (f != null) {
            Forecast old = e.getForecast();

            em.persist(f);
            cm.changeEventForecast(e, f);

            if (old != null) {
                em.remove(old);
            }

            // now it's time to generate the notifications
            //if the event is tomorrow and the weather is bad generate a bad weather alert
            if (cm.isTomorrow(e) && !isGoodWeather(f.getWeatherId())) {
                nm.sendBadWeatherAlert(e);
            }

            //if the event starts in three days and the weather is bad send a sunny day proposal
            if (cm.isInThreeDays(e) && !isGoodWeather(f.getWeatherId())) {
                nm.sendSunnyDayProposal(e);
            }
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
     *
     * @param weatherID The integer represting the weather
     * @return If the weather has to be considered good
     */
    public boolean isGoodWeather(int weatherID) {
        String icon = getUrlOfWeatherIcon(weatherID);
        //remove "d.png" and go to the first number
        int beginning = icon.length() - 7;
        int num = Integer.parseInt(icon.substring(beginning, beginning + 2));

        return num <= 4;
    }

    @Schedule(hour = "*", persistent = false)
    /**
     * Update the forecast for all the events (and generate the notifications)
     */
    private void updateForecastsForAllTheEvents() {
        List<Event> events = cm.getAllEventsAsEvents();

        for (Event e : events) {
            //check if the event is in the future and if it has a valid location
            if (e.getEventLocation() != null && e.getStartDate().after(new Date())) {

                //if the event is in the future by more than 16 days don't even try to download a new forecast as we won't get it
                if (!cm.isInNDays(e, 16)) {
                    continue;
                }

                saveNewForecastForecastForEvent(e);
            }
        }
    }

    /**
     * Given an event search for the days where the event could be moved to
     * obtain a good weather. A day is considered to be good or not basing on
     * the daily forecast, not on the 3 hours forecast. For a matter of
     * simplicity the time coverage of the forecasts is not set as
     * 0.0.00/23.59.59 but is set equal to the duration of the given event, in
     * that way they can be directly used to reschedule the event
     *
     * @param e The event to be moved
     * @return A list of forecast that are considered good
     */
    public List<Forecast> searchGoodWeatherForEvent(Event e) {
        List<Forecast> goodDates = new LinkedList<>();

        e = em.find(Event.class, e.getId());

        //if the event is not "valid" return an empty list
        if (e.getEventLocation() == null || e.getStartDate() == null) {
            return goodDates;
        }

        try {
            Long cityID = e.getEventLocation().getId();
            Date eventStarting = e.getStartDate();
            Date eventEnding = e.getEndDate();

            JSONObject entireForecast = new JSONObject(download16DayForecastsByCityID(cityID));
            JSONArray forecastList = entireForecast.getJSONArray("list");

            //compute the making time
            Date now = new Date();

            for (int i = 0; i < forecastList.length(); i++) {
                JSONObject forecastJson = forecastList.getJSONObject(i);

                //GET THE WEATHER ID
                int weatherID = forecastJson.getJSONArray("weather").getJSONObject(0).getInt("id");

                //IF IT IS GOOD CREATE THE FORECAST AND ADD IT TO THE LIST
                if (isGoodWeather(weatherID)) {
                    Forecast forecast = new Forecast();

                    //the starting validity has the day equal to the forecast validity and the time equal to the event duration
                    Date forecastStarting = new Date(forecastJson.getLong("dt")*1000);

                    Date starting = mixTiming(forecastStarting, eventStarting);
                    Date ending = mixTiming(forecastStarting, eventEnding);

                    forecast.setStartingValidity(starting);
                    forecast.setEndingValidity(ending);
                    forecast.setPlace(lm.getPlaceByID(cityID));
                    forecast.setMakingTime(now);
                    forecast.setWeatherId(weatherID);

                    goodDates.add(forecast);
                }
            }
        } catch (UnableToDownloadException ex) {
            Logger.getLogger(ForecastManager.class
                    .getName()).log(Level.INFO, "Unable to download forecast.", ex);
        } catch (JSONException ex) {
            Logger.getLogger(ForecastManager.class.
                    getName()).log(Level.SEVERE, "Error while parsing the forecast", ex);
        }
        return goodDates;
    }

    /**
     * Return a date that has the day/month/year equal to the first parameter
     * and the hour/minute/second/ms equals to the second
     */
    private Date mixTiming(Date day, Date hour) {
        Calendar cHour = Calendar.getInstance();
        cHour.setTime(hour);

        Calendar toReturn = Calendar.getInstance();
        toReturn.setTime(day);
        toReturn.set(Calendar.HOUR_OF_DAY, cHour.get(Calendar.HOUR_OF_DAY));
        toReturn.set(Calendar.MINUTE, cHour.get(Calendar.MINUTE));
        toReturn.set(Calendar.SECOND, cHour.get(Calendar.SECOND));
        toReturn.set(Calendar.MILLISECOND, cHour.get(Calendar.MILLISECOND));

        return (toReturn.getTime());
    }
}

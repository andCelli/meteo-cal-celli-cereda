/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.cellicereda.meteocal.businesslogic;

import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author stefano
 */
public class ForecastManager {

    private String downloadForecastsBiCityID(int cityID) {
        try {
            String url = "http://api.openweathermap.org/data/2.5/forecast?id=" + cityID;
            return URLConnectionReader.getText(url);
        } catch (Exception e) {
            Logger.getLogger("ForecastManager").log(Level.SEVERE, "Unable to download forecasts", e);
            return null;
        }
    }
}

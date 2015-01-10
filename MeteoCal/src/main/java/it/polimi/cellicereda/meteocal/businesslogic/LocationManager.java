/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.cellicereda.meteocal.businesslogic;

import it.polimi.cellicereda.meteocal.entities.Place;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author stefano
 */
public class LocationManager {

    @PersistenceContext
    EntityManager em;

    public void initializePlaceList() {
        try {
            String placesList = URLConnectionReader.getText("http://openweathermap.org/help/city_list.txt");
            String places[] = placesList.split("\\n");

            //the first line is descriptive
            for (int i = 1; i < places.length; i++) {
                String values[] = places[i].split("\\t");

                Place p = new Place();
                p.setId(Long.parseLong(values[0]));
                p.setName(values[1]);
                p.setLatitude(Double.parseDouble(values[2]));
                p.setLongitude(Double.parseDouble(values[3]));
                p.setCountry(values[4]);

                em.persist(p);
            }
        } catch (Exception ex) {
            Logger.getLogger(LocationManager.class.getName()).log(Level.SEVERE, "Error while creating the places list", ex);
        }
    }

    public List<Place> getAllPlaces() {
        return em.createNamedQuery("Place.findAll").getResultList();
    }
}

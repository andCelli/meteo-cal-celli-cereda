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
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author stefano
 */
@Stateless
public class LocationManager {

    @PersistenceContext
    EntityManager em;

    /**
     * This method downloads the list of places from openweathermap, it should
     * be called only once, at the startup of the system. It is declared package
     * private instead of private so the test package can use it
     */
    public void initializePlaceList() {
        if (!em.createNamedQuery("Place.findAll").getResultList().isEmpty()) {
            return;
        }

        try {
            String placesList = URLConnectionReader.getText("http://openweathermap.org/help/city_list.txt");
            String places[] = placesList.split("\\n");

            //the first line is descriptive and the last is empty
            for (int i = 1; i < places.length - 1; i++) {
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

    /**
     * Return the list of all the places in the DB
     *
     * @return All the places
     */
    public List<Place> getAllPlaces() {
        return em.createNamedQuery("Place.findAll").getResultList();
    }

    /**
     * Search a place by its ID
     *
     * @param id The searched ID
     * @return The place that has that ID (or null)
     */
    public Place getPlaceByID(Long id) {
        return (Place) em.createNamedQuery("Place.findByID").
                setParameter("ID", id).getSingleResult();
    }

    /**
     * Search a place by the name
     *
     * @param name The wanted name
     * @return All the places with that name
     */
    public List<Place> getPlaceByName(String name) {
        return em.createNamedQuery("Place.findByName").
                setParameter("name", name).getResultList();
    }

    /**
     * Search a place by name and country
     *
     * @param name The name to search
     * @param country The country to search
     * @return The place that have the name and the country given
     */
    public Place getPlaceByNameAndCountry(String name, String country) {
        return (Place) em.createNamedQuery("Place.findByNameAndCountry").
                setParameter("name", name).setParameter("country", country).getResultList().get(0);
    }
}

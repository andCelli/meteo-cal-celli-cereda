/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.cellicereda.meteocal.businesslogic;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

/**
 * Used to initialize the place list
 *
 * @author stefano
 */
@Singleton
@Startup
public class Initializer {

    @EJB
    private LocationManager lm;

    @PostConstruct
    public void contextInitialized() {
        lm.initializePlaceList();
    }
}

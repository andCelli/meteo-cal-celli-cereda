/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.cellicereda.meteocal.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.validation.constraints.NotNull;

/**
 *
 * @author stefano
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "Forecast.findAll",
            query = "SELECT f FROM Forecast f"),
    @NamedQuery(name = "Forecast.findByPlaceAndTime",
            query = "SELECT f FROM Forecast f WHERE f.id = :id AND f.startingValidity <= :time AND f.endingValidity >= :time"),
    @NamedQuery(name = "Forecast.findForecastWithouEvent",
            query = "SELECT f FROM Forecast f WHERE f NOT IN "
            + "(SELECT f FROM Event e JOIN Forecast f WHERE e.forecast = f)")
})
public class Forecast implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * The referred place
     */
    @NotNull(message = "May not be empty")
    @ManyToOne
    @JoinColumn(name = "PLACE")
    private Place place;

    /**
     * The starting validity hour
     */
    @NotNull(message = "May not be empty")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date startingValidity;

    /**
     * The ending validity hour
     */
    @NotNull(message = "May not be empty")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date endingValidity;

    /**
     * The time when the forecast was made
     */
    @NotNull(message = "May not be empty")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date makingTime;

    /**
     * The weather id (see http://openweathermap.org/weather-conditions)
     */
    @NotNull(message = "May not be empty")
    private int weatherId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public Date getStartingValidity() {
        return startingValidity;
    }

    public void setStartingValidity(Date startingValidity) {
        this.startingValidity = startingValidity;
    }

    public Date getEndingValidity() {
        return endingValidity;
    }

    public void setEndingValidity(Date endingValidity) {
        this.endingValidity = endingValidity;
    }

    public Date getMakingTime() {
        return makingTime;
    }

    public void setMakingTime(Date makingTime) {
        this.makingTime = makingTime;
    }

    public int getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(int weatherId) {
        this.weatherId = weatherId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Forecast)) {
            return false;
        }
        Forecast other = (Forecast) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "it.polimi.cellicereda.meteocal.entities.Forecast[ id=" + id + " ]";
    }

}

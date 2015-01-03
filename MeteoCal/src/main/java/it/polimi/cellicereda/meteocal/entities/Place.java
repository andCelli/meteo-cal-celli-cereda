/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.cellicereda.meteocal.entities;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

/**
 * A place is where an outdoor event takes place (i.e. a physical place for
 * which we can retrieve a forecast). The table has the same structure of the
 * file http://openweathermap.org/help/city_list.txt so it can be easily
 * compiled
 *
 * @author stefano
 */
@Entity
public class Place implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * The id will be the same referenced by the meteo service
     */
    @Id
    private Long id;

    /**
     * The location name as referenced by the meteo service
     */
    @NotNull(message = "May not be empty")
    private String name;

    /**
     * The location latitude as referenced by the meteo service
     */
    @NotNull(message = "May not be empty")
    private Double latitude;

    /**
     * The location longitude as referenced by the meteo service
     */
    @NotNull(message = "May not be empty")
    private Double longitude;

    /**
     * The location country as referenced by the meteo service
     */
    @NotNull(message = "May not be empty")
    private String country;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
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
        if (!(object instanceof Place)) {
            return false;
        }
        Place other = (Place) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "it.polimi.cellicereda.meteocal.entities.Place[ id=" + id + " ]";
    }

}

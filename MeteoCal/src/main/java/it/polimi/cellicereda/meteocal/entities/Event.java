/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.cellicereda.meteocal.entities;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.validation.constraints.NotNull;

/**
 * @author stefano
 */
@Entity

@NamedQueries({
    @NamedQuery(name = "Event.findAll",
            query = "SELECT e FROM EVENT e"),})

public class Event implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull(message = "May not be empty")
    private Timestamp starting;

    @NotNull(message = "May not be empty")
    private Timestamp ending;

    /**
     * The location where the event takes place (can be null when the event is
     * indoor)
     */
    private Place eventLocation;

    /**
     * The forecast for the event, if any
     */
    @ManyToOne
    @JoinColumn(name = "FORECAST")
    private Forecast forecast;

    @NotNull(message = "May not be empty")
    private Boolean publicEvent;

    @NotNull(message = "May not be empty")
    @ManyToOne
    @JoinColumn(name = "CREATOR")
    private User creator;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Timestamp getStarting() {
        return starting;
    }

    public void setStarting(Timestamp starting) {
        this.starting = starting;
    }

    public Timestamp getEnding() {
        return ending;
    }

    public void setEnding(Timestamp ending) {
        this.ending = ending;
    }

    public Boolean getPublicEvent() {
        return publicEvent;
    }

    public void setPublicEvent(Boolean publicEvent) {
        this.publicEvent = publicEvent;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
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
        if (!(object instanceof Event)) {
            return false;
        }
        Event other = (Event) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "it.polimi.cellicereda.meteocal.entities.Event[ id=" + id + " ]";
    }

}

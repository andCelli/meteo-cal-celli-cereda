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
import org.primefaces.model.ScheduleEvent;

/**
 * @author stefano
 */
@Entity

@NamedQueries({
    @NamedQuery(name = "Event.findAll",
            query = "SELECT e FROM Event e"),
    @NamedQuery(name = "Event.findByCreator",
            query = "SELECT e FROM Event e WHERE e.creator = :creator")

})

public class Event implements Serializable, ScheduleEvent {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;

    @NotNull(message = "May not be empty")
    private String title;

    private String description;

    @Temporal(javax.persistence.TemporalType.DATE)
    private Date startingDate;

    @Temporal(javax.persistence.TemporalType.DATE)
    private Date endingDate;

    /**
     * The location where the event takes place (can be null when the event is
     * indoor)
     */
    @ManyToOne
    @JoinColumn(name = "LOCATION")
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
    private Boolean isAllDay;

    @NotNull(message = "May not be empty")
    @ManyToOne
    @JoinColumn(name = "CREATOR")
    private User creator;

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

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public Date getStartDate() {
        return startingDate;
    }

    @Override
    public Date getEndDate() {
        return endingDate;
    }

    @Override
    public boolean isAllDay() {
        return isAllDay;
    }

    public void setIsAllDay(Boolean allDay) {
        isAllDay = allDay;
    }

    @Override
    public String getStyleClass() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    /**
     * All the events are editable
     */
    public boolean isEditable() {
        return true;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String desc) {
        description = desc;
    }

    public Boolean isPublicEvent() {
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

    public void setStartingDate(Date starting) {
        this.startingDate = starting;
    }

    public void setEndingDate(Date ending) {
        this.endingDate = ending;
    }

    public Place getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(Place eventLocation) {
        this.eventLocation = eventLocation;
    }

    public Forecast getForecast() {
        return forecast;
    }

    public void setForecast(Forecast forecast) {
        this.forecast = forecast;
    }

    @Override
    /**
     * @return The event's forecast
     */
    public Object getData() {
        return forecast;
    }
}

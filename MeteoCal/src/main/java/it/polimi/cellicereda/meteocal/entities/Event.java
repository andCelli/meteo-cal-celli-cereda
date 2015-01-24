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
            query = "SELECT e FROM Event e WHERE e.creator = :creator"),
    @NamedQuery(name = "Event.findPreviousToDate",
            query = "SELECT e FROM Event e WHERE e.endDate < :date")
})

public class Event implements Serializable, ScheduleEvent {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;

    private String title;

    private String description;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date startDate;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date endDate;

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
    private boolean publicEvent;

    @NotNull(message = "May not be empty")
    private Boolean isAllDay = false;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public boolean isPublicEvent() {
        return publicEvent;
    }

    public void setPublicEvent(boolean publicEvent) {
        this.publicEvent = publicEvent;
    }

    public Boolean getIsAllDay() {
        return isAllDay;
    }

    public void setIsAllDay(Boolean isAllDay) {
        this.isAllDay = isAllDay;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public Object getData() {
        return null;
    }

    @Override
    public boolean isAllDay() {
        return isAllDay;
    }

    @Override
    public String getStyleClass() {
        return null;
    }

    @Override
    public boolean isEditable() {
        return true;
    }

    @Override
    public Event clone() {
        Event toRet = new Event();

        toRet.setDescription(this.description);
        toRet.setEndDate(this.endDate);
        toRet.setEventLocation(this.eventLocation);
        toRet.setStartDate(startDate);
        toRet.setTitle(title);

        return toRet;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.cellicereda.meteocal.businesslogic;

import it.polimi.cellicereda.meteocal.entities.Event;
import it.polimi.cellicereda.meteocal.entities.User;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.primefaces.model.ScheduleEvent;

/**
 *
 * @author stefano
 */
@Stateless
public class CalendarManager {

    @PersistenceContext
    EntityManager em;

    /**
     * Save into the DB the given event
     *
     * @param e the event to be saved
     */
    public void save(Event e) {
        em.persist(e);
    }

    /**
     * Delete from the DB the given event
     *
     * @param e The event to remove
     */
    public void delete(Event e) {
        em.remove(e);
    }

    public Event getByID(Long id) {
        return em.find(Event.class, id);
    }

    public List<? extends ScheduleEvent> getAllEvents() {
        return em.createNamedQuery("event.findAll").getResultList();
    }

    public List<? extends ScheduleEvent> getEventsByCreator(User creator) {
        return em.createNamedQuery("Event.findByCreator").
                setParameter("creator", creator).getResultList();
    }

    public List<? extends ScheduleEvent> getEventsByParticipant(User participant) {
        return em.createNamedQuery("User.getParticipationList").
                setParameter("participant", participant).getResultList();
    }

}

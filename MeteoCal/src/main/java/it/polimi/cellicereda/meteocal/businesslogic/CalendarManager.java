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
     * Delete from the DB the given event (should not be used)
     *
     * @param e The event to remove
     */
    @Deprecated
    public void delete(Event e) {
        em.remove(e);
    }

    public Event getByID(Long id) {
        return em.find(Event.class, id);
    }

    public List<Event> getAllEvents() {
        return em.createNamedQuery("event.findAll").getResultList();
    }

    public List<Event> getEventsByCreator(User creator) {
        return em.createNamedQuery("Event.findByCreator").
                setParameter("creator", creator).getResultList();
    }

    public List<Event> getEventsByParticipant(User participant) {
        return em.createNamedQuery("User.getParticipationList").
                setParameter("participant", participant).getResultList();
    }

    /**
     * This method adds the user to the list of participants of the event
     * Usually this method is used by the notification manager when an user
     * answer to an event invite
     *
     * @param newParticipant The user to add to the list of participants
     * @param event The event where the user will participate
     */
    public void addAnUserToAnEventParticipants(User newParticipant, Event event) {
        newParticipant.addEvent(event);
    }

}

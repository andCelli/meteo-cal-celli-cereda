/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.cellicereda.meteocal.entities;

import java.io.Serializable;
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
 *
 * @author stefano
 */
@Entity

@NamedQueries({
    @NamedQuery(name = "Notification.findAll",
            query = "SELECT n FROM Notification n"),
    @NamedQuery(name = "Notification.findForUserAndState",
            query = "SELECT n FROM Notification n WHERE n.recipient = :user AND n.notificationState = :state")
})
public class Notification implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * The user that receives this notification
     */
    @NotNull(message = "May not be empty")
    @ManyToOne
    @JoinColumn(name = "RECIPIENT")
    private User recipient;

    /**
     * The referred event (may be empty)
     */
    @ManyToOne
    @JoinColumn(name = "EVENT")
    private Event referredEvent;

    /**
     * The type of notification (event changed, bad weather alert, sunny day
     * proposal, event invite)
     */
    @NotNull(message = "May not be empty")
    private NotificationType notificationType;

    /**
     * The state of the notification (pending, readed, answered)
     */
    @NotNull(message = "May not be empty")
    private NotificationState notificationState;

    /**
     * The user's answer to the notification (applies only to notifications that
     * can have an answer)
     */
    private Boolean notificationAnswer;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public Event getReferredEvent() {
        return referredEvent;
    }

    public void setReferredEvent(Event referredEvent) {
        this.referredEvent = referredEvent;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public NotificationState getNotificationState() {
        return notificationState;
    }

    public void setNotificationState(NotificationState notificationState) {
        this.notificationState = notificationState;
    }

    public Boolean getNotificationAnswer() {
        return notificationAnswer;
    }

    public void setNotificationAnswer(Boolean notificationAnswer) {
        this.notificationAnswer = notificationAnswer;
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
        if (!(object instanceof Notification)) {
            return false;
        }
        Notification other = (Notification) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "it.polimi.cellicereda.meteocal.entities.Notification[ id=" + id + " ]";
    }

    /**
     * Constructor, by default sets the state to Pending
     *
     * @param type The type of notification
     * @param recipient The user that will receive the notification
     * @param event The event referred by the notification
     */
    public Notification(NotificationType type, User recipient, Event event) {
        this.notificationState = NotificationState.PENDING;
        this.notificationType = type;
        this.recipient = recipient;
        this.referredEvent = event;
    }

    /**
     * The default constructor is used by JEE, we should prefer the complete one
     */
    @Deprecated
    public Notification() {
    }

}

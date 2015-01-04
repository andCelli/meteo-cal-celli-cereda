/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.cellicereda.meteocal.entities;

import java.io.Serializable;

/**
 * A notification goes through the following state: - Pending: when created and
 * still not shown to the user - Readed: when the interested user has seen the
 * notification - Answered: when the user has seen the notification and provided
 * an answer (this applis only to event invite and sunny day proposal)
 *
 * @author stefano
 */
public enum NotificationState{

    PENDING, READED, ANSWERED;
}

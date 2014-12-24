/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.cellicereda.meteocal.entities;

import it.polimi.cellicereda.meteocal.businesslogic.PasswordEncrypter;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * The user entity represent the system user
 *
 * @author stefano
 */
@Entity(name = "USERS")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * The username is a sort of a nickname
     */
    @NotNull(message = "May not be empty")
    private String username;

    /**
     * The real name of the user
     */
    @NotNull(message = "May not be empty")
    private String name;

    /**
     * The surname of the user
     */
    @NotNull(message = "May not be empty")
    private String surname;

    /**
     * The email of the user, acts as identificator
     */
    @Id
    @Pattern(regexp = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?",
            message = "invalid email")
    @NotNull(message = "May not be empty")
    private String email;

    /**
     * The user password
     */
    @NotNull(message = "May not be empty")
    private String password;

    /**
     * The user's group
     */
    @NotNull(message = "May not be empty")
    private String groupName;

    /**
     * Specifies if the user's calendar is public
     */
    @NotNull(message = "May not be empty")
    private boolean publicCalendar;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = PasswordEncrypter.encryptPassword(password);
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public boolean isPublicCalendar() {
        return publicCalendar;
    }

    public void setPublicCalendar(boolean publicCalendar) {
        this.publicCalendar = publicCalendar;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (email != null ? email.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final User other = (User) obj;
        return Objects.equals(this.email, other.email);
    }

    @Override
    public String toString() {
        return "User[ email = " + email + " ]";
    }

}

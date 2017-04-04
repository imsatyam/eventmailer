package com.satyam.mailer.model;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Created by Satyam on 9/10/2016.
 */
public class Person implements Serializable{

    private static final Long serialVersionUID = 11311231231231L;

    private String firstName;
    private String lastName;
    private String petName;
    private String preferredName;
    private LocalDate dateOfBirth;
    private LocalDate anniversaryDate;
    private String email;
    private Relation relation;
    private String customBirthdayMessage;
    private String customAnniversaryMessage;
    private String signature;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPreferredName() {
        return preferredName;
    }

    public void setPreferredName(String preferredName) {
        this.preferredName = preferredName;
    }

    public LocalDate getAnniversaryDate() {
        return anniversaryDate;
    }

    public void setAnniversaryDate(LocalDate anniversaryDate) {
        this.anniversaryDate = anniversaryDate;
    }

    public Relation getRelation() {
        return relation;
    }

    public void setRelation(Relation relation) {
        this.relation = relation;
    }

    public String getCustomBirthdayMessage() {
        return customBirthdayMessage;
    }

    public void setCustomBirthdayMessage(String customBirthdayMessage) {
        this.customBirthdayMessage = customBirthdayMessage;
    }

    public String getCustomAnniversaryMessage() {
        return customAnniversaryMessage;
    }

    public void setCustomAnniversaryMessage(String customAnniversaryMessage) {
        this.customAnniversaryMessage = customAnniversaryMessage;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}

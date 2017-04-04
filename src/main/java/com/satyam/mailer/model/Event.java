package com.satyam.mailer.model;

/**
 * Created by Satyam on 9/10/2016.
 */
public enum Event {

    ANNIVERSARY ("Happy Anniversary", "anniversary.vm"),
    BIRTHDAY ("Happy Birthday To You", "birthday.vm"),
    GENERAL ("", "generalEvent.vm");

    private String subject;
    private String template;
    Event (String subject, String template){
        this.subject = subject;
        this.template = template;
    }

    public String getSubject() {
        return subject;
    }

    public String getTemplate() {
        return template;
    }
}

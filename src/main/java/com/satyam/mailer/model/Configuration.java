package com.satyam.mailer.model;

import java.io.Serializable;

/**
 * Created by Satyam on 9/10/2016.
 */
public class Configuration implements Serializable{

    private static final Long serialVersionUID = 113112312312221L;

    private Application application;
    private Person user;
    private Repository repository;

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public Person getUser() {
        return user;
    }

    public void setUser(Person user) {
        this.user = user;
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }
}

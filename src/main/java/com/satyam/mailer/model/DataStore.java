package com.satyam.mailer.model;

/**
 * Created by Satyam on 9/11/2016.
 */
public enum DataStore {

    GOOGLE ("googleService");

    private String serviceName;
    DataStore(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceName() {
        return serviceName;
    }
}

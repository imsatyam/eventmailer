package com.satyam.mailer.model;

import java.io.Serializable;

/**
 * Created by Satyam on 9/10/2016.
 */
public class Application implements Serializable{

    private static final Long serialVersionUID = 113132878987931231L;

    private String applicationId;
    private String applicationName;
    private String clientSecretFileUrl;

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getClientSecretFileUrl() {
        return clientSecretFileUrl;
    }

    public void setClientSecretFileUrl(String clientSecretFileUrl) {
        this.clientSecretFileUrl = clientSecretFileUrl;
    }
}

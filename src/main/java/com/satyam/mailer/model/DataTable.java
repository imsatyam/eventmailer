package com.satyam.mailer.model;

import java.io.Serializable;

/**
 * Created by Satyam on 9/11/2016.
 */
public class DataTable implements Serializable {

    private static final Long serialVersionUID = 88893778787811L;

    private String id;
    private String url;
    private String userName;
    private String password;

    public DataTable() {
    }

    public DataTable(String id, String url, String userName, String password) {
        this.id = id;
        this.url = url;
        this.userName = userName;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

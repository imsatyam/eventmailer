package com.satyam.mailer.service;

/**
 * Created by Satyam on 9/11/2016.
 */
public interface DataServiceFactory {

    public DataService getDataService (String dataSource);

}

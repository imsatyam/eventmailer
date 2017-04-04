package com.satyam.mailer.model;

/**
 * Created by Satyam on 9/10/2016.
 */
public enum RepositoryType {

    EXCEL ("excelRepositoryProcessor");      //At present we keep the details in the excel only

    private String processorKey;
    RepositoryType (String processorKey) {
        this.processorKey = processorKey;
    }

    public String getProcessorKey() {
        return processorKey;
    }
}

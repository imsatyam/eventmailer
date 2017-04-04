package com.satyam.mailer.service;

/**
 * Created by Satyam on 9/10/2016.
 */
public interface JobProcessorFactory {

    JobProcessor getJobProcessor (String processorId);

}

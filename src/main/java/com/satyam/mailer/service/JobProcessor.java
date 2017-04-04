package com.satyam.mailer.service;

import com.satyam.mailer.model.Configuration;

/**
 * Created by Satyam on 9/10/2016.
 */
public interface JobProcessor {

    /**
     * Processes the job
     * @param configuration
     */
    void processJob (Configuration configuration);

}

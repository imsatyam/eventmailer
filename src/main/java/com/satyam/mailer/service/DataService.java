package com.satyam.mailer.service;

import com.satyam.mailer.model.Application;
import com.satyam.mailer.model.DataTable;

import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Map;

/**
 * Created by Satyam on 9/11/2016.
 */
public interface DataService {

    /**
     * Get the configuration path
     * @param table
     * @return
     */
    Map<String, List<Map<String, String>>> readExcelFile (Application application, DataTable table);

    /**
     * Send email
     * @param emailList
     */
    void sendEmail (Application application, List<MimeMessage> emailList);


}

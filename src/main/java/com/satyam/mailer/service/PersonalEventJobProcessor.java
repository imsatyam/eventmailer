package com.satyam.mailer.service;

import com.satyam.mailer.exception.PersonalEventsException;
import com.satyam.mailer.model.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.*;
import java.io.StringWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Satyam on 9/10/2016.
 */
@Service("personalEventProcessor")
public class PersonalEventJobProcessor extends EventJobProcessor implements JobProcessor{

    private static final Logger LOG = LoggerFactory.getLogger(PersonalEventJobProcessor.class);

    @Autowired
    private DataServiceFactory dataServiceFactory;

    @Autowired
    private VelocityEngine velocityEngine;

    @Override
    public void processJob (Configuration configuration) {
        try {
            DataStore dataStore = DataStore.valueOf(configuration.getRepository().getIdentifier().trim().toUpperCase());
            LOG.info("Getting the relevant data service...");
            DataService dataService = dataServiceFactory.getDataService(dataStore.getServiceName());

            // Let us just say that we'll stick with excel for some time to store data. It is flexible.
            LOG.info("Getting list of people...");
            DataTable table = configuration.getRepository().getDataTables().get(PEOPLE);
            Map<String, List<Map<String, String>>> people = dataService.readExcelFile(configuration.getApplication(), table);
            if (people != null && !people.isEmpty()) {
                List<Person> persons = getPersonList(people);

                LocalDate today = LocalDate.now();
                LOG.info("Today is: [{}]", today);

                List<Person> birthdayList = new ArrayList<>();
                List<Person> anniversaryList = new ArrayList<>();
                LOG.info("Get people who has birthday or anniversary today..");
                persons.stream().forEach(p -> filterAnniversaryAndBirthday(p, birthdayList, anniversaryList, today));

                List<MimeMessage> emailList = new ArrayList<>();

                // prepare birthday emails
                if (!birthdayList.isEmpty()) {
                    LOG.info("Preparing birthday wishes..");
                    List<MimeMessage> birthdayEmails = getMIMEMessages(configuration, birthdayList, Event.BIRTHDAY);
                    if (birthdayEmails != null && !birthdayEmails.isEmpty()) {
                        emailList.addAll(birthdayEmails);
                    }
                }

                // prepare anniversary emails
                if (!anniversaryList.isEmpty()) {
                    LOG.info("Preparing anniversary wishes..");
                    List<MimeMessage> anniversaryEmails = getMIMEMessages(configuration, anniversaryList, Event.ANNIVERSARY);
                    if (anniversaryEmails != null && !anniversaryEmails.isEmpty()) {
                        emailList.addAll(anniversaryEmails);
                    }
                }

                if (!emailList.isEmpty()) {
                    dataService.sendEmail(configuration.getApplication(), emailList);
                } else {
                    LOG.info("No email to send...");
                }
                LOG.info("Processing complete...");
            }
        } catch (Exception exception) {
            LOG.error("An error while processing the job.", exception);
            throw new PersonalEventsException(exception);
        }
    }

    /**
     * Create MIME Messages
     * @param configuration
     * @param personList
     * @param event
     * @return
     * @throws MessagingException
     */
    private List<MimeMessage> getMIMEMessages (Configuration configuration, List<Person> personList, Event event) throws MessagingException {

        List<MimeMessage> emailList = new ArrayList<MimeMessage>();
        Template template = this.velocityEngine.getTemplate(event.getTemplate());

        for (Person person : personList) {

            Properties props = new Properties();
            Session session = Session.getDefaultInstance(props, null);
            MimeMessage email = new MimeMessage(session);

            Address[] replyTo = {new InternetAddress(configuration.getUser().getEmail())};
            email.setReplyTo(replyTo);

            email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(person.getEmail()));
            email.addRecipient(javax.mail.Message.RecipientType.CC, new InternetAddress(configuration.getUser().getEmail()));
            email.setSubject(event.getSubject());

            String sentBy = configuration.getUser().getFirstName();
            if (person.getRelation() != null && person.getRelation().equals(Relation.FAMILY)) {
                sentBy = person.getSignature();
            }

            String sentTo = person.getFirstName();
            if (StringUtils.isNotBlank(person.getPreferredName())) {
                sentTo = person.getPreferredName();
            }

            VelocityContext ctx = new VelocityContext();
            ctx.put("who", sentTo);
            ctx.put("byWhom", sentBy);

            // If needed, set the custom message
            if (event == Event.BIRTHDAY && StringUtils.isNotBlank(person.getCustomBirthdayMessage())) {
                ctx.put("customMessage", person.getCustomBirthdayMessage());
            } else if(event == Event.ANNIVERSARY && StringUtils.isNotBlank(person.getCustomAnniversaryMessage())) {
                ctx.put("customMessage", person.getCustomAnniversaryMessage());
            }

            StringWriter writer = new StringWriter();
            template.merge(ctx, writer);

            Multipart mp = new MimeMultipart();
            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(writer.toString(), "text/html");
            mp.addBodyPart(htmlPart);
            email.setContent(mp);

            emailList.add(email);
        }

        return emailList;
    }

    /**
     * Get birthday and anniversary list
     * @param person
     * @param birthdayList
     * @param anniversaryList
     * @param today
     */
    private void filterAnniversaryAndBirthday (Person person, List<Person> birthdayList, List<Person> anniversaryList, LocalDate today) {

        if (person.getDateOfBirth() != null && person.getDateOfBirth().getDayOfMonth() == today.getDayOfMonth()
                && person.getDateOfBirth().getMonth() == today.getMonth()) {
            birthdayList.add(person);
        }

        if (person.getAnniversaryDate() != null && person.getAnniversaryDate().getDayOfMonth() == today.getDayOfMonth()
                && person.getAnniversaryDate().getMonth() == today.getMonth()) {
            anniversaryList.add(person);
        }
    }



}

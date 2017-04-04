package com.satyam.mailer.service.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Draft;
import com.google.api.services.gmail.model.Message;
import com.satyam.mailer.exception.IOClientException;
import com.satyam.mailer.model.Application;
import com.satyam.mailer.model.DataTable;
import com.satyam.mailer.service.BaseDataService;
import com.satyam.mailer.service.DataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Satyam on 9/11/2016.
 */
@Service("googleService")
public class GoogleDataService extends BaseDataService implements DataService {

    private static final Logger LOG = LoggerFactory.getLogger(GoogleDataService.class);

    /** Directory to store user credentials for this application. */
    private static final java.io.File DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"), ".credentials/eventbook-mailer");

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    /** Global instance of the {@link FileDataStoreFactory}. */
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    /** Global instance of the scopes required by this quickstart.
     **/
    private static final List<String> SCOPES = Arrays.asList(GmailScopes.GMAIL_COMPOSE, GmailScopes.GMAIL_SEND, DriveScopes.DRIVE_READONLY);

    /** Global instance of the HTTP transport. */
    private static HttpTransport HTTP_TRANSPORT;

    @PostConstruct
    private void doSetUp () {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Exception exception) {
            LOG.error("Exception while setting up google service api.", exception);
            throw new IOClientException(exception);
        }
    }

    @Override
    public Map<String, List<Map<String, String>>> readExcelFile (Application application, DataTable table) {

        Map<String, List<Map<String, String>>> dataMap = null;
        try {
            Credential credential = authorize(application.getClientSecretFileUrl());
            Drive drive = getDriveService(application.getApplicationName(), credential);
            dataMap = readExcelWithHeader(getFileOnDrive(drive, table.getUrl()));
        } catch (Exception exception) {
            LOG.error("Error while reading an excel file from google drive...");
            throw new IOClientException(exception);
        }
        return dataMap;
    }

    @Override
    public void sendEmail (Application application, List<MimeMessage> emailList) {
        try {
            Credential credential = authorize(application.getClientSecretFileUrl());
            Gmail gmail = getGmailService(application.getApplicationName(), credential);
            for (MimeMessage msg : emailList) {
                sendMessage(gmail, "me", msg);
            }
        } catch (Exception exception) {
            LOG.error("Error while reading an excel file from google drive...");
            throw new IOClientException(exception);
        }
    }

    /**
     * Creates an authorized Credential object.
     * @return an authorized Credential object.
     * @throws IOException
     */
    public Credential authorize(String secret) throws IOException {
        // Load client secrets.
        InputStream in = new FileInputStream(secret);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(DATA_STORE_FACTORY)
                .setAccessType("offline")
                .build();

        Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
        return credential;
    }

    /**
     * Get mailing service
     * @param applicationName
     * @param credential
     * @return
     * @throws IOException
     */
    public static Gmail getGmailService(String applicationName, Credential credential) throws IOException {
        return new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(applicationName)
                .build();
    }

    /**
     * Get drive service
     * @param applicationName
     * @param credential
     * @return
     * @throws IOException
     */
    public static Drive getDriveService(String applicationName, Credential credential) throws IOException {
        return new Drive.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(applicationName)
                .build();
    }

    /**
     * Get file on Google drive
     * @param drive
     * @param fileId
     * @return
     * @throws IOException
     */
    private InputStream getFileOnDrive(Drive drive, String fileId) throws IOException {
        return drive.files().get(fileId).executeMediaAsInputStream();
    }

    /**
     * Create draft email.
     *
     * @param service an authorized Gmail API instance
     * @param userId user's email address. The special value "me"
     * can be used to indicate the authenticated user
     * @param emailContent the MimeMessage used as email within the draft
     * @return the created draft
     * @throws MessagingException
     * @throws IOException
     */
    public Draft createDraft(Gmail service,
                                    String userId,
                                    MimeMessage emailContent)
            throws MessagingException, IOException {
        Message message = createMessageWithEmail(emailContent);
        Draft draft = new Draft();
        draft.setMessage(message);
        draft = service.users().drafts().create(userId, draft).execute();

        LOG.debug("Draft id: {}" + draft.getId());
        LOG.debug(draft.toPrettyString());
        return draft;
    }


    /**
     * Send an email from the user's mailbox to its recipient.
     *
     * @param service Authorized Gmail API instance.
     * @param userId User's email address. The special value "me"
     * can be used to indicate the authenticated user.
     * @param emailContent Email to be sent.
     * @return The sent message
     * @throws MessagingException
     * @throws IOException
     */
    public Message sendMessage(Gmail service,
                                      String userId,
                                      MimeMessage emailContent)
            throws MessagingException, IOException {
        Message message = createMessageWithEmail(emailContent);
        message = service.users().messages().send(userId, message).execute();

        LOG.debug("Message id: {}" + message.getId());
        LOG.debug(message.toPrettyString());
        return message;
    }


    /**
     * Create a message from an email.
     *
     * @param emailContent Email to be set to raw of message
     * @return a message containing a base64url encoded email
     * @throws IOException
     * @throws MessagingException
     */
    public Message createMessageWithEmail(MimeMessage emailContent)
            throws MessagingException, IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        emailContent.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }

    /**
     * Create a MimeMessage using the parameters provided.
     *
     * @param to email address of the receiver
     * @param from email address of the sender, the mailbox account
     * @param subject subject of the email
     * @param bodyText body text of the email
     * @return the MimeMessage to be used to send email
     * @throws MessagingException
     */
    public MimeMessage createEmail(String to,
                                   String from,
                                   String cc,
                                   String subject,
                                   String bodyText)
            throws MessagingException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);

        email.setFrom(new InternetAddress(from));
        email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));
        email.addRecipient(javax.mail.Message.RecipientType.CC, new InternetAddress(cc));

        email.setSubject(subject);
        email.setText(bodyText);
        return email;
    }


    /**
     * Create a MimeMessage using the parameters provided.
     *
     * @param to Email address of the receiver.
     * @param from Email address of the sender, the mailbox account.
     * @param subject Subject of the email.
     * @param bodyText Body text of the email.
     * @param file Path to the file to be attached.
     * @return MimeMessage to be used to send email.
     * @throws MessagingException
     */
    public MimeMessage createEmailWithAttachment(String to,
                                                        String from,
                                                        String subject,
                                                        String bodyText,
                                                        File file)
            throws MessagingException, IOException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);

        email.setFrom(new InternetAddress(from));
        email.addRecipient(javax.mail.Message.RecipientType.TO,
                new InternetAddress(to));
        email.setSubject(subject);

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(bodyText, "text/plain");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);

        mimeBodyPart = new MimeBodyPart();
        DataSource source = new FileDataSource(file);

        mimeBodyPart.setDataHandler(new DataHandler(source));
        mimeBodyPart.setFileName(file.getName());

        multipart.addBodyPart(mimeBodyPart);
        email.setContent(multipart);

        return email;
    }
}

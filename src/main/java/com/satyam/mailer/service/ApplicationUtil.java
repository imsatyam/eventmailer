package com.satyam.mailer.service;

import com.satyam.mailer.exception.IOClientException;
import com.satyam.mailer.model.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Created by Satyam on 9/10/2016.
 */
public class ApplicationUtil {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationUtil.class);

    /**
     * Reads the configuration file and returns the configuration object
     * @param configFilePath
     * @return
     */
    public static Configuration getConfiguration (String configFilePath) {

        Configuration configuration = null;
        LOG.info("Reading job configuration from file [{}]", configFilePath);
        long startTime = System.currentTimeMillis();

        InputStream inputStream = null;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(configFilePath.trim()));

            // Create XMLInputFactory and XMLEventReader
            XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
            XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(inputStream);

            Application application = null;
            Person user = null;
            Repository repository = null;
            while(xmlEventReader.hasNext()) {
                XMLEvent xmlEvent = xmlEventReader.nextEvent();

                if (xmlEvent.isStartElement()) {
                    StartElement startElement = xmlEvent.asStartElement();
                    switch (startElement.getName().getLocalPart()) {

                        // Exploding the entire xml here. There are better ways to accomplish it.
                        case "configuration":
                            configuration = new Configuration();
                            break;
                        case "application":
                            application = new Application();
                            application.setApplicationId(getAttributeValue(startElement, "id"));
                            break;
                        case "applicationName":
                            application.setApplicationName(getValueOfNextEvent(xmlEventReader));
                            break;
                        case "clientSecretFile":
                            application.setClientSecretFileUrl(getValueOfNextEvent(xmlEventReader));
                            break;
                        case "user":
                            user = new Person();
                            break;
                        case "firstName":
                            user.setFirstName(getValueOfNextEvent(xmlEventReader));
                            break;
                        case "lastName":
                            user.setLastName(getValueOfNextEvent(xmlEventReader));
                            break;
                        case "petName":
                            user.setPetName(getValueOfNextEvent(xmlEventReader));
                            break;
                        case "dateOfBirth":
                            String dateOfBirth = getValueOfNextEvent(xmlEventReader);
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                            LocalDate dob = StringUtils.isNotBlank(dateOfBirth) ? LocalDate.parse(dateOfBirth, formatter) : null;
                            user.setDateOfBirth(dob);
                            break;
                        case "email":
                            user.setEmail(getValueOfNextEvent(xmlEventReader));
                            break;
                        case "repository":
                            repository = new Repository();
                            repository.setIdentifier(getAttributeValue(startElement, "id"));
                            break;
                        case "repositoryType":
                            String repoType = getValueOfNextEvent(xmlEventReader);
                            repository.setRepositoryType(RepositoryType.valueOf(repoType.trim().toUpperCase()));
                            break;
                        case "dataTable":
                            DataTable dataTable = new DataTable(getAttributeValue(startElement, "id"),
                                                                getAttributeValue(startElement, "url"),
                                                                getAttributeValue(startElement, "username"),
                                                                getAttributeValue(startElement, "password"));
                            repository.addDataTables(dataTable);
                            break;

                        default:
                            // do nothing.
                            break;
                    }
                }
            }
            if (configuration != null) {
                configuration.setApplication(application);
                configuration.setUser(user);
                configuration.setRepository(repository);
            }
            LOG.info("File Configuration read properly. Time taken: [{} ms]", System.currentTimeMillis() - startTime);
        } catch (Exception exception) {
            throw new IOClientException("Exception while reading job configuration.", exception);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }

        return configuration;
    }

    /**
     * Gets the value of a given attribute
     * @param startElement
     * @param attributeName
     * @return
     */
    private static String getAttributeValue (StartElement startElement, String attributeName) {
        String value = null;
        Attribute idAttr = startElement.getAttributeByName(new QName(attributeName));
        if (idAttr != null) {
            value = idAttr.getValue();
        }
        return value;
    }

    /**
     * Returns the value retrieved in next event. Useful for reading Satyam from <name>Satyam</name>
     * @return
     */
    private static String getValueOfNextEvent (XMLEventReader xmlEventReader) throws XMLStreamException{
        XMLEvent xmlEvent = xmlEventReader.nextEvent();
        return xmlEvent.asCharacters().getData();
    }
}

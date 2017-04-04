package com.satyam.mailer.service;

import com.satyam.mailer.model.Person;
import com.satyam.mailer.model.Relation;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Satyam on 9/11/2016.
 */
public class EventJobProcessor {

    protected static final String PEOPLE = "people";

    // Headers for people
    private static final String FIRST_NAME = "FIRST NAME";
    private static final String LAST_NAME = "LAST NAME";
    private static final String PREFERRED_NAME = "PREFFERED NAME";
    private static final String EMAIL = "EMAIL";
    private static final String DOB = "DATE OF BIRTH";
    private static final String ANNIVERSARY = "ANNIVERSARY";
    private static final String BDAY_MSG = "BIRTHDAY MESSAGE";
    private static final String ANNIV_MSG = "ANNIVERSARY MESSAGE";
    private static final String SIGNATURE = "SIGNATURE";

    /**
     * Convert map of string in person object
     * @param people
     * @return
     */
    protected List<Person> getPersonList (Map<String, List<Map<String, String>>> people) {
        List<Person> persons = new ArrayList<>();

        for (Map.Entry<String, List<Map<String, String>>> entry : people.entrySet()) {
            String rln = entry.getKey();
            Relation relation = Relation.valueOf(rln.trim().toUpperCase());

            for (Map<String, String> objMap : entry.getValue()) {
                Person person = new Person();
                person.setFirstName(StringUtils.trimToEmpty(objMap.get(FIRST_NAME)));
                person.setLastName(StringUtils.trimToEmpty(objMap.get(LAST_NAME)));
                person.setPreferredName(StringUtils.trimToEmpty(objMap.get(PREFERRED_NAME)));
                person.setEmail(StringUtils.trimToEmpty(objMap.get(EMAIL)));
                person.setCustomAnniversaryMessage(StringUtils.trimToNull(objMap.get(ANNIV_MSG)));
                person.setCustomBirthdayMessage(StringUtils.trimToNull(objMap.get(BDAY_MSG)));

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

                String dateOfBirth = StringUtils.trimToEmpty(objMap.get(DOB));
                LocalDate dob = StringUtils.isNotBlank(dateOfBirth) ? LocalDate.parse(dateOfBirth, formatter) : null;
                person.setDateOfBirth(dob);

                String anniversary = StringUtils.trimToEmpty(objMap.get(ANNIVERSARY));
                LocalDate anniv = StringUtils.isNotBlank(anniversary) ? LocalDate.parse(anniversary, formatter) : null;
                person.setAnniversaryDate(anniv);

                person.setSignature(StringUtils.trimToNull(objMap.get(SIGNATURE)));

                person.setRelation(relation);
                persons.add(person);
            }
        }

        return persons;
    }





}

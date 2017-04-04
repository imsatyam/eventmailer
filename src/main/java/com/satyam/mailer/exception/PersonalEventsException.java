package com.satyam.mailer.exception;

/**
 * Created by Satyam on 9/10/2016.
 */
public class PersonalEventsException extends RuntimeException {
    public PersonalEventsException() {
    }

    public PersonalEventsException(String message) {
        super(message);
    }

    public PersonalEventsException(String message, Throwable cause) {
        super(message, cause);
    }

    public PersonalEventsException(Throwable cause) {
        super(cause);
    }
}

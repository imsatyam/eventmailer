package com.satyam.mailer.exception;

/**
 * Created by Satyam on 9/10/2016.
 */
public class IOClientException extends RuntimeException{

    public IOClientException() {
    }

    public IOClientException(String message) {
        super(message);
    }

    public IOClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public IOClientException(Throwable cause) {
        super(cause);
    }
}

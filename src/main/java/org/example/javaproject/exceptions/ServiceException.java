package org.example.javaproject.exceptions;

public class ServiceException extends RuntimeException {
    public ServiceException() {
        super("Error 500");
    }

    public ServiceException(String exception) {
        super(exception);
    }

}

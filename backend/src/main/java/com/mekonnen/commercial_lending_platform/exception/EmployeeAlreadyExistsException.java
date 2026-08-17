package com.mekonnen.commercial_lending_platform.exception;


public class EmployeeAlreadyExistsException extends RuntimeException {

    public EmployeeAlreadyExistsException(String message) {
        super(message);
    }
}

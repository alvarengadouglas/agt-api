package com.betmotion.agentsmanagement.service.exceptions;

public class SemaphoreControlNumberOfAttemptsException extends Exception {

    private static final long serialVersionUID = 1L;
    public static final String ERROR_CODE = "number_of_attempts_exception";
    private String code;
    private String message;

    public String getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

}
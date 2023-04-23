package com.epam.esm.services.exceptions;

/**
 * @author Myroslav Dudnyk
 */
public class ServiceException extends RuntimeException {
    private final int errorCode;

    public ServiceException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}

package com.epam.esm.controllers;

/**
 * @author Myroslav Dudnyk
 */
class ErrorResponse {
    private final String errorMessage;
    private final int errorCode;

    public ErrorResponse(String errorMessage, int errorCode) {
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
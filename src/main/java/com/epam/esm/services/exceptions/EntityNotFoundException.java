package com.epam.esm.services.exceptions;

/**
 * @author Myroslav Dudnyk
 */
public class EntityNotFoundException extends ServiceException {
    private static final int errorCode = 40404;

    public EntityNotFoundException(String entityName, int entityId) {
        super("Requested " + entityName + " not found (id = " + entityId + ")", errorCode);
    }

    public EntityNotFoundException(String errorMessage) {
        super(errorMessage, errorCode);
    }
}

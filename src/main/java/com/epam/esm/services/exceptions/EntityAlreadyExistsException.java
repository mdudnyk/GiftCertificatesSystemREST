package com.epam.esm.services.exceptions;

/**
 * @author Myroslav Dudnyk
 */
public class EntityAlreadyExistsException extends ServiceException {
    private static final int errorCode = 40407;

    public EntityAlreadyExistsException(String entityName, String objectName) {
        super("The " + entityName + " entity with name = '" + objectName + "' already exists in database", errorCode);
    }
}

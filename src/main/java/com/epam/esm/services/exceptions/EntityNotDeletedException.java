package com.epam.esm.services.exceptions;

/**
 * @author Myroslav Dudnyk
 */
public class EntityNotDeletedException extends ServiceException {
    private static final int errorCode = 40412;

    public EntityNotDeletedException(String entityName, int entityId) {
        super("The " + entityName + " entity with id=" + entityId + " was not deleted", errorCode);
    }
}

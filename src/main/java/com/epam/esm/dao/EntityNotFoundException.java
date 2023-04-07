package com.epam.esm.dao;

/**
 * @author Myroslav Dudnyk
 */
public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}
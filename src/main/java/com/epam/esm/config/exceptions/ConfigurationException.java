package com.epam.esm.config.exceptions;

/**
 * @author Myroslav Dudnyk
 */
public class ConfigurationException extends RuntimeException {
    public ConfigurationException(String errorMessage) {
        super(errorMessage);
    }
}

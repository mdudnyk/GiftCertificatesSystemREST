package com.epam.esm.services.exceptions;

/**
 * @author Myroslav Dudnyk
 */
public class UnsupportedSortingParameter extends ServiceException {
    private static final int errorCode = 40503;

    public UnsupportedSortingParameter() {
        super("Invalid sorting parameter", errorCode);
    }
}

package com.epam.esm.controllers.errorHandlers;

import com.epam.esm.services.exceptions.EntityNotFoundException;
import com.epam.esm.services.exceptions.UnsupportedSortingParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.SQLException;

/**
 * @author Myroslav Dudnyk
 */
@RestControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), e.getErrorCode());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnsupportedSortingParameter.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedSortingParameterException(UnsupportedSortingParameter e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), e.getErrorCode());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler()
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleSQLException(SQLException e) {
        return new ErrorResponse(e.getMessage(), Integer.parseInt(e.getSQLState()));
    }
}

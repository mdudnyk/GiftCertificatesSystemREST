package com.epam.esm.controllers.errorHandlers;

import com.epam.esm.services.exceptions.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.SQLException;

import static org.springframework.http.HttpStatus.*;

/**
 * @author Myroslav Dudnyk
 */
@RestControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), e.getErrorCode());
        return new ResponseEntity<>(errorResponse, NOT_FOUND);
    }

    @ExceptionHandler(UnsupportedSortingParameter.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedSortingParameterException(UnsupportedSortingParameter e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), e.getErrorCode());
        return new ResponseEntity<>(errorResponse, BAD_REQUEST);
    }

    @ExceptionHandler(EntityAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEntityAlreadyExistsException(EntityAlreadyExistsException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), e.getErrorCode());
        return new ResponseEntity<>(errorResponse, CONFLICT);
    }

    @ExceptionHandler(EntityNotDeletedException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotDeletedException(EntityNotDeletedException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), e.getErrorCode());
        return new ResponseEntity<>(errorResponse, NOT_FOUND);
    }

    @ExceptionHandler(ServiceException.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ErrorResponse handleCommonServiceException(ServiceException e) {
        return new ErrorResponse(e.getMessage(), e.getErrorCode());
    }

    @ExceptionHandler(SQLException.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ErrorResponse handleSQLException(SQLException e) {
        return new ErrorResponse(e.getMessage(), Integer.parseInt(e.getSQLState()));
    }
}

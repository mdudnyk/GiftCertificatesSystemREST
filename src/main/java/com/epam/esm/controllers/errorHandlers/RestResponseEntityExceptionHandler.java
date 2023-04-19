package com.epam.esm.controllers.errorHandlers;

import com.epam.esm.dao.exceptions.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.SQLException;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * @author Myroslav Dudnyk
 */
@RestControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), 40401);
        return new ResponseEntity<>(errorResponse, NOT_FOUND);
    }

    @ExceptionHandler()
    @ResponseStatus(value = INTERNAL_SERVER_ERROR)
    public ErrorResponse handleSQLException(SQLException e) {
        return new ErrorResponse(e.getMessage(), Integer.parseInt(e.getSQLState()));
    }
}

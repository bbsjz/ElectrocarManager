package com.carmanager.server.exception;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({NotFoundException.class})
    protected ResponseEntity<Object> handleNotFoundException(Exception e, WebRequest request) {
        ExceptionResponse response = new ExceptionResponse(e);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({NotSupportArgumentException.class})
    protected ResponseEntity<Object> handleBadRequestException(Exception e, WebRequest request) {
        ExceptionResponse response = new ExceptionResponse(e);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({DataAccessException.class})
    protected ResponseEntity<Object> handleGeneralException(Exception e, WebRequest request) {
        ExceptionResponse response = new ExceptionResponse(e);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

}

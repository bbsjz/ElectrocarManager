package com.carmanager.server.exception;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExceptionResponse {

    private String message;
    private LocalDateTime time;

    public ExceptionResponse(Exception e) {
        setMessage(e.getMessage());
        setTime(LocalDateTime.now());
    }

}

package com.budgettracker.exception;

import lombok.Getter;

@Getter
public class HttpException extends RuntimeException {
    private final int statusCode;
    
    public HttpException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}


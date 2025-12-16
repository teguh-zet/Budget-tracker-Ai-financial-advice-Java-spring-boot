package com.budgettracker.exception;

public class BadRequestException extends HttpException {
    public BadRequestException(String message) {
        super(message, 400);
    }
}


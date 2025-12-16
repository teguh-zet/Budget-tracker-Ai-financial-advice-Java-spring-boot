package com.budgettracker.exception;

public class ForbiddenException extends HttpException {
    public ForbiddenException(String message) {
        super(message, 403);
    }
}


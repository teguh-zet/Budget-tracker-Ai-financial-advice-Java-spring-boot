package com.budgettracker.exception;

public class UnauthorizedException extends HttpException {
    public UnauthorizedException(String message) {
        super(message, 401);
    }
}


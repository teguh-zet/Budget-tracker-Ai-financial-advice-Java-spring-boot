package com.budgettracker.exception;

public class RateLimitException extends HttpException {
    public RateLimitException(String message) {
        super(message, 429); // 429 TOO_MANY_REQUESTS
    }
}


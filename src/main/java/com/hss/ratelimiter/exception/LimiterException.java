package com.hss.ratelimiter.exception;

public class LimiterException extends Exception{
    private String errorCode;

    public LimiterException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    private String getErrorCode() {
        return errorCode;
    }
}

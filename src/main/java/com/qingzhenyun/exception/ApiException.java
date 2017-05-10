package com.qingzhenyun.exception;

import org.springframework.http.HttpStatus;

/**
 * Exc
 * Created by guna on 2017/5/10.
 */
public class ApiException extends RuntimeException {
    private final int code;

    public ApiException() {
        super("INTERNAL_SERVER_ERROR");
        this.code = 500;
    }

    public ApiException(int code) {
        this.code = code;
    }

    public ApiException(String message) {
        super(message);
        this.code = 500;
    }

    public ApiException(HttpStatus status, String message) {
        super(message);
        this.code = status.value();
    }

    public ApiException(int code, String message) {
        super(message);
        this.code = code;
    }

    public ApiException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public ApiException(HttpStatus status, String message, Throwable cause) {
        super(message, cause);
        this.code = status.value();
    }

    public ApiException(int code, Throwable cause) {
        super(cause);
        this.code = code;
    }

    public ApiException(int code, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }
}

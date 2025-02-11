package io.jhchoe.familytree.common.exception;

import org.springframework.http.HttpStatus;

public class FTException extends RuntimeException {

    private final BaseExceptionType baseException;

    public FTException(final BaseExceptionType baseException) {
        super(baseException.getMessage());
        this.baseException = baseException;
    }

    public String getCode() {
        return this.baseException.getCode();
    }

    public String getMessage() {
        return this.baseException.getMessage();
    }

    public HttpStatus getStatus() {
        return this.baseException.getStatus();
    }
}

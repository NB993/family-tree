package io.jhchoe.familytree.common.auth.exception;

import io.jhchoe.familytree.common.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public class AuthException extends RuntimeException {

    private final BaseExceptionType baseExceptionType;

    public AuthException(final BaseExceptionType baseExceptionType) {
        super(baseExceptionType.getMessage());
        this.baseExceptionType = baseExceptionType;
    }

    public String getCode() {
        return this.baseExceptionType.getCode();
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }

    public HttpStatus getStatus() {
        return this.baseExceptionType.getStatus();
    }
}
